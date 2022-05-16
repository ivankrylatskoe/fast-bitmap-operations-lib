#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#define  LOG_TAG    "FastBitmapRotationLibLog"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C" {
    JNIEXPORT jboolean JNICALL
    Java_com_jni_fast_1bitmap_1rotation_JniFastBitmapRotationLib_jniRotateBitmap180(
            JNIEnv *env, jclass cls, jobject bitmap);
}

JNIEXPORT jboolean JNICALL
Java_com_jni_fast_1bitmap_1rotation_JniFastBitmapRotationLib_jniRotateBitmap180(
        JNIEnv *env, jclass cls, jobject bitmap)
{
    AndroidBitmapInfo bitmapInfo;
    uint32_t* storedBitmapPixels = NULL;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed, error=%d", ret);
        return false;
    }
    if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888");
        return false;
    }
    if (bitmapInfo.stride != bitmapInfo.width * sizeof(uint32_t)) {
        LOGE("Bitmap stride <> width * 4");
        return false;
    }

    void* bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed, error=%d", ret);
        return false;
    }
    uint32_t* src = (uint32_t*) bitmapPixels;
    uint32_t* dst = src + bitmapInfo.width * bitmapInfo.height - 1;
    while(dst > src) {
        uint32_t tmp = *src;
        *src = *dst;
        *dst = tmp;
        dst--;
        src++;
    }
    if ((ret = AndroidBitmap_unlockPixels(env, bitmap)) < 0) {
        LOGE("AndroidBitmap_unlockPixels() failed, error=%d", ret);
        return false;
    }
    return true;
}
