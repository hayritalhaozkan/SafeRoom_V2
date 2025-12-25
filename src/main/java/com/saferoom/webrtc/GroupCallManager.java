package com.saferoom.webrtc;

import com.saferoom.grpc.SafeRoomProto.WebRTCSignal;
import dev.onvoid.webrtc.RTCRtpEncodingParameters;
import dev.onvoid.webrtc.RTCRtpSendParameters;
import dev.onvoid.webrtc.media.MediaStreamTrack;
import dev.onvoid.webrtc.media.video.VideoTrack;

import com.saferoom.log.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.TimeUnit;

/**
 * GroupCallManager - Manages mesh topology group calls (≤4 participants)
 * 
 * Architecture:
 * - Each participant maintains N-1 direct WebRTC peer connections (mesh)
 * - Server acts as signaling relay (distributes
 * MESH_OFFER/MESH_ANSWER/MESH_ICE_CANDIDATE)
 * - Room-based: participants join via roomId
 * 
 * Flow:
 * 1. User creates/joins room → sends ROOM_JOIN
 * 2. Server responds with ROOM_JOINED + peerList
 * 3. For each peer: create WebRTCClient, exchange SDP offers/answers
 * 4. New peer joins → all receive ROOM_PEER_JOINED → create new connection
 * 5. Peer leaves → all receive ROOM_PEER_LEFT → close connection
 */
public class GroupCallManager {

    private static final Logger logger = Logger.getLogger(GroupCallManager.class);
    private static GroupCallManager instance;

    // Room state
    private String currentRoomId;
    private String localUsername;
    private boolean inRoom = false;

    // Mesh connections: peerId → WebRTCClient
    private final Map<String, WebRTCClient> peerConnections = new ConcurrentHashMap<>();
    // Synchronization: peerId → Future that completes when tracks are added
    private final Map<String, CompletableFuture<Void>> peerReadyFutures = new ConcurrentHashMap<>();
    // 📺 Remote Track Buffering: stores tracks that arrive before UI callback is
    // registered
    // Fixes race condition where meeting UI misses remote video from peers
    private final Map<String, java.util.List<MediaStreamTrack>> pendingPeerTracks = new ConcurrentHashMap<>();

    // Local video track for preview (created immediately, shared by all peer
    // connections)
    private VideoTrack localVideoTrack;
    private dev.onvoid.webrtc.media.video.VideoDeviceSource localVideoSource;
    private CameraCaptureService.CaptureProfile currentCaptureProfile;
    private final AdaptiveCapturePolicy adaptiveCapturePolicy = new AdaptiveCapturePolicy();

    // Signaling client
    private WebRTCSignalingClient signalingClient;

    // Room settings
    private boolean audioEnabled = true;
    private boolean videoEnabled = true;

    // Callbacks
    private Runnable onRoomJoinedCallback;
    private Runnable onRoomLeftCallback;
    private PeerJoinedCallback onPeerJoinedCallback;
    private PeerLeftCallback onPeerLeftCallback;
    private RemoteTrackCallback onRemoteTrackCallback;
    private RoomErrorCallback onRoomErrorCallback; // NEW: Room error callback

    /**
     * Singleton pattern
     */
    public static synchronized GroupCallManager getInstance() {
        if (instance == null) {
            instance = new GroupCallManager();
        }
        return instance;
    }

    private GroupCallManager() {
        // Private constructor
    }

    /**
     * Initialize with signaling client
     */
    public void initialize(WebRTCSignalingClient signalingClient, String username) {
        this.signalingClient = signalingClient;
        this.localUsername = username;

        logger.info("Initialized for user: " + username);

        // Register signal handlers for group call signals
        setupSignalHandlers();
    }

    /**
     * Setup signal handlers for group call messages
     */
    private void setupSignalHandlers() {
        if (signalingClient == null) {
            logger.error("Cannot setup handlers - signaling client null", null);
            return;
        }

        // Handle ROOM_JOINED (server confirms join + sends peer list)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.ROOM_JOINED, signal -> {
            handleRoomJoined(signal);
        });

        // Handle ROOM_PEER_JOINED (new peer joined room)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.ROOM_PEER_JOINED, signal -> {
            handlePeerJoined(signal);
        });

        // Handle ROOM_PEER_LEFT (peer left room)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.ROOM_PEER_LEFT, signal -> {
            handlePeerLeft(signal);
        });

        // Handle MESH_OFFER (peer sending SDP offer)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.MESH_OFFER, signal -> {
            handleMeshOffer(signal);
        });

        // Handle MESH_ANSWER (peer sending SDP answer)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.MESH_ANSWER, signal -> {
            handleMeshAnswer(signal);
        });

        // Handle MESH_ICE_CANDIDATE (peer sending ICE candidate)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.MESH_ICE_CANDIDATE, signal -> {
            handleMeshIceCandidate(signal);
        });

        // Handle ROOM_ERROR (room not found, full, etc.)
        signalingClient.addSignalHandler(WebRTCSignal.SignalType.ROOM_ERROR, signal -> {
            handleRoomError(signal);
        });
    }

    /**
     * Join a room with mode validation
     * 
     * @param roomId Room ID to join
     * @param audio  Enable audio
     * @param video  Enable video
     * @param mode   "CREATE_MODE" (always create) or "JOIN_MODE" (only join
     *               existing)
     */
    public CompletableFuture<Void> joinRoom(String roomId, boolean audio, boolean video, String mode) {
        logger.info(String.format("Joining room: %s (mode=%s, audio=%b, video=%b)",
                roomId, mode, audio, video));

        if (inRoom) {
            logger.warn("Already in a room, leave first!");
            return CompletableFuture.failedFuture(new IllegalStateException("Already in room"));
        }

        this.currentRoomId = roomId;
        this.audioEnabled = audio;
        this.videoEnabled = video;
        this.inRoom = true;

        // FIX: Create local video track async, then join
        CompletableFuture<Void> videoTrackFuture = video ? createLocalVideoTrack()
                : CompletableFuture.completedFuture(null);

        return videoTrackFuture.thenCompose(v -> {
            // Send ROOM_JOIN signal with mode
            WebRTCSignal joinSignal = WebRTCSignal.newBuilder()
                    .setType(WebRTCSignal.SignalType.ROOM_JOIN)
                    .setFrom(localUsername)
                    .setRoomId(roomId)
                    .setAudioEnabled(audio)
                    .setVideoEnabled(video)
                    .setMetadata(mode) // "CREATE_MODE" or "JOIN_MODE"
                    .setTimestamp(System.currentTimeMillis())
                    .build();

            return signalingClient.sendSignal(joinSignal)
                    .thenAccept(response -> {
                        logger.info(String.format("Room join signal sent (mode=%s)", mode));
                    });
        }).exceptionally(ex -> {
            logger.error("Failed to join room: " + ex.getMessage(), ex);
            inRoom = false; // Reset state on failure
            return null;
        });
    }

    /**
     * Join a room (creates room if doesn't exist) - backward compatibility
     * Default mode: CREATE_MODE
     */
    public CompletableFuture<Void> joinRoom(String roomId, boolean audio, boolean video) {
        return joinRoom(roomId, audio, video, "CREATE_MODE");
    }

    /**
     * Leave current room
     */
    public CompletableFuture<Void> leaveRoom() {
        if (!inRoom) {
            logger.warn("Not in a room");
            return CompletableFuture.completedFuture(null);
        }

        logger.info("Leaving room: " + currentRoomId);

        // Send ROOM_LEAVE signal
        WebRTCSignal leaveSignal = WebRTCSignal.newBuilder()
                .setType(WebRTCSignal.SignalType.ROOM_LEAVE)
                .setFrom(localUsername)
                .setRoomId(currentRoomId)
                .setTimestamp(System.currentTimeMillis())
                .build();

        return signalingClient.sendSignal(leaveSignal)
                .thenAccept(response -> {
                    logger.info("Left room successfully");
                    cleanup();
                })
                .exceptionally(ex -> {
                    logger.error("Error leaving room: " + ex.getMessage(), ex);
                    cleanup(); // Cleanup anyway
                    return null;
                });
    }

    /**
     * Handle ROOM_JOINED signal (server sends peer list)
     */
    private void handleRoomJoined(WebRTCSignal signal) {
        logger.info("Room joined confirmation received");

        List<String> peerList = signal.getPeerListList();
        logger.info("Current peers in room: " + peerList);

        // Create peer connections for existing peers
        for (String peerUsername : peerList) {
            if (!peerUsername.equals(localUsername)) {
                createPeerConnection(peerUsername, true); // true = we initiate offer

                if (onPeerJoinedCallback != null) {
                    javafx.application.Platform.runLater(() -> onPeerJoinedCallback.onPeerJoined(peerUsername));
                }
            }
        }

        maybeReconfigureLocalVideoTrack().exceptionally(ex -> {
            logger.error("❌ Reconfiguration failed after room join: " + ex.getMessage(), ex);
            return null;
        });

        if (onRoomJoinedCallback != null) {
            onRoomJoinedCallback.run();
        }

        // Fix Bug 3-13: Distribute bandwidth after joining
        distributeUploadBandwidth();
    }

    /**
     * Handle ROOM_PEER_JOINED signal (new peer joined)
     */
    private void handlePeerJoined(WebRTCSignal signal) {
        String newPeerUsername = signal.getFrom();
        logger.info("New peer joined: " + newPeerUsername);

        if (newPeerUsername.equals(localUsername)) {
            // Ignore self
            return;
        }

        // Create peer connection (we DON'T initiate offer, new peer will)
        createPeerConnection(newPeerUsername, false);

        // Notify UI
        if (onPeerJoinedCallback != null) {
            onPeerJoinedCallback.onPeerJoined(newPeerUsername);
        }

        maybeReconfigureLocalVideoTrack().exceptionally(ex -> {
            logger.error("❌ Reconfiguration failed after peer join: " + ex.getMessage(), ex);
            return null;
        });

        // Fix Bug 3-13: Redistribute bandwidth when new peer joins
        distributeUploadBandwidth();
    }

    /**
     * Handle ROOM_PEER_LEFT signal (peer left room)
     */
    private void handlePeerLeft(WebRTCSignal signal) {
        String peerUsername = signal.getFrom();
        logger.info("Peer left: " + peerUsername);

        // Fix Bug 3-15: Move cleanup inside Platform.runLater to avoid crash
        javafx.application.Platform.runLater(() -> {
            // 1. Notify UI first (detach video sink)
            if (onPeerLeftCallback != null) {
                onPeerLeftCallback.onPeerLeft(peerUsername);
            }

            // 2. Then close and remove peer connection
            WebRTCClient client = peerConnections.remove(peerUsername);
            if (client != null) {
                client.close();
                logger.info("Connection to " + peerUsername + " closed");
            }

            // Fix Bug 3-13: Redistribute bandwidth NOW (After removal is guaranteed)
            distributeUploadBandwidth();
        });

        maybeReconfigureLocalVideoTrack().exceptionally(ex -> {
            logger.error("❌ Reconfiguration failed after peer left: " + ex.getMessage(), ex);
            return null;
        });
    }

    /**
     * Handle ROOM_ERROR signal (room not found, full, etc.)
     */
    private void handleRoomError(WebRTCSignal signal) {
        String errorType = signal.hasMetadata() ? signal.getMetadata() : "UNKNOWN_ERROR";
        String roomId = signal.getRoomId();

        logger.error(String.format("❌ Room error: %s (room=%s)", errorType, roomId), null);

        // Reset state
        inRoom = false;
        currentRoomId = null;

        // Cleanup any partial initialization
        cleanup();

        // Notify UI
        if (onRoomErrorCallback != null) {
            javafx.application.Platform.runLater(() -> {
                onRoomErrorCallback.onRoomError(errorType, roomId);
            });
        }
    }

    /**
     * Handle MESH_OFFER signal (peer sending SDP offer)
     */
    private void handleMeshOffer(WebRTCSignal signal) {
        String peerUsername = signal.getFrom();
        String sdp = signal.getSdp();

        logger.info("Received MESH_OFFER from " + peerUsername);

        WebRTCClient client = peerConnections.get(peerUsername);
        if (client == null) {
            logger.warn("No connection found for peer: " + peerUsername);
            return;
        }

        // Set remote SDP offer
        client.setRemoteDescription("offer", sdp);

        // FIX: Wait for local tracks to be ready before creating answer
        CompletableFuture<Void> readyFuture = peerReadyFutures.get(peerUsername);
        if (readyFuture == null) {
            readyFuture = CompletableFuture.completedFuture(null);
        }

        readyFuture.thenRun(() -> {
            logger.info("✅ Peer ready (tracks added), proceeding to create ANSWER for " + peerUsername);

            // Create and send answer
            // FIX: Added timeout to prevent hangs
            client.createAnswer()
                    .orTimeout(5, TimeUnit.SECONDS)
                    .thenAccept(answerSDP -> {
                        logger.info("Sending MESH_ANSWER to " + peerUsername);

                        WebRTCSignal answerSignal = WebRTCSignal.newBuilder()
                                .setType(WebRTCSignal.SignalType.MESH_ANSWER)
                                .setFrom(localUsername)
                                .setTo(peerUsername)
                                .setRoomId(currentRoomId)
                                .setSdp(answerSDP)
                                .setTimestamp(System.currentTimeMillis())
                                .build();

                        signalingClient.sendSignal(answerSignal);
                    })
                    .exceptionally(e -> {
                        logger.error("❌ Failed to create/send answer: " + e.getMessage(), e);
                        return null;
                    });
        });
    }

    /**
     * Handle MESH_ANSWER signal (peer sending SDP answer)
     */
    private void handleMeshAnswer(WebRTCSignal signal) {
        String peerUsername = signal.getFrom();
        String sdp = signal.getSdp();

        logger.info("Received MESH_ANSWER from " + peerUsername);

        WebRTCClient client = peerConnections.get(peerUsername);
        if (client == null) {
            logger.warn("No connection found for peer: " + peerUsername);
            return;
        }

        // Set remote SDP answer
        client.setRemoteDescription("answer", sdp);
    }

    /**
     * Handle MESH_ICE_CANDIDATE signal (peer sending ICE candidate)
     */
    private void handleMeshIceCandidate(WebRTCSignal signal) {
        String peerUsername = signal.getFrom();
        String candidate = signal.getCandidate();
        String sdpMid = signal.getSdpMid();
        int sdpMLineIndex = signal.getSdpMLineIndex();

        logger.info("Received MESH_ICE_CANDIDATE from " + peerUsername);

        WebRTCClient client = peerConnections.get(peerUsername);
        if (client == null) {
            logger.warn("No connection found for peer: " + peerUsername);
            return;
        }

        // Add ICE candidate
        client.addIceCandidate(candidate, sdpMid, sdpMLineIndex);
    }

    /**
     * Create peer connection for a peer
     * 
     * @param peerUsername  Peer's username
     * @param initiateOffer true if we should send offer, false if we wait for
     *                      peer's offer
     */
    private CompletableFuture<Void> createPeerConnection(String peerUsername, boolean initiateOffer) {
        if (peerConnections.containsKey(peerUsername)) {
            logger.debug(String.format("Connection to %s already exists", peerUsername));
            return CompletableFuture.completedFuture(null);
        }

        logger.info(String.format("Creating peer connection to: %s (initiate=%b)",
                peerUsername, initiateOffer));

        // Create unique callId for this peer connection
        String callId = UUID.randomUUID().toString();

        // Create WebRTCClient
        WebRTCClient client = new WebRTCClient(callId, peerUsername);
        peerConnections.put(peerUsername, client);

        // Setup callbacks
        setupPeerCallbacks(client, peerUsername);

        // Create peer connection
        client.createPeerConnection(audioEnabled, videoEnabled);

        // Add local tracks
        List<CompletableFuture<Void>> trackFutures = new ArrayList<>();

        if (audioEnabled) {
            // addAudioTrack is now async
            trackFutures.add(client.addAudioTrack());
        }
        if (videoEnabled) {
            // Use SHARED video track from GroupCallManager (don't create new camera source)
            if (localVideoTrack != null) {
                client.addSharedVideoTrack(localVideoTrack);
                // Bandwidth distributed dynamically now
            } else {
                logger.error("Local video track not ready!", null);
            }
        }

        // Wait for tracks to be added before creating offer
        CompletableFuture<Void> readyFuture = CompletableFuture.allOf(trackFutures.toArray(new CompletableFuture[0]));

        // Register readiness
        peerReadyFutures.put(peerUsername, readyFuture);

        readyFuture.thenRun(() -> {
            // If we initiate, create and send offer
            if (initiateOffer) {
                // FIX: Added timeout
                client.createOffer()
                        .orTimeout(5, TimeUnit.SECONDS)
                        .thenAccept(offerSDP -> {
                            logger.info("Sending MESH_OFFER to " + peerUsername);

                            WebRTCSignal offerSignal = WebRTCSignal.newBuilder()
                                    .setType(WebRTCSignal.SignalType.MESH_OFFER)
                                    .setFrom(localUsername)
                                    .setTo(peerUsername)
                                    .setRoomId(currentRoomId)
                                    .setSdp(offerSDP)
                                    .setAudioEnabled(audioEnabled)
                                    .setVideoEnabled(videoEnabled)
                                    .setTimestamp(System.currentTimeMillis())
                                    .build();

                            signalingClient.sendSignal(offerSignal);
                        })
                        .exceptionally(e -> {
                            logger.error("❌ Failed to create offer: " + e.getMessage(), e);
                            return null;
                        });
            }
        });

        return readyFuture;
    }

    /**
     * Setup callbacks for a peer connection
     */
    private void setupPeerCallbacks(WebRTCClient client, String peerUsername) {
        // ICE candidate callback
        client.setOnIceCandidateCallback(candidate -> {
            logger.debug("Sending MESH_ICE_CANDIDATE to " + peerUsername);

            WebRTCSignal iceSignal = WebRTCSignal.newBuilder()
                    .setType(WebRTCSignal.SignalType.MESH_ICE_CANDIDATE)
                    .setFrom(localUsername)
                    .setTo(peerUsername)
                    .setRoomId(currentRoomId)
                    .setCandidate(candidate.sdp)
                    .setSdpMid(candidate.sdpMid)
                    .setSdpMLineIndex(candidate.sdpMLineIndex)
                    .setTimestamp(System.currentTimeMillis())
                    .build();

            signalingClient.sendSignal(iceSignal);
        });

        // Connection established callback
        client.setOnConnectionEstablishedCallback(() -> {
            logger.info("Connection established with " + peerUsername);
        });

        // Connection closed callback
        client.setOnConnectionClosedCallback(() -> {
            logger.info("Connection closed with " + peerUsername);
            peerConnections.remove(peerUsername);
        });

        // Remote track callback
        client.setOnRemoteTrackCallback(track -> {
            logger.info(String.format("Remote track from %s: %s (%s)",
                    peerUsername, track.getId(), track.getKind()));

            // Notify UI if callback is registered
            if (onRemoteTrackCallback != null) {
                onRemoteTrackCallback.onRemoteTrack(peerUsername, track);
            } else {
                // Buffer track for later - UI callback not registered yet
                // This happens when peer tracks arrive before MeetingCallDialog is shown
                logger.info("📺 Buffering remote track from " + peerUsername + ": " + track.getId());
                pendingPeerTracks
                        .computeIfAbsent(peerUsername, k -> new java.util.ArrayList<>())
                        .add(track);
            }
        });
    }

    /**
     * Cleanup all connections
     */
    private void cleanup() {
        logger.info("Cleaning up all connections...");

        // Close all peer connections
        for (Map.Entry<String, WebRTCClient> entry : peerConnections.entrySet()) {
            logger.debug("Closing connection to " + entry.getKey());
            entry.getValue().close();
        }

        peerConnections.clear();
        peerReadyFutures.clear();
        pendingPeerTracks.clear(); // Clear buffered tracks

        // Stop and dispose local video track
        if (localVideoSource != null) {
            try {
                localVideoSource.stop();
                localVideoSource.dispose();
                logger.info("Local video source stopped and disposed");
            } catch (Exception e) {
                logger.error("Error disposing video source: " + e.getMessage(), e);
            }
            localVideoSource = null;
        }

        if (localVideoTrack != null) {
            try {
                localVideoTrack.setEnabled(false);
                localVideoTrack.dispose();
                logger.info("Local video track disposed");
            } catch (Exception e) {
                logger.error("Error disposing video track: " + e.getMessage(), e);
            }
            localVideoTrack = null;
        }
        currentCaptureProfile = null;

        inRoom = false;
        currentRoomId = null;

        // Notify UI
        if (onRoomLeftCallback != null) {
            onRoomLeftCallback.run();
        }

        System.out.println("[GroupCallManager] Cleanup complete");
    }

    /**
     * Toggle local audio
     */
    public void toggleAudio(boolean enabled) {
        this.audioEnabled = enabled;

        // Toggle audio for all peer connections
        for (WebRTCClient client : peerConnections.values()) {
            client.toggleAudio(enabled);
        }

        logger.info(String.format("Audio %s for all peers", enabled ? "enabled" : "muted"));
    }

    /**
     * Toggle local video
     */
    public void toggleVideo(boolean enabled) {
        this.videoEnabled = enabled;

        // CRITICAL: Toggle local video track itself (for self-preview)
        if (localVideoTrack != null) {
            localVideoTrack.setEnabled(enabled);
            logger.info(String.format("Local video track %s", enabled ? "enabled" : "disabled"));
        }

        // Toggle video for all peer connections (for remote peers)
        for (WebRTCClient client : peerConnections.values()) {
            client.toggleVideo(enabled);
        }

        logger.info(String.format("Video %s for all peers", enabled ? "enabled" : "disabled"));
    }

    /**
     * Get local video track (for self-preview)
     */
    public VideoTrack getLocalVideoTrack() {
        // Return our pre-created local video track
        return localVideoTrack;
    }

    /**
     * Create local video track for self-preview and sharing with peers
     */
    /**
     * Create local video track for self-preview and sharing with peers
     * Async to prevent UI blocking
     */
    private CompletableFuture<Void> createLocalVideoTrack() {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Creating local video track for preview...");
                CameraCaptureService.CaptureProfile profile = adaptiveCapturePolicy
                        .selectProfile(getExpectedParticipantCount());
                CameraCaptureService.CameraCaptureResource resource = CameraCaptureService
                        .createCameraTrack("group_local_video", profile);

                localVideoSource = resource.getSource();
                localVideoTrack = resource.getTrack();
                currentCaptureProfile = profile;

                // FIX: Explicitly start capture
                resource.startCapture();

                logger.info("✅ Local video track created, enabled, and started");

            } catch (Exception e) {
                logger.error("Failed to create local video track: " + e.getMessage(), e);
                throw new RuntimeException("Failed to create local video track", e);
            }
        });
    }

    private CompletableFuture<Void> maybeReconfigureLocalVideoTrack() {
        if (!videoEnabled || localVideoTrack == null) {
            return CompletableFuture.completedFuture(null);
        }
        CameraCaptureService.CaptureProfile desired = adaptiveCapturePolicy
                .selectProfile(getExpectedParticipantCount());
        if (desired.equals(currentCaptureProfile)) {
            return CompletableFuture.completedFuture(null);
        }
        logger.info(String.format("Adjusting capture profile to %dx%d@%dfps",
                desired.width(), desired.height(), desired.fps()));

        if (localVideoSource == null) {
            // FIX: Return the future so caller knows when it's ready
            return createLocalVideoTrack();
        }

        try {
            localVideoSource.stop();
            dev.onvoid.webrtc.media.video.VideoCaptureCapability capability = new dev.onvoid.webrtc.media.video.VideoCaptureCapability(
                    desired.width(), desired.height(), desired.fps());
            localVideoSource.setVideoCaptureCapability(capability);
            localVideoSource.start();
            currentCaptureProfile = desired;
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            logger.error("Failed to reconfigure camera: " + ex.getMessage(), ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    /**
     * Fix Bug 3-13: Dynamic bandwidth distribution
     * Distributes 2500kbps across all active peers (min 200kbps)
     */
    private void distributeUploadBandwidth() {
        if (peerConnections.isEmpty()) {
            return;
        }

        int activePeers = peerConnections.size();
        final int TOTAL_UPLOAD_BUDGET_KBPS = 2500;
        final int MIN_BITRATE_KBPS = 200;

        // Calculate target per peer
        int targetBitrateKbps = Math.max(MIN_BITRATE_KBPS, TOTAL_UPLOAD_BUDGET_KBPS / activePeers);
        int targetBitrateBps = targetBitrateKbps * 1000;

        logger.info(String.format("Distributing bandwidth: %dkbps total / %d peers = %dkbps each",
                TOTAL_UPLOAD_BUDGET_KBPS, activePeers, targetBitrateKbps));

        // Apply to all peers
        for (WebRTCClient client : peerConnections.values()) {
            applyBitrateToClient(client, targetBitrateBps);
        }
    }

    private void applyBitrateToClient(WebRTCClient client, int bitrateBps) {
        if (client == null || client.getVideoSender() == null) {
            return;
        }
        try {
            RTCRtpSendParameters params = client.getVideoSender().getParameters();
            if (params == null || params.encodings == null) {
                return;
            }
            for (RTCRtpEncodingParameters encoding : params.encodings) {
                if (encoding == null)
                    continue;
                encoding.maxBitrate = bitrateBps;
            }
            client.getVideoSender().setParameters(params);
        } catch (Exception ex) {
            logger.error("Failed to update bitrate: " + ex.getMessage(), ex);
        }
    }

    /**
     * Get list of connected peer usernames
     */
    public List<String> getConnectedPeers() {
        return new ArrayList<>(peerConnections.keySet());
    }

    /**
     * Get peer connection for specific peer
     */
    public WebRTCClient getPeerConnection(String peerUsername) {
        return peerConnections.get(peerUsername);
    }

    // ===============================
    // Callback Setters
    // ===============================

    public void setOnRoomJoinedCallback(Runnable callback) {
        this.onRoomJoinedCallback = callback;
    }

    public void setOnRoomLeftCallback(Runnable callback) {
        this.onRoomLeftCallback = callback;
    }

    public void setOnPeerJoinedCallback(PeerJoinedCallback callback) {
        this.onPeerJoinedCallback = callback;
    }

    public void setOnPeerLeftCallback(PeerLeftCallback callback) {
        this.onPeerLeftCallback = callback;
    }

    public void setOnRemoteTrackCallback(RemoteTrackCallback callback) {
        this.onRemoteTrackCallback = callback;

        // Replay any buffered tracks that arrived before the callback was registered
        if (callback != null && !pendingPeerTracks.isEmpty()) {
            // Copy and clear to prevent concurrent modification
            Map<String, java.util.List<MediaStreamTrack>> toReplay = new java.util.HashMap<>(pendingPeerTracks);
            pendingPeerTracks.clear();

            int totalTracks = toReplay.values().stream().mapToInt(java.util.List::size).sum();
            logger.info("📺 Replaying " + totalTracks + " buffered remote track(s) from " + toReplay.size()
                    + " peer(s)...");

            for (Map.Entry<String, java.util.List<MediaStreamTrack>> entry : toReplay.entrySet()) {
                String peer = entry.getKey();
                for (MediaStreamTrack track : entry.getValue()) {
                    logger.info(
                            "  → Replaying track from " + peer + ": " + track.getId() + " (" + track.getKind() + ")");
                    callback.onRemoteTrack(peer, track);
                }
            }
        }
    }

    public void setOnRoomErrorCallback(RoomErrorCallback callback) {
        this.onRoomErrorCallback = callback;
    }

    // ===============================
    // Callback Interfaces
    // ===============================

    @FunctionalInterface
    public interface PeerJoinedCallback {
        void onPeerJoined(String peerUsername);
    }

    @FunctionalInterface
    public interface PeerLeftCallback {
        void onPeerLeft(String peerUsername);
    }

    @FunctionalInterface
    public interface RemoteTrackCallback {
        void onRemoteTrack(String peerUsername, MediaStreamTrack track);
    }

    @FunctionalInterface
    public interface RoomErrorCallback {
        void onRoomError(String errorType, String roomId);
    }

    // ===============================
    // Getters
    // ===============================

    public boolean isInRoom() {
        return inRoom;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public int getPeerCount() {
        return peerConnections.size();
    }

    private int getExpectedParticipantCount() {
        // include self
        return peerConnections.size() + 1;
    }
}
