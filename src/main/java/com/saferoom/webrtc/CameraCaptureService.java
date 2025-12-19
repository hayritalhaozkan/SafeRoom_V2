package com.saferoom.webrtc;

import dev.onvoid.webrtc.PeerConnectionFactory;
import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.video.VideoCaptureCapability;
import dev.onvoid.webrtc.media.video.VideoDevice;
import dev.onvoid.webrtc.media.video.VideoDeviceSource;
import dev.onvoid.webrtc.media.video.VideoTrack;

import java.util.List;

/**
 * Tek noktadan kamera capture kaynağı oluşturan yardımcı servis.
 * DM ve grup görüşmeleri aynı çözünürlük/FPS/GPU ayarlarını buradan alır.
 */
public final class CameraCaptureService {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;
    private static final int DEFAULT_FPS = 30;

    private CameraCaptureService() {
    }

    public static CameraCaptureResource createCameraTrack(String trackId) {
        return createCameraTrack(trackId, new CaptureProfile(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FPS));
    }

    public static CameraCaptureResource createCameraTrack(String trackId, CaptureProfile profile) {
        System.out.println("[CameraCaptureService] ═══════════════════════════════════════════");
        System.out.println("[CameraCaptureService] Creating camera track: " + trackId);

        PeerConnectionFactory factory = WebRTCClient.getFactory();
        if (factory == null) {
            throw new IllegalStateException("[CameraCaptureService] PeerConnectionFactory bulunamadı");
        }

        // List all available cameras
        List<VideoDevice> cameras = MediaDevices.getVideoCaptureDevices();
        System.out.printf("[CameraCaptureService] Found %d camera(s):%n", cameras.size());
        for (int i = 0; i < cameras.size(); i++) {
            System.out.printf("  [%d] %s%n", i, cameras.get(i).getName());
        }

        if (cameras.isEmpty()) {
            throw new IllegalStateException("[CameraCaptureService] Kamera bulunamadı");
        }

        // Smart Camera Selection
        VideoDevice camera = selectBestCamera(cameras);
        if (camera == null) {
            throw new IllegalStateException("[CameraCaptureService] No suitable camera found");
        }
        System.out.println("[CameraCaptureService] Selected camera: " + camera.getName());

        VideoDeviceSource source = new VideoDeviceSource();
        source.setVideoCaptureDevice(camera);
        System.out.println("[CameraCaptureService] VideoDeviceSource created");

        VideoCaptureCapability capability = new VideoCaptureCapability(
                profile.width(),
                profile.height(),
                profile.fps());
        source.setVideoCaptureCapability(capability);
        System.out.printf("[CameraCaptureService] Capture capability set: %dx%d@%dfps%n",
                profile.width(), profile.height(), profile.fps());

        VideoTrack track = factory.createVideoTrack(trackId, source);
        track.setEnabled(true);
        System.out.printf("[CameraCaptureService] VideoTrack created: id=%s, enabled=%b%n",
                track.getId(), track.isEnabled());

        System.out.println("[CameraCaptureService] ═══════════════════════════════════════════");
        return new CameraCaptureResource(source, track);
    }

    private static VideoDevice selectBestCamera(List<VideoDevice> devices) {
        if (devices == null || devices.isEmpty())
            return null;

        // 1. Filter out known bad devices (loopback, virtual, dummy)
        List<VideoDevice> validDevices = devices.stream()
                .filter(d -> {
                    String name = d.getName().toLowerCase();
                    return !name.contains("loopback") &&
                            !name.contains("virtual") &&
                            !name.contains("dummy");
                })
                .toList();

        // If all were filtered out, fallback to original list
        if (validDevices.isEmpty()) {
            System.out.println("[CameraCaptureService] ⚠️ All devices seem virtual/dummy, falling back to index 0");
            return devices.get(0);
        }

        // 2. Sort/Prioritize (Front/Integrated > USB > Others)
        return validDevices.stream()
                .min((d1, d2) -> {
                    String n1 = d1.getName().toLowerCase();
                    String n2 = d2.getName().toLowerCase();

                    boolean p1 = n1.contains("front") || n1.contains("integrated") || n1.contains("webcam");
                    boolean p2 = n2.contains("front") || n2.contains("integrated") || n2.contains("webcam");

                    return Boolean.compare(!p1, !p2); // True is "smaller" (comes first)
                })
                .orElse(validDevices.get(0));
    }

    public static final class CameraCaptureResource {
        private final VideoDeviceSource source;
        private final VideoTrack track;
        private boolean isStarted = false;

        public CameraCaptureResource(VideoDeviceSource source, VideoTrack track) {
            this.source = source;
            this.track = track;
        }

        public VideoDeviceSource getSource() {
            return source;
        }

        public VideoTrack getTrack() {
            return track;
        }

        public synchronized void startCapture() {
            if (isStarted)
                return;
            try {
                source.start();
                isStarted = true;
                System.out.println("[CameraCaptureService] ✅ Camera capture STARTED successfully");
            } catch (Exception e) {
                System.err.printf("[CameraCaptureService] ❌ Failed to start camera: %s%n", e.getMessage());
                throw new RuntimeException("Camera start failed", e);
            }
        }

        public synchronized void stopCapture() {
            if (!isStarted)
                return;
            try {
                source.stop();
                isStarted = false;
                System.out.println("[CameraCaptureService] 🛑 Camera capture STOPPED");
            } catch (Exception e) {
                System.err.printf("[CameraCaptureService] ❌ Failed to stop camera: %s%n", e.getMessage());
            }
        }

    }

    public record CaptureProfile(int width, int height, int fps) {
    }
}
