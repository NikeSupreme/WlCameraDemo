package com.wulian.wlcamera;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wulian.wlcamera.tools.Preference;


/**
 * Base 基类。同时部分集成 WLFragment。
 */
public abstract class BaseFragment extends Fragment {
    protected String TAG = getClass().getSimpleName();
    protected Preference preference = Preference.getPreferences();
    protected FragmentActivity mActivity;
    protected LayoutInflater inflater;
    protected View rootView;

    @Override
    public void onAttach(Activity activity) {
        this.mActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(getLayoutResID(), null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();
    }

    public abstract int getLayoutResID();

    public abstract void initView();

    protected void initData(){
    }

    public void initListener(){
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}