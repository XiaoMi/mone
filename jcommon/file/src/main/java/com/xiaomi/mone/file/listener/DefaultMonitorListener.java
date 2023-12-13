package com.xiaomi.mone.file.listener;

import com.xiaomi.mone.file.LogFile2;
import com.xiaomi.mone.file.ReadEvent;
import com.xiaomi.mone.file.ReadListener;
import com.xiaomi.mone.file.common.SafeRun;
import com.xiaomi.mone.file.event.EventListener;
import com.xiaomi.mone.file.event.EventType;
import com.xiaomi.mone.file.event.FileEvent;
import com.xiaomi.mone.file.ozhera.HeraFileMonitor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 09:49
 */
@Slf4j
public class DefaultMonitorListener implements EventListener {

    private HeraFileMonitor monitor;

    private Consumer<ReadEvent> consumer;

    @Getter
    private List<ReadListener> readListenerList = new CopyOnWriteArrayList<>();

    private ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

    public DefaultMonitorListener(HeraFileMonitor monitor, Consumer<ReadEvent> consumer) {
        this.monitor = monitor;
        this.consumer = consumer;
    }

    @Override
    public void onEvent(FileEvent event) {
        if (event.getType().equals(EventType.init)) {
            log.info("log file:{}", event.getFileName());
            LogFile2 logFile = new LogFile2(event.getFileName());
            OzHeraReadListener ozHeraReadListener = new OzHeraReadListener(monitor, logFile, consumer);
            readListenerList.add(ozHeraReadListener);
            pool.submit(() -> {
                logFile.setListener(ozHeraReadListener);
                SafeRun.run(logFile::readLine);
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

//            LogFile2 logFile = new LogFile2(event.getFileName());
            LogFile2 logFile = new LogFile2(event.getFileName(), 0, 0);
            OzHeraReadListener ozHeraReadListener = new OzHeraReadListener(monitor, logFile, consumer);
            readListenerList.add(ozHeraReadListener);
            pool.submit(() -> {
                logFile.setListener(ozHeraReadListener);
                SafeRun.run(logFile::readLine);
            });
        }
    }
}
