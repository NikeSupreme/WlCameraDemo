package com.wulian.wlcamera.utils;

import android.content.Context;
import android.os.Vibrator;

import com.wulian.wlcamera.MainApplication;


/**
 * Created by zbl on 2018/4/4.
 */

public class VibratorUtil {
    private static final long[] notificationPattern = {100, 400, 100, 400};   // 停止 开启 停止 开启

    public static void notificationVibration() {

        Vibrator vibrator = (Vibrator) MainApplication.getApplication().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(notificationPattern, -1);
    }

    public static void holdSpeakVibration() {
        Vibrator vibrator = (Vibrator) MainApplication.getApplication().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }
}
