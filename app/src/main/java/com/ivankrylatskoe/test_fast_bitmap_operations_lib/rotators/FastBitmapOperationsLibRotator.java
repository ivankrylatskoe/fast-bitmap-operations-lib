package com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators;

import android.graphics.Bitmap;
import com.jni.fast_bitmap_operations.JniFastBitmapOperationsLib;

public class FastBitmapOperationsLibRotator implements IRotator {

    public Bitmap rotate180(Bitmap bitmap) {
        try {
            JniFastBitmapOperationsLib.rotateBitmap180(bitmap);
            return bitmap;
        }
        catch (Exception e) {
            return null;
        }
    }
}
