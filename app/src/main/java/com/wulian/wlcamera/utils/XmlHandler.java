package com.wulian.wlcamera.utils;

import com.wulian.wlcamera.bean.DeviceDetailMsg;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;


/**
 * Created by hxc on 2017/5/16.
 */

public class XmlHandler {
    /**
     * @return
     * @MethodName: getDeviceDetailMsg
     * @Function: 解析设备详细信息XML
     * @author: yuanjs
     * @date: 2015年10月19日
     * @email: jiansheng.yuan@wuliangroup.com
     */
    // XML模型：
    // <answer>
    // <cmd>00</cmd>
    // <seq>3</seq>
    // <uri>sip:cmic0365aea213cd8a3a@sh.gg</uri>
    // <model>ICAM-0001</model>
    // <version>V1.3.3</version>
    // <hardware>WL-ZNCD-3516-1-0.3</hardware>
    // <DPIs>320x240,640x480,1280x720</DPIs>
    // <wifi_ssid>TP-LINK_EGeeks</wifi_ssid>
    // <wifi_signal>100</wifi_signal>
    // <ip>192.168.1.101</ip>
    // <mac>ac:a2:13:cd:8a:3a</mac>
    // </answer>
    public static DeviceDetailMsg getDeviceDetailMsg(String xml) {
        DeviceDetailMsg deviceDetailMsg = null;
        StringReader xmlReader = new StringReader(xml);
        XmlPullParserFactory pullFactory;
        try {
            pullFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = pullFactory.newPullParser();
            xmlPullParser.setInput(xmlReader); // 保存创建的xml
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String localName = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        deviceDetailMsg = new DeviceDetailMsg();
                    }
                    break;
                    case XmlPullParser.START_TAG:
                        localName = xmlPullParser.getName();
                        if ("version".equalsIgnoreCase(localName)) {
                            deviceDetailMsg.setVersion(xmlPullParser.nextText());
                        } else if ("wifi_ssid".equalsIgnoreCase(localName)) {
                            deviceDetailMsg.setWifi_ssid(xmlPullParser.nextText()
                                    .trim());
                        } else if ("wifi_signal".equalsIgnoreCase(localName)) {
                            deviceDetailMsg
                                    .setWifi_signal(xmlPullParser.nextText());
                        } else if ("ip".equalsIgnoreCase(localName)) {
                            deviceDetailMsg.setWifi_ip(xmlPullParser.nextText());
                        } else if ("mac".equalsIgnoreCase(localName)) {
                            deviceDetailMsg.setWifi_mac(xmlPullParser.nextText());
                        }else if("DPIs".equalsIgnoreCase(localName)){
                            deviceDetailMsg.setDpis(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
            e.printStackTrace();
            deviceDetailMsg = null;
        } catch (IllegalArgumentException e) { // xmlSerializer.setOutput
            e.printStackTrace();
            deviceDetailMsg = null;
        } catch (IllegalStateException e) { // xmlSerializer.setOutput
            e.printStackTrace();
            deviceDetailMsg = null;
        } catch (Exception e) {
            e.printStackTrace();
            deviceDetailMsg = null;
        }
        return deviceDetailMsg;
    }

    public static String parseDeviceSipInfo(String xml, String keyWord) {
        String data = null;
        StringReader xmlReader = new StringReader(xml);
        XmlPullParserFactory pullFactory;
        try {
            pullFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = pullFactory.newPullParser();
            xmlPullParser.setInput(xmlReader); // 保存创建的xml
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String localName = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                    }
                    break;
                    case XmlPullParser.START_TAG:
                        localName = xmlPullParser.getName();
                        if (keyWord.equalsIgnoreCase(localName)) {
                            data = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
            e.printStackTrace();
        } catch (IllegalArgumentException e) { // xmlSerializer.setOutput
            e.printStackTrace();
        } catch (IllegalStateException e) { // xmlSerializer.setOutput
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
