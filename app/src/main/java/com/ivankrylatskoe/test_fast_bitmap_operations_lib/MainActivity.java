package com.ivankrylatskoe.test_fast_bitmap_operations_lib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.FastBitmapOperationsLibRotator;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.IRotator;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.MatrixRotator;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView placeholder = (TextView) findViewById(R.id.textView);
        testRotation(placeholder);
    }

    /*
        Measures rotation execution time for two rotators.
        Checks correctness of rotations.
        Fills textView with results.
     */
    private void testRotation(final TextView textView) {
        new Thread(new Runnable() {
            public void run() {
                final long fastBitmapRotationLibTime = estimateRotatorTime(new FastBitmapOperationsLibRotator());
                final long matrixRotationTime = estimateRotatorTime(new MatrixRotator());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format("Matrix rotation time: %d\n"+
                                "Fast bitmap rotation lib time: %d\n"+
                                "Comparing results...", matrixRotationTime, fastBitmapRotationLibTime));
                    }
                });

                final boolean success = checkRotationCorrectness();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format("Matrix rotation time: %d\n"+
                            "Fast bitmap rotation lib time: %d\n"+
                            "Results match: %b", matrixRotationTime, fastBitmapRotationLibTime, success));
                    }
                });
            }
        }).start();
    }

    /*
        Returns number of milliseconds required to perform 100 iterations of rotation of test image with given rotator.
     */
    private long estimateRotatorTime(IRotator rotator) {
        Bitmap testImg = BitmapFactory.decodeResource(getResources(), R.drawable.test_img);
        long startTime = System.currentTimeMillis();
        for(int n = 0; n < 100; n++) {
            testImg = rotator.rotate180(testImg);
        }
        return System.currentTimeMillis() - startTime;
    }

    /*
        Checks if the results of rotation of two different methods are same
     */
    private boolean checkRotationCorrectness() {
        // Get bitmaps
        Bitmap testImgFast = BitmapFactory.decodeResource(getResources(), R.drawable.test_img);
        Bitmap testImgMatrix = BitmapFactory.decodeResource(getResources(), R.drawable.test_img);

        // Rotate bitmaps
        testImgMatrix = (new MatrixRotator()).rotate180(testImgMatrix);
        testImgFast = (new FastBitmapOperationsLibRotator()).rotate180(testImgFast);

        if (testImgMatrix.getWidth() != testImgFast.getWidth() || testImgMatrix.getHeight() != testImgFast.getHeight())
            return false;

        for (int y = 0; y < testImgMatrix.getHeight(); y++) {
            for (int x = 0; x < testImgMatrix.getWidth(); x++) {
                if (testImgMatrix.getPixel(x, y) != testImgFast.getPixel(x, y))
                    return false;
            }
        }
        return true;
    }

}
