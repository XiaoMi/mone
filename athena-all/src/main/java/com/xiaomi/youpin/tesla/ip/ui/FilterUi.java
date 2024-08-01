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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.xiaomi.youpin.tesla.ip.generator.ClassGenerator;
import com.xiaomi.youpin.tesla.ip.generator.DirectoryGenerator;
import com.xiaomi.youpin.tesla.ip.generator.FileGenerator;
import com.xiaomi.youpin.tesla.ip.generator.PomGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FilterUi extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField projectPathField;
    private JTextField projectNameField;
    private JTextField groupIdField;
    private JTextField packageField;
    private JTextField filterOrderField;
    private JTextField authorField;
    private JTextField versionField;
    private JTextField descField;
    private JTextField gitAddressField;
    private JTextField paramsField;
    private JTextField cnameField;
    private JTextField isSystemField;

    private Project project;

    public FilterUi(Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setSize(this.getWidth()+1,this.getHeight()+1);
    }

    private void onOK() {

        String projectPath = projectPathField.getText().trim();
        String projectName = projectNameField.getText().trim();
        String srcPath = "/src/main/java/";
        String groupId = groupIdField.getText().trim();
        String packageName = packageField.getText().trim();
        String author = authorField.getText().trim();
        if (!author.endsWith("@abc.com")) {
            author = author + "@abc.com";
        }
        String versionId = versionField.getText().trim();
        String filterOrder = filterOrderField.getText().trim();
        String desc = descField.getText().trim();
        String gitAddress = gitAddressField.getText().trim();
        String params = paramsField.getText().trim();
        String cname = cnameField.getText().trim();
        String isSystem = isSystemField.getText().trim();

        String packagePath = packageName.replaceAll("\\.", "/");

        //创建项目
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "src/main/java/" + packagePath + "/filter");
        directoryGenerator.generator();

        generageParentPom(projectPath, projectName, groupId, versionId);
        generageGitignore(projectPath, projectName);

        //生成入口类
        generateFilter(projectPath, projectName, packageName, author, packagePath + "/filter", "src/main/java", filterOrder);

        //生成配置文件
        generateExtensions(projectPath, projectName, versionId, desc, author, packageName, gitAddress, params, cname, isSystem);

        if (null != project) {
            Messages.showMessageDialog(project, "Success", "Generate Success", null);
        }

    }

    private void generateExtensions(String projectPath, String projectName, String version, String desc, String author, String packageName, String gitAddress, String params, String cname, String system) {
        if (!system.equals("0") && !system.equals("1")) {
            system = "0";
        }

        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, "src/main/resources/FilterDef", "filter_def.tml");
        Map<String, Object> m = new HashMap<>(9);
        m.put("name", projectName);
        m.put("filter", packageName + ".filter." + StringUtils.capitalize(projectName));
        m.put("date", new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
        m.put("version", version);
        m.put("desc", desc);
        m.put("author", author);
        m.put("gitAddress", gitAddress);
        m.put("params", params);
        m.put("cname", cname);
        m.put("system", system);
        fileGenerator.generator(m);
    }

    private void generateFilter(String projectPath, String projectName, String packageName, String author, String packagePath, String serviceSrcPath, String filterOrder) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName), "filter_class.tml");
        Map<String, Object> m = new HashMap<>(4);
        m.put("package", packageName + ".filter");
        m.put("author", author);
        m.put("filterOrder", filterOrder);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }

    private void generageParentPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "filter_pom.tml");
        Map<String, Object> mpom = new HashMap<>();
        mpom.put("groupId", groupId);
        mpom.put("artifactId", projectName);
        mpom.put("version", versionId + "-SNAPSHOT");
        pomGenerator.generator(mpom);
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "springboot_gitignore.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        FilterUi dialog = new FilterUi(null);
        dialog.pack();
        // 设置对话框大小是否可改变
        dialog.setResizable(true);
        dialog.setSize(800, 800);
        dialog.setVisible(true);
        System.exit(0);
    }
}
