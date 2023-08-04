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
package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

@Getter
public enum ESClusterEnum {
    CN();
    private String dept;
    private String name;
    private String esClusterId;

    public static ESClusterEnum name2enum(String name) {
        for (ESClusterEnum ecEnum : ESClusterEnum.values()) {
            if (ecEnum.name.equals(name)) {
                return ecEnum;
            }
        }
        return null;
    }

    public static ESClusterEnum dept2enum(String dept) {
        for (ESClusterEnum ecEnum : ESClusterEnum.values()) {
            if (ecEnum.dept.equals(dept)) {
                return ecEnum;
            }
        }
        return null;
    }
}
