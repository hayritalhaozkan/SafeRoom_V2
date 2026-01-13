package com.saferoom.webrtc.pipeline;

import com.saferoom.util.NativeLibraryLoader;
import java.nio.ByteBuffer;

/**
 * JNI-based Video Encoder Implementation.
 * 
 * <h2>Zero-Copy Architecture</h2>
 * <p>
 * This class serves as the bridge to the native C++ encoding layer.
 * By passing the {@link java.nio.DirectByteBuffer} directly to native code,
 * we avoid all JVM-heap copies. The native code can read the YUV/ARGB data
 * directly from the native memory address of the buffer.
 * </p>
 * 
 * <h2>Usage</h2>
 * 
 * <pre>
 * NativeVideoEncoder encoder = new NativeVideoEncoder();
 * encoder.init(640, 480, 30);
 * encoder.encodeFrame(directBuffer, timestamp);
 * encoder.release();
 * </pre>
 */
public class NativeVideoEncoder {

    static {
        // Load the platform-specific native library (e.g. libnative_encoder.so)
        // Checks System path first, then extracts from JAR resources.
        NativeLibraryLoader.loadLibrary("native_encoder");
    }

    /**
     * Encode a frame data directly from a ByteBuffer.
     * 
     * @param buffer DirectByteBuffer containing frame data (YUV or ARGB).
     *               MUST be allocated with ByteBuffer.allocateDirect().
     * @param length Length of data to encode.
     * @param width  Frame width.
     * @param height Frame height.
     * @return Encoded size or error code.
     */
    public native int encodeFrame(ByteBuffer buffer, int length, int width, int height);

    /*
     * Example C++ implementation provided in documentation
     * ... (comments preserved from original)
     */
}
