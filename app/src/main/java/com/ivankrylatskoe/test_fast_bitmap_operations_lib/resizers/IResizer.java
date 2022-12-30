package com.ivankrylatskoe.test_fast_bitmap_operations_lib.resizers;

import android.graphics.Bitmap;

public interface IResizer {
    // Resizes down a bitmap by 225 percent (9/4)
    void resize225(Bitmap srcBitmap, Bitmap dstBitmap);
}
