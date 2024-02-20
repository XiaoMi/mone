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

package run.mone.m78.ip.common;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import run.mone.m78.ip.bo.TeslaPluginConfig;
import org.apache.commons.lang3.StringUtils;
import run.mone.ultraman.AthenaContext;

/**
 * @author goodjava@qq.com
 */
public abstract class ConfigUtils {

    public static TeslaPluginConfig getConfig() {
        String v = PropertiesComponent.getInstance().getValue("");
        if (StringUtils.isNotEmpty(v)) {
            TeslaPluginConfig config = new Gson().fromJson(v, TeslaPluginConfig.class);

            String model = config.getModel();
            if (StringUtils.isNotEmpty(model)) {
                AthenaContext.ins().setGptModel(model);
                AthenaContext.ins().setModelList(config.getModelList());
            }

            if (StringUtils.isEmpty(config.getDashServer())) {
                config.setDashServer(System.getenv("DASH"));
            }
            if (StringUtils.isEmpty(config.getNickName())) {
                config.setNickName(System.getenv("USER"));
            }
            if (StringUtils.isEmpty(config.getChatgptKey())) {
                config.setChatgptKey(System.getenv("API"));
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
