package run.mone.m78.ip.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import run.mone.m78.ip.bo.ClassInfo;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.MessageConsumer;
import run.mone.m78.ip.bo.ProxyAsk;
import run.mone.m78.ip.common.Context;
import run.mone.m78.ip.util.EditorUtils;
import run.mone.m78.ip.util.ImportUtils;
import run.mone.m78.ip.util.PsiClassUtils;
import run.mone.m78.ip.util.UltramanConsole;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.common.ImportCode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
        return null;
    }


    public static PsiClass[] getClassesInPackage(Project project, String packageName) {
        return null;
    }


    /**
     * 移动到方法的开头,且插入空行
     * Navigate to the beginning of the method and insert a blank line.
     *
     * @param project
     */
    public static void moveToMethodAndInsertLine(Project project) {

    }

    public static String getMethodAndLineNumbers(PsiMethod method) {
        return "";
    }

    public static String getClassName(PsiClass psiClass) {
        return "";
    }


    private static boolean readCodeContinue(String lineCode) {
        return lineCode.trim().startsWith("//") || lineCode.trim().startsWith("/*")
                || lineCode.trim().startsWith("*") || lineCode.trim().startsWith("*/");
    }

    public static String getClassAndLineNumbers(PsiClass psiClass) {
        return "";
    }

    public static void addField(Project project, Document document, PsiClass psiClass, String code, String name) {

    }

    /**
     * 添加方法
     *
     * @param project
     * @param code
     */
    public static void addMethod(Project project, String code) {

    }


    public static void insertCode(Project project, String code) {
    }

    public static void insertCode(Project project, String code, boolean enter) {
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

    }

    public static void moveCaretToEndOfLine(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int lineNumber = document.getLineNumber(caretModel.getOffset());
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        caretModel.moveToOffset(lineEndOffset);
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
    }

    @Nullable
    public static Project deleteCode(@NotNull Editor editor) {
        return null;
    }


    /**
     * 删除指定行
     *
     * @param project
     * @param lineToDelete
     */
    public static void deleteLine(Project project, int lineToDelete) {
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
        return null;
    }

    public static String getMethodCode(Project project) {
        return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> getMethod(project).getText());
    }

    public static PsiField getPsiField(Project project) {
        return null;
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
        return null;
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
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
        return psiClass;
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


    public static PsiMethod getMethod(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, false);
        return methods[0];
    }


    public static Map<Integer, String> getLineContentsAndNumbersForMethod(PsiMethod method) {
        return Maps.newHashMap();
    }


    public static String getClassText(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        return editor.getDocument().getText();
    }

    public static String getClassText2(Project project) {
        return "";
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
        return null;
    }

    private static List<PsiJavaFile> getAllJavaFiles(PsiDirectory dir) {
        return null;
    }


    /**
     * 创建包路径
     *
     * @param project
     * @param packageName
     */
    public static void createPackage(Project project, String packageName) {

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
        return "";
    }


    public static void generateCodeWithAi5(GenerateCodeReq req, Project project, String promptName, String[] pramas, Map<String, String> paramMap, BiConsumer<Project, String> consumer, MessageConsumer messageConsumer) {

    }

    public static void setModelAndDebug(Project project, ProxyAsk pa, Map<String, String> paramMap, GenerateCodeReq req) {
    }

    private static int getCodeSize(Map<String, String> paramMap) {
        return 1;
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

    }

    public static List<ClassInfo> getClassList(Project project, String end, String mn) {
        return null;
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
        return null;
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


    @Override
    public void next(Context context, AnActionEvent e) {
        super.next(context, e);
    }
}
