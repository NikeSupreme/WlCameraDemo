package com.wulian.wlcamera.bean;

import java.io.Serializable;

/**
 * created by huxc  on 2017/10/31.
 * func：
 * email: hxc242313@qq.com
 */

public class LanguageVolumeBean implements Serializable {
    private int volume = -1 ;
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
