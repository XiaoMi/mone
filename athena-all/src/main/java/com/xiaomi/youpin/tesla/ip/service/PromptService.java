package com.xiaomi.youpin.tesla.ip.service;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.javadoc.PsiDocCommentImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.unfbx.chatgpt.entity.chat.Message;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Completions;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Format;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiChatMessage;
import com.xiaomi.youpin.tesla.ip.bo.robot.Role;
import com.xiaomi.youpin.tesla.ip.bo.z.ZKnowledgeRes;
import com.xiaomi.youpin.tesla.ip.common.*;
import com.xiaomi.youpin.tesla.ip.consumer.CodeConsumer;
import com.xiaomi.youpin.tesla.ip.consumer.FinishConsumer;
import com.xiaomi.youpin.tesla.ip.dialog.DialogReq;
import com.xiaomi.youpin.tesla.ip.dialog.DialogResult;
import com.xiaomi.youpin.tesla.ip.dialog.ParamTableDialog;
import com.xiaomi.youpin.tesla.ip.dialog.SelectClassAndMethodDialog;
import com.xiaomi.youpin.tesla.ip.framework.flex.Flex;
import com.xiaomi.youpin.tesla.ip.listener.OpenAiListener;
import com.xiaomi.youpin.tesla.ip.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.openai.Ask;
import run.mone.openai.OpenaiCall;
import run.mone.openai.ReqConfig;
import run.mone.openai.listener.AskListener;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.background.AthenaEditorTask;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.bo.*;
import run.mone.ultraman.common.CodeUtils;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.common.TemplateUtils;
import run.mone.ultraman.manager.InlayHintManager;
import run.mone.ultraman.service.AiCodeService;
import run.mone.ultraman.service.AthenaCodeService;
import run.mone.ultraman.state.AthenaEvent;
import run.mone.ultraman.state.ProjectFsmManager;
import run.mone.ultraman.state.PromptAndFunctionProcessor;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.tesla.ip.util.PromptUtils.*;

/**
 * @author goodjava@qq.com
 * @author baoyu
 * @date 2023/5/11 21:53
 * <p>
 * 这个类主要处理Prompt在Idea中的落地(写入编辑器)
 */
@Slf4j
public class PromptService {

    private static Gson gson = new Gson();

    /**
     * 动态执行
     *
     * @param req
     * @return
     */
    public static String dynamicInvoke(GenerateCodeReq req) {
        setReq(req);
        switch (req.getPromptType()) {
            case createClass -> createClass(req.getProject(), req.getModule().getName(), req.getPromptName());
            case createClass4 -> createClass4(req);
            case createMethod -> createMethod(req);
            case createMethod2 -> createMethod2(req);
            case comment -> addComment(req);
            case lineByLineComment -> lineByLineCommentOrCode(req.getPromptName(), req);
            case createFile -> createFile(req.getPromptName(), req.getFileName(), req);
            case modifyClass -> updateClass(req);
            case modifyMethod -> modifyMethod(req);
            case select -> select(req, req.getProject(), req.getModule(), req.getPromptInfo());
            case executeBot -> BotService.executeBot(req);
            case removeComment -> removeComment(req.getProject());
            case showInfo -> showInfo(req);
            case repleaceSelectContent -> repleaceSelectContent(req);
            case testPrompt -> testPrompt(req);
            case checkPomVersion -> checkPomVersion(req);
            case generateBootStrapAnno -> generateAnnoForBootStrap(req.getPromptName(), req.getProject());
            case inlayHint -> inlayHint(req);
            case genBizMethodCode -> genBizMethodCode(req);
            case generateMethod -> generateMethod(req, req.getProject(), "");
            case bot -> RobotService.bot(req);
            case generateTestMethod -> generateTestMethod(req);
            case generateMiapiMethod -> generateMiapiMethod(req);
            case generateInterface -> generateInterface(req);
            case question -> question(req);
            case createClass2 ->
                    createClass2(req.getProject(), req.getPromptName(), req.getShowDialog(), req.getParam());
            default -> {
                return "UnSupport";
            }
        }
        return "ok";
    }


    private static void createClass4(GenerateCodeReq req) {
        String promptName = req.getPromptName();
        List<AiChatMessage<?>> list = Lists.newArrayList();
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        String prompt = promptInfo.getData();
        String code = getCode(req, new PromptContext());
        prompt = TemplateUtils.renderTemplate(prompt, ImmutableMap.of("code", code));
        list.add(AiChatMessage.builder().data(prompt).role(Role.user).build());
        List<com.xiaomi.youpin.tesla.ip.bo.chatgpt.Message> messageList = list.stream().map(it -> com.xiaomi.youpin.tesla.ip.bo.chatgpt.Message.builder().content(it.toString()).role(it.getRole().name()).build()).collect(Collectors.toList());
        Completions completions = Completions.builder()
                .stream(false)
                .response_format(Format.builder().build()).messages(messageList).build();
        JsonObject jsonObj = AiService.call(GsonUtils.gson.toJson(completions), Long.valueOf(promptInfo.getLabels().getOrDefault("timeout", "50000")), true);
        System.out.println(jsonObj);
        String className = jsonObj.get("name").getAsString();
        String generateCode = jsonObj.get("code").getAsString();
        String packageStr = jsonObj.get("package").getAsString();
        PsiClassUtils.createClass(req.getProject(), packageStr, className, generateCode);
    }

    //ai会向你提问(绝大部分分多步的操作,ai问你更合适)
    private static void question(GenerateCodeReq req) {
        String projectName = req.getProjectName();
        ProjectFsmManager.map.get(projectName)
                .getFsm()
                .getEventQueue().add(AthenaEvent.builder()
                        .promptInfo(req.getPromptInfo())
                        .project(req.getProjectName())
                        .promptType(req.getPromptType())
                        .build());
    }

    //后边代码尽量用这里获取的内容,editor中的内容放入到req中,避免后边的再次获取
    public static void setReq(GenerateCodeReq req) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Safe.run(() -> {
                Project project = req.getProject();
                req.setProjectName(project.getName());
                String moduleName = getModuleName(req);
                //当前module的名字
                req.setModuleName(moduleName);
                //所有module的列表
                req.setModuleNameList(ProjectUtils.listAllModules(project).stream().filter(it -> !it.equals(project.getName())).collect(Collectors.toList()));
                Editor editor = CodeService.getEditor(project);
                if (null != editor) {
                    req.setEditor(editor);
                    VirtualFile vf = editor.getVirtualFile();
                    String fileName = vf.getName();
                    req.setFileName(fileName);
                    PsiClass psiClass = CodeService.getPsiClass(project);
                    if (null != psiClass) {
                        req.setQualifiedName(psiClass.getQualifiedName());
                        req.setClassPackage(getClassPackage(psiClass.getQualifiedName()));
                        req.setClassName(psiClass.getName());
                        req.setClassCode(psiClass.getText());

                        //这个类的父类或者接口中已经实现的方法定义
                        Set<PsiMethod> methodSet = PsiClassUtils.findInheritedNonPrivateNonStaticMethods(psiClass);
                        List<String> inheritedMethods = methodSet.stream().map(it -> {
                            return PsiClassUtils.generateMethodSignature(it);
                        }).collect(Collectors.toList());
                        req.setInheritedMethods(inheritedMethods);
                    }
                    PsiMethod psiMethod = CodeService.getMethod(project);
                    if (null != psiMethod) {
                        req.setPsiMethod(psiMethod);
                        req.setMethodCode(psiMethod.getText());
                        req.setLineNumberedMethod(CodeService.getMethodAndLineNumbers(psiMethod));
                    }
                    //选中的内容
                    req.setSelectText(EditorUtils.getSelectedContentOrLine(editor, false));
                    //当前行的内容
                    req.setCurrentLine(EditorUtils.getCurrentLineContent(editor));
                    Document document = FileDocumentManager.getInstance().getDocument(editor.getVirtualFile());
                    if (null != document) {
                        //这里可能有多个class的内容
                        String virtualFileText = document.getText();
                        req.setVirtualFileText(virtualFileText);
                    }
                    req.setClassCode2(CodeService.getClassText2(project));

                    CaretModel caretModel = editor.getCaretModel();
                    if (null != caretModel) {
                        int offset = caretModel.getOffset();
                        req.setOffset(offset);
                        req.setClassCodeWithMark(replaceCaretWithCode(offset, document));
                    }
                    // 如果是根据注释生成代码，且满足规则，则获取注释
                    setCommentInParam(req, editor);
                }
                req.setSystemName(System.getProperty("os.name"));
                req.setIdeaVersion(ApplicationInfo.getInstance().getBuild().asString());
                req.setPluginVersion(new Version().toString());
            });
        });
    }

    private static void setCommentInParam(GenerateCodeReq req, Editor editor){
        if(Action.GENERATE_CODE == ActionUtils.getActionByReq(req)){
            PsiComment comment = EditorUtils.getPsiComment(editor);
            if(comment != null && EditorUtils.isCommentInsideClass(comment)
                    && EditorUtils.isNotClassHeaderComment(comment)
                    && EditorUtils.isNotMethodComment(comment)){
                Map<String, String> param = req.getParam();
                if(param == null){
                    param = new HashMap<>();
                    param.put(Const.GENERATE_CODE_COMMENT, comment.getText().trim());
                    req.setParam(param);
                }else if (!param.containsKey(Const.GENERATE_CODE_COMMENT)){
                    param.put(Const.GENERATE_CODE_COMMENT, comment.getText().trim());
                }
            }
        }
    }

    // 根据传入的carte的位置和psiClass, 将psiClass中caret所在位置替换为"$$这里需要补全代码$$"并返回替换后的代码字符串，不要影响原来的psiClass
    public static String replaceCaretWithCode(int caret, Document document) {
        if (document == null) {
            return "";
        }
        String classText = document.getText();
        StringBuilder modifiedText = new StringBuilder(classText);
        modifiedText.replace(caret, caret, "$$这里需要补全代码$$");
        return modifiedText.toString();
    }

    private static String getClassPackage(String qualifiedName) {
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            return qualifiedName.substring(0, lastDotIndex);
        }
        return "";
    }


    //获取编辑器的注释信息
    private static String getComment(Editor editor) {
        return getComment(editor, "//biz:");
    }

    private static String getComment(Editor editor, String str) {
        String content = EditorUtils.getSelectContent(editor);
        if (content.contains(str)) {
            int index = content.indexOf(str) + str.length();
            return content.substring(index);
        }
        return content;
    }


    /**
     * 生成业务方法
     *
     * @param req
     */
    public static void genBizMethodCode(GenerateCodeReq req) {
        Editor editor = CodeService.getEditor(req.getProject());
        if (StringUtils.isEmpty(req.getComment())) {
            req.setComment(getComment(editor));
        }
        CodeService.moveCaretToEndOfLine(editor);
        CodeService.writeCode(req.getProject(), "\n");
        Map<String, String> m = new HashMap<>();
        m.put("comment", req.getComment());
        //添加代码
        addCode(req, m, new PromptContext());
        //添加上下文
        addContext(req, m, new PromptContext());
        ImportCode importCode = new ImportCode();
        importCode.setProject(req.getProject());
        importCode.setEditor(editor);

        AthenaEditorTask.start(new AthenaEditorTask(req, "Athena:Biz", m, new MessageConsumer() {
            @Override
            public void onEvent(AiMessage message) {
                importCode.append(message.getText());
            }
        }));

    }

    private static String addCode(GenerateCodeReq req, Map<String, String> m, PromptContext context) {
        String code = getCode(req, context);
        //如果class code 太大,这里需要筛选出需要的method
        if (code.length() > AthenaCodeService.CLASS_MAX_LEN && "class".equals(context.getScope())) {
            if (StringUtils.isNotEmpty(req.getChatComment())) {
                List<String> list = AthenaCodeService.getMethodCodeList(CodeReq.builder().code(code).requirement(req.getChatComment()).build());
                if (list.size() > 0) {
                    code = list.stream().collect(Collectors.joining("\n"));
                }
            }
        }
        m.put("code", code);
        if (StringUtils.isNotEmpty(req.getMethodCode())) {
            m.put("methodCode", req.getMethodCode());
        }
        return code;
    }


    //添加上下文信息到映射中
    public static void addContext(GenerateCodeReq req, Map<String, String> m, PromptContext promptContext) {
        Mutable<String> context = new MutableObject<>();
        String scope = req.getScope();
        if (!StringUtils.isEmpty(scope)) {
            promptContext.setScope(scope);
            //method不需要计算任何上下文
            if (scope.equals("method")) {
                promptContext.setScope("method");
                m.put("context", "");
                return;
            }
        }
        //可以粗略的认为是把@Resource的类导入到上下文
        processGenerateCodeReq(req, promptContext, context);
        m.put("context", context.getValue());
    }

    private static void processGenerateCodeReq(GenerateCodeReq req, PromptContext promptContext, Mutable<String> context) {
        if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "context")) {
            ApplicationManager.getApplication().runReadAction(() -> {
                PsiClass clazz = CodeService.getPsiClass(req.getProject());
                if (null == clazz) {
                    return;
                }
                List<PsiField> list = PsiClassUtils.findFieldsWithResourceAnnotation(clazz);
                if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "ai_context")) {
                    List<String> resourceCode = list.stream().map(it -> it.getType().getCanonicalText()).toList();
                    promptContext.setResourceCode(resourceCode);
                    //设置po列表信息
                    setPoList(promptContext, list);
                } else {
                    List<String> classList = PsiClassUtils.getClassText(req.getProject(), list);
                    context.setValue(Joiner.on("\n").join(classList));
                }

                //用Service那种模式的Flex
                setPoClassInfo(promptContext, clazz);
            });
        }
    }

    private static void setPoClassInfo(PromptContext promptContext, PsiClass clazz) {
        String boCode = Flex.findAndProcessServiceImplWithGeneric(clazz);
        if (StringUtils.isNotEmpty(boCode)) {
            List<PoClassInfo> polist = promptContext.getPoClassInfos();
            if (null == polist) {
                polist = Lists.newArrayList(PoClassInfo.builder().code(boCode).name("").build());
            } else {
                polist.add(PoClassInfo.builder().code(boCode).name("").build());
            }
            promptContext.setPoClassInfos(polist);
        }
    }

    private static void setPoList(PromptContext promptContext, List<PsiField> list) {
        Map<String, String> map = Maps.newHashMap();
        list.stream().forEach(it -> {
            if (it.getType() instanceof PsiClassType pct) {
                PsiClass psiClass = pct.resolve();
                Pair<String, String> pair = Flex.getBaseMapperGenericTypeNameAndText(psiClass);
                if (StringUtils.isNotEmpty(pair.getKey())) {
                    map.put(pair.getKey(), pair.getValue());
                }
            }
        });
        promptContext.setPoClassInfos(map.entrySet().stream().map(it -> {
            return PoClassInfo.builder().name(it.getKey()).code(it.getValue()).build();
        }).collect(Collectors.toList()));
    }

    public static String getResourceFromAi(Project project, Map<String, String> m, PromptContext promptContext, List<String> resourceCode) {
        List<String> resouceList = new ArrayList<>();
        if (resourceCode.size() > 3 && AthenaContext.ins().gptModel().isOptimizeTokens()) {
            // 用chatGpt分析一遍需要用到哪些resource下的内容
            Map<String, String> resourceMap = new HashMap<>();
            resourceMap.put("beanList", gson.toJson(resourceCode));
            String comment = "";
            if (m.get("comment") == null) {
                comment = m.get("code");
            }
            resourceMap.put("comment", comment);
            String res = CodeService.call("ai_context", resourceMap);

            if (StringUtils.isEmpty(res)) {
                return "";
            }
            List<String> tmpList = gson.fromJson(res, new TypeToken<List<String>>() {
            }.getType());
            resouceList.addAll(tmpList);
        } else {
            resouceList.addAll(resourceCode);
        }

        //如果resource里边已经有了,则module context就不需要再次引入了
        promptContext.setResourceBeanList(resouceList);

        return ApplicationManager.getApplication().runReadAction((Computable<String>) () -> {
            String context = resouceList.stream().map(it -> {
                String str = PsiClassUtils.getClassText(project, it);
                return str;
            }).collect(Collectors.joining("\n"));
            return context;
        });
    }


    public static void inlayHint(GenerateCodeReq req) {
        req.setFormat(false);
        CodeService.insertCode(req.getProject(), "", false);
        PromptInfo promptInfo = Prompt.getPromptInfo("hi2");
        PromptType promptType = Prompt.getPromptType(promptInfo);
        Map<String, String> m = new HashMap<>();
        Editor editor = CodeService.getEditor(req.getProject());
        //添加代码
        addCode(req, m, new PromptContext());
        //添加上下文
        addContext(req, m, new PromptContext());
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();

        AthenaEditorTask.start(new AthenaEditorTask(req, "Athena", m, new FinishConsumer() {

            @Override
            public void end(AiMessage message) {
                String content = getContent();
                ApplicationManager.getApplication().invokeLater(() -> {
                    String newLines = "\n".repeat(content.split("\n").length);
                    WriteCommandAction.runWriteCommandAction(req.getProject(), () -> {
                        editor.getDocument().insertString(offset, newLines);
                        PsiDocumentManager.getInstance(req.getProject()).commitDocument(editor.getDocument());
                    });
                    InlayHintManager.ins().addInlayHint(editor, offset, content);
                });
            }
        }));
    }

    /**
     * 测试prompt的接口
     *
     * @param req
     */
    public static void testPrompt(GenerateCodeReq req) {
        //从aiproxy获取参数列表
        Map<String, Object> map = getParamsFromAiProxy(req.getPromptName(), req.getPromptInfo().getMeta());
        //输入prompt参数
        ParamTableDialog table = new ParamTableDialog(ParamDialogReq.builder().title("test prompt").build(), req.getProject(), map, ImmutableMap.of(), req.getPromptInfo());
        table.setConsumer(values -> {
            Editor editor = EditorUtils.getOrOpenEditor(req.getProject(), "prompt.md", "");
            Map<String, String> m = table.getValuesMap().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().toString()));
            AthenaEditorTask.start(new AthenaEditorTask(req, "Athena", m, new CodeConsumer(editor, req)));
        });
        table.show();
    }

    private static void repleaceSelectContent(GenerateCodeReq req) {
        Editor editor = CodeService.getEditor(req.getProject());
        if (null == editor) {
            HintUtils.show(req.getProjectName(), com.xiaomi.youpin.tesla.ip.bo.Message.selectTextMsg, true);
            return;
        }
        String text = getCode(req, new PromptContext());
        if (StringUtils.isNotEmpty(text)) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel.hasSelection()) {
                int start = selectionModel.getSelectionStart();
                int end = selectionModel.getSelectionEnd();
                Document document = CodeService.getDocument(req.getProject());
                AthenaEditorTask task = new AthenaEditorTask(req, "Athena", ImmutableMap.of("code", text), new MessageConsumer() {
                    private StringBuilder sb = new StringBuilder();

                    @Override
                    public void onEvent(AiMessage message) {
                        sb.append(message.getText());
                    }

                    @Override
                    public void end(AiMessage message) {
                        WriteCommandAction.runWriteCommandAction(req.getProject(), () -> {
                            document.replaceString(start, end, sb.toString());
                            PsiDocumentManager.getInstance(req.getProject()).commitDocument(document);
                        });
                    }
                });
                AthenaEditorTask.start(task);
            }
        }
    }

    @NotNull
    private static String getModuleName(GenerateCodeReq req) {
        String moduleName = "";
        if (null == req.getModule()) {
            Module module = ProjectUtils.getCurrentModule(req.getProject());
            if (null != module) {
                req.setModule(module);
                moduleName = module.getName();
            }
        } else {
            moduleName = req.getModule().getName();
        }
        return moduleName;
    }

    /**
     * 在聊天窗口显示信息
     *
     * @param req
     */
    public static void showInfo(GenerateCodeReq req) {
        //不需要格式化代码
        req.setFormat(false);
        //在聊天框显示
        req.setTaskType("chat");
        generateMethod(req);
    }


    public static void generateMethod(Project project, String content) {
        ClientData clientData = AthenaContext.ins().getClientData(project.getName());
        GenerateCodeReq req = AiCodeService.getGenerateCodeReq(project, clientData.getScope(), content);
        req.setEditor(CodeService.getEditor(project));
        req.setModule(ProjectUtils.getCurrentModule(project));
        req.setTaskType("edit");
        PromptService.setReq(req);
        PromptService.generateMethod(req);
        //添加到聊天框
        ChromeUtils.call(req.getProject().getName(), content, 0);
    }


    public static void generateMethod(GenerateCodeReq req) {
        generateMethod(req, true);
    }


    //创建方法都收口在这里了
    public static AthenaTask generateMethod(GenerateCodeReq req, boolean startTask) {
        Project project = req.getProject();
        String promptName = req.getPromptName();
        Map<String, String> aiReqMap = new HashMap<>();
        initAiReqMap(req, aiReqMap);
        //如果是chat,这里拿到的会是注释
        PromptContext context = new PromptContext();
        context.setProject(project.getName());
        context.setModule(req.getModule().getName());
        context.setEditor(req.getEditor());
        String code = addCode(req, aiReqMap, context);

        if (StringUtils.isEmpty(code) && req.getPromptInfo().open("needCode")) {
            log.info("code is empty prompt name:{}", promptName);
            return null;
        }

        AthenaTask task = new AthenaTask(project, "Athena", promptName, code, aiReqMap);
        //在这里的都是异步执行
        task.setInitRunnable(() -> {
            if (StringUtils.isNotEmpty(req.getUserSettingScope())) {
                req.setScope(req.getUserSettingScope());
            } else {
                //自动分析scope
                if (req.getPromptInfo().open("analysis_scope") && AthenaContext.ins().gptModel().isSupportJsonResponse()) {
                    analysisScope(req, context);
                } else {
                    req.setScope("class");
                }
            }
            addContext(req, aiReqMap, context);
            addClass(req, aiReqMap, context);
            addField(req, aiReqMap);
            addInheritedMethods(req, aiReqMap, context);
        });

        task.setReq(req);
        task.setFormat(req.isFormat());
        task.setPromptContext(context);
        task.setType(req.getTaskType());
        if (startTask) {
            AthenaTask.start(task);
        }
        return task;
    }

    //添加抽象类或interface中已经实现的方法定义
    public static void addInheritedMethods(GenerateCodeReq req, Map<String, String> aiReqMap, PromptContext context) {
        if (null == req.getInheritedMethods()) {
//            NotificationCenter.notice(ErrorMessage.ERR_NOT_IN_CLASS_SCOPE, NotificationType.ERROR);
//            throw new RuntimeException(ErrorMessage.ERR_NOT_IN_CLASS_SCOPE);
            log.error(ErrorMessage.ERR_NOT_IN_CLASS_SCOPE);
        } else {
            aiReqMap.put("inheritedMethods", req.getInheritedMethods().stream().collect(Collectors.joining("\n\n\n")));
        }
    }

    private static void initAiReqMap(GenerateCodeReq req, Map aiReqMap) {
        aiReqMap.put("current_module_name", req.getModuleName());
        aiReqMap.put("project_module_list", req.getModuleNameList().stream().collect(Collectors.joining(",")));
    }

    public static void analysisScope(GenerateCodeReq req, PromptContext context) {
        Map<String, Object> m = new HashMap<>();
        m.put("class", req.getClassCode());
        //获取方法签名(代码补全的时候)
        if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "method_info")) {
            m.put("code", req.getMethodCode());
        } else {
            //获取注释
            String comment = req.getParam().getOrDefault(Const.GENERATE_CODE_COMMENT, req.getChatComment());
            if (StringUtils.isEmpty(comment)) {
                comment = req.getCurrentLine();
            }
            if (StringUtils.isEmpty(comment)) {
                context.setScope("class");
                req.setScope("class");
                return;
            }
            m.put("code", comment);
            String scope = com.xiaomi.youpin.tesla.ip.common.StringUtils.extractEndingKeyword(comment);
            //不再计算scope
            if (StringUtils.isNotEmpty(scope)) {
                context.setScope(scope);
                req.setScope(scope);
                return;
            }
        }
        initAiReqMap(req, m);
        PromptInfo promptInfo = Prompt.getPromptInfo("analysis_scope");
        JsonObject obj = PromptAndFunctionProcessor.callPrompt(req.getProject(), promptInfo, m);
        String scope = obj.get("scope").getAsString();
        context.setScope(scope);
        req.setScope(scope);
    }

    //计算两数和

    public static void addField(GenerateCodeReq req, Map<String, String> map) {
        if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "select_field")) {
            map.put("field", CodeService.getPsiField(req.getProject()).getName());
        }
    }

    public static void addClass(GenerateCodeReq req, Map<String, String> map, PromptContext context) {
        map.put("lang", "java"); // HINT: 标记来源
        if (LabelUtils.isOpen(req.getProject(), req.getPromptInfo(), "add_class") && !"method".equals(context.getScope())) {
            List<AthenaMethodInfo> list = AthenaCodeService.parseMethodCode(req.getClassCode());
            List<AthenaFieldInfo> fieldList = AthenaCodeService.parseFieldCode(req.getClassCode());
            //通过需求生成的代码,class不能用全部的,不然会超过预期
            if (StringUtils.isNotEmpty(context.getComment())) {
                //后边异步计算
                context.setMethodCodeList(list);
                context.setFieldCodeList(fieldList);
                context.setClazzName(req.getClassName()); // HINT: class如果使用FQCN则不能被parse
            }

            //完成脱敏
            String classCode = AiCodeService.desensitizeCode(req.getClassCode());
            log.info("desensitize code class:{}", classCode);
            map.put("class", classCode);
            map.put("fqcn", req.getQualifiedName()); //HINT: FQCN单独传递, 在prompt模板中需要使用自定义函数 ${prompt_value('fqcn','')}来实现向下兼容
        } else {
            map.put("class", "");
        }
    }


    public static String getCode(GenerateCodeReq req, PromptContext context) {
        String text = "";
        if (req.getPromptInfo().open("selectComment")) {
            text = req.getChatComment();
            context.setComment(text);
        }
        if (req.getPromptInfo().open("selectText")) {
            text = req.getSelectText();
            if (StringUtils.isEmpty(text)) {
                HintUtils.show(req.getEditor(), com.xiaomi.youpin.tesla.ip.bo.Message.selectTextMsg, true);
            }
        }
        if (req.getPromptInfo().open("selectMethod")) {
            context.setScope("method");
            text = req.getMethodCode();
            if (StringUtils.isEmpty(text)) {
                HintUtils.show(req.getEditor(), com.xiaomi.youpin.tesla.ip.bo.Message.selectMethodMsg, true);
            }
        }
        if (req.getPromptInfo().open("selectClass")) {
            context.setScope("class");
            context.setProject(req.getProject().getName());
            context.setModule(!Objects.isNull(req.getModule()) ? req.getModule().getName() : ProjectUtils.getCurrentModule(req.getProject()).getName());
            text = req.getClassCode();
        }

        //选择整个java文件(里边有package信息)
        if (req.getPromptInfo().open("selectFile")) {
            text = req.getVirtualFileText();
        }

        if (req.getPromptInfo().open("selectClassName")) {
            text = req.getQualifiedName();
        }

        if (req.getPromptInfo().open("commitFile")) {
            text = GitUtils.getAffectedFileNames(req.getProject()).stream().collect(Collectors.joining("\n"));
        }

        if (req.getPromptInfo().open("selectClass2")) {
            text = req.getClassCode2();
        }
        return text == null ? "" : text;
    }

    /**
     * 删除逐行注释
     *
     * @param project
     */
    private static void removeComment(Project project) {
        PsiMethod psiMethod = CodeService.getMethod(project);
        Editor editor = CodeService.getEditor(project);
        if (null == psiMethod) {
            HintUtils.show(editor, com.xiaomi.youpin.tesla.ip.bo.Message.selectMethodMsg, true);
            return;
        }
        PsiMethodUtils.deleteCommentsFromMethod(project, psiMethod, comment -> comment.getText().startsWith("//Athena:"));
    }

    /**
     * 生成方法(在edit中)
     *
     * @param project
     * @param text
     */
    public static void generateMethod(GenerateCodeReq req, Project project, String text) {
        Editor codeEditor = CodeService.getEditor(project);
        if (StringUtils.isEmpty(text)) {
            text = getComment(codeEditor, "//ai:");
        }
        CodeService.moveCaretToEndOfLine(codeEditor);
        CodeService.writeCode2(project, codeEditor, "\n");

        req.setChatComment(text);
        generateMethod(req);
    }


    private static void createMethod(GenerateCodeReq req) {
        Project project = req.getProject();
        Editor editor = CodeService.getEditor(project);
        CodeService.writeCode2(project, editor, "\n");
        CodeService.generateCodeWithAi2(project, req.getPromptName(), new String[]{req.getMeta()}, (p, code) -> CodeService.writeCode2(p, editor, code));
    }

    /**
     * 创建方法2
     * <p>
     * 会打开两个表单
     * 1.填参数
     * 2.选中类和方法
     *
     * <p>
     * 选择类和方法,然后插入代码
     * <p>
     * 可以用来生成Controller中的方法
     *
     * @param req
     */
    public static void createMethod2(GenerateCodeReq req) {
        String promptName = req.getPromptName();
        Project project = req.getProject();
        PsiMethod pm = CodeService.getMethod(req.getProject());
        //没有选中方法(目前创建方法是根据某个已经选中的方法)
        if (null == pm) {
            HintUtils.show(CodeService.getEditor(req.getProject()), com.xiaomi.youpin.tesla.ip.bo.Message.selectMethodMsg, true);
            return;
        }

        String mn = pm.getName();

        String methodCode = pm.getText();
        PsiClass pc = CodeService.getPsiClass(req.getProject());

        String serviceName = pc.getQualifiedName();
        String shortServiceName = pc.getNameIdentifier().getText();

        //选择类的label
        String cmd = req.getPromptInfo().getLabels().get("cmd");
        String type = req.getPromptInfo().getLabels().get("type");
        String name = req.getPromptInfo().getLabels().get("name");

        //获取需要填写那些参数
        Map<String, Object> map = getParamsFromAiProxy(promptName, req.getPromptInfo().getMeta());

        setLabels(req, map);
        map.put("methodName", mn);
        map.put("serviceName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, shortServiceName));

        String reqPackage = LabelUtils.getLabelValue(req.getProject(), req.getPromptInfo(), Const.REQ_PACKAGE, Const.DEFAULT_REQ_PACKAGE);
        List<String> list = PackageUtils.getClassList(req.getProject(), reqPackage);

        ParamTableDialog table = new ParamTableDialog(ParamDialogReq.builder().title("param").build(), project, map, new HashMap<>(), req.getPromptInfo());
        table.setConsumer(cp -> {
            Map<String, Object> m = table.getValuesMap();
            String reqClass = m.getOrDefault("reqClass", "").toString();

            //用来获取要添加代码的Class和Method
            Module module = req.getModule();
            SelectClassAndMethodDialog dialog = new SelectClassAndMethodDialog(project, DialogReq.builder().cmd(cmd).type(type).name(name).module(module).build());
            dialog.show();
            int exitCode = dialog.getExitCode();
            if (exitCode != DialogWrapper.OK_EXIT_CODE) {
                return;
            }

            DialogResult result = dialog.getResult();
            String methodName = result.getData().get("method");
            String className = result.getData().get("class");


            StringBuilder sb = new StringBuilder();

            //打开PsiClass(需要更新的)
            PsiClass psiClass = CodeService.openJavaClass(project, className);
            Editor editor = CodeService.getEditor(req.getProject());
            //添加import
            addImports(req, project, serviceName, reqClass, editor);
            //添加字段
            addFields(project, serviceName, shortServiceName, psiClass, editor);

            //移动到合理的位置
            if (methodName.equals("New")) {
                EditorUtils.moveToLastMethodEnd(psiClass, editor);
                CodeService.writeCode2(project, editor, "\n");
            } else {
                PsiMethod psiMethod = CodeService.getMethod(psiClass, methodName);
                CodeService.deleteMethod(project, psiMethod);
            }

            sb.append(req.getMeta()).append("\n");
            sb.append(methodCode).append("\n");
            String param = sb.toString();

            Map<String, String> params = Maps.newHashMap();
            m.put("code", param);
            m.put("service", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, shortServiceName));
            m.put("reqClass", JavaClassUtils.getClassName(reqClass));

            //修改Req(添加入字段)
            modifyReq(req, pm, reqClass);

            //interface中添加方法(Dubbo中的interface需要同步更新)
            addMethodToInterface(req, psiClass, pm, PsiClassUtils.findClassByName(req.getProject(), reqClass));

            m.entrySet().forEach(entry -> params.put(entry.getKey(), entry.getValue().toString()));

            //生成方法代码
            CodeService.generateCodeWithAi3(req.getProject(), promptName, new String[]{}, params, (p, code) -> CodeService.writeCode2(p, editor, code));
        });
        table.show();


    }

    public static void addFields(Project project, String serviceName, String shortServiceName, PsiClass psiClass, Editor editor) {
        CodeService.addField(project, editor.getDocument(), psiClass, String.format("@Resource\nprivate %s %s;", shortServiceName, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, shortServiceName)), serviceName);
    }

    public static void addImports(GenerateCodeReq req, Project project, String serviceName, String reqClass, Editor editor) {
        addImports(req, project, serviceName, reqClass, editor, false, false, "");
    }

    public static void addImports(GenerateCodeReq req, Project project, String serviceName, String reqClass, Editor editor, boolean unitTest, boolean resource, String unitVersion) {
        ArrayList<String> importService = Lists.newArrayList();
        importService.add(serviceName);
        if (null != reqClass) {
            importService.add(reqClass);
        }
        importService.addAll(PromptUtils.getImportList(req.getPromptInfo()));
        if (unitTest) {
            importService.addAll(TestUtils.getImportList(unitVersion));
        }
        if (resource) {
            importService.addAll(ProjectUtils.getResourceImport(project));
        }
        CodeService.addImport(project, editor, importService);
    }

    /**
     * 给interface中添加方法(返回值可能被包装要注意)
     *
     * @param psiClass
     */
    private static void addMethodToInterface(GenerateCodeReq req, PsiClass psiClass, PsiMethod pm, PsiClass reqClass) {
        boolean modifyReq = Boolean.valueOf(req.getPromptInfo().getLabels().getOrDefault("add_interface_method", "false"));
        if (!modifyReq) {
            return;
        }
        Safe.run(() -> {
            Project project = req.getProject();
            PsiClass interfacePsiClass = PsiClassUtils.getInterface(psiClass);
            PsiType type = PsiElementFactory.getInstance(project).createType(reqClass);
            PsiType returnType = getReturnType(req, project, pm, interfacePsiClass);
            List<ParamInfo> list = Lists.newArrayList(ParamInfo.builder().name(reqClass.getName()).psiType(type).build());
            WriteCommandAction.runWriteCommandAction(project, () -> PsiClassUtils.addMethod(project, interfacePsiClass, AddMethodConfig.builder().name(pm.getName()).returnType(returnType).isInterface(true).build(), list));
        });
    }


    public static PsiType getReturnType(GenerateCodeReq req, Project project, PsiMethod psiMethod, PsiClass psiClass) {
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        String returnType = req.getPromptInfo().getLabels().getOrDefault("return_type", "");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(returnType)) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiClassUtils.addImport(psiClass, returnType);
            });
            PsiClass resultClass = JavaPsiFacade.getInstance(project).findClass("com.xiaomi.youpin.infra.rpc.Result", GlobalSearchScope.allScope(project));
            @Nullable PsiType rt = psiMethod.getReturnType();
            if (rt instanceof PsiPrimitiveType) {
                PsiPrimitiveType primitiveType = (PsiPrimitiveType) rt;
                rt = primitiveType.getBoxedType(PsiManager.getInstance(project), GlobalSearchScope.allScope(project));
            }
            PsiType[] typeArguments = {rt};
            PsiType psiType = factory.createType(resultClass, typeArguments);
            return psiType;
        } else {
            return psiMethod.getReturnType();
        }
    }


    private static void modifyReq(GenerateCodeReq req, PsiMethod pm, String reqClass) {
        //修改Req里边的参数
        boolean modifyReq = Boolean.valueOf(req.getPromptInfo().getLabels().getOrDefault("modify_req", "false"));
        if (modifyReq) {
            Safe.run(() -> {
                List<ParamInfo> list = PsiMethodUtils.getParamInfoList(pm);
                PsiClass reqPsiClass = PsiClassUtils.findClassByName(req.getProject(), reqClass);
                WriteCommandAction.runWriteCommandAction(req.getProject(), () -> list.stream().forEach(it -> PsiClassUtils.addField(req.getProject(), reqPsiClass, it.getName(), it.getPsiType())));
            });
        }
    }

    private static void setLabels(GenerateCodeReq req, Map<String, Object> map) {
        Map<String, String> labels = req.getPromptInfo().getLabels();
        if (null != labels) {
            labels.entrySet().forEach(entry -> {
                if (map.keySet().contains(entry.getKey())) {
                    map.put(entry.getKey(), entry.getValue());
                }
            });
        }
    }

    //暂时只生成接口引入
    public static void generateMiapiMethod(GenerateCodeReq req) {
        Project project = req.getProject();
        Editor editor = req.getEditor();
        String requirement = getCode(req, new PromptContext()).replace("//", "");
        List<ZKnowledgeRes> list = AthenaCodeService.getCodeList(CodeReq.builder().requirement(requirement).fileTypeList(Arrays.asList("miapi")).build());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(i -> {
                log.info("miapi similarQuery rst:" + i.getContent());
                CodeService.writeCode2(project, editor, "\n");
                CodeService.generateCodeWithAi3(project, req.getPromptName(), new String[]{i.getContent()}, ImmutableMap.of("interfaceInfo", i.getContent()), (p, code) -> CodeService.writeCode2(p, editor, code));
            });
        }
    }

}
