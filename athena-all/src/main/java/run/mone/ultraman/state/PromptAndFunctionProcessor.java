package run.mone.ultraman.state;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.bo.GenerateCodeReq;
import run.mone.m78.ip.bo.PromptContext;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.bo.chatgpt.Completions;
import run.mone.m78.ip.bo.chatgpt.Format;
import run.mone.m78.ip.bo.chatgpt.Message;
import run.mone.m78.ip.bo.robot.*;
import run.mone.m78.ip.common.ChromeUtils;
import run.mone.m78.ip.common.Const;
import run.mone.m78.ip.common.Prompt;
import run.mone.m78.ip.service.AiService;
import run.mone.m78.ip.service.PromptService;
import run.mone.m78.ip.service.RobotService;
import run.mone.m78.ip.util.ResourceUtils;
import run.mone.m78.ip.util.UltramanConsole;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.AthenaContext;
import run.mone.ultraman.common.FunctionReqUtils;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.common.TemplateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/4 17:53
 * <p>
 * 长对话调用function和prompt都从这里调出去
 */
@Slf4j
public class PromptAndFunctionProcessor {


    //直接调用某个prompt(这个prompt 必须是返回json的那种prompt)
    public static JsonObject callPrompt(Project project, PromptInfo promptInfo, Map<String, Object> map) {
        String prompt = promptInfo.getData();
        List<AiChatMessage<?>> list = new ArrayList<>();
        boolean vip = false;

        Message.MessageBuilder messageBuilder = Message.builder();

        if (!"no permission".equals(prompt.trim()) && false) {
            prompt = TemplateUtils.renderTemplate(prompt, map);
            list.add(AiChatMessage.builder().data(prompt).role(Role.user).build());
            vip = true;
        } else {
            //需要远程拼凑出prompt
            list.add(AiChatMessage.builder().data("").role(Role.user).build());
            Map<String, String> params = getParamMap(map);
            messageBuilder.promptName(promptInfo.getPromptName()).params(params);
        }

        List<Message> messageList = list.stream().map(it -> messageBuilder
                .content(it.toString())
                .role(it.getRole().name())
                .build()).collect(Collectors.toList());

        log.info("vip:{} prompt:{}", vip, prompt);

        Completions completions = Completions.builder().stream(false).response_format(Format.builder().build()).messages(messageList).build();
        UltramanConsole.append(project, "call ai prompt:" + promptInfo.getPromptName());
        Stopwatch swCallAi = Stopwatch.createStarted();
        JsonObject jsonObj = AiService.call(GsonUtils.gson.toJson(completions), Long.parseLong(promptInfo.getLabels().getOrDefault("timeout", "50000")), vip);
        UltramanConsole.append(project, "call ai prompt:" + promptInfo.getPromptName() + " finish res:" + jsonObj + " use time:" + swCallAi.elapsed(TimeUnit.SECONDS) + "s");
        return jsonObj;
    }

    @NotNull
    private static Map<String, String> getParamMap(Map<String, Object> map) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.valueOf(e.getValue())
                ));
    }


    /**
     * 处理prompt,后边可能挂一个function
     * <p>
     * 模式都是用prompt完成推理+参数抽取->调用function
     *
     * @param promptName
     * @param context
     */
    public static void prompt(String promptName, StateContext context) {
        List<AiChatMessage<?>> list = ProjectAiMessageManager.getInstance().getMessageList(context.getProject());
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);

        //需要获取一些code信息
        if (promptInfo.open("_code")) {
            Project project = AthenaContext.ins().getProjectMap().get(context.getProject());
            GenerateCodeReq req = GenerateCodeReq.builder().project(project).promptInfo(promptInfo).build();
            PromptService.setReq(req);
            String code = PromptService.getCode(req, new PromptContext());
            context.getMemary().put("_code", code);
        }

        String prompt = promptInfo.getData();
        prompt = TemplateUtils.renderTemplate(prompt, context.getMemary());
        list.add(AiChatMessage.builder().data(prompt).role(Role.user).build());
        //这里的it.toString 就是message to string 的过程
        List<Message> messageList = list.stream().map(it -> Message.builder().content(it.toString()).role(it.getRole().name()).build()).collect(Collectors.toList());
        //让返回的结果是json
        Completions completions = Completions.builder().stream(false).response_format(Format.builder().build()).messages(messageList).build();
        UltramanConsole.append(context.getProject(), "call ai prompt:" + promptName);
        Stopwatch swCallAi = Stopwatch.createStarted();
        JsonObject jsonObj = AiService.call(GsonUtils.gson.toJson(completions), Long.parseLong(promptInfo.getLabels().getOrDefault("timeout", "50000")), true);
        UltramanConsole.append(context.getProject(), "call ai prompt:" + promptName + " finish res:" + jsonObj + " use time:" + swCallAi.elapsed(TimeUnit.SECONDS) + "s");
        log.info("promptName:{} res:{}", promptName, jsonObj);
        if (!jsonObj.has("action")) {
            jsonObj.addProperty("action", ActionType.print.name());
        }

        //展示一些debug信息到前台
        showDebugInfo(context, jsonObj.toString());

        String action = jsonObj.get("action").getAsString();

        //直接退出了
        if (action.equals(ActionType.exit.name())) {
            log.info("action exit");
            ProjectFsmManager.stop(context.getProject());
            ChromeUtils.call(context.getProject(), "stop", 0);
            return;
        }

        //跳过这个function
        if (action.equals(ActionType.skip.name())) {
            log.info("action skip");
            ChromeUtils.call(context.getProject(), "skip", 0);
            return;
        }

        //记忆结果(先放入记忆)
        memaryResult(context, jsonObj);

        if (action.equals(ActionType.memary.name())) {
            //完成记忆
        } else if (action.equals(ActionType.print.name())) {
            //输出到前台
            ChromeUtils.call(context.getProject(), jsonObj.toString(), 0);
            ProjectAiMessageManager.getInstance().addMessage(context.getProject(), AiChatMessage.builder().role(Role.assistant).type(MessageType.string).data(jsonObj.toString()).build());
        } else if (action.equals(ActionType.method.name())) {
            //执行一个方法
            String methodName = jsonObj.get("methodName").getAsString();
            //记忆中的内容也会放入请求map
            Map<String, Object> map = FunctionReqUtils.getCallScriptMap(context.getProject(), context.getMemary(), context.getPromptLables());
            Object r = null;
            Stopwatch sw = Stopwatch.createStarted();
            try {
                UltramanConsole.append(context.getProject(), "call ai funtion:" + methodName);
                //根据prompt需要调整一些调用function的参数(会改变请求参数)
                changeFunctionParam(jsonObj, map, promptInfo);
                r = RobotService.runScriptMethod(map, methodName);
                log.info("call function finish result:{}", r);
                if (r instanceof AiChatMessage<?> am) {
                    //table中填的map直接当记忆使用
                    if (am.getType().equals(MessageType.map)) {
                        MapData mapData = (MapData) am.getData();
                        if (!mapData.getMap().isEmpty()) {
                            mapData.getMap().entrySet().stream().forEach((entry) -> {
                                String key = entry.getKey();
                                if (mapData.getMemaryMap().containsKey(key)) {
                                    context.getMemary().put(mapData.getMemaryMap().get(key).toString(), entry.getValue());
                                } else {
                                    context.getMemary().put(key, entry.getValue());
                                }
                            });
                        }
                    }
                    context.getMemary().putAll(am.getMeta());
                    ProjectAiMessageManager.getInstance().addMessage(context.getProject(), AiChatMessage.builder().role(Role.assistant).type(am.getType()).data(am.getData()).build());
                    ChromeUtils.call(context.getProject(), am);
                }
            } catch (Throwable ex) {
                log.error("call function error:" + ex.getMessage(), ex);
                ChromeUtils.call(context.getProject(), "error:" + ex.getMessage(), 0);
            } finally {
                UltramanConsole.append(context.getProject(), "call ai function:" + methodName + " finish res:" + r + " use time:" + sw.elapsed(TimeUnit.SECONDS) + "s");
            }
        }
    }

    //label 的优先级比较高,会直接替换掉map中的值(调用function的参数)
    private static void changeFunctionParam(JsonObject promptRes, Map<String, Object> map, PromptInfo promptInfo) {
        Map<String, String> lables = promptInfo.getLabels();
        if (null != lables) {
            lables.entrySet().stream().forEach(entry -> {
                String key = entry.getKey();
                if (key.startsWith("#") && !entry.getValue().equals("null")) {
                    String newKey = key.substring(1);
                    if (map.containsKey(newKey) && promptRes.has(newKey)) {
                        log.info("change call function param key:{} value:{} oldValue:{}", newKey, entry.getValue(), map.get(newKey));
                        map.put(newKey, entry.getValue());
                    }
                }
            });
        }
    }

    private static void memaryResult(StateContext context, JsonObject jsonObj) {
        jsonObj.keySet().forEach(key -> {
            if (jsonObj.get(key).isJsonPrimitive()) {
                context.getMemary().put(key, jsonObj.get(key).getAsString());
            } else {
                context.getMemary().put(key, jsonObj.get(key));
            }
        });
    }

    private static void showDebugInfo(StateContext context, String jsonObj) {
        if (ResourceUtils.getAthenaConfig().getOrDefault(Const.DEBUG, "false").equals("true")) {
            ChromeUtils.call(context.getProject(), "debug: res:" + jsonObj + " memary:" + context.getMemary().toString(), 0);
        }
    }


    public static void funciton(StateContext context, String prompt) {
        //单纯调用function(会调用bot prompt 获取functio)
        String project = context.getProject();
        String cmd = "";
        //直接调用方法
        //知道具体的命令
        if (prompt.startsWith("+")) {
            cmd = prompt.substring(1);
        } else {
            //调用一次ai获取命令
            cmd = RobotService.getPromptCmd(prompt);
        }

        Map<String, Object> map = FunctionReqUtils.getCallScriptMap(project, context.getMemary(), context.getPromptLables());
        log.info("call function:{} params:{}", cmd, map);

        Object r = RobotService.runScript(map, cmd);

        showDebugInfo(context, GsonUtils.gson.toJson(r));

        if (r instanceof AiChatMessage<?> am) {
            //这个计算结果其实是通过函数来计算的
            String id = UUID.randomUUID().toString();
            AiChatMessage aiChatMessage = AiChatMessage.builder().id(id).role(Role.assistant).type(am.getType()).data(am.getData()).build();
            if (null != am.getMeta()) {
                aiChatMessage.getMeta().putAll(am.getMeta());
            }

            //放入记忆体
            context.getMemary().putAll(am.getMeta());

            //添加到自己记录的信息管理器中
            ProjectAiMessageManager.getInstance().addMessage(project, aiChatMessage);
            am.setId(id);
            //发送到前台
            ChromeUtils.call(project, am);
        }
    }

}
