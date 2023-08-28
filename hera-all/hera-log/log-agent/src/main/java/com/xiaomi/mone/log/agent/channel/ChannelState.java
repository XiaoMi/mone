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

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author shanwb
 * @date 2021-08-26
 */
@Data
public class ChannelState implements Serializable {

    private Long tailId;

    private String tailName;

    private Long appId;

    private String appName;

    private String logPattern;
    /**
     * 由appId + logPattern生成
     */
    private String logPatternCode;
    /**
     * 总采集发送行数
     */
    private Long totalSendCnt;

    private List<String> ipList;

    private Long collectTime;

    private Map<String, StateProgress> stateProgressMap;

    @Data
    public static class StateProgress implements Serializable {
        /**
         * ip
         */
        private String ip;
        /**
         * 当前采集文件
         */
        private String currentFile;
        /**
         * 当前采集的最新行号
         */
        private Long currentRowNum;
        /**
         * 当前采集的最新字符号
         */
        private Long pointer;

        /**
         * 当前文件的最大字符号
         */
        private Long fileMaxPointer;

        /**
         * 采集时间
         */
        private Long ctTime;
    }
}
