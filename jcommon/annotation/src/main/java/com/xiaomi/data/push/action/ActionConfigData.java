/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class ActionConfigData {

    private Map<String, ActionInfo> actionConfMap = new HashMap<>();

    private String version;

    private String time;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, ActionInfo> getActionConfMap() {
        return actionConfMap;
    }

    public void setActionConfMap(Map<String, ActionInfo> actionConfMap) {
        this.actionConfMap = actionConfMap;
    }
}
