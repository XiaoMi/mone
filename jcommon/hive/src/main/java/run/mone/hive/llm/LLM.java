package run.mone.hive.llm;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static run.mone.hive.llm.ClaudeProxy.getClaudeKey;
import static run.mone.hive.llm.ClaudeProxy.getClaudeName;

@Data
@Slf4j
public class LLM {

    protected LLMConfig config;

    private LLMProvider llmProvider;

    //可以外部设置进来
    private Function<LLMProvider, Optional<LLMConfig>> configFunction;

    private BotBridge botBridge;

    private Gson gson = new Gson();

    private boolean google = false;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public LLM(LLMConfig config) {
        this.config = config;
        this.llmProvider = config.getLlmProvider();
    }

    //支持多模态
    public String chat(String prompt) {
        return ask(prompt).join();
    }


    public String chat(List<AiMessage> msgList) {
        return chatCompletion(getToken(), msgList, llmProvider.getDefaultModel(), "", config);
    }

    public String chat(List<AiMessage> msgList, String systemPrompt) {
        return chatCompletion(getToken(), msgList, llmProvider.getDefaultModel(), systemPrompt, config);
    }

    public String chat(List<AiMessage> msgList, LLMConfig config) {
        return chatCompletion(getToken(), msgList, llmProvider.getDefaultModel(), "", config);
    }

    public String getApiUrl(String apiKey, boolean stream) {
        String key = "";
        if (this.llmProvider == LLMProvider.GOOGLE_2 && StringUtils.isEmpty(config.getUrl())) {
            key = apiKey;
        }

        if (null != this.config && StringUtils.isNotEmpty(this.config.getUrl())) {
            String urlToUse;
            if (stream && StringUtils.isNotEmpty(this.config.getStreamUrl())) {
                // 处理多个流式URL的情况
                String[] streamUrls = this.config.getStreamUrl().split(",");
                urlToUse = getRandomUrl(streamUrls);
            } else {
                // 处理多个普通URL的情况
                String[] urls = this.config.getUrl().split(",");
                urlToUse = getRandomUrl(urls);
            }
            return urlToUse + key;
        }
        return llmProvider.getUrl() + key;
    }

    public String getApiUrl(String apiKey) {
        return getApiUrl(apiKey, false);
    }


    public CompletableFuture<String> ask(String prompt) {
        if (config.isDebug()) {
            return CompletableFuture.completedFuture("res:" + prompt);
        } else {
            String model = llmProvider.getDefaultModel();
            if (StringUtils.isNotEmpty(this.config.getModel())) {
                model = this.config.getModel();
            }
            return CompletableFuture.completedFuture(chatCompletion(getToken(), prompt, model));
        }
    }

    //支持多模态
    public String chatCompletion(String apiKey, String content, String model) {
        return chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role("user").content(content).build()), model, "", config);
    }


    public String ask(List<AiMessage> messages) {
        return chatCompletion(getToken(), messages, getModel(), "", this.config);
    }

    public String chatCompletion(List<AiMessage> messages, String systemInstruction) {
        return chatCompletion(getToken(), messages, getModel(), systemInstruction, this.config);
    }


    @SneakyThrows
    public String chatCompletion(String apiKey, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);

        if (clientConfig.isWebSearch()) {
            JsonArray tools = new JsonArray();
            JsonObject tool = new JsonObject();
            tool.addProperty("type", "web_search");
            JsonObject function = new JsonObject();
            function.addProperty("description", "这个web_search用来搜索互联网的信息");
            tool.add("function", function);
            tools.add(tool);
            requestBody.add("tools", tools);
            systemPrompt = systemPrompt + "\n每个提问先通过web search，然后通过web search的结果，回答用户问题\n";
        }


        if (this.config.isStream()) {
            requestBody.addProperty("stream", true);
        }
        JsonArray msgArray = new JsonArray();

        if (this.llmProvider != LLMProvider.GOOGLE_2) {
            if (this.config.isJson() || clientConfig.isJson()) {
                String jsonSystemPrompt = """
                         返回结果请用JSON返回(如果用户没有指定json格式,则直接返回{"content":$res}),thx
                        """;
                JsonObject rf = new JsonObject();
                rf.addProperty("type", "json_object");
                requestBody.add("response_format", rf);
                msgArray.add(createMessageObject("system", jsonSystemPrompt));
            } else {
                if (StringUtils.isNotEmpty(systemPrompt)) {
                    msgArray.add(createMessageObject("system", systemPrompt));
                }
            }
        }

        //gemini的系统提示词
        if (llmProvider == LLMProvider.GOOGLE_2 && StringUtils.isNotEmpty(systemPrompt)) {
            JsonObject system_instruction = new JsonObject();
            JsonObject text = new JsonObject();
            text.addProperty("text", systemPrompt);
            system_instruction.add("parts", text);
            requestBody.add("system_instruction", system_instruction);
        }


        for (AiMessage message : messages) {
            //使用openrouter,并且使用多模态
            if ((this.llmProvider == LLMProvider.OPENROUTER
                    || this.llmProvider == LLMProvider.MOONSHOT
                    || this.llmProvider == LLMProvider.DOUBAO
            ) && null != message.getJsonContent()) {
                msgArray.add(message.getJsonContent());
            } else if (this.llmProvider == LLMProvider.GOOGLE_2) {
                msgArray.add(createMessageObjectForGoogle(message));
            } else {
                msgArray.add(createMessageObject(message.getRole(), message.getContent()));
            }
        }

        requestBody.add(getContentsName(), msgArray);

        Request.Builder requestBuilder = new Request.Builder();

        if (this.llmProvider != LLMProvider.GOOGLE_2) {
            requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
        }

        //使用的cloudflare
        String url = getCloudFlareUrl(apiKey, model, requestBuilder);

        String rb = requestBody.toString();

        log.info("call llm:{}\nmessage:{}\n", model, rb);
        Stopwatch sw = Stopwatch.createStarted();
        String res = "";

        Request request = requestBuilder
                .url(url)
                .post(RequestBody.create(rb, JSON))
                .build();
        String responseBody = "";
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            responseBody = response.body().string();
            log.info("res:{}", responseBody);
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (llmProvider == LLMProvider.GOOGLE_2) {
                JsonObject candidate = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject();
                JsonObject content = candidate.get("content").getAsJsonObject();
                String text = content.get("parts").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                return text;
            }

            //openai那个流派的
            res = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            return res;
        } finally {
            log.info("call llm res:\n{}\n use time:{}ms", responseBody, sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }


    //把一张图片变成base64 要考虑 浏览器能接受的格式 png jpeg (--input) (class)
    @SneakyThrows
    public String imageToBase64(String imagePath, String formatName) {
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }


    public String getContentsName() {
        return this.llmProvider == LLMProvider.GOOGLE_2 ? "contents" : "messages";
    }

    // 文本转语音
    public byte[] generateSpeech(String text) throws IOException {
        return generateSpeech(getToken(), text, "wenrounvsheng", null);
    }

    public byte[] generateSpeech(String text, String voice) throws IOException {
        return generateSpeech(getToken(), text, voice, null);
    }

    public byte[] generateSpeech(String text, String voice, String outputPath) throws IOException {
        return generateSpeech(getToken(), text, voice, outputPath);
    }


    public String getToken() {
        //直接获取token
        if (null != this.configFunction) {
            Optional<LLMConfig> optional = this.configFunction.apply(this.llmProvider);
            if (optional.isPresent()) {
                return optional.get().getToken();
            }
        }
        //从环境变量里获取
        String token = System.getProperty(llmProvider.getEnvName());
        if (StringUtils.isEmpty(token)) {
            return System.getenv(llmProvider.getEnvName());
        }
        return token;
    }


    public byte[] generateSpeech(String apiKey, String text, String voice, String outputPath) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", llmProvider.getDefaultModel());
        requestBody.addProperty("input", text);
        requestBody.addProperty("voice", voice);

        Request request = new Request.Builder()
                .url(llmProvider.getUrl())
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            byte[] audioData = response.body().bytes();

            if (outputPath != null) {
                java.nio.file.Files.write(java.nio.file.Paths.get(outputPath), audioData);
                return null;
            }

            return audioData;
        }
    }

    private JsonObject createMessageObject(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

    private JsonObject createMessageObjectForGoogle(AiMessage am, String role, String content) {
        if (null != am.getJsonContent()) {
            return am.getJsonContent();
        }

        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        JsonArray array = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("text", content);
        array.add(obj);
        message.add("parts", array);
        return message;
    }

    private JsonObject createMessageObjectForGoogle(AiMessage am) {
        if (null != am.getJsonContent()) {
            return am.getJsonContent();
        }

        JsonObject message = new JsonObject();
        message.addProperty("role", am.getRole());
        JsonArray array = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("text", am.getContent());
        array.add(obj);
        message.add("parts", array);

        return message;
    }


    public void chat(List<AiMessage> messages, BiConsumer<String, JsonObject> messageHandlerr) {
        chatCompletionStream(getToken(),
                messages,
                llmProvider.getDefaultModel(),
                messageHandlerr,
                line -> {
                },
                "");
    }

    public void chat(List<AiMessage> messages, BiConsumer<String, JsonObject> messageHandlerr, String systemPrompt) {
        chatCompletionStream(System.getenv(llmProvider.getEnvName()),
                messages,
                llmProvider.getDefaultModel(),
                messageHandlerr,
                line -> {
                },
                systemPrompt
        );
    }

    public Flux<String> compoundMsgCall(LLMCompoundMsg msg, String systemPrompt) {
        JsonObject req = getReq(this, msg);
        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        return call(messages, systemPrompt);
    }

    public Flux<String> call(List<AiMessage> messages) {
        return call(messages, "");
    }

    public Flux<String> call(List<AiMessage> messages, String systemPrompt) {
        return Flux.create(sink -> chatCompletionStream(getToken(),
                messages,
                llmProvider.getDefaultModel(),
                (a, b) -> {
                },
                (a) -> {
                },
                systemPrompt,
                sink)
        );
    }


    public Flux<String> chat(String apiKey, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer, String systemPrompt) {
        return Flux.create(sink -> chatCompletionStream(apiKey, messages, model, messageHandler, lineConsumer, systemPrompt, sink));
    }


    public void chatCompletionStream(String apiKey, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer, String systemPrompt) {
        chatCompletionStream(apiKey, messages, model, messageHandler, lineConsumer, systemPrompt, null);
    }

    public void chatCompletionStream(String apiKey, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer, String systemPrompt, FluxSink<String> sink) {
        JsonObject requestBody = new JsonObject();

        if (this.llmProvider != LLMProvider.GOOGLE_2
                && this.llmProvider != LLMProvider.CLAUDE_COMPANY) {
            requestBody.addProperty("model", model);
            requestBody.addProperty("stream", true);
        }

        if (this.llmProvider == LLMProvider.CLAUDE_COMPANY) {
            requestBody.addProperty("anthropic_version", this.config.getVersion());
            requestBody.addProperty("max_tokens", this.config.getMaxTokens());
            requestBody.addProperty("stream", true);
        }

        JsonArray msgArray = new JsonArray();

        if (this.llmProvider != LLMProvider.GOOGLE_2
                && this.llmProvider != LLMProvider.CLAUDE_COMPANY) {
            if (this.config.isJson()) {
                String jsonSystemPrompt = """
                         返回结果请用JSON返回(如果用户没有指定json格式,则直接返回{"content":$res}),thx
                        """;
                JsonObject rf = new JsonObject();
                rf.addProperty("type", "json_object");
                requestBody.add("response_format", rf);
                msgArray.add(createMessageObject("system", jsonSystemPrompt));
            } else {
                if (StringUtils.isNotEmpty(systemPrompt)) {
                    msgArray.add(createMessageObject("system", systemPrompt));
                }
            }
        }

        //claude的系统提示词
        if (llmProvider == LLMProvider.CLAUDE_COMPANY && StringUtils.isNotEmpty(systemPrompt)) {
            requestBody.addProperty("system", systemPrompt);
        }

        //gemini的系统提示词
        if (llmProvider == LLMProvider.GOOGLE_2 && StringUtils.isNotEmpty(systemPrompt)) {
            JsonObject system_instruction = new JsonObject();
            JsonObject text = new JsonObject();
            text.addProperty("text", systemPrompt);
            system_instruction.add("parts", text);
            requestBody.add("system_instruction", system_instruction);
        }

        for (AiMessage message : messages) {
            //使用openrouter,并且使用多模态
            if ((this.llmProvider == LLMProvider.OPENROUTER || this.llmProvider == LLMProvider.MOONSHOT || this.llmProvider == LLMProvider.DEEPSEEK || this.llmProvider == LLMProvider.CLAUDE_COMPANY) && null != message.getJsonContent()) {
                msgArray.add(message.getJsonContent());
            } else if (this.llmProvider == LLMProvider.GOOGLE_2) {
                msgArray.add(createMessageObjectForGoogle(message, message.getRole(), message.getContent()));
            } else {
                msgArray.add(createMessageObject(message.getRole(), message.getContent()));
            }
        }
        requestBody.add(getContentsName(), gson.toJsonTree(msgArray));

        Request.Builder rb = new Request.Builder();

        if (this.llmProvider != LLMProvider.GOOGLE_2) {
            if (this.llmProvider == LLMProvider.CLAUDE_COMPANY) {
                rb.addHeader("Authorization", "Bearer " + getClaudeKey(getClaudeName()));
            } else {
                rb.addHeader("Authorization", "Bearer " + apiKey);
            }
        }

        //使用的cloudflare
        String url = getCloudFlareUrl(apiKey, model, rb, true);

        Request request = rb
                .url(url)
                .addHeader("Accept", "text/event-stream")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Stream request failed", e);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("type", "failure");
                jsonResponse.addProperty("content", e.getMessage());
                messageHandler.accept(e.getMessage(), jsonResponse);
                if (null != sink) {
                    sink.error(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        log.error("Unexpected response code: " + response);
                        throw new IOException("Unexpected response code: " + response);
                    }
                    SSEReader reader = new SSEReader(responseBody.source());
                    String line;

                    // 添加begin标识
                    JsonObject beginResponse = new JsonObject();
                    beginResponse.addProperty("type", "begin");
                    beginResponse.addProperty("content", "[BEGIN]");
                    messageHandler.accept("[BEGIN]", beginResponse);

                    while ((line = reader.readLine()) != null) {
                        System.out.println("===>" + line);
                        lineConsumer.accept(line);
                        if (llmProvider == LLMProvider.GOOGLE_2) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                JsonObject jsonResponse = gson.fromJson(data, JsonObject.class);
                                JsonObject candidate = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject();
                                JsonObject content = candidate.get("content").getAsJsonObject();
                                String text = content.get("parts").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                                jsonResponse.addProperty("type", "event");
                                jsonResponse.addProperty("content", text);
                                messageHandler.accept(text, jsonResponse);
                                if (null != sink) {
                                    sink.next(text);
                                }
                                if (candidate.has("finishReason")) {
                                    JsonObject finishRes = new JsonObject();
                                    finishRes.addProperty("type", "finish");
                                    finishRes.addProperty("content", candidate.get("finishReason").getAsString());
                                    messageHandler.accept("[DONE]", finishRes);

                                    if (null != sink) {
                                        sink.complete();
                                    }

                                }
                            }
                        } else if (llmProvider == LLMProvider.CLAUDE_COMPANY) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                JsonObject jsonResponse = gson.fromJson(data, JsonObject.class);
                                if ("message_start".equals(jsonResponse.get("type").getAsString())
                                        || "ping".equals(jsonResponse.get("type").getAsString())
                                        || "content_block_start".equals(jsonResponse.get("type").getAsString())) {
                                    continue;
                                }

                                if ("content_block_stop".equals(jsonResponse.get("type").getAsString())) {
                                    JsonObject jsonRes = new JsonObject();
                                    jsonRes.addProperty("type", "finish");
                                    jsonRes.addProperty("content", "[DONE]");
                                    messageHandler.accept("[DONE]", jsonRes);
                                    if (null != sink) {
                                        sink.complete();
                                    }
                                    break;
                                }

                                if ("content_block_delta".equals(jsonResponse.get("type").getAsString())) {
                                    String content = "";
                                    try {
                                        JsonObject delta = jsonResponse.getAsJsonObject("delta");
                                        content = delta.get("text").getAsString();
                                    } catch (Throwable ex) {
                                        log.error(ex.getMessage());
                                    }
                                    jsonResponse.addProperty("type", "event");
                                    jsonResponse.addProperty("content", content);
                                    messageHandler.accept(content, jsonResponse);
                                    if (null != sink) {
                                        sink.next(content);
                                    }
                                }
                            }
                        } else {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if ("[DONE]".equals(data)) {
                                    JsonObject jsonResponse = new JsonObject();
                                    jsonResponse.addProperty("type", "finish");
                                    jsonResponse.addProperty("content", "[DONE]");
                                    messageHandler.accept("[DONE]", jsonResponse);
                                    if (null != sink) {
                                        sink.complete();
                                    }
                                    break;
                                }
                                JsonObject jsonResponse = gson.fromJson(data, JsonObject.class);
                                String content = "";
                                try {
                                    JsonObject delta = jsonResponse.getAsJsonArray("choices")
                                            .get(0).getAsJsonObject()
                                            .getAsJsonObject("delta");

                                    JsonElement c = delta.get("content");
                                    if ((c.isJsonPrimitive() && StringUtils.isEmpty(c.getAsString())) || c.isJsonNull()) {
                                        JsonElement rc = delta.get("reasoning_content");
                                        if (null != rc && !rc.isJsonNull()) {
                                            content = rc.getAsString();
                                        }
                                    } else {
                                        content = c.getAsString();
                                    }
                                } catch (Throwable ex) {
                                    log.error(ex.getMessage());
                                }

                                jsonResponse.addProperty("type", "event");
                                jsonResponse.addProperty("content", content);
                                messageHandler.accept(content, jsonResponse);
                                if (null != sink) {
                                    sink.next(content);
                                }
                            }
                        }
                    }
                    log.info("FINISH");
                } catch (Throwable ex) {
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("type", "failure");
                    jsonResponse.addProperty("content", ex.getMessage());
                    messageHandler.accept(ex.getMessage(), jsonResponse);
                    if (null != sink) {
                        sink.error(ex);
                    }
                }
            }
        });
    }

    private String getCloudFlareUrl(String apiKey, String model, Request.Builder rb, boolean stream) {
        if (this.llmProvider == LLMProvider.GOOGLE_2 && StringUtils.isNotEmpty(config.getUrl())) {
            rb.addHeader("x-goog-api-key", apiKey);
        }
        String url = getApiUrl(apiKey, stream);
        //使用google的需要把模型拼进去
        if (this.llmProvider == LLMProvider.GOOGLE_2) {
            url = url.formatted(model);
        }
        return url;
    }

    private String getCloudFlareUrl(String apiKey, String model, Request.Builder rb) {
        return getCloudFlareUrl(apiKey, model, rb, false);
    }


    public String syncChat(Role role, String str) {
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        String msgId = UUID.randomUUID().toString();
        chat(Lists.newArrayList(AiMessage.builder().role("user").content(str).build()), roleSendMessageConsumer(role, msgId, latch, sb));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String syncChat(Role role, List<AiMessage> messages) {
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        String msgId = UUID.randomUUID().toString();
        chat(messages, roleSendMessageConsumer(role, msgId, latch, sb));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String syncChat(Role role, List<AiMessage> messages, String systemPrompt) {
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        String msgId = UUID.randomUUID().toString();
        chat(messages, roleSendMessageConsumer(role, msgId, latch, sb), systemPrompt);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private BiConsumer<String, JsonObject> roleSendMessageConsumer(Role role, String msgId, CountDownLatch latch, StringBuilder sb) {
        return ((c, o) -> {
            String type = o.get("type").getAsString();
            if (type.equals("begin")) {
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_BEGIN).id(msgId).role(role.getName()).build());
            } else if (type.equals("finish") || type.equals("failure")) {
                latch.countDown();
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_END).id(msgId).role(role.getName()).build());
            } else {
                sb.append(o.get("content").getAsString());
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_EVENT).id(msgId).role(role.getName()).content(o.get("content").getAsString()).build());
            }
        });
    }

    public String getModel() {
        if (StringUtils.isNotEmpty(this.config.getModel())) {
            return config.getModel();
        }
        return this.llmProvider.getDefaultModel();
    }

    private static class SSEReader {
        private final BufferedSource source;

        public SSEReader(BufferedSource source) {
            this.source = source;
        }

        public String readLine() throws IOException {
            try {
                // 使用 readUtf8LineStrict 确保读取完整行
                String line = source.readUtf8LineStrict();
                // 如果是空行，继续读取下一行
                while (line != null && line.trim().isEmpty()) {
                    line = source.readUtf8LineStrict();
                }
                return line;
            } catch (IOException e) {
                // 如果到达流的末尾，返回null
                if (source.exhausted()) {
                    return null;
                }
                throw e;
            }
        }
    }


    public String chatWithBot(Role role, String content) {
        return chatWithBot(role, content, new JsonObject());
    }

    public String chatWithBot(Role role, String content, JsonObject params) {
        if (botBridge == null) {
            throw new IllegalStateException("Bot bridge not initialized");
        }
        String result = botBridge.call(content, params);
        role.sendMessage(Message.builder().id(UUID.randomUUID().toString()).role(role.getName()).content(result).build());
        return result;
    }

    public String chatWithBot(Role role, String content, JsonObject params, Function<String, String> responseHandler) {
        if (botBridge == null) {
            throw new IllegalStateException("Bot bridge not initialized");
        }
        String result = botBridge.call(content, params, responseHandler);
        role.sendMessage(Message.builder().id(UUID.randomUUID().toString()).role(role.getName()).content(result).build());
        return result;
    }

    public String transcribeAudio(String filePath, String base64) throws IOException {
        // 将base64解码并写入临时文件
        byte[] audioData = java.util.Base64.getDecoder().decode(base64);
        File file = new File(filePath);
        java.nio.file.Files.write(file.toPath(), audioData);

        // 复用现有的文件处理方法
        return transcribeAudio(filePath, file);
    }

    public String transcribeAudio(String filePath, File file) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // 构建multipart请求
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("audio/mpeg"))) // 更明确的媒体类型
                .addFormDataPart("model", LLMProvider.STEPFUN_ASR.getDefaultModel())
                .addFormDataPart("response_format", "json")
                .build();

        Request request = new Request.Builder()
                .url(LLMProvider.STEPFUN_ASR.getUrl()) // 使用完整的API URL
                .addHeader("Authorization", "Bearer " + getToken())
                .post(requestBody)
                .build();

        Stopwatch sw = Stopwatch.createStarted();
        String res = "";

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("音频转写失败: " + response);
            }
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            res = jsonResponse.get("text").getAsString();
            return res;
        } finally {
            log.info("音频转写完成:\n{}\n 耗时:{}ms", res, sw.elapsed(TimeUnit.MILLISECONDS));
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("无法删除临时音频文件: {}", filePath);
                }
            }
        }
    }

    // 添加一个新方法，用于从多个URL中随机选择一个
    private String getRandomUrl(String[] urls) {
        if (urls == null || urls.length == 0) {
            return "";
        }
        // 随机选择一个URL
        int randomIndex = (int) (Math.random() * urls.length);
        return urls[randomIndex].trim(); // 去除可能的空白字符
    }

    /*********************************** 增强的调用方法系列 ***********************************/
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_USER = "user";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LLMCompoundMsg {
        private String role; // 角色: assistant, user
        private String content; // 文本内容
        private List<LLMPart> parts; // 消息内容
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LLMPart {
        String type; // 内容类型，text, image
        String text; // 文本内容
        String data; // 数据内容
        String mimeType; // 媒体类型
    }

    /**
     * 同步调用LLM，发送文本和图像输入，并返回结果
     *
     * @param llm       LLM实例
     * @param msg       消息
     * @param sysPrompt 系统提示
     * @return 结果字符串
     */
    public String call(LLMPart msg, String sysPrompt) {
        JsonObject req = getReq(this, msg);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = this.chatCompletion(messages, sysPrompt);
        log.info("{}", result);
        return result;

    }

    /**
     * 同步调用LLM，发送文本和图像输入，并返回结果
     *
     * @param sysPrompt 系统提示
     * @return 结果字符串
     */
    public String call(LLMCompoundMsg msg, String sysPrompt) {
        JsonObject req = getReq(this, msg);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = this.chatCompletion(messages, sysPrompt);
        log.info("{}", result);
        return result;

    }

    /**
     * 流式调用LLM，发送文本和图像输入，并返回结果
     *
     * @param role         角色实例
     * @param systemPrompt 系统提示
     * @return 结果字符串
     */
    public String callStream(Role role, LLMCompoundMsg msg, String systemPrompt) {
        JsonObject req = getReq(this, msg);
        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = this.syncChat(role, messages, systemPrompt);
        log.info("{}", result);
        return result;

    }

    // TODO: 2025/2/17 实现mcp的流式调用
    public String callStreamWithMcp(LLM llm, LLMCompoundMsg msg, String systemPrompt) {

        return null;
    }

    /**
     * 获取LLM请求对象
     *
     * @param llm LLM实例
     * @return 请求对象
     */
    public JsonObject getReq(LLM llm, LLMCompoundMsg msg) {
        JsonObject req = new JsonObject();

        if (llm.getConfig().getLlmProvider() == LLMProvider.GOOGLE_2) {
            JsonArray parts = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("text", msg.getContent());
            parts.add(obj);

            if (msg.getParts() != null && !msg.getParts().isEmpty()) {
                msg.getParts().forEach(part -> {
                    JsonObject obj2 = new JsonObject();
                    JsonObject objImg = new JsonObject();
                    objImg.addProperty("mime_type", part.getMimeType());
                    objImg.addProperty("data", part.getData());
                    obj2.add("inline_data", objImg);
                    parts.add(obj2);
                });
            }

            req.add("parts", parts);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER
                || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", "user");
            JsonArray array = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", "text");
            obj1.addProperty("text", msg.getContent());
            array.add(obj1);

            if (msg.getParts() != null && !msg.getParts().isEmpty()) {
                msg.getParts().forEach(part -> {
                    JsonObject obj2 = new JsonObject();
                    obj2.addProperty("type", "image_url");
                    JsonObject imgObj = new JsonObject();
                    if (!part.getData().startsWith("data:image")) {
                        imgObj.addProperty("url", "data:image/jpeg;base64," + part.getData());
                    } else {
                        imgObj.addProperty("url", part.getData());
                    }
                    obj2.add("image_url", imgObj);
                    array.add(obj2);
                });
            }
            req.add("content", array);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.CLAUDE_COMPANY) {
            req.addProperty("role", "user");
            JsonArray contentJsons = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", "text");
            obj1.addProperty("text", msg.getContent());
            contentJsons.add(obj1);

            if (msg.getParts() != null && !msg.getParts().isEmpty()) {
                msg.getParts().forEach(part -> {
                    JsonObject obj2 = new JsonObject();
                    obj2.addProperty("type", "image");
                    JsonObject source = new JsonObject();
                    source.addProperty("type", "base64");
                    source.addProperty("media_type", part.getMimeType());
                    source.addProperty("data", part.getData());
                    obj2.add("source", source);
                    contentJsons.add(obj2);
                });
            }
            req.add("content", contentJsons);
        }
        return req;
    }

    private JsonObject getReq(LLM llm, LLMPart llmPart) {
        JsonObject req = new JsonObject();

        if (llm.getConfig().getLlmProvider() == LLMProvider.GOOGLE_2) {
            JsonArray parts = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("text", llmPart.getText());
            parts.add(obj);

            if (TYPE_IMAGE.equals(llmPart.getType()) && StringUtils.isNotEmpty(llmPart.getData())) {
                JsonObject obj2 = new JsonObject();
                JsonObject objImg = new JsonObject();
                objImg.addProperty("mime_type", llmPart.getMimeType());
                objImg.addProperty("data", llmPart.getData());
                obj2.add("inline_data", objImg);
                parts.add(obj2);
            }

            req.add("parts", parts);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER
                || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", "user");
            JsonArray array = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", TYPE_TEXT);
            obj1.addProperty("text", llmPart.getText());
            array.add(obj1);

            if (TYPE_IMAGE.equals(llmPart.getType()) && StringUtils.isNotEmpty(llmPart.getData())) {
                JsonObject obj2 = new JsonObject();
                obj2.addProperty("type", "image_url");
                JsonObject img = new JsonObject();
                img.addProperty("url", "data:image/jpeg;base64," + llmPart.getData());
                obj2.add("image_url", img);
                array.add(obj2);
            }

            req.add("content", array);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.CLAUDE_COMPANY) {
            req.addProperty("role", "user");
            JsonArray contentJsons = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type", TYPE_TEXT);
            obj1.addProperty("text", llmPart.getText());
            contentJsons.add(obj1);

            if (TYPE_IMAGE.equals(llmPart.getType()) && StringUtils.isNotEmpty(llmPart.getData())) {
                JsonObject obj2 = new JsonObject();
                obj2.addProperty("type", "image");
                JsonObject source = new JsonObject();
                source.addProperty("type", "base64");
                source.addProperty("media_type", llmPart.getMimeType());
                source.addProperty("data", llmPart.getData());
                obj2.add("source", source);
                contentJsons.add(obj2);
            }
            req.add("content", contentJsons);
        }


        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENAICOMPATIBLE) {

        }
        return req;
    }

    private JsonObject getGeminiJsonObject(LLMPart p) {
        JsonObject part = new JsonObject();
        if (TYPE_IMAGE.equals(p.getType())) {
            JsonObject inline = new JsonObject();
            inline.addProperty("mime_type", p.getMimeType());
            inline.addProperty("data", p.getData());
            part.add("inline_data", inline);
        } else {
            part.addProperty("text", p.getText());
        }
        return part;
    }

    private JsonObject getOpenaiJsonObject(LLMPart p) {
        JsonObject part = new JsonObject();
        if (TYPE_IMAGE.equals(p.getType())) {
            part.addProperty("type", "image_url");
            JsonObject inline = new JsonObject();
            inline.addProperty("url", String.format("data:%s;base64,%s", p.getMimeType(), p.getData()));
            part.add("image_url", inline);
        } else {
            part.addProperty("type", "text");
            part.addProperty("text", p.getText());
        }
        return part;
    }
    /***************************************************************************************/
}