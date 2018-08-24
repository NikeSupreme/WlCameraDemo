package com.wulian.wlcamera.device.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.DeviceDetailMsg;
import com.wulian.wlcamera.bean.ICamDeviceBean;
import com.wulian.wlcamera.tools.IcamMsgEventHandler;
import com.wulian.wlcamera.utils.ToastUtil;
import com.wulian.wlcamera.utils.XmlHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by hxc on 2017/5/12.
 * 摄像机信息界面
 */

public class CameraInformationActivity extends BaseTitleActivity implements View.OnClickListener, IcamMsgEventHandler {
    private TextView tvDeviceType;
    private TextView tvDeviceNumber;
    private TextView tvFirmwareVersion;
    private TextView tvConnectWifi;
    private TextView tvWifiStrength;
    private TextView tvIpAddress;
    private TextView tvMacAddress;
    private ICamDeviceBean iCamDeviceBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_camera_information, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getString(R.string.Device_Detail));
    }

    @Override
    protected void initView() {
        super.initView();
        tvDeviceType = (TextView) findViewById(R.id.tv_device_type);
        tvDeviceNumber = (TextView) findViewById(R.id.tv_device_number);
        tvFirmwareVersion = (TextView) findViewById(R.id.tv_firmware_version);
        tvConnectWifi = (TextView) findViewById(R.id.tv_connect_wifi);
        tvWifiStrength = (TextView) findViewById(R.id.tv_wifi_strength);
        tvIpAddress = (TextView) findViewById(R.id.tv_ip_address);
        tvMacAddress = (TextView) findViewById(R.id.tv_mac_address);
        tvFirmwareVersion.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initData() {
        super.initData();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        tvDeviceNumber.setText(iCamDeviceBean.did);
        queryDetailInformation();
        getLatestFirmwareVersion();

    }


    private void queryDetailInformation() {
        IPCMsgController.MsgQueryDeviceDescriptionInfo(iCamDeviceBean.did,
                iCamDeviceBean.sdomain);
    }

    private void getLatestFirmwareVersion() {
        IPCMsgController.MsgQueryFirewareVersion(iCamDeviceBean.did, iCamDeviceBean.sdomain);

    }

    private void setDeviceInformation(DeviceDetailMsg detailMsg) {
        tvIpAddress.setText(detailMsg.getWifi_ip());
        tvWifiStrength.setText(detailMsg.getWifi_signal() + "%");
        tvMacAddress.setText(detailMsg.getWifi_mac());
        tvConnectWifi.setText(detailMsg.getWifi_ssid());
        tvFirmwareVersion.setText(detailMsg.getVersion());
    }



    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                case QUERY_FIREWARE_VERSION:
                    ToastUtil.show(this,getString(R.string.Config_Query_Device_Fail));
                    break;
            }
        } else {
            switch (event.getApiType()) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                    DeviceDetailMsg detailMsg = XmlHandler
                            .getDeviceDetailMsg(event.getMessage());
                    if (detailMsg != null) {
                        setDeviceInformation(detailMsg);
                    }
                    break;
                case CONFIG_FIREWARE_UPDATE_MODE:
                    break;

            }
        }
    }
}
