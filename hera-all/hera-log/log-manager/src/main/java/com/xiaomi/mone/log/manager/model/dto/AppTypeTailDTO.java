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

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/26 19:43
 */
@Data
public class AppTypeTailDTO {

    private Integer appType;

    private String appTypName;

    private List<TailApp> tailAppList;

    @Data
    public static class TailApp {

        private String nameEn;

        private String nameCn;

        private List<TailInfo> tailInfos;
    }

    @Data
    public static class TailInfo {
        private Long id;
        private String tailName;
    }
}
