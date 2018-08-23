package com.wulian.wlcamera.utils;

import java.util.Locale;


/**
 * About Language
 */
public class LanguageUtil {
    public static final String LANGUAGE_EN_US = "en-US";
    public static final String LANGUAGE_ZH_CN = "zh-CN";
    public static final String LANGUAGE_ZH_TW = "zh-TW";

    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_ZH = "zh";

    public static final String COUNTRY_US = "US";
    public static final String COUNTRY_CN = "CN";
    public static final String COUNTRY_TW = "TW";
    public static final String COUNTRY_HK = "HK";

    public static String getLocaleLanguage() {
        Locale l = Locale.getDefault();
        return String.format("%s-%s", l.getLanguage(), l.getCountry());
    }

    public static String getLanguage() {
        Locale l = Locale.getDefault();
        return String.format("%s", l.getLanguage());
    }

    public static String getCountry() {
        Locale l = Locale.getDefault();
        return String.format("%s", l.getCountry());
    }

    public static boolean isEnglish() {
        return getLanguage().equals(LANGUAGE_EN);
    }

    public static boolean isChina() {
        return LANGUAGE_ZH.equals(getLanguage()) && COUNTRY_CN.equals(getCountry());
    }

    public static boolean isAllChina() {
        if (isTaiWan() || isChina() || isHongKong()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isHongKong() {
        return LANGUAGE_ZH.equals(getLanguage()) && COUNTRY_HK.equals(getCountry());
    }

    public static boolean isTaiWan() {
        return getCountry().equals(COUNTRY_TW);
    }

    // for fix ifly locale set
    public static Locale getCurrentLocale() {
        Locale locale;
        if (isEnglish()) {
            locale = Locale.ENGLISH;
        } else if (isTaiWan()) {
            locale = Locale.TRADITIONAL_CHINESE;
        } else {
            locale = Locale.CHINESE;
        }
        return locale;
    }

}