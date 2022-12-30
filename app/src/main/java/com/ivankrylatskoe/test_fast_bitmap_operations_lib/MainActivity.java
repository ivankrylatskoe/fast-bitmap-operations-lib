package com.ivankrylatskoe.test_fast_bitmap_operations_lib;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import com.ivankrylatskoe.test_fast_bitmap_operations_lib.resizers.FastBitmapOperationsLibResizer;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.resizers.IResizer;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.resizers.OpenCVResizer;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.FastBitmapOperationsLibRotator;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.IRotator;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.MatrixRotator;
import com.ivankrylatskoe.test_fast_bitmap_operations_lib.rotators.OpenCVRotator;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    static {
        if (!OpenCVLoader.initDebug()) {
            System.exit(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView placeholder = (TextView) findViewById(R.id.textView);
        runTests(placeholder);
    }

    /*
        Measures operations time and checks their correctness.
        Fills textView with results.
     */
    private void runTests(final TextView textView) {
        new Thread(new Runnable() {
            public void run() {
                while(true) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(String.format("Checking rotation...\n"));
                        }
                    });

                    final long fastBitmapOperationsLibRotationTime = estimateRotatorTime(new FastBitmapOperationsLibRotator());
                    final long matrixRotationTime = estimateRotatorTime(new MatrixRotator());
                    final long openCVRotationTime = estimateRotatorTime(new OpenCVRotator());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(textView.getText() + String.format(
                                    "Matrix rotation time: %d\n" +
                                            "OpenCV rotation time time: %d\n" +
                                            "Fast bitmap operations lib rotation time: %d\n",
                                    matrixRotationTime, openCVRotationTime, fastBitmapOperationsLibRotationTime));
                        }
                    });

                    final boolean rotationSuccess = checkRotationCorrectness();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(textView.getText() + String.format("Results match: %b\n" +
                                    "\nChecking resize...\n", rotationSuccess));
                        }
                    });


                    final long fastBitmapOperationsLibResizeTime = estimateResizerTime(new FastBitmapOperationsLibResizer());
                    final long openCVResizeTime = estimateResizerTime(new OpenCVResizer());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(textView.getText() + String.format(
                                    "OpenCV resize time: %d\n" +
                                            "Fast bitmap operations lib resize time: %d\n",
                                    openCVResizeTime, fastBitmapOperationsLibResizeTime));
                        }
                    });

                    final boolean resizeSuccess = checkResizeCorrectness();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(textView.getText() + String.format("Results match: %b\n", resizeSuccess));
                        }
                    });

                    // Wait 7 sec
                    try {
                        Thread.sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /*
        Returns number of milliseconds required to perform 100 iterations of rotation of test image with given rotator.
     */
    private long estimateRotatorTime(IRotator rotator) {
        Bitmap testImg = loadBitmapFromAssets("test_img_1920.png");
        long startTime = System.currentTimeMillis();
        for(int n = 0; n < 100; n++) {
            testImg = rotator.rotate180(testImg);
        }
        return System.currentTimeMillis() - startTime;
    }

    /*
     Returns number of milliseconds required to perform 100 iterations of resizing of test image with given resizer.
     */
    private long estimateResizerTime(IResizer resizer) {
        long duration = 0;
        for(int n = 0; n < 100; n++) {
            Bitmap testImg = loadBitmapFromAssets("test_img_1440.png");
            Bitmap resizedBitmap = Bitmap.createBitmap(testImg.getWidth()/9*4, testImg.getHeight()/9*4, Bitmap.Config.ARGB_8888);
            long startTime = System.currentTimeMillis();
            resizer.resize225(testImg, resizedBitmap);
            duration += System.currentTimeMillis() - startTime;
        }
        return duration;
    }

    /*
     Loads bitmap from assets.
     */
    private Bitmap loadBitmapFromAssets(String filename) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }


    /*
     Checks if the results of rotation of three different methods are same
     */
    private boolean checkRotationCorrectness() {
        // Get bitmaps
        Bitmap testImgFast = loadBitmapFromAssets("test_img_1920.png");
        Bitmap testImgMatrix = loadBitmapFromAssets("test_img_1920.png");
        Bitmap testImgOpenCV = loadBitmapFromAssets("test_img_1920.png");

        // Rotate bitmaps
        testImgMatrix = (new MatrixRotator()).rotate180(testImgMatrix);
        testImgFast = (new FastBitmapOperationsLibRotator()).rotate180(testImgFast);
        testImgOpenCV = (new OpenCVRotator()).rotate180(testImgOpenCV);

        // Compare bitmaps
        if (!compareBitmaps(testImgFast, testImgMatrix))
            return false;
        if (!compareBitmaps(testImgFast, testImgOpenCV))
            return false;

        return true;
    }


    /*
     Checks if the results of resize of two different methods are same
     */
    private boolean checkResizeCorrectness() {
        // Get bitmaps
        Bitmap testImgFast = loadBitmapFromAssets("test_img_1440.png");
        Bitmap testImgOpenCV = loadBitmapFromAssets("test_img_1440.png");

        // Create output images
        Bitmap testImgFastResized = Bitmap.createBitmap(testImgFast.getWidth()/9*4, testImgFast.getHeight()/9*4, Bitmap.Config.ARGB_8888);
        Bitmap testImgOpenCVResized = Bitmap.createBitmap(testImgOpenCV.getWidth()/9*4, testImgOpenCV.getHeight()/9*4, Bitmap.Config.ARGB_8888);

        // Resize bitmaps
        (new FastBitmapOperationsLibResizer()).resize225(testImgFast, testImgFastResized);
        (new OpenCVResizer()).resize225(testImgOpenCV, testImgOpenCVResized);

        // Compare bitmaps
        if (!compareBitmaps(testImgFastResized, testImgOpenCVResized))
            return false;

        return true;
    }

    /*
     Returns true if two bitmaps are same. This means that there sizes are same and corresponding pixel colors are same.
     */
    boolean compareBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        return bitmap1.sameAs(bitmap2);
    }

}
