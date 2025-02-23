package com.aml.ocr.paddle.jni;

public class Native {
    static {
        System.loadLibrary("paddle");
    }

    public static native int paddleOcrTest(String modelPath, String imgPath, String keyPath, String configPath);
}
