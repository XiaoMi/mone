package com.xiaomi.youpin.redis.test;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.redis.monitor.MetricTypes;
import com.xiaomi.data.push.redis.monitor.RedisMonitor;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisTest {


    @Test
    public void testNx() {
        Redis redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();
        Long v = redis.setNx("name1", "zzy");
        System.out.println(v);
    }

    @Test
    public void testSet() {
        Redis redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();
        //单位毫秒
        String v = redis.set("name11", "zzy", 15000);
        System.out.println(v);
    }


    @Test
    public void testSetXX() {
        Redis redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();
        //单位毫秒
        String v = redis.set("name11", "zzy", "XX", 15000);
        System.out.println(v);
    }



    @Test
    public void testCluster() {
        Redis redis = new Redis();
        redis.setServerType("prod");
        redis.setRedisHosts("");
        redis.init();
        //单位毫秒
        redis.set("name11", "zzy11",  "NX", 10 * 60 * 1000);
        redis.set("name22", "zzy22", "NX",10 * 60 * 1000);
        redis.set("name12", "zzy12", "NX",10 * 60 * 1000);
        redis.set("name13", "zzy13", "NX",10 * 60 * 1000);
        redis.set("name14", "zzy14", "NX",10 * 60 * 1000);
        redis.set("name15", "zzy15", "NX",10 * 60 * 1000);
        redis.set("name16", "zzy16", "NX",10 * 60 * 1000);
        redis.set("name17", "zzy17", "NX",10 * 60 * 1000);
        redis.set("name18", "zzy18", "NX",10 * 1000);

        List<String> keys = new ArrayList<>();
        keys.add("name11");
        keys.add("name22");
        keys.add("name12");
        keys.add("name13");
        keys.add("name14");
        keys.add("name15");
        keys.add("name16");
        keys.add("name17");
        keys.add("name18");

        System.out.println(redis.get("name12") + ", " + redis.get("name14") + "," + redis.get("name18"));
        System.out.println(new Gson().toJson(redis.mget(keys)));

        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i=0; i< 100; i++) {
            redis.mget(keys);
        }
        stopwatch.stop();
        System.out.println("mget 耗时：" + stopwatch.toString());

        stopwatch.reset();
        stopwatch.start();
        for (int j=0; j< 9 * 100; j++) {
            redis.get(keys.get(j % 9));
        }
        System.out.println("get 耗时：" + stopwatch.toString());
    }

    @Test
    public void testMonitor() throws Exception {
//        HTTPServer server = new HTTPServer(4857);
        RedisMonitor redisMonitor = new RedisMonitor();
        redisMonitor.recordMonitorInfo(false,null,true,false,"INIT",MetricTypes.Gauge,"init",null,true);
        while(true){
            redisMonitor.recordMonitorInfo(false,null,true,false,"TestMonitor", MetricTypes.Counter,"get","testKeys",true);
            redisMonitor.recordMonitorInfo(false,null,true,false,"TestMonitor",MetricTypes.Counter,"get","testKeys",false);
            Thread.sleep(2000l);

        }

    }





}
