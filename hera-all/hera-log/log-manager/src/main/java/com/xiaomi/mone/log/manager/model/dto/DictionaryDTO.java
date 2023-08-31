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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/26 15:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryDTO<T> {

    private T value;
    private String label;

    private boolean showDeploymentType;
    private boolean showEnvGroup;
    private boolean showMachineType;
    private boolean showMachineRegion;
    private boolean showServiceIp;
    private boolean showDeploymentSpace;
    private boolean showMqConfig;

    private List<DictionaryDTO> children;

    public DictionaryDTO(T value, String label, List<DictionaryDTO> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    public static DictionaryDTO Of(Object label, String value) {
        return new DictionaryDTO(label, value, null);
    }
}
