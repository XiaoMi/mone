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

package com.xiaomi.youpin.gateway.common;

/**
 * @author goodjava@qq.com
 */
public abstract class Keys {

    public static final String mockKey(String id) {
        return "gw:mock_data_" + id;
    }

    public static final String cacheKey(Long id, String str) {
        return "gw:cache_data_" + id + "_" + str;
    }

    public static final String ipAntiBrushKey(String str, String ip) {
        return "gw:ip_anti_brush_" + str + "_" + ip;
    }

    public static final String uidAntiBrushKey(String str, String uid) {
        return "gw:uid_anti_brush_" + str + "_" + uid;
    }

    public static final String scriptKey(String id) {
        return "gw:script_" + id;
    }

    public static final String scriptDebugKey(String id) {
        return "gw:script_debug_" + id;
    }

    public static final String groupKey(Long id) {
        return "gw:gc_" + id;
    }

    public static final String dsKey(Long id) {
        return "gw:ds_" + id;
    }

    public static final String pluginKey(String name) {
        return "gw:plugin_" + name;
    }

    public static final String systemFilterSetKey() {
        return "gw:system_filter_set";
    }

    public static final String systemFilterKey(String name) {
        return "gw:system_filter_" + name;
    }


}
