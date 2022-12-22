package com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class MatrixRotator implements IRotator {

    public Bitmap rotate180(Bitmap bitmap) {
        Matrix rotMatrix = new Matrix();
        rotMatrix.postRotate(180);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotMatrix, true);
    }
}
