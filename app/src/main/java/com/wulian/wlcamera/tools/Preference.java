package com.wulian.wlcamera.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.wulian.wlcamera.MainApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * 系统参数
 */
public class Preference {
    public static final String ENTER_TYPE_ACCOUNT = "account";
    public static final String ENTER_TYPE_GW = "gateway";
    public static final String ISADMIN_TRUE = "1";
    public static final String ISADMIN_FALSE = "0";
    /**
     * 默认皮肤ID，根据版本需要进行调整
     */
    public static final String DEFAULT_SKIN_ID = "001";

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final Editor mEditor;

    private static Preference mInstance;

    public static Preference getPreferences() {
        if (mInstance == null)
            mInstance = new Preference();
        return mInstance;
    }

    private Preference() {
        mContext = MainApplication.getApplication();
        mPreferences = mContext.getSharedPreferences(
                IPreferenceKey.P_KEY_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }
    /**
     * 是否第一次进入摄像机全屏
     */
    public void setIsFrisCameraFullScreen(boolean flag) {
        mEditor.putBoolean(IPreferenceKey.P_KEY_ISFRIST_CAMERA_FULL_SCREEN, flag)
                .commit();
    }

    public boolean getIsFrisCameraFullScreen() {
        return mPreferences.getBoolean(IPreferenceKey.P_KEY_ISFRIST_CAMERA_FULL_SCREEN, true);
    }

    /**
     * currentSipSuid
     */
    public void saveCurrentSipSuid(String value) {
        mEditor.putString(IPreferenceKey.P_KEY_SIP_SUID, value)
                .commit();
    }

    public String getCurrentSipSuid() {
        return mPreferences.getString(IPreferenceKey.P_KEY_SIP_SUID, "");
    }

}