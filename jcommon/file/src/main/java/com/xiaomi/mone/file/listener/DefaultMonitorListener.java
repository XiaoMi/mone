package com.xiaomi.mone.file.listener;

import com.xiaomi.mone.file.LogFile2;
import com.xiaomi.mone.file.common.SafeRun;
import com.xiaomi.mone.file.event.EventListener;
import com.xiaomi.mone.file.event.EventType;
import com.xiaomi.mone.file.event.FileEvent;
import com.xiaomi.mone.file.ozhera.HeraFileMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 09:49
 */
@Slf4j
public class DefaultMonitorListener implements EventListener {

    private HeraFileMonitor monitor;

    private ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    public DefaultMonitorListener(HeraFileMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void onEvent(FileEvent event) {
        if (event.getType().equals(EventType.init)) {
            log.info("log file:{}", event.getFileName());
            LogFile2 logFile = new LogFile2(event.getFileName());
            pool.submit(() -> {
                logFile.setListener(new OzHeraReadListener(monitor, logFile));
                SafeRun.run(() -> logFile.readLine());
            });
        }

        if (event.getType().equals(EventType.rename)) {
            log.info("rename:{} {}", event.getFileKey(), event.getFileName());
            monitor.getMap().remove(event.getFileKey());
        }

        if (event.getType().equals(EventType.delete)) {
            log.info("delete:{}", event.getFileName());
        }

        if (event.getType().equals(EventType.empty)) {
            log.info("empty:{}", event.getFileName());
            monitor.getMap().get(event.getFileKey()).getPointer().set(-1);
        }

        if (event.getType().equals(EventType.create)) {
            log.info("create:{}", event.getFileName());
            LogFile2 logFile = new LogFile2(event.getFileName());
            pool.submit(() -> {
                logFile.setListener(new OzHeraReadListener(monitor, logFile));
                SafeRun.run(() -> logFile.readLine());
            });
        }
    }
}
