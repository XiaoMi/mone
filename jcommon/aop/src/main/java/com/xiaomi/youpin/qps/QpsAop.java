package com.xiaomi.youpin.qps;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 * 记录qps
 */
@Aspect
@Configuration
@Order(-1)
@Slf4j
public class QpsAop {

    private AtomicLong qpsNum = new AtomicLong();

    private AtomicLong num = new AtomicLong();


    private ConcurrentHashMap<String, AtomicLong> qpsMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, AtomicLong> map = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                num.set(qpsNum.get());
                qpsNum.set(0L);

                map.putAll(qpsMap);

                //去除已经无用的key
                Set<String> rmKeys = map.keySet().stream().filter(it -> !qpsMap.containsKey(it)).collect(Collectors.toSet());
                rmKeys.forEach(it->map.remove(it));

                qpsMap.clear();
            } catch (Throwable ex) {
                log.info("error:{}", ex.getMessage());
            }
        }, 5, 1, TimeUnit.SECONDS);
    }


    @Around(value = "@annotation(qps)")
    public Object qps(ProceedingJoinPoint joinPoint, Qps qps) throws Throwable {
        try {
            qpsNum.incrementAndGet();
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        }
    }


    public void incr() {
        this.qpsNum.incrementAndGet();
    }

    public void incr(String name) {
        this.qpsNum.incrementAndGet();
        qpsMap.compute(name, (s, atomicLong) -> {
            if (null == atomicLong) {
                return new AtomicLong(1);
            } else {
                atomicLong.incrementAndGet();
            }
            return atomicLong;
        });
    }


    public long getQps() {
        return this.num.get();
    }


    public long getQps(String name) {
        return map.getOrDefault(name, new AtomicLong(0)).get();
    }

}
