LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := JniFastBitmapOperationsLib
LOCAL_SRC_FILES := JniFastBitmapOperationsLib.cpp
LOCAL_LDLIBS := -llog
LOCAL_LDFLAGS += -ljnigraphics

include $(BUILD_SHARED_LIBRARY)
APP_OPTIM := release
LOCAL_CFLAGS := -g -O3

#if you need to add more module, do the same as the one we started with (the one with the CLEAR_VARS)
