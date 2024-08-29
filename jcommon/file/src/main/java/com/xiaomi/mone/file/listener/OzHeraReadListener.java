package com.xiaomi.mone.file.listener;

import com.xiaomi.mone.file.LogFile2;
import com.xiaomi.mone.file.ReadEvent;
import com.xiaomi.mone.file.ReadListener;
import com.xiaomi.mone.file.common.SafeRun;
import com.xiaomi.mone.file.ozhera.HeraFile;
import com.xiaomi.mone.file.ozhera.HeraFileMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 16:08
 */
@Slf4j
public class OzHeraReadListener implements ReadListener {

    private HeraFileMonitor monitor;

    private LogFile2 logFile;

    private Consumer<ReadEvent> consumer;

    public OzHeraReadListener(HeraFileMonitor monitor, LogFile2 logFile, Consumer<ReadEvent> consumer) {
        this.monitor = monitor;
        this.logFile = logFile;
        this.consumer = consumer;
    }

    @Override
    public void onEvent(ReadEvent event) {
        consumer.accept(event);
    }

    @Override
    public boolean isContinue(String line) {
        if (null == line) {
            SafeRun.run(() -> TimeUnit.MILLISECONDS.sleep(300));
            return true;
        }
        return false;
    }

    @Override
    public void saveProgress() {
        logFile.saveProgress();
    }

    @Override
    public boolean isBreak(String line) {
        if (null == line) {
            HeraFile f = monitor.getMap().get(logFile.getFileKey());
            if (null == f || f.getState().get() == 1) {
                log.info("file isBreak,file:{},f:{}", logFile.getFile(), f);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPointer(Object obj) {
        if (obj instanceof LogFile2) {
            LogFile2 lf = (LogFile2) obj;
            HeraFile f = monitor.getMap().get(logFile.getFileKey());
            if (null != f && f.getPointer().get() == -1) {
                lf.setPointer(-1);
                f.getPointer().set(0);
            }
        }
    }

    @Override
    public void setReadTime() {
        HeraFile f = monitor.getFileMap().get(logFile.getFileKey());
        if (null != f) {
            f.getReadTime().set(System.currentTimeMillis());
        }
    }
}
