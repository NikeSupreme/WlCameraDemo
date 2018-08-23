package com.wulian.wlcamera.utils;

public class ConstantsUtil {
	public static final int GATEWAY_ID_LENGTH = 12;
	
	public static final int GATEWAY_MD5_PASSWDLENGTH = 32;

	public static final int CONFIG_WIRED_THIRD_BIND_SETTING = 1;//户外摄像机有线三方绑定
	public static final int CONFIG_DIRECT_WIFI_THIRD_BIND_SETTING =2;// Wi-Fi直连三方绑定方式

	public static final String DEVICE_SCAN_ENTRY = "0";//总设备添加扫描入口
	public static final String CAMERA_ADD_ENTRY ="1";//单独的设备添加扫描入口
	public static final String BIND_CAMERA_GATEWAY_ENTRY = "2";//账号体系下绑定网关入口配网模式
	public static final String GATEWAY_LOGIN_ENTRY = "3";//网关登录扫描入口
	public static final String DEVICE_SETTING_ENTRY = "4";//设置页配置WiFi入口
	public static final String CYLINCAME_SETTING_ENTRY = "5";//小物摄像机设置页配置WiFi入口

	public static final String QR_CODE = "QR_CODE"; // 二维码
	public static final String GET_QR_CODE = "GET_QR_CODE"; // 获取二维码

	public static final String MINI_GATEWAY_LOGIN_ENTRY = "7";//mini网关局域网登录扫码入口
	public static final String MINI_GATEWAY_LIST_ENTRY = "8";//mini网关账号下添加网关扫码入口

	public static final String NEED_JUMP_BIND_GATEWAY_FLAG = "9";//摄像机账号下从总设备添加扫描入口作为扫描网关使用需跳转绑定网关界面

	public static  boolean START_PROTECT_AREA = false;

}