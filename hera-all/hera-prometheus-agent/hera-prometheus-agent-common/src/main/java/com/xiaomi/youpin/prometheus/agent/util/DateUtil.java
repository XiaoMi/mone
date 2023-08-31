package com.xiaomi.youpin.prometheus.agent.util;

import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {


    @SneakyThrows
    public static String Time2YYMMdd(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss",Locale.CHINA);
        Date date = inputFormat.parse(inputDate);
        String outputDate = outputFormat.format(date);
        return outputDate;
    }

}
