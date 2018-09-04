package com.wulian.wlcamera.device.setting;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.ICamDeviceBean;
import com.wulian.wlcamera.bean.ZoneBean;
import com.wulian.wlcamera.device.adapter.CameraZoneAdapter;
import com.wulian.wlcamera.utils.CameraUtil;
import com.wulian.wlcamera.utils.KeyboardUtil;
import com.wulian.wlcamera.utils.LanguageUtil;
import com.wulian.wlcamera.utils.Trans2PinYin;
import com.wulian.wlcamera.utils.WLog;

import java.util.ArrayList;
import java.util.List;

/**
 * created by huxc  on 2017/11/9.
 * func： 时区设置界面
 * email: hxc242313@qq.com
 */

public class CameraZoneSettingActivity extends BaseTitleActivity {
    private View layout_brand_header;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearSearch;
    private EditText mEditSearch;
    private TextView mTextCancel, mTextNoResult;

    private CameraZoneAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<ZoneBean> mTotalList;
    private List<ZoneBean> mSearchList;
    private String zoneName;
    private ZoneBean selectedZoneName = new ZoneBean();
    private ICamDeviceBean iCamDeviceBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_zone_setting, true);
    }


    @Override
    protected void initView() {
        super.initView();
        layout_brand_header = findViewById(R.id.layout_brand_header);
        mRecyclerView = (RecyclerView) findViewById(R.id.camera_zone_recycler);
        mLinearSearch = (LinearLayout) findViewById(R.id.camera_zone_linear_search);
        mTextCancel = (TextView) findViewById(R.id.camera_zone_text_cancel);
        mTextNoResult = (TextView) findViewById(R.id.camera_zone_text_noResult);
        mEditSearch = (EditText) findViewById(R.id.camera_zone_edit_search);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });

    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Time_Zone));
    }

    @Override
    protected void initData() {
        super.initData();
        mSearchList = new ArrayList<>();
        mTotalList = new ArrayList<>();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        zoneName = getIntent().getStringExtra("zoneName");
        selectedZoneName.en = zoneName;
        mAdapter = new CameraZoneAdapter();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 3);
            }
        });
        mTotalList = CameraUtil.getZoneDataFromJson(this);
        mAdapter.setData(mTotalList);
        setDefaultSelectedItem(selectedZoneName);

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mAdapter.setOnItemClickListener(new CameraZoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ZoneBean bean) {
                mAdapter.setSelectItem(position);
                mAdapter.notifyDataSetChanged();
                selectedZoneName = bean;
                for (int i = 0; i < mTotalList.size(); i++) {
                    if (mTotalList.get(i).cn.contains(bean.cn)) {
                        IPCMsgController.MsgNotifyTimeZone(iCamDeviceBean.deviceId, iCamDeviceBean.deviceDomain, mTotalList.get(i).en);
                        WLog.i(TAG, "ZoneCode : " + mTotalList.get(i).en);
                        setResult(RESULT_OK,new Intent().putExtra("zoneName",mTotalList.get(i).en));
                        finish();
                    }
                }
            }
        });
        mLinearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewClick();
            }
        });
        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTextClick(v);
            }
        });

        mEditSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = mEditSearch.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                }

                // 点击了 输入框中 右边的 x
                if (event.getX() > mEditSearch.getWidth()
                        - mEditSearch.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    mEditSearch.setText("");
                }
                return false;
            }
        });

        mEditSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                }
                return false;
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                WLog.i(TAG, "before: " + s + ", count: " + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WLog.i(TAG, "on: " + s + ", count: " + count);
                if (LanguageUtil.isChina()) {
                    doChSearch(s.toString());
                } else {
                    doEnSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                WLog.i("WL--->", "after: " + s);
            }
        });
    }

    /**
     * 默认设置地区显示
     * 取消键 显示设置的地区
     */
    private void setDefaultSelectedItem(ZoneBean bean) {
        mAdapter.setSelectItem(-1);
        for (int i = 0; i < mTotalList.size(); i++) {
            if (mTotalList.get(i).en.contains(bean.en)) {
                mAdapter.setSelectItem(i);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 搜索后显示设置的地区
     */
    private void showSearchSelectedItem() {
        mAdapter.setSelectItem(-1);
        for (int i = 0; i < mSearchList.size(); i++) {
            if (mSearchList.get(i).en.contains(selectedZoneName.en)) {
                mAdapter.setSelectItem(i);
            }
        }
    }

    /**
     * 取消TextView点击事件
     */
    private void cancelTextClick(View v) {

        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setData(mTotalList);
        setDefaultSelectedItem(selectedZoneName);
        mLinearSearch.setVisibility(View.VISIBLE);
        mEditSearch.setVisibility(View.INVISIBLE);
        mTextCancel.setVisibility(View.INVISIBLE);
        mTextNoResult.setVisibility(View.INVISIBLE);
        mEditSearch.clearFocus();
        // 强制隐藏键盘
        KeyboardUtil.hideKeyboard(CameraZoneSettingActivity.this, v);
    }

    /**
     * 点击 searchView
     */
    private void searchViewClick() {

        mLinearSearch.setVisibility(View.INVISIBLE);
        mEditSearch.setVisibility(View.VISIBLE);
        mTextCancel.setVisibility(View.VISIBLE);
        mEditSearch.setFocusable(true);
        mEditSearch.setFocusableInTouchMode(true);
        mEditSearch.requestFocus();
        // 强制打开键盘
        KeyboardUtil.showKeyboard(CameraZoneSettingActivity.this);
        mEditSearch.setText("");
    }

    private void doChSearch(String key) {
        if (key.trim().isEmpty()) {
            mSearchList = new ArrayList<>();
            mTextNoResult.setVisibility(View.INVISIBLE);
            mAdapter.setData(mSearchList);
            return;
        }

        String lowerKey = key.toLowerCase().trim();
        mSearchList.clear();

        for (ZoneBean bean : mTotalList) {
            boolean not_add_me = true;
            // 分组
            if (TextUtils.equals(key, bean.cn)) {
                not_add_me = false;
            }
            // 匹配 brandName
            if (not_add_me && (bean.cn.startsWith(lowerKey) || bean.cn.endsWith(lowerKey) || bean.cn.toLowerCase().contains(lowerKey))) {
                not_add_me = false;
            }
            // 匹配 localName
            if (not_add_me && !TextUtils.isEmpty(bean.cn)) {
                // 匹配汉字
                if (bean.cn.startsWith(key) || bean.cn.endsWith(key) || bean.cn.contains(key)) {
                    not_add_me = false;
                }
                // 拼音首字母
                if (not_add_me && Trans2PinYin.isFirstCharacter(lowerKey, bean.cn)) {
                    not_add_me = false;
                }
                // 拼音开头
                if (not_add_me && Trans2PinYin.isStartPinYin(lowerKey, bean.cn)) {
                    not_add_me = false;
                }
                // 拼音包含
                if (not_add_me && Trans2PinYin.isContainsPinYin(lowerKey, bean.cn)) {
                    not_add_me = false;
                }
            }

            if (!not_add_me) {
                mSearchList.add(bean);
            }
        }

        if (mSearchList.size() == 0) {
            mTextNoResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextNoResult.setVisibility(View.INVISIBLE);
        }
        mAdapter.setData(mSearchList);
        showSearchSelectedItem();

    }

    private void doEnSearch(String key) {
        if (key.trim().isEmpty()) {
            mSearchList = new ArrayList<>();
            mTextNoResult.setVisibility(View.INVISIBLE);
            mAdapter.setData(mSearchList);
            return;
        }

        String lowerKey = key.toLowerCase().trim();
        mSearchList.clear();

        for (ZoneBean bean : mTotalList) {
            boolean not_add_me = true;
            // 分组
            if (TextUtils.equals(key, bean.en)) {
                not_add_me = false;
            }
            // 匹配 brandName
            if (not_add_me && (bean.en.startsWith(lowerKey) || bean.en.endsWith(lowerKey) || bean.en.toLowerCase().contains(lowerKey))) {
                not_add_me = false;
            }
            // 匹配 localName
            if (not_add_me && !TextUtils.isEmpty(bean.en)) {
                // 匹配汉字
                if (bean.cn.startsWith(key) || bean.en.endsWith(key) || bean.en.contains(key)) {
                    not_add_me = false;
                }
                // 拼音首字母
                if (not_add_me && Trans2PinYin.isFirstCharacter(lowerKey, bean.en)) {
                    not_add_me = false;
                }
                // 拼音开头
                if (not_add_me && Trans2PinYin.isStartPinYin(lowerKey, bean.en)) {
                    not_add_me = false;
                }
                // 拼音包含
                if (not_add_me && Trans2PinYin.isContainsPinYin(lowerKey, bean.en)) {
                    not_add_me = false;
                }
            }

            if (!not_add_me) {
                mSearchList.add(bean);
            }
        }

        if (mSearchList.size() == 0) {
            mTextNoResult.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            return;
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextNoResult.setVisibility(View.INVISIBLE);
        }
        mAdapter.setData(mSearchList);
        showSearchSelectedItem();

    }

}
