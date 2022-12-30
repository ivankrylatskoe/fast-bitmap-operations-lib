package com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class OpenCVRotator implements IRotator {
    public Bitmap rotate180(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Core.rotate(mat, mat, Core.ROTATE_180);
        Utils.matToBitmap(mat, bitmap);
        mat.release();
        return bitmap;
    }
}
