#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#define  LOG_TAG    "FastBitmapOperationsLibLog"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C" {
    JNIEXPORT jboolean JNICALL
    Java_com_jni_fast_1bitmap_1operations_JniFastBitmapOperationsLib_jniRotateBitmap180(
            JNIEnv *env, jclass cls, jobject bitmap);

    JNIEXPORT jboolean JNICALL
    Java_com_jni_fast_1bitmap_1operations_JniFastBitmapOperationsLib_jniResizeDownBitmap225InterArea(
            JNIEnv *env, jclass cls, jobject srcBitmap, jobject dstBitmap);
}


bool checkLockBitmapBits(JNIEnv *env, jobject& bitmap, AndroidBitmapInfo& bitmapInfo, void** ppBitmapPixels) {
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

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, ppBitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed, error=%d", ret);
        return false;
    }
    return true;
}



bool unlockBitmapBits(JNIEnv *env, jobject& bitmap) {
    int ret;
    if ((ret = AndroidBitmap_unlockPixels(env, bitmap)) < 0) {
        LOGE("AndroidBitmap_unlockPixels() failed, error=%d", ret);
        return false;
    }
    return true;
}

JNIEXPORT jboolean JNICALL
Java_com_jni_fast_1bitmap_1operations_JniFastBitmapOperationsLib_jniRotateBitmap180(
        JNIEnv *env, jclass cls, jobject bitmap)
{
    AndroidBitmapInfo bitmapInfo;
    uint32_t* storedBitmapPixels = NULL;
    void* pBitmapPixels;
    int ret;

    if (!checkLockBitmapBits(env, bitmap, bitmapInfo, &pBitmapPixels)) {
        return false;
    }

    uint32_t* src = (uint32_t*) pBitmapPixels;
    uint32_t* dst = src + bitmapInfo.width * bitmapInfo.height - 1;
    while(dst > src) {
        uint32_t tmp = *src;
        *src = *dst;
        *dst = tmp;
        dst--;
        src++;
    }

    return unlockBitmapBits(env, bitmap);;
}


#define RESIZE_225_CELL(d, di,s1,s2,s3,si,sh11,sh12,sh13,sh21,sh22,sh23,sh31,sh32,sh33) \
    d[di]   = ((((unsigned short) s1[si]) sh11) + (((unsigned short) s1[si+4]) sh12) + (((unsigned short) s1[si+8]) sh13) + \
               (((unsigned short) s2[si]) sh21) + (((unsigned short) s2[si+4]) sh22) + (((unsigned short) s2[si+8]) sh23) + \
               (((unsigned short) s3[si]) sh31) + (((unsigned short) s3[si+4]) sh32) + (((unsigned short) s3[si+8]) sh33) + \
               81) / 162; \
    d[di+1] = ((((unsigned short) s1[si+1]) sh11) + (((unsigned short) s1[si+5]) sh12) + (((unsigned short) s1[si+9]) sh13) + \
               (((unsigned short) s2[si+1]) sh21) + (((unsigned short) s2[si+5]) sh22) + (((unsigned short) s2[si+9]) sh23) + \
               (((unsigned short) s3[si+1]) sh31) + (((unsigned short) s3[si+5]) sh32) + (((unsigned short) s3[si+9]) sh33) + \
               81) / 162; \
    d[di+2] = ((((unsigned short) s1[si+2]) sh11) + (((unsigned short) s1[si+6]) sh12) + (((unsigned short) s1[si+10]) sh13) + \
               (((unsigned short) s2[si+2]) sh21) + (((unsigned short) s2[si+6]) sh22) + (((unsigned short) s2[si+10]) sh23) + \
               (((unsigned short) s3[si+2]) sh31) + (((unsigned short) s3[si+6]) sh32) + (((unsigned short) s3[si+10]) sh33) + \
               81) / 162; \
    d[di+3] = 255;



JNIEXPORT jboolean JNICALL
Java_com_jni_fast_1bitmap_1operations_JniFastBitmapOperationsLib_jniResizeDownBitmap225InterArea(
        JNIEnv *env, jclass cls, jobject srcBitmap, jobject dstBitmap)
{
    AndroidBitmapInfo srcBitmapInfo, dstBitmapInfo;
    void *pSrcBitmapPixels, *pDstBitmapPixels;
    if (!checkLockBitmapBits(env, srcBitmap, srcBitmapInfo, &pSrcBitmapPixels)) {
        return false;
    }
    if (!checkLockBitmapBits(env, dstBitmap, dstBitmapInfo, &pDstBitmapPixels)) {
        unlockBitmapBits(env, srcBitmap);
        return false;
    }

    bool checks = true;
    if (srcBitmapInfo.width % 9 != 0) {
        checks = false;
        LOGE("Source bitmap width must divide by 9");
    }
    if (srcBitmapInfo.height % 9 != 0) {
        checks = false;
        LOGE("Source bitmap height must divide by 9");
    }
    if (dstBitmapInfo.width % 4 != 0) {
        checks = false;
        LOGE("Destination bitmap width must divide by 4");
    }
    if (dstBitmapInfo.height % 4 != 0) {
        checks = false;
        LOGE("Destination bitmap height must divide by 4");
    }

    int dstWidth = srcBitmapInfo.width / 9 * 4;
    int dstHeight = srcBitmapInfo.height / 9 * 4;

    if ((dstBitmapInfo.width != dstWidth) || (dstBitmapInfo.height != dstHeight)) {
        checks = false;
        LOGE("Destination bitmap must be 9/4 times smaller than source");
    }

    if (!checks) {
        unlockBitmapBits(env, srcBitmap);
        unlockBitmapBits(env, dstBitmap);
        return false;
    }

    int srcLineStride = srcBitmapInfo.width * sizeof(uint32_t);
    int dstLineStride = dstWidth * 4;
    int srcLineStride9 = srcLineStride * 9;
    int dstLineStride4 = dstLineStride * 4;

    uint8_t* srcLine = (uint8_t*) pSrcBitmapPixels;
    uint8_t* src = srcLine;
    uint8_t* srcNextLine = srcLine + srcLineStride;
    uint8_t* srcEnd = srcLine + srcLineStride * srcBitmapInfo.height;
    uint8_t* dstLine = (uint8_t*) pDstBitmapPixels;
    uint8_t* dst = dstLine;
    while(srcLine < srcEnd) {
        while (src < srcNextLine) {
            {
                uint8_t* srcLine2 = src + srcLineStride;
                uint8_t* srcLine3 = srcLine2 + srcLineStride;
                uint8_t* srcLine4 = srcLine3 + srcLineStride;
                uint8_t* srcLine5 = srcLine4 + srcLineStride;
                uint8_t* srcLine6 = srcLine5 + srcLineStride;
                uint8_t* srcLine7 = srcLine6 + srcLineStride;
                uint8_t* srcLine8 = srcLine7 + srcLineStride;
                uint8_t* srcLine9 = srcLine8 + srcLineStride;

                uint8_t* dstLine2 = dst + dstLineStride;
                uint8_t* dstLine3 = dstLine2 + dstLineStride;
                uint8_t* dstLine4 = dstLine3 + dstLineStride;

                // Magic table
                RESIZE_225_CELL(dst,      0, src, srcLine2, srcLine3,     0, << 5, << 5, << 3, << 5, << 5, << 3, << 3, << 3, << 1 )
                RESIZE_225_CELL(dst,      4, src, srcLine2, srcLine3,     8, * 24, << 5, << 4, * 24, << 5, << 4, * 6, << 3, << 2)
                RESIZE_225_CELL(dst,      8, src, srcLine2, srcLine3,     16, << 4, << 5, * 24, << 4, << 5, * 24, << 2, << 3, * 6)
                RESIZE_225_CELL(dst,      12, src, srcLine2, srcLine3,     24, << 3, << 5, << 5, << 3, << 5, << 5, << 1 , << 3, <<3)

                RESIZE_225_CELL(dstLine2, 0, srcLine3, srcLine4, srcLine5, 0, * 24, * 24, * 6, << 5, << 5, << 3, << 4, << 4, << 2)
                RESIZE_225_CELL(dstLine2, 4, srcLine3, srcLine4, srcLine5, 8, * 18, * 24, * 12, * 24, << 5, << 4, * 12, << 4, << 3)
                RESIZE_225_CELL(dstLine2, 8, srcLine3, srcLine4, srcLine5,16, * 12, * 24, * 18, << 4, << 5, * 24, << 3, << 4, * 12)
                RESIZE_225_CELL(dstLine2, 12, srcLine3, srcLine4, srcLine5,24, * 6, * 24, * 24, << 3, << 5, << 5, << 2, << 4, << 4)

                RESIZE_225_CELL(dstLine3, 0, srcLine5, srcLine6, srcLine7, 0, << 4, << 4, << 2, << 5, << 5, << 3, * 24, * 24, * 6)
                RESIZE_225_CELL(dstLine3, 4, srcLine5, srcLine6, srcLine7, 8, * 12,  << 4, << 3, * 24, << 5, << 4, * 18, * 24, * 12)
                RESIZE_225_CELL(dstLine3, 8, srcLine5, srcLine6, srcLine7,16, << 3, << 4, * 12, << 4, << 5, * 24, * 12, * 24, * 18)
                RESIZE_225_CELL(dstLine3, 12, srcLine5, srcLine6, srcLine7,24, << 2, << 4, << 4, << 3, << 5, << 5, * 6, * 24, * 24)

                RESIZE_225_CELL(dstLine4, 0, srcLine7, srcLine8, srcLine9, 0, << 3, << 3, << 1 , << 5, << 5, << 3, << 5, << 5, << 3)
                RESIZE_225_CELL(dstLine4, 4, srcLine7, srcLine8, srcLine9, 8, * 6,  << 3, << 2, * 24, << 5, << 4, * 24, << 5, << 4)
                RESIZE_225_CELL(dstLine4, 8, srcLine7, srcLine8, srcLine9,16, << 2, << 3, * 6, << 4, << 5, * 24, << 4, << 5, * 24)
                RESIZE_225_CELL(dstLine4, 12, srcLine7, srcLine8, srcLine9,24,  << 1 , << 3, << 3, << 3, << 5, << 5, << 3, << 5, << 5)
            }
            src += 4 * 9;
            dst += 4 * 4;
        }
        srcLine += srcLineStride9;
        src = srcLine;
        srcNextLine = srcLine + srcLineStride;
        dstLine += dstLineStride4;
        dst = dstLine;
    }
    bool ret1 = unlockBitmapBits(env, srcBitmap);
    bool ret2 = unlockBitmapBits(env, dstBitmap);
    return ret1 && ret2;
}
