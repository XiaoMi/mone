package com.xiaomi.youpin.tesla.ip.common;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2023/4/12 11:05
 */
public abstract class JavaClassUtils {

    /**
     * Create a class.
     */
    public static void createClass(AnActionEvent e) {
        createClass(e.getProject(), e.getData(PlatformDataKeys.EDITOR), "abc", "");
    }

    public static void createClass(Project project, Editor editor, String name, String code) {
        if (project == null) {
            return;
        }
        String classContent = code;
        @NotNull PsiFile javaClassFile = PsiFileFactory.getInstance(project).createFileFromText(name, JavaFileType.INSTANCE, classContent);
        javaClassFile.setName(name + ".java");
        //Add Java classes to the project.
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiDirectory containerDirectory = psiFile.getContainingDirectory();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            containerDirectory.add(javaClassFile);
        });
    }



	public static void createClass(Project project, Editor editor, String name, String code, boolean testClass) {
        if (project == null) {
            return;
        }
        String classContent = code;
        @NotNull PsiFile javaClassFile = PsiFileFactory.getInstance(project).createFileFromText(name, JavaFileType.INSTANCE, classContent);
        javaClassFile.setName(name + ".java");
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiDirectory containerDirectory = testClass ? CodeService.getSourceDirectory(project, "", true) : psiFile.getContainingDirectory();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            containerDirectory.add(javaClassFile);
        });
    }

    public static void createClass(Project project, Editor editor, String name, String code, boolean testClass, String packagePath) {
        if (project == null) {
            return;
        }
        String classContent = code;
        @NotNull PsiFile javaClassFile = PsiFileFactory.getInstance(project).createFileFromText(name, JavaFileType.INSTANCE, classContent);
        javaClassFile.setName(name + ".java");
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiDirectory containerDirectory = testClass ? CodeService.getSourceDirectory(project, true, packagePath) : psiFile.getContainingDirectory();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            containerDirectory.add(javaClassFile);
            // 生成后打开文件
            openClass(project, name);
        });
    }


    public static void openClass(AnActionEvent e) {
        // 获取当前项目实例
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        // 定位目标Java类
        String targetClassName = "MyNewJavaClass";
        PsiClass[] targetClasses = PsiShortNamesCache.getInstance(project).getClassesByName(targetClassName, GlobalSearchScope.allScope(project));
        if (targetClasses.length > 0) {
            PsiClass targetClass = targetClasses[0];
            PsiFile containingFile = targetClass.getContainingFile();
            // 打开目标Java文件
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(containingFile.getVirtualFile(), true);
        }
    }

    public static void openClass(Project project, String className) {
        // 获取当前项目实例
        if (project == null) {
            return;
        }
        // 定位目标Java类
        String targetClassName = className;
        PsiClass[] targetClasses = PsiShortNamesCache.getInstance(project).getClassesByName(targetClassName, GlobalSearchScope.allScope(project));
        if (targetClasses.length > 0) {
            PsiClass targetClass = targetClasses[0];
            PsiFile containingFile = targetClass.getContainingFile();
            // 打开目标Java文件
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(containingFile.getVirtualFile(), true);
        }
    }


    public static String getClassName(String className) {
        String[] classNameParts = className.split("\\.");
        String simpleClassName = classNameParts[classNameParts.length - 1];
        return simpleClassName;
    }


}
