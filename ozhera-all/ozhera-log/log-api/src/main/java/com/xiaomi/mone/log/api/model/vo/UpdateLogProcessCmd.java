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
 * It doesn't matter if docker corresponds to one IP, K8S has multiple pods under a node, so a tail may have multiple IPs
 */
@Data
public class UpdateLogProcessCmd implements Serializable {
    /**
     * Machine IP->K8S corresponds to Node IP
     */
    private String ip;

    private List<CollectDetail> collectList;

    @Data
    @EqualsAndHashCode
    public static class CollectDetail implements Serializable {

        private String tailId;

        private String tailName;
        /**
         * k8s may have multiple IPs
         */
        private List<String> ipList;
        /**
         * Configured path (original path)
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
        // The path to the real acquisition file
        private String pattern;
        // Design the configured IP
        private String configIp;

        // Log file line number
        private Long fileRowNumber;

        private Long pointer;

        private Long fileMaxPointer;

        // Collection time
        private Long collectTime;

        // Collect progress
        private String collectPercentage;
    }
}


