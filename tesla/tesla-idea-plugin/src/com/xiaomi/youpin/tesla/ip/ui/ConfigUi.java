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

import com.xiaomi.youpin.tesla.ip.bo.TeslaPluginConfig;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author goodjava@qq.com
 * <p>
 * 保存配置
 */
public class ConfigUi extends JDialog {

    private JPanel contentPane;
    private JTextField javaTextField;
    private JTextField mvnTextField;
    private JTextField chatServertextField;
    private JPasswordField tokenPasswordField;
    private JLabel tokenLabel;
    private JTextField dashServerTextField;
    private JTextField nickNameTextField;
    private JTextField opsLocaltextField;
    private JTextField opsStagingtextField;
    private JTextField groupTextField;


    private MutableBoolean modified;

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
        javaTextField.getDocument().addDocumentListener(listener);
        mvnTextField.getDocument().addDocumentListener(listener);
        chatServertextField.getDocument().addDocumentListener(listener);
        dashServerTextField.getDocument().addDocumentListener(listener);
        tokenPasswordField.getDocument().addDocumentListener(listener);
        nickNameTextField.getDocument().addDocumentListener(listener);
        opsLocaltextField.getDocument().addDocumentListener(listener);
        opsStagingtextField.getDocument().addDocumentListener(listener);
        groupTextField.getDocument().addDocumentListener(listener);
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
            this.mvnTextField.setText(config.getMvnPath());
            this.javaTextField.setText(config.getJavaPath());
            this.chatServertextField.setText(config.getChatServer());
            this.tokenPasswordField.setText(config.getToken());
            this.dashServerTextField.setText(config.getDashServer());
            this.nickNameTextField.setText(config.getNickName());
            this.opsLocaltextField.setText(config.getOpsLocal());
            this.opsStagingtextField.setText(config.getOpsStaging());
            this.groupTextField.setText(config.getGroupList());
        } catch (Exception ex) {

        }
    }


    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        ConfigUi dialog = new ConfigUi(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }


    public MutableBoolean getModified() {
        return modified;
    }


    public JTextField getJavaTextField() {
        return javaTextField;
    }

    public void setJavaTextField(JTextField javaTextField) {
        this.javaTextField = javaTextField;
    }

    public JTextField getMvnTextField() {
        return mvnTextField;
    }

    public void setMvnTextField(JTextField mvnTextField) {
        this.mvnTextField = mvnTextField;
    }

    public JTextField getChatServertextField() {
        return chatServertextField;
    }

    public void setChatServertextField(JTextField chatServertextField) {
        this.chatServertextField = chatServertextField;
    }

    public JPasswordField getTokenPasswordField() {
        return tokenPasswordField;
    }

    public void setTokenPasswordField(JPasswordField tokenPasswordField) {
        this.tokenPasswordField = tokenPasswordField;
    }

    public JLabel getTokenLabel() {
        return tokenLabel;
    }

    public void setTokenLabel(JLabel tokenLabel) {
        this.tokenLabel = tokenLabel;
    }

    public JTextField getDashServerTextField() {
        return dashServerTextField;
    }

    public void setDashServerTextField(JTextField dashServerTextField) {
        this.dashServerTextField = dashServerTextField;
    }

    public JTextField getNickNameTextField() {
        return nickNameTextField;
    }

    public JTextField getOpsLocaltextField() {
        return opsLocaltextField;
    }

    public JTextField getOpsStagingtextField() {
        return opsStagingtextField;
    }

    public JTextField getGroupTextField() {
        return groupTextField;
    }
}
