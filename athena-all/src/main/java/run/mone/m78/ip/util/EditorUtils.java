package run.mone.m78.ip.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightVirtualFile;
import run.mone.m78.ip.common.Const;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2023/4/18 00:13
 */
public class EditorUtils {

    public static void scroll(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_DOWN); // 翻到下一页
    }

    public static String getSelectContent(Editor editor) {
        return getSelectContent(editor, true);
    }

    public static String getSelectContent(Editor editor, boolean selectLine) {
        try {
            Document document = editor.getDocument();
            final SelectionModel selectionModel = editor.getSelectionModel();
            final int start = selectionModel.getSelectionStart();
            final int end = selectionModel.getSelectionEnd();
            TextRange range = new TextRange(start, end);
            String selectTxt = document.getText(range);

            //没有选中任何东西,则选中那一行
            if (StringUtils.isEmpty(selectTxt) && selectLine) {
                selectionModel.selectLineAtCaret();
                final int start1 = selectionModel.getSelectionStart();
                final int end1 = selectionModel.getSelectionEnd();
                TextRange range1 = new TextRange(start1, end1);
                selectTxt = document.getText(range1);
                if (null != selectTxt) {
                    selectTxt = selectTxt.trim();
                }
            }
            return selectTxt;
        } catch (Throwable ignore) {
            return "";
        }
    }

    public static Editor getEditorFromPsiClass(Project project, PsiClass psiClass) {
        PsiFile psiFile = psiClass.getContainingFile();
        if (psiFile != null) {
            String fileName = psiFile.getName();
            PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project));
            for (PsiFile file : files) {
                VirtualFile virtualFile = file.getVirtualFile();
                if (virtualFile != null) {
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    return fileEditorManager.openTextEditor(new OpenFileDescriptor(project, virtualFile), true);
                }
            }
        }
        return null;
    }

    /**
     * 移动到最后一个方法的最后部
     *
     * @param psiClass
     * @param editor
     */
    public static void moveToLastMethodEnd(PsiClass psiClass, Editor editor) {
        PsiMethod @NotNull [] methods = psiClass.getMethods();
        int offset = 0;
        if (methods.length == 0) {
            offset = psiClass.getTextRange().getEndOffset() - 1;
        } else {
            PsiMethod psiMethod = methods[methods.length - 1];
            offset = psiMethod.getTextRange().getEndOffset();
        }
        editor.getCaretModel().moveToOffset(offset);
    }

    public static void closeEditor(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (null != editor) {
            FileEditorManager manager = FileEditorManager.getInstance(project);
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            manager.closeFile(psiFile.getVirtualFile());
        }
    }

    public static void openEditor(Project project, String content, String name) {
        VirtualFile virtualFile = new LightVirtualFile(name, content);
        virtualFile.putUserData(Const.T_KEY, name);
        ApplicationManager.getApplication().runWriteAction((Computable) () -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                document.setText(content);
            }
            return FileEditorManager.getInstance(project).openFile(virtualFile, true);
        });
    }

    public static Editor getEditor(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        return editor;
    }


    /**
     * 获取或打开指定名称和内容的编辑器。
     * 如果已经存在打开的编辑器，则返回该编辑器，否则打开一个新的编辑器。
     * 如果打开的文件扩展名为"md"，则返回该编辑器。
     *
     * @param project 项目对象
     * @param name    文件名称
     * @param content 文件内容
     * @return 编辑器对象
     */
    public static Editor getOrOpenEditor(Project project, String name, String content) {
        Editor editor = getEditor(project);
        if (null == editor || !FileDocumentManager.getInstance().getFile(editor.getDocument()).equals("md")) {
            openEditor(project, content, name);
        } else {
            return editor;
        }
        return getEditor(project);
    }


}
