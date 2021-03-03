/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.common;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author zhangzhiyong
 * @date 04/06/2018
 */
public abstract class DateUtils {
    public static final long SECONDS_OF_DAY = 60 * 60 * 24;
    public static final long MILLISECONDS_OF_DAY = 60 * 60 * 24 * 1000;
    public static final long SECONDS_OF_HOUR = 60 * 60;
    public static final long MILLISECONDS_OF_HOUR = 60 * 60 * 1000;
    public static final long SECONDS_OF_MONTH = 60 * 60 * 24 * 30;

    public static String timeToStr() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddhhmm");
        return ldt.format(formatter);
    }


    public static String timeDayToStr() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMdd");
        return ldt.format(formatter);
    }


    public static String timeMinToStr(LocalDateTime ldt) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return ldt.format(formatter);
    }

    public static long getStartOfToday() {
        long now = System.currentTimeMillis() / 1000L;
        long daySecond = 60 * 60 * 24;
        long dayStart = now - (now + 8 * 3600) % daySecond;
        return dayStart;
    }

    public static long getEndOfToday() {
        long now = System.currentTimeMillis() / 1000L;
        long dayEnd = now - (now + 8 * SECONDS_OF_HOUR) % SECONDS_OF_DAY + SECONDS_OF_DAY;
        return dayEnd;
    }


    public static int getDateOfDaysBefore(int days) {
        long now = System.currentTimeMillis();
        long nowOfDaysBefore = now - MILLISECONDS_OF_DAY * days;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String time = fmt.format(new Date(nowOfDaysBefore));
        return Integer.valueOf(time);
    }

    /**
     * 根据输入的时间戳(秒)来获取指定的日期
     *
     * @param rawTime
     * @return
     */
    public static int getDateOfTime(long rawTime) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String time = fmt.format(new Date(rawTime * 1000));
        return Integer.valueOf(time);
    }


}
