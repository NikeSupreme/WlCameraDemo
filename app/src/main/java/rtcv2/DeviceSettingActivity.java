/**
 * Project Name:  WulianIPCRTCV2JarLib
 * File Name:     DeviceSettingActivity.java
 * Package Name:  com.wulian.sdk.android.ipc.rtcv2
 * @Date:         2017年2月6日
 * Copyright (c)  2017, wulian All Rights Reserved.
 */

package rtcv2;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.wlcamera.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @ClassName: DeviceSettingActivity
 * @Date: 2017年2月6日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceSettingActivity extends Activity implements OnClickListener {

	private Button btn_QueryDeviceDescriptionInfo;
	private Button btn_QueryCameraInfo;
	private Button btn_ConfigCSC;
	private Button btn_QueryFirewareUpdateMode;
	private Button btn_ConfigUploadIPCLogs;
	private Button btn_ConfigMovementDetection;
	private Button btn_QueryMovementDetectionInfo;
	private Button btn_ConfigLinkageArming;
	private Button btn_QueryLinkageArmingInfo;
	private Button btn_QueryPTZInfo;
	private Button btn_QueryStorageStatus;
	private Button btn_ConfigLocalStorageDeviceFormat;
	private Button btn_NotifySynchroPermission;
	private Button btn_QueryFirewareVersion;
	private Button btn_QueryHistoryRecord;
	private Button btn_ControlStartRecord;
	private Button btn_ControlStopRecord;
	private Button btn_QueryManyAlarmIDLinkedVideoInfo;
	private Button btn_ControlHistoryRecordProgress;
	private Button btn_NotifyHistoryRecordHeartbeat;
	private Button btn_SendMessage_Set_LED;
	private Button btn_WulianBellQueryNotifyHeartBeat;
	private Button btn_WulianBellQueryControlAppInitiativeHangup;
	private Button btn_WulianBellQueryDeviceConfigInformation;
	private Button btn_WulianBellQuerySetDeviceLanguage;
	private Button btn_WulianBellQuerySetPIRConfigInformation;
	private String destID;
	private String destPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devicesetting);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		btn_QueryDeviceDescriptionInfo = (Button) findViewById(R.id.btn_QueryDeviceDescriptionInfo);
		btn_QueryCameraInfo = (Button) findViewById(R.id.btn_QueryCameraInfo);
		btn_ConfigCSC = (Button) findViewById(R.id.btn_ConfigCSC);
		btn_QueryFirewareUpdateMode = (Button) findViewById(R.id.btn_QueryFirewareUpdateMode);
		btn_ConfigUploadIPCLogs = (Button) findViewById(R.id.btn_ConfigUploadIPCLogs);

		btn_ConfigMovementDetection = (Button) findViewById(R.id.btn_ConfigMovementDetection);
		btn_QueryMovementDetectionInfo = (Button) findViewById(R.id.btn_QueryMovementDetectionInfo);
		btn_ConfigLinkageArming = (Button) findViewById(R.id.btn_ConfigLinkageArming);
		btn_QueryLinkageArmingInfo = (Button) findViewById(R.id.btn_QueryLinkageArmingInfo);
		btn_QueryPTZInfo = (Button) findViewById(R.id.btn_QueryPTZInfo);
		btn_QueryStorageStatus = (Button) findViewById(R.id.btn_QueryStorageStatus);
		btn_ConfigLocalStorageDeviceFormat = (Button) findViewById(R.id.btn_ConfigLocalStorageDeviceFormat);
		btn_NotifySynchroPermission = (Button) findViewById(R.id.btn_NotifySynchroPermission);
		btn_QueryFirewareVersion = (Button) findViewById(R.id.btn_QueryFirewareVersion);
		btn_QueryHistoryRecord = (Button) findViewById(R.id.btn_QueryHistoryRecord);
		btn_ControlStartRecord = (Button) findViewById(R.id.btn_ControlStartRecord);
		btn_ControlStopRecord = (Button) findViewById(R.id.btn_ControlStopRecord);
		btn_ControlHistoryRecordProgress = (Button) findViewById(R.id.btn_ControlHistoryRecordProgress);
		btn_NotifyHistoryRecordHeartbeat = (Button) findViewById(R.id.btn_NotifyHistoryRecordHeartbeat);
		btn_SendMessage_Set_LED = (Button) findViewById(R.id.btn_SendMessage_Set_LED);
		
		btn_WulianBellQueryNotifyHeartBeat = (Button) findViewById(R.id.btn_WulianBellQueryNotifyHeartBeat);
		btn_WulianBellQueryControlAppInitiativeHangup = (Button) findViewById(R.id.btn_WulianBellQueryControlAppInitiativeHangup);
		btn_WulianBellQueryDeviceConfigInformation = (Button) findViewById(R.id.btn_WulianBellQueryDeviceConfigInformation);
		btn_WulianBellQuerySetDeviceLanguage = (Button) findViewById(R.id.btn_WulianBellQuerySetDeviceLanguage);
		btn_WulianBellQuerySetPIRConfigInformation = (Button) findViewById(R.id.btn_WulianBellQuerySetPIRConfigInformation);
	}

	private void initData() {
		Bundle bd = getIntent().getExtras();
		destID = bd.getString("DestID");
		destPoint = bd.getString("DestPoint");
		if (TextUtils.isEmpty(destID) || TextUtils.isEmpty(destPoint)) {
			Toast.makeText(this, "Please input correct ID and Point!",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void setListener() {
		btn_SendMessage_Set_LED.setOnClickListener(this);
		btn_QueryDeviceDescriptionInfo.setOnClickListener(this);
		btn_QueryCameraInfo.setOnClickListener(this);
		btn_ConfigCSC.setOnClickListener(this);
		btn_QueryFirewareUpdateMode.setOnClickListener(this);
		btn_ConfigUploadIPCLogs.setOnClickListener(this);
		btn_ConfigMovementDetection.setOnClickListener(this);
		btn_QueryMovementDetectionInfo.setOnClickListener(this);
		btn_ConfigLinkageArming.setOnClickListener(this);
		btn_QueryLinkageArmingInfo.setOnClickListener(this);
		btn_QueryPTZInfo.setOnClickListener(this);
		btn_QueryStorageStatus.setOnClickListener(this);
		btn_ConfigLocalStorageDeviceFormat.setOnClickListener(this);
		btn_NotifySynchroPermission.setOnClickListener(this);
		btn_QueryFirewareVersion.setOnClickListener(this);
		btn_QueryHistoryRecord.setOnClickListener(this);
		btn_ControlStartRecord.setOnClickListener(this);
		btn_ControlStopRecord.setOnClickListener(this);
		btn_ControlHistoryRecordProgress.setOnClickListener(this);
		btn_NotifyHistoryRecordHeartbeat.setOnClickListener(this);
		btn_WulianBellQueryNotifyHeartBeat.setOnClickListener(this);
		btn_WulianBellQueryControlAppInitiativeHangup.setOnClickListener(this);
		btn_WulianBellQueryDeviceConfigInformation.setOnClickListener(this);
		btn_WulianBellQuerySetDeviceLanguage.setOnClickListener(this);
		btn_WulianBellQuerySetPIRConfigInformation.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCcameraXmlMsgEvent event) {
		Toast.makeText(this,event.getApiType().name()+"\n"+ event.getMessage(), Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public void onClick(View v) {
 
		switch (v.getId()) {
		case R.id.btn_SendMessage_Set_LED:
			IPCMsgController.MsgConfigLedAndVoicePrompt(destID, destPoint, true, true, false);
			break;
		case R.id.btn_QueryDeviceDescriptionInfo:
			IPCMsgController.MsgQueryDeviceDescriptionInfo(destID, destPoint);
			break;
		case R.id.btn_QueryCameraInfo:
			IPCMsgController.MsgQueryCameraInfo(destID, destPoint);
			break;
		case R.id.btn_ConfigCSC:
			IPCMsgController.MsgConfigCSC(destID, destPoint, 50, 50, 50, 50);
			break;
		case R.id.btn_QueryFirewareUpdateMode:
			IPCMsgController.MsgQueryFirewareUpdateMode(destID, destPoint);
			break;
		case R.id.btn_ConfigUploadIPCLogs:
			IPCMsgController.MsgConfigUploadIPCLogs(destID, destPoint);
			break;
		case R.id.btn_ConfigMovementDetection:
			String[] areas = new String[3];
			areas[0] = "0,0,120,120";
			areas[1] = "10,10,120,120";
			areas[2] = "50,50,120,120";
			IPCMsgController.MsgConfigMovementDetection(destID, destPoint, true, 50, areas);
			break;
		case R.id.btn_QueryMovementDetectionInfo:
			IPCMsgController.MsgQueryMovementDetectionInfo(destID, destPoint);
			break;
 
		case R.id.btn_ConfigLinkageArming:
			String[] schedule = new String[7];
			for (int i = 0; i < 7; i++) {
				StringBuilder sb = new StringBuilder(String.valueOf(i + 1));
				sb.append(",");
				sb.append("12:00-13:30");
				sb.append(",");
				sb.append("14:00-16:30");
				schedule[i] = sb.toString();
			}
			IPCMsgController.MsgConfigLinkageArming(destID, destPoint, true, schedule);
			break;
		case R.id.btn_QueryLinkageArmingInfo:
			IPCMsgController.MsgQueryLinkageArmingInfo(destID, destPoint);
			break;
		case R.id.btn_QueryPTZInfo:
			break;
		case R.id.btn_QueryStorageStatus:
			IPCMsgController.MsgQueryStorageStatus(destID, destPoint);
			break;
		case R.id.btn_ConfigLocalStorageDeviceFormat:
			IPCMsgController.MsgConfigLocalStorageDeviceFormat(destID, destPoint);
			break;
		case R.id.btn_NotifySynchroPermission:
			IPCMsgController.MsgNotifySynchroPermission(destID, destPoint);
			break;
		case R.id.btn_QueryFirewareVersion:
			IPCMsgController.MsgQueryFirewareVersion(destID, destPoint);
			break;
		case R.id.btn_QueryHistoryRecord:
			IPCMsgController.MsgQueryHistoryRecord(destID, destPoint);
			break;
		case R.id.btn_ControlStartRecord:
			IPCMsgController.MsgControlStartRecord(destID, destPoint, "INPUT PHONE IME");
			break;
		case R.id.btn_ControlStopRecord:
			IPCMsgController.MsgControlStopRecord(destID, destPoint,
					"GET_SESSION_FROM_START_RECORD");
			break;
		case R.id.btn_ControlHistoryRecordProgress:
			IPCMsgController.MsgControlHistoryRecordProgress(destID, destPoint, "GET_SESSION_FROM_START_RECORD", 1474891743);
			break;
		case R.id.btn_NotifyHistoryRecordHeartbeat:
			IPCMsgController.MsgNotifyHistoryRecordHeartbeat(destID, destPoint, "GET_SESSION_FROM_START_RECORD");
			break;
		case R.id.btn_WulianBellQueryNotifyHeartBeat:
			IPCMsgController.MsgWulianBellQueryNotifyHeartBeat(destID, destPoint, 30);
			break;
		case R.id.btn_WulianBellQueryControlAppInitiativeHangup:
			IPCMsgController.MsgWulianBellQueryControlAppInitiativeHangup(
					destID, destPoint);
			break;
		case R.id.btn_WulianBellQueryDeviceConfigInformation:
			IPCMsgController.MsgWulianBellQueryDeviceConfigInformation(destID, destPoint);
			break;
		case R.id.btn_WulianBellQuerySetDeviceLanguage:
			IPCMsgController.MsgWulianBellQuerySetDeviceLanguage(destID, destPoint, 1);
			break;
		case R.id.btn_WulianBellQuerySetPIRConfigInformation:
			IPCMsgController.MsgWulianBellQuerySetPIRConfigInformation(destID, destPoint, 0, 10, 1, 1);
			break;
		default:
			break;
		}
 
	}
}
