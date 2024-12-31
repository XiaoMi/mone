package com.xiaomi.youpin.tesla.ip.service;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.*;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.Message;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.*;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiChatMessage;
import com.xiaomi.youpin.tesla.ip.bo.robot.MessageType;
import com.xiaomi.youpin.tesla.ip.bo.robot.ProjectAiMessageManager;
import com.xiaomi.youpin.tesla.ip.bo.robot.Role;
import com.xiaomi.youpin.tesla.ip.common.ChromeUtils;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.common.NotificationCenter;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import run.mone.openai.OpenaiCall;
import run.mone.openai.ReqConfig;
import run.mone.openai.StreamListener;
import run.mone.ultraman.AthenaInspection;
import run.mone.ultraman.background.AthenaTask;
import run.mone.ultraman.common.GsonUtils;
import run.mone.ultraman.state.ProjectFsmManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/11/23 13:56
 * <p>
 * 这个类允许你本地直连chatgpt(如果是国内的网络需要设置open_ai_proxy)
 */
@Slf4j
public class LocalAiService {


    private static String getOpenAiUrl() {
        String url = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_PROXY, "");
        if (StringUtils.isEmpty(url)) {
            return "https://api.openai.com/v1/";
        }
        return url;
    }


    /**
     * 调用chatgpt的语音翻译(你需要有chatgpt的账号)
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public static Pair<Integer, String> callChatgptTranscriptions(File file) {
        String apiKey = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_KEY, "");

        if (StringUtils.isEmpty(apiKey)) {
            return Pair.of(500, "在环境变量中设置:OPENAI_API_KEY");
        }


        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mpeg"), file);

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        multipartBodyBuilder.addFormDataPart("file", "openai.mp3", requestBody);
        multipartBodyBuilder.addFormDataPart("model", "whisper-1");

        Request request = new Request.Builder()
                .url(getOpenAiUrl() + "/audio/transcriptions")
                .post(multipartBodyBuilder.build())
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "multipart/form-data")
                .build();

        OkHttpClient client = new OkHttpClient();

        Response response = client.newCall(request).execute();

        if (response.code() == 200) {
            AudioRes res = new Gson().fromJson(response.body().string(), AudioRes.class);
            return Pair.of(0, res.getText());
        }

        return Pair.of(500, "");
    }


    /**
     * 直接把文字转成语音
     *
     * @param req
     * @return
     */
    @SneakyThrows
    public static Pair<Integer, String> speech(SpeechReq req) {
        String apiKey = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_KEY, "");
        if (StringUtils.isEmpty(apiKey)) {
            return Pair.of(500, "在环境变量中设置:OPENAI_API_KEY");
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(req));
        Request request = new Request.Builder()
                .url(getOpenAiUrl() + "/audio/speech")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        if (response.code() == 200) {
            byte[] saveData = response.body().bytes();
            Files.write(Paths.get("/tmp/sp.mp3"), saveData);
            return Pair.of(0, Base64.encode(saveData));
        }

        return Pair.of(500, "");

    }


    //多模态调用(支持图片)
    public static String vision(VisionReq req) {
        String apiKey = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_KEY, "");
        if (StringUtils.isEmpty(apiKey)) {
            return "";
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(req));
        Request request = new Request.Builder()
                .url(getOpenAiUrl() + "/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String str = response.body().string();
            VisionRes visionRes = new Gson().fromJson(str, VisionRes.class);
            System.out.println(visionRes.getChoices().get(0));
            return visionRes.getChoices().get(0).getMessage().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static JsonObject call(List<Message> messageList) {
        Completions completions = Completions.builder()
                .stream(false)
                .response_format(Format.builder().build()).messages(messageList).build();
        String req = GsonUtils.gson.toJson(completions);
        return call(req);
    }


    public static JsonObject call(String req) {
        return call(req, 5000);
    }

    //这个会直接调用chatgpt
    @SneakyThrows
    public static JsonObject call(String req, long timeout) {
        long time = timeout;
        for (int i = 1; i <= 3; i++) {
            time = time * i;
            log.info("call gpt req:{} timeout:{}", req, timeout);
            String apiKey = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_KEY, "");
            if (StringUtils.isEmpty(apiKey)) {
                return null;
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(time, TimeUnit.MILLISECONDS)
                    .readTimeout(time, TimeUnit.MILLISECONDS)
                    .build();

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

            Request request = new Request.Builder()
                    .url(getOpenAiUrl() + "/chat/completions")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(mediaType, req.getBytes(Charset.forName("utf8"))))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                // 判断请求是否成功
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseJson(responseBody);
                } else {
                    System.err.println("Request failed: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                TimeUnit.SECONDS.sleep(1);
            }
        }
        return null;
    }


    //直接问问题(直接调用chatgpt)
    public static void completions(String req, StreamListener sl) {
        String apiKey = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_KEY, "");
        if (StringUtils.isEmpty(apiKey)) {
            return;
        }
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        Request request = new Request.Builder()
                .url(getOpenAiUrl() + "/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(mediaType, req.getBytes(Charset.forName("utf8"))))
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .build();


        EventSourceListener listener = new EventSourceListener() {

            @Override
            public void onOpen(EventSource eventSource, Response response) {
                log.info("onOpen");
                sl.begin();
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                data = parse(data);
                sl.onEvent(data);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("onClosed");
                sl.end();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.info("onFailure response:" + response, t);
                sl.onFailure(t, response);
                sl.end();
            }
        };

        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, listener);
    }


    private static String parse(String data) {
        log.info(data);
        if (data.equals("[DONE]")) {
            return "";
        }
        try {
            JSONArray choices = JSON.parseObject(data).getJSONArray("choices");
            if (choices.isEmpty()) {
                return "";
            }

            String obj = choices.getJSONObject(0).getJSONObject("delta").getString("content");
            if (null != obj) {
                return obj;
            }
            return "";
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return "";
    }

    private static JsonObject parseJson(String data) {
        log.info("data:{}", data);
        JsonObject obj = GsonUtils.gson.fromJson(data, JsonObject.class);
        JsonArray choices = obj.getAsJsonArray("choices");
        return GsonUtils.gson.fromJson(choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString(), JsonObject.class);
    }

    //本地直接调用chatgpt,单条提问
    public static void localCall(Project project, String prompt) {
        localCall(project, Lists.newArrayList(Message.builder().content(prompt).role("user").build()), false, 0);
    }


    public static void localCall(String projectName, boolean code) {
        log.info("question call prompt");
        List<AiChatMessage<?>> list = ProjectAiMessageManager.getInstance().getMessageList(projectName);
        //转换为Message
        List<Message> newMessageList = list.stream().map(it -> Message.builder().role(it.getRole().name()).content(it.toString()).build()).peek(it -> {
            log.info("---->role:{} content:{}", it.getRole(), it.getContent());
        }).collect(Collectors.toList());
        String id = UUID.randomUUID().toString();
        chat(ProjectUtils.projectFromManager(projectName), code, newMessageList, projectName, id);
    }


    //目前是从前端拿到的,这个需要自己记录,这样以后能完成的事情更多
    public static void localCall(Project project, List<Message> messageList, boolean code, int num) {
        //这是前端传过来的(以后只信任最后一条)
        Message msg = messageList.get(messageList.size() - 1);

        boolean process = ProjectFsmManager.processMsg(project.getName(), msg.getContent());
        if (process) {
            return;
        }

        //这就是自己维护的那份消息列表(这里的id是判断这个msg的时候返回给前端的)
        List<AiChatMessage<?>> list = ProjectAiMessageManager.getInstance().addMessage(project,
                AiChatMessage.builder()
                        .message(msg.getContent())
                        .type(MessageType.string)
                        .data(msg.getContent())
                        .id(msg.getId())
                        .role(Role.user)
                        .build());

        //转换为Message
        List<Message> newMessageList = list.stream().map(it -> Message.builder().role(it.getRole().name()).content(it.toString()).build()).peek(it -> {
            log.info("---->role:{} content:{}", it.getRole(), it.getContent());
        }).collect(Collectors.toList());

        //取最后的一部分
        if (num != 0) {
            newMessageList = getLastElements(newMessageList, num);
        }


        String projectName = project.getName();
        String msgId = UUID.randomUUID().toString();

        // TODO 直接调用BOT
        if("true".equals(ResourceUtils.get(Const.CONF_CHAT_USE_BOT, "true"))) {
            Map<String, String> param = new HashMap<>();
            param.put("chatContent", msg.getContent());
            AthenaInspection.invokePrompt(project, Const.ATHENA_CHAT, param);
            return;
        }

        //调用ai proxy
        if (LabelUtils.open(project, Const.AI_PROXY_CHAT, "true")) {
            aiProxyChat(project, newMessageList);
            return;
        }

        //直接本地调用
        chat(project, code, newMessageList, projectName, msgId);
    }


    public static List<Message> getLastElements(List<Message> list, int num) {
        if (list == null || list.isEmpty() || num <= 0) {
            return Collections.emptyList();
        }
        int startIndex = Math.max(list.size() - num, 0);
        return list.subList(startIndex, list.size());
    }

    private static void chat(Project project, boolean code, List<Message> newMessageList, String projectName, String id) {
        ApplicationManager.getApplication().invokeLater(() -> AthenaTask.start(new Task.Backgroundable(project, "chat", true) {
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String model = ResourceUtils.getAthenaConfig().getOrDefault(Const.OPEN_AI_MODEL, "gpt-3.5-turbo");
                Completions completions = Completions.builder().model(model).messages(newMessageList).build();
                CountDownLatch latch = new CountDownLatch(1);
                StringBuilder sb = new StringBuilder();
                LocalAiService.completions(GsonUtils.gson.toJson(completions), new StreamListener() {

                    @Override
                    public void begin() {
                        AiMessage msg = AiMessage.builder().projectName(projectName).code(code).type(AiMessageType.begin).id(id).build();
                        sendMsg(msg, projectName);
                    }

                    @Override
                    public void onEvent(String str) {
                        AiMessage msg = AiMessage.builder().projectName(projectName).type(AiMessageType.process).id(id).text(str).build();
                        sendMsg(msg, projectName);
                        sb.append(msg.getText());
                    }


                    @Override
                    public void end() {
                        latch.countDown();
                        AiMessage msg = AiMessage.builder().projectName(projectName).type(AiMessageType.success).id(id).build();
                        sendMsg(msg, projectName);
                        String str = sb.toString();
                        ProjectAiMessageManager.getInstance().addMessage(project,
                                AiChatMessage.builder().id(id).message(str).data(str).type(MessageType.string).role(Role.assistant).build());
                    }
                });
                latch.await(15, TimeUnit.SECONDS);
            }
        }));
    }


    //调用ai proxy(进行聊天)
    private static void aiProxyChat(Project project, List<Message> newMessageList) {
        ApplicationManager.getApplication().invokeLater(() -> AthenaTask.start(new Task.Backgroundable(project, "Athena", true) {
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                CountDownLatch latch = new CountDownLatch(1);
                String projectName = project.getName();
                List<Msg> msgList = newMessageList.stream().map(it -> {
                    Msg msg = new Msg();
                    msg.setContent(it.getContent());
                    msg.setRole(it.getRole().toUpperCase());
                    return msg;
                }).collect(Collectors.toList());
                try {
                    ProxyAsk pa = new ProxyAsk();
                    pa.setMsgList(msgList);
                    pa.setType(1);
                    pa.setFrom("chat");
                    CodeService.setModelAndDebug(project, pa, Maps.newHashMap(), GenerateCodeReq.builder().project(project).build());
                    String id = UUID.randomUUID().toString();
                    pa.setId(id);
                    pa.setZzToken(ConfigUtils.getConfig().getzToken());
                    OpenaiCall.callStream2(GsonUtils.gson.toJson(pa), new StreamListener() {

                        StringBuilder sb = new StringBuilder();

                        @Override
                        public void begin() {
                            AiMessage msg = AiMessage.builder().projectName(projectName).code(false).type(AiMessageType.begin).id(id).build();
                            sendMsg(msg, projectName);
                        }

                        @Override
                        public void onEvent(String str) {
                            str = new String(java.util.Base64.getDecoder().decode(str.getBytes(Charset.forName("utf8"))), Charset.forName("utf8"));
                            AiMessage msg = AiMessage.builder().projectName(projectName).type(AiMessageType.process).id(id).text(str).build();
                            sendMsg(msg, projectName);
                            sb.append(msg.getText());
                        }

                        @Override
                        public void onFailure(Throwable t, Response response) {
                            NotificationCenter.notice("调用ai proxy发生了错误:" + t.getMessage(), NotificationType.ERROR);
                        }

                        @Override
                        public void end() {
                            latch.countDown();
                            AiMessage msg = AiMessage.builder().projectName(projectName).type(AiMessageType.success).id(id).build();
                            sendMsg(msg, projectName);
                            String str = sb.toString();
                            ProjectAiMessageManager.getInstance().addMessage(project,
                                    AiChatMessage.builder().id(id).message(str).data(str).type(MessageType.string).role(Role.assistant).build());
                        }
                    }, ReqConfig.builder().connectTimeout(TimeUnit.SECONDS.toMillis(10)).readTimeout(TimeUnit.SECONDS.toMillis(30)).maxTokens(4000).askUrl(ConfigUtils.getConfig().getAiProxy() + "/ask").build());
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
                latch.await(1, TimeUnit.MINUTES);
            }
        }));


    }

    //每次都只发一部分(发送到右侧 athena)
    public static void sendMsg(AiMessage message, String projectName) {
        String str = GsonUtils.gson.toJson(message);
        ChromeUtils.call(projectName, "setResultCode", str);
    }


}
