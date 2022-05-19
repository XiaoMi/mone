package com.xiaomi.youpin.jcommon.log.test;

import com.google.gson.Gson;
import com.xiaomi.youpin.jcommon.log.LogRecord;
import org.apache.dubbo.common.utils.NetUtils;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CommonTest {


    @Test
    public void testBoolean() {
        System.out.println(Boolean.TRUE.toString());
    }

    @Test
    public void testStreamGeneraotr() {
        Stream.generate(CommonTest::sleep).forEach(it->{
            System.out.println(it);
        });
    }


    @Test
    public void testGetHost() {
        String host = NetUtils.getLocalAddress().getHostAddress();
        System.out.println(host);
    }


    private static long sleep() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }


    @Test
    public void testException() {
        System.out.println("" + (new RuntimeException() instanceof Throwable));
        System.out.println(new Throwable() instanceof Throwable);
    }

    @Test
    public void testEnv() {
        System.out.println(System.getenv("host.ip1"));
    }


    @Test
    public void testGson() {
        LogRecord record = new LogRecord();
        record.setErrorInfo("error");
        record.setTraceId("123213a");
        record.setAppName("appName");
        record.setGroup("groupName");
        record.setClassName("className");
        record.setMethodName("methodName");
        record.setIp("127.0.0.1");
        record.setLevel("INFO");
        record.setMessage("message");
        record.setPid("1233");
        record.setLine("12");
        record.setTag("tag");
        record.setThreadName("main");
        record.setTime("time");
        record.setTimestamp(111L);
        System.out.println(new Gson().toJson(record));
    }


    @Test
    public void testMap() {
        ConcurrentHashMap<String, AtomicLong>m = new ConcurrentHashMap<>();
        IntStream.range(0,100000).parallel().forEach(it->{
            m.compute("name",(s, atomicLong) -> {
                if (null == atomicLong) {
                    return new AtomicLong(1L);
                } else {
                    atomicLong.incrementAndGet();
                }
                return atomicLong;
            });
        });

        System.out.println(m);
    }


    @Test
    public void testMap2() {
        ConcurrentHashMap<String, AtomicLong>m = new ConcurrentHashMap<>();
        m.put("name",new AtomicLong(0));

    }
}
