package com.xiaomi.youpin.tesla.ip.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.xiaomi.youpin.tesla.ip.util.PsiClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author goodjava@qq.com
 * @date 2023/5/17 15:46
 */
@Slf4j
public class TextService {


    public static void writeContent(Project project, String fileName, String moduleName, Runnable runnable) {
        writeContent(project, fileName, moduleName, runnable, null);
    }


    public static String readContent(Project project, String module, String fileName) {
        PsiDirectory directory = getPsiDirectory(project, module);
        if (null == directory) {
            return "";
        }
        PsiFile file = directory.findFile(fileName);
        if (null == file) {
            return "";
        }
        return file.getText();
    }


    public static void writeContent(Project project, String fileName, String moduleName, Runnable runnable, String content) {
        String fileContent = getFileContent(content);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            PsiFile psiFile = psiFileFactory.createFileFromText(
                    fileName, PlainTextFileType.INSTANCE, fileContent);

            PsiDirectory directory = getPsiDirectory(project, moduleName);
            if (null == directory) {
                log.info("directory is null moduleName:{}", moduleName);
                return;
            }

            @Nullable PsiFile file = directory.findFile(fileName);
            if (null != file) {
                file.delete();
            }

            PsiElement addedFile = directory.add(psiFile);
            if (addedFile instanceof PsiFile) {
                VirtualFile virtualFile = ((PsiFile) addedFile).getVirtualFile();
                if (virtualFile != null) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                    if (StringUtils.isEmpty(content)) {
                        runnable.run();
                    }
                }
            }
        });
    }

    @NotNull
    private static String getFileContent(String content) {
        String fileContent = " ";
        if (StringUtils.isNotEmpty(content)) {
            fileContent = content;
        }
        return fileContent;
    }


    private static PsiDirectory getPsiDirectory(Project project, String moduleName) {
        PsiDirectory directory = PsiClassUtils.getSourceDirectory(project, moduleName);
        if (null == directory) {
            return null;
        }
        PsiDirectory resourcesDir = directory.findSubdirectory("resources");
        if (null == resourcesDir) {
            directory = directory.createSubdirectory("resources");
        } else {
            directory = resourcesDir;
        }
        PsiDirectory subdirectory = directory.findSubdirectory("athena");
        if (subdirectory != null) {
            return subdirectory;
        } else {
            return directory.createSubdirectory("athena");
        }
    }


}
