package com.wulian.wlcamera.device.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.ICamDeviceBean;
import com.wulian.wlcamera.bean.LanguageVolumeBean;
import com.wulian.wlcamera.device.adapter.WLBaseAdapter;
import com.wulian.wlcamera.tools.IcamMsgEventHandler;
import com.wulian.wlcamera.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * created by huxc  on 2017/10/30.
 * func：  摄像机语音播报
 * email: hxc242313@qq.com
 */

public class CameraBroadcastActivity extends BaseTitleActivity implements View.OnClickListener, IcamMsgEventHandler {
    private RelativeLayout rlBroadcast;
    private ListView lvBroadcastVoice;
    private RelativeLayout rlEnglish;
    private RelativeLayout rlChinese;
    private ImageView ivEnglishChecked;
    private ImageView ivChineseChecked;

    private VoiceAdapter adapter;
    private List<String> mData;
    private LanguageVolumeBean languageVolumeBean;
    private ICamDeviceBean iCamDeviceBean;
    private String selectLanguage;
    private int selectVolume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_camera_broadcast, true);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Camera_Voice_Set));
    }

    @Override
    protected void initView() {
        super.initView();
        rlChinese = (RelativeLayout) findViewById(R.id.rl_ch);
        rlEnglish = (RelativeLayout) findViewById(R.id.rl_en);
        lvBroadcastVoice = (ListView) findViewById(R.id.lv_broadcast_voice);
        ivChineseChecked = (ImageView) findViewById(R.id.iv_checked_ch);
        ivEnglishChecked = (ImageView) findViewById(R.id.iv_checked_en);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlChinese.setOnClickListener(this);
        rlEnglish.setOnClickListener(this);
        lvBroadcastVoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectItem(position);
                adapter.notifyDataSetInvalidated();
                selectVolume = getSelectVolume(position);
                IPCMsgController.MsgNotifyVolume(iCamDeviceBean.uniqueDeviceId, iCamDeviceBean.sdomain, selectVolume);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        languageVolumeBean = (LanguageVolumeBean) getIntent().getSerializableExtra("languageVolumeBean");
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        getData();
        showSelectedLanguage(languageVolumeBean);
        showSelectedVolume(languageVolumeBean);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_en:
                ivEnglishChecked.setVisibility(View.VISIBLE);
                ivChineseChecked.setVisibility(View.GONE);
                selectLanguage = "en";
                IPCMsgController.MsgNotifyLanguage(iCamDeviceBean.uniqueDeviceId, iCamDeviceBean.sdomain, selectLanguage);
                break;
            case R.id.rl_ch:
                ivChineseChecked.setVisibility(View.VISIBLE);
                ivEnglishChecked.setVisibility(View.GONE);
                selectLanguage = "cn";
                IPCMsgController.MsgNotifyLanguage(iCamDeviceBean.uniqueDeviceId, iCamDeviceBean.sdomain, selectLanguage);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("volume", selectVolume);
        it.putExtra("language", selectLanguage);
        setResult(RESULT_OK, it);
        finish();
        super.onBackPressed();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case NOTIFY_LANGUAGE:
                case NOTIFY_VOLUME:
                    ToastUtil.show(this,getString(R.string.Config_Query_Device_Fail));
                    break;
            }
        } else {
            switch (event.getApiType()) {
                case NOTIFY_LANGUAGE:
                    languageVolumeBean.setLanguage(selectLanguage);
                    break;
                case NOTIFY_VOLUME:
                    languageVolumeBean.setVolume(selectVolume);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new VoiceAdapter(this, mData);
        lvBroadcastVoice.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getData() {
        mData = new ArrayList<>();
        mData.add(getString(R.string.Cateyemini_Humandetection_Mute));
        mData.add(getString(R.string.Low));
        mData.add(getString(R.string.Cateyemini_Humandetection_Middle));
        mData.add(getString(R.string.High));
        mData.add(getString(R.string.Camera_Ultrahigh));
    }

    private void showSelectedVolume(LanguageVolumeBean bean) {
        int volume = bean.getVolume();
        if (volume == 0) {//静音
            setSelectItem(0);
        } else if (0 < volume && volume <= 25) {//低
            setSelectItem(1);
        } else if (25 < volume && volume <= 50) {//中
            setSelectItem(2);
        } else if (50 < volume && volume <= 75) {//高
            setSelectItem(3);
        } else if (75 < volume) {//超高
            setSelectItem(4);
        }
    }

    private void showSelectedLanguage(LanguageVolumeBean bean) {
        if (!TextUtils.isEmpty(bean.getLanguage())) {
            switch (bean.getLanguage()) {
                case "en":
                    ivEnglishChecked.setVisibility(View.VISIBLE);
                    break;
                case "cn":
                    ivChineseChecked.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    private int getSelectVolume(int position) {
        int volume = 0;
        switch (position) {
            case 0:
                volume = 0;
                break;
            case 1:
                volume = 25;
                break;
            case 2:
                volume = 50;
                break;
            case 3:
                volume = 75;
                break;
            case 4:
                volume = 100;
                break;
        }
        return volume;
    }


    public class VoiceAdapter extends WLBaseAdapter<String> {
        public VoiceAdapter(Context context, List<String> data) {
            super(context, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CameraBroadcastActivity.ViewHolder holder = null;
            if (convertView == null) {
                holder = new CameraBroadcastActivity.ViewHolder();
                convertView = mInflater.inflate(R.layout.item_broadcast_voice, null);
                holder.ivCheck = (ImageView) convertView.findViewById(R.id.iv_checked);
                holder.tvBroadcastLanguage = (TextView) convertView.findViewById(R.id.tv_broadcast_language);
                holder.tvBroadcastLanguage.setText(mData.get(position));
                convertView.setTag(holder);
            } else {
                holder = (CameraBroadcastActivity.ViewHolder) convertView.getTag();
            }
            if (position == selectItem) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;

    public final class ViewHolder {
        public ImageView ivCheck;
        public TextView tvBroadcastLanguage;
    }

}
