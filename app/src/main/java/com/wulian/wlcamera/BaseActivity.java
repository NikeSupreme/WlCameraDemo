package com.wulian.wlcamera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wulian.wlcamera.customview.ProgressDialogManager;
import com.wulian.wlcamera.tools.Preference;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;


/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected MainApplication mainApplication = MainApplication.getApplication();
    protected ProgressDialogManager progressDialogManager = ProgressDialogManager.getDialogManager();
    protected Preference preference = Preference.getPreferences();
    private SwipeBackActivityHelper swipeBackActivityHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WLog.i("BaseActivity",TAG+" onCreate");
        if (enableSwipeBack()) {
            swipeBackActivityHelper = new SwipeBackActivityHelper(this);
            swipeBackActivityHelper.onActivityCreate();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (enableSwipeBack()) {
            swipeBackActivityHelper.onPostCreate();
        }
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && swipeBackActivityHelper != null)
            return swipeBackActivityHelper.findViewById(id);
        return v;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackActivityHelper == null ? null : swipeBackActivityHelper.getSwipeBackLayout();
    }

    /**
     * 是否开启滑动退出功能。默认开启，重写该方法返回false来关闭功能
     *
     * @return
     */
    public boolean enableSwipeBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
