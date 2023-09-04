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
package com.xiaomi.mone.log.manager.model.dto;

import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/24 15:45
 */
@Data
public class MilogAppConfigTailDTO {

    private Long id;
    private String appId;
    private String appName;
    private String source;
    private Integer type;
    private Long ctime;
    private String creator;

    private List<ConfigTailDTO> configTailDTOList;

    @Data
    public static class ConfigTailDTO {
        private Long middlewareId;
        private String type;
        private String middlewareName;
        private Long tailId;
        private String tailName;
        private String tailCreator;
        private Long tailCreateTime;
        private Long tailUpdateTime;
        private String tailUpdater;
        public MilogAppMiddlewareRel.Config mqConfig;
    }
}
