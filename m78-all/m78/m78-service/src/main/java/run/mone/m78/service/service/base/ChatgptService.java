package run.mone.m78.service.service.base;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.multiModal.audio.AudioParam;
import run.mone.m78.service.bo.AiProxyMessage;
import run.mone.m78.service.bo.chatgpt.*;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.dao.entity.M78UserCostTokenDetail;
import run.mone.m78.service.prompt.Prompt;
import run.mone.m78.service.service.token.M78UserCostTokenDetailService;
import run.mone.openai.OpenaiCall;
import run.mone.openai.ReqConfig;
import run.mone.openai.StreamListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;

/**
 * @author goodjava@qq.com
 * @date 2024/1/9 17:07
 */
@Service
@Slf4j
public class ChatgptService {

    @Resource
    private M78UserCostTokenDetailService costTokenService;

    /**
     * 初始化方法，在对象创建后自动调用。
     * <p>
     * 该方法执行以下操作：
     * 1. 记录初始化日志。
     * 2. 调用Prompt类的初始化方法。
     * 3. 创建一个定时任务，每隔60秒调用一次Prompt.flush()方法，首次调用延迟5秒。
     */
    @PostConstruct
    public void init() {
        log.info("init");
        Prompt.init();

        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(() -> SafeRun.run(() -> Prompt.flush()), 5, 60, TimeUnit.SECONDS);
    }

    /**
     * 调用带有默认JsonObject参数的call3方法
     *
     * @param promptName  提示名称
     * @param params      参数映射
     * @param keys        参数键列表
     * @param model       模型名称
     * @param temperature 温度参数
     * @return 返回包含字符串结果的Result对象
     */
    public Result<String> call3(String promptName, Map<String, String> params, List<String> keys, String model, String temperature) {
        return call3(promptName, params, keys, model, temperature, new JsonObject());
    }

    public Result<String> call3(String promptName, Map<String, String> params, List<String> keys, String model, String temperature, JsonObject req) {
        log.info("ChatgptService.call, promptName:{}, params:{}, keys:{}", promptName, params, keys);
        Stopwatch sw = Stopwatch.createStarted();
        try {
            JsonObject jsonObj = callAiProxy(promptName, params, model, temperature, req);
            log.info("call prompt:{} res:{}", promptName, jsonObj);
            return Result.success(jsonObj.toString());
        } finally {
            log.info("call prompt:{} use time:{} s", promptName, sw.elapsed(TimeUnit.SECONDS));
        }
    }

    /**
     * 调用ChatGPT服务并返回指定键的结果
     *
     * @param promptName 提示名称
     * @param params     参数映射
     * @param keys       需要提取的键列表
     * @param model      模型名称
     * @return 包含指定键及其对应值的结果
     */
    public Result<Map<String, String>> call(String promptName, Map<String, String> params, List<String> keys, String model) {
        log.info("ChatgptService.call, promptName:{}, params:{}, keys:{}, model:{}", promptName, params, keys, model);
        Stopwatch sw = Stopwatch.createStarted();
        try {
            JsonObject jsonObj = call0(promptName, params, model);
            log.info("call prompt:{} res:{}", promptName, jsonObj);
            Map<String, String> res = new HashMap<>();
            keys.stream().forEach(it -> {
                if (jsonObj.has(it)) {
                    JsonElement value = jsonObj.get(it);
                    if (value.isJsonPrimitive()) {
                        res.put(it, value.getAsString());
                    } else {
                        res.put(it, value.toString());
                    }
                }
            });
            return Result.success(res);
        } finally {
            log.info("call prompt:{} use time:{} s", promptName, sw.elapsed(TimeUnit.SECONDS));
        }
    }

    /**
     * Executes an AI service call with the given prompt name, parameters, and key, then returns a Result object containing the response string associated with the key.
     */
    public Result<String> call(String promptName, Map<String, String> params, String key) {
        return call(promptName, params, key, null);
    }

    public Result<String> call(String promptName, Map<String, String> params, String key, String model) {
        log.info("ChatgptService.call, promptName:{}, params:{}, key:{}, model:{}", promptName, params, key, model);
        Stopwatch sw = Stopwatch.createStarted();
        try {
            JsonObject jsonObj = call0(promptName, params, model);
            log.info("call prompt:{} res:{}", promptName, jsonObj);
            return Result.success(jsonObj.get(key).getAsString());
        } finally {
            log.info("call prompt:{} use time:{} s", promptName, sw.elapsed(TimeUnit.SECONDS));
        }
    }

    /**
     * 调用指定模型并返回结果
     *
     * @param promptName 提示名称
     * @param params     参数映射
     * @param model      模型名称
     * @return 调用结果的JsonObject
     */
    public JsonObject callWithModel(String promptName, Map<String, String> params, String model) {
        log.info("ChatgptService.callWithModel, promptName:{}, params:{}, model:{}", promptName, params, model);
        Stopwatch sw = Stopwatch.createStarted();
        try {
            JsonObject jsonObj = call0(promptName, params, model);
            log.info("call prompt:{} res:{}", promptName, jsonObj);
            return jsonObj;
        } finally {
            log.info("call prompt:{} use time:{} s", promptName, sw.elapsed(TimeUnit.SECONDS));
        }
    }


    /**
     * Executes an asynchronous call to an AI proxy with the provided 'ask' object, processes the stream of responses using the given 'consumer', and handles the stream lifecycle events including logging the beginning and end of the call, and error handling.
     */
    public void ask(Ask ask, Consumer<String> consumer) {
        String askReq = GsonUtils.gson.toJson(ask);
        log.info("ask req:{}", askReq);
        OpenaiCall.callStream2(askReq, new StreamListener() {

            @Override
            public void begin() {
                log.info("begin");
            }

            @Override
            public void onEvent(String str) {
                consumer.accept(str);
            }

            @Override
            public void onFailure(Throwable t, Response response) {
                log.error("调用ai proxy发生了错误:" + t.getMessage());
                consumer.accept("^quit");
            }

            @Override
            public void end() {
                log.info("end");
                consumer.accept("^quit");
            }
        }, ReqConfig.builder().connectTimeout(TimeUnit.SECONDS.toMillis(10)).readTimeout(TimeUnit.SECONDS.toMillis(30)).maxTokens(4000).askUrl(Config.aiProxy + "/ask").build());
    }


    /**
     * 处理Ask请求并通过流式监听器处理响应
     *
     * @param ask      请求对象，包含请求的详细信息
     * @param consumer 消费者，用于处理AiProxyMessage对象
     */
    public void ask2(Ask ask, Consumer<AiProxyMessage> consumer) {
        String askReq = GsonUtils.gson.toJson(ask);
        log.info("ask req:{}", askReq);
        OpenaiCall.callStream2(askReq, new StreamListener() {
            StringBuilder sb = new StringBuilder();

            @Override
            public void begin() {
                log.info("begin");
                consumer.accept(AiProxyMessage.builder().msgId(ask.getId()).message("").type("begin").build());
            }

            @Override
            public void onEvent(String str) {
                consumer.accept(AiProxyMessage.builder().msgId(ask.getId()).message(str).type("event").build());
                sb.append(new String(Base64.decodeBase64(str)));
            }

            @Override
            public void onFailure(Throwable t, Response response) {
                log.error("调用ai proxy发生了错误:" + t.getMessage());
                consumer.accept(AiProxyMessage.builder().msgId(ask.getId()).message("^quit").type("failure").build());
            }

            @Override
            public void end() {
                log.info("end");
                consumer.accept(AiProxyMessage.builder().msgId(ask.getId()).message("^quit").type("end").build());
                // 计算token并异步落库

                costTokenService.addCostTokenRecord(M78UserCostTokenDetail.builder()
                        .type(1).relationId(ask.getRelationId()).user(ask.getUserName()).input(GsonUtils.gson.toJson(ask.getMsgList())).output(sb.toString())
                        .build());

            }
        }, ReqConfig.builder().connectTimeout(TimeUnit.SECONDS.toMillis(10)).readTimeout(TimeUnit.MINUTES.toMillis(5)).maxTokens(4096).askUrl(Config.aiProxy + "/ask").build());
    }

    private JsonObject call0(String promptName, Map<String, String> params, String model) {
        log.info("ChatgptService.call0, promptName:{}, params:{}, , model:{}", promptName, params, model);
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);
        List<AiChatMessage<?>> list = new ArrayList<>();
        list.add(AiChatMessage.builder().data("").role(Role.user).build());

        Message.MessageBuilder messageBuilder = Message.builder();
        messageBuilder.promptName(promptInfo.getPromptName()).params(params);
        List<Message> messageList = list.stream().map(it -> messageBuilder
                .content(it.toString())
                .role(it.getRole().name())
                .build()).collect(Collectors.toList());

        Completions completions = Completions.builder().model(model).stream(false).response_format(Format.builder().build()).messages(messageList).build();
        JsonObject jsonObj = ProxyAiService.call(GsonUtils.gson.toJson(completions), Long.parseLong(promptInfo.getLabels().getOrDefault("timeout", "100000")), false, model);
        return jsonObj;
    }

    private JsonObject callAiProxy(String promptName, Map<String, String> params, String model, String temperature, JsonObject req) {
        log.info("ChatgptService.call AiProxy, promptName:{}, params:{}, , model:{}", promptName, params, model);
        removeUserName(params);
        PromptInfo promptInfo = Prompt.getPromptInfo(promptName);

        List<Message> messageList = new ArrayList<>();

        //system
        if (params.containsKey("system_setting") && StringUtils.isNotEmpty(params.get("system_setting"))) {
            Message systemMessage = Message.builder().content(params.get("system_setting")).params(ImmutableMap.of("system_setting", params.get("system_setting"))).role(Role.system.name()).build();
            messageList.add(systemMessage);
        }

        //history
        if (req.has("history")) {
            req.get("history").getAsJsonArray().forEach(it -> {
                JsonObject obj = it.getAsJsonObject();
                Message msg = Message.builder().content(obj.get("content").getAsString()).role(obj.get("role").getAsString()).build();
                messageList.add(msg);
            });
        }

        //user
        AiChatMessage userAiMessage = AiChatMessage.builder().data("").role(Role.user).build();
        Message userMessage = Message.builder().promptName(promptInfo.getPromptName()).params(params).content(userAiMessage.toString())
                .role(userAiMessage.getRole().name()).build();
        messageList.add(userMessage);

        Completions completions = Completions.builder().stream(false).response_format(Format.builder().build()).messages(messageList).build();
        JsonObject jsonObj = ProxyAiService.call(GsonUtils.gson.toJson(completions), Long.parseLong(promptInfo.getLabels().getOrDefault("timeout", "100000")), false, model, StringUtils.isEmpty(temperature) ? "0.0" : temperature, req);
        return jsonObj;
    }

    private static void removeUserName(Map<String, String> params) {
        SafeRun.run(() -> {
            //调用大模型时去掉用户信息
            if (params != null && !params.isEmpty() && params.containsKey("user_name") && !(params instanceof ImmutableMap)) {
                params.remove("user_name");
            }
        });
    }

    /**
     * 将音频文件转换为文本
     *
     * @param filePath 音频文件的路径
     * @return 转换后的文本内容
     */
    public String audioToText(String filePath) {
        log.info("audioToText filePath:{}", filePath);
        return ProxyAiService.audioToText(filePath);
    }

    /**
     * 将文本转换为音频
     *
     * @param audioParam 包含音频参数的对象
     * @return 生成的音频字节数组
     */
    public byte[] textToAudio(AudioParam audioParam) {
        return ProxyAiService.textToAudio(audioParam);
    }



}
