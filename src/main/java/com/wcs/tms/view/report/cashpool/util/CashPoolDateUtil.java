package com.wcs.tms.view.report.cashpool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CashPoolDateUtil {
	private static Logger log = LoggerFactory.getLogger(CashPoolDateUtil.class);
	private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private CashPoolDateUtil() {

	}

	/**
	 * 增加日期
	 * @param date ： 要改变的日期
	 * @param day  ：要增加的天数
	 * @param month：要增加的月份数
	 * @return
	 */
	public static Date addDays(Date date, int month, int day, int hour, int minute) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		cd.add(Calendar.DATE, day);// 增加天
		cd.add(Calendar.MONTH, month);// 增加月
		cd.add(Calendar.HOUR, hour);// 增加时
		cd.add(Calendar.MINUTE, minute);// 增加分
		return cd.getTime();
	}

	/**
	 * 根据Date获取String类型 
	 * 格式为：yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */

	public static String getStrDate(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		String sfPattern = DATETIME_PATTERN;
		if (pattern != null) {
			sfPattern = pattern;
		}
		String strDate = "";
		strDate = new SimpleDateFormat(sfPattern).format(date);
		return strDate;
	}

	/**
	 * 根据yyyy-MM-dd HH:mm:ss格式的字符串获取Date
	 * @param strDate
	 * @return
	 */
	public static Date getDateStr(String strDate, String pattern) {
		String sfPattern = DATETIME_PATTERN;
		if (pattern != null) {
			sfPattern = pattern;
		}
		if ("".equals(strDate)) {
			return new Date();
		}
		Date date = null;
		try {
			date = new SimpleDateFormat(sfPattern).parse(strDate);
		} catch (ParseException e) {
			log.error("\n", e);
		}
		return date;
	}

}
