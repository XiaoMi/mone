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
import lombok.EqualsAndHashCode;

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
     * Message cache list, changed to file address, loaded again when needed
     */
    private List<LineMessage> messageList;

    @Data
    public static class FileProgress implements Serializable {

        private String currentFile;
        /**
         * collection time
         */
        private Long ctTime;

        /**
         * The index number of the current file list is not the largest, indicating that the collection is delayed
         */
        private Integer currentFileIdx;
        /**
         * Identify the uniqueness of files in unix
         */
        private UnixFileNode unixFileNode;

        /**
         * The latest line number currently collected
         */
        private Long currentRowNum;
        /**
         * The latest character symbol currently collected
         */
        private Long pointer;
        /**
         * The latest character symbol of the current file
         */
        private Long fileMaxPointer;
        /**
         * File list. Generally, logs will be equipped with split rules and split into multiple files.
         */
        private List<String> fileList;

        /**
         * Whether to stop the collection
         * - if the collection is stopped, the agent will ignore the collection when it restarts
         * - if the collection is stopped, if the file does not exist, the memory record will also be deleted
         */
        private Boolean finished;
        /**
         * Only has value when deployed on k8s
         */
        private String podType;

    }

    @Data
    @EqualsAndHashCode
    public static class UnixFileNode {
        private Long st_dev;
        private Long st_ino;
    }
}
