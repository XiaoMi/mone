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
import run.mone.hive.llm.impl.minmax.MiniMax;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.ClaudeCacheControlHelper;
import run.mone.hive.utils.ModelInfoUtils;

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
import java.util.stream.Collectors;

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

    // cloudml上训练的分类模型
    /*
     * modelType 模型类型 bert or qwen
     * version 模型版本
     * texts 待分类文本列表
     * topK 返回topK个分类结果
     * */
    public String getClassifyScore(String modelType, String version, List<String> texts, Integer topK, String releaseServiceName) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model_type", modelType);
            requestBody.addProperty("version", version);
            requestBody.add("texts", gson.toJsonTree(texts));
            requestBody.addProperty("top_k", topK);
            if (StringUtils.isNotEmpty(releaseServiceName)) {
                requestBody.addProperty("releaseServiceName", releaseServiceName);
            }

            String url = this.config.getUrl();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            String rb = requestBody.toString();
            log.info("call classify api:{}\nrequest:{}\n", url, rb);
            Stopwatch sw = Stopwatch.createStarted();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                String responseBody = response.body().string();
                log.info("classify response:{}", responseBody);
                return responseBody;
            } finally {
                log.info("call classify api use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            log.error("调用接口失败, modelType:{}, version:{}, texts:{}, topK:{}, error:{}",
                    modelType, version, texts, topK, e.getMessage(), e);
            throw new RuntimeException("接口调用失败: " + e.getMessage(), e);
        }
    }

    // RAG新增接口
    /*
     * id 记录ID
     * question 问题
     * content 内容
     * askMark 询问标记
     * askSpeechSkill 询问语音技能
     * serviceType 服务类型
     * conclusion 结论
     * blockId 块ID
     * tenant 租户
     * */
    public String addRag(String id, String question, String content, Integer askMark,
                         String askSpeechSkill, String serviceType, String conclusion,
                         String blockId, String tenant) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("id", id);
            requestBody.addProperty("question", question);
            requestBody.addProperty("content", content);
            requestBody.addProperty("askMark", askMark);
            requestBody.addProperty("askSpeechSkill", askSpeechSkill);
            requestBody.addProperty("serviceType", serviceType);
            requestBody.addProperty("conclusion", conclusion);
            requestBody.addProperty("blockId", blockId);
            requestBody.addProperty("tenant", tenant);

            String url = this.config.getUrl();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            String rb = requestBody.toString();
            log.info("call rag add api:{}\nrequest:{}\n", url, rb);
            Stopwatch sw = Stopwatch.createStarted();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                String responseBody = response.body().string();
                log.info("rag add response:{}", responseBody);
                return responseBody;
            } finally {
                log.info("call rag add api use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            log.error("调用RAG新增接口失败, question:{}, content:{}, tenant:{}, error:{}",
                    question, content, tenant, e.getMessage(), e);
            throw new RuntimeException("RAG新增接口调用失败: " + e.getMessage(), e);
        }
    }

    // RAG查询接口
    /*
     * query 查询内容
     * topK 返回topK个结果
     * threshold 阈值
     * tag 标签
     * tenant 租户
     * */
    public String queryRag(String query, Integer topK, Double threshold, String tag, String tenant) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("query", query);
            requestBody.addProperty("topK", topK);
            requestBody.addProperty("threshold", threshold);
            requestBody.addProperty("tag", tag);
            requestBody.addProperty("tenant", tenant);

            String url = this.config.getUrl();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            String rb = requestBody.toString();
            log.info("call rag query api:{}\nrequest:{}\n", url, rb);
            Stopwatch sw = Stopwatch.createStarted();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                String responseBody = response.body().string();
                log.info("rag query response:{}", responseBody);
                return responseBody;
            } finally {
                log.info("call rag query api use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            log.error("调用RAG查询接口失败, query:{}, topK:{}, threshold:{}, tenant:{}, error:{}",
                    query, topK, threshold, tenant, e.getMessage(), e);
            throw new RuntimeException("RAG查询接口调用失败: " + e.getMessage(), e);
        }
    }

    // RAG ID查询接口
    /*
     * questionId 问题ID
     * contentId 内容ID
     * tenant 租户
     * */
    public String queryRagById(String questionId, String contentId, String tenant) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("questionId", questionId);
            requestBody.addProperty("contentId", contentId);
            requestBody.addProperty("tenant", tenant);

            String url = this.config.getUrl();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            String rb = requestBody.toString();
            log.info("call rag query by id api:{}\nrequest:{}\n", url, rb);
            Stopwatch sw = Stopwatch.createStarted();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                String responseBody = response.body().string();
                log.info("rag query by id response:{}", responseBody);
                return responseBody;
            } finally {
                log.info("call rag query by id api use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            log.error("调用RAG ID查询接口失败, questionId:{}, contentId:{}, tenant:{}, error:{}",
                    questionId, contentId, tenant, e.getMessage(), e);
            throw new RuntimeException("RAG ID查询接口调用失败: " + e.getMessage(), e);
        }
    }

    // 新知识库查询接口
    /*
     * query 查询内容
     * knowledgeBaseId 知识库ID
     * */
    public String queryNewKnowledgeBase(String query, String knowledgeBaseId, String apiKey) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(150, TimeUnit.SECONDS)
                    .writeTimeout(150, TimeUnit.SECONDS)
                    .readTimeout(150, TimeUnit.SECONDS)
                    .build();

            // 构建请求体
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("query", query);
            requestBody.addProperty("knowledge_base_id", knowledgeBaseId);
            requestBody.addProperty("simple", true);

            String url = this.config.getUrl();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .addHeader("Content-Type", "application/json");

            if (StringUtils.isNotEmpty(apiKey)) {
                requestBuilder.addHeader("X-API-Key", apiKey);
            }

            Request request = requestBuilder.build();

            String rb = requestBody.toString();
            log.info("call new knowledge base query api:{}\nrequest:{}\n", url, rb);
            Stopwatch sw = Stopwatch.createStarted();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                String responseBody = response.body().string();
                log.info("new knowledge base query response:{}", responseBody);
                return responseBody;
            } finally {
                log.info("call new knowledge base query api use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            log.error("调用新知识库查询接口失败, query:{}, knowledgeBaseId:{}, error:{}",
                    query, knowledgeBaseId, e.getMessage(), e);
            throw new RuntimeException("新知识库查询接口调用失败: " + e.getMessage(), e);
        }
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
        return chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role(ROLE_USER).content(content).build()), model, "", config);
    }


    public String ask(List<AiMessage> messages) {
        return chatCompletion(getToken(), messages, getModel(), "", this.config);
    }

    public String chatCompletion(List<AiMessage> messages, String systemInstruction) {
        return chatCompletion(getToken(), messages, getModel(), systemInstruction, this.config);
    }

    public String chatCompletion(String apiKey, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        return chatCompletion(apiKey, CustomConfig.DUMMY, messages, model, systemPrompt, clientConfig);
    }


    @SneakyThrows
    public String chatCompletion(String apiKey, CustomConfig customConfig, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        LLMChatCompletionResult result = chatCompletionWithUsage(apiKey, customConfig, messages, model, systemPrompt, clientConfig);
        return result.getResult();
    }

    public LLMChatCompletionResult chatCompletionWithUsage(String apiKey, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        return chatCompletionWithUsage(apiKey, CustomConfig.DUMMY, messages, model, systemPrompt, clientConfig);
    }

    @SneakyThrows
    public LLMChatCompletionResult chatCompletionWithUsage(String apiKey, CustomConfig customConfig, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();

        JsonObject requestBody = new JsonObject();
        if (StringUtils.isNotEmpty(model)) {
            requestBody.addProperty("model", model);
        }

        if (this.llmProvider == LLMProvider.CLAUDE_COMPANY) {
            requestBody.addProperty("anthropic_version", this.config.getVersion());
            requestBody.addProperty("max_tokens", this.config.getMaxTokens());
            requestBody.remove("model");
        }

        // former web search
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

        // live search (xai/grok)
        if (clientConfig.getLiveSearchConfig() != null) {
            requestBody.add("search_parameters", gson.toJsonTree(clientConfig.getLiveSearchConfig()));
        }


        if (this.config.isStream()) {
            requestBody.addProperty("stream", true);
        }
        //使用json格式返回
        if (clientConfig.isJson()) {
            JsonObject rf = new JsonObject();
            rf.addProperty("type", "json_object");
            requestBody.add("response_format", rf);
        }

        JsonArray msgArray = new JsonArray();

//        if (this.llmProvider != LLMProvider.GOOGLE_2) {
//            if (this.config.isJson() || clientConfig.isJson()) {
//                String jsonSystemPrompt = """
//                         返回结果请用JSON返回(如果用户没有指定json格式,则直接返回{"content":$res}),thx
//                        """;
//                JsonObject rf = new JsonObject();
//                rf.addProperty("type", "json_object");
//                requestBody.add("response_format", rf);
//                msgArray.add(createMessageObject("system", jsonSystemPrompt));
//            } else {
//                if (StringUtils.isNotEmpty(systemPrompt)) {
//                    msgArray.add(createMessageObject("system", systemPrompt));
//                }
//            }
//        }

        //gemini的系统提示词
        if (llmProvider == LLMProvider.GOOGLE_2 && StringUtils.isNotEmpty(systemPrompt)) {
            JsonObject system_instruction = new JsonObject();
            JsonObject text = new JsonObject();
            text.addProperty("text", systemPrompt);
            system_instruction.add("parts", text);
            requestBody.add("system_instruction", system_instruction);
        }

        if (this.llmProvider == LLMProvider.CLAUDE_COMPANY) {
            requestBody.addProperty("anthropic_version", this.config.getVersion());
            requestBody.addProperty("max_tokens", this.config.getMaxTokens());
        }

        for (AiMessage message : messages) {
            //使用openrouter,并且使用多模态
            if ((this.llmProvider == LLMProvider.OPENROUTER
                    || this.llmProvider == LLMProvider.MOONSHOT
                    || this.llmProvider == LLMProvider.DOUBAO
                    || this.llmProvider == LLMProvider.QWEN
                    || this.llmProvider == LLMProvider.MIFY
                    || this.llmProvider == LLMProvider.MIFY_GATEWAY
            ) && null != message.getJsonContent()) {
                msgArray.add(message.getJsonContent());
            } else if (this.llmProvider == LLMProvider.GOOGLE_2) {
                msgArray.add(createMessageObjectForGoogle(message));
            } else {
                msgArray.add(createMessageObject(message.getRole(), message.getContent()));
            }
        }

        // Apply cache control for Claude models on OpenRouter
        ClaudeCacheControlHelper.applyCacheControlToUserMessages(msgArray, model, this.llmProvider, customConfig);

        requestBody.add(getContentsName(), msgArray);
        if (this.llmProvider == LLMProvider.OPENROUTER) {
            JsonObject usage = new JsonObject();
            usage.addProperty("include", true);
            requestBody.add("usage", usage);
        }

        Request.Builder requestBuilder = new Request.Builder();

        // 设置api key
        if (this.llmProvider != LLMProvider.GOOGLE_2) {
            if (this.llmProvider == LLMProvider.CLAUDE_COMPANY) {
                requestBuilder.addHeader("Authorization", "Bearer " + getClaudeKey(getClaudeName()));
            } else {
                requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
            }
        }

        // 设置MIFY_GATEWAY相关header, 并覆盖model
        if (this.llmProvider == LLMProvider.MIFY_GATEWAY && customConfig != CustomConfig.DUMMY) {
            customConfig.getCustomHeaders().forEach((key, value) -> {
                requestBuilder.addHeader(key, value);
            });
            requestBody.addProperty("model", customConfig.getModel());
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
                res = content.get("parts").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                return LLMChatCompletionResult.builder().result(res).usage(null).build();
            }

            if (llmProvider == LLMProvider.CLAUDE_COMPANY) {
                res = jsonResponse.get("content").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
                return LLMChatCompletionResult.builder().result(res).usage(null).build();
            }

            //openai那个流派的
            res = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            LLMUsage usage = null;
            if (jsonResponse.has("usage") && jsonResponse.get("usage").isJsonObject()) {
                JsonObject usageJson = jsonResponse.getAsJsonObject("usage");
                LLMUsage.LLMUsageBuilder usageBuilder = LLMUsage.builder();

                // Common fields
                int inputTokens = usageJson.has("prompt_tokens") ? usageJson.get("prompt_tokens").getAsInt() : 0;
                int outputTokens = usageJson.has("completion_tokens") ? usageJson.get("completion_tokens").getAsInt() : 0;
                usageBuilder.inputTokens(inputTokens).outputTokens(outputTokens);

                // Provider-specific fields
                if (llmProvider == LLMProvider.OPENROUTER) {
                    Double totalCost = usageJson.has("cost") ? usageJson.get("cost").getAsDouble() : null;
                    usageBuilder.totalCost(totalCost);

                    Integer cacheReadTokens = null;
                    if (usageJson.has("prompt_tokens_details")) {
                        JsonElement promptTokensDetailsEl = usageJson.get("prompt_tokens_details");
                        if (promptTokensDetailsEl.isJsonObject()) {
                            JsonObject promptTokensDetails = promptTokensDetailsEl.getAsJsonObject();
                            if (promptTokensDetails.has("cached_tokens")) {
                                cacheReadTokens = promptTokensDetails.get("cached_tokens").getAsInt();
                            }
                        }
                    }
                    usageBuilder.cacheReadTokens(cacheReadTokens);

                    Integer thoughtsTokenCount = null;
                    if (usageJson.has("completion_tokens_details")) {
                        JsonElement completionTokensDetailsEl = usageJson.get("completion_tokens_details");
                        if (completionTokensDetailsEl.isJsonObject()) {
                            JsonObject completionTokensDetails = completionTokensDetailsEl.getAsJsonObject();
                            if (completionTokensDetails.has("reasoning_tokens")) {
                                thoughtsTokenCount = completionTokensDetails.get("reasoning_tokens").getAsInt();
                            }
                        }
                    }
                    usageBuilder.thoughtsTokenCount(thoughtsTokenCount);
                    usageBuilder.cacheWriteTokens(null); // Not provided by OpenRouter
                } else if (llmProvider == LLMProvider.DEEPSEEK) {
                    Integer cacheReadTokens = usageJson.has("prompt_cache_hit_tokens") ? usageJson.get("prompt_cache_hit_tokens").getAsInt() : null;
//                    Integer cacheWriteTokens = usageJson.has("prompt_cache_miss_tokens") ? usageJson.get("prompt_cache_miss_tokens").getAsInt() : null;
                    usageBuilder.cacheReadTokens(cacheReadTokens);
//                    usageBuilder.cacheWriteTokens(cacheWriteTokens);
                    usageBuilder.totalCost(null);
                    usageBuilder.thoughtsTokenCount(null);
                }

                Integer tokenLimit = ModelInfoUtils.getModelInfo(llmProvider, model)
                        .map(ModelInfoUtils.ModelInfo::getTokenLimit)
                        .orElse(null);
                usageBuilder.tokenLimit(tokenLimit);

                usage = usageBuilder.build();
            }

            return LLMChatCompletionResult.builder().result(res).usage(usage).build();

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
        //使用minmax
        if (this.llmProvider.equals(LLMProvider.MINIMAX)) {
            return new MiniMax().generateAudio(this.llmProvider.getCustomModelEnv(), this.llmProvider.getEnvName(), text);
        }
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
                getModel(),
                messageHandlerr,
                line -> {
                },
                "");
    }

    public void chat(List<AiMessage> messages, BiConsumer<String, JsonObject> messageHandlerr, String systemPrompt) {
        chatCompletionStream(System.getenv(llmProvider.getEnvName()),
                messages,
                getModel(),
                messageHandlerr,
                line -> {
                },
                systemPrompt
        );
    }

    public void chat(List<AiMessage> messages, BiConsumer<String, JsonObject> messageHandlerr, String systemPrompt, CustomConfig customConfig) {
        chatCompletionStream(getToken(),
                customConfig,
                messages,
                getModel(),
                messageHandlerr,
                line -> {
                },
                systemPrompt,
                null,
                u -> {
                }
        );
    }

    public void chatWithUsage(List<AiMessage> messages, BiConsumer<String, JsonObject> messageHandler, String systemPrompt, CustomConfig customConfig, Consumer<LLMUsage> usageConsumer) {
        chatCompletionStream(getToken(),
                customConfig,
                messages,
                getModel(),
                messageHandler,
                line -> {
                },
                systemPrompt,
                null,
                usageConsumer
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

    public Flux<String> call(List<AiMessage> messages, String systemPrompt, CustomConfig customConfig) {
        return Flux.create(sink -> chatCompletionStream(getToken(),
                customConfig,
                messages,
                getModel(),
                (a, b) -> {
                },
                (a) -> {
                },
                systemPrompt,
                sink));
    }

    public Flux<String> call(List<AiMessage> messages, String systemPrompt) {
        return Flux.create(sink -> chatCompletionStream(getToken(),
                messages,
                getModel(),
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
        chatCompletionStream(apiKey, CustomConfig.DUMMY, messages, model,
                messageHandler, lineConsumer, systemPrompt, sink,
                u -> {
                    sink.next(llmUsageContent(u));
                });
    }

    private String llmUsageContent(LLMUsage usage) {
        return "\n<chat>\n<usage>\n" + gson.toJson(usage) + "\n</usage>\n</chat>\n";
    }

    public void chatCompletionStream(String apiKey, CustomConfig customConfig, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer, String systemPrompt, FluxSink<String> sink) {
        chatCompletionStream(apiKey, customConfig, messages, model, messageHandler, lineConsumer, systemPrompt, sink, u -> {
        });
    }

    public void chatCompletionStream(String apiKey, CustomConfig customConfig, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer, String systemPrompt, FluxSink<String> sink, Consumer<LLMUsage> usageConsumer) {
        JsonObject requestBody = new JsonObject();

        if (this.llmProvider != LLMProvider.GOOGLE_2
                && this.llmProvider != LLMProvider.CLAUDE_COMPANY) {
            requestBody.addProperty("model", model);
            requestBody.addProperty("stream", true);

            if (null != this.config.getTemperature()) {
                requestBody.addProperty("temperature", this.config.getTemperature());
            }
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
            if ((this.llmProvider == LLMProvider.OPENROUTER ||
                    this.llmProvider == LLMProvider.MOONSHOT ||
                    this.llmProvider == LLMProvider.KIMI_K2_TURBO_PREVIEW ||
                    this.llmProvider == LLMProvider.DOUBAO_DEEPSEEK_V3 ||
                    this.llmProvider == LLMProvider.DEEPSEEK ||
                    this.llmProvider == LLMProvider.DOUBAO_UI_TARS ||
                    this.llmProvider == LLMProvider.DOUBAO_VISION ||
                    this.llmProvider == LLMProvider.GROK ||
                    this.llmProvider == LLMProvider.DOUBAO ||
                    this.llmProvider == LLMProvider.QWEN ||
                    this.llmProvider == LLMProvider.MIFY ||
                    this.llmProvider == LLMProvider.MIFY_GATEWAY ||
                    this.llmProvider == LLMProvider.CLAUDE_COMPANY) && null != message.getJsonContent()) {
                msgArray.add(message.getJsonContent());
            } else if (this.llmProvider == LLMProvider.GOOGLE_2) {
                msgArray.add(createMessageObjectForGoogle(message, message.getRole(), message.getContent()));
            } else {
                msgArray.add(createMessageObject(message.getRole(), message.getContent()));
            }
        }

        // Apply cache control for Claude models on OpenRouter
        ClaudeCacheControlHelper.applyCacheControlToUserMessages(msgArray, model, this.llmProvider, customConfig);

        requestBody.add(getContentsName(), gson.toJsonTree(msgArray));

        // openai 系列的应该都可以
        if (this.llmProvider == LLMProvider.OPENROUTER || this.llmProvider == LLMProvider.DEEPSEEK) {
            JsonObject usage = new JsonObject();
            usage.addProperty("include_usage", true);
            requestBody.add("stream_options", usage);
        }

        // 设置关闭思考模型的思考能力
        if (!config.isReasoningOutPut()) {
            // 各个模型关闭思考能力的数据结构
            if (this.llmProvider == LLMProvider.DOUBAO_VISION) {
                JsonObject thinkingType = new JsonObject();
                thinkingType.addProperty("type", "disabled");
                requestBody.add("thinking", thinkingType);
            }
        }

        Request.Builder rb = new Request.Builder();

        // 设置API key
        if (this.llmProvider != LLMProvider.GOOGLE_2) {
            if (this.llmProvider == LLMProvider.CLAUDE_COMPANY) {
                rb.addHeader("Authorization", "Bearer " + getClaudeKey(getClaudeName()));
            } else if (this.llmProvider == LLMProvider.MIFY) {
                rb.addHeader("api-key", apiKey);
            } else {
                rb.addHeader("Authorization", "Bearer " + apiKey);
            }
        }

        // 设置MIFY_GATEWAY相关header
        if (this.llmProvider == LLMProvider.MIFY_GATEWAY && customConfig != CustomConfig.DUMMY) {
            customConfig.getCustomHeaders().forEach((key, value) -> {
                rb.addHeader(key, value);
            });
            requestBody.addProperty("model", customConfig.getModel());
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

                                if ("message_start".equals(jsonResponse.get("type").getAsString())) {
                                    JsonObject msg = jsonResponse.get("message").getAsJsonObject();
                                    if (msg.has("usage")) {
                                        JsonObject usageJson = msg.getAsJsonObject("usage");
                                        LLMUsage.LLMUsageBuilder usageBuilder = LLMUsage.builder();

                                        // Common fields
                                        int inputTokens = usageJson.has("input_tokens") ? usageJson.get("input_tokens").getAsInt() : 0;
                                        int outputTokens = usageJson.has("output_tokens") ? usageJson.get("output_tokens").getAsInt() : 0;
                                        usageBuilder.inputTokens(inputTokens).outputTokens(outputTokens);
                                        Integer cacheReadTokens = usageJson.has("cache_read_input_tokens") ? usageJson.get("cache_read_input_tokens").getAsInt() : null;
                                        Integer cacheWriteTokens = usageJson.has("cache_creation_input_tokens") ? usageJson.get("cache_creation_input_tokens").getAsInt() : null;
                                        usageBuilder.cacheReadTokens(cacheReadTokens);
                                        usageBuilder.cacheWriteTokens(cacheWriteTokens);
                                        usageBuilder.totalCost(null);
                                        usageBuilder.thoughtsTokenCount(null);
                                        Integer tokenLimit = ModelInfoUtils.getModelInfo(llmProvider, model)
                                                .map(ModelInfoUtils.ModelInfo::getTokenLimit)
                                                .orElse(null);
                                        usageBuilder.tokenLimit(tokenLimit);

                                        LLMUsage usage = usageBuilder.build();
                                        if (null != usageConsumer) {
                                            usageConsumer.accept(usage);
                                        }
                                    }
                                }

                                if ("message_start".equals(jsonResponse.get("type").getAsString())
                                        || "ping".equals(jsonResponse.get("type").getAsString())
                                        || "content_block_start".equals(jsonResponse.get("type").getAsString())) {
                                    continue;
                                }

                                if ("message_delta".equals(jsonResponse.get("type").getAsString())) {
                                    if (jsonResponse.has("usage")) {
                                        JsonObject usageJson = jsonResponse.getAsJsonObject("usage");
                                        LLMUsage.LLMUsageBuilder usageBuilder = LLMUsage.builder();
                                        int inputTokens = usageJson.has("input_tokens") ? usageJson.get("input_tokens").getAsInt() : 0;
                                        int outputTokens = usageJson.has("output_tokens") ? usageJson.get("output_tokens").getAsInt() : 0;
                                        usageBuilder.inputTokens(inputTokens).outputTokens(outputTokens);
                                        LLMUsage usage = usageBuilder.build();
                                        if (null != usageConsumer) {
                                            usageConsumer.accept(usage);
                                        }
                                    }

                                    JsonObject jsonRes = new JsonObject();
                                    jsonRes.addProperty("type", "finish");
                                    jsonRes.addProperty("content", jsonResponse.get("delta").getAsJsonObject().get("stop_reason").getAsString());
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

                                if (jsonResponse.has("usage") && jsonResponse.get("usage").isJsonObject()) {
                                    JsonObject usageJson = jsonResponse.getAsJsonObject("usage");
                                    LLMUsage.LLMUsageBuilder usageBuilder = LLMUsage.builder();

                                    // Common fields
                                    int inputTokens = usageJson.has("prompt_tokens") ? usageJson.get("prompt_tokens").getAsInt() : 0;
                                    int outputTokens = usageJson.has("completion_tokens") ? usageJson.get("completion_tokens").getAsInt() : 0;
                                    usageBuilder.inputTokens(inputTokens).outputTokens(outputTokens);

                                    // Provider-specific fields
                                    if (llmProvider == LLMProvider.OPENROUTER) {
                                        Double totalCost = usageJson.has("cost") ? usageJson.get("cost").getAsDouble() : null;
                                        usageBuilder.totalCost(totalCost);

                                        Integer cacheReadTokens = null;
                                        if (usageJson.has("prompt_tokens_details")) {
                                            JsonElement promptTokensDetailsEl = usageJson.get("prompt_tokens_details");
                                            if (promptTokensDetailsEl.isJsonObject()) {
                                                JsonObject promptTokensDetails = promptTokensDetailsEl.getAsJsonObject();
                                                if (promptTokensDetails.has("cached_tokens")) {
                                                    cacheReadTokens = promptTokensDetails.get("cached_tokens").getAsInt();
                                                }
                                            }
                                        }
                                        usageBuilder.cacheReadTokens(cacheReadTokens);

                                        Integer thoughtsTokenCount = null;
                                        if (usageJson.has("completion_tokens_details")) {
                                            JsonElement completionTokensDetailsEl = usageJson.get("completion_tokens_details");
                                            if (completionTokensDetailsEl.isJsonObject()) {
                                                JsonObject completionTokensDetails = completionTokensDetailsEl.getAsJsonObject();
                                                if (completionTokensDetails.has("reasoning_tokens")) {
                                                    thoughtsTokenCount = completionTokensDetails.get("reasoning_tokens").getAsInt();
                                                }
                                            }
                                        }
                                        usageBuilder.thoughtsTokenCount(thoughtsTokenCount);
                                        usageBuilder.cacheWriteTokens(null); // Not provided by OpenRouter
                                    } else if (llmProvider == LLMProvider.DEEPSEEK) {
                                        Integer cacheReadTokens = usageJson.has("prompt_cache_hit_tokens") ? usageJson.get("prompt_cache_hit_tokens").getAsInt() : null;
                                        Integer cacheWriteTokens = usageJson.has("prompt_cache_miss_tokens") ? usageJson.get("prompt_cache_miss_tokens").getAsInt() : null;
                                        usageBuilder.cacheReadTokens(cacheReadTokens);
                                        usageBuilder.cacheWriteTokens(cacheWriteTokens);
                                        usageBuilder.totalCost(null);
                                        usageBuilder.thoughtsTokenCount(null);
                                    }

                                    Integer tokenLimit = ModelInfoUtils.getModelInfo(llmProvider, model)
                                            .map(ModelInfoUtils.ModelInfo::getTokenLimit)
                                            .orElse(null);
                                    usageBuilder.tokenLimit(tokenLimit);

                                    LLMUsage usage = usageBuilder.build();
                                    if (null != usageConsumer) {
                                        usageConsumer.accept(usage);
                                    }
                                }

                                String content = "";
                                try {
                                    JsonArray choicesJson = jsonResponse.getAsJsonArray("choices");
                                    if (choicesJson == null || choicesJson.isEmpty()) {
                                        continue;
                                    }
                                    JsonObject delta = choicesJson
                                            .get(0).getAsJsonObject()
                                            .getAsJsonObject("delta");

                                    JsonElement c = delta.get("content");
                                    if ((c.isJsonPrimitive() && StringUtils.isEmpty(c.getAsString())) || c.isJsonNull()) {
                                        // 当Content为空并且设置了不输出思考内容时，直接跳过
                                        if (!config.isReasoningOutPut()) {
                                            continue;
                                        }
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
        chat(Lists.newArrayList(AiMessage.builder().role(ROLE_USER).content(str).build()), roleSendMessageConsumer(role, msgId, latch, sb));
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

    public String syncChat(Role role, List<AiMessage> messages, String systemPrompt, CustomConfig customConfig) {
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        String msgId = UUID.randomUUID().toString();
        chat(messages, roleSendMessageConsumer(role, msgId, latch, sb), systemPrompt, customConfig);
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
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_END).id(msgId).role(role.getName()).content(o.get("content").getAsString()).build());
                latch.countDown();
            } else {
                sb.append(o.get("content").getAsString());
                role.sendMessage(Message.builder().type(StreamMessageType.BOT_STREAM_EVENT).id(msgId).role(role.getName()).content(o.get("content").getAsString()).build());
            }
        });
    }

    public String getModel() {
        if (this.config != null && StringUtils.isNotEmpty(this.config.getModel())) {
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

    /**
     * 意图识别方法
     * 根据用户输入的prompt和提供的分类列表，让AI判断应该使用哪个分类
     *
     * @param prompt     用户输入的文本
     * @param categories 可选的分类列表
     * @return IntentClassificationResult 包含分类结果的对象
     */
    public IntentClassificationResult classifyIntent(String prompt, List<String> categories) {
        if (StringUtils.isEmpty(prompt)) {
            throw new IllegalArgumentException("prompt不能为空");
        }
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("分类列表不能为空");
        }

        try {
            // 构造系统提示词，要求AI返回JSON格式的分类结果
            String systemPrompt = String.format("""
                    你是一个专业的意图识别助手。请根据用户的输入文本，从给定的分类列表中选择最合适的分类。
                    
                    分类列表：%s
                    
                    请严格按照以下JSON格式返回结果：
                    {
                        "selectedCategory": "选中的分类",
                        "confidence": 0.95,
                        "reason": "选择这个分类的原因"
                    }
                    
                    要求：
                    1. selectedCategory 必须是分类列表中的一个
                    2. confidence 是置信度，范围0-1
                    3. reason 简要说明选择理由
                    4. 只返回JSON，不要其他内容
                    """, gson.toJson(categories));

            // 构造消息
            List<AiMessage> messages = Lists.newArrayList(
                    AiMessage.builder().role(ROLE_USER).content(prompt).build()
            );

            // 调用LLM进行分类
            String response = chatCompletion(getToken(), messages, getModel(), systemPrompt, config);

            log.info("意图识别请求 - prompt: {}, categories: {}", prompt, categories);
            log.info("意图识别响应: {}", response);

            // 解析JSON响应
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            String selectedCategory = jsonResponse.get("selectedCategory").getAsString();
            double confidence = jsonResponse.has("confidence") ? jsonResponse.get("confidence").getAsDouble() : 0.0;
            String reason = jsonResponse.has("reason") ? jsonResponse.get("reason").getAsString() : "";

            // 验证选中的分类是否在原始列表中
            if (!categories.contains(selectedCategory)) {
                log.warn("AI选择的分类 '{}' 不在原始分类列表中，使用第一个分类作为默认值", selectedCategory);
                selectedCategory = categories.get(0);
                confidence = 0.5; // 降低置信度
                reason = "AI选择的分类不在列表中，使用默认分类";
            }

            return IntentClassificationResult.builder()
                    .selectedCategory(selectedCategory)
                    .confidence(confidence)
                    .reason(reason)
                    .originalPrompt(prompt)
                    .availableCategories(categories)
                    .build();

        } catch (Exception e) {
            log.error("意图识别失败 - prompt: {}, categories: {}, error: {}", prompt, categories, e.getMessage(), e);

            // 返回默认结果
            return IntentClassificationResult.builder()
                    .selectedCategory(categories.get(0))
                    .confidence(0.0)
                    .reason("意图识别失败，使用默认分类: " + e.getMessage())
                    .originalPrompt(prompt)
                    .availableCategories(categories)
                    .build();
        }
    }

    /**
     * 情感AI主动聊天判断方法
     * 根据聊天记录和当前时间，判断AI是否需要主动和用户聊天
     *
     * @param chatHistory 聊天记录列表，按时间顺序排列
     * @param currentTime 当前时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return EmotionalChatDecisionResult 包含判断结果的对象
     */
    public EmotionalChatDecisionResult shouldInitiateChat(List<String> chatHistory, String currentTime) {
        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
        }
        if (StringUtils.isEmpty(currentTime)) {
            throw new IllegalArgumentException("当前时间不能为空");
        }

        try {
            // 构造系统提示词，让AI作为情感AI来判断是否需要主动聊天
            String systemPrompt = """
                    你是一个具有情感智能的AI助手。请根据用户的聊天记录和当前时间，判断是否需要主动和用户聊天。
                    
                    作为情感AI，你需要考虑以下因素：
                    1. 用户的情绪状态（从聊天记录中分析）
                    2. 聊天的频率和间隔时间
                    3. 用户可能的作息时间和生活规律
                    4. 是否有未完成的话题或关怀需要跟进
                    5. 特殊时间节点（如节假日、生日等）
                    6. 用户是否表现出需要陪伴或支持的迹象
                    
                    判断标准：
                    - 需要主动聊天：用户情绪低落、长时间未聊天、有未完成关怀、特殊时间节点等
                    - 不需要主动聊天：用户状态良好、刚刚聊过、用户可能在休息时间等
                    
                    请严格按照以下JSON格式返回结果：
                    {
                        "shouldInitiate": true/false,
                        "confidence": 0.85,
                        "reason": "判断理由",
                        "suggestedMessage": "如果需要主动聊天，建议发送的消息内容",
                        "emotionalAnalysis": "对用户情绪状态的分析",
                        "timeAnalysis": "对时间因素的分析"
                    }
                    
                    要求：
                    1. shouldInitiate 表示是否需要主动聊天
                    2. confidence 是置信度，范围0-1
                    3. reason 详细说明判断理由
                    4. suggestedMessage 如果需要主动聊天，提供温暖、贴心的消息建议
                    5. emotionalAnalysis 分析用户的情绪状态
                    6. timeAnalysis 分析时间因素的影响
                    7. 只返回JSON，不要其他内容
                    """;

            // 构造用户输入，包含聊天记录和当前时间
            String userInput = String.format("""
                            当前时间：%s
                            
                            聊天记录（按时间顺序）：
                            %s
                            
                            请分析以上信息，判断我是否需要主动和用户聊天。
                            """,
                    currentTime,
                    chatHistory.isEmpty() ? "暂无聊天记录" : String.join("\n", chatHistory)
            );

            // 构造消息
            List<AiMessage> messages = Lists.newArrayList(
                    AiMessage.builder().role(ROLE_USER).content(userInput).build()
            );

            // 调用LLM进行判断
            String response = chatCompletion(getToken(), messages, getModel(), systemPrompt, config);

            log.info("情感AI主动聊天判断请求 - 当前时间: {}, 聊天记录条数: {}", currentTime, chatHistory.size());
            log.info("情感AI主动聊天判断响应: {}", response);

            // 解析JSON响应
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            boolean shouldInitiate = jsonResponse.has("shouldInitiate") ? jsonResponse.get("shouldInitiate").getAsBoolean() : false;
            double confidence = jsonResponse.has("confidence") ? jsonResponse.get("confidence").getAsDouble() : 0.0;
            String reason = jsonResponse.has("reason") ? jsonResponse.get("reason").getAsString() : "";
            String suggestedMessage = jsonResponse.has("suggestedMessage") ? jsonResponse.get("suggestedMessage").getAsString() : "";
            String emotionalAnalysis = jsonResponse.has("emotionalAnalysis") ? jsonResponse.get("emotionalAnalysis").getAsString() : "";
            String timeAnalysis = jsonResponse.has("timeAnalysis") ? jsonResponse.get("timeAnalysis").getAsString() : "";

            return EmotionalChatDecisionResult.builder()
                    .shouldInitiate(shouldInitiate)
                    .confidence(confidence)
                    .reason(reason)
                    .suggestedMessage(suggestedMessage)
                    .emotionalAnalysis(emotionalAnalysis)
                    .timeAnalysis(timeAnalysis)
                    .currentTime(currentTime)
                    .chatHistorySize(chatHistory.size())
                    .build();

        } catch (Exception e) {
            log.error("情感AI主动聊天判断失败 - 当前时间: {}, 聊天记录条数: {}, error: {}",
                    currentTime, chatHistory.size(), e.getMessage(), e);

            // 返回保守的默认结果（不主动聊天）
            return EmotionalChatDecisionResult.builder()
                    .shouldInitiate(false)
                    .confidence(0.0)
                    .reason("判断失败，采用保守策略: " + e.getMessage())
                    .suggestedMessage("")
                    .emotionalAnalysis("无法分析")
                    .timeAnalysis("无法分析")
                    .currentTime(currentTime)
                    .chatHistorySize(chatHistory.size())
                    .build();
        }
    }

    /**
     * 情感AI主动聊天判断结果类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmotionalChatDecisionResult {
        private boolean shouldInitiate;       // 是否需要主动聊天
        private double confidence;            // 置信度 (0-1)
        private String reason;               // 判断理由
        private String suggestedMessage;     // 建议发送的消息内容
        private String emotionalAnalysis;    // 情绪分析
        private String timeAnalysis;         // 时间分析
        private String currentTime;          // 当前时间
        private int chatHistorySize;         // 聊天记录条数

        /**
         * 判断结果是否可信
         *
         * @param threshold 置信度阈值
         * @return 是否可信
         */
        public boolean isReliable(double threshold) {
            return confidence >= threshold;
        }

        /**
         * 获取JSON格式的结果
         *
         * @return JSON字符串
         */
        public String toJson() {
            return new Gson().toJson(this);
        }

        /**
         * 获取简要的决策摘要
         *
         * @return 决策摘要
         */
        public String getSummary() {
            return String.format("决策: %s, 置信度: %.2f, 理由: %s",
                    shouldInitiate ? "需要主动聊天" : "不需要主动聊天",
                    confidence,
                    reason);
        }
    }

    /**
     * 模型复杂度分类方法
     * 根据问题的难度和复杂性，自动选择合适的模型类型
     *
     * @param prompt 用户输入的问题或任务描述
     * @return ModelComplexityResult 包含模型选择结果的对象
     */
    public ModelComplexityResult classifyModelComplexity(String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            throw new IllegalArgumentException("问题描述不能为空");
        }

        // 预定义的模型复杂度分类
        List<String> modelTypes = Lists.newArrayList("高级模型", "标准模型", "基础模型");

        try {
            // 构造系统提示词，要求AI根据问题复杂度选择合适的模型
            String systemPrompt = """
                    你是一个专业的AI模型选择助手。请根据用户的问题或任务描述，分析其复杂度和难度，然后选择最合适的模型类型。
                    
                    模型类型说明：
                    1. 高级模型：适用于复杂推理、创意写作、专业分析、多步骤问题解决、需要深度思考的任务
                    2. 标准模型：适用于一般性问答、常规对话、基础分析、中等复杂度的任务
                    3. 基础模型：适用于简单问答、基础信息查询、格式转换、简单的文本处理任务
                    
                    评估标准：
                    - 问题的逻辑复杂度（是否需要多步推理）
                    - 专业知识要求（是否涉及专业领域）
                    - 创造性要求（是否需要创新思维）
                    - 上下文理解深度（是否需要深度理解）
                    - 任务的综合性（是否涉及多个方面）
                    
                    请严格按照以下JSON格式返回结果：
                    {
                        "selectedModel": "选中的模型类型",
                        "confidence": 0.95,
                        "reason": "选择这个模型的详细理由",
                        "complexityAnalysis": "对问题复杂度的分析",
                        "requiredCapabilities": ["需要的能力1", "需要的能力2"],
                        "difficultyLevel": "简单/中等/困难/极难"
                    }
                    
                    要求：
                    1. selectedModel 必须是：高级模型、标准模型、基础模型 中的一个
                    2. confidence 是置信度，范围0-1
                    3. reason 详细说明选择理由
                    4. complexityAnalysis 分析问题的复杂度特征
                    5. requiredCapabilities 列出解决该问题需要的主要能力
                    6. difficultyLevel 评估问题的整体难度等级
                    7. 只返回JSON，不要其他内容
                    """;

            // 构造消息
            List<AiMessage> messages = Lists.newArrayList(
                    AiMessage.builder().role(ROLE_USER).content(prompt).build()
            );

            // 调用LLM进行分类
            String response = chatCompletion(getToken(), messages, getModel(), systemPrompt, config);

            log.info("模型复杂度分类请求 - prompt: {}", prompt);
            log.info("模型复杂度分类响应: {}", response);

            // 解析JSON响应
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            String selectedModel = jsonResponse.get("selectedModel").getAsString();
            double confidence = jsonResponse.has("confidence") ? jsonResponse.get("confidence").getAsDouble() : 0.0;
            String reason = jsonResponse.has("reason") ? jsonResponse.get("reason").getAsString() : "";
            String complexityAnalysis = jsonResponse.has("complexityAnalysis") ? jsonResponse.get("complexityAnalysis").getAsString() : "";
            String difficultyLevel = jsonResponse.has("difficultyLevel") ? jsonResponse.get("difficultyLevel").getAsString() : "未知";

            // 解析所需能力列表
            List<String> requiredCapabilities = new ArrayList<>();
            if (jsonResponse.has("requiredCapabilities") && jsonResponse.get("requiredCapabilities").isJsonArray()) {
                JsonArray capabilitiesArray = jsonResponse.getAsJsonArray("requiredCapabilities");
                for (JsonElement element : capabilitiesArray) {
                    requiredCapabilities.add(element.getAsString());
                }
            }

            // 验证选中的模型是否在预定义列表中
            if (!modelTypes.contains(selectedModel)) {
                log.warn("AI选择的模型类型 '{}' 不在预定义列表中，使用标准模型作为默认值", selectedModel);
                selectedModel = "标准模型";
                confidence = 0.5; // 降低置信度
                reason = "AI选择的模型类型不在列表中，使用默认的标准模型";
            }

            return ModelComplexityResult.builder()
                    .selectedModel(selectedModel)
                    .confidence(confidence)
                    .reason(reason)
                    .complexityAnalysis(complexityAnalysis)
                    .requiredCapabilities(requiredCapabilities)
                    .difficultyLevel(difficultyLevel)
                    .originalPrompt(prompt)
                    .availableModels(modelTypes)
                    .build();

        } catch (Exception e) {
            log.error("模型复杂度分类失败 - prompt: {}, error: {}", prompt, e.getMessage(), e);

            // 返回默认结果（使用标准模型）
            return ModelComplexityResult.builder()
                    .selectedModel("标准模型")
                    .confidence(0.0)
                    .reason("模型复杂度分类失败，使用默认的标准模型: " + e.getMessage())
                    .complexityAnalysis("无法分析")
                    .requiredCapabilities(Lists.newArrayList("基础处理"))
                    .difficultyLevel("未知")
                    .originalPrompt(prompt)
                    .availableModels(modelTypes)
                    .build();
        }
    }

    /**
     * 模型复杂度分类结果类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModelComplexityResult {
        private String selectedModel;         // 选中的模型类型
        private double confidence;            // 置信度 (0-1)
        private String reason;               // 选择理由
        private String complexityAnalysis;   // 复杂度分析
        private List<String> requiredCapabilities; // 所需能力列表
        private String difficultyLevel;     // 难度等级
        private String originalPrompt;       // 原始问题
        private List<String> availableModels; // 可用模型列表

        /**
         * 判断分类结果是否可信
         *
         * @param threshold 置信度阈值
         * @return 是否可信
         */
        public boolean isReliable(double threshold) {
            return confidence >= threshold;
        }

        /**
         * 获取JSON格式的结果
         *
         * @return JSON字符串
         */
        public String toJson() {
            return new Gson().toJson(this);
        }

        /**
         * 获取模型类型的英文标识
         *
         * @return 英文标识
         */
        public String getModelTypeCode() {
            switch (selectedModel) {
                case "高级模型":
                    return "ADVANCED";
                case "标准模型":
                    return "STANDARD";
                case "基础模型":
                    return "BASIC";
                default:
                    return "STANDARD";
            }
        }

        /**
         * 判断是否为高复杂度任务
         *
         * @return 是否为高复杂度
         */
        public boolean isHighComplexity() {
            return "高级模型".equals(selectedModel);
        }

        /**
         * 判断是否为简单任务
         *
         * @return 是否为简单任务
         */
        public boolean isSimpleTask() {
            return "基础模型".equals(selectedModel);
        }

        /**
         * 获取简要的分类摘要
         *
         * @return 分类摘要
         */
        public String getSummary() {
            return String.format("推荐模型: %s, 难度: %s, 置信度: %.2f",
                    selectedModel, difficultyLevel, confidence);
        }
    }

    /**
     * LLM Usage
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LLMUsage {
        private int inputTokens;
        private int outputTokens;
        // 下面的都有可能会是null，不同模型提供的不一样
        private Integer cacheWriteTokens;
        private Integer cacheReadTokens;
        private Integer thoughtsTokenCount;
        private Double totalCost;
        private Integer tokenLimit;
    }

    /**
     * LLM Chat Completion Result
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LLMChatCompletionResult {
        private String result;
        private LLMUsage usage;
    }

    /**
     * 意图识别结果类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntentClassificationResult {
        private String selectedCategory;      // 选中的分类
        private double confidence;            // 置信度 (0-1)
        private String reason;               // 选择理由
        private String originalPrompt;       // 原始输入
        private List<String> availableCategories; // 可用分类列表

        /**
         * 判断分类结果是否可信
         *
         * @param threshold 置信度阈值
         * @return 是否可信
         */
        public boolean isReliable(double threshold) {
            return confidence >= threshold;
        }

        /**
         * 获取JSON格式的结果
         *
         * @return JSON字符串
         */
        public String toJson() {
            return new Gson().toJson(this);
        }
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
        private String imageType;
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
     * @param msg          消息
     * @param sysPrompt    系统提示
     * @param customConfig 自定义配置
     * @return 结果字符串
     */
    public String call(LLMPart msg, String sysPrompt, CustomConfig customConfig) {
        JsonObject req = getReq(this, msg);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = this.chatCompletion(getToken(), customConfig, messages, customConfig.getModel(), sysPrompt, this.config);
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
     * 同步调用LLM，发送文本和图像输入，并返回结果
     *
     * @param msg          消息
     * @param sysPrompt    系统提示
     * @param customConfig 自定义配置
     * @return 结果字符串
     */
    public String call(LLMCompoundMsg msg, String sysPrompt, CustomConfig customConfig) {
        JsonObject req = getReq(this, msg);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = this.chatCompletion(getToken(), customConfig, messages, customConfig.getModel(), sysPrompt, this.config);
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

    /**
     * 流式调用LLM，发送文本和图像输入，并返回结果
     *
     * @param role         角色实例
     * @param systemPrompt 系统提示
     * @param customConfig 自定义配置
     * @return 结果字符串
     */
    public String callStream(Role role, LLMCompoundMsg msg, String systemPrompt, CustomConfig customConfig) {
        JsonObject req = getReq(this, msg);
        List<AiMessage> messages = new ArrayList<>();
        messages.add(AiMessage.builder().jsonContent(req).build());
        String result = this.syncChat(role, messages, systemPrompt, customConfig);
        log.info("{}", result);
        return result;
    }

    public static LLMCompoundMsg getLlmCompoundMsg(String userPrompt, Message msg) {
        return LLMCompoundMsg.builder()
                .content(userPrompt)
                .parts(msg.getImages() == null
                        ? new ArrayList<>()
                        : msg.getImages()
                        .stream()
                        .map(it -> LLM.LLMPart.builder().type(LLM.TYPE_IMAGE).data(it).mimeType("image/jpeg").build())
                        .collect(Collectors.toList())).build();
    }


    /**
     * 获取LLM请求对象
     *
     * @param llm LLM实例
     * @return 请求对象
     */
    public JsonObject getReq(LLM llm, LLMCompoundMsg msg) {
        JsonObject req = new JsonObject();
        String imageType = getImageType(msg);

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
        } else if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER
                || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT
                || llm.getConfig().getLlmProvider() == LLMProvider.DOUBAO
                || llm.getConfig().getLlmProvider() == LLMProvider.DOUBAO_UI_TARS
                || llm.getConfig().getLlmProvider() == LLMProvider.DOUBAO_VISION
                || llm.getConfig().getLlmProvider() == LLMProvider.MIFY
                || llm.getConfig().getLlmProvider() == LLMProvider.MIFY_GATEWAY
        ) {
            req.addProperty("role", ROLE_USER);
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
                        imgObj.addProperty("url", "data:image/" + imageType + ";base64," + part.getData());
                    } else {
                        imgObj.addProperty("url", part.getData());
                    }
                    obj2.add("image_url", imgObj);
                    array.add(obj2);
                });
            }
            req.add("content", array);
        } else if (llm.getConfig().getLlmProvider() == LLMProvider.CLAUDE_COMPANY) {
            req.addProperty("role", ROLE_USER);
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
        } else {
            // HINT: openai compatible
            req.addProperty("role", ROLE_USER);
            req.addProperty("content", msg.getContent());
        }
        return req;
    }

    private static String getImageType(LLMCompoundMsg msg) {
        String imageType = "jpeg";
        if (StringUtils.isNotEmpty(msg.getImageType())) {
            imageType = msg.getImageType();
        }
        return imageType;
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
        } else if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER
                || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", ROLE_USER);
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
        } else if (llm.getConfig().getLlmProvider() == LLMProvider.CLAUDE_COMPANY) {
            req.addProperty("role", ROLE_USER);
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
        } else {
            // HINT: openai compatible
            req.addProperty("role", ROLE_USER);
            req.addProperty("content", llmPart.getText());
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
}
