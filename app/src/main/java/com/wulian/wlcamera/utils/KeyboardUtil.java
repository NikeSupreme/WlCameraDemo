package com.wulian.wlcamera.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by 王伟 on 2017/3/16
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 键盘工具类
 */
public class KeyboardUtil {

    /**
     * 强制开启键盘
     */
    public static void showKeyboard(Context context) {
        InputMethodManager imm2 = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制隐藏键盘
     */
    public static void hideKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
