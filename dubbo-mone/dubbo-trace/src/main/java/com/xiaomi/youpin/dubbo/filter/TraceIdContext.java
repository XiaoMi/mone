package com.xiaomi.youpin.dubbo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public class TraceIdContext {

    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private TraceIdContext() {
        pool.scheduleAtFixedRate(() -> {
            try {
                int size = ids.size();
                if (size > 100000) {
                    ids.clear();
                    logger.error("trace id size > 100000 :{}", size);
                } else {
                    List<String> list = ids.keySet().stream().limit(10).collect(Collectors.toList());
                    logger.debug("trace context id size:{} {}", size, list);
                }
            } catch (Throwable ex) {
                //ignore
            }

        }, 0, 60, TimeUnit.SECONDS);
    }

    private static final Logger logger = LoggerFactory.getLogger(TraceIdContext.class);

    private ConcurrentHashMap<String, AtomicInteger> ids = new ConcurrentHashMap<>();

    private static class LazyHolder {
        private static TraceIdContext ins = new TraceIdContext();
    }


    public static TraceIdContext ins() {
        return TraceIdContext.LazyHolder.ins;
    }

    public int getSpanId(String key) {
        AtomicInteger id = ids.compute(key, (s, atomicInteger) -> {
            if (null == atomicInteger) {
                return new AtomicInteger(0);
            } else {
                return atomicInteger;
            }
        });
        return id.incrementAndGet();
    }



    public void remove(String key) {
        ids.remove(key);
    }
}
