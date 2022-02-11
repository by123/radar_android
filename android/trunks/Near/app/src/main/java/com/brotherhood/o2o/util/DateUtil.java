package com.brotherhood.o2o.util;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.application.NearApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间的处理
 */
public class DateUtil {

    /**
     * 获取年龄
     *
     * @param birthDayMills 生日毫秒值
     * @return
     */
    public static int parseAge(long birthDayMills) {
        String[] birthDayArray = DateUtil.parseYearMonthDay(birthDayMills*1000);
        String[] currentArray = DateUtil.parseYearMonthDay(System.currentTimeMillis());
        int age = Integer.valueOf(currentArray[0]) - Integer.valueOf(birthDayArray[0]);
        if (Integer.valueOf(currentArray[1]) < Integer.valueOf(birthDayArray[1])) {
            age -= 1;
        } else if (Integer.valueOf(currentArray[1]) == Integer.valueOf(birthDayArray[1])) {
            if (Integer.valueOf(currentArray[2]) < Integer.valueOf(birthDayArray[2])) {
                age -= 1;
            }
        }
        return age;
    }


    public static String[] parseYearMonthDay(long mills) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = format.format(new Date(mills));
        return timeStr.split("-");
    }

    /**
     * @param mills      long型时间
     * @param formatText 转换规则
     */
    public static String parseJavaTimeToString(long mills, String formatText) {
        SimpleDateFormat format = new SimpleDateFormat(formatText);
        return format.format(new Date(mills));
    }


    public static String parseJavaTimeToString(long mills) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(mills));
    }


    /**
     * @param mills      long型时间
     * @param formatText 转换规则
     */
    public static String parseUnixTimeToString(long mills, String formatText) {
        SimpleDateFormat format = new SimpleDateFormat(formatText);
        return format.format(new Date(mills * 1000L));
    }


    public static String parseUnixTimeToString(long mills) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(mills * 1000L));
    }


    /**
     * 根据时间获取星座
     *
     * @param mills
     * @return 白羊座：3月21日～4月20日
     * 金牛座：4月21日～5月21日
     * 双子座：5月22日～6月21日
     * 巨蟹座：6月22日～7月22日
     * 狮子座：7月23日～8月23日
     * 处女座：8月24日～9月23日
     * 天秤座：9月24日～10月23日
     * 天蝎座：10月24日～11月22日
     * 射手座：11月23日～12月21日
     * 魔羯座：12月22日～1月20日
     * 水瓶座：1月21日～2月19日
     * 双鱼座：2月20日～3月20日
     */

    public static String parseConstellation(long mills) {
        String timeStr = parseUnixTimeToString(mills, "MM-dd");
        String[] timeStrings = timeStr.split("-");
        int month = Integer.valueOf(timeStrings[0]);
        int day = Integer.valueOf(timeStrings[1]);
        String[] constellationArr = NearApplication.mInstance.getResources().getStringArray(R.array.constellation);
        String star = "";
        if (month == 1 && day >= 20 || month == 2 && day <= 18) {
            star = constellationArr[0];
        }
        if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            star = constellationArr[1];
        }
        if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            star = constellationArr[2];
        }
        if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            star = constellationArr[3];
        }
        if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            star = constellationArr[4];
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            star = constellationArr[5];
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            star = constellationArr[6];
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            star = constellationArr[7];
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 22) {
            star = constellationArr[8];
        }
        if (month == 10 && day >= 23 || month == 11 && day <= 21) {
            star = constellationArr[9];
        }
        if (month == 11 && day >= 22 || month == 12 && day <= 21) {
            star = constellationArr[10];
        }
        if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            star = constellationArr[11];
        }
        return star;
    }


    public static String getLatestMsgStrTime(long msgTime) {
        String time = "";

        long loc_time = Long.valueOf(msgTime);
        Date webData = new Date(loc_time * 1000L);

        Date currentDate = new Date(System.currentTimeMillis());

        long diff = currentDate.getTime() - webData.getTime();

        long days = diff / (1000 * 60 * 60 * 24);

        long hours = diff / (1000 * 60 * 60);

        //if (hours < 1) {
        //    time = "刚刚";
        //} else

        if (days < 1) {
            time = parseUnixTimeToString(msgTime, "HH:mm");
        } else if (days < 2) {
            time = "昨天";
        } else if (days < 7) {
            time = getWeek(parseUnixTimeToString(msgTime, "yyyy-MM-dd"));
        }
        //
        //else if (days < 365) {
        //    sdf = new SimpleDateFormat("MM-dd");
        //    time = sdf.format(new Date(loc_time * 1000L));
        //}
        else {
            time = parseUnixTimeToString(msgTime, "yyyy-MM-dd");
        }
        return time;
    }


    private static String getWeek(String pTime) {

        String Week = "星期";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "六";
        }
        return Week;
    }


}
