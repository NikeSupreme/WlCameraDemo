package com.wulian.wlcamera.utils;

import android.content.Context;
import android.text.format.Time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtil
{
	public static final long MILLI_SECONDS_OF_DAY = 86400000;
	private static ThreadLocal<SimpleDateFormat> hhmiss = new ThreadLocal<SimpleDateFormat>(){
		@Override
		public SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		}
	};
	
	public static Date getDateBefore(int i) {
		return getDateBefore(new Date(), i);
	}
	
	public static Date getDateBefore(Date d, int i) {
		return new Date(d.getTime() - i*DateUtil.MILLI_SECONDS_OF_DAY);
	}

	/**
	 * 该日期是未来
	 */
	public static boolean isFutureDay(int y, int m, int d) {
		Calendar c = Calendar.getInstance();
		c.set(y, m, d);
		return c.getTimeInMillis() > now();
	}

	/**
	 * 获得当前时间的毫秒数
	 * @return
	 */
	public static final long now() {
		return new Date().getTime();
	}
	
	/**
	 * 判断时间戳是否在当前时间的milis毫秒内，主要用来检查超时
	 * @param timestamp
	 * @param milis
	 * @return
	 */
	public static final boolean inXXXMilis(long timestamp, long milis) {
		return now() - timestamp <= milis;
	}
	
	/**
	 * "yyyy-MM-dd HH.mm.ss"
	 */
	public static String getFormatIMGTime( long time ){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		return format.format(new Date(time));
	}

	/**
	 * "yyyy.MM.dd"
	 */
	public static String getFormatShortTime( long time ){
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		return format.format(new Date(time));
	}

	/**
	 * "MM-dd HH.mm"
	 */
	public static String getFormatMiddleTime( long time ) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(new Date(time));
	}
	/**
	 * "dd"
	 */
	public static String getFormatShortDay( TimeZone zone, long millis ){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("dd");
		sDateFormat.setTimeZone(zone);
		return sDateFormat.format(new Date(millis));
	}

	public static String getFormatSimpleDate( Date d ){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return sDateFormat.format(d);
	}
	/**
	 * "HH:mm:ss"
	 */
	public static String getFormatTime( TimeZone zone, long millis ){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss");
		sDateFormat.setTimeZone(zone);
		return sDateFormat.format(new Date(millis));
	}
	
	
	public static String getCurrentHHMMSS() {
		return hhmiss.get().format(now());
	}
	
	public static Date getDate0H0M0S(Date datetime){
		SimpleDateFormat selectDayFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		Date selectDay = null;
		try {
			selectDay = selectDayFormat.parse(getFormatSimpleDate(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return selectDay;
	}
	

	/**
	 *  will be yyyy-MM-dd HH.mm.ss into "yyyy-MM-dd" 13 bit time stamp
	 */
	public static long getTime0H0M0S(Date datetime){
		return getDate0H0M0S(datetime).getTime();
	}
	
//	/**
//	 * "HH:mm:ss"
//	 */
//	public static String getFormatSimpleTime( long millis ){
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss");
//		return sDateFormat.format(new Date(millis));
//	}

	public static long get3MonthAgoDateLong(){
		Calendar threeMonthCalendar = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 4){
			threeMonthCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
			threeMonthCalendar.set(Calendar.MONTH, month + 9);
		}
		else{
			threeMonthCalendar.set(Calendar.MONTH, month - 3);
		}
		return threeMonthCalendar.getTime().getTime();
	}
	
	
	public static String getHourAndMinu( Context context, long timestamp ){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Timestamp(timestamp));
	}
	private static String convert2FormatPattern( String dateString ){
		String match = "^([0-1][0-9]|[2][0-3]):([0-5][0-9])$";
		Pattern p = Pattern.compile(match);
		Matcher matcher = p.matcher(dateString);

		if (matcher.matches()){
			return "HH:mm";
		}
		else{
			return "HH:mm:ss";
		}
	}

	private static long diffTime;
	private static int diffWeek;
	private static int diffTimeZoneHour;


	/**
	 * long time convert 2 local long time, like 132395854783 -> 13829483829
	 */
	public static long convert2LocalTimeLong( long serverSetLong){
		long localTimeLong = serverSetLong - diffTime;
		return localTimeLong;
	}

	/**
	 * long time convert 2 server long time, like 132395854783 -> 13829483829
	 */
	public static long convert2ServerTimeLong( long localSetLong){
		long serverTimeLong = localSetLong + diffTime;
		return serverTimeLong;
	}


	
	public static String convert2LocalWeekday(String repeatWeekday ,String hourAndMinute){
		int hour = StringUtil.toInteger(hourAndMinute.split(":")[0]);
		int diffday = 0 ;
		if((hour -diffTimeZoneHour) >= 24){
			diffday = 1;
		}else if((hour - diffTimeZoneHour) < 0){
			diffday = -1;
		}
		String localWeekday = "0,0,0,0,0,0,0";
		if (!StringUtil.isNullOrEmpty(repeatWeekday)){
			String weekdays[] = repeatWeekday.split(",");
			if(weekdays.length == 7){
				String[] localWeeks = new String[]{"0","0","0","0","0","0","0"};
				for(int i=0;i<weekdays.length; i++){
					if("1".equals(weekdays[i])){
						int serverWeekIndex = (i+7-diffWeek+diffday)%7;
						localWeeks[serverWeekIndex]="1";
					}
				}
				localWeekday = arrayWeekToStringWeek(localWeeks);
			}
		}
		return localWeekday;
	}
	public static String convert2ServerWeekday(String repeatWeekday,String hourAndMinute ){
		int hour = StringUtil.toInteger(hourAndMinute.split(":")[0]);
		int diffday = 0 ;
		if((hour + diffTimeZoneHour) >= 24){
			diffday = 1;
		}else if((hour + diffTimeZoneHour) < 0){
			diffday = -1;
		}
		String serverWeekday = "0,0,0,0,0,0,0";
		if (!StringUtil.isNullOrEmpty(repeatWeekday)){
			String weekdays[] = repeatWeekday.split(",");
			if(weekdays.length == 7){
				String[] serverWeeks = new String[]{"0","0","0","0","0","0","0"};
				for(int i=0;i<weekdays.length; i++){
					if("1".equals(weekdays[i])){
						int serverWeekIndex = (i+7+diffWeek+diffday)%7;
						serverWeeks[serverWeekIndex]="1";
					}
				}
				serverWeekday = arrayWeekToStringWeek(serverWeeks);
			}
		}
		return serverWeekday;
	}
	private static String arrayWeekToStringWeek(String[] weeks){
		StringBuffer buffer = new StringBuffer();
		for(String w : weeks){
			buffer.append(w+",");
		}
		String str = buffer.substring(0, buffer.length()-1);
		return str;
	}

	//拆分时间字符串  将1011拆分成  10：11
	public static String parseTime(String time){
		  if(time!=null&&time.length()>=4){
			  return time.substring(0, 2) + ":" + time.substring(2,4);
		  }
		  return "";
	  }
	
	//二进制字符串转   0,1,0,0,1  ->16进制字符串
	  public static String BinaryToHex(String paramString)
	  {
		  String str="00";
		  try{
			  Long.parseLong(paramString.replace(",", ""), 2);
			  str = Long.toHexString(Long.parseLong(paramString.replace(",", ""), 2));
		  }catch(NumberFormatException e){
			  e.printStackTrace();
		  }	    
	      if ((str != null) && (str.length() < 2))str = "0" + str;
	      return str;
	  }
	  // 十六进制字符串 转换成 本地格式的weekday  
	  public static String Hexconvert2LocalWeekday(String weekday){
		  String localWeekday = "0,0,0,0,0,0,0";
		  if (!StringUtil.isNullOrEmpty(weekday)){
		    char[] arrayOfChar = Integer.toBinaryString(Integer.valueOf(weekday, 16).intValue()).toCharArray();
		    StringBuffer buffer = new StringBuffer();
		    for(int i=0;i<7 - arrayOfChar.length;i++){
		    	buffer.append(0).append(",");
		    }
		    for (int j = 0;j<arrayOfChar.length ; j++) {
		    	buffer.append(arrayOfChar[j]).append(",");	     	      
		    }
		    localWeekday=buffer.substring(0, buffer.length()-1);
		}
		  return localWeekday;
	 }
		//时间选择器中 weekday的顺序 周日开头，左边为周一       转换成----->     协议要求 周日开头，右边为周一           反过来也可用
	  public static String changeWeekOrder(String data){
	        String[] cArray=data.split(",");
	        StringBuffer buffer=new StringBuffer();
	        buffer.append(cArray[0]).append(",");
	        for(int i=cArray.length-1;i>0;i--){
	            buffer.append(cArray[i]).append(",");
	        }
	        return buffer.substring(0,buffer.length()-1);
	    }
	  
}