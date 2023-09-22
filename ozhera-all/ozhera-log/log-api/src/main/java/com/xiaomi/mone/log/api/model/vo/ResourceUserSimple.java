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
     * Whether the resource list is initialized
     */
    private Boolean initializedFlag = false;
    /**
     * The presentation message content is not initialized
     */
    private String notInitializedMsg;
    /**
     * Whether the list of resources is displayed
     */
    private Boolean showFlag = false;
    /**
     * After displaying the MQ resource list, the list is filtered
     */
    private List<ValueKeyObj<Integer>> mqResourceList;
    /**
     * After displaying the ES resource list, the list is filtered
     */
    private List<ValueKeyObj<Integer>> esResourceList;


}
