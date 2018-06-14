package com.tbs.circle.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tbs.circle.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huangfangyi on 2016/12/24.
 * qq 84543217
 */


public class DateUtils
{
    private static final long INTERVAL_IN_MILLISECONDS = 30000L;

    public DateUtils() {
    }

    public static String getDuration(Context context,String rel_time, String now_time) {

        if(TextUtils.isEmpty(now_time)){
            if(!TextUtils.isEmpty(rel_time)){
                String showTime=rel_time.substring(0,rel_time.lastIndexOf(":"));

                return showTime;
            }

            return "时间错误";
        }

        String backStr = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(rel_time);
            d2 = format.parse(now_time);

            // 毫秒ms
            long diff = d2.getTime() - d1.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays != 0) {
                if (diffDays < 30) {
                    if (1 < diffDays && diffDays < 2) {
                        backStr = context.getString(R.string.yesterday);
                    } else if (1 < diffDays && diffDays < 2) {
                        backStr = context.getString(R.string.The_day_before_yesterday);
                    } else {
                        backStr = String.valueOf(diffDays) + context.getString(R.string.Days_ago);
                    }
                } else {
                    backStr = context.getString(R.string.long_long_ago);
                }

            } else if (diffHours != 0) {
                backStr = String.valueOf(diffHours) +context.getString(R.string.An_hour_ago);

            } else if (diffMinutes != 0) {
                backStr = String.valueOf(diffMinutes) + context.getString(R.string.minutes_ago);

            } else {

                backStr = context.getString(R.string.just);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return backStr;

    }
    public static String getStringTime(long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date);
        return startTime;
    }
    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }


    public static class TimeInfo {
        private long startTime;
        private long endTime;

        public TimeInfo() {
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long var1) {
            this.startTime = var1;
        }

        public long getEndTime() {
            return this.endTime;
        }

        public void setEndTime(long var1) {
            this.endTime = var1;
        }
    }

}