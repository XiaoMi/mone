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

import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author shanwb
 * @date 2021-07-21
 */
@Data
public class ChannelMemory implements Serializable {

    public transient static final String DEFAULT_VERSION = "2.0";

    private Long channelId;

    private Input input;

    private String version;

    private Map<String, FileProgress> fileProgressMap;

    private Long currentTime;

    /**
     * todo 消息缓存列表，改为文件地址，需要时再加载
     */
    private List<LineMessage> messageList;

    @Data
    public static class FileProgress implements Serializable {

        private String currentFile;

        /**
         * 当前文件列表索引号，
         * 不是最大，表示采集有延迟
         */
        private Integer currentFileIdx;
        /**
         * unix中标识文件唯一性
         */
        private UnixFileNode unixFileNode;

        /**
         * 当前采集的最新行号
         */
        private Long currentRowNum;
        /**
         * 当前采集的最新字符号
         */
        private Long pointer;
        /**
         * 当前文件的最新字符号
         */
        private Long fileMaxPointer;
        /**
         * 文件列表，一般日志都会配split规则，拆成多个文件
         */
        private List<String> fileList;

        /**
         * 是否停止采集
         * - 停止采集的，agent重启时也会忽略采集
         * - 停止采集的，如果文件不存在，也会删除掉该memory记录
         */
        private Boolean finished;
        /**
         * 只有当部署在k8s上时才有值
         */
        private String podType;

    }

    @Data
    public static class UnixFileNode {
        private Long st_dev;
        private Long st_ino;
    }
}
