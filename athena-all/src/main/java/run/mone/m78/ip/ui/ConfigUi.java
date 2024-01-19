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

package run.mone.m78.ip.ui;

import run.mone.m78.ip.bo.ZAddrRes;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.bo.TeslaPluginConfig;
import run.mone.m78.ip.common.ConfigUtils;
import lombok.Getter;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * <p>
 * 保存配置
 */
public class ConfigUi extends JDialog {

    private JPanel contentPane;
    private JTextField dashServerTextField;
    private JTextField nickNameTextField;
    private JPasswordField zTokenPassword;

    @Getter
    private JTextField aiProxyTextField;

    @Getter
    private JComboBox modelComboBox;
    private JButton refreshButton;


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
        dashServerTextField.getDocument().addDocumentListener(listener);
        nickNameTextField.getDocument().addDocumentListener(listener);
        this.zTokenPassword.getDocument().addDocumentListener(listener);
        this.aiProxyTextField.getDocument().addDocumentListener(listener);
        this.modelComboBox.addItemListener(itemEvent -> modified.setTrue());
        this.refreshButton.addActionListener(actionEvent -> {
            ZAddrRes res = Prompt.zAddrRes();
            this.dashServerTextField.setText(res.getAthenaDashServer().trim());
            List<String> list = res.getModels().stream().map(it -> it.getValue()).collect(Collectors.toList());
            ComboBoxModel<String> model = new DefaultComboBoxModel<>(list.toArray(String[]::new));
            this.modelComboBox.setModel(model);
            if (list.size() > 0) {
                this.modelComboBox.setSelectedItem(list.get(0));
            }
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
            this.dashServerTextField.setText(config.getDashServer());
            this.nickNameTextField.setText(config.getNickName());
            this.zTokenPassword.setText(config.getzToken());
            this.aiProxyTextField.setText(config.getAiProxy());

            List<String> list = config.getModelList();
            String model = config.getModel();
            if (null != list && null != model) {
                this.modelComboBox.setModel(new DefaultComboBoxModel(list.toArray(String[]::new)));
                this.modelComboBox.setSelectedItem(model);
            }

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


    public JTextField getDashServerTextField() {
        return dashServerTextField;
    }

    public void setDashServerTextField(JTextField dashServerTextField) {
        this.dashServerTextField = dashServerTextField;
    }

    public JTextField getNickNameTextField() {
        return nickNameTextField;
    }

    public JPasswordField getzTokenPassword() {
        return zTokenPassword;
    }

    public void setzTokenPassword(JPasswordField zTokenPassword) {
        this.zTokenPassword = zTokenPassword;
    }

}
