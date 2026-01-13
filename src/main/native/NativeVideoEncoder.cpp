#include <jni.h>
#include <iostream>
#include <cstring>
#include <vector>
#include <cstdint>
/*
 * Implementation of the NativeVideoEncoder JNI methods.
 * 
 * This is a boilerplate/connector implementation. 
 * In a real production environment, you would link this against 
 * libraries like FFmpeg (libavcodec) or x264/OpenH264.
 */

extern "C" {

    /*
     * Class:     com_saferoom_webrtc_pipeline_NativeVideoEncoder
     * Method:    encodeFrame
     * Signature: (Ljava/nio/ByteBuffer;III)I
     */
    JNIEXPORT jint JNICALL Java_com_saferoom_webrtc_pipeline_NativeVideoEncoder_encodeFrame
      (JNIEnv *env, jobject thisObj, jobject buffer, jint length, jint width, jint height) {
        
        // 1. Get Direct Buffer Address (Zero Copy)
        // This gives us a pointer to the actual memory used by the DirectByteBuffer in Java.
        // NO COPY is performed here.
        void* raw_pixels = env->GetDirectBufferAddress(buffer);
        
        if (raw_pixels == nullptr) {
            std::cerr << "[NativeVideoEncoder] Error: Buffer is not direct or is invalid!" << std::endl;
            return -1;
        }

        // Validate capacity (optional, but good practice)
        jlong capacity = env->GetDirectBufferCapacity(buffer);
        if (capacity < length) {
             std::cerr << "[NativeVideoEncoder] Error: Buffer capacity (" << capacity 
                       << ") less than requested length (" << length << ")" << std::endl;
             return -2;
        }

        // ---------------------------------------------------------
        // SIMULATED ENCODING LOGIC
        // ---------------------------------------------------------
        // In a real implementation:
        // x264_picture_t pic;
        // x264_picture_alloc(&pic, X264_CSP_I420, width, height);
        // memcpy(pic.img.plane[0], raw_pixels, ...);
        // x264_encoder_encode(..., &pic, ...);
        // ---------------------------------------------------------

        // For demonstration, we just touch the memory to ensure access works
        // and print a log once in a while.
        
        static int frame_counter = 0;
        frame_counter++;
        
        if (frame_counter % 100 == 0) {
            uint8_t* byte_ptr = static_cast<uint8_t*>(raw_pixels);
            std::cout << "[NativeVideoEncoder] Enocded frame #" << frame_counter 
                      << " (Resolution: " << width << "x" << height 
                      << ", Ptr: " << raw_pixels 
                      << ", FirstByte: " << (int)byte_ptr[0] << ")" << std::endl;
        }

        // Return encoded size (simulated)
        return length / 10; 
    }

}
