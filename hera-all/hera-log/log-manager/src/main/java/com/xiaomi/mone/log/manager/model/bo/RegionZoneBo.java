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
package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/22 17:37
 */
@Data
public class RegionZoneBo {
    private Integer code;
    private String message;
    private String userMessage;
    private String level;
    private List<InnerClass> data;

    @Data
    public static class InnerClass {
        private Integer id;
        private String zone_name_en;
        private String zone_name_cn;
        private String region_cn;
        private String region_en;
        private Boolean is_used;
    }
}
