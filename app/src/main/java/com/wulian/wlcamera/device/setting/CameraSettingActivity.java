package com.wulian.wlcamera.device.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.ICamDeviceBean;
import com.wulian.wlcamera.bean.LanguageVolumeBean;
import com.wulian.wlcamera.tools.IcamMsgEventHandler;
import com.wulian.wlcamera.utils.CameraUtil;
import com.wulian.wlcamera.utils.WLog;
import com.wulian.wlcamera.utils.XmlHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hxc on 2017/6/5.
 * func:随便看摄像机设置界面
 */

public class CameraSettingActivity extends BaseTitleActivity implements IcamMsgEventHandler, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private RelativeLayout rlDeviceName;
    private RelativeLayout rlDeviceInformation;
    private RelativeLayout rlRecordStorage;
    private RelativeLayout rlSafeProtect;
    private RelativeLayout rlZoneSetting;

    private TextView tvDeviceName;
    private TextView tvHasSDCard;
    private TextView tvZoneName;
    private ToggleButton tbInvert;
    private ToggleButton tbLed;
    private static final String QUERY = "QUERY";
    private static final String UPDATE_NAME = "UPDATE_NAME";
    private static final String UNBIND = "UNBIND";
    private static final String SEND_REQUEST = "SEND_REQUEST";
    private static final int REQUEST_ZONE = 1;
    private static final int MSG_FINISH = 1;
    private static final int MSG_EDIT_META = 2;
    private String deviceId;
    private String sipDomain;
    private String deviceName;
    private boolean hasSDCard;
    private int angle = 2;//0为不倒置，180为倒置
    private int led = 2;//0为灭，1为亮
    private int voice = 22;//0为静音，1为有声音
    private int volume = 10000;//0-100
    private String language = "";//ch,en
    private String zoneName = "";
    private ICamDeviceBean iCamDeviceBean;
    private LanguageVolumeBean languageVolumeBean;
    private boolean isQueryLedAndVoiceAndInvert = true;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH:// 结束页面
                    CameraSettingActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
    private RelativeLayout rlInvert;
    private RelativeLayout rlLed;
    private RelativeLayout rlVoice;

    private boolean isShared = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_setting, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isShared) {
            initWebData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvHasSDCard = (TextView) findViewById(R.id.tv_has_SD_card);
        tvZoneName = (TextView) findViewById(R.id.tv_zone_name);
        rlInvert = (RelativeLayout) findViewById(R.id.rl_invert);
        rlLed = (RelativeLayout) findViewById(R.id.rl_led);
        rlVoice = (RelativeLayout) findViewById(R.id.rl_voice);
        rlDeviceName = (RelativeLayout) findViewById(R.id.rl_device_name);
        rlDeviceInformation = (RelativeLayout) findViewById(R.id.rl_device_information);
        rlRecordStorage = (RelativeLayout) findViewById(R.id.rl_record_storage);
        rlSafeProtect = (RelativeLayout) findViewById(R.id.rl_safe_protect);
        rlZoneSetting = (RelativeLayout) findViewById(R.id.rl_zone);
        tbInvert = (ToggleButton) findViewById(R.id.tb_invert);
        tbLed = (ToggleButton) findViewById(R.id.tb_led);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Mine_Setts));
    }

    @Override
    protected void initData() {
        super.initData();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        deviceId = iCamDeviceBean.deviceId;
        sipDomain = iCamDeviceBean.deviceDomain;

        if (TextUtils.equals(iCamDeviceBean.type, "CMICA2")) {
            tvDeviceName.setText(getString(R.string.Lookever) + deviceId.substring(deviceId.length() - 3));
        } else if (TextUtils.equals(iCamDeviceBean.type, "CMICA3")) {
            tvDeviceName.setText(getString(R.string.Penguin) + deviceId.substring(deviceId.length() - 3));
        }

        languageVolumeBean = new LanguageVolumeBean();
        if (isShared) {
            rlDeviceInformation.setVisibility(View.GONE);
            rlInvert.setVisibility(View.GONE);
            rlLed.setVisibility(View.GONE);
            rlVoice.setVisibility(View.GONE);
            rlRecordStorage.setVisibility(View.GONE);
            rlSafeProtect.setVisibility(View.GONE);
            rlZoneSetting.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        rlInvert.setOnClickListener(this);
        rlLed.setOnClickListener(this);
        rlVoice.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlRecordStorage.setOnClickListener(this);
        rlSafeProtect.setOnClickListener(this);
        rlZoneSetting.setOnClickListener(this);
        tbInvert.setOnCheckedChangeListener(this);
        tbLed.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_invert:
                isQueryLedAndVoiceAndInvert = false;
                tbInvert.toggle();
                break;
            case R.id.rl_led:
                isQueryLedAndVoiceAndInvert = false;
                tbLed.toggle();
                break;
            case R.id.rl_voice:
                startActivity(new Intent(this, CameraBroadcastActivity.class)
                        .putExtra("languageVolumeBean", languageVolumeBean)
                        .putExtra("ICamDeviceBean", iCamDeviceBean));
                break;
            case R.id.rl_device_information:
                startActivity(new Intent(this, CameraInformationActivity.class).
                        putExtra("ICamDeviceBean", iCamDeviceBean));
                break;
            case R.id.rl_record_storage:
                if (hasSDCard) {
                    startActivity(new Intent(this, CameraRecordStorageActivity.class).
                            putExtra("ICamDeviceBean", iCamDeviceBean));
                }
                break;
            case R.id.rl_safe_protect:
                startActivity(new Intent(this, CameraSafeProtectActivity.class).
                        putExtra("ICamDeviceBean", iCamDeviceBean));
                break;
            case R.id.rl_zone:
                startActivityForResult(new Intent(this, CameraZoneSettingActivity.class)
                        .putExtra("ICamDeviceBean", iCamDeviceBean)
                        .putExtra("zoneName", zoneName), REQUEST_ZONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ZONE:
                    zoneName = data.getStringExtra("zoneName");
                    tvZoneName.setText(CameraUtil.getZoneNameByLanguage(this, zoneName));
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.tb_invert) {
            if (isChecked) {
                angle = 180;
            } else {
                angle = 0;
            }
            if (isQueryLedAndVoiceAndInvert) {
                isQueryLedAndVoiceAndInvert = false;
            } else {
                configLEDVoiceAngel();
                reload();//画面倒置会挂断视频，需要重呼
            }
        } else if (id == R.id.tb_led) {
            if (isChecked) {
                led = 1;
            } else {
                led = 0;
            }
            if (isQueryLedAndVoiceAndInvert) {
                isQueryLedAndVoiceAndInvert = false;
            } else {
                configLEDVoiceAngel();
            }
            WLog.i(TAG, "isQueryLedAndVoiceAndInvert: " + isQueryLedAndVoiceAndInvert);
        }
    }

    private void reload() {
        IPCController.closeAllVideoAsync(null);
        IPCController.makeCallAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {

            }
        }, deviceId, iCamDeviceBean.deviceDomain);
    }

    /**
     * 查询随便看设置信息
     */
    private void initWebData() {
        IPCMsgController.MsgQueryLedAndVoicePromptInfo(deviceId, sipDomain);//查询led和voice
        IPCMsgController.MsgQueryStorageStatus(deviceId, sipDomain);//查询储状态信息
        IPCMsgController.MsgQueryVolume(deviceId, sipDomain);//查询摄像机音量设置
        IPCMsgController.MsgQueryLanguage(deviceId, sipDomain);//查询摄像机播报语言
        IPCMsgController.MsgQueryTimeZone(deviceId, sipDomain);//查询摄像机时区

    }


    //更新led、voice、angle视图
    private void updateLedVoiceAngleView(String xmlData) {
        led = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                "led_on").trim());
        voice = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                "audio_online").trim());
        angle = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                "angle").trim());
        if (angle == 180) {
            tbInvert.setChecked(true);
        } else {
            tbInvert.setChecked(false);
            isQueryLedAndVoiceAndInvert = false;
        }
        if (led == 1) {
            tbLed.setChecked(true);
        } else {
            tbLed.setChecked(false);
            isQueryLedAndVoiceAndInvert = false;
        }
    }

    //设置led、voice、angle
    private void configLEDVoiceAngel() {
        IPCMsgController.MsgConfigLedAndVoicePrompt(deviceId, sipDomain, led == 1 ? true : false,
                voice == 1 ? true : false, angle == 180 ? true : false);
    }


    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        }
    }

    protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType, String xmlData, String from, String code) {
        if (isSuccess) {
            switch (apiType) {
                case QUERY_LED_AND_VOICE_PROMPT_INFO:
                    isQueryLedAndVoiceAndInvert = true;
                    updateLedVoiceAngleView(xmlData);
                    break;
                case QUERY_STORAGE_STATUS:
                    Pattern pstatus = Pattern
                            .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                    Matcher matchers = pstatus.matcher(xmlData);
                    if (matchers.find()) {
                        String status = matchers.group(1).trim();
                        if ("1".equals(status)) {
                            hasSDCard = false;
                            tvHasSDCard.setText(getString(R.string.Backsee_No_SDcard));
                        } else if ("2".equals(status)) {
                            hasSDCard = true;
                            tvHasSDCard.setText(getString(R.string.Have_SD));
                        }
                    }
                    break;
                case QUERY_VOLUME:
                    volume = Integer.parseInt(CameraUtil.getParamFromXml(xmlData,
                            "vol").trim());
                    languageVolumeBean.setVolume(volume);
                    break;
                case QUERY_LANGUAGE:
                    language = XmlHandler
                            .parseDeviceSipInfo(xmlData, "language");
                    languageVolumeBean.setLanguage(language);
                    break;
                case QUERY_TIME_ZONE:
                    zoneName = XmlHandler
                            .parseDeviceSipInfo(xmlData, "zonename");
                    tvZoneName.setText(CameraUtil.getZoneNameByLanguage(this, zoneName));
                    WLog.i(TAG, "zoneName:" + zoneName);
                    break;

            }
        } else {
            switch (apiType) {
                case QUERY_LED_AND_VOICE_PROMPT_INFO:
                case QUERY_STORAGE_STATUS:
                case QUERY_VOLUME:
                case QUERY_LANGUAGE:
                case QUERY_TIME_ZONE:
                    break;
            }
        }
    }
}
