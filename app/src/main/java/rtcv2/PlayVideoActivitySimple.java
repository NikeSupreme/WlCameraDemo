/**
 * * Project Name:  iCam
 * File Name:     AddDeviceActivity.java
 * Package Name:  com.wulian.icam.view.device
 *
 * @Date: 2014年10月21日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package rtcv2;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
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


/**
 * @author Wangjj
 * @ClassName: PlayVideoActivity
 * @Function: 视频播放页
 * @Date: 2014年10月21日
 * @email wangjj@wuliangroup.cn
 */
public class PlayVideoActivitySimple extends Activity implements OnClickListener {
	private ImageView iv_cap_gallery;

	private String destID;
	private String destPoint;

	private RelativeLayout rl_video;// 竖屏宿主

	private Button btn_snapshot_new;
	ViEAndroidGLES20 cameraPreview;

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
			iv_cap_gallery.setImageBitmap(event.getmVideoBitmap());
			break;

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video_simple);
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

		iv_cap_gallery = (ImageView) findViewById(R.id.iv_cap_gallery);
		btn_snapshot_new = (Button) findViewById(R.id.btn_snapshot_new);
		cameraPreview = (ViEAndroidGLES20) findViewById(R.id.view_video);
		btn_snapshot_new.setOnClickListener(this);
		IPCController.setRender("", cameraPreview);
		IPCController.makeCallAsync(null, destID, destPoint);
	}

	@Override
	protected void onStop() {
		super.onStop();
		hangUpVideo();
	}

	private void hangUpVideo() {
		IPCController.closeAllVideoAsync(null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		detachVideoPreview();
	}

	private void detachVideoPreview() {
		IPCController.setRender("", null);
		if (rl_video != null && cameraPreview != null) {
			rl_video.removeView(cameraPreview);
		}
		if (cameraPreview != null) {
			cameraPreview = null;
		}
	}

	@Override
	public void onClick(View v) {
		Log.d("PML",
				"onClickonClickonClickonClickonClickonClickonClickonClickonClickonClickonClick");
		int id = v.getId();
		if (id == R.id.btn_snapshot_new) {
			Log.d("PML", "btn_snapshot_new");
			cameraPreview
					.getRenderFrame(IPCGetFrameFunctionType.FRAME_PLAY_THUMBNAIL);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

}
