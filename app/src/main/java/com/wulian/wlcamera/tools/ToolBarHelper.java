package com.wulian.wlcamera.tools;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wulian.wlcamera.MainApplication;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.utils.DisplayUtil;


/**
 * Created by caoww on 16/5/8.
 */
public class ToolBarHelper {
    private Context mContext;
    private FrameLayout mContentView;
    /*用户定义的view*/
    private View mUserView;
    private View toolbarView;
    /*toolbar*/ private Toolbar mToolBar;
    private int toolbarHeight;
    private int statusBarHeight;

    /*视图构造器*/ private LayoutInflater mInflater; /*
    * 两个属性
    * 1、toolbar是否悬浮在窗口之上
    * 2、toolbar的高度获取
    * */
    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };

    public ToolBarHelper(Context context, int layoutId, int statusBarHeight) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        toolbarHeight = DisplayUtil.dip2Pix(context, 48);
        this.statusBarHeight = statusBarHeight;
        /*初始化整个内容*/
        initContentView();

         /*初始化toolbar*/
        initToolBar();

        /*初始化用户定义的布局*/
        initUserView(layoutId);
    }

    private void initContentView() { /*直接创建一个帧布局，作为视图容器的父容器*/
        mContentView = new FrameLayout(mContext);
//        mContentView.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
    }

    private void initToolBar() { /*通过inflater获取toolbar的布局文件*/
        toolbarView = mInflater.inflate(R.layout.toolbar, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, toolbarHeight + statusBarHeight);
        mContentView.addView(toolbarView, params);
        mToolBar = (Toolbar) toolbarView.findViewById(R.id.toolbar);
        mToolBar.setPadding(0, statusBarHeight, 0, 0);
    }

    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(ATTRS);
        params.topMargin = toolbarHeight + statusBarHeight;
        mContentView.addView(mUserView, 0, params);
    }

    /**
     * 获取整体布局
     *
     * @return
     */
    public FrameLayout getContentView() {
        return mContentView;
    }

    /**
     * 获得头部之外的，用户交互布局
     *
     * @return
     */
    public View getmUserView() {
        return mUserView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

    public void setToolBarVisible(boolean visible) {
        if (visible) {
            toolbarView.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mUserView.getLayoutParams();
            params.topMargin = toolbarHeight + statusBarHeight;
            mUserView.setLayoutParams(params);
        } else {
            toolbarView.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mUserView.getLayoutParams();
            params.topMargin = 0;
            mUserView.setLayoutParams(params);
        }
    }
}
