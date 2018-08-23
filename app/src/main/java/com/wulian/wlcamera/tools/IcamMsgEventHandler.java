package com.wulian.wlcamera.tools;

import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface IcamMsgEventHandler {
    void onMessageEvent(IPCcameraXmlMsgEvent event);
}
