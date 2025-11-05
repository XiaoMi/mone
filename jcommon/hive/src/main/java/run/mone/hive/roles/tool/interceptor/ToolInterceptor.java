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
package run.mone.hive.roles.tool.interceptor;

import com.google.gson.JsonObject;

import run.mone.hive.common.JsonUtils;
import run.mone.hive.common.ToolDataInfo;
import run.mone.hive.roles.tool.ITool;

import java.util.Map;

/**
 * tool interceptor
 */
public class ToolInterceptor {

    public static void before(ITool tool, ToolDataInfo toolDataInfo, JsonObject parameters,
            Map<String, String> extraParam) {
        switch (tool.getName()) {
            case "speech_to_text":
                parameters.addProperty("base64", extraParam.get("voiceBase64"));
        }

        if (tool.toolInfoAsParam()) {
            parameters.add("_tool_info_as_param_", JsonUtils.gson.toJsonTree(toolDataInfo));
        }
    }

    public void after() {

    }
}
