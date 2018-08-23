package com.wulian.wlcamera;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wulian.wlcamera.utils.DisplayUtil;


/**
 * 不带title 的fragment
 *
 * @author czh
 */
public abstract class WLFragment extends BaseFragment {
    protected Resources resources;
    protected LinearLayout mLinSub;
    //整体性布局元素
    private FrameLayout header;//整个头部
    public TextView mTvTitle;// 头部中央标题
    private ImageView mIVTitle;// 头部中央标题
    //左侧元素
    private TextView mLeftTitle;//左侧标题
    public ImageView mImgBack;// 返回按钮,左侧图标
    protected ImageView img_setting;
    //左右两个整体布局
    private LinearLayout mLinBack;
    private LinearLayout mRelRight;
    //右侧元素
    private TextView mRightTitle;
    private ImageView mRightImg;
    protected EditText mEtSearch;
    protected LinearLayout llSearch;
    protected TextView tv_search_content;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        resources = activity.getResources();
    }

    protected void initTitle(View v) {
        mTvTitle = (TextView) v.findViewById(R.id.base_tv_fragment_title);
        mIVTitle = (ImageView) v.findViewById(R.id.base_img_title);
        mEtSearch = (EditText) v.findViewById(R.id.et_title_search);
        mLeftTitle = (TextView) v.findViewById(R.id.txt_left);
        mImgBack = (ImageView) v.findViewById(R.id.base_img_back_fragment);
        img_setting = (ImageView) v.findViewById(R.id.news_setting);
        mLinBack = (LinearLayout) v.findViewById(R.id.base_lin_back);
        mRelRight = (LinearLayout) v.findViewById(R.id.rel_right);
        mRightTitle = (TextView) v.findViewById(R.id.txt_right);
        mRightImg = (ImageView) v.findViewById(R.id.base_img_right);
        header = (FrameLayout) v.findViewById(R.id.hl_base_header);
        llSearch = (LinearLayout) v.findViewById(R.id.ll_search);
        tv_search_content = (TextView) v.findViewById(R.id.tv_search_content);
        mTvTitle.setVisibility(View.VISIBLE);
        mRelRight.setVisibility(View.GONE);
        mEtSearch.setVisibility(View.GONE);
        llSearch.setVisibility(View.GONE);
    }

    @Override
    public final int getLayoutResID() {
        return R.layout.wl_base_fragment;
    }

    public abstract int layoutResID();

    public abstract void initView(View view);

    protected void updateSkin() {

        //状态栏高度调整
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = 0;
            //获取status_bar_height资源的ID
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            } else {
                statusBarHeight = DisplayUtil.dip2Pix(MainApplication.getApplication(), 20);
            }
            header.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    @Override
    public void initView() {
        initSubView(rootView);
        initTitle(mLinSub);
        initView(rootView);
        updateSkin();
    }

    private void initSubView(View v) {
        View view = null;
        if (layoutResID() != 0) {
            view = inflater.inflate(layoutResID(), null);
        }
        mLinSub = (LinearLayout) v.findViewById(R.id.hl_base_fragment_lin_sub);
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (view != null) {
            mLinSub.addView(view, layoutParams);
        }
    }

    /**
     * 返回标题栏 TextView 对象，用来设置标题栏文本
     *
     * @return
     */
    public TextView getFragmentTitleText() {
        return mTvTitle;
    }

    /**
     * 设置左侧图片，设置则显示
     *
     * @param resId
     */
    public void setLeftImg(int resId) {
        getLeftLin();
        mImgBack.setVisibility(View.VISIBLE);
        mImgBack.setImageResource(resId);
    }

    /**
     * 设置左侧的图片，并且显示
     *
     * @param id
     */
    public void setLeftImageAndEvent(int id, View.OnClickListener event) {
        getLeftLin();
        mImgBack.setVisibility(View.VISIBLE);
        mImgBack.setImageResource(id);
        mImgBack.setOnClickListener(event);
    }

    /**
     * 设置左侧图片隐藏
     */
    public void setLeftImgGone() {
        mImgBack.setVisibility(View.GONE);
    }

    /**
     * 设置左侧的文本，并且显示
     *
     * @param text
     */
    public TextView setLeftText(String text) {
        getLeftLin();
        mLeftTitle.setVisibility(View.VISIBLE);
        mLeftTitle.setText(text);
        return mLeftTitle;
    }

    /**
     * 隐藏左侧的文本
     *
     */
    public TextView setLeftTextGone() {
        mLeftTitle.setVisibility(View.GONE);
        return mLeftTitle;
    }

    /**
     * 隐藏标题，显示搜索字样
     *
     * @param relRightImg
     */
    public void setmEtSearchAndmRelRight(int relRightImg, String searchContent) {
        getRightRel();
        llSearch.setVisibility(View.VISIBLE);
        mTvTitle.setVisibility(View.GONE);
        mRightImg.setVisibility(View.VISIBLE);
        mRightTitle.setVisibility(View.GONE);
        mRightImg.setImageResource(relRightImg);
        tv_search_content.setText(searchContent);
    }

    /**
     * 设置左侧图片隐藏
     */
    public void setTitleImg(int id) {
        mTvTitle.setVisibility(View.GONE);
        mIVTitle.setVisibility(View.VISIBLE);
        llSearch.setVisibility(View.GONE);
        mIVTitle.setImageResource(id);
    }

    public void setmEtSearchShow() {
        llSearch.setVisibility(View.GONE);
        mEtSearch.setVisibility(View.VISIBLE);
        mEtSearch.setFocusable(true);
        mEtSearch.setFocusableInTouchMode(true);
        mEtSearch.requestFocus();
//        InputMethodUtils.show(getActivity(), mEtSearch);
    }

    /**
     * 设置右侧的文本，并且显示
     *
     * @param text
     */
    public TextView setRightText(String text) {
        getRightRel();
        mRightTitle.setVisibility(View.VISIBLE);
        mRightTitle.setText(text);
        return mRightTitle;
    }

    /**
     * 设置右侧的图片，并且显示
     *
     * @param id
     */
    public void setRightImage(int id) {
        getRightRel();
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(id);
    }

    /**
     * 设置右侧的图片，并且显示
     *
     * @param id
     */
    public void setRightImageAndEvent(int id, View.OnClickListener event) {
        getRightRel();
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(id);
        mRightImg.setOnClickListener(event);
    }

    /**
     * 右半边的布局
     *
     * @return
     */
    public LinearLayout getRightRel() {
        mRelRight.setVisibility(View.VISIBLE);
        return mRelRight;
    }

    /**
     * 左半边的布局，返回按钮
     *
     * @return
     */
    public LinearLayout getLeftLin() {
        mLinBack.setVisibility(View.VISIBLE);
        return mLinBack;
    }

    public LinearLayout getSearchContent() {
        return llSearch;
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();

    }
}
