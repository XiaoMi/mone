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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/11 14:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceUserSimple {
    /**
     * 资源列表是否初始化
     */
    private Boolean initializedFlag = false;
    /**
     * 没有初始化展示消息内容
     */
    private String notInitializedMsg;
    /**
     * 资源列表是否展示
     */
    private Boolean showFlag = false;
    /**
     * MQ资源列表展示后筛选列表
     */
    private List<ValueKeyObj<Integer>> mqResourceList;
    /**
     * ES资源列表展示后筛选列表
     */
    private List<ValueKeyObj<Integer>> esResourceList;


}
