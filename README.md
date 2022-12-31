# Fast Bitmap Operations Library for Android
This is a collection of fast operations with images optimized for the maximum performance on Android.
The alorithms are written in C++ and available using JNI.


## Algorithms
### 1. 180 degrees inplace rotation

#### Performance (approximately)
Test were performed on an image with dimensions 1920x1080.
Average methods' times (based on 10 test runs):
* Fast Operations Library: 2.2-2.6 ms (the fastest method)
* OpenCV: 8.9-9.6 ms (3.5-4.3 times slower than Fast Operations Library)
* Matrix method: 23-27 ms (9.9-12.4 times slower than Fast Operations Library)

### 2. Scaling down by factor of 2.25 using INTER_AREA interpolation
#### Performance (approximately)
Test were performed on an image with dimensions 1440x1080.
Average methods' times (based on 10 test runs):
* Fast Operations Library: 6.4-7.0 ms (the fastest method)
* OpenCV: 42-51 ms (6.1-7.8 times slower than Fast Operations Library)


## How to run the application and check performance
Clone the project, open it in Android Studio and run.
Wait approximately 20 seconds and the results will be shown on the screen.
The tests will run cyclically.

## How to use the library in your project
* Add the following line: `include ':fast_bitmap_operations_lib'` to `settings.graddle` file.
* Add the following line: `implementation project(':fast_bitmap_operations_lib')` to `dependencies` section of application `build.gradle` file.

## Acknowledgments
* [riwnodennyk](https://stackoverflow.com/users/986216/riwnodennyk "riwnodennyk") - idea and different rotation implementations.
* A [discussion](https://stackoverflow.com/a/29734593/1707617) on different rotation methods.
* GitHub [repository](https://github.com/riwnodennyk/ImageRotation) with different rotation methods.
* [Ndk](https://developer.android.com/ndk) - a toolset that lets you implement parts of your app in native code, using languages such as C and C++.
* [Description](https://stackoverflow.com/questions/72251642/super-fast-bitmap-180-degrees-rotation-using-opencv-on-android) of OpenCV implementation.
