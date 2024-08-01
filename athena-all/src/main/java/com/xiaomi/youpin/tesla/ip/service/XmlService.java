package com.xiaomi.youpin.tesla.ip.service;

import com.google.common.collect.Maps;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.xiaomi.youpin.tesla.ip.dialog.SelectModuleDialog;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/5/21 22:07
 */
public class XmlService {

    /**
     * 方便操纵xml,这里的作用是修改maven 中的 pom 加入依赖
     *
     * @param project
     * @param moduleName
     * @param dependency
     */
    public static void openMavenPomAndModify(Project project, String moduleName, String dependency) {
        String pomFilePath = project.getBasePath() + File.separator + moduleName + File.separator + "pom.xml";
        VirtualFile pomFile = LocalFileSystem.getInstance().findFileByPath(pomFilePath);
        if (pomFile != null && !pomFile.isDirectory()) {
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, pomFile);
            if (descriptor.canNavigate()) {
                descriptor.navigate(true);
            }
            PsiManager psiManager = PsiManager.getInstance(project);
            XmlFile xmlFile = (XmlFile) psiManager.findFile(pomFile);
            XmlTag dependenciesTag = xmlFile.getRootTag().findFirstSubTag("dependencies");
            XmlElementFactory factory = XmlElementFactory.getInstance(project);
            XmlTag dependencyTag = factory.createTagFromText(dependency);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                dependenciesTag.addSubTag(dependencyTag, false);
            });
        }


    }

    public static Pair<Integer, String> checkPomVersion(Project project, String groupId, String artifactId, String version) {
        List<String> modules = ProjectUtils.listAllModules(project).stream().filter(m -> !project.getName().equals(m)).toList();
        Map<String, String> modulePathMap = Maps.newHashMap();
        modules.forEach(i -> modulePathMap.put(i, project.getBasePath() + File.separator + i + File.separator + "pom.xml"));
        // 根目录单独处理一下
        modulePathMap.put(Arrays.stream(project.getBasePath().split("/"))
                .reduce((first, second) -> second)
                .orElse(""), project.getBasePath() + File.separator + "pom.xml");
        List<File> files = modulePathMap.values().stream().map(File::new).toList();
        for (File pomFile : files) {
            try {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model = reader.read(new FileReader(pomFile));
                List<Dependency> dependencies = model.getDependencies();
                for (Dependency dependency : dependencies) {
                    if (dependency.getGroupId().equals(groupId) &&
                            dependency.getArtifactId().equals(artifactId)) {
                        if (dependency.getVersion().equals(version)) {
                            // true
                            return Pair.of(0, "success");
                        }
                        // 修改版本
                        dependency.setVersion(version);
                        Pair<Integer, String> writeRes = writeDependency(model, pomFile);
                        ProjectUtils.openFileByPath(project, pomFile.getPath());
                        return writeRes.getKey() == 0 ? Pair.of(0, "您好，您的项目已经添加过miapi的maven包，但它不是最新版本，已为您升级到最新版本\n"
                                + buildDependencyString(groupId, artifactId, version)) : writeRes;
                    }
                }
                // 没有引入
                return insertDependency(project, groupId, artifactId, version, modulePathMap);
            } catch (Exception e) {
                e.printStackTrace();
                return Pair.of(500, e.getMessage());
            }
        }
        return Pair.of(0, "success");
    }

    @NotNull
    private static Pair<Integer, String> insertDependency(Project project, String groupId, String artifactId, String version, Map<String, String> modulePathMap) throws IOException, XmlPullParserException {
        SelectModuleDialog dialog = new SelectModuleDialog(project, ProjectUtils.listAllModules(project));
        dialog.show();
        if (dialog.getExitCode() != 0) {
            return Pair.of(0, "success");
        }
        MavenXpp3Reader reader = new MavenXpp3Reader();
        String selectedModule = dialog.getSelectedModule();
        String pomFilePath = modulePathMap.get(selectedModule);
        Model insertModel = reader.read(new FileReader(pomFilePath));
        Dependency dependency = new Dependency();
        dependency.setVersion(version);
        dependency.setArtifactId(artifactId);
        dependency.setGroupId(groupId);
        insertModel.getDependencies().add(dependency);
        Pair<Integer, String> writeRes = writeDependency(insertModel, new File(pomFilePath));
        ProjectUtils.openFileByPath(project, pomFilePath);
        return writeRes.getKey() == 0 ? Pair.of(0, "您好，您的项目之前没有添加过miapi的maven包，我已经为您添加了，下面是我添加的包\n"
                + buildDependencyString(groupId, artifactId, version)) : writeRes;
    }

    private static Pair<Integer, String> writeDependency(Model model, File pomFile) {
        try (FileWriter writer = new FileWriter(pomFile)) {
            new MavenXpp3Writer().write(writer, model);
            return Pair.of(0, "success");
        } catch (IOException ex) {
            ex.printStackTrace();
            return Pair.of(500, ex.getMessage());
        }
    }

    private static String buildDependencyString(String groupId, String artifactId, String version) {
        return String.format("```\n" +
                "<dependency>\n" +
                "    \t<groupId>%s</groupId>\n" +
                "    \t<artifactId>%s</artifactId>\n" +
                "    \t<version>%s</version>\n" +
                "</dependency>", groupId, artifactId, version);

    }


}
