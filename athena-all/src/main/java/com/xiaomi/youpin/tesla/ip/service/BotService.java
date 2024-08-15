package com.xiaomi.youpin.tesla.ip.service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.common.*;
import com.xiaomi.youpin.tesla.ip.service.consumer.BotMsgConsumer;
import com.xiaomi.youpin.tesla.ip.util.ActionUtils;
import com.xiaomi.youpin.tesla.ip.util.GitUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import com.xiaomi.youpin.tesla.ip.util.TerminalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.bo.Version;
import run.mone.ultraman.common.CodeUtils;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.common.ImportCode;
import run.mone.ultraman.common.TestRunnerUtils;
import run.mone.ultraman.http.HttpClient;
import run.mone.ultraman.http.WsClient;
import run.mone.ultraman.listener.event.TaskEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/6/5 23:03
 * 目前是Athena最核心的逻辑
 * 主要用来调用bot(调用m78平台)
 * 分为同步调用和异步调用
 */
@Slf4j
public class BotService {

    private static final String BOT_URL = "http://localhost/open-apis/ai-plugin-new/feature/router/probot/query";

    //执行bot(支持流式调用,和直接json返回)
    public static void executeBot(GenerateCodeReq req) {
        PromptInfo promptInfo = req.getPromptInfo();
        if (null != promptInfo) {
            Map<String, String> labels = promptInfo.getLabels();
            if (isLabeldWithSteam(labels)) {
                //调用流试服务(实现打字机效果),比如代码生成
                streamBotCall(req, labels);
            } else {
                //直接调用bot,返回json,比如:给方法从命名,一键提交代码
                directBotCall(req, labels);
            }
            // 执行bot之后上传插件统计信息。这块只统计非生成代码的行为。生成代码会在BotMsgConsumer中进行统计
            Action action = ActionUtils.getActionByReq(req);
            if (Action.GENERATE_CODE != action) {
                CodeUtils.uploadCodeGenInfo(action.getCode(),
                        req.getProjectName(),
                        req.getClassName(),
                        req.getPsiMethod() == null ? "" : req.getPsiMethod().getName());
            }
        }
    }

    private static boolean isLabeldWithSteam(Map<String, String> labels) {
        return labels.getOrDefault("stream", "false").equals("true");
    }

    private static void directBotCall(GenerateCodeReq req, Map<String, String> labels) {
        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty(Const.BOT_ID, labels.get("botId"));

        //如果是private_prompt = true 的bot,需要注意传一个params参数,这个参数实际是一个Map<String, String>结构

        //git push
        if (handleGitPushInfo(req, jsonReq)) return;

        //修改方法名
        handleModifyMethodName(req, jsonReq);

        //bug fix(问题修复)
        if (handleBugFix(req, labels, jsonReq)) return;

        handleGenerateCodeReqAndBotRes(req, jsonReq);
    }

    private static void handleGenerateCodeReqAndBotRes(GenerateCodeReq req, JsonObject jsonReq) {
        String botRes = null;
        try {
            botRes = callBot(req.getProject(), jsonReq);
        } catch (Throwable e) {
            log.warn("callBot error:{}", e.getMessage());
            return;
        }
        //处理结果
        handleGenerateCodeReqAndBotRes(req, botRes);
    }

    private static boolean handleBugFix(GenerateCodeReq req, Map<String, String> labels, JsonObject jsonReq) {
        //bug fix(问题修复)
        if (Action.BUG_FIX == ActionUtils.getActionByReq(req)) {
            ApplicationManager.getApplication().invokeLater(() -> {
                TestRunnerUtils.runTest(req.getProject(), req.getPsiMethod().getName(), pair -> {
                    //查问题需要行号
                    Map<String, String> params = ImmutableMap.of("method", pair.getKey(), "error", pair.getValue());
                    jsonReq.add("params", GsonUtils.gson.toJsonTree(params));
                    jsonReq.addProperty("input", "");
                    //处理结果
                    handleGenerateCodeReqAndBotRes(req, jsonReq);
                });
            });
            return true;
        }
        return false;
    }

    private static void handleModifyMethodName(GenerateCodeReq req, JsonObject jsonReq) {
        if (Action.SMART_NAMING == ActionUtils.getActionByReq(req)) {
            Map<String, String> params = ImmutableMap.of("code", req.getMethodCode());
            jsonReq.add("params", GsonUtils.gson.toJsonTree(params));
            jsonReq.addProperty("input", "");
        }
    }

    private static boolean handleGitPushInfo(GenerateCodeReq req, JsonObject jsonReq) {
        if (Action.GIT_PUSH == ActionUtils.getActionByReq(req)) {
            List<String> list = GitUtils.getAffectedFileNames(req.getProject());
            String commitDiff = GitUtils.getCommitDiff(req.getProject());
            if (commitDiff.length() > 10000) {
                // 太长了，截断下
                commitDiff = commitDiff.substring(0, 5000);
            }
            String currentBranch = GitUtils.getCurrentBranch(req.getProject());
            if (list.isEmpty()) {
                log.info("getAffectedFileNames num:0");
                return true;
            }
            JsonObject input = new JsonObject();
            input.add("changedFiles", GsonUtils.gson.toJsonTree(list));
            input.addProperty("diff", commitDiff);
            input.addProperty("branch", currentBranch);
            jsonReq.addProperty("input", input.toString());
        }
        return false;
    }

    private static void handleGenerateCodeReqAndBotRes(GenerateCodeReq req, String botRes) {
        switch (ActionUtils.getActionByReq(req)) {
            //操作terminal
            case GIT_PUSH:
                TerminalUtils.executeTerminalCommand(req.getProject(), botRes);
                break;
            //给方法重命名
            case SMART_NAMING:
                PsiMethodUtils.renameMethod(req.getProject(), req.getPsiMethod(), botRes);
                break;
            //bug fix
            case BUG_FIX:
                log.info("fix method:{}", botRes);
                //替换这个方法的内容
                PsiMethodUtils.replacePsiMethod(req.getProject(), req.getPsiMethod(), botRes);
                break;
        }
    }

    //这里会调用到m78:WebSocketHandler
    private static void streamBotCall(GenerateCodeReq req, Map<String, String> labels) {
        String out = req.getPromptInfo().getLabels().getOrDefault("bot_out", "athena");
        WsClient wsClient = new WsClient();
        if (!Objects.isNull(req.getCountDownLatch())) {
            wsClient.setLatch(req.getCountDownLatch());
        }
        //for test
//                wsClient.setUrl("ws://127.0.0.1:8077/ws/bot/abc");
        wsClient.setProjectName(req.getProject().getName());
        String id = UUID.randomUUID().toString();
        wsClient.setId(id);

        ImportCode importCode = new ImportCode();
        importCode.setProject(req.getProject());
        importCode.setEditor(req.getEditor());

        Stopwatch sw = Stopwatch.createStarted();
        wsClient.init(getAiMessageConsumer(req, out, importCode, sw));
        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty(Const.BOT_ID, labels.get("botId"));
        //ztoken(可以用来换取用户名)
        jsonReq.addProperty(Const.TOKEN, ConfigUtils.getConfig().getzToken());

        String input = "";
        switch (ActionUtils.getActionByReq(req)) {
            case GENERATE_CODE:
                //通过注释生成代码
                if (null != req.getParam().get(Const.GENERATE_CODE_COMMENT)) {
                    input = req.getParam().get(Const.GENERATE_CODE_COMMENT);
                } else {
                    input = req.getCurrentLine();
                }

                if (req.getParam().containsKey("__code")) {
                    input = req.getParam().get("__code");
                }
                //生成代码的时候有许多参数和上下文,靠这里拿到这些参数列表
                JsonObject params = generateParams(req, input);
                input = "";
                jsonReq.add("params", params);
                break;
            case CHAT:
                // 聊天
                input = req.getParam().get("chatContent");
                break;
            case UNIT_TEST:
                // 单元测试
                jsonReq.add("params", generateUnitTestParams(req));
                break;
            case GENERATE_COMMENT:
            case CODE_SUGGESTION:
                //给代码添加注释, 代码建议
                if (StringUtils.isNotEmpty(req.getMethodCode())) {
                    input = req.getMethodCode();
                } else {
                    wsClient.getWs().close(1000, null);
                    return;
                }
                break;
        }
        jsonReq.addProperty("input", input);
        jsonReq.addProperty("topicId", id);
        // 添加version参数
        addVersionParam(jsonReq);
        // 添加用户自定义model
        setModel(req, jsonReq);

        wsClient.send(jsonReq);
    }

    @NotNull
    public static JsonObject generateParams(GenerateCodeReq req, String input) {
        JsonObject params = new JsonObject();
        params.addProperty("code", input);
        params.addProperty("class", req.getClassCode());

        if (null == req.getInheritedMethods()) {
//            NotificationCenter.notice(ErrorMessage.ERR_NOT_IN_CLASS_SCOPE, NotificationType.ERROR);
//            throw new RuntimeException(ErrorMessage.ERR_NOT_IN_CLASS_SCOPE);
            log.error(ErrorMessage.ERR_NOT_IN_CLASS_SCOPE);
        } else {
            params.addProperty("inheritedMethods", req.getInheritedMethods().stream().collect(Collectors.joining("\n\n\n")));
        }
        params.addProperty("fqcn", req.getQualifiedName());
        //计算上下文(context)
        updateParamsWithContext(req, params);
        return params;
    }

    @NotNull
    public static JsonObject generateUnitTestParams(GenerateCodeReq req) {
        JsonObject params = new JsonObject();
        params.addProperty("code", req.getParam().get("code"));
        params.addProperty("testClassCode", req.getParam().get("testClassCode"));
        return params;
    }

    private static void updateParamsWithContext(GenerateCodeReq req, JsonObject params) {
        Safe.run(() -> {
            String context = getContextFromGenerateCodeReq(req);
            params.addProperty("context", context);
        });
    }

    //获取计算
    private static String getContextFromGenerateCodeReq(GenerateCodeReq req) {
        AthenaTask athenaTask = PromptService.generateMethod(req, false);
        athenaTask.getInitRunnable().run();
        athenaTask.initContexts();
        athenaTask.calculateAndOptimizeRequestSize();
        String context = athenaTask.getContext();
        return context;
    }


    @NotNull
    private static Consumer<AiMessage> getAiMessageConsumer(GenerateCodeReq req, String out, ImportCode importCode, Stopwatch sw) {
        return new BotMsgConsumer(req, out, importCode, sw);
    }


    //直接调用bot,然后返回json,解析json结果
    public static String callBot(Project project, JsonObject jsonReq) {
        String userName = ConfigUtils.getConfig().getNickName();
        jsonReq.addProperty("userName", userName);
        jsonReq.addProperty("projectName", project.getName());
        jsonReq.addProperty(Const.TOKEN, ConfigUtils.getConfig().getzToken());
        // 添加version参数
        addVersionParam(jsonReq);
        String param = jsonReq.toString();

        //看起来很麻烦,是因为这个不一定在那个线程里,只能通过这种方式稳定的调用
        ApplicationManager.getApplication().getMessageBus().syncPublisher(TaskEvent.TOPIC).onEvent(TaskEvent.builder().message("begin").build());
        Stopwatch sw = Stopwatch.createStarted();
        String resStr = "";
        try {
            String botUrl = ResourceUtils.getAthenaConfig().getOrDefault(Const.BOT_URL, "");
            if (StringUtils.isEmpty(botUrl)) {
                botUrl = BOT_URL;
            }
            //for test
//            http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query
            resStr = HttpClient.callHttpServer(botUrl, "callBot", param, false, false, 20);
        } finally {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(TaskEvent.TOPIC).onEvent(TaskEvent.builder().message("end").time(sw.elapsed(TimeUnit.SECONDS)).build());
        }

        log.info("call bot res:{} param:{}", resStr, param);
        JsonObject resObj = GsonUtils.gson.fromJson(resStr, JsonObject.class);

        //处理结果中的错误
        handleResponse(resObj);

        String commond = JsonParser.parseString(resObj.get("data").getAsString()).getAsJsonObject().get("result").getAsJsonObject().get("data").getAsString();
        return commond;
    }

    private static void handleResponse(JsonObject resObj) {
        if (resObj.get("code").getAsInt() != 0) {
            NotificationCenter.notice(resObj.get("message").getAsString(), NotificationType.ERROR);
            throw new RuntimeException("callHttpServer error:" + resObj);
        }
    }

    // 在给定的JsonObject中添加version参数(class)
    private static void addVersionParam(JsonObject resObj) {
        Version version = new Version();
        resObj.addProperty(Const.CLIENT_VERSION, version.getVersion());
        resObj.addProperty(Const.CLIENT_NAME, version.getName());
    }

    private static void setModel(GenerateCodeReq req, JsonObject jsonReq) {
        Action action = ActionUtils.getActionByReq(req);
        String model = action == Action.CHAT ? AthenaContext.ins().getGptModel() : AthenaContext.ins().getNoChatModel();
        if (!Const.USE_BOT_MODEL.equals(model)) {
            jsonReq.addProperty(Const.AI_MODEL_PARAM_KEY, model);
        }
    }


}
