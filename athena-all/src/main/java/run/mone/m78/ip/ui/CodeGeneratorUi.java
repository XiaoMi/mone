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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import run.mone.m78.ip.generator.ClassGenerator;
import run.mone.m78.ip.generator.DirectoryGenerator;
import run.mone.m78.ip.generator.FileGenerator;
import run.mone.m78.ip.generator.PomGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CodeGeneratorUi extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField projectPathField;
    private JTextField urlField;
    private JTextField projectNameField;
    private JTextField groupIdField;
    private JTextField packageField;
    private JTextField versionField;
    private JTextField authorField;
    private JLabel projectField;

    private Project project;

    public CodeGeneratorUi(Project project) {
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
        String versionId = versionField.getText().trim();
        String url = urlField.getText().trim();

        String packagePath = packageName.replaceAll("\\.", "/");

        //创建项目
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, "");
        directoryGenerator.generator();

        //创建module
        DirectoryGenerator apiModule = new DirectoryGenerator(projectPath, projectName, projectName + "-api" +
                File.separator + srcPath);
        apiModule.generator();

        generateApiPom(projectPath, projectName, groupId, versionId);


        DirectoryGenerator commonModule = new DirectoryGenerator(projectPath, projectName, projectName + "-common"
                + File.separator + srcPath
        );
        commonModule.generator();

        generateCommonPom(projectPath, projectName, groupId, versionId);


        String serviceSrcPath = projectName + "-service" + File.separator + srcPath;
        String serviceFullSrcPath = serviceSrcPath + File.separator + packagePath;

//        generateDubboService(author, projectPath, projectName, packageName);


        DirectoryGenerator serviceModule = new DirectoryGenerator(projectPath, projectName, serviceFullSrcPath);
        serviceModule.generator();

        generateServicePom(projectPath, projectName, groupId, author, versionId, url);

        generageParentPom(projectPath, projectName, groupId, versionId);
        generageGitignore(projectPath, projectName);

        //生成入口类
        generateHandler(projectPath, projectName, packageName, author, url, packagePath, serviceSrcPath);

        //生成plugin handler 配置文件
        generateExtensions(projectPath, projectName, packageName);

        if (null != project) {
            Messages.showMessageDialog(project, "Success", "Generate Success", null);
        }

    }

    private void generageGitignore(String projectPath, String projectName) {
        FileGenerator mdGenerator = new FileGenerator(projectPath, projectName, ".gitignore", "springboot_gitignore.tml");
        mdGenerator.generator(new HashMap<>());
    }

    private void generateExtensions(String projectPath, String projectName, String handlerPkg) {
        //生成文件夹
        DirectoryGenerator directoryGenerator = new DirectoryGenerator(projectPath, projectName, projectName + "-service/src/main/resources/META-INF");
        directoryGenerator.generator();

        //生成文件
        FileGenerator fileGenerator = new FileGenerator(projectPath, projectName, projectName + "-service/src/main/resources/META-INF/extensions.idx", "extensions.tml");
        Map<String, Object> m = new HashMap<>(2);
        m.put("package", handlerPkg);
        m.put("project", StringUtils.capitalize(projectName));
        fileGenerator.generator(m);
    }

    private void generateHandler(String projectPath, String projectName, String packageName, String author, String url, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName) + "Handler", "handler_class.tml");
        Map<String, Object> m = new HashMap<>(4);
        m.put("package", packageName);
        m.put("author", author);
        m.put("url", url);
        m.put("project", StringUtils.capitalize(projectName));
        classGenerator.generator(m);
    }


    private void generateDubboService(String author, String projectPath, String projectName, String packageName, String packagePath, String serviceSrcPath) {
        ClassGenerator classGenerator = new ClassGenerator(projectPath, projectName, serviceSrcPath, packagePath, StringUtils.capitalize(projectName) + "Service", "dubbo_service_class.tml");
        Map<String, Object> m = new HashMap<>(3);
        m.put("package", packageName);
        m.put("project", StringUtils.capitalize(projectName));
        m.put("author", author);
        classGenerator.generator(m);
    }


    private void generateApiPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成api module 下的pom文件
        PomGenerator apiPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-api", "pom_api.tml");
        Map<String, Object> ampom = new HashMap<>();
        ampom.put("groupId", groupId);
        ampom.put("parent_artifactId", projectName);
        ampom.put("artifactId", projectName + "-api");
        ampom.put("version", versionId + "-SNAPSHOT");
        ampom.put("plugin_id", projectName);
        ampom.put("version_id", versionId);
        apiPomGenerator.generator(ampom);
    }

    private void generateCommonPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成api module 下的pom文件
        PomGenerator commonPomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-common", "pom_common.tml");
        Map<String, Object> cmpom = new HashMap<>();
        cmpom.put("groupId", groupId);
        cmpom.put("parent_artifactId", projectName);
        cmpom.put("artifactId", projectName + "-common");
        cmpom.put("version", versionId + "-SNAPSHOT");
        commonPomGenerator.generator(cmpom);
    }

    private void generageParentPom(String projectPath, String projectName, String groupId, String versionId) {
        //生成主项目的pom文件
        PomGenerator pomGenerator = new PomGenerator(projectPath, projectName, "pom.tml");
        Map<String, Object> mpom = new HashMap<>();
        mpom.put("groupId", groupId);
        mpom.put("artifactId", projectName);
        mpom.put("version", versionId + "-SNAPSHOT");
        mpom.put("api_module", projectName + "-api");
        mpom.put("common_module", projectName + "-common");
        mpom.put("service_module", projectName + "-service");
        pomGenerator.generator(mpom);
    }

    private void generateServicePom(String projectPath, String projectName, String groupId, String author, String versionId,
                                    String url

    ) {
        //生成service module 下的pom文件
        PomGenerator servicePomGenerator = new PomGenerator(projectPath, projectName + File.separator + projectName + "-service", "pom_service.tml");
        Map<String, Object> smpom = new HashMap<>();
        smpom.put("groupId", groupId);
        smpom.put("parent_artifactId", projectName);
        smpom.put("artifactId", projectName + "-service");
        smpom.put("version", versionId + "-SNAPSHOT");
        smpom.put("plugin_id", projectName);
        smpom.put("version_id", versionId);
        smpom.put("author", author);
        smpom.put("plugin", projectName);
        smpom.put("plugin_url", url);
        servicePomGenerator.generator(smpom);
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        CodeGeneratorUi dialog = new CodeGeneratorUi(null);
        dialog.pack();
        // 设置对话框大小是否可改变
        dialog.setResizable(true);
        dialog.setSize(500, 400);
        dialog.setVisible(true);
        System.exit(0);
    }
}
