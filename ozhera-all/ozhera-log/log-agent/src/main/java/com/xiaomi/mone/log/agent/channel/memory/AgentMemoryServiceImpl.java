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
package com.xiaomi.mone.log.agent.channel.memory;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.file.FileUtils;
import com.xiaomi.mone.file.ReadResult;
import com.xiaomi.mone.log.agent.common.AbstractElementAdapter;
import com.xiaomi.mone.log.agent.common.ExecutorUtil;
import com.xiaomi.mone.log.agent.exception.AgentException;
import com.xiaomi.mone.log.agent.input.Input;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shanwb
 * @date 2021-07-19
 */
@Slf4j
public class AgentMemoryServiceImpl implements AgentMemoryService {

    private static ConcurrentHashMap<Long, File> memoryFileList = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ChannelMemory> channelMemoryMap = new ConcurrentHashMap<>();

    private String basePath;
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Input.class, new AbstractElementAdapter());
        gson = gsonBuilder.create();
    }

    public AgentMemoryServiceImpl(String basePath) {
        this.basePath = basePath;
        initFolder(this.basePath + MEMORY_DIR);
        initChannelMemory();
        //Brush the disc regularly for 30 s
        initFlushTask(this);
    }

    private static void initFolder(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            try {
                Files.createDirectories(file.toPath());
            } catch (IOException e) {
                log.error("initFolder:{} exception:{}", dir, e);
                throw new AgentException(dir + " create exception", e);
            }
        } else {
            log.info("dir:{} is exists,no need to create", dir);
        }
    }

    private void initChannelMemory() {
        List<ChannelMemory> channelMemoryList = this.restoreFromDisk();
        if (CollectionUtils.isEmpty(channelMemoryList)) {
            return;
        }

        channelMemoryList.forEach(c -> {
            if (c != null && ChannelMemory.DEFAULT_VERSION.equals(c.getVersion())) {
                channelMemoryMap.put(c.getChannelId(), c);
            }
        });
    }

    private void initFlushTask(AgentMemoryService agentMemoryService) {
        ExecutorUtil.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                agentMemoryService.flush2disk(agentMemoryService.getMemory());
            }
        }, 10, 30, TimeUnit.SECONDS);
    }


    @Override
    public void refreshMemory(ChannelMemory channelMemory) {
        //refresh memory
        channelMemoryMap.put(channelMemory.getChannelId(), channelMemory);
        //refresh disk
        this.flush2disk(Lists.newArrayList(channelMemory));
    }

    @Override
    public List<ChannelMemory> getMemory() {
        return new ArrayList<>(channelMemoryMap.values());
    }

    @Override
    public ChannelMemory getMemory(Long channelId) {
        return channelMemoryMap.get(channelId);
    }

    @Override
    public List<ChannelMemory> restoreFromDisk() {
        List<ChannelMemory> channelMemoryList = new ArrayList<>();
        File file = new File(this.basePath + MEMORY_DIR);
        File[] fs = file.listFiles();
        if (null == fs || fs.length == 0) {
            return Lists.newArrayList();
        }

        for (File f : fs) {
            if (!f.isDirectory()) {
                try {
                    if (f.getName().startsWith(CHANNEL_FILE_PREFIX)) {
                        ReadResult readResult = FileUtils.readFile(f.getAbsolutePath(), 0, 0);
                        List<String> list = readResult.getLines();
                        if (CollectionUtils.isNotEmpty(list)) {
                            String channel = list.get(0);
                            ChannelMemory channelMemory = gson.fromJson(channel, ChannelMemory.class);


                            if (channelMemory != null && ChannelMemory.DEFAULT_VERSION.equals(channelMemory.getVersion())) {
                                channelMemoryList.add(channelMemory);
                            }
                            log.warn("restoreFromDisk channel:{},channelMemory:{}", channel, channelMemory);
                        }
                    }
                } catch (Exception e) {
                    log.error("restoreFromDisk error,file:{}", f.getName(), e);
                }
            }
        }

        return channelMemoryList;
    }

    @Override
    public void flush2disk(List<ChannelMemory> channelMemoryList) {
        for (ChannelMemory channelMemory : channelMemoryList) {
            FileWriter writer = null;
            try {
                Long channelId = channelMemory.getChannelId();
                File memoryFile = memoryFileList.get(channelId);
                if (null == memoryFile) {
                    memoryFile = new File(this.basePath + MEMORY_DIR + CHANNEL_FILE_PREFIX + channelId);
                    if (!memoryFile.exists()) {
                        try {
                            memoryFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // put
                    memoryFileList.put(channelId, memoryFile);
                }

                writer = new FileWriter(memoryFile, false);
                writer.append(gson.toJson(channelMemory));
                writer.flush();
            } catch (Throwable e) {
                log.error("flush2disk error, channelId:{}", channelMemory.getChannelId(), e);
            }
        }
    }

    @Override
    public void cleanChannelMemoryContent(Long channelId, List<String> filePaths) {
        ChannelMemory channelMemory = channelMemoryMap.get(channelId);
        if (null != channelMemory) {
            Map<String, ChannelMemory.FileProgress> fileProgressMap = channelMemory.getFileProgressMap();
            List<String> expireFilePaths = fileProgressMap.keySet().stream()
                    .filter(filePath -> !filePaths.contains(filePath))
                    .collect(Collectors.toList());
            expireFilePaths.forEach(expireFilePath -> {
                fileProgressMap.remove(expireFilePath);
            });
            flush2disk((Lists.newArrayList(channelMemory)));
        }
    }

    @Override
    public void cleanMemoryHistoryFile(List<Long> channelIds) {
        try {
            log.info("all channelIds:{}", gson.toJson(channelIds));
            List<String> channelIdList = channelIds.stream().map(String::valueOf).collect(Collectors.toList());
            List<File> files = FileUtil.loopFiles(this.basePath + MEMORY_DIR);
            Map<String, File> fileMap = files.stream()
                    .collect(Collectors
                            .toMap(File::getName, Function.identity(), (file, file2) -> file2));
            for (String fileName : fileMap.keySet()) {
                if (!channelIdList.contains(StringUtils.substringAfter(fileName, CHANNEL_FILE_PREFIX))) {
                    fileMap.get(fileName).delete();
                }
            }
        } catch (Exception e) {
            log.error("cleanMemoryHistoryFile error,channelIds:{}", gson.toJson(channelIds), e);
        }
    }

    /**
     * Repair data usage, this does not need to be used
     */
    private void fixedData() {
        channelMemoryMap.entrySet().stream().forEach(memoryEntry -> {
            FileWriter writer;
            ChannelMemory channelMemory = memoryEntry.getValue();
            channelMemory.getFileProgressMap().entrySet().forEach(progressEntry -> {
                progressEntry.getValue().setFinished(Boolean.FALSE);
            });
            try {
                File memoryFile = new File(this.basePath + MEMORY_DIR + CHANNEL_FILE_PREFIX + memoryEntry.getKey());
                writer = new FileWriter(memoryFile, false);
                writer.append(gson.toJson(channelMemory));
                writer.flush();
            } catch (Throwable e) {
                log.error("flush2disk error, channelId:{}", channelMemory.getChannelId(), e);
            }
        });
    }
}
