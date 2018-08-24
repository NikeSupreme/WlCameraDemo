/**
 * Project Name:  WulianIPCRTCV2JarLib
 * File Name:     ReplayVideoActivity.java
 * Package Name:  com.wulian.sdk.android.ipc.rtcv2
 * @Date:         2017年2月6日
 * Copyright (c)  2017, wulian All Rights Reserved.
 */

package rtcv2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCCallStateMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCOnReceivedMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCVideoFrameMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgCallState;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgReceivedType;
import com.wulian.sdk.android.ipc.rtcv2.utils.IPCGetFrameFunctionType;
import com.wulian.webrtc.ViEAndroidGLES20;
import com.wulian.wlcamera.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

/**
 * @ClassName: ReplayVideoActivity
 * @Function: TODO
 * @Date: 2017年2月6日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ReplayVideoActivity extends Activity implements OnClickListener {

	private Button btn_QueryHistoryRecord;
	private Button btn_ControlStartRecord;
	private Button btn_ControlStopRecord;
	private Button btn_ControlHistoryRecordProgress;
	private Button btn_NotifyHistoryRecordHeartbeat;

	ViEAndroidGLES20 mViEAndroidGLES20;
	boolean isRec = false;
	boolean isVideoInvert = false;
	private String destID;
	private String destPoint;
	private String replay_session;

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCCallStateMsgEvent event) {
		MsgCallState callState = MsgCallState.getMsgCallState(event
				.getCallState());
		switch (callState) {
		case STATE_ESTABLISHED:
			break;
		case STATE_TERMINATED:
			break;
		case STATE_VIDEO_INCOMING:
			break;
		default:
			break;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCOnReceivedMsgEvent event) {
		switch (MsgReceivedType.getMsgReceivedTypeByID(event.getRtcType())) {
		case HANDLE_RTC_CALL_SPEED_TYPE:
			break;
		case HANDLE_RTC_CALL_DQ_TYPE:
			break;
		default:
			break;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCVideoFrameMsgEvent event) {
		Log.d("PML", "End time is:" + System.currentTimeMillis());
		IPCGetFrameFunctionType type = event.getType();
		switch (type) {
		case FRAME_MAIN_THUNBNAIL:
			break;
		case FRAME_PLAY_THUMBNAIL:
			break;

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replayvideo);
		initView();
		initData();
		setListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IPCController.setRender("", null);
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

	private void initView() {
		btn_QueryHistoryRecord = (Button) findViewById(R.id.btn_QueryHistoryRecord);
		btn_ControlStartRecord = (Button) findViewById(R.id.btn_ControlStartRecord);
		btn_ControlStopRecord = (Button) findViewById(R.id.btn_ControlStopRecord);
		btn_ControlHistoryRecordProgress = (Button) findViewById(R.id.btn_ControlHistoryRecordProgress);
		btn_NotifyHistoryRecordHeartbeat = (Button) findViewById(R.id.btn_NotifyHistoryRecordHeartbeat);
	}

	private void initData() {
		Bundle bd = getIntent().getExtras();
		if (bd == null) {
			Toast.makeText(this, "Bundle is Empty!", Toast.LENGTH_SHORT).show();
			return;
		}
		destID = bd.getString("DestID");
		destPoint = bd.getString("DestPoint");
		if (TextUtils.isEmpty(destID) || TextUtils.isEmpty(destPoint)) {
			Toast.makeText(this, "Please input correct ID and Point!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		mViEAndroidGLES20 = (ViEAndroidGLES20) findViewById(R.id.view_video);
		IPCController.setRender("", mViEAndroidGLES20);
	}

	private void setListener() {
		btn_QueryHistoryRecord.setOnClickListener(this);
		btn_ControlStartRecord.setOnClickListener(this);
		btn_ControlStopRecord.setOnClickListener(this);
		btn_ControlHistoryRecordProgress.setOnClickListener(this);
		btn_NotifyHistoryRecordHeartbeat.setOnClickListener(this);
	}

	// Just Test. Get the Device only Serial
	String getuniqueId() {
		TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		String imei = tm.getDeviceId();
		String simSerialNumber = tm.getSimSerialNumber();
		String androidId = android.provider.Settings.Secure.getString(
				getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) imei.hashCode() << 32) | simSerialNumber.hashCode());
		String uniqueIuniqueId = deviceUuid.toString();
		return uniqueIuniqueId;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_QueryHistoryRecord:
			IPCMsgController.MsgQueryHistoryRecord(destID, destPoint);
			break;
		case R.id.btn_ControlStartRecord:
			String uniqueIuniqueId = getuniqueId();
			if (TextUtils.isEmpty(uniqueIuniqueId)) {
				// Wrong! It should not be happen.
			} else {
				IPCMsgController.MsgControlStartRecord(destID, destPoint,
						uniqueIuniqueId);
			}
			break;
		case R.id.btn_ControlStopRecord:
			if (TextUtils.isEmpty(replay_session)) {
				// Wrong! It should not be happen.
			} else {
				IPCMsgController.MsgControlStopRecord(destID, destPoint,
						replay_session);
			}
			break;
		case R.id.btn_ControlHistoryRecordProgress:
			if (TextUtils.isEmpty(replay_session)) {
				// Wrong! It should not be happen.
			} else {
				IPCMsgController.MsgControlHistoryRecordProgress(destID,
						destPoint, replay_session, 1474891743);
			}
			break;
		case R.id.btn_NotifyHistoryRecordHeartbeat:
			IPCMsgController.MsgNotifyHistoryRecordHeartbeat(destID, destPoint,
					replay_session);
			break;
		default:
			break;
		}
	}
}
