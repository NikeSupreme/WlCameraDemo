package com.wulian.wlcamera.utils;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;


import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.ZoneBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class CameraUtil {

    public static String getParamFromXml(String xmlString, String param) {
        // \\w+不能匹配local_mac中的':',如00:11:22
        Pattern p = Pattern.compile("<" + param + ">(.+)</" + param + ">");
        Matcher matcher = p.matcher(xmlString);
        if (matcher.find())
            return matcher.group(1).trim();
        return "";
    }

    public static String convertFileSize(long sizeInB) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (sizeInB >= gb) {
            return String.format("%.1f GB", (float) sizeInB / gb);
        } else if (sizeInB >= mb) {
            float f = (float) sizeInB / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (sizeInB >= kb) {
            float f = (float) sizeInB / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", sizeInB);
    }
    /* <area id="0">105,41,164,155</area> */

    /**
     * @param xmlString xml字符串
     * @param maxCount  监测个数上限
     * @return
     * @Function 解析移动侦测区域
     * @author Wangjj
     * @date 2014年12月26日
     */
    public static String[] getMotionArea(String xmlString, int maxCount) {
        ArrayList<String> results = new ArrayList<String>();
        Pattern p = null;
        Matcher matcher = null;
        for (int i = 0; i < maxCount; i++) {
            p = Pattern.compile("<area id=\"" + i + "\">(.+)</area>");
            matcher = p.matcher(xmlString);
            if (matcher.find()) {
                results.add(matcher.group(1).trim());
            }
        }
        String[] areas = new String[results.size()];
        for (int i = 0; i < areas.length; i++) {
            areas[i] = results.get(i);
        }
        return areas;
    }

    public static String convertWeekday(Context context, String numbers) {
        // 7,1,2,3,4,5,6, ->每天 everyday
        // 1,2,3,4,5, ->工作日 workdayzhou
        // 2,4, -> 周二,周四 Tue,Thus

        if ("7,1,2,3,4,5,6,".equals(numbers) || "1,2,3,4,5,6,7,".equals(numbers)) {
            return context.getString(R.string.Everyday);
        }
        if ("1,2,3,4,5,".equals(numbers)) {

            return context.getString(R.string.Workdays);
        }
        if (TextUtils.isEmpty(numbers) || ",".equals(numbers)) {
            // 默认值为 ""
            // 网络同步下来 但没有日期数据，解析后为""
            // 设置日期 但未选中时候 为","
            return "";
        } else {
            return context.getString(R.string.Workdays);
        }
    }

    /**
     * @return
     * @Function 转换星期
     * @author Wangjj
     * @date 2015年2月5日
     */
    public static String getWeekFromXmlData(String xmlData) {
        // String weekdays[] = { "Sun", "Mon", "Tues", "Wed", "Thurs", "Fri",
        // "Sat" };
        StringBuilder sbWeek = new StringBuilder();

        if (xmlData.contains("day=\"Sun\"")) {
            sbWeek.append("7,");
        }
        if (xmlData.contains("day=\"Mon\"")) {
            sbWeek.append("1,");
        }
        if (xmlData.contains("day=\"Tues\"")) {
            sbWeek.append("2,");
        }
        if (xmlData.contains("day=\"Wed\"")) {
            sbWeek.append("3,");
        }
        if (xmlData.contains("day=\"Thurs\"")) {
            sbWeek.append("4,");
        }
        if (xmlData.contains("day=\"Fri\"")) {
            sbWeek.append("5,");
        }
        if (xmlData.contains("day=\"Sat\"")) {
            sbWeek.append("6,");
        }
        return sbWeek.toString();
    }

    public static String convertTime(Context context, String times) {
        if (TextUtils.isEmpty(times)) {
            return "";
        } else {
            String timeNums[] = times.split(",");
            int first = Integer.parseInt(timeNums[0]);
            int second = Integer.parseInt(timeNums[1]);
            int third = Integer.parseInt(timeNums[2]);
            int fourth = Integer.parseInt(timeNums[3]);
            if (timeNums.length == 4) {
                if (first < third) {
                    formatSingleNum(timeNums);
                    return timeNums[0] + ":" + timeNums[1] + "-" + timeNums[2]
                            + ":" + timeNums[3];
                } else if (first == third && second < fourth) {
                    formatSingleNum(timeNums);
                    return timeNums[0] + ":" + timeNums[1] + "-" + timeNums[2]
                            + ":" + timeNums[3];
                } else {
                    formatSingleNum(timeNums);
                    return timeNums[0]
                            + ":"
                            + timeNums[1]
                            + "-"
                            + context.getString(R.string.Next_Day) + " "
                            + timeNums[2] + ":" + timeNums[3];
                }
            } else {
                return "";
            }
        }
    }

    /**
     * @param in
     * @return
     * @Function 将3转成03
     * @author Wangjj
     * @date 2015年5月28日
     */

    public static String fillZeroBeforeSingleNum(String in) {
        if (TextUtils.isEmpty(in)) {
            return "";
        }
        return in.length() == 1 ? "0" + in : in;
    }

    /**
     * @param ins
     * @Function 将数组里的所有3转成03
     * @author Wangjj
     * @date 2015年5月28日
     */

    public static void formatSingleNum(String[] ins) {
        for (int i = 0; i < ins.length; i++) {
            ins[i] = fillZeroBeforeSingleNum(ins[i]);
        }
    }

    /**
     * 本地判断摄像机类型（包括一物一码的摄像机）
     *
     * @param id
     * @return
     */
    public static String getTypeByDeviceId(String id) {
        String type = "";
        if (id.startsWith("cmic07") || id.startsWith("cmic05") || id.startsWith("cmic03") || id.startsWith("CG07")) {
            type = "CMICA2";
        } else if (id.startsWith("cmic08")
                || id.startsWith("AV08")
                || id.startsWith("av08")) {
            type = "CMICA1";
        } else if (id.startsWith("cmic20") || id.startsWith("cmic04") || id.startsWith("CG20")) {
            type = "CMICA3";
        } else if (id.startsWith("cmic06") || id.startsWith("CG06")) {
            type = "CMICA4";
        } else if (id.startsWith("cmic02") || id.startsWith("CG02")) {
            type = "CMICA5";
        } else if (id.startsWith("cmic21") || id.startsWith("CG21")) {
            type = "CMICA6";
        }
        return type;
    }

    /**
     * 本地判断是否是摄像机网关（包括一物一码的网关）
     *
     * @param id
     * @return
     */
    public static boolean isCameraGateway(String id) {
        if (id.startsWith("cmic02") || id.startsWith("cmic03")
                || id.startsWith("cmic04") || id.startsWith("cmic05")
                || id.startsWith("cmic06") || id.startsWith("cmic07")
                || id.startsWith("cmic20") || id.startsWith("CG02")
                || id.startsWith("CG06") || id.startsWith("CG07")
                || id.startsWith("CG20") || id.startsWith("CG21")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取摄像机和网关时区
     *
     * @param context
     * @return
     */
    public static List<ZoneBean> getZoneDataFromJson(Context context) {
        ArrayList<ZoneBean> list = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open("WLTimeZone.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            org.json.JSONObject jsonObject = new org.json.JSONObject(builder.toString());//builder读取了JSON中的数据。
            //直接传入JSONObject来构造一个实例
            org.json.JSONArray array = jsonObject.getJSONArray("zone");         //从JSONObject中取出数组对象
            for (int i = 0; i < array.length(); i++) {
                ZoneBean bean = new ZoneBean();
                org.json.JSONObject jsonObject1 = array.getJSONObject(i);    //取出数组中的对象
                bean.cn = jsonObject1.getString("cn");
                bean.en = jsonObject1.getString("en");
                bean.zone = jsonObject1.getString("zone");
                list.add(i, bean);
            }//

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据系统语言显示摄像机或网关时区的中文或者英文名称
     *
     * @return
     */
    public static String getZoneNameByLanguage(Context context, String zoneName) {
        List<ZoneBean> list = CameraUtil.getZoneDataFromJson(context);
        String name = null;
        for (ZoneBean bean :
                list) {
            if (TextUtils.equals(bean.en, zoneName)) {
                if (LanguageUtil.isChina()) {
                    name = bean.cn;
                } else {
                    name = bean.en;
                }
            }

        }
        if (TextUtils.equals(zoneName, "Hong_Kong")) {
            if (LanguageUtil.isChina()) {
                name = "香港";
            } else {
                name = "Hong_Kong";
            }
        }
        return name;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    private static volatile boolean hasVideoActivityRunning = false;

    public static void setHasVideoActivityRunning(boolean flag) {
        hasVideoActivityRunning = flag;
    }

}
