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
     * Refresh memory progress
     *
     * @param channelMemory
     */
    void refreshMemory(ChannelMemory channelMemory);

    /**
     * Get cache information
     *
     * @return
     */
    List<ChannelMemory> getMemory();

    /**
     * Get cache information
     *
     * @param channelId
     * @return
     */
    ChannelMemory getMemory(Long channelId);

    /**
     * Restore memory from disk
     *
     * @return
     */
    List<ChannelMemory> restoreFromDisk();

    /**
     * Flush to disk
     *
     * @param channelMemoryList
     */
    void flush2disk(List<ChannelMemory> channelMemoryList);

    /**
     * Compare files to prevent them from becoming too large
     *
     * @param filePaths All files that actually exist
     * @param channelId
     */
    void cleanChannelMemoryContent(Long channelId, List<String> filePaths);

    /**
     * Manage deleted tail memory files
     *
     * @param channelIds Full collection of chanel IDs
     */
    void cleanMemoryHistoryFile(List<Long> channelIds);

}
