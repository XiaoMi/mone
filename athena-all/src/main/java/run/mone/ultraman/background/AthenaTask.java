package run.mone.ultraman.background;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiChatMessage;
import com.xiaomi.youpin.tesla.ip.bo.robot.ProjectAiMessageManager;
import com.xiaomi.youpin.tesla.ip.bo.robot.Role;
import com.xiaomi.youpin.tesla.ip.bo.z.ZKnowledgeRes;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.Safe;
import com.xiaomi.youpin.tesla.ip.service.CodeService;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import com.xiaomi.youpin.tesla.ip.util.EditorUtils;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.UltramanConsole;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.bo.AthenaMethodInfo;
import run.mone.ultraman.bo.CodeReq;
import run.mone.ultraman.common.CodeUtils;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.service.AthenaCodeService;
import run.mone.ultraman.state.PromptAndFunctionProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 * @date 2023/6/23 08:10
 * 底边栏会显示进度,避免用户不知道当前的ai执行状态
 * 以后再chat 和 editor的都会再这里处理
 */
@Slf4j
public class AthenaTask extends Task.Backgroundable {

    /**
     * 是否是确定的(有时间进度)
     */
    private boolean indeterminate = false;

    private Project project;

    private String promptName;

    private String text;

    @Getter
    private Map<String, String> params = new HashMap<>();

    @Setter
    private boolean format = true;

    @Setter
    private GenerateCodeReq req;

    @Setter
    @Getter
    private PromptContext promptContext;

    private static final String METHOD_CONTEXT = "method_context";

    private static final String MODULE_CONTEXT = "module_context";

    private static final String RESOURCE_CONTEXT = "resource_context";

    private static final String PO_CONTEXT = "po_context";

    public static final String CONTEXT = "context";

    //chat(聊天窗口) editor(ide中)
    @Setter
    private String type = "chat";

    @Setter
    @Getter
    private Runnable initRunnable;


    public AthenaTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, String promptName, String text, Map<String, String> params) {
        super(project, title);
        this.project = project;
        this.promptName = promptName;
        this.text = text;
        this.params = params;
    }

    public AthenaTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title) {
        super(project, title);
    }

    @SneakyThrows
    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        if (null != this.initRunnable) {
            initRunnable.run();
        }

        //参数的一些替换,比如翻译用中文
        extractPrefixedUserLabelsToParams();

        String scope = "";
        if (null != this.promptContext) {
            initContexts();
            scope = this.promptContext.getScope();
        }

        calculateAndOptimizeRequestSize();

        ZAddrRes zAddrRes = Prompt.zAddrRes();
        if (CollectionUtils.isNotEmpty(zAddrRes.getModels())) {
            Long userCredits = 0L;
            // Hint: the first model carries the user credit info
            ModelRes modelRes = zAddrRes.getModels().get(0);
            if (modelRes != null) {
                userCredits = modelRes.getPoints();
            }
            String progressIndicatorText = "Athena Running, User Credits: " + userCredits;
            progressIndicator.setText(progressIndicatorText);
            progressIndicator.setText2(progressIndicatorText);
        }

        progressIndicator.setIndeterminate(this.indeterminate);
        progressIndicator.setFraction(0.0);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        UltramanConsole.append(project, "\ncall prompt begin:" + promptName + " scope:" + scope);
        AiCode aiCode = getAiCode(progressIndicator, countDownLatch);

        Stopwatch sw = Stopwatch.createStarted();
        String text = "";
        if (!LabelUtils.isOpen(this.project, req.getPromptInfo(), "call_json")) {
            //一点一点的生成代码
            CodeService.generateCodeWithAi5(GenerateCodeReq.builder().format(format).promptName(promptName).project(project).build(), project, promptName, new String[]{text}, params, (p, code) -> {
            }, aiCode);
            //最多等3分钟
            countDownLatch.await(5, TimeUnit.MINUTES);
            text = aiCode.getMarkDownText();
        } else {
            //直接调用返回json的接口(目前代码补全会走到这里)
            JsonObject obj = PromptAndFunctionProcessor.callPrompt(this.project, req.getPromptInfo(), convertMap(params));
            log.info("{}", obj);
            text = obj.get("code").getAsString();
            int offset = EditorUtils.moveCursorToMethodEndIfOutside(req.getEditor(), req.getPsiMethod(), req.getOffset());
            CodeService.insertCode(this.project, text, false, offset, req.getEditor());
        }
        // 上报生成信息
        String finalText = text;
        UltramanConsole.append(project, "call prompt finish:" + promptName + " use time:" + sw.elapsed(TimeUnit.SECONDS) + "s\n");
        ProjectAiMessageManager.getInstance().appendMsg(project, AiChatMessage.builder().id(aiCode.getMessageId()).role(Role.assistant).message(text).data(text).build());

        CodeUtils.uploadCodeGenInfo(getInfo(finalText));
    }

    private M78CodeGenerationInfo getInfo(String code){
        M78CodeGenerationInfo info = new M78CodeGenerationInfo();
        info.setCodeLinesCount(CodeUtils.getLineCnt(code, false));
        info.setAnnotation(CodeUtils.checkLineStartsWithComment(this.req.getChatComment()));
        info.setProjectName(this.project.getName());
        info.setClassName(this.req.getClassName());
        info.setMethodName("GENERATED");
        return info;
    }

    public void initContexts() {
        Safe.run(() -> callResourceContext());
        Safe.run(() -> callPoContext());
        Safe.run(() -> callMethodContext());
        Safe.run(() -> callModuleContext());
    }

    private void extractPrefixedUserLabelsToParams() {
        Map<String, String> userLabels = req.getPromptInfo().getLabels();
        if (MapUtils.isNotEmpty(userLabels)) {
            userLabels.entrySet().stream().forEach(it -> {
                if (it.getKey().startsWith("__")) {
                    this.params.put(it.getKey(), it.getValue());
                }
            });
        }
    }

    //把一个Map<String,String>转换为Map<String,Object>(method)
    public Map<String, Object> convertMap(Map<String, String> stringMap) {
        return stringMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Object) e.getValue()));
    }


    /**
     * 无论如何都会在chat里显示信息
     *
     * @param progressIndicator
     * @param countDownLatch
     * @return
     */
    @NotNull
    protected AiCode getAiCode(@NotNull ProgressIndicator progressIndicator, CountDownLatch countDownLatch) {
        //方法注释
        if (type.equals("method_comment")) {
            return new AiCode(progressIndicator, countDownLatch, this.project, message -> {
                ImportCode importCode = new ImportCode();
                importCode.setProject(project);
                importCode.setEditor(promptContext.getEditor());

                if (message.getType().equals(AiMessageType.begin)) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        CodeService.moveToMethodAndInsertLine(project);
                    });
                }

                if (message.getType().equals(AiMessageType.process)) {
                    importCode.append(message.getText());
                }
            });
        }
        //在编辑器添加代码(添加到ide中)
        else if (type.equals("edit")) {
            ImportCode importCode = new ImportCode();
            importCode.setProject(project);
            importCode.setEditor(promptContext.getEditor());
            return new AiCode(progressIndicator, countDownLatch, this.project, message -> {
                //开始(调整光标位置)
                if (message.getType().equals(AiMessageType.begin)) {
                    String comment = params.get("code");
                    ProjectAiMessageManager.getInstance().appendMsg(project, AiChatMessage.builder().role(Role.user).message(comment).data(comment).build());

                    ApplicationManager.getApplication().invokeLater(() -> {
                        CodeService.moveCaretToEndOfLine(this.promptContext.getEditor());
                        CodeService.writeCode2(project, this.promptContext.getEditor(), "\n");
                    });
                }
                //处理中(添加内容到ide)
                if (message.getType().equals(AiMessageType.process)) {
                    importCode.append(message.getText());
                }
            });
        } else {
            return new AiCode(progressIndicator, countDownLatch, this.project);
        }
    }

    //计算请求的size,有写大小需要优化(最终大小需要低于3500)
    public void calculateAndOptimizeRequestSize() {
        String resourceContext = this.params.getOrDefault(RESOURCE_CONTEXT, "");
        String poContext = this.params.getOrDefault(PO_CONTEXT, "");
        String moduleContext = this.params.getOrDefault(MODULE_CONTEXT, "");
        String methodContext = this.params.getOrDefault(METHOD_CONTEXT, "");
        //prompt的长度
        int promptSize = 1460;

        int i = resourceContext.length() + methodContext.length() + moduleContext.length() + promptSize;
        NotificationCenter.notice(this.project, String.format("Athena Task call size resource:%s class:%s module:%s prompt len:%s total:%s",
                resourceContext.length(), methodContext.length(), moduleContext.length(), promptSize, i
        ), true);

        if (i > AthenaContext.ins().getMaxTokenNum()) {
            moduleContext = "";
        }

        //拼接处完整的上下文
        this.params.put(CONTEXT, Joiner.on("\n\n").join(resourceContext, poContext, moduleContext));

        if (this.params.get(CONTEXT).length() > 30) {
            UltramanConsole.append(this.project, "context:\n" + this.params.get(CONTEXT));
        }
    }


    public String getContext() {
        return this.params.getOrDefault(CONTEXT, "");
    }


    private static Function<String, String> f = str -> {
        List<String> l = Splitter.on(".").splitToList(str);
        if (l.size() > 0) {
            return l.get(l.size() - 1);
        }
        return str;
    };

    private void callModuleContext() {
        if ((null != this.promptContext.getScope()) && (this.promptContext.getScope().equals("module") || this.promptContext.getScope().equals("project"))) {
            String moduleName = "";
            if (this.promptContext.getScope().equals("module")) {
                moduleName = this.promptContext.getModule();
            }
            //计算module中的context
            List<ZKnowledgeRes> list = AthenaCodeService.getCodeList(CodeReq.builder().projectName(this.promptContext.getProject()).moduleName(moduleName).requirement(promptContext.getComment()).build());

            if (null != this.promptContext.getResourceBeanList() && (this.promptContext.getResourceBeanList().size() > 0) && (null != list && list.size() > 0)) {
                list = list.stream().filter(it -> {
                    MutableBoolean mb = new MutableBoolean(true);
                    Safe.run(() -> {
                        AthenaClassInfo ci = AthenaCodeService.classInfo(it.getContent());
                        boolean v = promptContext.getResourceBeanList().stream().map(f).collect(Collectors.toList()).contains(ci.getName())
                                ||
                                (Sets.intersection(Sets.newHashSet(promptContext.getResourceBeanList().stream().map(f).collect(Collectors.toList())), Sets.newHashSet(ci.getInterfaceList())).size() > 0);
                        mb.setValue(!v);
                    });
                    return (boolean) mb.getValue();
                }).collect(Collectors.toList());
            }
            //如果codeserver 有相关代码,则相信code server 中的代码(不然数量太大,很难处理)
            String moduleContext = "";
            if (null != list && list.size() > 0) {
                int limitNum = AthenaContext.ins().gptModel().getModuleClassNum();
                moduleContext = list.stream().limit(limitNum).map(it -> it.getContent()).collect(Collectors.joining("\n"));
                UltramanConsole.append(this.project, "\nmodule context:" + moduleContext + "\n");
            }
            this.params.put(MODULE_CONTEXT, moduleContext);
        }
    }

    //引入一些class内部的方法(能起到一定的教学作用)
    private void callMethodContext() {
        //计算引入哪些方法(会调用chatgpt)
        if (null != this.promptContext.getMethodCodeList()) {
            List<AthenaMethodInfo> list = this.promptContext.getMethodCodeList();
            int methodLimitNum = 2;
            if (!AthenaContext.ins().gptModel().isOptimizeTokens()) {
                methodLimitNum = 10;
                list.stream().forEach(it -> it.setScore(100));
            } else {
                list.stream().forEach(it -> {
                    try {
                        Map<String, String> map = new HashMap<>();
                        map.put("method", it.getCode());
                        map.put("requirement", promptContext.getComment());
                        //这里有个问题,调用次数过多,会超过某些module的限制
                        int value = Integer.valueOf(AthenaCodeService.callProxy(this.project, map, "requirement", 5).value());
                        it.setScore(value);
                        UltramanConsole.append(this.project, "method score:" + value + "\n" + it.getCode() + "\n\n");
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                });
            }

            String fieldCode = this.promptContext.getFieldCodeList().stream().map(it -> it.getCode() + "\n").collect(Collectors.joining());
            List<AthenaMethodInfo> newList = list.stream().sorted((a, b) -> b.getScore() - a.getScore()).filter(it -> it.getScore() > 30).collect(Collectors.toList());
            if (newList.size() == 0) {
                newList = list.stream().sorted((a, b) -> b.getScore() - a.getScore()).limit(1).collect(Collectors.toList());
            } else {
                newList = list.stream().limit(methodLimitNum).collect(Collectors.toList());
            }
            String classCode = newList.stream().map(it -> it.getCode()).collect(Collectors.joining("\n\n"));
            classCode = "public class " + promptContext.getClazzName() + "{\n" + fieldCode + classCode + "\n}";
            UltramanConsole.append(this.project, "class:\n" + classCode + "\n" + "size:" + classCode.length() + "\n\n");
            this.params.put("class", classCode);
            this.params.put(METHOD_CONTEXT, classCode);
        }
    }

    private void callResourceContext() {
        //计算引入哪些资源(会调用chatgpt)
        if (null != this.promptContext.getResourceCode()) {
            //获取@Resource引入的代码
            String resource = PromptService.getResourceFromAi(this.project, this.params, this.promptContext, this.promptContext.getResourceCode());
            this.params.put(RESOURCE_CONTEXT, resource);
            UltramanConsole.append(project, "resource:\n" + resource + "\n");
        }
    }

    //计算po的上下文引入
    private void callPoContext() {
        if (null != this.promptContext.getPoClassInfos()) {
            this.params.put(PO_CONTEXT, Joiner.on("\n").join(this.promptContext.getPoClassInfos().stream().map(it -> it.getCode()).collect(Collectors.toList())));
            UltramanConsole.append(project, "po:\n" + this.params.get(PO_CONTEXT) + "\n");
        }
    }


    //启动任务
    public static void start(Task task) {
        ProgressManager.getInstance().run(task);
    }
}
