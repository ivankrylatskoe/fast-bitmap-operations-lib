package com.ivankrylatskoe.test_fast_bitmap_rotation_lib.rotators;

import android.graphics.Bitmap;
import com.jni.fast_bitmap_rotation.JniFastBitmapRotationLib;

public class FastBitmapRotationLibRotator implements IRotator {

    public Bitmap rotate180(Bitmap bitmap) {
        try {
            JniFastBitmapRotationLib.rotateBitmap180(bitmap);
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }
}
