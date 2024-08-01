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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.SearchableConfigurable;
import com.xiaomi.youpin.tesla.ip.bo.TeslaPluginConfig;
import com.xiaomi.youpin.tesla.ip.ui.ConfigUi;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.listener.bo.CompletionEnum;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
        return "Athena";
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
        String oldServer = config.getDashServer();
        if (null != config.getDashServer() && (null == oldServer || !config.getDashServer().equals(oldServer))) {
            Safe.run(() -> {
                if (null != AthenaContext.ins().getAthenaTreeKeyAdapter()) {
                    AthenaContext.ins().getAthenaTreeKeyAdapter().loadMone(config.getDashServer(), "mone");
                }
            });
        }
        config.setzToken(configUi.getzTokenPassword().getText().trim());
        // 聊天模型的设置
        if (configUi.getChatModelComboBox().getModel().getSize() > 0 && null != configUi.getChatModelComboBox().getSelectedItem()) {
            config.setModel(configUi.getChatModelComboBox().getSelectedItem().toString());
            AthenaContext.ins().setGptModel(config.getModel());
        }
        // 非聊天模型的设置
        if (configUi.getNoChatModelComboBox().getModel().getSize() > 0 && null != configUi.getNoChatModelComboBox().getSelectedItem()) {
            config.setNoChatModel(configUi.getNoChatModelComboBox().getSelectedItem().toString());
            AthenaContext.ins().setNoChatModel(config.getNoChatModel());
        }
        ComboBoxModel model = configUi.getChatModelComboBox().getModel();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            list.add(model.getElementAt(i).toString());
        }
        config.setModelList(list);
        ComboBoxModel completionMode = configUi.getCompletionMode().getModel();
        config.setCompletionMode(CompletionEnum.getTypeEnumByDesc(completionMode.getSelectedItem().toString()));
        PropertiesComponent.getInstance().setValue("tesla_plugin_token", new Gson().toJson(config));
        configUi.getModified().setValue(false);

        new Thread(Prompt::flush).start();

        ApplicationManager.getApplication().getMessageBus()
                .syncPublisher(UltramanNotifier.ULTRAMAN_ACTION_TOPIC).onEvent(new UltramanEvent("save_config", ""));

    }
}
