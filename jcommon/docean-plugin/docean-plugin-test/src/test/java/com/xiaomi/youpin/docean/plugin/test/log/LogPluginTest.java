package com.xiaomi.youpin.docean.plugin.test.log;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.log.Log;
import com.xiaomi.youpin.docean.plugin.log.LogWriter;
import com.xiaomi.youpin.docean.plugin.log.Logger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2020/7/8
 */
@Slf4j
public class LogPluginTest {


    @Test
    public void testLog() throws InterruptedException, IOException {
        if (Files.exists(Paths.get("/tmp/data"))) {
            Files.delete(Paths.get("/tmp/data"));
        }

        Log log = new Log();
        LogWriter lw = new LogWriter("/tmp/data");
        lw.init(0, 1024 * 1024 * 20 * 100);
//        lw.init(0, 1);
        log.setLogWriter(lw);
        log.init();

        long start = System.currentTimeMillis();
        System.out.println("start: " + start);
        IntStream.range(0, 1000000).parallel().forEach(it -> {
            log.info("", "abcssssssssss, xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        });
        long end = System.currentTimeMillis();
        System.out.println("end: " + end);

        log.shutdown();
        System.out.println("cost time: " + (end - start));
    }

    //6203
    //2700
    @Test
    public void testLog2() throws InterruptedException, IOException {
        Log log = new Log();
        LogWriter lw = new LogWriter("/tmp/data");
        lw.init(0, 1024 * 1024 * 20 * 100);
        log.setLogWriter(lw);

        log.init();

        Config config = new Config();

        Ioc.ins().putBean(log).putBean(config);

        Logger logger = new Logger(LogPluginTest.class);

        long start = System.currentTimeMillis();
        System.out.println("start: " + start);
        IntStream.range(0, 1000000).parallel().forEach(it -> {
            logger.info("abcssssssssss, xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        });
        long end = System.currentTimeMillis();
        System.out.println("end: " + end);

        log.shutdown();
        System.out.println("cost time: " + (end - start));
    }


    //47485
    @Test
    public void testLog3() {
        long start = System.currentTimeMillis();
        System.out.println("start: " + start);
        IntStream.range(0, 1000000).parallel().forEach(it -> {
            log.info("abcssssssssss, xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        });
        long end = System.currentTimeMillis();
        System.out.println("end: " + end);
        System.out.println("cost time: " + (end - start));
    }
}
