package com.xiaomi.youpin.tesla.ip.service;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import com.intellij.util.IncorrectOperationException;
import com.xiaomi.youpin.tesla.ip.bo.ParamInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/6/3 22:31
 */
public class PsiMethodUtils {


    /**
     * 删除指定方法中的注释
     *
     * @param psiMethod
     */
    public static void deleteCommentsFromMethod(Project project, PsiMethod psiMethod, Predicate<PsiComment> predicate) {
        @NotNull Collection<PsiComment> comments = PsiTreeUtil.collectElementsOfType(psiMethod, PsiComment.class);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiComment comment : comments) {
                if (predicate.test(comment)) {
                    comment.delete();
                }
            }
        });
    }

    public static List<ParamInfo> getParamInfoList(PsiMethod psiMethod) {
        return Arrays.stream(psiMethod.getParameterList().getParameters()).map(it -> {
            String name = it.getName();
            String type = it.getType().getCanonicalText();
            return ParamInfo.builder().name(name).type(type).psiType(it.getType()).build();
        }).collect(Collectors.toList());
    }


    /**
     * 修改方法名
     *
     * @param project  项目对象
     * @param document 文档对象
     * @param method   方法对象
     * @param newName  新方法名
     */
    public static void modifyMethodName(Project project, Document document, PsiMethod method, String newName) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiIdentifier identifier = method.getNameIdentifier();
            int startOffset = identifier.getTextRange().getStartOffset();
            int endOffset = identifier.getTextRange().getEndOffset();
            document.replaceString(startOffset, endOffset, newName);
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }


    /**
     * Renames the specified method within the given project to the new name using a rename refactoring process wrapped in a write command action.
     */
    public static void renameMethod(Project project, PsiMethod method, String newName) {
        ApplicationManager.getApplication().invokeLater(() -> {
            RenameRefactoring renameRefactoring = RefactoringFactory.getInstance(project)
                    .createRename(method, newName);
            renameRefactoring.setSearchInComments(false);
            renameRefactoring.setSearchInNonJavaFiles(false);
            renameRefactoring.run();
        });
    }

    //replace PsiMethod整体的信息,参数是一个String method code(class)
    public static void replacePsiMethod(Project project, PsiMethod psiMethod, String methodCode) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            PsiMethod newMethod = elementFactory.createMethodFromText(methodCode.trim(), psiMethod.getContainingFile());
            psiMethod.replace(newMethod);
        });
    }


    /**
     * 在指定项目中重命名给定的参数。
     *
     * @param project   当前工作的项目对象。
     * @param parameter 要重命名的参数。
     * @param newName   参数的新名称。
     */
    public static void renameParameter(Project project, PsiParameter parameter, String newName) {
        ApplicationManager.getApplication().invokeLater(() -> {
            // 确保我们在写操作允许的上下文中执行重命名
            CommandProcessor.getInstance().executeCommand(project, () -> {
                try {
                    // 使用重构 API 创建一个重命名重构动作
                    RenameRefactoring renameRefactoring = RefactoringFactory.getInstance(project)
                            .createRename(parameter, newName);
                    renameRefactoring.setSearchInComments(false);
                    renameRefactoring.setSearchInNonJavaFiles(false);
                    renameRefactoring.run();
                } catch (IncorrectOperationException e) {
                    // 处理可能的异常
                }
            }, "Rename Parameter", null);
        });
    }

    // 通过getFirstChild迭代，获取PsiMethod的第一叶子节点
    public static PsiElement getFirstLeafNode(PsiMethod psiMethod) {
        PsiElement element = psiMethod.getFirstChild();
        while (element != null && element.getFirstChild() != null) {
            element = element.getFirstChild();
        }
        return element;
    }


    public static JsonObject extractMethodCallParts(@NotNull Project project, @NotNull Editor editor) {
        JsonObject res = new JsonObject();
        // 获取当前 PsiFile
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = psiDocumentManager.getPsiFile(editor.getDocument());
        if (psiFile == null) return res;

        // 获取当前光标位置的 PsiElement
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAtCursor = psiFile.findElementAt(offset);
        if (elementAtCursor == null) return res;

        // 向上遍历 PSI 树，直到找到方法调用表达式
        PsiElement parent = PsiTreeUtil.getParentOfType(elementAtCursor, PsiMethodCallExpression.class);
        if (parent instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) parent;

            // 获取方法名
            PsiReferenceExpression methodExpression = methodCallExpression.getMethodExpression();
            String methodName = methodExpression.getReferenceName();
            System.out.println("Method name: " + methodName);

            String methodCode = methodExpression.resolve().getText();
            res.addProperty("methodCode", methodCode);
            res.addProperty("methodName", methodName);


            // 获取调用对象
            PsiExpression qualifier = methodExpression.getQualifierExpression();
            if (qualifier instanceof PsiReferenceExpression) {
                PsiReferenceExpression referenceExpression = (PsiReferenceExpression) qualifier;
                PsiElement resolved = referenceExpression.resolve();
                if (resolved instanceof PsiVariable) {
                    System.out.println("Referenced variable: " + ((PsiVariable) resolved).getName());
                    PsiVariable pv = (PsiVariable) resolved;
                    res.addProperty("className", pv.getType().getCanonicalText());
                }
            }
        }
        return res;
    }


}
