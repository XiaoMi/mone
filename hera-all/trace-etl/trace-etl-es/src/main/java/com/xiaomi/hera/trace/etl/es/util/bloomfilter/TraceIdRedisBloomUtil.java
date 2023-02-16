package com.xiaomi.hera.trace.etl.es.util.bloomfilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TraceIdRedisBloomUtil {

    private String localUpdateTime = "04:00:00";
    private String localUpdateTimeMiddle = "12:00:00";

    private static final long LOCAL_EXPECTEDINSERTIONS = 100000000L;
    private static final double LOCAL_REDIS_ACCIRACY = 0.0001;

    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public static volatile BloomFilter<CharSequence> localBloomFilter;

    private Funnel<CharSequence> charSequenceFunnel = Funnels.stringFunnel(Charset.defaultCharset());

    @PostConstruct
    public void init() {
        localBloomFilter = BloomFilter.create(charSequenceFunnel, LOCAL_EXPECTEDINSERTIONS, LOCAL_REDIS_ACCIRACY);
        // 设置定时任务，每天凌晨四点更新local bloom filter
        updateLocalBloomTimer();
        // 设置定时任务，每天中午十二点更新local bloom filter
        updateLocalBloomTimerMiddle();
    }

    public boolean isExistLocal(String traceId) {
        try {
            return localBloomFilter.mightContain(traceId);
        } catch (Exception e) {
            log.error("判断traceID：" + traceId + " 在local bloomfilter中是否存在失败：", e);
        }
        return true;
    }

    public synchronized void addBatch(String traceId) {
        TraceIdRedisBloomUtil.localBloomFilter.put(traceId);
    }

    private void updateLocalBloomTimer() {
        // 计算更新时间距离当前时间的差值
        long initDelay = getTimeMillis(localUpdateTime) - System.currentTimeMillis();
        // 如果差值小于0，说明更新时间已过，就计算下一个更新时间的差值，即加24h
        initDelay = initDelay > 0 ? initDelay : PERIOD_DAY + initDelay;
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
                () -> {
                    updateLocalBloom();
                },
                initDelay,
                PERIOD_DAY,
                TimeUnit.MILLISECONDS);
    }

    private void updateLocalBloomTimerMiddle() {
        // 计算更新时间距离当前时间的差值
        long initDelay = getTimeMillis(localUpdateTimeMiddle) - System.currentTimeMillis();
        // 如果差值小于0，说明更新时间已过，就计算下一个更新时间的差值，即加24h
        initDelay = initDelay > 0 ? initDelay : PERIOD_DAY + initDelay;
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
                () -> {
                    updateLocalBloom();
                },
                initDelay,
                PERIOD_DAY,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"
     * @return
     */
    private static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (Exception e) {
            log.error("time transfer error : ", e);
        }
        return 0L;
    }

    private void updateLocalBloom(){
        localBloomFilter = BloomFilter.create(charSequenceFunnel, LOCAL_EXPECTEDINSERTIONS, LOCAL_REDIS_ACCIRACY);
        log.info("update local bloom filter success");
    }
}
