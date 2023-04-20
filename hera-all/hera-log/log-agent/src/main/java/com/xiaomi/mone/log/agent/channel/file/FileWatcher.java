package com.xiaomi.mone.log.agent.channel.file;

import com.xiaomi.mone.file.LogFile;
import org.apache.commons.io.monitor.FileAlterationMonitor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author shanwb
 * @date 2021-07-19
 */
public interface FileWatcher {
    /**
     * watch目录文件变化
     * @param logFile
     */
    void watch(LogFile logFile);

    /**
     * watch目录文件变化
     * @param filePattern
     * @param monitorList
     * @param consumer
     */
    void watch(String filePattern, List<FileAlterationMonitor> monitorList, Consumer<String> consumer);

    /**
     *
     */
    void onChange();

}
