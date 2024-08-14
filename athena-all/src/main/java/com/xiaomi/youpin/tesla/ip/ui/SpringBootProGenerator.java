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
import com.xiaomi.youpin.tesla.ip.common.FileUtils;
import com.xiaomi.youpin.tesla.ip.generator.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SpringBootProGenerator extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField projectPathField;
    private JTextField projectNameField;
    private JTextField groupIdField;
    private JTextField packageField;
    private JTextField versionField;
    private JTextField authorField;

    private Project project;

    public SpringBootProGenerator(Project project) {
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
        String testPath = "/src/test/java/";
        String deployPath = "/deploy/manifests";
        String groupId = groupIdField.getText().trim();
        String packageName = packageField.getText().trim();
        String author = authorField.getText().trim();
        String versionId = versionField.getText().trim();
        String catAppdatas = "/data/appdatas/cat";
        String catApplogs = "/data/applogs/cat";

        String packagePath = packageName.replaceAll("\\.", "/");

        //创建项目
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "");
        directoryGenerator.generator();
        generageParentPom(projectPath, projectName, groupId, versionId);
        generageReadMe(projectPath, projectName);
        generageGitignore(projectPath, projectName);

        //创建module: api
        DirectoryGenerator apiModule = new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                File.separator + srcPath + File.separator + packagePath + File.separator + "api" + File.separator + "service");
        apiModule.generator();
        generateApiPom(projectPath, projectName, groupId, versionId);
        generateDubboApi(projectPath, projectName, packageName, packagePath + File.separator + "api" + File.separator + "service", projectName + "-api" + File.separator + srcPath);
        DirectoryGenerator apiTest = new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                File.separator + testPath + File.separator + packagePath + File.separator + "test");
        apiTest.generator();

        //创建module: common
        DirectoryGenerator commonModule = new DirectoryGenerator(projectPath, projectName, projectName + "-common"
                + File.separator + srcPath
        );
        commonModule.generator();
        generateCommonPom(projectPath, projectName, groupId, versionId);
        DirectoryGenerator commonTest = new DirectoryGenerator(projectPath, projectName, projectName + "-common" +
                File.separator + testPath);
        commonTest.generator();

        //创建module: service
        DirectoryGenerator serviceModule = new DirectoryGenerator(projectPath, projectName, projectName + "-service" +
                File.separator + srcPath + File.separator + packagePath);
        serviceModule.generator();
        generateServicePom(projectPath, projectName, groupId, author, versionId);
        DirectoryGenerator serviceTest = new DirectoryGenerator(projectPath, projectName, projectName + "-service" +
                File.separator + testPath + File.separator + packagePath);
        serviceTest.generator();

        //创建module: server
        DirectoryGenerator serverModule = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                File.separator + srcPath + File.separator + packagePath + File.separator + "bootstrap");
        serverModule.generator();
        DirectoryGenerator serverModule1 = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                File.separator + srcPath + File.separator + packagePath + File.separator + "service");
        serverModule1.generator();
        DirectoryGenerator serverModule2 = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                File.separator + srcPath + File.separator + packagePath + File.separator + "config");
        serverModule2.generator();
        generateServerPom(projectPath, projectName, groupId, author, versionId);
        DirectoryGenerator serverTest = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                File.separator + testPath + File.separator + packagePath);
        serverTest.generator();
        //生成入口类
        generateBootstrap(projectPath, projectName, packageName, author, packagePath + File.separator + "bootstrap", projectName + "-server" + File.separator + srcPath);
        //生成配置文件
        generateResources(projectPath, projectName);
        generateLogback(projectPath, projectName);
        //生成dubbo相关
        generateDubboProperties(projectPath, projectName);
        generateDubboConfig(projectPath, projectName, packageName, packagePath + File.separator + "config", projectName + "-server" + File.separator + srcPath);
        generateDubboApiImp(projectPath, projectName, packageName,packagePath + File.separator + "service", projectName + "-server" + File.separator + srcPath);
        //生成nacos相关
        generateNacosConfig(projectPath, projectName, packageName, packagePath + File.separator + "config", projectName + "-server" + File.separator + srcPath);
        //生成编译部署相关
        generateBuild(projectPath, projectName, versionId);
        DirectoryGenerator deploy = new DirectoryGenerator(projectPath, projectName, projectName + "-server" +
                File.separator + deployPath);
        deploy.generator();
        generateDeploy(projectPath, projectName, versionId, deployPath);
        //生成cat监控相关
        generateCatProperties(projectPath, projectName);
        FileUtils.createDirectories(catAppdatas);
        FileUtils.createDirectories(catApplogs);
        String template = FileUtils.getTemplate("springboot_cat_client.tml");
        String str = FileUtils.renderTemplate(template, new HashMap<>());
        FileUtils.writeFile(catAppdatas + File.separator + "client.xml", str);

//        colorEgg(projectPath, projectName, testPath, packagePath);

        if (null != project) {
            Messages.showMessageDialog(project, "Success", "Generate Success", null);
        }

    }

    private void generateResources(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/application.properties", "springboot_application_properties.tml");
        FileGenerator devGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/dev.properties", "springboot_application_properties_dev.tml");
        FileGenerator stagingGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/staging.properties", "springboot_application_properties_st.tml");
        FileGenerator c3Generator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/c3.properties", "springboot_application_properties_c3.tml");
        FileGenerator c4Generator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/c4.properties", "springboot_application_properties_c4.tml");
        FileGenerator previewGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/config/preview.properties", "springboot_application_properties_preview.tml");

        Map<String, Object> m = new HashMap<>(1);
        m.put("appName", projectName);
        fileGenerator.generator(m);
        devGenerator.generator(m);
        stagingGenerator.generator(m);
        c3Generator.generator(m);
        c4Generator.generator(m);
        previewGenerator.generator(m);
    }

    private void generateLogback(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/logback.xml", "springboot_logback.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("project", projectName);
        fileGenerator.generator(m);
    }

    private void generateCatProperties(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources/META-INF");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/META-INF/app.properties", "springboot_cat_properties.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("appName", projectName);
        fileGenerator.generator(m);
    }

    private void generateDubboProperties(String projectPath, String projectName) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-server/src/main/resources");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-server/src/main/resources/dubbo.properties", "springboot_dubbo_properties.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("project", projectName);
        fileGenerator.generator(m);
    }

    private void generateBootstrap(String projectPath, String projectName, String packageName, String author, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName) + "Bootstrap", "springboot_bootstrap_class.tml");
        Map<String, Object> m = new HashMap<>(3);
        m.put("package", packageName);
        m.put("author", author);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }

    private void generateDubboApiImp(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboTestServiceImpl", "springboot_dubbo_api_imp_class.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        classGenerator.generator(m);
    }

    private void generateDubboApi(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboTestService", "springboot_dubbo_api_class.tml");
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("project", projectName);
        classGenerator.generator(m);
    }

    private void generateDubboConfig(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "DubboConfiguration", "springboot_dubbo_config.tml");
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", packageName);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }

    private void generateNacosConfig(String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, "NacosConfiguration", "springboot_nacos_config.tml");
        Map<String, Object> m = new HashMap<>(1);
        m.put("package", packageName);
        classGenerator.generator(m);
    }

    private void generateApiPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成api module 下的pom文件
        PomGenerator apiPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-api", "springboot_pom_api.tml");
        Map<String, Object> ampom = new HashMap<>();
        ampom.put("groupId", groupId);
        ampom.put("parent_artifactId", projectName);
        ampom.put("artifactId", projectName + "-api");
        ampom.put("version", versionId + "-SNAPSHOT");
        ampom.put("version_id", versionId);
        apiPomGenerator.generator(ampom);
    }

    private void generateCommonPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成api module 下的pom文件
        PomGenerator commonPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-common", "springboot_pom_common.tml");
        Map<String, Object> cmpom = new HashMap<>();
        cmpom.put("groupId", groupId);
        cmpom.put("parent_artifactId", projectName);
        cmpom.put("artifactId", projectName + "-common");
        cmpom.put("version", versionId + "-SNAPSHOT");
        commonPomGenerator.generator(cmpom);
    }

    private void generageParentPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "springboot_pom.tml");
        Map<String, Object> mpom = new HashMap<>();
        mpom.put("groupId", groupId);
        mpom.put("artifactId", projectName);
        mpom.put("version", versionId + "-SNAPSHOT");
        mpom.put("api_module", projectName + "-api");
        mpom.put("common_module", projectName + "-common");
        mpom.put("service_module", projectName + "-service");
        mpom.put("server_module", projectName + "-server");
        pomGenerator.generator(mpom);
    }

    private void generageReadMe(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, "README.md", "springboot_readme.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "springboot_gitignore.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generateServicePom(String projectPath, String projectName, String groupId, String author, String versionId) {
        //生成service module 下的pom文件
        PomGenerator servicePomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-service", "springboot_pom_service.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("groupId", groupId);
        smpom.put("parent_artifactId", projectName);
        smpom.put("artifactId", projectName + "-service");
        smpom.put("version", versionId + "-SNAPSHOT");
        smpom.put("version_id", versionId);
        smpom.put("author", author);
        smpom.put("api_artifactId", projectName + "-api");
        smpom.put("common_artifactId", projectName + "-common");
        servicePomGenerator.generator(smpom);
    }

    private void generateServerPom(String projectPath, String projectName, String groupId, String author, String versionId) {
        //生成service module 下的pom文件
        PomGenerator servicePomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-server", "springboot_pom_server.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("groupId", groupId);
        smpom.put("parent_artifactId", projectName);
        smpom.put("artifactId", projectName + "-server");
        smpom.put("version", versionId + "-SNAPSHOT");
        smpom.put("bootstrap", StringUtils.capitalize(projectName) + "Bootstrap");
        smpom.put("service_artifactId", projectName + "-service");
        servicePomGenerator.generator(smpom);
    }

    private void generateBuild(String projectPath, String projectName, String versionId) {
        FileGenerator buildGenerator = new FileGenerator(projectPath, projectName + File.separator + projectName + "-server", "build.sh", "springboot_build.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("project", projectName);
        smpom.put("version", versionId + "-SNAPSHOT");
        buildGenerator.generator(smpom);
    }

    private void generateDeploy(String projectPath, String projectName, String versionId, String deployPath) {
        FileGenerator deployGenerator = new FileGenerator(projectPath, projectName + File.separator + projectName + "-server" + File.separator + deployPath, "config.pp.template", "springboot_deploy.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("project", projectName);
        smpom.put("version", versionId + "-SNAPSHOT");
        deployGenerator.generator(smpom);
    }

    private void onCancel() {
        dispose();
    }


    public static void main(String[] args) {
        SpringBootProGenerator dialog = new SpringBootProGenerator(null);
        dialog.pack();
        // 设置对话框大小是否可改变
        dialog.setResizable(true);
        dialog.setSize(500, 400);
        dialog.setVisible(true);
        System.exit(0);

    }
}
