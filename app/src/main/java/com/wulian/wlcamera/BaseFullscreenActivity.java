package com.wulian.wlcamera;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wulian.wlcamera.utils.DisplayUtil;

import java.lang.reflect.Field;


/**
 * Created by zbl on 2017/10/18.
 * 全屏Activity基类，适用于需要全屏并且沉浸式显示的界面
 */

public abstract class BaseFullscreenActivity extends BaseActivity {
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {}
        }
        super.setContentView(layoutResID);
        initView();
        initData();
        initListeners();
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //状态栏高度调整
            int statusBarHeight = 0;
            //获取status_bar_height资源的ID
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            } else {
                statusBarHeight = DisplayUtil.dip2Pix(MainApplication.getApplication(), 20);
            }
            View view = getFullscreenPaddingLayout();
            if (view == null) {
                view = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            }
            view.setPadding(0, statusBarHeight, 0, 0);

            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    protected abstract void initView();

    protected abstract void initData();


    protected abstract void initListeners();

    /**
     * 获取沉浸式状态栏下需要修改padding的布局元素，用来设置padding状态栏高度 <p/>
     * 可以不重写，默认使用根布局。重写可以自行指定布局元素
     */
    protected View getFullscreenPaddingLayout() {
        return null;
    }
}
