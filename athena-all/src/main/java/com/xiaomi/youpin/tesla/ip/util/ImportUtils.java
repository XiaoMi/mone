package com.xiaomi.youpin.tesla.ip.util;

import com.google.common.base.Splitter;
import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.codeInspection.unusedImport.UnusedImportInspection;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/6/22 21:41
 */
@Slf4j
public abstract class ImportUtils {

    public static void removeInvalidImports(Project project, Editor editor) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            PsiImportList importList = PsiTreeUtil.findChildOfType(psiFile, PsiImportList.class);
            UnusedImportInspection unusedImportInspection = new UnusedImportInspection();
            if (importList != null) {
                boolean modify = false;
                if (importList != null) {
                    for (PsiImportStatementBase importStatement : importList.getAllImportStatements()) {
                        PsiJavaCodeReferenceElement importReference = importStatement.getImportReference();
                        if (importReference != null && importReference.resolve() != null && importReference.multiResolve(true).length == 0) {
                            modify = true;
                            importStatement.delete();
                        }
                    }
                }
                if (modify) {
                    PsiDocumentManager.getInstance(psiFile.getProject()).commitDocument(editor.getDocument());
                }
            }
        });
    }

    public static void optimizeImports(Project project, Editor editor) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            AnActionEvent event = AnActionEvent.createFromDataContext(
                    ActionPlaces.UNKNOWN,
                    null,
                    dataId -> {
                        if (CommonDataKeys.PSI_FILE.is(dataId)) {
                            return psiFile;
                        } else if (CommonDataKeys.PROJECT.is(dataId)) {
                            return project;
                        } else if (CommonDataKeys.EDITOR.is(dataId)) {
                            return editor;
                        } else {
                            return null;
                        }
                    }
            );
            new OptimizeImportsAction().actionPerformed(event);
        });
    }


    public static void addImport(Project project, Editor editor, List<String> importStrList) {
        try {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
                Document document = editor.getDocument();
                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiImportList importList = PsiTreeUtil.findChildOfType(psiFile, PsiImportList.class);
                for (String importStr : importStrList) {
                    boolean isAlreadyImported = false;
                    boolean isStatic = false;
                    String memberName = "";
                    if (importStr.contains("static ")) {
                        isStatic = true;
                        importStr = importStr.trim().split(" +")[1];
                        String[] array = importStr.split("\\.");
                        importStr = Arrays.stream(array).limit(array.length - 1).collect(Collectors.joining("."));
                        memberName = array[array.length - 1];
                    }
                    for (PsiImportStatement importStatement : importList.getImportStatements()) {
                        String str = importStr;
                        List<String> list = Splitter.on(" ").splitToList(importStr.replace(";", ""));
                        if (list.size() > 0) {
                            str = list.get(list.size() - 1);
                        }
                        if (importStatement.getQualifiedName().equals(str)) {
                            isAlreadyImported = true;
                            break;
                        }
                    }
                    if (!isAlreadyImported) {
                        PsiClass psiClass = createPsiClass(factory, importStr);
                        if (null == psiClass) {
                            continue;
                        }
                        PsiImportStatementBase importStatement = null;

                        if (isStatic) {
                            importStatement = factory.createImportStaticStatement(psiClass, memberName);
                        } else {
                            importStatement = factory.createImportStatement(psiClass);
                        }

                        if (importList != null) {
                            importList.add(importStatement);
                            PsiDocumentManager.getInstance(psiFile.getProject()).commitDocument(document);
                        }
                    }
                }
            });
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static PsiClass createPsiClass(PsiElementFactory factory, String className) {
        try {
            String[] array = className.split("\\.");
            PsiClass psiClass = factory.createClass(array[array.length - 1]);
            PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
            javaFile.setPackageName(Arrays.stream(array).limit(array.length - 1).collect(Collectors.joining(".")));
            return psiClass;
        } catch (Throwable ex) {
            return null;
        }
    }


    public static String junitVersion(PsiClass psiClass) {
        PsiElement c = psiClass.getParent();
        if (c instanceof PsiJavaFileImpl pfi) {
            String text = pfi.getImportList().getText();
            if (StringUtils.isEmpty(text)) {
                return "unknow";
            }
            if (text.contains("org.junit.Test")) {
                return "junit";
            }
            if (text.contains("org.junit.jupiter.api.Test")) {
                return "jupiter";
            }
        }
        return "unknow";
    }


}
