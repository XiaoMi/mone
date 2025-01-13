package run.mone.hive.llm;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import org.beetl.ext.fn.Json;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@Slf4j
public class LLM {

    protected LLMConfig config;

    private LLMProvider llmProvider;

    private Gson gson = new Gson();

    private boolean google = false;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public LLM(LLMConfig config) {
        this.config = config;
        this.llmProvider = config.getLlmProvider();
    }

    public String chat(String prompt) {
        return ask(prompt).join();
    }


    public String chat(List<AiMessage> msgList) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), "", config);
    }

    public String chat(List<AiMessage> msgList, String systemPrompt) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), systemPrompt, config);
    }

    public String chat(List<AiMessage> msgList, LLMConfig config) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), "", config);
    }


    public String getApiUrl() {
        return llmProvider.getUrl();
    }


    public CompletableFuture<String> ask(String prompt) {
        if (config.isDebug()) {
            return CompletableFuture.completedFuture("res:" + prompt);
        } else {
            return CompletableFuture.completedFuture(chatCompletion(System.getenv(llmProvider.getEnvName()), prompt, llmProvider.getDefaultModel()));
        }
    }

    public String chatCompletion(String apiKey, String content, String model) {
        return chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role("user").content(content).build()), model, "", config);
    }

    @SneakyThrows
    public String chatCompletion(String apiKey, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
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

        for (AiMessage message : messages) {
            msgArray.add(createMessageObject(message.getRole(), message.getContent()));
        }



        requestBody.add("messages", msgArray);
        String apiUrl = getApiUrl();

        String rb = requestBody.toString();

        log.info("call llm:{}\nmessage:{}\n", model, rb);
        Stopwatch sw = Stopwatch.createStarted();
        String res = "";

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(rb, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            res = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            return res;
        } finally {
            log.info("call llm res:\n{}\n use time:{}ms", res, sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private JsonObject createMessageObject(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }


    public void chat(List<AiMessage> messages, BiConsumer<String, JsonObject> messageHandlerr) {
        chatCompletionStream(System.getenv(llmProvider.getEnvName()), messages, llmProvider.getDefaultModel(), messageHandlerr, line -> {
        });
    }


    public void chatCompletionStream(String apiKey, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("stream", true);

        JsonArray msgArray = new JsonArray();

        for (AiMessage message : messages) {
            msgArray.add(createMessageObject(message.getRole(), message.getContent()));
        }

        requestBody.add("messages", gson.toJsonTree(msgArray));

        Request request = new Request.Builder()
                .url(getApiUrl())
                .addHeader("Authorization", "Bearer " + apiKey)
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
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
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                JsonObject jsonResponse = new JsonObject();
                                jsonResponse.addProperty("type", "finish");
                                jsonResponse.addProperty("content", "[DONE]");
                                messageHandler.accept("[DONE]", jsonResponse);
                                break;
                            }
                            JsonObject jsonResponse = gson.fromJson(data, JsonObject.class);
                            String content = jsonResponse.getAsJsonArray("choices")
                                    .get(0).getAsJsonObject()
                                    .getAsJsonObject("delta")
                                    .get("content").getAsString();
                            jsonResponse.addProperty("type", "event");
                            jsonResponse.addProperty("content", content);
                            messageHandler.accept(content, jsonResponse);
                        }
                    }
                    System.out.println("FINISH");
                } catch (Throwable ex) {
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("type", "failure");
                    jsonResponse.addProperty("content", "");
                    messageHandler.accept(ex.getMessage(), jsonResponse);
                }
            }
        });
    }


    public String syncChat(Role role, String str) {
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        String msgId = UUID.randomUUID().toString();
        chat(Lists.newArrayList(AiMessage.builder().role("user").content(str).build()), (c, o) -> {
            String type = o.get("type").getAsString();
            if (type.equals("begin")){
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_BEGIN).id(msgId).role(role.getName()).build());
            } else if (type.equals("finish") || type.equals("failure")) {
                latch.countDown();
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_END).id(msgId).role(role.getName()).build());
            } else {
                sb.append(o.get("content").getAsString());
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_EVENT).id(msgId).role(role.getName()).content(o.get("content").getAsString()).build());
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
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


}