package com.saferoom.webrtc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for optimizing SDP generation with platform-aware codec preference.
 * 
 * Key principle: NEVER strip VP8/VP9/H264 - they are all needed for
 * cross-platform
 * compatibility. Instead, REORDER codecs based on platform preference.
 * 
 * - Windows: Prefers H.264 (hardware encoding via QuickSync/NVENC/VCE)
 * - Linux/Mac: Prefers VP8 (better software/VAAPI support)
 */
public class SDPUtils {

    // Platform detection (same as WebRTCClient)
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS_NAME.contains("win");
    private static final boolean IS_LINUX = OS_NAME.contains("linux");
    private static final boolean IS_MAC = OS_NAME.contains("mac");

    // Codec payload type patterns
    private static final Pattern RTPMAP_PATTERN = Pattern.compile("a=rtpmap:(\\d+)\\s+(\\S+)");
    private static final Pattern MLINE_VIDEO_PATTERN = Pattern.compile("m=video\\s+\\d+\\s+\\S+\\s+(.+)");

    /**
     * Optimize SDP by removing unused audio codecs (Opus is universal).
     * DOES NOT remove video codecs VP8/VP9/H264 - they're essential for
     * cross-platform.
     */
    public static String mungeSDP(String sdp) {
        if (sdp == null || sdp.isEmpty())
            return sdp;

        StringBuilder sb = new StringBuilder();
        String[] lines = sdp.split("\r\n");

        for (String line : lines) {
            String trimmed = line.trim();

            // 1. Remove specific unwanted lines entirely
            if (shouldRemoveLine(trimmed)) {
                continue;
            }

            // 2. Filter attribute lines (a=)
            if (trimmed.startsWith("a=")) {
                if (shouldRemoveAttribute(trimmed)) {
                    continue;
                }
            }

            // 3. Keep line
            sb.append(line).append("\r\n");
        }

        // 4. Reorder codec preference based on platform
        return reorderCodecPreference(sb.toString());
    }

    private static boolean shouldRemoveLine(String line) {
        // Remove known useless or heavy lines
        return false; // Aggressive filtering handled in attributes
    }

    private static boolean shouldRemoveAttribute(String line) {
        // ONLY remove uncommon audio codecs - Opus is universally supported
        // DO NOT remove VP8/VP9/H264 - they're essential for cross-platform video

        if (line.contains("AV1") || // AV1 has limited hardware support
                line.contains("PCMU") || line.contains("PCMA") || // G.711 variants
                line.contains("ISAC") || line.contains("G722")) { // Less common audio
            return true;
        }

        // Keep everything else (including VP8, VP9, H264)
        return false;
    }

    /**
     * Force 'sendrecv' direction on a specific media section.
     * Often needed when creating an Offer before the local track is fully live.
     */
    public static String enforceSendRecv(String sdp, String mediaType) {
        if (sdp == null || !sdp.contains("m=" + mediaType)) {
            return sdp;
        }

        StringBuilder sb = new StringBuilder();
        String[] lines = sdp.split("\r\n");
        boolean inMediaSection = false;

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.startsWith("m=")) {
                inMediaSection = trimmed.startsWith("m=" + mediaType);
            }

            if (inMediaSection) {
                // If we see an existing direction, replace it
                if (trimmed.equals("a=sendonly") || trimmed.equals("a=recvonly") || trimmed.equals("a=inactive")) {
                    sb.append("a=sendrecv").append("\r\n");
                    continue;
                }
            }

            sb.append(line).append("\r\n");
        }

        return sb.toString();
    }

    /**
     * Reorder video codec preference based on platform.
     * 
     * This ensures the PREFERRED codec is listed first in the m=video line,
     * but ALL codecs remain available for negotiation.
     * 
     * Windows: H264 first (hardware encoding support)
     * Linux/Mac: VP8 first (better software/VAAPI support)
     */
    public static String reorderCodecPreference(String sdp) {
        if (sdp == null || !sdp.contains("m=video")) {
            return sdp;
        }

        // Determine preferred codec based on platform
        String preferredCodec = IS_WINDOWS ? "H264" : "VP8";

        System.out.printf("[SDPUtils] Platform: %s → Preferred codec: %s%n",
                IS_WINDOWS ? "Windows" : (IS_LINUX ? "Linux" : (IS_MAC ? "macOS" : "Unknown")),
                preferredCodec);

        // Parse payload types for each codec
        List<String> h264Payloads = new ArrayList<>();
        List<String> vp8Payloads = new ArrayList<>();
        List<String> vp9Payloads = new ArrayList<>();
        List<String> otherPayloads = new ArrayList<>();

        String[] lines = sdp.split("\r\n");

        // First pass: collect payload types for each codec
        for (String line : lines) {
            Matcher m = RTPMAP_PATTERN.matcher(line);
            if (m.find()) {
                String payloadType = m.group(1);
                String codecName = m.group(2).toUpperCase();

                if (codecName.contains("H264")) {
                    h264Payloads.add(payloadType);
                } else if (codecName.contains("VP8")) {
                    vp8Payloads.add(payloadType);
                } else if (codecName.contains("VP9")) {
                    vp9Payloads.add(payloadType);
                }
            }
        }

        // Build ordered payload list based on platform preference
        List<String> orderedPayloads = new ArrayList<>();

        if (IS_WINDOWS) {
            // Windows: H264 → VP8 → VP9
            orderedPayloads.addAll(h264Payloads);
            orderedPayloads.addAll(vp8Payloads);
            orderedPayloads.addAll(vp9Payloads);
        } else {
            // Linux/Mac: VP8 → H264 → VP9
            orderedPayloads.addAll(vp8Payloads);
            orderedPayloads.addAll(h264Payloads);
            orderedPayloads.addAll(vp9Payloads);
        }

        // Second pass: reorder payloads in m=video line
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("m=video")) {
                Matcher m = MLINE_VIDEO_PATTERN.matcher(line);
                if (m.find()) {
                    String originalPayloads = m.group(1);
                    String[] allPayloads = originalPayloads.split("\\s+");

                    // Collect payloads not in our video codec list (RTX, RED, ULPFEC, etc.)
                    for (String pt : allPayloads) {
                        if (!orderedPayloads.contains(pt) && !otherPayloads.contains(pt)) {
                            otherPayloads.add(pt);
                        }
                    }

                    // Build new m= line with reordered payloads
                    StringBuilder newPayloads = new StringBuilder();
                    for (String pt : orderedPayloads) {
                        newPayloads.append(pt).append(" ");
                    }
                    for (String pt : otherPayloads) {
                        newPayloads.append(pt).append(" ");
                    }

                    String newMLine = line.substring(0, line.indexOf(originalPayloads)) +
                            newPayloads.toString().trim();
                    sb.append(newMLine).append("\r\n");

                    System.out.printf("[SDPUtils] Reordered video payloads: %s%n", newPayloads.toString().trim());
                    continue;
                }
            }
            sb.append(line).append("\r\n");
        }

        return sb.toString();
    }

}
