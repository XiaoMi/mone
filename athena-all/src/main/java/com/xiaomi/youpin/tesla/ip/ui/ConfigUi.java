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

package com.xiaomi.youpin.tesla.ip.ui;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.tesla.ip.bo.ModelRes;
import com.xiaomi.youpin.tesla.ip.bo.TeslaPluginConfig;
import com.xiaomi.youpin.tesla.ip.bo.ZAddrRes;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import lombok.Getter;
import org.apache.commons.lang3.mutable.MutableBoolean;
import run.mone.ultraman.listener.bo.CompletionEnum;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * <p>
 * 保存配置
 */
public class ConfigUi extends JDialog {

    private JPanel contentPane;
    private JPasswordField zTokenPassword;

    @Getter
    private JComboBox chatModelComboBox;

    private JButton refreshButton;

    @Getter
    private JComboBox completionMode;
    @Getter
    private JComboBox noChatModelComboBox;

    private MutableBoolean modified;

    private static final List<String> COMP_MODE_LIST = CompletionEnum.getDisplayList();


    public ConfigUi(MutableBoolean modified) {
        this.modified = modified;
        setContentPane(contentPane);
        setModal(true);


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addListener();

        init();

        this.setSize(this.getWidth() + 1, this.getHeight() + 1);
    }

    private void addListener() {
        this.zTokenPassword.getDocument().addDocumentListener(listener);
        this.chatModelComboBox.addItemListener(itemEvent -> modified.setTrue());
        this.noChatModelComboBox.addItemListener(itemEvent -> modified.setTrue());
        this.completionMode.addItemListener(itemEvent -> modified.setTrue());
        this.refreshButton.addActionListener(actionEvent -> {
            ZAddrRes res = Prompt.zAddrRes();
            List<String> list = res.getModels().stream().map(ModelRes::getValue).toList();
            ArrayList<String> models = new ArrayList<>(list);
            if (!models.contains(Const.USE_BOT_MODEL)) {
                models.add(Const.USE_BOT_MODEL);
            }
            ComboBoxModel<String> chatModel = new DefaultComboBoxModel<>(models.toArray(String[]::new));
            ComboBoxModel<String> noChatModel = new DefaultComboBoxModel<>(models.toArray(String[]::new));
            ComboBoxModel<String> compMode = new DefaultComboBoxModel<>(COMP_MODE_LIST.toArray(String[]::new));
            this.chatModelComboBox.setModel(chatModel);
            this.noChatModelComboBox.setModel(noChatModel);
            if (!models.isEmpty()) {
                this.chatModelComboBox.setSelectedItem(models.get(0));
                this.noChatModelComboBox.setSelectedItem(models.get(0));
            }
            this.completionMode.setModel(compMode);
            this.completionMode.setSelectedItem(COMP_MODE_LIST.get(0));
            modified.setTrue();
        });
    }


    private DocumentListener listener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            modified.setTrue();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            modified.setTrue();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            modified.setTrue();
        }
    };


    private void init() {
        try {
            TeslaPluginConfig config = ConfigUtils.getConfig();
            this.zTokenPassword.setText(config.getzToken());

            List<String> list = config.getModelList();

            // 聊天模型设置
            String chatModel = config.getModel();
            if (null != list && null != chatModel) {
                if (!list.contains(Const.USE_BOT_MODEL)) {
                    list.add(Const.USE_BOT_MODEL);
                }
                this.chatModelComboBox.setModel(new DefaultComboBoxModel(list.toArray(String[]::new)));
                this.chatModelComboBox.setSelectedItem(chatModel);
            }

            // 非聊天模型设置
            String noChatModel = config.getNoChatModel();
            if (null != list && null != noChatModel) {
                this.noChatModelComboBox.setModel(new DefaultComboBoxModel(list.toArray(String[]::new)));
                this.noChatModelComboBox.setSelectedItem(noChatModel);
            }
            this.completionMode.setModel(new DefaultComboBoxModel<>(COMP_MODE_LIST.toArray(String[]::new)));
            this.completionMode.setSelectedItem(config.getCompletionMode().getDesc());

        } catch (Exception ignore) {

        }
    }


    private void onCancel() {
        dispose();
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }


    public MutableBoolean getModified() {
        return modified;
    }


    public JPasswordField getzTokenPassword() {
        return zTokenPassword;
    }

    public void setzTokenPassword(JPasswordField zTokenPassword) {
        this.zTokenPassword = zTokenPassword;
    }

}
