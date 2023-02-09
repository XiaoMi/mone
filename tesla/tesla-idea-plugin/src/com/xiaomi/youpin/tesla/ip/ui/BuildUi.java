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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.tesla.ip.bo.IdeaPluginInfoBo;
import com.xiaomi.youpin.tesla.ip.bo.PluginDeleteParam;
import com.xiaomi.youpin.tesla.ip.bo.Result;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.util.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.sender.FilePostSender;
import org.nutz.http.sender.PostSender;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

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


        status();

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
            status();
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

    private void status() {
        new Thread(() -> {
            String data = "";
            try {
                Request req = Request.create("http://" + getServerAddress() + "/open/plugin/info", Request.METHOD.POST);
                Map<String, String> mh = new HashMap<>();
                mh.put("Content-Type", "application/json");
                Header header = Header.create(mh);
                req.setHeader(header);
                Map<String, String> m = new HashMap<>(3);
                m.put("token", getToken());
                m.put("name", projectNameField.getText());
                m.put("userName", getUserName());
                req.setData(new Gson().toJson(m));
                PostSender ps = new PostSender(req);
                Response res = ps.setTimeout(timeOut).send();

                data = res.getContent();

                MutableInt status = new MutableInt(-1);
                final MutableInt pluginId = new MutableInt(-1);
                if (StringUtils.isNotEmpty(data)) {
                    Result<IdeaPluginInfoBo> rm = new Gson().fromJson(data, new TypeToken<Result<IdeaPluginInfoBo>>() {
                    }.getType());

                    if (null == rm.getData()) {
                        pluginId.setValue(-1);
                    } else {
                        pluginId.setValue(rm.getData().getId());
                    }
                    status.setValue(rm.getData().getStatus());
                }

                SwingUtilities.invokeLater(() -> {
                    this.textArea1.append(VERSION + "\r\n");
                    this.pluginIdtextField.setText(String.valueOf(pluginId.getValue()));
                    this.statusField.setText(status.getValue().equals(1) ? "started" : "stop");
                });


            } catch (Throwable ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    textArea1.setText("");
                    textArea1.append("status:" + ex.getMessage() + "\r\n");
                    this.pluginIdtextField.setText("-1");
                    this.statusField.setText("-1");
                });
            }
        }).start();
    }

    private int stop() {

        this.stopButton.setEnabled(false);

        new Thread(() -> {
            MutableInt status = new MutableInt(-1);
            try {

                Request req = Request.create("http://" + getServerAddress() + "/open/plugin/stop", Request.METHOD.POST);
                Map<String, String> mh = new HashMap<>();
                mh.put("Content-Type", "application/json");
                Header header = Header.create(mh);
                req.setHeader(header);

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

                req.setData(new Gson().toJson(pluginDeleteParam));
                PostSender ps = new PostSender(req);
                Response res = ps.setTimeout(3000).send();

                status.setValue(res.getStatus());

                textArea1.append("stop:" + res.getStatus() + "\r\n");

                SwingUtilities.invokeLater(() -> {
                    textArea1.setText("");
                    textArea1.append("stop:" + res.getStatus() + ":" + res.getContent() + "\r\n");
                    this.startButton.setEnabled(true);
                    this.statusField.setText("stop");
                });
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
                Request req = Request.create("http://" + getServerAddress() + "/open/plugin/start", Request.METHOD.POST);
                Map<String, String> mh = new HashMap<>();
                mh.put("Content-Type", "application/json");
                Header header = Header.create(mh);
                req.setHeader(header);

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


                req.setData(new Gson().toJson(pluginDeleteParam));
                PostSender ps = new PostSender(req);
                Response res = ps.setTimeout(3000).send();

                SwingUtilities.invokeLater(() -> {
                    textArea1.setText("");
                    textArea1.append("start:" + res.getStatus() + ":" + res.getContent() + "\r\n");
                    this.startButton.setEnabled(true);
                    this.statusField.setText("started");
                });
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

                    //new
                    Request req = Request.create("http://" + getServerAddress() + "/open/plugin/upload", Request.METHOD.POST);
                    File f = new File(this.projectPath + "/" + this.projectName + "-service/target/" + this.projectName + "_" + this.versionTextField.getText() + ".jar");
                    req.getParams().put("file", f);
                    req.getParams().put("token", getToken());
                    req.getParams().put("userName", getUserName());
                    req.getParams().put("url", pluginUrltextField.getText());
                    FilePostSender sender = new FilePostSender(req);
                    Response resp = sender.send();
                    System.out.println(resp.getStatus());
                    textArea1.append("push:" + resp.getStatus() + ":" + resp.getContent() + "\r\n");
                } else {
                    //update
                    Request req = Request.create("http://" + getServerAddress() + "/open/plugin/update/upload", Request.METHOD.POST);
                    File f = new File(this.projectPath + "/" + this.projectName + "-service/target/" + this.projectName + "_" + this.versionTextField.getText() + ".jar");
                    req.getParams().put("file", f);
                    req.getParams().put("token", getToken());
                    req.getParams().put("pluginId", pluginIdtextField.getText());
                    req.getParams().put("url", pluginUrltextField.getText());
                    req.getParams().put("userName", getUserName());
                    FilePostSender sender = new FilePostSender(req);
                    Response resp = sender.setTimeout(3000).send();
                    System.out.println(resp.getStatus());


                    SwingUtilities.invokeLater(() -> {
                        this.pushButton.setEnabled(true);
                        textArea1.setText("");
                        textArea1.setSelectionStart(0);
                        textArea1.append("push:" + resp.getStatus() + "\r\n");
                    });

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
