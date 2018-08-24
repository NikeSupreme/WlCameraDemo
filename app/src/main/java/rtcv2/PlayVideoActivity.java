/**
 * Project Name:  WulianIPCRTCV2JarLib
 * File Name:     PlayVideoActivity.java
 * Package Name:  com.wulian.sdk.android.ipc.rtcv2
 * @Date:         2017年2月6日
 * Copyright (c)  2017, wulian All Rights Reserved.
 */

package rtcv2;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCCallStateMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCOnReceivedMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCVideoFrameMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgCallState;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgDPIType;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgReceivedType;
import com.wulian.sdk.android.ipc.rtcv2.utils.IPCGetFrameFunctionType;
import com.wulian.webrtc.ViEAndroidGLES20;
import com.wulian.wlcamera.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @ClassName: PlayVideoActivity
 * @Function: TODO
 * @Date: 2017年2月6日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class PlayVideoActivity extends Activity implements OnClickListener {
	private Button btn_VideoInvert;
	private Button btn_MakeCall;
	private Button btn_getRenderFrame;
	private Button btn_DQ;
	private Button btn_getSpeed;
	private Button btn_CloseVideo;

	private Button btn_Play_And_Record_SND;
	private Button btn_Stop_Play_And_Record_SND;
	private Button btn_SendInfo_Play_SND;
	private Button btn_SendInfo_Stop_play_SND;
	private Button btn_SendInfo_Stop_record_SND;
	private Button btn_SendInfo_Set_voice_mute_SND;
	private Button btn_SendInfo_Record_SND;

	private Button btn_SendMessage_Low_DPI;
	private Button btn_SendMessage_Middle_DPI;
	private Button btn_SendMessage_HIGH_DPI;
	private Button btn_SendMessage_SUPER_DPI;

	private ImageView iv_getPicture;
	private ViewGroup mainview;

	// ViEAndroidGLES20 mViEAndroidGLES20;
	// 参考了Webrtc的ViEAndroidGLES20 代码如下：
	// https://github.com/Cacifer/webrtcForAndroid/blob/master/src/org/webrtc/videoengine/ViEAndroidGLES20.java
	ViEAndroidGLES20 mViEAndroidGLES20;
	boolean isRec = false;
	boolean isVideoInvert = false;

	private String destID;
	private String destPoint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playvideo);
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

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCcameraXmlMsgEvent event) {
		Toast.makeText(this,
				event.getApiType().name() + "\n" + event.getMessage(),
				Toast.LENGTH_SHORT).show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCVideoFrameMsgEvent event) {
		Log.d("PML", "End time is:" + System.currentTimeMillis());
		switch (event.getType()) {
		case FRAME_MAIN_THUNBNAIL:
			if (event.getmVideoBitmap() == null) {
				Log.d("PML", "抓拍图片为空");
			} else {
				Log.d("PML", "抓拍图片不为空");
			}
			iv_getPicture.setImageBitmap(event.getmVideoBitmap());
			break;

		default:
			break;
		}

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCCallStateMsgEvent event) {
		Log.d("PML", "End time is:" + System.currentTimeMillis());
		MsgCallState callState = MsgCallState
				.getMsgCallState(event.getCallState());
		switch (callState) {
		case STATE_ESTABLISHED:
			Log.d("PML", "STATE_ESTABLISHED P2P established");
			break;
		case STATE_TERMINATED:
			Log.d("PML", "STATE_TERMINATED close");
			break;
		case STATE_VIDEO_INCOMING:
			Log.d("PML", "STATE_VIDEO_INCOMING video picture incoming");
			break;
		default:
			Log.d("PML", callState.name());
			break;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(IPCOnReceivedMsgEvent event) {
		Log.d("PML", "End time is:" + System.currentTimeMillis());
		switch (MsgReceivedType.getMsgReceivedTypeByID(event.getRtcType())) {
		case HANDLE_RTC_CALL_DQ_TYPE:
			Toast.makeText(this, "DQ info is:" + event.getMessage(),
					Toast.LENGTH_SHORT).show();
			break;
		case HANDLE_RTC_CALL_SPEED_TYPE:
			Toast.makeText(this, "SPEED info is:" + event.getMessage(),
					Toast.LENGTH_SHORT).show();
			break;
		case HANDLE_RTC_AUDIO_PLAYER_EXCEPTION_TYPE:
			Log.d("PML", "RTC Audio Player info is:" + event.getMessage());
			break;
		case HANDLE_RTC_AUDIO_RECORDER_EXCEPTION_TYPE:
			Log.d("PML", "RTC Audio Recorder info is:" + event.getMessage());
			break;
		case HANDLE_RTC_VIDEO_DPI_TYPE:
			int DPITYPE = -1;
			try {
				DPITYPE = Integer.parseInt(event.getMessage());
			} catch (NumberFormatException e) {
				DPITYPE = -1;
			}
			String DPI_INFO = "UNKNOWN";
			MsgDPIType dpi = MsgDPIType.getMsgDPIType(DPITYPE);
			switch (dpi) {
			case IPC_RTC_LOW_DPI:
				DPI_INFO = "320x240";
				break;
			case IPC_RTC_MIDDLE_DPI:
				DPI_INFO = "640x480";
				break;
			case IPC_RTC_HIGH_DPI:
				DPI_INFO = "1280x720";
				break;
			case IPC_RTC_SUPER_DPI:
				DPI_INFO = "1920x1080";
				break;
			case IPC_RTC_UNKNOWN_DPI:
				DPI_INFO = "UNKNOWN";
				break;
			default:
				break;
			}
			Toast.makeText(this, "VIDEO info is:" + DPI_INFO,
					Toast.LENGTH_SHORT).show();
			break;
		case HANDLE_RTC_NULL_TYPE:
			Log.d("PML", "NULL info is:" + event.getMessage());
			break;
		default:
			break;
		}
	}

	private void initView() {
		mainview = (ViewGroup) findViewById(R.id.mainview);
		iv_getPicture = (ImageView) findViewById(R.id.iv_getPicture);

		btn_VideoInvert = (Button) findViewById(R.id.btn_VideoInvert);
		btn_MakeCall = (Button) findViewById(R.id.btn_MakeCall);
		btn_getRenderFrame = (Button) findViewById(R.id.btn_getRenderFrame);
		btn_DQ = (Button) findViewById(R.id.btn_DQ);
		btn_getSpeed = (Button) findViewById(R.id.btn_getSpeed);
		btn_CloseVideo = (Button) findViewById(R.id.btn_CloseVideo);
		btn_Play_And_Record_SND = (Button) findViewById(
				R.id.btn_Play_And_Record_SND);
		btn_Stop_Play_And_Record_SND = (Button) findViewById(
				R.id.btn_Stop_Play_And_Record_SND);
		btn_SendInfo_Play_SND = (Button) findViewById(
				R.id.btn_SendInfo_Play_SND);
		btn_SendInfo_Record_SND = (Button) findViewById(
				R.id.btn_SendInfo_Record_SND);
		btn_SendInfo_Stop_play_SND = (Button) findViewById(
				R.id.btn_SendInfo_Stop_play_SND);
		btn_SendInfo_Stop_record_SND = (Button) findViewById(
				R.id.btn_SendInfo_Stop_record_SND);
		btn_SendInfo_Set_voice_mute_SND = (Button) findViewById(
				R.id.btn_SendInfo_Set_voice_mute_SND);

		btn_SendMessage_Low_DPI = (Button) findViewById(
				R.id.btn_SendMessage_Low_DPI);
		btn_SendMessage_Middle_DPI = (Button) findViewById(
				R.id.btn_SendMessage_Middle_DPI);
		btn_SendMessage_HIGH_DPI = (Button) findViewById(
				R.id.btn_SendMessage_HIGH_DPI);
		btn_SendMessage_SUPER_DPI = (Button) findViewById(
				R.id.btn_SendMessage_SUPER_DPI);
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
		mViEAndroidGLES20 = new ViEAndroidGLES20(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1080, 1080 * 9 /16);

		// lp.addRule(LinearLayout.CENTER_IN_PARENT);// 全尺寸时居中显示
		mainview.addView(mViEAndroidGLES20, 0, lp);
		mViEAndroidGLES20.setKeepScreenOn(true);
		// mViEAndroidGLES20 = (ViEAndroidGLES20) findViewById(R.id.view_video);
		IPCController.setRender("", mViEAndroidGLES20);
		// Here,you Can call the makeCall method.
	}

	private void setListener() {
		btn_VideoInvert.setOnClickListener(this);
		btn_MakeCall.setOnClickListener(this);
		btn_getRenderFrame.setOnClickListener(this);
		btn_DQ.setOnClickListener(this);
		btn_getSpeed.setOnClickListener(this);
		btn_CloseVideo.setOnClickListener(this);
		btn_Play_And_Record_SND.setOnClickListener(this);
		btn_Stop_Play_And_Record_SND.setOnClickListener(this);
		btn_SendInfo_Play_SND.setOnClickListener(this);
		btn_SendInfo_Record_SND.setOnClickListener(this);
		btn_SendInfo_Stop_play_SND.setOnClickListener(this);
		btn_SendInfo_Stop_record_SND.setOnClickListener(this);
		btn_SendInfo_Set_voice_mute_SND.setOnClickListener(this);

		btn_SendMessage_Low_DPI.setOnClickListener(this);
		btn_SendMessage_Middle_DPI.setOnClickListener(this);
		btn_SendMessage_HIGH_DPI.setOnClickListener(this);
		btn_SendMessage_SUPER_DPI.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_VideoInvert:
			isVideoInvert = !isVideoInvert;
			IPCController.setVideoPictureInvert("", isVideoInvert);
			break;
		case R.id.btn_MakeCall:
			IPCResultCallBack makeCallAsyncCallback = new IPCResultCallBack() {
				@Override
				public void getResult(int result) {
					Log.d("pml", "makeCallAsyncCallback result is:"
							+ (result == 0 ? "TRUE" : "FALSE"));
				}
			};
			IPCController.makeCallAsync(makeCallAsyncCallback, destID,
					destPoint);
			break;
		case R.id.btn_getRenderFrame:
			Log.d("PML", "Begin time is:" + System.currentTimeMillis());
	/*		((ViEAndroidGLES20) mViEAndroidGLES20).getRenderFrame(
						IPCGetFrameFunctionType.FRAME_MAIN_THUNBNAIL);*/		
		IPCController.getRenderFrame("hello", IPCGetFrameFunctionType.FRAME_MAIN_THUNBNAIL);
			break;
		case R.id.btn_DQ:
			IPCController.getCallDqInfo();
			break;
		case R.id.btn_getSpeed:
			IPCController.getCallSpeedInfo();
			break;
		case R.id.btn_Play_And_Record_SND:
			IPCController.playAndRecordAudioAsync(null);
			break;
		case R.id.btn_Stop_Play_And_Record_SND:
			IPCController.stopPlayAndRecordAudioAsync(null);
			break;
		case R.id.btn_CloseVideo:
			IPCController.closeAllVideoAsync(null);
			break;
		case R.id.btn_SendInfo_Play_SND:
			IPCController.playAudioAsync(null);
			//IPCController.playAudio();
			break;
		case R.id.btn_SendInfo_Record_SND:
			IPCController.recordAudioAsync(null);
			break;
		case R.id.btn_SendInfo_Stop_play_SND:
			IPCController.stopPlayAudioAsync(null);
			break;
		case R.id.btn_SendInfo_Stop_record_SND:
			IPCController.stopRecordAudioAsync(null);
			break;
		case R.id.btn_SendInfo_Set_voice_mute_SND:
			IPCMsgController.InfoConfigVoiceMute(false);
			break;
		case R.id.btn_SendMessage_Low_DPI:
			IPCMsgController.MsgConfigEncode(destID, destPoint, 0);
			break;
		case R.id.btn_SendMessage_Middle_DPI:
			IPCMsgController.MsgConfigEncode(destID, destPoint, 1);
			break;
		case R.id.btn_SendMessage_HIGH_DPI:
			IPCMsgController.MsgConfigEncode(destID, destPoint, 2);
			break;
		case R.id.btn_SendMessage_SUPER_DPI:
			IPCMsgController.MsgConfigEncode(destID, destPoint, 3);
			break;
		default:
			break;
		}

	}
}
