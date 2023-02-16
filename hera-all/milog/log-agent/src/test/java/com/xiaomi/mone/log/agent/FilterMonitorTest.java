package com.xiaomi.mone.log.agent;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.file.FileListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author: wtt
 * @date: 2022/6/2 12:12
 * @description:
 */
@Slf4j
public class FilterMonitorTest {

    @Test
    public void testScheduleExecutor() throws IOException {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            log.info("current thread:{}", Thread.currentThread().getName());
        }, 2, 7, TimeUnit.SECONDS);
        new Thread(() -> {
            ScheduledFuture<?> scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
                log.info("child current thread:{}", Thread.currentThread().getName());
            }, 5, 7, TimeUnit.SECONDS);
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scheduledFuture.cancel(true);
        }).start();
        System.in.read();
    }

    @Test
    public void test() throws IOException {
        Consumer<String> consumer = s -> {
            System.out.println("file come:" + s);
        };
        List<String> watchList = Lists.newArrayList("/home/work/log/test/");
        FileAlterationMonitor monitor = new FileAlterationMonitor(5000);
        log.info("agent monitor files:{}", new Gson().toJson(watchList));
        for (String watch : watchList) {
            File watchFile = new File(watch);
//            if (!watchFile.exists()) {
//                log.error("##!!## agent monitor file not exists:{}, filePattern:{}", watch, filePattern);
//                continue;
//            }

            FileAlterationObserver observer = new FileAlterationObserver(new File(watch));
            observer.addListener(new FileListener(consumer));

            log.info("## agent monitor file:{}, filePattern:{}", watch);
            monitor.addObserver(observer);
        }

        try {
            monitor.start();
            log.info("## agent monitor filePattern:{} started");
        } catch (Exception e) {
            log.error(String.format("agent file monitor start err,monitor filePattern:%s"), e);
        }
        System.in.read();
    }
}
