package cn.zhaoxi.zxyx.common.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间换算类
 */
public class DateUtil {

    /**
     * 显示时间，如果与当前时间差别小于一天，则自动用**秒(分，小时)前，
     * 如果大于一天则用format规定的格式显示
     *
     * @param cTimeLong 时间
     * @return 处理得到的时间字符串
     */
    public static String showTime(Long cTimeLong) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);

        Date cTime = new Date(cTimeLong);

        if (cTime == null) return "未知";

        long nowTimeLong = System.currentTimeMillis();
        long result = Math.abs(nowTimeLong - cTimeLong);

        if (result < 60000) {
            long seconds = result / 1000;
            if (seconds == 0) {
                return "刚刚";
            } else {
                return seconds + "秒前";
            }
        }
        if (result >= 60000 && result < 3600000) {
            long seconds = result / 60000;
            return seconds + "分钟前";
        }
        if (result >= 3600000 && result < 86400000) {
            long seconds = result / 3600000;
            return seconds + "小时前";
        }
        if (result >= 86400000 && result < 1702967296) {
            long seconds = result / 86400000;
            return seconds + "天前";
        }

        // 跨年
        sdf = new SimpleDateFormat("yyyy", Locale.SIMPLIFIED_CHINESE);
        long nowYearLong = 0;
        try {
            nowYearLong = sdf.parse(sdf.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        if (nowYearLong < cTimeLong){
            sdf = new SimpleDateFormat("MM-dd hh:mm", Locale.SIMPLIFIED_CHINESE);
        }
        return sdf.format(cTime);
    }

    /**
     * second to HH:MM:ss
     * @param seconds
     * @return
     */
    public static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = (int)seconds / 60;
            if (minute < 60) {
                second = (int)seconds % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int)(seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String convertSecondsToFormat(long seconds,String format){

        if(TextUtils.isEmpty(format))
            return "";

        Date date = new Date(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    //时间转化毫秒
    public static long date2ms(String dateForamt,String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat(dateForamt).parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    //毫秒转化成日期
    public static String ms2date(String dateForamt,long ms){
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat(dateForamt);
        return format.format(date);
    }

    /**时间戳转日期*/
    public static String unix2Date(String dateForamt, long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateForamt);
        String sd = sdf.format(new Date(ms*1000));
        return sd;
    }
}
