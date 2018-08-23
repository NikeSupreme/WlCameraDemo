package com.wulian.wlcamera.bean;

import java.io.Serializable;

/**
 * Created by zbl on 2017/5/11.
 * 爱看设备bean
 */

public class ICamDeviceBean implements Serializable {
    public String shares;
    public String nick;
    public long updated;
    public String location;
    public String description;
    public String did;//"cmic08a750294d412fce",//和云通信的id
    public String sdomain;
    public int protect;
    public int online;
    public String version;
    public String type;
    public int isRtmp;//是否开启极速模式;
    public String uniqueDeviceId;//一物一码唯一id,和爱看侧通信的id
}
