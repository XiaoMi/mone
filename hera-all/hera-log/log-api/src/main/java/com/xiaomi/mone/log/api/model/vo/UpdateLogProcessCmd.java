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
package com.xiaomi.mone.log.api.model.vo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * docker 对应一个ip没关系,k8s一个node下有多个pod，因此一个tail可能会有多个ip的情况
 */
@Data
public class UpdateLogProcessCmd implements Serializable {
    /**
     * 机器ip->k8s对应node ip
     */
    private String ip;

    private List<CollectDetail> collectList;

    @Data
    @EqualsAndHashCode
    public static class CollectDetail implements Serializable {

        private String tailId;

        private String tailName;
        /**
         * k8s可能有多个ip
         */
        private List<String> ipList;
        /**
         * 配置的路径(原始路径)
         */
        private String path;

        private Long appId;

        private String appName;

        private List<FileProgressDetail> fileProgressDetails;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileProgressDetail implements Serializable {
        // 真实采集文件路径
        private String pattern;
        // 设计配置的ip
        private String configIp;

        // 日志文件行号
        private Long fileRowNumber;

        private Long pointer;

        private Long fileMaxPointer;

        // 收集时间
        private Long collectTime;

        // 收集进度
        private String collectPercentage;
    }
}


