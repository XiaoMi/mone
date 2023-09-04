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

import java.util.List;

/**
 * @author shanwb
 * @date 2021-07-19
 */
public interface AgentMemoryService {

    String MEMORY_DIR = "/milog/memory/";
    String CHANNEL_FILE_PREFIX = "channel_";
    String DEFAULT_BASE_PATH = "/tmp/";

    /**
     * 刷新内存进度
     *
     * @param channelMemory
     */
    void refreshMemory(ChannelMemory channelMemory);

    /**
     * 获取缓存信息
     *
     * @return
     */
    List<ChannelMemory> getMemory();

    /**
     * 获取缓存信息
     *
     * @param channelId
     * @return
     */
    ChannelMemory getMemory(Long channelId);

    /**
     * 从磁盘恢复内存
     *
     * @return
     */
    List<ChannelMemory> restoreFromDisk();

    /**
     * 刷新到磁盘
     *
     * @param channelMemoryList
     */
    void flush2disk(List<ChannelMemory> channelMemoryList);

    /**
     * 比较文件，防止文件过大
     *
     * @param filePaths 真实已经存在的所有文件
     * @param channelId
     */
    void cleanChannelMemoryContent(Long channelId, List<String> filePaths);

    /**
     * 清理已经删除的tail 内存文件
     *
     * @param channelIds 全量chanelIds集合
     */
    void cleanMemoryHistoryFile(List<Long> channelIds);

}
