package run.mone.m78.service.common;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author wmin
 * @date 2024/5/24
 */
public class DateUtils {

    // 获取当前时间往后几天的时间戳（毫秒）
    public static long getFutureTimestampInMillis(int days) {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(days);
        return futureDate.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

}
