package com.xiaomi.mone.file;

import com.xiaomi.mone.file.event.EventListener;
import com.xiaomi.mone.file.event.FileEvent;
import com.xiaomi.mone.file.ozhera.HeraFileMonitor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 09:56
 */
@Slf4j
public class FileMonitorUtilsTest {


    @Test
    public void test1() throws IOException, InterruptedException {
        new HeraFileMonitor(event -> log.info("{}", event)).reg("/tmp/e/");
        System.in.read();
    }

}
