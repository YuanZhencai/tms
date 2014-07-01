package com.wcs.base.util;

/**
 日期类
 * @date   
 * @version 1.0
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Years;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodPrinter;

/**
 * <p>Project: oa</p>
 * <p>Description: WARNING - Suitable only for non-clustered environments</p>
 * <p>Copyright (c) 2011 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yansong@wcs-global.com">Yan Song</a>
 */
public class DateUtil {

    private static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static String DATE_SHORT_PATTERN = "yyyy-MM-dd";
    private static String TIME_PATTERN = "HH:mm:ss";
    private static Log log = LogFactory.getLog(DateUtil.class);
    /*------------当前时间函数 --------------*/

    /**
     * 获取现在时间
     * 
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getNowStringDate() {
        return DateTime.now().toString(DATE_PATTERN);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     * 
     * @return
     */
    public static String getNowTimeShort() {
        return DateTime.now().toString(TIME_PATTERN);
    }

    /**
     * 获取现在时间
     * 
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getNowStringDateShort() {
        return DateTime.now().toString(DATE_SHORT_PATTERN);
    }

    /**
     * 获取现在时间
     * 
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static DateTime getNowDate() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
        return formatter.parseDateTime(getNowStringDate());
    }

    /**
     * 获取现在时间
     * 
     * @return返回时间类型 yyyy-MM-dd
     */
    public static DateTime getNowDateShort() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_SHORT_PATTERN);
        return formatter.parseDateTime(getNowStringDateShort());
    }

    /*------------时间转换函数 --------------*/

    /**
     * Date转DateTime
     * 
     * @param dateDate
     * @return
     */
    public static DateTime dateToDateTime(Date dateDate) {
        return new DateTime(dateDate);
    }

    /**
     * DateTime转Date
     * 
     * @param dateDate
     * @return
     */
    public static Date dateTimeToDate(DateTime dtDate) {
        return dtDate.toDate();
    }

    /**
     * 将字符串转换为时间格式时间
     * 
     * @return
     */
    public static DateTime strToDate(String strDate, String strInPattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(strInPattern);
        return formatter.parseDateTime(strDate);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     * 
     * @param strDate
     * @return
     */
    public static DateTime strToDateShort(String strDate) {
        return strToDate(strDate, DATE_SHORT_PATTERN);
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * 
     * @param strDate
     * @return
     */
    public static DateTime strToDateLong(String strDate) {
        return strToDate(strDate, DATE_PATTERN);
    }

    /**
     * 将时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     * 
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(DateTime dateDate) {
        return dateDate.toString(DATE_PATTERN);
    }

    /**
     * 将时间格式时间转换为字符串 yyyy-MM-dd
     * 
     * @param dateDate
     * @param k
     * @return
     */
    public static String dateToStrShort(DateTime dateDate) {
        return dateDate.toString(DATE_SHORT_PATTERN);
    }

    /**
     * 将时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     * 
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date date) {
        DateTime dateDate = DateUtil.dateToDateTime(date);
        return dateDate.toString(DATE_PATTERN);
    }
    
    /**
     * 将时间格式时间转换为字符串 yyyy-MM-dd
     * 
     * @param dateDate
     * @return
     */
    public static String dateToStrShort(Date date) {
        DateTime dateDate = DateUtil.dateToDateTime(date);
        return dateDate.toString(DATE_SHORT_PATTERN);
    }

    /**
     * 日期加一天
     * @param date
     * @return
     */
    public static Date addOneDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 
     * <p>Description: 日期减一年减一天</p>
     * @param date
     * @return
     */
    public static Date minusYearAddDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, -1);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }
    
    /**
     * 
     * <p>Description: 日期减N年减N月减N天</p>
     * <p>不增不减参数传递0</p>
     * <p>例如：减一天传递-1，增加一天传递1</p>
     * @param date
     * @return
     */
    public static Date adjustYearAndMonthAndDay(Date date,int year,int month,int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year);
        c.add(Calendar.MONTH, month);
        c.add(Calendar.DATE, day);
        return c.getTime();
    }
    
    
    

    /**
     * 
     * <p>Description: 日期减一个月</p>
     * @param date
     * @return
     */
    public static Date minusMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    // 以下仅供学习
    /*------------时间计算函数 --------------*/
    public static void plusDate() {
        DateTime dateDate = DateTime.now();
        DateTime result = new DateTime();
        result = dateDate.plusDays(-9);
        log.info("plusDays" + result.toString());
        result = dateDate.plusHours(-9);
        log.info("plusHours" + result.toString());
        result = dateDate.plusMinutes(-9);
        log.info("plusMinutes" + result.toString());
        result = dateDate.plusMonths(-9);
        log.info("plusMonths" + result.toString());
        result = dateDate.plusWeeks(-9);
        log.info("plusWeeks" + result.toString());

        result = DateTime.now().minusYears(5) // five years ago
                .monthOfYear() // get monthOfYear property
                .setCopy(2) // set it to February
                .dayOfMonth() // get dayOfMonth property
                .withMaximumValue();// the last day of the month
        log.info("计算五年后的第二个月的最后一天" + result.toString());

        DateTime dt2 = new DateTime();
        DateTime year2000 = dt2.withYear(2000);
        DateTime twoHoursLater = year2000.plusHours(2);
        log.info("2000年的现在两小时后" + twoHoursLater.toString(DateTimeFormat.forPattern("yy-MM-dd HH:mm:ss")));

    }

    /*------------时间比较函数 --------------*/
    public static void compareDate() {
        DateTime dd = new DateTime("2010-10-01");
        DateTime dt = new DateTime("2010-10-03");
        Days d = Days.daysBetween(dd, dt);
        log.info("日期比较" + d.getDays());

        Months m = Months.monthsBetween(dd, dt);
        log.info("月份比较" + m.getMonths());

        Years y = Years.yearsBetween(dd, dt);
        log.info("年份比较" + y.getYears());
    }

    /*------------本地日期时间 --------------*/
    public static void localDate() {
        // 当地理位置（即时区）变得不重要时，使用它存储日期将非常方便
        LocalDate localDate = new LocalDate();
        // 当地理位置（即时区）变得不重要时，使用它存储时间将非常方便
        LocalTime localTime = new LocalTime();

        log.info("本地日期" + localDate.toString());
        log.info("本地时间" + localTime.toString());

    }

    /*------------改变时区 --------------*/
    public static void modifyTimeZone() {
        DateTime dt = new DateTime();
      
        DateTime dtLondon = dt.withZone(DateTimeZone.forID("Europe/London"));
        log.info("伦敦时间" + dtLondon.toString());
    }

    /*------------改变年历 --------------*/
    public static void modifyChronology() {
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        Chronology coptic = CopticChronology.getInstance(zone);
        // current time with coptic chronology
        DateTime dt = new DateTime(coptic);
        log.info("年历" + dt.toString());
    }

    /*------------实际应用 --------------*/
    public static void use(String simpleTime1, String simpleTime2) {
        // 计算一天的工作时间, 每天工作8小时,1小时休息: 还需要多少时间

        String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
        Hours FORMAL_TIME = Hours.EIGHT;
        Hours RESET_TIME = Hours.ONE;
        DateTimeFormatter FORMATTER;
        PeriodPrinter PRINTER;

        FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);
        PRINTER = new PeriodFormatterBuilder().appendHours().appendSuffix(" hour", " hours").appendSeparator(" and ")
                .appendMinutes().appendSuffix(" min").appendSeparator(" ").appendSeconds().appendSuffix(" second", " seconds")
                .toPrinter();

        DateTime start = FORMATTER.parseDateTime(simpleTime1);
        DateTime end = FORMATTER.parseDateTime(simpleTime2);

        long worked = new Duration(start, end).minus(FORMAL_TIME.plus(RESET_TIME).toStandardDuration()).getMillis();

        StringBuffer sb = new StringBuffer();

        if (worked < 0) {
            sb.append("Worked not enough! Still need work : ");
            PRINTER.printTo(sb, new Period(Math.abs(worked)), null);
        } else {
            sb.append("Good job! Worked : ");
            PRINTER.printTo(sb, new Period(Math.abs(worked)).plusHours(FORMAL_TIME.getHours()), null);
        }

        log.info(sb.toString());
    }

    // 测试用
    public static void main(String[] args) throws Exception {
        try {
            log.info(String.valueOf(System.currentTimeMillis()));
            /*------------当前时间函数 --------------*/
            log.info(getNowStringDate());
            /*------------时间转换函数 --------------*/
            log.info(strToDateShort("2010-10-01"));
            /*------------时间计算函数 --------------*/
            plusDate();
            /*------------时间比较函数 --------------*/
            compareDate();
            /*------------本地日期时间 --------------*/
            localDate();
            /*------------改变时区 --------------*/
            modifyTimeZone();
            /*------------改变年历 --------------*/
            modifyChronology();
            /*------------实际应用 --------------*/
            
            String simpleTime1 = "2010-10-01 8:50:26";
            String simpleTime2 = "2010-10-01 15:35:54";
            use(simpleTime1, simpleTime2);
            
            String test = "2012-01-03";
            log.info(DateTime.parse(test).plusDays(1));
            log.info(DateTime.parse(test).getDayOfMonth());
            
          
            log.info("jianshao:"+DateUtil.dateToStrShort(adjustYearAndMonthAndDay(new Date(),-1,0 ,0)));
            
        } catch (Exception e) {
        	log.error("出现异常", e);
        }
    }
    
    /**
	 * 将Date转换为字符串
	 * 
	 * @param aDate
	 * @param pattern
	 *            格式
	 * @return
	 */
	public static String convertDateToString(Date aDate, String pattern) {
		if (aDate == null) {
			return null;
		}
		if (StringUtils.isBlankOrNull(pattern)) {
			pattern = DATE_PATTERN;
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(aDate);
	}

}