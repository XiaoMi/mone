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

package com.xiaomi.youpin.tesla.billing.common;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author goodjava@qq.com
 */
public class TimeUtils {


    /**
     * 一个月的开始时间
     *
     * @return
     */
    public static long monthBegin(int year, int month) {
        LocalDate ld = LocalDate.of(year, Month.of(month), 1);
        Date date = Date.from(ld.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
        return date.getTime();
    }


    /**
     * 一个月的结束时间
     *
     * @return
     */
    public static long monthEnd(int year, int month) {
        LocalDate ld = LocalDate.of(year, Month.of(month), 1);
        ld = ld.with(TemporalAdjusters.lastDayOfMonth());
        Date date = Date.from(ld.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
        return date.getTime();
    }

    /**
     * 今天开始时间long类型
     *
     * @return
     */
    public static long dayBegin() {
        LocalDate date = LocalDate.now();
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 今天结束时间long类型
     *
     * @return
     */
    public static long dayEnd() {
        LocalDate localDateNow = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.parse(localDateNow + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

    /**
     * 获取上个月第一天
     *
     * @return
     */
    public static long lastMonthFirstDay() {
        LocalDate localDate = LocalDate.now();
        //上个月第一天
        LocalDate lastMonthFirstDay = LocalDate.of(localDate.getYear(), localDate.getMonthValue() - 1, 1);
        return lastMonthFirstDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取上个月最后一天
     *
     * @return
     */
    public static long lastMonthLastDay() {
        LocalDate localDate = LocalDate.now();
        //上个月第一天
        LocalDate lastMonthFirstDay = LocalDate.of(localDate.getYear(), localDate.getMonthValue() - 1, 1);
        //上个月的最后一天
        LocalDate lastMonthLastDay = lastMonthFirstDay.with(TemporalAdjusters.lastDayOfMonth());

        return TimeUtils.String2LongDate(lastMonthLastDay.toString() + " 23:59:59");
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    public static long thisMonthFirstDay() {
        LocalDate localDate = LocalDate.now();
        //本月第一天
        LocalDate thisMonthFirstDay = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
        return thisMonthFirstDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 字符串转LONG时间
     *
     * @param date
     * @return
     */
    public static long String2LongDate(String date) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(date, ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LONG转字符串时间
     *
     * @param time
     * @return
     */
    public static String Long2StringDate(Long time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }


    /**
     * long转LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime Long2Date(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 获取昨天时间
     * @return
     */
    public static long getYesterday() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }


}
