package run.mone.ultraman.listener;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.stmt.Statement;
import com.google.gson.JsonObject;
import com.intellij.codeInsight.completion.CompletionProcess;
import com.intellij.codeInsight.completion.CompletionService;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.Safe;
import com.xiaomi.youpin.tesla.ip.service.BotService;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.common.ScopeEnum;
import run.mone.ultraman.listener.bo.CompletionEnum;
import run.mone.ultraman.manager.InlayHintManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2024/5/31 14:52
 */
@Slf4j
@Data
public class CaretHoverPlugin {

    private Project project;

    private Editor editor;

    private ReentrantLock lock = new ReentrantLock();

    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private Future future;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final BeanCopier PROMPT_COPIER = BeanCopier.create(PromptInfo.class, PromptInfo.class, false);

    /**
     * 检查方法
     * 如果指定资源未打开则返回真
     * 如果事件不为空，获取旧位置，返回旧位置行和列是否都为 0
     */
    private static boolean isResourceClosedOrInitialPosition(CaretEvent event) {
        //只留下了chat
        if (ResourceUtils.checkDisableCodeCompletionStatus()) {
            return true;
        }

        if (!ResourceUtils.isOpen(Const.INLAY) || CompletionEnum.NONE == ConfigUtils.getConfig().getCompletionMode()) {
            return true;
        }

        if (event != null) {
            //刚进来的不做任何处理
            return event.getOldPosition().line == 0 && event.getOldPosition().column == 0;
        }
        return false;
    }


    public CaretHoverPlugin(Project project, Editor editor) {
        this.project = project;
        this.editor = editor;
        editor.getCaretModel().addCaretListener(new CaretListener() {
            @Override
            public void caretPositionChanged(CaretEvent event) {
                // config设置为关闭，则不做任何处理
                if (CompletionEnum.NONE.equals(ConfigUtils.getConfig().getCompletionMode())) {
                    return;
                }
                if (isResourceClosedOrInitialPosition(event)) {
                    return;
                }
                log.debug("caretPositionChanged :{}", event.getCaret());
                try {
                    lock.lock();
                    if (null != future && !future.isDone()) {
                        future.cancel(true);
                    }
                    Pair<Boolean, CompletionEnum> shouldComplete = shouldComplete(editor, event.getCaret());
                    if (shouldComplete.getLeft()) {
                        int offset = editor.getCaretModel().getOffset();
                        InlayHintManager.ins().setCaretOffsetWhenTriggered(offset);
                        log.info("start timer at offset:{}", offset);
                        startTimer(editor, event.getCaret(), shouldComplete.getRight());
                    }
                } finally {
                    lock.unlock();
                }
            }
        });
    }

    public void handleCaretPositionChangedEvent() {
        if (ResourceUtils.checkDisableCodeCompletionStatus()) return;
        ApplicationManager.getApplication().invokeLater(() -> {
            if (isResourceClosedOrInitialPosition(null)) {
                return;
            }
            log.debug("handleCaretPositionChangedEvent");
            try {
                lock.lock();
                if (null != future) {
                    log.info("cancel task");
                    future.cancel(true);
                }
                Caret caret = getCurrentCaretClass(this.editor);
                InlayHintManager.ins().dispose();
                if (!InlayHintManager.ins().hasInlay() && shouldComplete(editor, caret).getLeft()) {
                    log.info("start timer");
                    startTimer(editor, caret, ConfigUtils.getConfig().getCompletionMode());
                }
            } finally {
                lock.unlock();
            }
        });
    }

    @NotNull
    private static String getCurrentLine(Editor editor, Caret caret) {
        try {
            Document document = editor.getDocument();
            int lineNumber = caret.getLogicalPosition().line;
            int lineStartOffset = document.getLineStartOffset(lineNumber);
            int lineEndOffset = document.getLineEndOffset(lineNumber);
            String lineText = document.getText().substring(lineStartOffset, lineEndOffset).trim();
            return lineText;
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            return "";
        }
    }


    public static boolean isCompleteStatement(String lineText) {
        JavaParser parser = new JavaParser();
        ParseResult<Statement> result = parser.parseStatement(lineText);
        return result.isSuccessful();
    }


    private void startTimer(Editor editor, Caret caret, CompletionEnum completionMode) {
        int delay = Integer.parseInt(ResourceUtils.getAthenaConfig().getOrDefault(Const.INLAY_DELAY, "200"));
        log.info("Inlay delay time:{} ms", delay);
        Safe.run(() -> this.future = pool.schedule(() -> Safe.run(() -> showInlay(editor, caret, completionMode)), delay, TimeUnit.MILLISECONDS));
    }


    /**
     * 判断是否应该完成代码自动补全。
     * 如果光标不在方法内，返回false。
     * 如果当前行为空或光标在方法内，返回true。
     */
    private Pair<Boolean, CompletionEnum> shouldComplete(Editor editor, Caret caret) {
        if (InlayHintManager.ins().hasInlay()) {
            return Pair.of(false, CompletionEnum.NONE);
        }
        // lookup的时候不触发
        boolean memberCompletion = isMemberCompletion2(editor);
        log.debug("isMember :{}", memberCompletion);
        if (memberCompletion) {
            return Pair.of(false, CompletionEnum.NONE);
        }

        // 不在文本编辑区时不触发
        // 非java文件不触发
        AtomicReference<PsiFile> psiFile = new AtomicReference<>();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            psiFile.set(PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument()));
        });
        FileType javaFileType = FileTypeRegistry.getInstance().getFileTypeByExtension("java");
        if (psiFile.get() == null || psiFile.get().getFileType() != javaFileType) {
            return Pair.of(false, CompletionEnum.NONE);
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Document document = editor.getDocument();
                PsiDocumentManager.getInstance(project).commitDocument(document);
            });
        });
        log.debug("caret:{}", caret);
        PsiMethod method = getMethod(editor, psiFile.get());
        if (null != method) {
            @NotNull String line = getCurrentLine(editor, caret);
            if (StringUtils.isEmpty(line)) {
                //上一个操作不是回车,则忽略生成(review代码的时候经常这样)
                if ("enter".equals(editor.getUserData(CaretHoverEditorFactoryListener.EDIT_ACTION_TYPE).get())) {
                    editor.getUserData(CaretHoverEditorFactoryListener.EDIT_ACTION_TYPE).set(null);
                } else {
                    return Pair.of(false, CompletionEnum.NONE);
                }
                String bodyCode = getMethodBodyWithoutBracesNewlinesAndComments(method);
                if (StringUtils.isEmpty(bodyCode)) {
                    // 当前行是空，并且当前方法体是空，就走多行逻辑
                    return Pair.of(true, CompletionEnum.MULTI_LINE);
                }
            }

            if (singleLineShouldComplete(line, editor)) {
                // 当前行不为空、不完整且光标在末尾的情况下才触发补全
                return Pair.of(true, CompletionEnum.SINGLE_LINE);
            }
        }
        return Pair.of(false, CompletionEnum.NONE);
    }

    private boolean isCurrentlyInLookup(Project project) {
        log.info("begin isCurrentlyInLookup");
        LookupManager lookupManager = LookupManager.getInstance(project);
        log.info("lookupManager:{}", lookupManager);
        if (lookupManager == null) {
            return false;
        }
        LookupEx activeLookup = lookupManager.getActiveLookup();
        if (activeLookup != null && activeLookup.getCurrentItem() != null) {
            log.info("lookup:{}", activeLookup);
            boolean memberCompletion = isMemberCompletion();
            log.info("isMemberCompletion:{}", memberCompletion);
            return memberCompletion;
        } else {
            log.info("activeLookup status:{}", activeLookup);
            return false;
        }
    }

    public boolean isMemberCompletion2(Editor editor) {
        try {
            int offset = editor.getCaretModel().getOffset();
            CharSequence documentText = editor.getDocument().getCharsSequence();
            return documentText.charAt(offset - 1) == '.';
        } catch (Throwable e) {
            log.warn("check doc range err:", e.getMessage());
            return false;
        }
    }

    private static boolean isActiveLookupOrCompletionProcessPresent(Editor editor) {
        log.info("in . ");
        // Check if there's an active lookup
        Project project = editor.getProject();
        if (project != null) {
            Lookup activeLookup = LookupManager.getInstance(project).getActiveLookup();
            log.info("activelookup:{}", activeLookup);
            if (activeLookup != null) {
                return true;
            }
        }

        // Check if there's an active completion process
        CompletionService completionService = CompletionService.getCompletionService();
        CompletionProcess completionProcess = completionService.getCurrentCompletion();
        log.info("completionProcess:{}", completionProcess);
        return completionProcess != null;
    }

    private boolean isMemberCompletion() {
        log.info("in isMemberCompletion");
        CompletionService completionService = CompletionService.getCompletionService();
        CompletionProcess completionProcess = completionService.getCurrentCompletion();
        return completionProcess != null;
    }

    /**
     * 获取去掉大括号、换行符和单行注释的 PsiMethod body
     *
     * @param psiMethod 要处理的 PsiMethod
     * @return 去掉大括号、换行符和单行注释的 body 字符串
     */
    public static String getMethodBodyWithoutBracesNewlinesAndComments(PsiMethod psiMethod) {
        PsiCodeBlock body = psiMethod.getBody();
        if (body == null) {
            return null;
        }

        // 获取 body 的所有子元素
        PsiElement[] children = body.getChildren();
        StringBuilder bodyContent = new StringBuilder();

        for (PsiElement child : children) {
            String text = child.getText();
            // 跳过大括号、换行符和注释
            if (!(child instanceof PsiComment) && !text.equals("{") && !text.equals("}") && !text.trim().isEmpty()) {
                bodyContent.append(text);
            }
        }

        return bodyContent.toString().trim();
    }


    //从Editor中获取当前caret(class)
    private Caret getCurrentCaretClass(Editor editor) {
        return ApplicationManager.getApplication().runReadAction((Computable<Caret>) () -> editor.getCaretModel().getCurrentCaret());
    }


    private PsiMethod getMethod(Editor editor, PsiFile psiFile) {
        return ApplicationManager.getApplication().runReadAction((Computable<PsiMethod>) () -> {
            CaretModel caretModel = editor.getCaretModel();
            int offset = caretModel.getOffset();
            PsiElement elementAtCaret = psiFile.findElementAt(offset);
            PsiMethod method = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);
            return method;
        });
    }


    private void showInlay(Editor editor, Caret caret, CompletionEnum completionMode) {
        int id = editor.getUserData(CaretHoverEditorFactoryListener.EDIT_INCR_ID_KEY).incrementAndGet();

        JsonObject req = new JsonObject();

        PromptInfo promptInfo = Prompt.getPromptInfo("code_completion");
        PromptInfo forkedPromptInfo = new PromptInfo();
        PROMPT_COPIER.copy(promptInfo, forkedPromptInfo, null);
        GenerateCodeReq generateCodeReq = GenerateCodeReq.builder().project(editor.getProject()).promptInfo(forkedPromptInfo).build();
        PromptService.setReq(generateCodeReq);
        //生成单行代码
        if (CompletionEnum.SINGLE_LINE == completionMode) {
            // 生成部分代码 (160004) 测试用的100302
            req.addProperty("botId", ResourceUtils.getAthenaConfig().getOrDefault(Const.INLAY_BOT_ID, "160004"));
            if (MapUtils.isNotEmpty(forkedPromptInfo.getLabels())) {
                forkedPromptInfo.getLabels().remove("analysis_scope");
            }
            generateCodeReq.setUserSettingScope(ResourceUtils.getAthenaConfig().getOrDefault(Const.INLAY_SCOPE, ScopeEnum.SMethod.name()));
        } else {
            //生成全部代码
            //100294(比较单一的) 100317(支持上下文)
            req.addProperty("botId", "100317");
        }
        @NotNull JsonObject params = BotService.generateParams(generateCodeReq, generateCodeReq.getClassCodeWithMark());
        req.add("params", params);
        req.addProperty("input", "");

        //直接调用m78的bot接口,所以不走stream(open-apis/ai-plugin-new/feature/router/probot/query)
        String text = callBotAndGetResult(req);
        // TODO mason
        log.info("mode:{}, showInlay text:{}", completionMode.getName(), text);
        if (StringUtils.isEmpty(text)) {
            return;
        }

        //又有了新的补全
        if (id < editor.getUserData(CaretHoverEditorFactoryListener.EDIT_INCR_ID_KEY).get()) {
            log.info("id:{} cancel", id);
            return;
        }


        //关闭代码提示
        invokeCloseCompletion(editor);

        ApplicationManager.getApplication().invokeLater(() -> {
            InlayModel inlayModel = editor.getInlayModel();
            int offset = caret.getOffset();
            int lineNumber = editor.getDocument().getLineNumber(offset);
            int lineStartOffset = editor.getDocument().getLineStartOffset(lineNumber);
            String lineText = editor.getDocument().getText(new TextRange(lineStartOffset, offset));
            String replace = lineText.replace("\t", "    "); // 如果有\t转为四个空格，否则有问题
            String indent = replace.replaceAll("\\S", ""); // 获取当前行的缩进

            //插入空行,为了看起来更美观
            //insertBlankLinesFromRenderer(editor, renderer);
            long count = Stream.of(text.split("\n")).count();

            @Nullable Inlay<InlayCustomRenderer> inlay = null;
            if (count <= 1) {
                InlayCustomRenderer renderer = new InlayCustomRenderer(text, null);
                inlay = inlayModel.addInlineElement(offset, true, renderer);
            } else {
                InlayCustomRenderer renderer = new InlayCustomRenderer(text, indent);
                inlay = inlayModel.addBlockElement(offset, true, false, 0, renderer);
            }
            InlayHintManager.ins().setInlay(inlay, text);           /* @Nullable Inlay<InlayCustomRenderer> inlay = inlayModel.addInlineElement(offset, renderer);
            InlayHintManager.ins().setInlay(inlay, text);*/
        });

    }


    private static void invokeCloseCompletion(Editor editor) {
        if (editor == null || editor.getProject() == null || editor.getProject().isDisposed()) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> CodeService.closeCompletion(editor.getProject()));
    }

    private String callBotAndGetResult(JsonObject req) {
        String text = "";
        try {
            text = BotService.callBot(this.project, req);
        } catch (Throwable ignore) {

        }
        return text;
    }

    private static void insertBlankLinesFromRenderer(Editor editor, InlayCustomRenderer renderer) {
        //减去当前空行
        int lineSize = renderer.getLines().size() - 1;
        if (lineSize > 0) {
            CodeService.insertBlankLinesAtCaret(editor.getProject(), editor, lineSize);
        }
    }

    private boolean singleLineShouldComplete(String line, Editor editor) {
        return StringUtils.isBlank(line) || notBlankLineShouldComplete(line, editor);
    }

    private boolean notBlankLineShouldComplete(String line, Editor editor) {
        return !CodeService.isCursorInComment(editor)
                && !CodeService.isAtMultiCommentEnd(line)
                && CodeService.hasValidCharCnt(line)
                && !CodeService.isCaretLineOnlyAnnotation(editor)
                && CodeService.isCaretAtEndOfLine(editor)
                && !CodeService.isPreCharBrace(editor)
                && !isCompleteStatement(line);
    }

    /**
     * 关闭存留的悬浮面板及相关任务
     */
    public void editorReleased() {
        try {
            lock.lock();
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
            InlayHintManager.ins().dispose();
            invokeCloseCompletion(editor);
        } finally {
            lock.unlock();
        }
    }
}
