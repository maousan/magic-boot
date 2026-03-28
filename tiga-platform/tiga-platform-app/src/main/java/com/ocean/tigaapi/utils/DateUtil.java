package com.ocean.tigaapi.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * 日期工具类
 * 提供日期格式化、解析、计算、儒略日转换及ID生成等功能
 */
public class DateUtil {

    /**
     * 基础 ISO 8601 日期格式 (yyyyMMdd) 例如：20021225
     */
    public static final String ISO_DATE_FORMAT = "yyyyMMdd";

    /**
     * 扩展 ISO 8601 日期格式 (yyyy-MM-dd) 例如：2002-12-25
     */
    public static final String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 标准日期时间模式 (yyyy-MM-dd HH:mm:ss)
     */
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 紧凑日期时间模式 (yyyyMMddHHmmss)
     */
    public static String DATE_PATTERN = "yyyyMMddHHmmss";
   
    /**
     * 是否允许宽松解析日期（如允许 1月32日 自动转为 2月1日）
     */
    private static boolean LENIENT_DATE = false;

    private static Random random = new Random();
    private static final int ID_BYTES = 10;

    /**
     * 生成基于毫秒值和随机数的 ID
     * @return 字符串格式的唯一ID
     */
    public synchronized static String generateId() {
        StringBuffer result = new StringBuffer();
        result = result.append(System.currentTimeMillis());
        for (int i = 0; i < ID_BYTES; i++) {
            result = result.append(random.nextInt(10));
        }
        return result.toString();
    }

    /**
     * 标准化儒略日（四舍五入到最近的 .5 值）
     */
    protected static final float normalizedJulian(float JD) {
        float f = Math.round(JD + 0.5f) - 0.5f;
        return f;
    }

    /**
     * 将儒略日转换为 Java Date 对象
     * 注意：此方法在处理 1582 年以前的日期（格里高利历法前）可能不准确
     * @param JD 儒略日
     * @return 格里高利历日期
     */
    public static final Date toDate(float JD) {
        float Z = (normalizedJulian(JD)) + 0.5f;
        float W = (int) ((Z - 1867216.25f) / 36524.25f);
        float X = (int) (W / 4f);
        float A = Z + 1 + W - X;
        float B = A + 1524;
        float C = (int) ((B - 122.1) / 365.25);
        float D = (int) (365.25f * C);
        float E = (int) ((B - D) / 30.6001);
        float F = (int) (30.6001f * E);
        int day = (int) (B - D - F);
        int month = (int) (E - 1);

        if (month > 12) {
            month = month - 12;
        }

        int year = (int) (C - 4715); 

        if (month > 2) {
            year--;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1); // Calendar 月份从0开始
        c.set(Calendar.DATE, day);

        return c.getTime();
    }

    /**
     * 计算两个日期之间的天数差（Date类型）
     * @param early 较早的日期
     * @param late 较晚的日期
     * @return 天数差（正值表示 late 在 early 之后）
     */
    public static final int daysBetween(Date early, Date late) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(early);
        c2.setTime(late);
        return daysBetween(c1, c2);
    }

    /**
     * 计算两个日期之间的天数差（Calendar类型）
     * 基于儒略日计算，避免了夏令时和月天数不同的问题
     */
    public static final int daysBetween(Calendar early, Calendar late) {
        return (int) (toJulian(late) - toJulian(early));
    }

    /**
     * 计算时间差并格式化为中文描述
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 形如 "X天X小时X分钟" 的字符串
     */
    public static final String timeBetween(Date startDate, Date endDate) {
        long nd = 1000 * 24 * 60 * 60; // 一天的毫秒数
        long nh = 1000 * 60 * 60;      // 一小时的毫秒数
        long nm = 1000 * 60;           // 一分钟的毫秒数
        
        long diff = endDate.getTime() - startDate.getTime();
        long day = diff / nd;
        long hour = diff % nd / nh;
        long min = diff % nd % nh / nm;
        
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 将 Calendar 对象转换为儒略日数值
     */
    public static final float toJulian(Calendar c) {
        int Y = c.get(Calendar.YEAR);
        int M = c.get(Calendar.MONTH);
        int D = c.get(Calendar.DATE);
        int A = Y / 100;
        int B = A / 4;
        int C = 2 - A + B;
        float E = (int) (365.25f * (Y + 4716));
        float F = (int) (30.6001f * (M + 1));
        float JD = C + D + E + F - 1524.5f;

        return JD;
    }

    /**
     * 将 Date 对象转换为儒略日数值
     */
    public static final float toJulian(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return toJulian(c);
    }

    /**
     * 增加日期的指定字段（年、月、日）
     * @param isoString 原始日期字符串
     * @param fmt 日期格式
     * @param field Calendar 字段常量（如 Calendar.DATE）
     * @param amount 增加的数量（负数为减少）
     * @return 增加后的日期字符串
     */
    public static final String dateIncrease(String isoString, String fmt,
                                            int field, int amount) {
        try {
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTime(stringToDate(isoString, fmt, true));
            cal.add(field, amount);
            return dateToString(cal.getTime(), fmt);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 滚动日期字段（不影响更大的字段，如滚动天数到月底会回到1号）
     */
    public static final String roll(String isoString, String fmt, int field,
                                    boolean up) throws ParseException {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(stringToDate(isoString, fmt));
        cal.roll(field, up);
        return dateToString(cal.getTime(), fmt);
    }

    public static final String roll(String isoString, int field, boolean up) throws
            ParseException {
        return roll(isoString, DATETIME_PATTERN, field, up);
    }

    /**
     * 字符串转 java.util.Date
     * @param dateText 文本内容
     * @param format 格式（如 yyyy-MM-dd）
     * @param lenient 是否允许不严格的解析
     */
    public static Date stringToDate(String dateText, String format,
                                    boolean lenient) {
        if (dateText == null) return null;
        DateFormat df = null;
        try {
            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }
            df.setLenient(lenient); // 这里原代码写死false，修正为传参
            return df.parse(dateText);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取当前 SQL 格式的时间戳
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(new Date().getTime());
    }

    /**
     * 字符串转 Date (使用默认宽松策略)
     */
    public static Date stringToDate(String dateString, String format) {
        return stringToDate(dateString, format, LENIENT_DATE);
    }

    /**
     * 字符串转 Date (使用默认格式 yyyy-MM-dd)
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, ISO_EXPANDED_DATE_FORMAT, LENIENT_DATE);
    }

    /** * Date 转 字符串
     * @param date 日期对象
     * @param pattern 目标格式
     */
    public static String dateToString(Date date, String pattern) {
        if (date == null) return null;
        try {
            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);
            return sfDate.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Date 转 字符串 (格式：yyyy-MM-dd)
     */
    public static String dateToString(Date date) {
        return dateToString(date, ISO_EXPANDED_DATE_FORMAT);
    }

    /** * 获取当前系统日期时间
     */
    public static Date getCurrentDateTime() {
        return new Date();
    }

    /**
     * 获取当前系统日期字符串
     */
    public static String getCurrentDateString(String pattern) {
        return dateToString(getCurrentDateTime(), pattern);
    }

    /**
     * 获取当前系统日期字符串 (yyyy-MM-dd)
     */
    public static String getCurrentDateString() {
        return dateToString(getCurrentDateTime(), ISO_EXPANDED_DATE_FORMAT);
    }

    /**
     * 获取当前时间 (yyyy-MM-dd HH:mm:ss)
     */
    public static String dateToStringWithTime( ) {
        return dateToString(new Date(), DATETIME_PATTERN);
    }
 
    /**
     * 将指定 Date 转换为 (yyyy-MM-dd HH:mm:ss)
     */
    public static String dateToStringWithTime(Date date) {
        return dateToString(date, DATETIME_PATTERN);
    }

    /**
     * 增加天数 (基于 GMT 时区)
     */
    public static Date dateIncreaseByDay(Date date, int days) {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 增加月份
     */
    public static Date dateIncreaseByMonth(Date date, int mnt) {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        cal.add(Calendar.MONTH, mnt);
        return cal.getTime();
    }

    /**
     * 增加年份
     */
    public static Date dateIncreaseByYear(Date date, int mnt) {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        cal.add(Calendar.YEAR, mnt);
        return cal.getTime();
    }

    /**
     * 增加天数并返回字符串 (yyyyMMdd)
     */
    public static String dateIncreaseByDay(String date, int days) {
        return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
    }

    /**
     * 增加天数并返回字符串
     */
    public static String dateIncreaseByDay(String date, String fmt, int days) {
        return dateIncrease(date, fmt, Calendar.DATE, days);
    }

    /**
     * 格式转换：源格式字符串 转 目标格式字符串
     */
    public static String stringToString(String src, String srcfmt,
                                        String desfmt) {
        return dateToString(stringToDate(src, srcfmt), desfmt);
    }

    /**
     * 获取年份字符串 (yyyy)
     */
    public static String getYear(Date date) {
        return new SimpleDateFormat("yyyy").format(date);
    }

    /**
     * 获取月份字符串 (MM)
     */
    public static String getMonth(Date date) {
        return new SimpleDateFormat("MM").format(date);
    }

    /**
     * 获取日期字符串 (dd)
     */
    public static String getDay(Date date) {
        return new SimpleDateFormat("dd").format(date);
    }
    
    /**
     * 获取日期数字 (int)
     */
    public static int getDayInt(Date date) {
        return Integer.valueOf(getDay(date));
    }
    
    /**
     * 获取小时字符串 (HH)
     */
    public static String getHour(Date date) {
        return new SimpleDateFormat("HH").format(date);
    }    

    /**
     * 获取该日期当天的总分钟数（0-1439）
     */
    public static int getMinsFromDate(Date dt) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return ((hour * 60) + min);
    }

    /**
     * 转换为日期，解析失败时根据 isExpiry 返回兜底时间
     * @param isExpiry true: 兜底到明天的23:59; false: 兜底到今天的00:00
     */
    public static Date convertToDate(String str, boolean isExpiry) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = null;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            Calendar cal = Calendar.getInstance();
            if (isExpiry) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            dt = cal.getTime();
        }
        return dt;
    }

    /**
     * 转换为日期 (yyyy-MM-dd hh:mm)，失败返回当前时间
     */
    public static Date convertToDate(String str) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date dt = null;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            dt = new Date();
        }
        return dt;
    }
    
    /**
     * 转换为日期 (yyyy-MM-dd hh:mm:ss)，失败返回当前时间
     */
    public static Date convertToDateTime(String str) {
    	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	Date dt = null;
    	try {
    		dt = fmt.parse(str);
    	} catch (ParseException ex) {
    		dt = new Date();
    	}
    	return dt;
    }

    /**
     * 组合日期和分钟偏移量生成特定格式字符串
     * @param date 基础日期
     * @param minute 从 00:00 开始的分钟偏移
     * @return 形如 yyyyMMdd HHmm00 的字符串
     */
    public static String dateFromat(Date date, int minute) {
        String dateFormat = null;
        int year = Integer.parseInt(getYear(date));
        int month = Integer.parseInt(getMonth(date));
        int day = Integer.parseInt(getDay(date));
        int hour = minute / 60;
        int min = minute % 60;
        dateFormat = String.valueOf(year)
                     + (month > 9 ? String.valueOf(month) : "0" + String.valueOf(month))
                     + (day > 9 ? String.valueOf(day) : "0" + String.valueOf(day))
                     + " "
                     + (hour > 9 ? String.valueOf(hour) : "0" + String.valueOf(hour))
                     + (min > 9 ? String.valueOf(min) : "0" + String.valueOf(min))
                     + "00";
        return dateFormat;
    }
    
    /**
     * 获取当前系统时间的紧凑格式字符串 (yyyyMMddHHmmss)
     */
    public static String sDateFormat() {
    	return new SimpleDateFormat(DATE_PATTERN).format(Calendar.getInstance().getTime());	
    }
    
    /**
     * 获得本月的第一天日期字符串 (yyyy-MM-dd)
     */
    public static String getFirstDateOfThisMonth() {
    	SimpleDateFormat format = new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT);
		Calendar calendarFirst = Calendar.getInstance();
        calendarFirst.set(Calendar.DAY_OF_MONTH, 1);  
        return format.format(calendarFirst.getTime()); 
    }
    
    /**
     * 获得本月的最后一天日期字符串 (yyyy-MM-dd)
     */
    public static String getLastDateOfThisMonth() {
    	SimpleDateFormat format = new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT);  
		Calendar calendarLast = Calendar.getInstance();
		calendarLast.setTime(new Date());
        // 关键修正：需要设置到该月的最大天数
		int lastDay = calendarLast.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendarLast.set(Calendar.DAY_OF_MONTH, lastDay);
		
		return format.format(calendarLast.getTime());  
    }
}