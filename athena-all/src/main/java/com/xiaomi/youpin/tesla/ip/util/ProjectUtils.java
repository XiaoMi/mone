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

package com.xiaomi.youpin.tesla.ip.util;

import com.google.common.collect.Lists;
import com.intellij.ide.DataManager;
import com.intellij.ide.RecentProjectsManager;
import com.intellij.ide.RecentProjectsManagerBase;
import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import com.xiaomi.youpin.codegen.M78DoceanMongoGen;
import com.xiaomi.youpin.infra.rpc.Result;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 17:51
 */
public class ProjectUtils {


    public static Project project() {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        return (Project) dataContext.getData(DataConstants.PROJECT);
    }


    public static Project projectFromManager() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        return project;
    }

    public static Project projectFromManager(String name) {
        return Arrays.stream(ProjectManager.getInstance().getOpenProjects()).filter(it -> it.getName().equals(name)).findFirst().get();
    }

    /**
     * 最近打开的项目
     *
     * @return
     */
    public static List<String> recentProjects() {
        RecentProjectsManager recentProjectsManager = RecentProjectsManager.getInstance();
        List<String> v = ((RecentProjectsManagerBase) recentProjectsManager).getRecentPaths();
        return v;
    }

    /**
     * 已经打开的project 列表
     *
     * @return
     */
    public static List<String> listOpenProjects() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        return Arrays.stream(openProjects).map(it -> it.getName()).collect(Collectors.toList());
    }

    /**
     * 列出所有module
     *
     * @param project
     * @return
     */
    public static List<String> listAllModules(Project project) {
        try {
            ModuleManager moduleManager = ModuleManager.getInstance(project);
            Module[] modules = moduleManager.getModules();
            return Arrays.stream(modules).map(it -> it.getName()).collect(Collectors.toList());
        } catch (Throwable ex) {
            return Lists.newArrayList();
        }
    }

    public static Module getModuleWithName(Project project, String name) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        return Arrays.stream(modules).filter(it -> it.getName().equals(name)).findFirst().get();
    }

    public static List<String> listEditors(Project project) {
        List<String> result = new ArrayList<>();
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        for (FileEditor editor : editorManager.getAllEditors()) {
            if (editor instanceof TextEditor) {
                TextEditor textEditor = ((TextEditor) editor);
                result.add(textEditor.getFile().getName());
            }
        }
        return result;
    }

    public static Module getCurrentModule(Project project) {
        try {
            @Nullable Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (null == editor) {
                //如果没有打开的文件,尝试打开第一个module
                ModuleManager moduleManager = ModuleManager.getInstance(project);
                Module[] modules = moduleManager.getModules();
                if (modules.length > 0) {

                    List<Module> list = Arrays.stream(modules).sorted((a, b) -> {
                        String ap = a.getModuleFilePath();
                        String bp = b.getModuleFilePath();
                        return bp.length() - ap.length();
                    }).collect(Collectors.toList());

                    return list.get(0);
                }
                return null;
            }
            PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(Objects.requireNonNull(editor), project);
            if (psiFile != null) {
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null) {
                    return ModuleUtilCore.findModuleForFile(virtualFile, project);
                }
            }
            return null;
        } catch (Throwable ex) {
            return null;
        }
    }

    public static String getCurrentModudleName(Project project) {
        Module module = getCurrentModule(project);
        if (null == module) {
            return "";
        }
        return module.getName();
    }


    public static void openFileByPath(Project project, String filePath) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile);
        descriptor.navigate(true);
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformat(psiFile);
        }
    }

    public static VirtualFile getSourceRoot(Project project, String moduleName) {
        Module currentModule = ProjectUtils.getModuleWithName(project, moduleName);
        if (Objects.isNull(currentModule)) {
            return null;
        }
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(currentModule).getSourceRoots(false);
        return sourceRoots[0];
    }


    public static JavaSdkVersion getJdkVersion(Project project) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSdk != null) {
            return JavaSdkVersion.fromVersionString(projectSdk.getVersionString());
        }
        return null;
    }


    public static List<String> getResourceImport(Project project) {
        JavaSdkVersion version = getJdkVersion(project);
        if (null != version && version.ordinal() <= 8) {
            return Lists.newArrayList("import javax.annotation.Resource");
        }
        return Lists.newArrayList("import jakarta.annotation.Resource");
    }

    public static String getModulePath(Project project, String moduleName) {
        Module module = getModuleWithName(project, moduleName);
        return new File(module.getModuleFilePath()).getParent();
    }

    public static void openProject(Project project, String projectPathStr) {
        ApplicationManager.getApplication().invokeLater(() -> {
            // 获取本地文件系统
            ProjectManagerEx projectManager = ProjectManagerEx.getInstanceEx();
            Path projectPath = Paths.get(projectPathStr);
            OpenProjectTask openProjectTask = new OpenProjectTask(false, project, true, false);
            projectManager.openProject(projectPath, openProjectTask);
        });
    }

    public Optional<String> generateProjectBase(Project project, String projectName, String packageName, String groupId, String author) {
        VirtualFile contentRoot = ProjectRootManager.getInstance(project).getContentRoots()[0];
        String path = contentRoot.getPath();
        File projectRoot = new File(path);
        String absolutePath = projectRoot.getParentFile().getAbsolutePath();
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            PluginId pluginId = PluginManagerCore.getPluginByClassName(this.getClass().getName());
            if (pluginId != null) {
                IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
                if (plugin != null) {
                    Thread.currentThread().setContextClassLoader(plugin.getPluginClassLoader());
                }
            }
            M78DoceanMongoGen m78DoceanMongoGen = new M78DoceanMongoGen();
            Result<String> autoapp = m78DoceanMongoGen.generateAndZip(absolutePath, projectName, groupId, packageName, author, "1.0.0", null);
            if (autoapp.getCode() == 0) {
                return Optional.of(absolutePath + "/" + projectName);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

        return Optional.empty();

    }
}



