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
package com.xiaomi.mone.log.agent.channel;

import com.xiaomi.mone.log.agent.channel.file.MonitorFile;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.api.model.meta.FilterConf;

import java.util.List;

/**
 * @author shanwb
 * @date 2021-07-19
 */
public interface ChannelService extends Closeable {
    /**
     * Start channel task.
     */
    void start();

    /**
     * Dynamic refresh channel configuration
     *
     * @param channelDefine
     * @param msgExporter
     */
    void refresh(ChannelDefine channelDefine, MsgExporter msgExporter);

    /**
     * Stop specified file collection.
     *
     * @param filePrefixList
     */
    void stopFile(List<String> filePrefixList);

    /**
     * Get the current latest status of Chanel.
     *
     * @return
     */
    ChannelState state();

    /**
     * channel instance id
     *
     * @return
     */
    String instanceId();

    /**
     * There have been changes in the filter configuration.
     *
     * @param confs
     */
    void filterRefresh(List<FilterConf> confs);

    /**
     * Listening for changes and restarting file collection.
     *
     * @param filePath
     */
    void reOpen(String filePath);

    /**
     * List of files to be monitored
     *
     * @return
     */
    List<MonitorFile> getMonitorPathList();

    /**
     * File cleanup needed
     */
    void cleanCollectFiles();

    /**
     * Delete the file collection of a certain directory, applicable when using the demonset deployment method in k8s, when a certain node goes offline, it needs to delete its collection and release resource occupation.
     *
     * @param directory
     */
    void deleteCollFile(String directory);
}
