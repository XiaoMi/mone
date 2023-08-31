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
package com.xiaomi.mone.log.api.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class AlertInfo implements Serializable {

    private long alertId;
    private long ruleId;
    private Map<String, String> info;

    public void addInfo(String key, String value) {
        if (info == null) {
            info = new HashMap<>();
        }
        info.put(key, value);
    }

    public String getInfo(String key) {
        if (info == null) {
            info = new HashMap<>();
        }
        return info.get(key);
    }

    public String getInfoOrDefault(String key, String defaultValue) {
        if (info == null || key == null || "".equals(key)) {
            return defaultValue;
        }
        return info.get(key) != null && !"".equals(info.get(key)) ? info.get(key) : defaultValue;
    }

}
