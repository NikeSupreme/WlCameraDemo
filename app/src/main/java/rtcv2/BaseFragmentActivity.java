/**
 * Project Name:  FamilyRoute
 * File Name:     BaseActivity.java
 * Package Name:  com.wulian.familyroute.view.base
 * Date:          2014-9-5
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package rtcv2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author Puml
 * @ClassName: BaseFragmentActivity
 * @Function: 带有基本功能的基类
 * @date: 2014-9-9
 * @email puml@wuliangroup.cn
 */
public class BaseFragmentActivity extends FragmentActivity {
	private Toast toast;
	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		if (toast == null) {
			toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		}
		setViewContent();
	}

	protected void setViewContent() {

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

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
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType,
                                 String xmlData, String from, String code) {

	}

}
