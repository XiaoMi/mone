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
import com.intellij.openapi.options.SearchableConfigurable;
import com.xiaomi.youpin.tesla.ip.bo.TeslaPluginConfig;
import com.xiaomi.youpin.tesla.ip.ui.ConfigUi;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author goodjava@qq.com
 * <p>
 * 管理
 */
public class TeslaConfigurable implements SearchableConfigurable {

    private ConfigUi configUi;

    private MutableBoolean modified = new MutableBoolean(false);


    @NotNull
    @Override
    public String getId() {
        return "tesla_plugin_config";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == configUi) {
            configUi = new ConfigUi(modified);
        }
        return configUi.getContentPane();
    }

    @Override
    public boolean isModified() {
        return configUi.getModified().getValue();
    }

    @Override
    public void apply() {
        TeslaPluginConfig config = new TeslaPluginConfig();
        config.setChatServer(configUi.getChatServertextField().getText());
        config.setDashServer(configUi.getDashServerTextField().getText());
        config.setJavaPath(configUi.getJavaTextField().getText());
        config.setToken(configUi.getTokenPasswordField().getText());
        config.setMvnPath(configUi.getMvnTextField().getText());
        config.setNickName(configUi.getNickNameTextField().getText());
        config.setOpsLocal(configUi.getOpsLocaltextField().getText());
        config.setOpsStaging(configUi.getOpsStagingtextField().getText());
        config.setGroupList(configUi.getGroupTextField().getText());

        PropertiesComponent.getInstance().setValue("tesla_plugin_token", new Gson().toJson(config));

        configUi.getModified().setValue(false);
    }
}
