/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/29 10:30
 */
public class DateUtils {

    public static long dayms = 86400000L;

    public static String getTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return dateTimeFormatter.format(LocalDateTime.now());
    }

    /**
     * Get the first millisecond today
     *
     * @return
     */
    public static long getTodayFirstMillisecond() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * Get yesterday's first millisecond
     *
     * @return]
     */
    public static long getYesterdayFirstMillisecond() {
        return getTodayFirstMillisecond() - dayms;
    }

    /**
     * Get the first millisecond of the day before yesterday
     *
     * @return]
     */
    public static long getBeforeYesterdayFirstMillisecond() {
        return getTodayFirstMillisecond() - dayms * 2;
    }

    /**
     * Gets the first millisecond of the day
     *
     * @param thisDay
     * @return
     */
    public static Long getThisDayFirstMillisecond(String thisDay) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(thisDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * Get n days ago date (yyyy-MM-dd)
     *
     * @param n
     * @return
     */
    public static String getDaysAgo(int n) {
        return new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis() - n * dayms);
    }

    public static String timeStamp2Date(String millSeconds, String format) {
        if (StringUtils.isBlank(millSeconds)) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(millSeconds)));
    }

}
