package com.wulian.wlcamera.bean;

import android.content.Context;

import com.wulian.wlcamera.MainApplication;
import com.wulian.wlcamera.R;


/**
 * Created by huxc on 2017/6/8.
 * 清晰度
 */

public class DefinitionBean {
    public int value;
    public String name;
    public Context context;

    public DefinitionBean(Context context,int value) {
        this.value = value;
        this.name = getNameResByValue(context,value);
        this.context = context;
    }

    public static String getNameResByValue(Context context, int value) {
        if (value == 1) {
            return context.getString(R.string.Standard_Definition);
        } else if (value == 2) {
            return context.getString(R.string.High_Definition);
        } else if (value == 3) {
            return context.getString(R.string.Super_Definition);
        } else return "";
    }
}
