package com.xiaomi.youpin.tesla.ip.service;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.InspectionProfileImpl;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.codeInspection.ex.Tools;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @aythor goodjava@qq.com
 * @date 4/18/24 15:00
 */
@Slf4j
public class QuickFixInvokeUtil {

    @Deprecated
    public static void applyQuickFixes(@NotNull Project project, @NotNull PsiFile file) {
        InspectionManager inspectionManager = InspectionManager.getInstance(project);
        InspectionProfileImpl inspectionProfile = InspectionProjectProfileManager.getInstance(project).getCurrentProfile();

        // 获取所有启用的检查工具
        List<Tools> inspectionTools = inspectionProfile.getAllEnabledInspectionTools(project);

        for (Tools toolWrapper : inspectionTools) {
            if (toolWrapper instanceof LocalInspectionToolWrapper) {
                LocalInspectionTool localInspectionTool = ((LocalInspectionToolWrapper) toolWrapper).getTool();
                ProblemsHolder problemsHolder = new ProblemsHolder(inspectionManager, file, false);
                PsiElementVisitor visitor = localInspectionTool.buildVisitor(problemsHolder, true);
                file.accept(visitor);

                List<ProblemDescriptor> problems = problemsHolder.getResults();
                for (ProblemDescriptor problem : problems) {
                    // 获取问题描述符的 Quick Fixes
                    QuickFix[] fixes = problem.getFixes();
                    if (fixes != null) {
                        for (QuickFix fix : fixes) {
                            // 应用第一个可用的 Quick Fix
                            applyQuickFix(project, problem, fix);
                            break; // 只应用一个 Quick Fix，如果需要应用多个，移除这个 break
                        }
                    }
                }
            }
        }
    }

    public static List<ProblemDescriptor> problems(@NotNull Project project, @NotNull PsiFile file, PsiClass pc) {
        InspectionManager inspectionManager = InspectionManager.getInstance(project);
        InspectionProfileImpl inspectionProfile = InspectionProjectProfileManager.getInstance(project).getCurrentProfile();
        // 获取所有启用的检查工具
        List<Tools> inspectionTools = inspectionProfile.getAllEnabledInspectionTools(project);
        List<ProblemDescriptor> res = new ArrayList<>();


        Task.Backgroundable task = new Task.Backgroundable(project, "Running Task", true) {
            public void run(@NotNull ProgressIndicator indicator) {
                // 设置进度指示器的初始文本
                indicator.setText("Running task...");

                for (Tools toolWrapper : inspectionTools) {
                    InspectionToolWrapper<?, ?> t = toolWrapper.getTool();
                    InspectionProfileEntry e = t.getTool();
                    if (e instanceof LocalInspectionTool lt) {
                        res.addAll(lt.processFile(file,inspectionManager));
                    }
                }

                System.out.println(res);

            }
        };

        ProgressManager.getInstance().run(task);

        return res;

    }

    private static void applyQuickFix(@NotNull Project project, @NotNull ProblemDescriptor problem, @NotNull QuickFix quickFix) {
        // 在写操作中应用 Quick Fix
        WriteCommandAction.runWriteCommandAction(project, () -> quickFix.applyFix(project, problem));
    }


    public static  void a(Project project,PsiFile file) {
        DaemonCodeAnalyzer analyzer = DaemonCodeAnalyzer.getInstance(project);
        if (analyzer instanceof DaemonCodeAnalyzerImpl ex) {
            @Nullable Document document = PsiDocumentManager.getInstance(project).getDocument(file);
            ex.restart(file);
            TextEditor textEditor = TextEditorProvider.getInstance().getTextEditor(CodeService.getEditor(project));
            @NotNull List<HighlightInfo> list = ex.getFileLevelHighlights(project, file);
            System.out.println(list);
        }
    }

    @Deprecated
    public static void performQuickFix(@NotNull Project project, @NotNull PsiFile file) {
        DaemonCodeAnalyzer.getInstance(project).restart(file);
        Editor editor = openClassAndGetEditor(project, file);
        if (editor == null) {
            log.warn("Failed to open file:{} will not perform quick fix...", file);
            return;
        }

        ApplicationManager.getApplication().runReadAction(() -> {
            ShowIntentionsPass.IntentionsInfo intentions = new ShowIntentionsPass.IntentionsInfo();
            ShowIntentionsPass.getActionsToShow(editor, file, intentions, -1);

            // 遍历错误意图并尝试应用第一个
            // TODO mason: 目前这里拿到errorFixesToShow为空(不是null，但没有内容...)
            for (HighlightInfo.IntentionActionDescriptor actionDes : intentions.errorFixesToShow) {
                try {
                    IntentionAction action = actionDes.getAction();
                    if (action.isAvailable(project, editor, file)) {
                        action.invoke(project, editor, file);
                        break; // 应用了第一个可用的修复后退出循环
                    }
                } catch (IncorrectOperationException ex) {
                    log.error("Error while try to quick fix:{} of project:{}", file, project);
                }
            }
        });
    }

    public static Editor openClassAndGetEditor(@NotNull Project project, @NotNull PsiFile file) {
        return ReadAction.compute(() -> {
            if (!ApplicationManager.getApplication().isReadAccessAllowed()) {
                log.error("Read access is not allowed");
                return null;
            }
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            fileEditorManager.openFile(file.getVirtualFile(), true);
            Editor editor = fileEditorManager.getSelectedTextEditor();
            if (editor != null && editor.getDocument() == PsiDocumentManager.getInstance(project).getDocument(file)) {
                // 此时 editor 是打开的 PsiFile 对应的 Editor
                return editor;
            } else {
                // 如果没有找到对应的 Editor，可能是文件没有正确打开
                return null;
            }
        });
    }

    public static void quickFix2(Project project, PsiFile psiFile) {
        Editor editor = openClassAndGetEditor(project, psiFile);
        if (editor == null) {
            log.warn("Failed to open file:{} will not perform quick fix...", psiFile);
            return;
        }

        // 获取所有错误
        DaemonCodeAnalyzerImpl codeAnalyzer = (DaemonCodeAnalyzerImpl) DaemonCodeAnalyzer.getInstance(project);
        List<HighlightInfo> errors = DaemonCodeAnalyzerImpl.getHighlights(psiFile.getViewProvider().getDocument(), HighlightSeverity.ERROR, project);
        for (HighlightInfo error : errors) {
            if (error.getSeverity() == HighlightSeverity.ERROR) {
                // 获取并尝试执行快速修复
                List<IntentionAction> quickFixes = error.quickFixActionRanges.stream()
                        .map(range -> range.getFirst().getAction())
                        .toList();
                for (IntentionAction quickFix : quickFixes) {
                    if (quickFix.isAvailable(project, editor, psiFile)) {
                        quickFix.invoke(project, editor, psiFile);
                        break; // 只执行第一个可用的快速修复
                    }
                }
            }
        }
    }

    public static List<HighlightInfo> findAllErrors(Project project, PsiFile psiFile) {
        Editor editor = openClassAndGetEditor(project, psiFile);
        if (editor == null) {
            log.warn("Failed to open file:{} will not perform quick fix...", psiFile);
            return Collections.emptyList();
        }
        return DaemonCodeAnalyzerImpl.getHighlights(psiFile.getViewProvider().getDocument(), HighlightSeverity.ERROR, project);
    }
}
