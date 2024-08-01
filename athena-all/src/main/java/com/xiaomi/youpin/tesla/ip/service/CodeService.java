package com.xiaomi.youpin.tesla.ip.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AllClassesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.common.*;
import com.xiaomi.youpin.tesla.ip.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.openai.OpenaiCall;
import run.mone.openai.ReqConfig;
import run.mone.openai.StreamListener;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.common.TemplateUtils;
import run.mone.ultraman.service.AthenaCodeService;
import run.mone.ultraman.visitor.M78Visitor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class CodeService extends AbstractService {

    private static Gson gson = new Gson();

    /**
     * 打个某个指定的class
     *
     * @param project
     * @param className
     */
    public static PsiClass openJavaClass(Project project, String className) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiClass>) () -> {
            if (project == null) {
                return null;
            }
            //Search for Java classes
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiClass psiClass = javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));

            if (psiClass == null) {
                return null;
            }
            // 创建 OpenFileDescriptor
            OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, psiClass.getContainingFile().getVirtualFile());
            // 使用 FileEditorManager 打开 Java 类
            FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true);
            return psiClass;
        });
    }


    public static PsiClass[] getClassesInPackage(Project project, String packageName) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiPackage psiPackage = javaPsiFacade.findPackage(packageName);
        if (psiPackage != null) {
            PsiClass[] psiClasses = psiPackage.getClasses();
            return psiClasses;
        }
        return new PsiClass[0];
    }


    /**
     * 移动到方法的开头,且插入空行
     * Navigate to the beginning of the method and insert a blank line.
     *
     * @param project
     */
    public static void moveToMethodAndInsertLine(Project project) {
        PsiMethod method = getMethod(project);
        if (null == method) {
            return;
        }
        int offset = method.getTextRange().getStartOffset();
        Editor editor = getEditor(project);
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(offset);
        WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().insertString(offset, "\n\t"));
    }

    //获取Editor中上一行的内容,如果是注释则返回注释内容,且当前所处位置是PsiClass中(class)
    public static String getPreviousLineIfComment(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        int lineNumber = document.getLineNumber(offset);
        System.out.println(lineNumber);
        if (lineNumber > 0) {
            int previousLineStartOffset = document.getLineStartOffset(lineNumber - 1);
            int previousLineEndOffset = document.getLineEndOffset(lineNumber - 1);
            String previousLineText = document.getText(new TextRange(previousLineStartOffset, previousLineEndOffset));
            if (readCodeContinue(previousLineText)) {
                return previousLineText;
            }
        }
        return null;
    }


    //根据class name获取PsiClass(class)
    public static PsiClass getPsiClassByName(Project project, String className) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiClass>) () -> {
            if (project == null || className == null || className.isEmpty()) {
                return null;
            }
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            return javaPsiFacade.findClass(className, GlobalSearchScope.allScope(project));
        });
    }


    //给你class名字和method名字帮我获取这个PsiMethod(class)
    public static String getPsiMethodByName(Project project, String className, String methodName) {
        PsiClass psiClass = getPsiClassByName(project, className);
        if (psiClass == null || methodName == null || methodName.isEmpty()) {
            return null;
        }
        for (PsiMethod method : psiClass.getMethods()) {
            if (methodName.equals(method.getName())) {
                return getMethodAndLineNumbers(method);
            }
        }
        return null;
    }

    //我在Editor中选中一个被调用的方法(类似:),帮我找到这个方法的代码,并返回(class)
    public static PsiMethod findSelectedMethod(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return method;
    }

    public static PsiMethod getPsiMethod(Project project, String className, String methodName) {
        PsiClass psiClass = getPsiClassByName(project, className);
        if (psiClass == null || methodName == null || methodName.isEmpty()) {
            return null;
        }
        for (PsiMethod method : psiClass.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * 获取方法及行号相关信息
     */
    public static String getMethodAndLineNumbers(PsiMethod method) {
        StringBuilder sb = new StringBuilder();
        PsiFile psiFile = method.getContainingFile();
        if (psiFile != null) {
            Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
            if (document != null) {
                TextRange methodRange = method.getTextRange();
                int startLine = document.getLineNumber(methodRange.getStartOffset());
                int endLine = document.getLineNumber(methodRange.getEndOffset());

                for (int line = startLine; line <= endLine; line++) {
                    int lineStartOffset = document.getLineStartOffset(line);
                    int lineEndOffset = document.getLineEndOffset(line);
                    String lineText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
                    // 注释内容直接跳过
                    if (readCodeContinue(lineText)) {
                        continue;
                    }
                    sb.append("line：" + (line + 1) + " code：" + lineText + "\n");
                }
            }
        }
        return sb.toString();
    }

    public static String getClassName(PsiClass psiClass) {
        return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> psiClass.getQualifiedName());
    }

    private static boolean readCodeContinue(String lineCode) {
        return lineCode.trim().startsWith("//") || lineCode.trim().startsWith("/*")
                || lineCode.trim().startsWith("*") || lineCode.trim().startsWith("*/");
    }

    public static String getClassAndLineNumbers(PsiClass psiClass) {
        StringBuilder sb = new StringBuilder();
        PsiFile psiFile = psiClass.getContainingFile();
        if (psiFile != null) {
            Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
            if (document != null) {
                TextRange methodRange = psiClass.getTextRange();
                int startLine = document.getLineNumber(methodRange.getStartOffset());
                int endLine = document.getLineNumber(methodRange.getEndOffset());

                for (int line = startLine; line <= endLine; line++) {
                    int lineStartOffset = document.getLineStartOffset(line);
                    int lineEndOffset = document.getLineEndOffset(line);
                    String lineText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
                    sb.append("line：" + (line + 1) + " code：" + lineText + "\n");
                }
            }
        }
        return sb.toString();
    }

    public static void addField(Project project, Document document, PsiClass psiClass, String code, String name) {
        boolean find = Arrays.stream(psiClass.getFields()).filter(it -> it.getType().getCanonicalText().equals(name)).findAny().isPresent();
        if (!find) {
            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
            PsiField field = factory.createFieldFromText(code, null);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                psiClass.add(field);
                psiClass.getContainingFile().getVirtualFile().refresh(false, false);
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });
        }
    }

    /**
     * 添加方法
     *
     * @param project
     * @param code
     */
    public static void addMethod(Project project, String code) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiMethod method = factory.createMethodFromText(code, null);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiClass.add(method);
            psiClass.getContainingFile().getVirtualFile().refresh(false, false);
        });
    }


    /**
     * 在光标处插入指定数量的空白行。
     *
     * @param project            当前操作的项目对象
     * @param editor             当前操作的编辑器对象
     * @param numberOfBlankLines 要插入的空白行数
     */
    public static void insertBlankLinesAtCaret(Project project, Editor editor, int numberOfBlankLines) {
        // 获取当前文档和光标所在的偏移量
        Document document = editor.getDocument();
        int caretOffset = editor.getCaretModel().getOffset();

        // 创建一个包含指定数量空行的字符串
        StringBuilder newLines = new StringBuilder();
        for (int i = 0; i < numberOfBlankLines; i++) {
            newLines.append("\n");
        }

        //保证线程安全
        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // 在光标位置插入空行
                document.insertString(caretOffset, newLines.toString());

                // 提交文档更改
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });
        });
    }


    public static void insertCode(Project project, String code) {
        insertCode(project, code, true);
    }

    public static void insertCode(Project project, String code, boolean enter) {
        Editor editor = getEditor(project);
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        insertCode(project, code, enter, offset, editor);
    }

    public static void insertCode(Project project, String code, boolean enter, int offset, Editor editor) {
        log.info("insert code at offset:{}", offset);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = editor.getDocument();
            document.insertString(offset, code + (enter ? "\n" : ""));
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
        formatCode(project);
    }

    public static void insertCode(Project project, Document document, int offset, String code, boolean enter, boolean format) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.insertString(offset, code + (enter ? "\n" : ""));
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
        if (format) {
            formatCode(project);
        }
    }

    /**
     * 添加 import list
     *
     * @param project
     * @param importStrList
     */
    @SneakyThrows
    public static void addImport(Project project, List<String> importStrList) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        addImport(project, editor, importStrList);
    }


    public static void addImport(Project project, Editor editor, List<String> importStrList) {
        ImportUtils.addImport(project, editor, importStrList);
    }


    public static PsiClass createPsiClass(PsiElementFactory factory, String className) {
        return ImportUtils.createPsiClass(factory, className);
    }


    /**
     * 根据需求直接生成方法
     *
     * @param project
     * @param prompt
     */
    public static void generateMethod(Project project, String prompt) {
    }

    /**
     * 向方法中添加语句
     *
     * @param project
     * @param code
     */
    public static void addStatementToMethod(Project project, String code) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiStatement statement = factory.createStatementFromText(code, null);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiCodeBlock body = psiMethod.getBody();
            body.addBefore(statement, body.getFirstBodyElement());
            psiMethod.getContainingFile().getVirtualFile().refresh(false, false);
        });
    }

    /**
     * 把代码写入Idea Editor
     *
     * @param project
     * @param editor
     * @param code
     */
    public static void writeCode2(Project project, Editor editor, String code) {
        writeCode4(project, editor, code, true);
    }

    public static void writeCode4(Project project, Editor editor, String code, boolean appendT) {
        @NotNull Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            int offset = caretModel.getOffset();
            Pair<Integer, Integer> pair = lineAndColumnNum(document, offset);
            int line = pair.getKey();
            for (; ; ) {
                int startOffset = caretModel.getOffset();
                int endOffset = document.getLineEndOffset(line - 1);
                String content = document.getText(new TextRange(startOffset, endOffset));
                //如果后边是空的,则直接添加
                if (StringUtils.isEmpty(content.trim())) {
                    String s = "";
                    //补一个tab,看起来更美观
                    if (isFirstColumn(startOffset, editor)) {
                        s = appendT ? "\t" + code : "" + code;
                    } else {
                        s = code;
                    }
                    document.insertString(startOffset, s);
                    caretModel.moveToOffset(startOffset + s.length());
                    editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
                    break;
                } else { //否则插入空行
                    editor.getCaretModel().moveToOffset(endOffset);
                    addLineBreak(project, document, line, 1);
                    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
                }
            }
        });
    }

    /**
     * 用于关闭完成操作
     * 获取项目对应的编辑器，如果不为空则隐藏该项目的活动查找
     */
    public static void closeCompletion(Project project) {
        if (project == null || project.isDisposed()) {
            return;
        }
        @Nullable LookupEx lookup = LookupManager.getInstance(project).getActiveLookup();
        if (null != lookup) {
            LookupManager.getInstance(project).hideActiveLookup();
        }
    }


    /**
     * 将编辑器中的光标移动到当前行的末尾。
     *
     * @param editor 需要操作的编辑器对象
     */
    public static void moveCaretToEndOfLine(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int lineNumber = document.getLineNumber(caretModel.getOffset());
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        caretModel.moveToOffset(lineEndOffset);
    }

    //移动光标到指定PsiMethod上(method)
    public static void moveCaretToMethod(Editor editor, PsiMethod method) {
        int offset = method.getTextOffset();
        LogicalPosition logicalPosition = editor.offsetToLogicalPosition(offset);
        editor.getCaretModel().moveToLogicalPosition(logicalPosition);
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }


    //移动光标到指定PsiComment的最后位置(class)
    public static void moveCaretToCommentEnd(Editor editor, PsiComment comment) {
        int offset = comment.getTextRange().getEndOffset();
        LogicalPosition logicalPosition = editor.offsetToLogicalPosition(offset);
        editor.getCaretModel().moveToLogicalPosition(logicalPosition);
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }

    public static void moveCaretToOffset(Editor editor, int offset) {
        LogicalPosition logicalPosition = editor.offsetToLogicalPosition(offset);
        editor.getCaretModel().moveToLogicalPosition(logicalPosition);
        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }


    // 判断光标Caret所在位置后面是否没有任何字符
    public static boolean isCaretAtEndOfLine(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int lineNumber = document.getLineNumber(caretOffset);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        return caretOffset >= lineEndOffset;
    }

    // 判断光标向前遇到的第一个字符是不是 "{", "}" , ";"
    public static boolean isPreCharBrace(Editor editor) {
        if (editor == null) {
            return false;
        }
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        if (offset == 0) {
            return false;
        }
        CharSequence text = document.getCharsSequence();
        for (int i = offset - 1; i >= 0; i--) {
            char currentChar = text.charAt(i);
            if (Character.isWhitespace(currentChar)) {
                continue;
            }
            return currentChar == '{' || currentChar == '}' || currentChar == ';';
        }
        return false;
    }


    /**
     * 将编辑器的光标移动到方法的结束位置。
     *
     * @param editor 编辑器对象
     * @param method 要移动光标到其结束位置的方法对象
     */
    public static void moveCaretToMethodEnd(Editor editor, PsiMethod method) {
        if (method == null || null == method.getBody()) {
            return;
        }
        PsiJavaToken rBrace = method.getBody().getRBrace();
        if (Objects.isNull(rBrace)) {
            return;
        }
        int methodEndOffset = rBrace.getTextOffset();
        editor.getCaretModel().moveToOffset(methodEndOffset - 1);
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
    }


    private static boolean isFirstColumn(int offset, Editor editor) {
        int lineNumber = editor.getDocument().getLineNumber(offset);
        int lineStartOffset = editor.getDocument().getLineStartOffset(lineNumber);
        boolean isFirstColumn = (offset - lineStartOffset) == 0;
        return isFirstColumn;
    }


    public static void writeCode3(Project project, String code) {
        UltramanConsole.append(project, code, false);
    }


    public static Pair<Integer, Integer> lineAndColumnNum(Document document, int offset) {
        int lineNumber = document.getLineNumber(offset) + 1;
        int columnNumber = offset - document.getLineStartOffset(lineNumber - 1);
        return Pair.of(lineNumber, columnNumber);
    }


    /**
     * 移动到指定行号
     *
     * @param project
     * @param lineNumber
     */
    public static void moveLine(Project project, int lineNumber) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        LogicalPosition position = new LogicalPosition(lineNumber - 1, 0); // 创建逻辑位置对象
        editor.getCaretModel().moveToLogicalPosition(position); // 将光标移动到指定行
    }


    /**
     * 关闭当前Editor
     *
     * @param project
     */
    public static void closeEditor(Project project) {
        EditorUtils.closeEditor(project);
    }

    /**
     * 格式化当前代码
     */
    public static void formatCode(Project project) {
        if (null == project) {
            return;
        }
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            codeStyleManager.reformat(psiFile);
        });
    }

    @Nullable
    public static Project deleteCode(@NotNull Editor editor) {
        Project project = editor.getProject();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiMethod psiMethod = CodeService.getMethod(project);
            if (null != psiMethod) {
                String methodText = psiMethod.getText();
                methodText = methodText.replace("//❁", "");
                PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
                PsiMethod newMethod = factory.createMethodFromText(methodText, psiMethod);
                psiMethod.replace(newMethod);
            }
            if (project != null && editor.getDocument() != null) {
                PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            }
        });
        return project;
    }


    /**
     * 替换指定PsiFile文件的内容为新的文本内容
     *
     * @param psiFile    要替换内容的PsiFile对象
     * @param newContent 新的文件内容字符串
     */
    public static void replaceFileContent(PsiFile psiFile, String newContent) {
        Project project = psiFile.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(psiFile);

        if (document != null) {
            // 开始一个写操作，以修改文件内容
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.setText(newContent);
                psiDocumentManager.commitDocument(document);
            });
        }
    }


    /**
     * 删除指定行
     *
     * @param project
     * @param lineToDelete
     */
    public static void deleteLine(Project project, int lineToDelete) {
        lineToDelete = lineToDelete - 1;
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        int startOffset = document.getLineStartOffset(lineToDelete);
        int endOffset = document.getLineEndOffset(lineToDelete);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.deleteString(startOffset, endOffset);
        });

        ApplicationManager.getApplication().invokeLater(() -> editor.getContentComponent().repaint());
    }


    /**
     * 返回所有method
     *
     * @param project
     * @return
     */
    public static List<String> methods(Project project) {
        Editor editor = getEditor(project);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass currentPsiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        return Arrays.stream(currentPsiClass.getAllMethods()).map(it -> it.getName()).collect(Collectors.toList());
    }


    /**
     * 通过module查询会快点
     *
     * @param project
     * @param module
     * @param name
     * @return
     */
    public static PsiClass getPsiClass(Project project, Module module, String name) {
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.moduleScope(module));
        return psiClass;
    }


    public static List<String> methods(PsiClass psiClass) {
        return Arrays.stream(psiClass.getMethods()).filter(it -> it.getModifierList().hasModifierProperty("public")).map(it -> it.getName()).collect(Collectors.toList());
    }


    public static List<String> fields(Project project) {
        Editor editor = getEditor(project);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass currentPsiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        return Arrays.stream(currentPsiClass.getAllFields()).map(it -> it.getName()).collect(Collectors.toList());
    }

    public static PsiMethod getMethod(Project project) {
        Editor editor = getEditor(project);
        if (null == editor) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        //如果有选中内容,则分析选中的是否是PsiMethod
        if (isTextSelected(editor)) {
            PsiElement pe = getSelectedPsiMethod(editor, psiFile);
            if (pe instanceof PsiMethod) {
                return (PsiMethod) pe;
            }
        }
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();

        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);
        return method;
    }

    public static String getMethodCode(Project project) {
        return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> getMethod(project).getText());
    }

    public static PsiField getPsiField(Project project) {
        Editor editor = getEditor(project);
        if (null == editor) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiField psiField = PsiTreeUtil.getParentOfType(elementAtCaret, PsiField.class);
        return psiField;
    }


    public static boolean isTextSelected(Editor editor) {
        return editor.getSelectionModel().hasSelection();
    }

    public static String getSelectedText(Editor editor) {
        if (isTextSelected(editor)) {
            SelectionModel selectionModel = editor.getSelectionModel();
            String selectedText = selectionModel.getSelectedText();
            return selectedText;
        }
        return "";
    }


    public static PsiElement getSelectedPsiMethod(Editor editor, PsiFile psiFile) {
        SelectionModel selectionModel = editor.getSelectionModel();
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();
        for (int i = start; i < end; i++) {
            PsiElement element = psiFile.findElementAt(i);
            @Nullable PsiMethod pm = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (null != pm) {
                return pm;
            }
        }
        return null;
    }

    public static PsiMethod getPsiMethodWithLineNum(Project project, Document document, int lineNum) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiMethod>) () -> {
            @Nullable PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            int start = document.getLineStartOffset(lineNum - 1);
            int end = document.getLineEndOffset(lineNum - 1);
            for (int i = start; i < end; i++) {
                PsiElement element = psiFile.findElementAt(i);
                @Nullable PsiMethod pm = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (null != pm) {
                    return pm;
                }
            }
            return null;
        });
    }

    public static PsiField getPsiFieldWithLineNum(Project project, Document document, int lineNum) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiField>) () -> {
            @Nullable PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            int start = document.getLineStartOffset(lineNum - 1);
            int end = document.getLineEndOffset(lineNum - 1);
            for (int i = start; i < end; i++) {
                PsiElement element = psiFile.findElementAt(i);
                @Nullable PsiField field = PsiTreeUtil.getParentOfType(element, PsiField.class);
                if (null != field) {
                    return field;
                }
            }
            return null;
        });
    }


    public static PsiClass getPsiClassWithLineNum(Project project, Document document, int lineNum) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiClass>) () -> {
            @Nullable PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            int start = document.getLineStartOffset(lineNum - 1);
            int end = document.getLineEndOffset(lineNum - 1);
            for (int i = start; i < end; i++) {
                PsiElement element = psiFile.findElementAt(i);
                @Nullable PsiClass pm = PsiTreeUtil.getParentOfType(element, PsiClass.class);
                if (null != pm) {
                    return pm;
                }
            }
            return null;
        });

    }


    public static TextRange getTextRange(Project project, final Class<?> clazz) {
        Editor editor = getEditor(project);
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(offset);

        while (true) {
            if (elementAtCaret.getClass().equals(clazz)) {
                return elementAtCaret.getTextRange();
            }
            elementAtCaret = elementAtCaret.getParent();
            if (null == elementAtCaret) {
                return null;
            }
        }
    }

    public static String getText(Project project, Class<? extends PsiElement> clazz) {
        Editor editor = getEditor(project);
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiElement element = PsiTreeUtil.getParentOfType(elementAtCaret, clazz);
        return element.getText();
    }

    public static TextRange getParentTextRange(Project project) {
        Editor editor = getEditor(project);
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        return psiFile.findElementAt(offset).getParent().getParent().getTextRange();
    }


    public static PsiClass getPsiClass(Project project) {
        Editor editor = getEditor(project);
        if (null == editor) {
            return null;
        }
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            PsiElement elementAtCaret = psiFile.findElementAt(offset);
            PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
            return psiClass;
        } else {
            return null;
        }
    }

    public static PsiClass getPsiClassInRead(Project project) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiClass>) () -> {
            return getPsiClass(project);
        });
    }

    public static PsiElement getParentOfType(Project project, Class clazz) {
        Editor editor = getEditor(project);
        if (null == editor) {
            return null;
        }
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiElement res = PsiTreeUtil.getParentOfType(elementAtCaret, clazz);
        return res;
    }


    public static PsiClass getPsiClass2(Project project) {
        Editor editor = getEditor(project);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiClass psiClass = Arrays.stream(psiFile.getChildren()).filter(it -> it instanceof PsiClass).map(it -> (PsiClass) it).findFirst().get();
        return psiClass;
    }

    public static PsiFile getPsiFile(Project project) {
        Editor editor = getEditor(project);
        return PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    }


    public static PsiMethod getMethod(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, false);
        return methods[0];
    }


    public static Map<Integer, String> getLineContentsAndNumbersForMethod(PsiMethod method) {
        Map<Integer, String> lineContentsAndNumbers = new HashMap<>();
        PsiCodeBlock codeBlock = method.getBody();
        if (codeBlock != null) {
            PsiFile containingFile = codeBlock.getContainingFile();
            Document document = PsiDocumentManager.getInstance(method.getProject()).getDocument(containingFile);
            for (PsiElement element : codeBlock.getChildren()) {
                int startOffset = element.getTextOffset();
                int endOffset = startOffset + element.getTextLength();
                int startLineNumber = document.getLineNumber(startOffset);
                int endLineNumber = document.getLineNumber(endOffset);
                for (int i = startLineNumber; i <= endLineNumber; i++) {
                    int lineStartOffset = document.getLineStartOffset(i);
                    int lineEndOffset = document.getLineEndOffset(i);
                    TextRange textRange = new TextRange(lineStartOffset, lineEndOffset);
                    String lineContent = document.getText(textRange).trim();
                    lineContentsAndNumbers.put(i + 1, lineContent);
                }
            }
        }
        return lineContentsAndNumbers;
    }


    public static int getMethodLineCount(PsiMethod method) {
        if (method == null) {
            return 0;
        }
        PsiCodeBlock body = method.getBody();
        if (body == null) {
            return 0;
        }
        Document document = getEditor(method.getProject()).getDocument();
        if (document == null) {
            return 0;
        }
        int startLine = document.getLineNumber(body.getTextRange().getStartOffset());
        int endLine = document.getLineNumber(body.getTextRange().getEndOffset());
        return endLine - startLine + 1;
    }

    public static String getClassText(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        return editor.getDocument().getText();
    }

    public static String getClassText2(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        int offset = editor.getCaretModel().getOffset();
        String text = document.getText();
        return text.substring(0, offset) + "//❁" + text.substring(offset);
    }


    /**
     * 根据prompt删除注解
     *
     * @param project
     */
    public static void removeComments(Project project) {
    }


    public static void createEmptyClass(Project project, String moduleName, String packageName, String className) {
        createEmptyClass(project, moduleName, packageName, className, false);
    }

    /**
     * 创建空的类
     *
     * @param project
     * @param packageName
     * @param className
     */
    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath) {
        PsiClassUtils.createEmptyClass(project, moduleName, packageName, className, testPath);
    }


    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath, boolean isInterface) {
        PsiClassUtils.createEmptyClass(project, moduleName, packageName, className, testPath, false, null, isInterface);
    }


    public static void createEmptyClass(Project project, String moduleName, String packageName, String className, boolean testPath, List<String> annoList) {
        PsiClassUtils.createEmptyClass(project, moduleName, packageName, className, testPath, annoList);
    }


    public static PsiDirectory getSourceDirectory(Project project, String moduleName, boolean testPath) {
        return PsiClassUtils.getSourceDirectory(project, moduleName, testPath);
    }

    public static PsiDirectory getSourceDirectory(Project project, boolean testPath, String packagePath) {
        return PsiClassUtils.getSourceDirectory(project, testPath, packagePath);
    }

    public static PsiDirectory getSourceDirectory(Project project) {
        return PsiClassUtils.getSourceDirectory(project, "", false);
    }


    public static List<String> getClassByServiceAnno(Project project, String anno) {
        List<String> className = Lists.newArrayList();
        PsiDirectory psiDirectory = getSourceDirectory(project);
        List<PsiJavaFile> allJavaFiles = getAllJavaFiles(psiDirectory);
        for (PsiJavaFile javaFile : allJavaFiles) {
            for (PsiClass psiClass : javaFile.getClasses()) {
                PsiAnnotation annotation = psiClass.getAnnotation(anno);
                if (null != annotation) {
                    className.add(psiClass.getName());
                }
            }
        }
        return className;
    }

    private static List<PsiJavaFile> getAllJavaFiles(PsiDirectory dir) {
        List<PsiJavaFile> javaFiles = new ArrayList<>();
        for (PsiFile file : dir.getFiles()) {
            if (file instanceof PsiJavaFile) {
                javaFiles.add((PsiJavaFile) file);
            }
        }
        for (PsiDirectory subdir : dir.getSubdirectories()) {
            javaFiles.addAll(getAllJavaFiles(subdir));
        }
        return javaFiles;
    }


    /**
     * 创建包路径
     *
     * @param project
     * @param packageName
     */
    public static void createPackage(Project project, String packageName) {
        if (project != null) {
            // 获取项目的根目录
            PsiDirectory rootDirectory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
            if (rootDirectory != null) {
                // 创建新的包
                @NonNls @NotNull String path = ProjectRootManager.getInstance(project).getContentSourceRoots()[0].getPath();
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    DirectoryUtil.mkdirs(rootDirectory.getManager(), path + "/" + packageName.replace('.', '/'));
                });
            }
        }
    }

    public static void createPackageInModule(Module module, String packageName) {
        // 获取模块的根模型
        ModifiableRootModel modifiableRootModel = ModuleRootManager.getInstance(module).getModifiableModel();
        try {
            // 获取模块的内容入口
            ContentEntry[] contentEntries = modifiableRootModel.getContentEntries();
            if (contentEntries.length > 0) {
                // 通常情况下，我们使用第一个内容入口
                ContentEntry contentEntry = contentEntries[0];
                // 获取源文件夹的目录
                PsiDirectory directory = PsiManager.getInstance(module.getProject()).findDirectory(contentEntry.getFile());
                if (directory != null) {
                    // 使用 JavaDirectoryService 来创建或获取包
                    PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(directory);
                    if (psiPackage != null) {
                        // 创建包路径
                        String[] names = packageName.split("\\.");
                        for (String name : names) {
                            PsiDirectory subdirectory = directory.findSubdirectory(name);
                            if (subdirectory == null) {
                                // 如果子目录不存在，则创建它
                                directory = directory.createSubdirectory(name);
                            } else {
                                directory = subdirectory;
                            }
                        }
                    }
                }
            }
        } finally {
            // 提交模块的根模型更改
            modifiableRootModel.commit();
        }
    }

    public static List<String> listMethodInfo(String clazz) {
        return Lists.newArrayList();
    }

    /**
     * 生成一个moon demo的handler
     *
     * @param project
     */
    public static void generateMoonHandler(Project project) {
        // 获取项目的根目录
        PsiDirectory rootDirectory = PsiManager.getInstance(project).findDirectory(project.getBaseDir());
        if (null != rootDirectory) {
            @NonNls @NotNull String path = ProjectRootManager.getInstance(project).getContentSourceRoots()[0].getPath();
        }
        String handlerName = "MoonDemoHandler";
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        // 获取最近的PsiClass
        PsiClass currentPsiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        PsiJavaFile psiJavaFile = (PsiJavaFile) currentPsiClass.getContainingFile();
        PsiPackage psiPackage = JavaPsiFacade.getInstance(currentPsiClass.getProject()).findPackage(psiJavaFile.getPackageName());
        String packageStr = psiPackage.getQualifiedName();
        // 创建一个类，写入class描述符是为了能打开它
        String text = "public class " + handlerName + "{}";
        JavaClassUtils.createClass(project, editor, handlerName, text, false, packageStr);
        String qualifiedName = currentPsiClass.getQualifiedName();
        String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        deleteCode(project);
        generateCodeWithAi(project, Prompt.get("moon_handler"), new String[]{packageName, "MoonDemoHandler"});
    }

    /**
     * 获取当前类的package 路径
     *
     * @param project
     * @return 包路径
     */
    public static String getPackagePath(Project project) {
        Editor editor = getEditor(project);
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        // 获取最近的PsiClass
        PsiClass currentPsiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        PsiJavaFile psiJavaFile = (PsiJavaFile) currentPsiClass.getContainingFile();
        PsiPackage psiPackage = JavaPsiFacade.getInstance(currentPsiClass.getProject()).findPackage(psiJavaFile.getPackageName());
        return psiPackage.getQualifiedName();
    }


    @Override
    public void execute(Context context, AnActionEvent e) {
        this.next(context, e);
    }

    public static void addClassAnno(Project project, PsiClass psiClass, List<String> annoList) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        PsiModifierList modifierList = psiClass.getModifierList();
        PsiAnnotation[] annotations = modifierList.getAnnotations();
        // 原来的注解
        List<String> oldAnnoList = Arrays.stream(annotations).map(oldAnno -> "@" + oldAnno.getQualifiedName().substring(oldAnno.getQualifiedName().lastIndexOf('.') + 1)).toList();
        List<String> addAnno = annoList.stream().filter(anno -> !oldAnnoList.contains(anno)).toList();
        List<PsiAnnotation> psiAnnotationList = addAnno.stream().map(i -> elementFactory.createAnnotationFromText(i, null)).toList();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiAnnotationList.forEach(anno -> modifierList.addBefore(anno, modifierList.getFirstChild()));
        });
    }

    /**
     * 删除这个编辑器中的所有内容
     *
     * @param project
     */
    public static void deleteCode(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            editor.getDocument().setText("");
        });
        Caret caret = editor.getCaretModel().getPrimaryCaret();
        caret.moveToOffset(0);
    }

    public static void deleteTextRange(Project project, TextRange range) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = editor.getDocument();
            document.deleteString(range.getStartOffset(), range.getEndOffset());
            PsiDocumentManager.getInstance(project).commitDocument(document);
        });
    }


    public static boolean isClass(String text) {
        if (text.contains(" class ")) {
            return true;
        }
        return false;
    }

    public static boolean isPrivateField(String text) {
        if (text.contains("private") && text.contains(";")) {
            return true;
        }
        return false;
    }


    private static Integer getNumFromStr(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        String lineNumber = "0";
        if (matcher.find()) {
            lineNumber = matcher.group();
        }
        return Integer.valueOf(lineNumber);
    }

    /**
     * 通过chatgpt生成代码,然后插入编辑器中
     *
     * @param project
     * @param context
     * @param prompt
     */
    public static void generateCodeWithAi(Project project, String context, String[] prompt, BiConsumer<Project, String> consumer) {
        String key = ConfigUtils.getConfig().getChatgptKey();
        try {
            OpenaiCall.callStream(key, ProxyUtils.getProxy(), context, prompt, new StreamListener() {
                @Override
                public void onEvent(String str) {
                    consumer.accept(project, str);
                }

                @Override
                public void end() {
                    formatCode(project);
                }
            }, ReqConfig.builder().maxTokens(2000).build());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static void generateCodeWithAi2(Project project, String promptName, String[] pramas, BiConsumer<Project, String> consumer) {
        generateCodeWithAi3(project, promptName, pramas, Maps.newHashMap(), consumer);
    }


    public static void generateCodeWithAi3(Project project, String promptName, String[] pramas, Map<String, String> paramMap, BiConsumer<Project, String> consumer) {
        generateCodeWithAi4(project, promptName, pramas, paramMap, consumer, new MessageConsumer());
    }


    public static void generateCodeWithAi4(Project project, String promptName, String[] pramas, Map<String, String> paramMap, BiConsumer<Project, String> consumer, MessageConsumer messageConsumer) {
        generateCodeWithAi5(GenerateCodeReq.builder().promptName(promptName).project(project).build(), project, promptName, pramas, paramMap, consumer, messageConsumer);
    }


    @SneakyThrows
    public static String call(String promptName, Map<String, String> paramMap) {
        //开启了本地模式(而且只有admin权限的用户可以)
        if (ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_LOCAL, "false").equals("true")) {
            PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
            String prompt = promptInfo.getData();
            if (StringUtils.isNotEmpty(prompt)) {
                prompt = TemplateUtils.renderTemplate(prompt, paramMap.entrySet().stream().collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().toString())));
                log.info(prompt);
            }
        }


        ProxyAsk pa = new ProxyAsk();
        if (StringUtils.isNotEmpty(AthenaContext.ins().getGptModel())) {
            pa.setModel(AthenaContext.ins().getGptModel());
        }
        String id = UUID.randomUUID().toString();
        pa.setId(id);
        pa.setPromptName(promptName);
        pa.setParams(new String[]{});
        pa.setParamMap(paramMap);
        pa.setZzToken(ConfigUtils.getConfig().getzToken());
        NotificationCenter.notice(null, "call prompt:" + promptName + " model:" + pa.getModel(), true);
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        long begin = System.currentTimeMillis();
        OpenaiCall.callStream2(gson.toJson(pa), new StreamListener() {
            @Override
            public void begin() {
            }

            @Override
            public void onEvent(String str) {
                str = new String(Base64.getDecoder().decode(str.getBytes(Charset.forName("utf8"))), Charset.forName("utf8"));
                log.info(str + " " + Arrays.toString(str.getBytes(Charset.forName("utf8"))));
                sb.append(str);
            }

            @Override
            public void onFailure(Throwable t, Response response) {
                if (null != t) {
                    log.error(t.getMessage(), t);
                }
                String message = getErrorMessage(t, response);
                sb.append(message);
                latch.countDown();
            }

            @Override
            public void end() {
                latch.countDown();
            }
        }, ReqConfig.builder().connectTimeout(TimeUnit.SECONDS.toMillis(10)).readTimeout(TimeUnit.SECONDS.toMillis(20)).maxTokens(4096).askUrl(ConfigUtils.getConfig().getAiProxy() + "/ask").build());
        latch.await(10, TimeUnit.SECONDS);
        NotificationCenter.notice(null, "call prompt:" + promptName + " model:" + pa.getModel() + "use time:" + (System.currentTimeMillis() - begin) + "ms", true);
        return sb.toString();
    }


    public static boolean isCursorInComment(Editor editor) {
        // 获取光标的逻辑位置
        LogicalPosition logicalPosition = editor.getCaretModel().getLogicalPosition();
        int offset = editor.logicalPositionToOffset(logicalPosition);

        // 获取当前文件
        Document document = editor.getDocument();

        // 获取光标位置
        CaretModel caretModel = editor.getCaretModel();
        int lineNumber = logicalPosition.line;

        // 获取当前行的文本
        String lineText = document.getText(new TextRange(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber))).trim();

        // 检查该行是否以 "//" 开始
        if (lineText.startsWith("//")) {
            return true;
        }

        // 检查光标是否在多行注释中
        if (editor.getProject() != null) {
            PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
            if (psiFile == null) {
                return false;
            }
            PsiElement elementAtCaret = psiFile.findElementAt(offset);
            PsiComment comment = PsiTreeUtil.getParentOfType(elementAtCaret, PsiComment.class);
            return elementAtCaret instanceof PsiComment || comment != null;
        } else {
            return false;
        }
    }

    public static boolean isAtMultiCommentEnd(String line) {
        return StringUtils.isNotEmpty(line) && line.trim().equals("*/");
    }

    public static boolean hasValidCharCnt(String line) {
        return line.trim().length() > 2;
    }

    // 当前caret是否不在任何方法里但是在类的大括号范围内, 且不在类的第一行(包含"{"的行)和最后一行(包含"}"的行）
    public static boolean isCaretInClassButNotInMethod(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        int lineNumber = document.getLineNumber(offset);

        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);

        if (psiClass == null || psiMethod != null) {
            return false;
        }

        int classStartLine = document.getLineNumber(psiClass.getTextRange().getStartOffset());
        int classEndLine = document.getLineNumber(psiClass.getTextRange().getEndOffset());

        return lineNumber > classStartLine && lineNumber < classEndLine;
    }


    // 当前caret所在行是否只有一个注解
    public static boolean isCaretLineOnlyAnnotation(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        String lineText = document.getText(new TextRange(lineStartOffset, lineEndOffset)).trim();
        return lineText.startsWith("@") && !lineText.contains(" ");
    }


    public static void generateCodeWithAi5(GenerateCodeReq req, Project project, String promptName, String[] pramas, Map<String, String> paramMap, BiConsumer<Project, String> consumer, MessageConsumer messageConsumer) {
        try {
            ProxyAsk pa = new ProxyAsk();
            setModelAndDebug(project, pa, paramMap, req);
            String id = UUID.randomUUID().toString();
            pa.setId(id);
            pa.setPromptName(promptName);
            pa.setParams(pramas);
            pa.setParamMap(paramMap);
            pa.setZzToken(ConfigUtils.getConfig().getzToken());
            String projectName = project.getName();
            String strReq = gson.toJson(pa);
            log.info("ask req:{}", strReq);
            OpenaiCall.callStream2(strReq, new StreamListener() {

                @Override
                public void begin() {
                    messageConsumer.begin(AiMessage.builder().projectName(projectName).type(AiMessageType.begin).id(id).build());
                }

                @Override
                public void onEvent(String str) {
                    str = Base64Utils.decodeBase64String(str);
                    log.debug(str + " " + Arrays.toString(str.getBytes(Charset.forName("utf8"))));
                    consumer.accept(project, str);
                    messageConsumer.onEvent(AiMessage.builder().projectName(projectName).type(AiMessageType.process).id(id).text(str).build());
                }

                @Override
                public void onFailure(Throwable t, Response response) {
                    if (null != t) {
                        log.error(t.getMessage(), t);
                    }
                    String message = getErrorMessage(t, response);
                    messageConsumer.failure(AiMessage.builder().projectName(projectName).id(id).text(message).type(AiMessageType.failure).build());
                    NotificationCenter.notice("调用ai proxy发生了错误:" + message, NotificationType.ERROR);
                }

                @Override
                public void end() {
                    messageConsumer.end(AiMessage.builder().projectName(projectName).type(AiMessageType.success).id(id).build());
                    if (req.isFormat()) {
                        formatCode(project);
                    }
                }
            }, ReqConfig.builder().connectTimeout(TimeUnit.SECONDS.toMillis(10)).readTimeout(TimeUnit.SECONDS.toMillis(60)).maxTokens(4000).askUrl(ConfigUtils.getConfig().getAiProxy() + "/ask").build());
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static void setModelAndDebug(Project project, ProxyAsk pa, Map<String, String> paramMap, GenerateCodeReq req) {
        int size = getCodeSize(paramMap);
        if (StringUtils.isNotEmpty(AthenaContext.ins().getGptModel())) {
            pa.setModel(AthenaContext.ins().getGptModel());
            int maxToken = AthenaContext.ins().getModel(pa.getModel()).getMaxToken();
            pa.setMaxToken(maxToken);
        } else {
            pa.setMaxToken(4000);
        }
        NotificationCenter.notice(project, "prompt:" + req.getPromptName() + " param size:" + size + " model:" + pa.getModel(), true);
        pa.setDebug(AthenaContext.ins().isDebugAiProxy());
    }

    private static int getCodeSize(Map<String, String> paramMap) {
        int size = 0;
        if (paramMap.containsKey("code")) {
            size += paramMap.get("code").length();
        }
        if (paramMap.containsKey("context")) {
            size += paramMap.get("context").length();
        }
        if (paramMap.containsKey("comment") && null != paramMap.get("comment")) {
            size += paramMap.get("comment").length();
        }
        return size;
    }

    @NotNull
    private static String getErrorMessage(Throwable t, Response response) {
        String message = "";
        if (null != response) {
            message = response.message();
            if (StringUtils.isNotEmpty(message)) {
                message = "错误原因:" + message;
            } else {
                message = "";
            }
        }
        if (null != t) {
            message += t.getMessage();
        }
        return message;
    }


    public static void generateCodeWithAi(Project project, String context, String[] prompt) {
        generateCodeWithAi(project, context, prompt, (p, code) -> {
            writeCode(p, code);
        });
    }

    /**
     * 直接插入代码
     *
     * @param project
     * @param str
     */
    public static void writeCode(Project project, String str) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    Caret caret = editor.getCaretModel().getPrimaryCaret();
                    Document document = editor.getDocument();
                    int offset = caret.getOffset();
                    editor.getDocument().insertString(offset, str);
                    int maxOffset = document.getTextLength();
                    int nextLineStartOffset = caret.getOffset() + str.length();
                    int insertOffset = maxOffset > nextLineStartOffset ? nextLineStartOffset : maxOffset;
                    caret.moveToOffset(insertOffset);
                    editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                    PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
                }
        );
    }

    /**
     * 选中一个方法
     *
     * @param project
     */
    public static PsiMethod selectMethod(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (null == editor) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return method;
    }

    public static void deleteMethod(Project project, PsiMethod method) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (null == editor) {
            return;
        }

        if (null == method) {
            return;
        }
        Document document = editor.getDocument();
        int startOffset = method.getTextRange().getStartOffset();

        editor.getCaretModel().getPrimaryCaret().moveToOffset(startOffset);

        int endOffset = method.getTextRange().getEndOffset();
        WriteCommandAction.runWriteCommandAction(project, () -> document.deleteString(startOffset, endOffset));
    }

    public static void deletePsiClass(Project project, PsiClass psiClass) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        int startOffset = psiClass.getTextRange().getStartOffset();
        editor.getCaretModel().getPrimaryCaret().moveToOffset(startOffset);
        int endOffset = psiClass.getTextRange().getEndOffset();
        WriteCommandAction.runWriteCommandAction(project, () -> document.deleteString(startOffset, endOffset));
    }

    public static Editor getEditor(Project project) {
        return EditorUtils.getEditor(project);
    }

    public static Document getDocument(Project project) {
        return getEditor(project).getDocument();
    }

    /**
     * 添加空行
     *
     * @param project
     * @param document
     * @param num
     */
    public static void addLineBreak(Project project, Document document, int lineNum, int num) {
        String v = "\n\t";
        int lineEndOffset = document.getLineEndOffset(lineNum - 1);
        PsiClass pc = getPsiClass(project);
        if (null != pc) {
            int classOffset = pc.getTextRange().getEndOffset();
            lineEndOffset = Math.min(lineEndOffset, classOffset - 1);
            v = "\n";
        }
        int offset = lineEndOffset;
        String str = Strings.repeat(v, num);
        WriteCommandAction.runWriteCommandAction(project, () -> document.insertString(offset, str));
        PsiDocumentManager.getInstance(project).commitDocument(document);
    }

    public static List<ClassInfo> getClassList(Project project, String end, String mn) {
        GlobalSearchScope scope = StringUtils.isNotEmpty(mn) ? GlobalSearchScope.moduleScope(ProjectUtils.getModuleWithName(project, mn)) : GlobalSearchScope.projectScope(project);
        Query<PsiClass> query = AllClassesSearch.search(scope, project, s -> s.endsWith(end));
        Collection<PsiClass> classes = query.findAll();
        return classes.stream().map(it -> {
            String name = it.getQualifiedName();
            String moduleName = PsiClassUtils.getModule(project, it).getName();
            return ClassInfo.builder().className(name).moduleName(moduleName).build();
        }).collect(Collectors.toList());
    }

    private static String findPomDependencies(String basePath, String groupId, String artifactId, String version) throws IOException {
        File baseDir = new File(basePath);
        String[] extensions = {"xml"};
        boolean recursive = true;

        Collection<File> pomFiles = FileUtils.listFiles(baseDir, extensions, recursive);
        for (File pomFile : pomFiles) {
            try {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model = reader.read(new FileReader(pomFile));
                List<Dependency> dependencies = model.getDependencies();
                for (Dependency dependency : dependencies) {
                    if (dependency.getGroupId().equals(groupId) &&
                            dependency.getArtifactId().equals(artifactId) &&
                            dependency.getVersion().equals(version)) {
                        // 找到了指定的依赖项
                        return "找到了";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "没找到";
    }

    /**
     * 获取方法的注释
     *
     * @param psiMethod
     * @return
     */
    public static String getMethodComment(PsiMethod psiMethod) {
        PsiDocComment comment = psiMethod.getDocComment();
        if (comment != null) {
            return comment.getText();
        }
        return null;
    }

    /**
     * 获取属性的注释
     *
     * @param psiField
     * @return
     */
    public static String getFieldComment(PsiField psiField) {
        PsiDocComment comment = psiField.getDocComment();
        if (comment != null) {
            return comment.getText();
        }
        return "";
    }

    public static ImportCode createClassAndAddEmptyLine(Project project, String module, String packageName, String className, String classNameSuffix, boolean isTestClass, boolean isInterface) {
        String qualifiedName = packageName + "." + className;

        PsiClass openJavaClass = CodeService.openJavaClass(project, qualifiedName + classNameSuffix);
        if (null == openJavaClass) {
            CodeService.createEmptyClass(project, module, packageName, className + classNameSuffix, isTestClass, isInterface);
            openJavaClass = CodeService.openJavaClass(project, qualifiedName + classNameSuffix);
        }

        Editor editor = CodeService.getEditor(project);
        addEmptyLine(project, openJavaClass, editor);

        ImportCode importCode = new ImportCode();
        importCode.setProject(project);
        importCode.setEditor(editor);
        return importCode;
    }

    private static void addEmptyLine(Project project, PsiClass openJavaClass, Editor editor) {
        PsiMethod @NotNull [] methods = openJavaClass.getMethods();
        int offset = 0;
        if (0 == methods.length) {
            offset = openJavaClass.getTextRange().getEndOffset() - 3;
        } else {
            PsiMethod psiMethod = methods[methods.length - 1];
            offset = psiMethod.getTextRange().getEndOffset();
        }
        editor.getCaretModel().moveToOffset(offset);
        CodeService.writeCode2(project, editor, "\n");

    }

    public static void tryParserBasedFix(String code, String projectName, String className) {
        if (StringUtils.isBlank(code)) {
            log.warn("empty code detected, projectName:{}, className:{}", projectName, className);
            return;
        }
        AthenaClassInfo athenaClassInfo = AthenaCodeService.classInfoWithDetail(code, true);
        Project project = ProjectUtils.projectFromManager(projectName);
        PsiClass psiClass = PsiClassUtils.findClassByName(project, className);
        // TODO: 根据className pattern制定fix规则
        M78Visitor m78Visitor = new M78Visitor();
        m78Visitor.setMissedImports(ImmutableList.of("org.bson.Document", "dev.morphia.annotations.Id"));
        m78Visitor.setWildCardImportPackages(ImmutableList.of("java.util"));
        psiClass.getParent().accept(m78Visitor);
        // PsiClassFixer.fixUndefinedMethods(psiClass, project);
    }

    public static void tryQuickFix(String code, JsonObject obj, String projectName, String className) {
        if (StringUtils.isBlank(code)) {
            log.warn("calling fix code with req:{}, but code is not generated yet, will do nothing...", obj);
            return;
        }
        Project project = ProjectUtils.projectFromManager(projectName);
        PsiClass psiClass = PsiClassUtils.findClassByName(project, className);
        if (psiClass == null) {
            log.warn("unable to find className:{} in project:{}, will quit quick fix...", className, projectName);
            return;
        }
        PsiFile file = psiClass.getContainingFile();
        if (file == null) {
            log.warn("unable to find psi file for className:{} in project:{}, will quit quick fix...", className, projectName);
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> QuickFixInvokeUtil.quickFix2(project, file));
    }

    @Override
    public void next(Context context, AnActionEvent e) {
        super.next(context, e);
    }

    public static <T> T invoke(Supplier supplier) {
        MutableObject mo = new MutableObject();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            Object obj = supplier.get();
            mo.setValue(obj);
        });
        return (T) mo.getValue();
    }

    public static void invokeLater(Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(() -> runnable.run());
    }
}
