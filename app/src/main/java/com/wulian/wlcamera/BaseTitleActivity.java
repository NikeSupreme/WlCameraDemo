package com.wulian.wlcamera;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.wlcamera.tools.TimeLock;
import com.wulian.wlcamera.tools.ToolBarHelper;
import com.wulian.wlcamera.utils.DisplayUtil;
import com.wulian.wlcamera.utils.ViewUtils;

import java.lang.reflect.Field;


/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class BaseTitleActivity extends BaseActivity implements View.OnClickListener {

    public ToolBarHelper getmToolBarHelper() {
        return mToolBarHelper;
    }

    protected ToolBarHelper mToolBarHelper;
    private Toolbar toolbar;

    protected TextView mTitle;
    protected Button btn_left, btn_right;
    protected ImageView img_left, img_right;

    private TimeLock timeLock = new TimeLock();

    protected void initTitle() {
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void initListeners() {

    }


    public void setContentView(int layoutResID, boolean isHasTitle) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {
            }
        }
        //透明状态栏和获取状态栏高度
        int statusBarHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //状态栏高度调整
            {
                //获取status_bar_height资源的ID
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //根据资源ID获取响应的尺寸值
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                } else {
                    statusBarHeight = DisplayUtil.dip2Pix(MainApplication.getApplication(), 20);
                }
            }
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
        mToolBarHelper = new ToolBarHelper(this, layoutResID, statusBarHeight);
        setContentView(layoutResID);
    }

    @Override
    public void setContentView(int layoutResID) {
        toolbar = mToolBarHelper.getToolBar();
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        btn_left = (Button) toolbar.findViewById(R.id.btn_left);
        btn_right = (Button) toolbar.findViewById(R.id.btn_right);
        img_left = (ImageView) toolbar.findViewById(R.id.img_left);
        img_right = (ImageView) toolbar.findViewById(R.id.img_right);
        RelativeLayout rl_toolbar = (RelativeLayout) toolbar.findViewById(R.id.rl_toolbar);
        img_left.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);//昵称页面直接onClick重载
        img_right.setOnClickListener(this);
        setContentView(mToolBarHelper.getContentView()); /*把 toolbar 设置到Activity 中*/
        setSupportActionBar(toolbar); /*自定义的一些操作*/
        onCreateCustomToolBar(toolbar);
        initTitle();
        initView();
        initData();
        initListeners();
    }

    protected <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    public void setToolbarBackground(float alpha, int colorResId) {
        toolbar.setAlpha(alpha);
        toolbar.setBackgroundColor(colorResId);
    }

    public void setToolBarTitle(String title) {//标准模式下 只有标题和返回键
        mTitle.setText(title);
        img_left.setVisibility(View.VISIBLE);
        btn_left.setVisibility(View.GONE);
        btn_right.setVisibility(View.GONE);
        img_right.setVisibility(View.GONE);
    }

    public void setToolBarTitle(@StringRes int title) {//标准模式下 只有标题和返回键
        mTitle.setText(title);
        img_left.setVisibility(View.VISIBLE);
        btn_left.setVisibility(View.GONE);
        btn_right.setVisibility(View.GONE);
        img_right.setVisibility(View.GONE);
    }

    public void setToolBarOnlyTitle(String title) {//只有标题
        mTitle.setText(title);
        img_left.setVisibility(View.GONE);
        btn_left.setVisibility(View.GONE);
        btn_right.setVisibility(View.GONE);
        img_right.setVisibility(View.GONE);
    }

    public void setToolBarTitleAndRightImg(String title, int rightImgResId) {//左返回  中标题   右img
        mTitle.setText(title);
        btn_left.setVisibility(View.GONE);
        img_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.GONE);
        img_right.setVisibility(View.VISIBLE);
        img_right.setImageResource(rightImgResId);
    }

    public void setToolBarTitleAndRightImg(@StringRes int title, int rightImgResId) {//左返回  中标题   右img
        mTitle.setText(title);
        btn_left.setVisibility(View.GONE);
        img_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.GONE);
        img_right.setVisibility(View.VISIBLE);
        img_right.setImageResource(rightImgResId);
    }


    public void setToolBarTitleAndRightBtn(String title, String rightBtn) {//左返回 中标题 右button
        mTitle.setText(title);
        btn_left.setVisibility(View.GONE);
        img_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setText(rightBtn);
        btn_right.setTextSize(ViewUtils.sp2px(16, 1));
        img_right.setVisibility(View.GONE);
    }

    public void setToolBarTitleAndRightBtn(int title, int rightBtn) {//左返回 中标题 右button
        mTitle.setText(title);
        btn_left.setVisibility(View.GONE);
        img_left.setVisibility(View.VISIBLE);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setText(rightBtn);
        btn_right.setTextSize(ViewUtils.sp2px(16, 1));
        img_right.setVisibility(View.GONE);
    }

    public void setToolBarLeftAndRightText(String leftText, String rightText) {
        mTitle.setVisibility(View.GONE);
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setText(leftText);
        btn_left.setTextSize(ViewUtils.sp2px(16, 1));
        img_left.setVisibility(View.GONE);
        btn_right.setVisibility(View.VISIBLE);
        btn_right.setText(rightText);
        btn_right.setTextSize(ViewUtils.sp2px(16, 1));
        img_right.setVisibility(View.GONE);
    }


    //右边图片
    public ImageView getImgRight() {
        return img_right;
    }

    //左边图片
    public ImageView getImgLeft() {
        return img_left;
    }

    //右边按钮
    public Button getBtn_right() {
        return btn_right;
    }

    //设置标题文字
    public void setTitleText(String text) {
        if (mTitle != null) {
            mTitle.setText(text);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置标题字体的颜色
     */
    public void setTitleTextBackground(int drawResId, int color) {
        img_left.setImageResource(drawResId);
        mTitle.setTextColor(color);
    }

    /**
     * 设置标题字体的颜色
     */
    public void setTitleTextBackground(int drawResId, int color, int rightResId) {
        img_left.setImageResource(drawResId);
        mTitle.setTextColor(color);
        btn_right.setTextColor(rightResId);
    }

    /**
     * 设置标题字体的颜色
     */
    public void setTitleTextColor(int colorResId) {
        mTitle.setTextColor(colorResId);
    }

    /**
     * 设置标题字体的颜色
     */
    public void setRightBtnTextColor(int colorResId) {
        btn_right.setTextColor(colorResId);
    }


    public void setLeftAndRightBtnTextColor(int colorResId) {
        btn_right.setTextColor(colorResId);
        btn_left.setTextColor(colorResId);
    }

    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setContentInsetsRelative(0, 0);
    }

    @Override
    public void onClick(View v) {
        if (timeLock.isLock()) {
            return;
        }
        timeLock.lock();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.img_left:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                onBackPressed();
                break;
            case R.id.btn_right:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
            default:
                break;
        }
        onClickView(v);
    }

    public void onClickView(View v) {//BaseTitleActivity的子类复写此方法来处理点击事件，以便统一做防止重复点击处理

    }
}
