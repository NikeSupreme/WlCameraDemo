package com.wulian.wlcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.wulian.wlcamera.customview.material.MaterialEditText;
import com.wulian.wlcamera.device.LookeverDetailActivity;

/**
 * created by huxc  on 2018/8/22.
 * func：登录界面
 * email: hxc242313@qq.com
 */

public class LoginActivity extends BaseFullscreenActivity implements View.OnClickListener {


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private MaterialEditText etUserId;
    private MaterialEditText etPassword;
    private MaterialEditText etDeviceId;
    private MaterialEditText etDeviceDomain;
    private MaterialEditText etUserDomain;
    private Button btnLogin;

    private String deviceId = "cmic078e50294d415774";
    private String deviceDomain = "shsp.wuliangroup.cn";
    private String userDomain = "shsp.wuliangroup.cn";
    private String userId = "us954413g6l5v6rbtbsk";
    private String userPassword = "fe0d6b67c2d4ddf9";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
    }

    @Override
    protected void initView() {
        etUserId = (MaterialEditText) findViewById(R.id.et_userId);
        etPassword = (MaterialEditText) findViewById(R.id.et_password);
        etDeviceId = (MaterialEditText) findViewById(R.id.et_deviceId);
        etDeviceDomain = (MaterialEditText) findViewById(R.id.et_deviceDomain);
        etUserDomain = (MaterialEditText) findViewById(R.id.et_userDomain);
        btnLogin = (Button) findViewById(R.id.login);
    }

    @Override
    protected void initData() {
        checkPermission();
    }

    @Override
    protected void initListeners() {
        btnLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                login();
                break;
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
        }
    }

    private void login() {
        userId = etUserId.getText().toString();
        userPassword = etPassword.getText().toString();
        deviceId = etDeviceId.getText().toString();
        deviceDomain = etDeviceDomain.getText().toString();
        userDomain = etUserDomain.getText().toString();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(deviceDomain)
                || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(userPassword)
                || TextUtils.isEmpty(userDomain)) {
            return;
        } else {
            LookeverDetailActivity.start(LoginActivity.this, deviceId, userPassword, userId, deviceDomain, userDomain);
        }
    }
}
