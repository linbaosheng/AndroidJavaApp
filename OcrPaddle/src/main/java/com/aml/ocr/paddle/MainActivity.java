package com.aml.ocr.paddle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aml.ocr.paddle.jni.Native;
import com.aml.ocr.paddle.utils.CommonUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main_tag";

    private Button btnPermission;
    private Button btnCopy;
    private Button btnTest;

    private String modelPath;

    private String keyPath;

    private String imgPath;
    private String configPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
    }

    private void initData() {
        modelPath = getFilesDir() + "/models";
        keyPath = getFilesDir() + "/labels";
        imgPath = getFilesDir() + "/images";
        configPath = getFilesDir()+"/config.txt";
        Log.i(TAG, "configPath:" + configPath);
    }

    private void initListener() {
        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionEvent();
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "modelPath:" + modelPath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.copyAssets(getAssets(), "models", modelPath, false);
                        CommonUtils.copyAssets(getAssets(), "labels", keyPath, false);
                        CommonUtils.copyAssets(getAssets(), "images", imgPath, false);
                        CommonUtils.copyAssets(getAssets(), "config.txt", configPath, true);
                    }
                }).start();
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testEvent();
            }
        });
    }

    private void permissionEvent() {
        if (!checkAllPermissions()){
            requestAllPermissions();
        }else{
            Toast.makeText(getApplicationContext(), "已获取权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void testEvent() {
        Native.paddleOcrTest(modelPath, imgPath + "/test.png", keyPath + "/ppocr_keys_v1.txt", configPath);
    }

    private void initView() {
        btnPermission = findViewById(R.id.btn_permission);
        btnTest = findViewById(R.id.btn_test);
        btnCopy = findViewById(R.id.btn_copy);
    }

    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }

    private boolean checkAllPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}