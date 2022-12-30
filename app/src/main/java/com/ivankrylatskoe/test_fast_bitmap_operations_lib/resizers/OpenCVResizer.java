package com.ivankrylatskoe.test_fast_bitmap_operations_lib.resizers;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVResizer implements IResizer {
    public void resize225(Bitmap srcBitmap, Bitmap dstBitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(srcBitmap, mat);
        Mat resized = new Mat();
        int resizedWidth = srcBitmap.getWidth()/9*4;
        int resizedHeight = srcBitmap.getHeight()/9*4;
        org.opencv.core.Size sz = new org.opencv.core.Size(resizedWidth, resizedHeight);
        Imgproc.resize(mat, resized, sz, 0, 0, Imgproc.INTER_AREA);
        Utils.matToBitmap(resized, dstBitmap);
        mat.release();
        resized.release();
    }
}
