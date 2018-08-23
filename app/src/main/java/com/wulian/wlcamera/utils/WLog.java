package com.wulian.wlcamera.utils;


/**
 * Created by Veev on 2017/4/14
 * Tel: 18365264930
 * QQ: 2355738466
 * Function: 替换 android.util.WLog
 */
public class WLog {
    /**
     * isPrint: print switch, true will print. false not print
     */
    private static boolean isPrint = true;
    private static final String defaultTag = "WuLian";

    private WLog() {
    }

    public static int i(Object o) {
        return isPrint && o != null ? android.util.Log.i(defaultTag, o.toString()) : -1;
    }

    public static int i(String m) {
        return isPrint && m != null ? android.util.Log.i(defaultTag, m) : -1;
    }

    /**
     * ******************** WLog json **************************
     */
    public static int json(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.i(tag, format(msg)) : -1;
    }

    public static String format(String jsonStr) {
        int level = 0;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == buffer.charAt(buffer.length() - 1)) {
                buffer.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    buffer.append(c).append("\n");
                    level++;
                    break;
                case ',':
                    buffer.append(c).append("\n");
                    break;
                case '}':
                case ']':
                    buffer.append("\n");
                    level--;
                    buffer.append(getLevelStr(level));
                    buffer.append(c);
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }

        return buffer.toString();

    }

    private static String getLevelStr(int level) {
        StringBuilder buffer = new StringBuilder();
        for (int levelI = 0; levelI < level; levelI++) {
            buffer.append("\t");
        }
        return buffer.toString();
    }

    /**
     * ******************** WLog **************************
     */
    public static int v(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.v(tag, msg) : -1;
    }

    public static int d(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.d(tag, msg) : -1;
    }

    public static int i(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.i(tag, msg) : -1;
    }

    public static int w(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.w(tag, msg) : -1;
    }

    public static int e(String tag, String msg) {
        return isPrint && msg != null ? android.util.Log.e(tag, msg) : -1;
    }

    /**
     * ******************** WLog with object list **************************
     */
    public static int v(String tag, Object... msg) {
        return isPrint ? android.util.Log.v(tag, getLogMessage(msg)) : -1;
    }

    public static int d(String tag, Object... msg) {
        return isPrint ? android.util.Log.d(tag, getLogMessage(msg)) : -1;
    }

    public static int i(String tag, Object... msg) {
        return isPrint ? android.util.Log.i(tag, getLogMessage(msg)) : -1;
    }

    public static int w(String tag, Object... msg) {
        return isPrint ? android.util.Log.w(tag, getLogMessage(msg)) : -1;
    }

    public static int e(String tag, Object... msg) {
        return isPrint ? android.util.Log.e(tag, getLogMessage(msg)) : -1;
    }

    private static String getLogMessage(Object... msg) {
        if (msg != null && msg.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object s : msg) {
                if (s != null) {
                    sb.append(s.toString());
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * ******************** WLog with Throwable **************************
     */
    public static int v(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? android.util.Log.v(tag, msg, tr) : -1;
    }

    public static int d(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? android.util.Log.d(tag, msg, tr) : -1;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? android.util.Log.i(tag, msg, tr) : -1;
    }

    public static int w(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? android.util.Log.w(tag, msg, tr) : -1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return isPrint && msg != null ? android.util.Log.e(tag, msg, tr) : -1;
    }

    /**
     * ******************** TAG use Object Tag **************************
     */
    public static int v(Object tag, String msg) {
        return isPrint ? android.util.Log.v(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int d(Object tag, String msg) {
        return isPrint ? android.util.Log.d(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int i(Object tag, String msg) {
        return isPrint ? android.util.Log.i(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int w(Object tag, String msg) {
        return isPrint ? android.util.Log.w(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int e(Object tag, String msg) {
        return isPrint ? android.util.Log.e(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static String byte2hex(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String temp = Integer.toHexString(0xFF & data[i]);
            if (temp.length() == 1) {
                hexString.append("0");
                hexString.append(temp);
            } else if (temp.length() == 2) {
                hexString.append(temp);
            }
        }
        return hexString.toString();
    }
}
