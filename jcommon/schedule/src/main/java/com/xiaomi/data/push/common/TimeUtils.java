package com.xiaomi.data.push.common;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class TimeUtils {


    public static boolean moreThanOneHour(long time) {
        LocalDateTime ldt0 = millsToLocalDateTime(time);
        LocalDateTime ldt1 = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        log.debug(ldt0 + ":" + ldt1);
        return ldt1.isAfter(ldt0);
    }


    public static LocalDateTime millsToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

}
