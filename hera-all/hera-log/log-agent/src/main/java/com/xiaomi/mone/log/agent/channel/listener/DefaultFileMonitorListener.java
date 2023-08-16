/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.channel.listener;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.ChannelService;
import com.xiaomi.mone.log.agent.channel.file.FileMonitor;
import com.xiaomi.mone.log.agent.channel.file.MonitorFile;
import com.xiaomi.mone.log.agent.common.ExecutorUtil;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.common.PathUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.PathUtils.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/4 15:09
 */
public class DefaultFileMonitorListener implements FileMonitorListener {

    private static Logger LOGGER = LoggerFactory.getLogger(DefaultFileMonitorListener.class);

    private static Gson gson = new Gson();

    /**
     * 默认监听的文件夹
     */
    private String defaultMonitorPath = "/home/work/log/";
    /**
     * 真实监听的文件夹列表
     */
    List<String> pathList = new CopyOnWriteArrayList<>();
    /**
     * 实际的监听器列表
     */
    private List<FileAlterationMonitor> monitorList = new CopyOnWriteArrayList();
    /**
     * 每个监听的线程
     */
    private Map<String, Future<?>> scheduledFutureMap = new ConcurrentHashMap<>();
    /**
     * 每个ChannelService 对应监听的文件
     */
    Map<List<MonitorFile>, ChannelService> pathChannelServiceMap = new ConcurrentHashMap<>();

    private final List<String> specialFileNameSuffixList = Lists.newArrayList("wf");

    private static final int DEFAULT_FILE_SIZE = 50000;

    public DefaultFileMonitorListener() {
        //Check if there are too many files, if there are more than 50,000 files,
        // then it cannot be monitored.
        long size = getDefaultFileSize();
        LOGGER.info("defaultMonitorPath:{} file size:{}", defaultMonitorPath, size);
        if (size < DEFAULT_FILE_SIZE) {
            pathList.add(defaultMonitorPath);
            this.startFileMonitor(defaultMonitorPath);
        }
    }

    private long getDefaultFileSize() {
        CompletableFuture<Integer> fileSizeFuture = CompletableFuture
                .supplyAsync(() -> FileUtils.listFiles(new File(defaultMonitorPath), null, true).size());
        try {
            return fileSizeFuture.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.info("getDefaultFileSize error", e);
        }
        return DEFAULT_FILE_SIZE * 2;
    }

    @Override
    public void addChannelService(ChannelService channelService) {
        List<MonitorFile> monitorPathList = channelService.getMonitorPathList();
        List<String> newMonitorDirectories = newMonitorDirectories(monitorPathList);
        for (String watchDirectory : newMonitorDirectories) {
            if (isValidWatch(watchDirectory)) {
                pathList.add(watchDirectory);
                startFileMonitor(watchDirectory);
            }
        }
        pathChannelServiceMap.put(monitorPathList, channelService);
    }

    private boolean isValidWatch(String watchDirectory) {
        if (pathList.contains(watchDirectory)) {
            return false;
        }
        for (String path : pathList) {
            if (watchDirectory.startsWith(path)) {
                return false;
            }
        }
        return true;
    }

    private List<String> newMonitorDirectories(List<MonitorFile> monitorPathList) {
        LOGGER.info("start newMonitorDirectories:{}", gson.toJson(monitorPathList));
        List<String> newMonitorDirectories = Lists.newArrayList();
        Set<String> expressList = monitorPathList.stream().map(MonitorFile::getMonitorFileExpress).collect(Collectors.toSet());
        Set<String> realExpressList = Sets.newHashSet();
        /**
         * 处理多个路径拼接起来的，如：(/home/work/data/logs/mishop-oscar/mishop-oscar-.*|/home/work/data/logs/mishop-oscar/mishop-oscar-current.log.*)
         */
        for (String express : expressList) {
            if (express.startsWith(MULTI_FILE_PREFIX) && express.endsWith(MULTI_FILE_SUFFIX)
                    && express.contains("|")) {
                for (String perExpress : StringUtils.substringBetween(express, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX).split(SPLIT_VERTICAL_LINE)) {
                    realExpressList.add(perExpress);
                }
            } else {
                realExpressList.add(express);
            }
        }
        for (String perExpress : realExpressList) {
            if (pathList.stream().noneMatch(perExpress::startsWith)) {
                List<String> watchDList = PathUtils.parseWatchDirectory(perExpress);
                /**
                 * 已经是最干净的目录了，只会有1个
                 */
                String monitorDirectory = watchDList.get(0);
                if (monitorDirectory.contains(".*")) {
                    monitorDirectory = StringUtils.substringBefore(monitorDirectory, ".*");
                }
                if (pathList.stream().noneMatch(monitorDirectory::startsWith)) {
                    newMonitorDirectories.add(monitorDirectory);
                }
            }
        }
        newMonitorDirectories = newMonitorDirectories.stream().distinct().collect(Collectors.toList());
        LOGGER.info("end newMonitorDirectories:", gson.toJson(newMonitorDirectories));
        return newMonitorDirectories;
    }

    @Override
    public void removeChannelService(ChannelService channelService) {
        try {
            pathChannelServiceMap.remove(channelService.getMonitorPathList());
            List<MonitorFile> monitorPathList = channelService.getMonitorPathList();
            List<String> newMonitorDirectories = newMonitorDirectories(monitorPathList);
            for (String watchDirectory : newMonitorDirectories) {
                pathList.remove(watchDirectory);
                if (scheduledFutureMap.containsKey(watchDirectory)) {
                    scheduledFutureMap.get(watchDirectory).cancel(true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("removeChannelService file listener,monitorPathList:{}", gson.toJson(channelService.getMonitorPathList()), e);
        }
    }

    public void startFileMonitor(String monitorFilePath) {
        LOGGER.debug("startFileMonitor,monitorFilePath:{}", monitorFilePath);
        Future<?> fileMonitorFuture = ExecutorUtil.submit(() -> {
            new FileMonitor().watch(monitorFilePath, monitorList, changedFilePath -> {
                try {
                    if (FileUtil.isDirectory(changedFilePath)) {
                        return;
                    }
                    LOGGER.info("monitor changedFilePath：{}", changedFilePath);
                    List<String> filterSuffixList = judgeSpecialFileNameSuffix(changedFilePath);
                    if (CollectionUtils.isNotEmpty(filterSuffixList)) {
                        specialFileSuffixChanged(changedFilePath, filterSuffixList);
                        return;
                    }
                    ordinaryFileChanged(changedFilePath);
                } catch (Exception e) {
                    LOGGER.error("FileMonitor error,monitorFilePath:{},changedFilePath:{}", monitorFilePath, changedFilePath, e);
                }
            });
        });
        scheduledFutureMap.put(monitorFilePath, fileMonitorFuture);
    }

    /**
     * 正常文件变化事件处理
     *
     * @param changedFilePath
     */
    private void ordinaryFileChanged(String changedFilePath) {
        for (Map.Entry<List<MonitorFile>, ChannelService> channelServiceEntry : pathChannelServiceMap.entrySet()) {
            for (MonitorFile monitorFile : channelServiceEntry.getKey()) {
                if (monitorFile.getFilePattern().matcher(changedFilePath).matches()) {
                    String reOpenFilePath = monitorFile.getRealFilePath();
                    /**
                     * OPENTELEMETRY 日志特殊处理
                     */
                    if (LogTypeEnum.OPENTELEMETRY == monitorFile.getLogTypeEnum()) {
                        reOpenFilePath = String.format("%s%s%s", StringUtils.substringBeforeLast(changedFilePath, SEPARATOR),
                                SEPARATOR, StringUtils.substringAfterLast(reOpenFilePath, SEPARATOR));
                    }
                    if (monitorFile.isCollectOnce()) {
                        reOpenFilePath = changedFilePath;
                    }
                    LOGGER.info("【change file path reopen】started,changedFilePath:{},realFilePath:{},monitorFileExpress:{}",
                            changedFilePath, reOpenFilePath, monitorFile.getMonitorFileExpress());
                    channelServiceEntry.getValue().reOpen(reOpenFilePath);
                    LOGGER.info("【end change file path】 end,changedFilePath:{},realFilePath:{},monitorFileExpress:{},InstanceId:{}",
                            changedFilePath, reOpenFilePath, monitorFile.getMonitorFileExpress(), channelServiceEntry.getValue().instanceId());
                }
            }

        }
    }

    /**
     * 特殊文件后缀变化事件处理
     * 通过实际观察，go项目发现好像日志的错误日志文件是server.log.wf 这样的，这样和正常的server.log就冲突了，
     * 都会收到重新开始的信息，因此为了兼容这样特殊的，需要把wf的单独拎出来判断
     */
    private void specialFileSuffixChanged(String changedFilePath, List<String> filterSuffixList) {
        Map<String, ChannelService> serviceMap = new HashMap<>();
        for (Map.Entry<List<MonitorFile>, ChannelService> channelServiceEntry : pathChannelServiceMap.entrySet()) {
            for (MonitorFile monitorFile : channelServiceEntry.getKey()) {
                if (filterSuffixList.stream()
                        .filter(s -> monitorFile.getRealFilePath().contains(s)).findAny().isPresent()
                        && monitorFile.getFilePattern().matcher(changedFilePath).matches()) {
                    serviceMap.put(monitorFile.getRealFilePath(), channelServiceEntry.getValue());
                }
            }
        }
        for (Map.Entry<String, ChannelService> serviceEntry : serviceMap.entrySet()) {
            serviceEntry.getValue().reOpen(serviceEntry.getKey());
        }
    }

    private List<String> judgeSpecialFileNameSuffix(String changedFilePath) {
        String changedFileName = StringUtils.substringAfterLast(changedFilePath, SEPARATOR);
        return specialFileNameSuffixList.stream()
                .filter(s -> changedFileName.contains(s))
                .collect(Collectors.toList());
    }


}
