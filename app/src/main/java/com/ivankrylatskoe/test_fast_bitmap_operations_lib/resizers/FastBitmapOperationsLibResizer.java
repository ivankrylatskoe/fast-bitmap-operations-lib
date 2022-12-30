package com.ivankrylatskoe.test_fast_bitmap_operations_lib.resizers;

import android.graphics.Bitmap;

import com.jni.fast_bitmap_operations.JniFastBitmapOperationsLib;

public class FastBitmapOperationsLibResizer implements IResizer {
    public void resize225(Bitmap srcBitmap, Bitmap dstBitmap){
        try {
            JniFastBitmapOperationsLib.resizeDownBitmap225InterArea(srcBitmap, dstBitmap);
        }
        catch (Exception e) {
            System.exit(0);
        }
    }
}
