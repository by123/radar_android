package com.brotherhood.o2o.explore.helper;


public class ImageBlur{

    public static native void blurIntArray(int[] pImg, int w, int h, int r);

    static {
        System.loadLibrary("JNI_ImageBlur");
    }
}
