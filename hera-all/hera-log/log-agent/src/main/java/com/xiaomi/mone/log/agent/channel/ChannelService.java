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
     * 启动channel任务
     */
    void start();

    /**
     * 动态刷新channel配置
     *
     * @param channelDefine
     * @param msgExporter
     */
    void refresh(ChannelDefine channelDefine, MsgExporter msgExporter);

    /**
     * 停止指定的文件采集
     *
     * @param filePrefixList
     */
    void stopFile(List<String> filePrefixList);

    /**
     * 获取chanel当前最新状态
     *
     * @return
     */
    ChannelState state();

    /**
     * channel 实例id
     *
     * @return
     */
    String instanceId();

    /**
     * filter配置存在变更
     *
     * @param confs
     */
    void filterRefresh(List<FilterConf> confs);

    /**
     * 监听到变化重新开始采集文件
     *
     * @param filePath
     */
    void reOpen(String filePath);

    /**
     * 需要监听的文件列表
     *
     * @return
     */
    List<MonitorFile> getMonitorPathList();

    /**
     * openteltry日志多文件结束clean
     */
    void delayDeletionFinishedFile();

    /**
     * 删除某个目录的文件采集,适用于k8s中使用demonset方式部署时某个某个下线，需要删除它的采集,解除资源占用
     *
     * @param directory
     */
    void deleteCollFile(String directory);
}
