package com.xiaomi.youpin.tesla.ip.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.xiaomi.youpin.tesla.ip.common.Const;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        return getSelectedContentOrLine(editor, true);
    }


    //获取当前行内容(class)
    public static String getCurrentLineContent(Editor editor) {
        if (editor == null) {
            return "";
        }
        Document document = editor.getDocument();
        int caretOffset = editor.getCaretModel().getOffset();
        int lineNumber = document.getLineNumber(caretOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        return document.getText(new TextRange(lineStartOffset, lineEndOffset)).trim();
    }

    /**
     * 获取编辑器选中内容或者当前行
     */
    public static String getSelectedContentOrLine(Editor editor, boolean selectLine) {
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
                    return selectTxt.trim();
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


    public static int moveCursorToMethodEndIfOutside(Editor editor, PsiMethod method, int cursorOffset) {
        // 获取当前编辑器
        if (editor == null) {
            return cursorOffset;
        }

        // 获取方法体的起始和结束偏移量
        PsiElement methodBody = method.getBody();
        if (methodBody == null) {
            return cursorOffset;
        }
        int methodStartOffset = methodBody.getTextRange().getStartOffset();
        int methodEndOffset = methodBody.getTextRange().getEndOffset() - 1;

        // 检查光标是否在方法体内
        if (cursorOffset < methodStartOffset || cursorOffset > methodEndOffset) {
            // 光标不在方法体内，移动光标到方法体最后一个字符后面
            ApplicationManager.getApplication().invokeAndWait(() -> {
                editor.getCaretModel().moveToOffset(methodEndOffset);
                editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            });
            return methodEndOffset;
        }

        Document document = editor.getDocument();
        // 获取当前行号
        int currentLineNumber = document.getLineNumber(cursorOffset);

        int lineStartOffset = document.getLineStartOffset(currentLineNumber);
        // 获取当前行的结束偏移量
        int lineEndOffset = document.getLineEndOffset(currentLineNumber);

        // 检查cursorOffset前面是否有字符
        boolean hasCharactersBeforeCursor = cursorOffset > lineStartOffset;

        // 没有在行尾
        if (cursorOffset <= lineEndOffset) {
            int num = hasCharactersBeforeCursor ? 1 : 0;
            ApplicationManager.getApplication().invokeAndWait(() -> {
                if (hasCharactersBeforeCursor) {
                    WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> editor.getDocument().insertString(lineEndOffset, "\n"));
                }
                editor.getCaretModel().moveToOffset(lineEndOffset + num);
                editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            });
            return lineEndOffset + num;
        }

        return cursorOffset;
    }

    public static String getFirstClassName(PsiFile psiFile) {
        PsiClass psiClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
        if (psiClass != null) {
            return psiClass.getName();
        }
        return null;
    }

    //鼠标移动到注释末尾
    public static void moveCaretToCommentEnd(@NotNull Project project, @NotNull PsiComment comment) {
        // 获取编辑器
        PsiFile file = comment.getContainingFile();
        if (file == null) return;

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) return;

        // 获取注释的结束偏移量
        int endOffset = comment.getTextRange().getEndOffset();

        // 移动光标到注释的末尾
        editor.getCaretModel().moveToOffset(endOffset);
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
    }

    //基于鼠标位置判断是否是注释，并将光标移动到注释末尾
    public static void moveCaretToCommentEnd(Editor editor) {
        // 获取光标的逻辑位置
        LogicalPosition logicalPosition = editor.getCaretModel().getLogicalPosition();
        int offset = editor.logicalPositionToOffset(logicalPosition);
        // 获取当前文件
        Document document = editor.getDocument();
        // 检查光标是否在多行注释中
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiComment comment = PsiTreeUtil.getParentOfType(elementAtCaret, PsiComment.class);

        if (null != comment) {
            EditorUtils.moveCaretToCommentEnd(editor.getProject(), comment);
        }
    }

    public static boolean isNotClassHeaderComment(@NotNull PsiComment comment) {
        PsiElement parent = comment.getParent();
        if (parent instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) parent;
            PsiElement firstElement = psiClass.getFirstChild();
            if (firstElement instanceof PsiComment && firstElement == comment) {
                return false;
            }
        }
        return true;
    }


    // PsiComment是否在方法体内
	public static boolean isCommentInsideMethod(@NotNull PsiComment comment) {
	    PsiMethod psiMethod = PsiTreeUtil.getParentOfType(comment, PsiMethod.class);
        return psiMethod != null;
	}

    public static boolean isNotLicenseComment(@NotNull PsiComment comment) {
        PsiFile file = comment.getContainingFile();
        if (file != null) {
            PsiElement firstElement = file.getFirstChild();
            if (firstElement instanceof PsiComment && firstElement == comment) {
                return false;
            }
        }
        return true;
    }

    //PsiComment是否在class范围内
	public static boolean isCommentInsideClass(@NotNull PsiComment comment) {
	    PsiClass psiClass = PsiTreeUtil.getParentOfType(comment, PsiClass.class);
	    return psiClass != null;
	}

    //判断当前光标是否在注释上
	public static boolean isCursorInComment(PsiMethod psiMethod) {
	    Project project = psiMethod.getProject();
        // 获取编辑器
        PsiFile file = psiMethod.getContainingFile();
        if (file == null) return false;
        //获取editor
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) return false;

        int offset = editor.getCaretModel().getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        if (null == elementAtCaret) {
            return false;
        }
        PsiComment comment = PsiTreeUtil.getParentOfType(elementAtCaret, PsiComment.class);
        return null != comment;
	}



    public static boolean isNotMethodComment(@NotNull PsiComment comment) {
        return !(comment.getParent() instanceof PsiMethod);
    }

    // 判断当前光标位置是否是在注释上，如果是，获取当前PsiComment并返回，如果当前行为空，则获取前一行的PsiComment
    public static PsiComment getPsiComment(Editor editor) {
        int offset = editor.getCaretModel().getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiComment comment = PsiTreeUtil.getParentOfType(elementAtCaret, PsiComment.class);
        if(comment == null){
            comment = getPreviousLinePsiComment(editor);
        }
        return comment;
    }

    // 获取当前光标前一行的PsiComment
	public static PsiComment getPreviousLinePsiComment(Editor editor) {
	    if (editor == null) {
	        return null;
	    }
	    Document document = editor.getDocument();
	    int caretOffset = editor.getCaretModel().getOffset();
	    int lineNumber = document.getLineNumber(caretOffset);
	    if (lineNumber == 0) {
	        return null; // 光标在第一行，没有前一行
	    }
	    int previousLineStartOffset = document.getLineStartOffset(lineNumber - 1);
	    PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
	    PsiElement elementAtPreviousLine = psiFile.findElementAt(previousLineStartOffset);
	    return PsiTreeUtil.getParentOfType(elementAtPreviousLine, PsiComment.class);
	}
}

// 快排
