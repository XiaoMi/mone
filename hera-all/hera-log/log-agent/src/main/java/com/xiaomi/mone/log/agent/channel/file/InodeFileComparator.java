package com.xiaomi.mone.log.agent.channel.file;

import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import com.xiaomi.mone.log.agent.common.ChannelUtil;
import org.apache.commons.io.comparator.DefaultFileComparator;

import java.io.File;
import java.util.Comparator;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/7/14 11:19
 */
public class InodeFileComparator extends DefaultFileComparator {

    public static final Comparator<File> INODE_COMPARATOR = new InodeFileComparator();

    @Override
    public int compare(File file1, File file2) {
        int sort = file1.compareTo(file2);
        if (sort == 0) {
            ChannelMemory.UnixFileNode unixFileNode1 = ChannelUtil.buildUnixFileNode(file1.getAbsolutePath());
            ChannelMemory.UnixFileNode unixFileNode2 = ChannelUtil.buildUnixFileNode(file2.getAbsolutePath());
            if (unixFileNode1.getSt_ino() != unixFileNode2.getSt_ino()) {
                return 1;
            }
        }
        return sort;
    }
}
