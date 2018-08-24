package com.wulian.wlcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wulian.wlcamera.customview.material.MaterialEditText;
import com.wulian.wlcamera.device.LookeverDetailActivity;
import com.wulian.wlcamera.device.PenguinDetailActivity;

import rtcv2.CameraStepsActivity;
import rtcv2.PlayVideoActivity;

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
    private Button btnUiLogin;
    private Button btnCommonLogin;
    private RadioGroup radioGroup;
    private RadioButton leftBtn, rightBtn;
    private ImageView ivLogout;

    private String type = "CMICA2";
    private String deviceId;//随便看"cmic078e50294d415774" 企鹅"cmic20b350294d000440"
    private String deviceDomain ;
    private String userDomain ;
    private String userId ;
    private String userPassword ;

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
        btnUiLogin = (Button) findViewById(R.id.btn_ui_login);
        ivLogout = (ImageView) findViewById(R.id.imageView_finish);
        btnCommonLogin = (Button) findViewById(R.id.btn_common_login);
        radioGroup = (RadioGroup) findViewById(R.id.rg_type);
        leftBtn = (RadioButton) findViewById(R.id.rb_tab_lookever);
        rightBtn = (RadioButton) findViewById(R.id.rb_tab_penguin);
    }

    @Override
    protected void initData() {
        checkPermission();
    }

    @Override
    protected void initListeners() {
        btnUiLogin.setOnClickListener(this);
        btnCommonLogin.setOnClickListener(this);
        ivLogout.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                leftBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                rightBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                switch (checkedId) {
                    case R.id.rb_tab_lookever:
                        leftBtn.setTextColor(getResources().getColor(R.color.white));
                        type = leftBtn.getTag().toString();
                        break;
                    case R.id.rb_tab_penguin:
                        rightBtn.setTextColor(getResources().getColor(R.color.white));
                        type = rightBtn.getTag().toString();
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ui_login:
                loginUIDemo();
                break;
            case R.id.btn_common_login:
                loginCommonDemo();
                break;
            case R.id.imageView_finish:
                finish();
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

    private void loginUIDemo() {
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
            if (TextUtils.equals(type, "CMICA2")) {
                LookeverDetailActivity.start(LoginActivity.this, deviceId, userPassword, userId, deviceDomain, userDomain);
            } else if (TextUtils.equals(type, "CMICA3")) {
                PenguinDetailActivity.start(LoginActivity.this, deviceId, userPassword, userId, deviceDomain, userDomain);
            }
        }
    }

    private void loginCommonDemo(){
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
                CameraStepsActivity.start(LoginActivity.this, deviceId, userPassword, userId, deviceDomain, userDomain);
        }
    }
}
