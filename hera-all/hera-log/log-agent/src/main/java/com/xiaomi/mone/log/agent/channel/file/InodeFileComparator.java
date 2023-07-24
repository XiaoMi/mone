package com.xiaomi.mone.log.agent.channel.file;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.agent.channel.memory.ChannelMemory;
import com.xiaomi.mone.log.agent.common.ChannelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.comparator.DefaultFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.util.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/7/14 11:19
 */
@Slf4j
public class InodeFileComparator extends DefaultFileComparator {

    public static final Comparator<File> INODE_COMPARATOR = new InodeFileComparator();

    private static final Comparator<File> fileComparator = NameFileComparator.NAME_SYSTEM_COMPARATOR;

    private static final Map<String, Long> INODE_MAP = new HashMap<>();

    private static final List<String> filePaths = Lists.newArrayList();

    @Override
    public int compare(File file1, File file2) {
        if (file1.isDirectory() || file2.isDirectory()) {
            return 0;
        }
        int sort = fileComparator.compare(file1, file2);
        if (sort == 0 && filePaths.contains(file1.getAbsolutePath())) {
            //文件名称相同
            Long oldInode;
            if (INODE_MAP.containsKey(file1.getAbsolutePath())) {
                oldInode = INODE_MAP.get(file1.getAbsolutePath());
            } else {
                oldInode = ChannelUtil.buildUnixFileNode(file1.getAbsolutePath()).getSt_ino();
                INODE_MAP.put(file1.getAbsolutePath(), oldInode);
            }
            ChannelMemory.UnixFileNode unixFileNode2 = ChannelUtil.buildUnixFileNode(file2.getAbsolutePath());
            if (!Objects.equals(oldInode, unixFileNode2.getSt_ino())) {
                INODE_MAP.put(file2.getAbsolutePath(), unixFileNode2.getSt_ino());
                return 1;
            }
        }
        return sort;
    }

    public static void addFile(String filePath) {
        filePaths.add(filePath);
    }
}
