package com.xiaomi.mone.log.agent.channel.file;

import cn.hutool.core.util.ReflectUtil;
import com.xiaomi.data.push.rpc.common.RemotingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.io.monitor.FileEntry;

import java.io.File;
import java.io.FileFilter;

import static com.xiaomi.mone.log.agent.channel.file.InodeFileComparator.INODE_COMPARATOR;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/7/14 10:51
 */
@Slf4j
public class LogFileAlterationObserver extends FileAlterationObserver {

    public LogFileAlterationObserver(String directoryName) {
        this(new File(directoryName));
    }

    public LogFileAlterationObserver(String directoryName, FileFilter fileFilter) {
        this(new File(directoryName), fileFilter);
    }

    public LogFileAlterationObserver(String directoryName, FileFilter fileFilter, IOCase caseSensitivity) {
        this(new File(directoryName), fileFilter, caseSensitivity);
    }

    public LogFileAlterationObserver(File directory) {
        this(directory, null);
    }

    public LogFileAlterationObserver(File directory, FileFilter fileFilter) {
        this(directory, fileFilter, null);
    }

    public LogFileAlterationObserver(File directory, FileFilter fileFilter, IOCase caseSensitivity) {
        this(new FileEntry(directory), fileFilter, caseSensitivity);
    }

    protected LogFileAlterationObserver(FileEntry rootEntry, FileFilter fileFilter, IOCase caseSensitivity) {
        super(rootEntry, fileFilter, caseSensitivity);
        if (!RemotingUtil.isWindowsPlatform()) {
            log.info("LogFileAlterationObserver set comparator:{}", INODE_COMPARATOR);
            ReflectUtil.setFieldValue(this, "comparator", INODE_COMPARATOR);
        }
    }
}
