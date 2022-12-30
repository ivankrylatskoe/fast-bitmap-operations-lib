package com.jni.fast_bitmap_operations;

import android.graphics.Bitmap;

public class JniFastBitmapOperationsLib {
    static {
        System.loadLibrary("JniFastBitmapOperationsLib");
    }

    public static void rotateBitmap180(Bitmap bitmap) throws Exception {
        if (!jniRotateBitmap180(bitmap)) {
            throw new Exception("Failed to rotate bitmap");
        }
    }

    // Resizes image down by 225 percent using area interpolation
    public static void resizeDownBitmap225InterArea(Bitmap srcBitmap, Bitmap dstBitmap) throws Exception {
        if (!jniResizeDownBitmap225InterArea(srcBitmap, dstBitmap)) {
            throw new Exception("Failed to rotate bitmap");
        }
    }

    private native static boolean jniRotateBitmap180(Bitmap bitmap);

    private native static boolean jniResizeDownBitmap225InterArea(Bitmap srcBitmap, Bitmap dstBitmap);
}
