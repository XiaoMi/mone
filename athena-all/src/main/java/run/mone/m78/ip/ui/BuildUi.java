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

import run.mone.m78.ip.bo.IdeaPluginInfoBo;
import run.mone.m78.ip.bo.PluginDeleteParam;
import run.mone.m78.ip.common.ConfigUtils;
import run.mone.m78.ip.util.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class BuildUi extends JDialog {


    private static final String VERSION = "0.0.1:2019-07-17";


    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField projectNameField;
    private JTextField pathField;
    private JTextArea textArea1;
    private JButton pushButton;
    private JButton startButton;
    private JButton stopButton;
    private JButton statusButton;
    private JTextField statusField;
    private JTextField versionTextField;
    private JTextField pluginUrltextField;
    private JTextField gatewayServerTextField;
    private JTextField pluginIdtextField;
    private JTextField mvnPathTextField;
    private JComboBox opsServercomboBox;
    private JComboBox groupComboBox;


    private final String projectName;
    private final String projectPath;


    private IdeaPluginInfoBo pluginInfo;


    public BuildUi(String projectName, String projectPath) {
        this.projectName = projectName;
        this.projectPath = projectPath;


        Map<String, String> m = XmlUtils.getKV(projectPath + File.separator + projectName + "-service" + File.separator + "pom.xml", "manifestEntries");

        this.versionTextField.setText(m.get("Plugin-Version"));
        this.pluginUrltextField.setText(m.get("Plugin-Url"));
        this.mvnPathTextField.setText(m.get("Plugin-MvnPath"));
        //gateway服务器
        this.gatewayServerTextField.setText(m.get("Plugin-GateWayServer"));

        if (StringUtils.isNotEmpty(this.projectName) && StringUtils.isNotEmpty(this.projectPath)) {
            projectNameField.setText(this.projectName);
            pathField.setText(this.projectPath);
        }

        String localOps = ConfigUtils.getConfig().getOpsLocal();
        String stagingOps = ConfigUtils.getConfig().getOpsStaging();

        this.opsServercomboBox.addItem(new ComboBoxItme("staging", stagingOps));
        this.opsServercomboBox.addItem(new ComboBoxItme("local", localOps));


        String groups = ConfigUtils.getConfig().getGroupList();
        if (StringUtils.isNotEmpty(groups)) {
            String[] ss = groups.split(",");
            Arrays.stream(ss).forEach(it -> {
                groupComboBox.addItem(it);
            });
        } else {
            groupComboBox.addItem("");
        }


        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> build());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> {
            //退出
            onCancel();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pushButton.addActionListener(e -> {
            //推送上去
            push();
        });
        //获取状态
        statusButton.addActionListener(e -> {
            //获取状态
        });
        startButton.addActionListener(e -> {
            //启动
            start();
        });

        stopButton.addActionListener(e -> {
            //停止
            stop();
        });


        this.setSize(this.getWidth() + 1, this.getHeight() + 1);
    }

    class ComboBoxItme {
        private String type;
        private String name;

        public ComboBoxItme(String type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return type + ":" + name;
        }
    }


    private String getToken() {
        String token = ConfigUtils.getConfig().getToken();
        return token;
    }


    private static final int timeOut = 3000;


    private int stop() {

        this.stopButton.setEnabled(false);

        new Thread(() -> {
            MutableInt status = new MutableInt(-1);
            try {


                PluginDeleteParam pluginDeleteParam = new PluginDeleteParam();
                pluginDeleteParam.setName(projectNameField.getText());
                pluginDeleteParam.setToken(getToken());
                pluginDeleteParam.setUserName(getUserName());


                if (!this.groupComboBox.getSelectedItem().toString().equals("")) {
                    List<String> groupList = new ArrayList<>();
                    groupList.add(this.groupComboBox.getSelectedItem().toString());
                    pluginDeleteParam.setGroupList(groupList);
                }

                pluginDeleteParam.setId(Integer.valueOf(pluginIdtextField.getText()));


            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    textArea1.setText("");
                    textArea1.append("stop:" + ex.getMessage() + "\r\n");
                    this.startButton.setEnabled(true);
                });

            }

            SwingUtilities.invokeLater(() -> {
                textArea1.setText("");
                textArea1.append("stop:" + status.getValue() + "\r\n");
                this.stopButton.setEnabled(true);
            });
        }).start();


        return 1;
    }

    /**
     * 启动插件
     *
     * @return
     */
    private int start() {

        this.startButton.setEnabled(false);

        new Thread(() -> {
            try {

                PluginDeleteParam pluginDeleteParam = new PluginDeleteParam();
                pluginDeleteParam.setToken(getToken());
                pluginDeleteParam.setUserName(getUserName());
                //设置插件名称
                pluginDeleteParam.setName(projectNameField.getText());

                List<String> addressList = new ArrayList<>();
                pluginDeleteParam.setAddressList(addressList);

                pluginDeleteParam.setId(Integer.valueOf(pluginIdtextField.getText()));


                if (!this.groupComboBox.getSelectedItem().toString().equals("")) {
                    List<String> groupList = new ArrayList<>();
                    groupList.add(this.groupComboBox.getSelectedItem().toString());
                    pluginDeleteParam.setGroupList(groupList);
                }


            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    textArea1.setText("");
                    textArea1.append("start:" + ex.getMessage() + "\r\n");
                    this.startButton.setEnabled(true);
                });
            }

        }).start();


        return 1;
    }


    private String getServerAddress() {
        ComboBoxItme item = (ComboBoxItme) this.opsServercomboBox.getSelectedItem();
        return item.name;
    }

    /**
     * 上传jar包
     */
    private void push() {
        this.pushButton.setEnabled(false);
        new Thread(() -> {
            try {
                if (pluginIdtextField.getText().equals("-1")) {
                } else {
                }


            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    textArea1.setText(ex.getMessage());
                    this.pushButton.setEnabled(true);
                });
            }

        }).start();
    }

    private String getUserName() {
        String name = ConfigUtils.getConfig().getNickName();
        return name;
    }


    //build
    private void build() {

        this.buttonOK.setEnabled(false);

        new Thread(() -> {
            String cmd = mvnPathTextField.getText().trim() + " clean compile package -Dmaven.test.skip=true -f " + projectPath + File.separator + "pom.xml";
            Runtime run = Runtime.getRuntime();
            try {
                Process p = run.exec(cmd);
                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
                String lineStr;
                while ((lineStr = inBr.readLine()) != null) {
                    System.out.println(lineStr);
                    String l = lineStr;
                    SwingUtilities.invokeLater(() -> {
                        textArea1.append(l);
                        textArea1.append("\r\n");
                    });


                }
                //检查命令是否执行失败。
                if (p.waitFor() != 0) {
                    if (p.exitValue() == 1) {
                        SwingUtilities.invokeLater(() -> textArea1.append("build error"));
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    this.buttonOK.setEnabled(true);
                });

                inBr.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    this.buttonOK.setEnabled(true);
                    textArea1.append("build:" + e.getMessage());
                });
            }


        }).start();


    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        BuildUi dialog = new BuildUi("databank", "/private/tmp/databank");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
