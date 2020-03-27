/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.x7.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TimeUtil {

	public final static long ONE_DAY = 86400000L;
	public final static long ONE_WEEK = 604800000L;
	public final static long HALF_HOUR = 1800000;
	public final static long ONE_HOUR = 3600000;
	public final static long ONE_MINUTE = 60000;

	/**
	 * 判断是否同一天
	 *
	 */
	public static boolean isSameDay(long time1, long time2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(time1);

		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(time2);

		return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1
					.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
	}
	
	
	public static boolean isSameMonth(long time1, long time2){
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(time1);

		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(time2);

		return c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1
					.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
	}

	public static long now() {
		return System.currentTimeMillis();
	}

	public static String toMillisString() {
		return "" + System.currentTimeMillis();
	}

	public static long toMillis(String strMillis) {
		return Long.valueOf(strMillis);
	}
	public static long toMillis(Date time) {
		if (time == null)
			return 0;
		return time.getTime();
	}
	

	/**
	 * same as now()
	 *
	 */
	public static long toMillis() {
		return System.currentTimeMillis();
	}

	public static long nextDay(long millis) {
		return millis + ONE_DAY;
	}

	public static long nextWeek(long millis) {
		return millis + ONE_WEEK;
	}

	public static long nextHour(long millis) {
		return millis + ONE_HOUR;
	}

	public static long nextHalfHour(long millis) {
		return millis + HALF_HOUR;
	}

	public static long lastWeek(long millis) {
		return millis - ONE_WEEK;
	}

	public static long lastDay(long millis) {
		return millis - ONE_DAY;
	}

	public static long lastHour(long millis) {
		return millis - ONE_HOUR;
	}

	public static long lastMinute(long millis) {
		return millis - ONE_MINUTE;
	}

	/**
	 * 
	 * @return 当天的0点0分0秒
	 */
	public static long CLOCK_0() {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		c.set(Calendar.MILLISECOND,0);	
		return c.getTimeInMillis();
	}
	
	public static long CLOCK_0(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		c.set(Calendar.MILLISECOND,0);	
		return c.getTimeInMillis();
	}

	/**
	 * 计划在几点
	 * 
	 * @param HOUR
	 * @return
	 */
	public static long scheduledAt(int HOUR) {
		return scheduledAt(HOUR, 0);
	}

	/**
	 * 计划在几点几分
	 * 
	 * @param HOUR
	 * @param minute
	 * @return
	 */
	public static long scheduledAt(int HOUR, int minute) {
		long scheduledAt = CLOCK_0() + ONE_HOUR * HOUR + ONE_MINUTE * minute;
		if (scheduledAt <= now()) {
			scheduledAt += ONE_DAY;
		}

		return scheduledAt;
	}
	
	public static long fixedAt(int HOUR, int minute){
		return CLOCK_0() + ONE_HOUR * HOUR + ONE_MINUTE * minute;
	}

	/**
	 * 将时间格式和时间字符串值传入，获得时间戳<br>
	 * 举例时间格式 yyyy-MM-dd hh:mm:ss
	 * 
	 * @param timeStr
	 * @param format
	 * @return
	 */
	public static long getTime(String timeStr, String format) {
		try {
			// 时间格式 yyyy-MM-dd hh:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date d = sdf.parse(timeStr);
			return d.getTime();
		} catch (Exception ex) {
		}
		return 0L;
	}



	/**
	 * 支持把 hh:mm:ss 格式的字符串，转换成秒为单位的long。 mm:ss 或者 ss 格式的字符串也可以
	 * 
	 * @param ss
	 * @return 单位:秒
	 */
	public static long format(String ss) {
		String[] tt = ss.split(":");
		int[] unit = new int[4];
		unit[0] = 60;
		unit[1] = 60;
		unit[2] = 60;
		unit[3] = 24;

		long total = 0;
		int j = 0;
		int x = 1;
		for (int i = tt.length - 1; i >= 0; i--, j++) {
			String t = tt[i];

			int time = Integer.parseInt(t);
			time *= x;
			total += time;
			x *= unit[j];
		}

		return total;
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isSameDay(Date d1, long l2) {
		Date d2 = new Date();
		d2.setTime(l2);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str1 = sdf.format(d1);
		String str2 = sdf.format(d2);
		if (str1.equals(str2)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isToday(Date date) {
		return isSameDay(date.getTime(), now());
	}

	/**
	 * 判断是否是今天
	 */
	public static boolean isToday(long time) {
		return isSameDay(time, now());
	}
	
	/**
	 * 是否是本月
	 * @param time
	 * @return
	 */
	public static boolean isThisMonth(long time){
		return isSameMonth(time,now());
	}
	
	/**
	 * 是否是本月, 扣除定时推迟的小时数
	 * @param time
	 * @return
	 */
	public static boolean isThisMonth(long time, int sheduledClock){
		return isSameMonth(time, now() - sheduledClock * ONE_HOUR);
	}


	public static String format(Date d) {
		if (d == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

	public static String format(Date d, String formatter) {
		if (d == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formatter); // "MM-dd HH:mm"
		return sdf.format(d);
	}



	/**
	 * 动态定时的 小时计算<br>
	 * 仅仅支持小时
	 * 
	 * @param nowClock
	 * @param delay
	 * @param clockBeforeDelay
	 * @return
	 */
	public static int scheduledAtHour(int nowClock, int delay,
			int clockBeforeDelay) {
		if (nowClock < clockBeforeDelay)
			return clockBeforeDelay;
		if ((nowClock - clockBeforeDelay) % delay == 0)
			return nowClock + delay;
		return nowClock - nowClock % delay + clockBeforeDelay % delay;
	}
	
	
		public static long addTime(int seconds){
			long now = System.currentTimeMillis();
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(now);			
			ca.add(Calendar.SECOND, seconds);
			return ca.getTimeInMillis();
		}


	/**
	 * 现在几点
	 * 
	 * @return
	 */
	public static int getHour() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 是否大于每点的固定时间(小时数)
	 * @param hour
	 * @return
	 */
	public static boolean isAfaterTheSameDayOfHour(long time,int hour){
		boolean isSameDay = isSameDay(time, now());//是否同一天		
		if(!isSameDay){//不是同一天
			int daysBetween= (int) ((System.currentTimeMillis()-time)/(3600*24*1000));	
			if(daysBetween >= 1){
				return true;
			}
			int cuhour = getHour();
			if(cuhour >= hour){
				return true;
			}
		}	
		return false;
	}


	public static Date getDate(long v) {
		
		try{
			return new Date(Long.valueOf(v));
		}catch (Exception ee){
			ee.printStackTrace();
		}
		
		
		return null;
	}
	

	public static Date getDate(String timeStr) {
		try {
			// 时间格式 yyyy-MM-dd hh:mm:ss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(timeStr);
			
		} catch (Exception ex) {
		}
		return null;
	}
	
	public static long nowForCache() {
		long now = System.currentTimeMillis();
		
		return now / 10000 * 10000;
	
	}
	
	public static Date dateForCache(){
		
		long now = nowForCache();
		return new Date(now);
	}
	
	public static Object parseDateForJson(Object v){
		if (Objects.isNull(v))
			return v;
		String str = v.toString();
		str = str.trim();
		if (str.startsWith("$date")){
			String[] arr = str.split("=");
			String time = arr[1];
			time = time.trim();
			return new Date(Long.parseLong(time));
		}
		return v;
	}
	
	public static long year(Date date){
		if (date == null)
			return Calendar.getInstance().get(Calendar.YEAR);;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	
	public static long month(Date date){
		if (date == null)
			return Calendar.getInstance().get(Calendar.MONTH);;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}

	public static long day(Date date){
		if (date == null)
			return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static long hour(Date date){
		if (date == null)
			return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY);
	}
	
	public static long minute(Date date){
		if (date == null)
			return Calendar.getInstance().get(Calendar.MINUTE);;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MINUTE);
	}
	
	public static long weekFrom2000(Date date){
		
		long time = date.getTime();
		
		long t000 = 946656000450L; //2000-00-01 00:00:00
		
		long CLOCK_0 = CLOCK_0(time);
		
		return (CLOCK_0 - t000) / ONE_WEEK;
	}
	
	public static long week_0(Date date){
		
		long time = date.getTime();
		
		return week_0(time);
	}
	
	public static long week_0(long time){
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		
		int dayWeek = c.get(Calendar.DAY_OF_WEEK);
		
		time = time - dayWeek * ONE_DAY;
		
		c.setTimeInMillis(time);

		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		c.set(Calendar.MILLISECOND,0);	
		return c.getTimeInMillis();
	}


}
