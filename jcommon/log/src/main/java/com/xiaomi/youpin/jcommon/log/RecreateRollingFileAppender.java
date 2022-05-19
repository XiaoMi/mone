package com.xiaomi.youpin.jcommon.log;

import ch.qos.logback.core.rolling.RollingFileAppender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 日志文件自检查的appender，日志文件或目录被删除后，会自动恢复
 * 用于替换logback的RollingFileAppender
 * @see ch.qos.logback.core.rolling.RollingFileAppender
 *
 * 用法：
 * <appender name="FILE" class="com.xiaomi.youpin.jcommon.log.RecreateRollingFileAppender">
 *    <fileCheckInterval>100</fileCheckInterval>
 *    其它配置请参考：RollingFileAppender
 * </appender>
 *
 * @author shanwenbang@xiaomi.com
 * @date 2021/3/9
 */
public class RecreateRollingFileAppender<E> extends RollingFileAppender<E> {
    /**
     * 定时检查文件是否存在的 时间间隔（单位秒，默认20s检查一次）;
     * 间隔配置 <= 0时，定时check不会生效
     */
    private long fileCheckInterval = 20;

    public long getFileCheckInterval() {
        return fileCheckInterval;
    }

    public void setFileCheckInterval(long fileCheckInterval) {
        this.fileCheckInterval = fileCheckInterval;
    }

    @Override
    public void start() {
        super.start();
        this.startCheckTask(this);
    }

    private void startCheckTask(final RecreateRollingFileAppender fileAppender) {
        if (fileCheckInterval < 1) {
            return;
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(
            () -> {
                if (!Files.exists(Paths.get(getFile()))) {
                    try {
                        fileAppender.openFile(getFile()); // recreates file
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },
            10,
            fileCheckInterval,
            TimeUnit.SECONDS);
    }

}
