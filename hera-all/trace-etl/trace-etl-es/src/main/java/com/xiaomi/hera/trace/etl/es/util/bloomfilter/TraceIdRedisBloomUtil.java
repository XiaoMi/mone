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
        // Set a scheduled task to update the local bloom filter at 4:00 a.m. every day
        updateLocalBloomTimer();
        // Set a scheduled task to update the local bloom filter at noon every day
        updateLocalBloomTimerMiddle();
    }

    public boolean isExistLocal(String traceId) {
        try {
            return localBloomFilter.mightContain(traceId);
        } catch (Exception e) {
            log.error("judgment traceID: " + traceId + " whether there are failures in the local bloomfilter:", e);
        }
        return true;
    }

    public synchronized void addBatch(String traceId) {
        TraceIdRedisBloomUtil.localBloomFilter.put(traceId);
    }

    private void updateLocalBloomTimer() {
        // Calculates the difference between the update time and the current time
        long initDelay = getTimeMillis(localUpdateTime) - System.currentTimeMillis();
        /**
         * If the difference is less than 0, it indicates that the update time has passed,
         * and the difference of the next update time is calculated, that is, 24h is added
         */
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
        // Calculates the difference between the update time and the current time
        long initDelay = getTimeMillis(localUpdateTimeMiddle) - System.currentTimeMillis();
        // If the difference is less than 0, it indicates that the update time has passed, and the difference of the next update time is calculated, that is, 24h is added
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
     * Gets the number of milliseconds for a specified time
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
