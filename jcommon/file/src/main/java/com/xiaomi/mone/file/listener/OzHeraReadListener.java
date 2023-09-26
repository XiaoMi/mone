package com.xiaomi.mone.file.listener;

import com.xiaomi.mone.file.LogFile2;
import com.xiaomi.mone.file.ReadEvent;
import com.xiaomi.mone.file.ReadListener;
import com.xiaomi.mone.file.common.SafeRun;
import com.xiaomi.mone.file.ozhera.HeraFile;
import com.xiaomi.mone.file.ozhera.HeraFileMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 16:08
 */
@Slf4j
public class OzHeraReadListener implements ReadListener {

    private HeraFileMonitor monitor;

    private LogFile2 logFile;

    public OzHeraReadListener(HeraFileMonitor monitor, LogFile2 logFile) {
        this.monitor = monitor;
        this.logFile = logFile;
    }

    @Override
    public void onEvent(ReadEvent event) {
        System.out.println(event.getReadResult().getLines());
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
    public boolean isBreak(String line) {
        if (null == line) {
            HeraFile f = monitor.getMap().get(logFile.getFileKey());
            if (null == f || f.getState().get() == 1) {
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
}
