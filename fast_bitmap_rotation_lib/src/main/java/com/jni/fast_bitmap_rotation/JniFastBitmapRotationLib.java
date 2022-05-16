package com.jni.fast_bitmap_rotation;

import android.graphics.Bitmap;

public class JniFastBitmapRotationLib {
    static {
        System.loadLibrary("JniFastBitmapRotationLib");
    }

    public static void rotateBitmap180(Bitmap bitmap) throws Exception {
        if (!jniRotateBitmap180(bitmap)) {
            throw new Exception("Failed to rotate bitmap");
        }
    }

    private native static boolean jniRotateBitmap180(Bitmap bitmap);
}
