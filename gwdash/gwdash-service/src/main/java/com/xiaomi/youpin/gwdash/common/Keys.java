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

package com.xiaomi.youpin.gwdash.common;

import java.util.Calendar;

public abstract class Keys {

    public static final String mockKey(long id) {
        return "gw:mock_data_" + id;
    }

    public static final String mockDescKey(long id) {
        return "gw:mock_data_desc_" + id;
    }

    public static final String scriptKey(long id) {
        return "gw:script_" + id;
    }

    public static final String groupConfigKey(long id) {
        return "gw:gc_" + id;
    }

    public static final String gitScriptKey(long id) {
        return "gw:git:script_" + id;
    }

    public static final String scriptDebugKey(String id) {
        return "gw:script_debug_" + id;
    }

    public static final String dsKey(Integer id) {
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

    public static final String predictKey(long id) {
        return "gw:predict_project_" + id;
    }

    public static final String replicatesKey(long projectId, long envId) {
        return "gw:replicates_project_" + projectId + "_env_" + envId;
    }

    public static final String deployKey(long projectId) {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return "gw:deploy_project_" + month + ":" + projectId;
    }

}
