package com.aml.ocr.paddle.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommonUtils {

    private static final String TAG = "COMMON_TAG";

    public static void copyAssets(AssetManager assetManager, String assetName, String destPath, boolean isFile){
        File destFile = new File(destPath);
        if (!isFile && !destFile.exists()){
            boolean isMk = destFile.mkdirs();
            if (!isMk){
                return;
            }
        }
        if (isFile){
            InputStream is = null;
            OutputStream out = null;
            try {
//            assetManager.list(assetName);
                is = assetManager.open(assetName);
                out = new FileOutputStream(destPath);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // file does not exist
                Log.e(TAG, "file does not exist");
                return;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }else {
            try {
                String[] files = assetManager.list(assetName);
                if (files != null){
                    for (String fileName : files) {
                        String assetChildName = assetName + "/" + fileName;
                        String destChildName = destPath + "/" + fileName;
                        InputStream is = null;
                        OutputStream out = null;
                        try {
//            assetManager.list(assetName);
                            is = assetManager.open(assetChildName);
                            out = new FileOutputStream(destChildName);
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = is.read(buffer)) != -1) {
                                out.write(buffer, 0, read);
                            }
                        } catch (IOException ex) {
                            // file does not exist
                            Log.e(TAG, "file does not exist");
                            return;
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                }
                            }
                            if (out != null) {
                                try {
                                    out.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "");
            }
        }




        // check whether file on SD card
        /*File fileInSD = new File(ctx.getExternalFilesDir(null), nnFileName);

        if (fileInSD.exists()) {
            Log.d("debug===", "NN model on SD card " + fileInSD);
            return;
        }
        // copy file to app
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(nnFileName);
            File outFile = new File(ctx.getExternalFilesDir(null), nnFileName);
            out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + nnFileName, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }*/
    }

}
