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

package com.xiaomi.youpin.tesla.ip.common;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import com.xiaomi.youpin.tesla.ip.bo.TeslaPluginConfig;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.AthenaContext;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public abstract class ConfigUtils {

    public static TeslaPluginConfig getConfig() {
        String v = PropertiesComponent.getInstance().getValue("tesla_plugin_token");
        if (StringUtils.isNotEmpty(v)) {
            TeslaPluginConfig config = new Gson().fromJson(v, TeslaPluginConfig.class);

            // 聊天的model设置
            String model = config.getModel();
            if (StringUtils.isNotEmpty(model)) {
                AthenaContext.ins().setGptModel(model);
                AthenaContext.ins().setModelList(config.getModelList());
            }

            // 非聊天的model设置
            String noChatModel = config.getNoChatModel();
            if (StringUtils.isNotEmpty(noChatModel)) {
                AthenaContext.ins().setNoChatModel(noChatModel);
            }

            // setup from env
            if (StringUtils.isEmpty(config.getDashServer())) {
                config.setDashServer(System.getenv("DASH"));
            }
            if (StringUtils.isEmpty(config.getNickName())) {
                config.setNickName(System.getenv("USER"));
            }
            if (StringUtils.isEmpty(config.getChatgptKey())) {
                config.setChatgptKey(System.getenv("API"));
            }

            // setup from inner config
            Map<String, String> athenaConfig = ResourceUtils.getAthenaConfig();
            if (StringUtils.isEmpty(config.getDashServer())) {
                config.setDashServer(athenaConfig.get(Const.CONF_DASH_URL).trim());
            }
            if (StringUtils.isEmpty(config.getNickName())) {
                config.setNickName(athenaConfig.get(Const.CONF_NICK_NAME).trim());
            }
            if (StringUtils.isEmpty(config.getAiProxy())) {
                config.setAiProxy(athenaConfig.get(Const.CONF_AI_PROXY_URL).trim());
            }
            if (StringUtils.isEmpty(config.getChatServer())) {
                config.setChatServer(athenaConfig.get(Const.CONF_PORT).trim());
            }

            AthenaContext.ins().setToken(config.getToken());
            return config;
        }
        return new TeslaPluginConfig();

    }


    public static String user() {
        try {
            TeslaPluginConfig config = getConfig();
            return config.getNickName();
        } catch (Throwable ex) {
            return "";
        }
    }

}
