package run.mone.neo4j;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Ollama客户端
 * 支持调用Ollama生成文本，返回JSON格式数据
 * 并处理返回结果中可能包含的代码块标记
 */
@Slf4j
@Data
public class OllamaClient {
    
    private final HttpClient httpClient;
    private final Gson gson;
    private String baseUrl;
    private String model;
    private int connectTimeout;
    private int readTimeout;
    private Map<String, String> customHeaders;
    
    // 默认配置
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "qwen2.5:7b";
    private static final int DEFAULT_CONNECT_TIMEOUT = 30;
    private static final int DEFAULT_READ_TIMEOUT = 300;
    
    // 代码块匹配正则表达式
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```([a-zA-Z]*)?\\n([\\s\\S]*?)```", Pattern.MULTILINE);
    
    /**
     * 默认构造函数
     */
    public OllamaClient() {
        this(DEFAULT_BASE_URL, DEFAULT_MODEL);
    }
    
    /**
     * 构造函数
     * @param baseUrl Ollama服务基础URL
     * @param model 模型名称
     */
    public OllamaClient(String baseUrl, String model) {
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_BASE_URL;
        this.model = model != null ? model : DEFAULT_MODEL;
        this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        this.readTimeout = DEFAULT_READ_TIMEOUT;
        this.customHeaders = new HashMap<>();
        
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(this.connectTimeout))
            .build();
        this.gson = new Gson();
        
        log.info("OllamaClient initialized with model: {} at {}", this.model, this.baseUrl);
    }
    
    /**
     * 发送聊天请求到Ollama
     * @param prompt 用户输入的提示
     * @return OllamaResponse 响应对象
     */
    public OllamaResponse chat(String prompt) {
        return chat(prompt, null);
    }
    
    /**
     * 发送聊天请求到Ollama
     * @param prompt 用户输入的提示
     * @param systemPrompt 系统提示（可选）
     * @return OllamaResponse 响应对象
     */
    public OllamaResponse chat(String prompt, String systemPrompt) {
        try {
            // 构建请求体
            JsonObject request = new JsonObject();
            request.addProperty("model", this.model);
            request.addProperty("prompt", prompt);
            request.addProperty("stream", false); // 不使用流式响应
            request.addProperty("format", "json"); // 请求JSON格式响应
            
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                request.addProperty("system", systemPrompt);
            }
            
            // API URL
            String apiUrl = this.baseUrl + "/api/generate";
            
            // 构建HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .timeout(Duration.ofSeconds(this.readTimeout));
            
            // 添加自定义请求头
            if (this.customHeaders != null) {
                this.customHeaders.forEach(requestBuilder::header);
            }
            
            HttpRequest httpRequest = requestBuilder.build();
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("Ollama API error: {} - {}", response.statusCode(), response.body());
                return OllamaResponse.error("API调用失败: " + response.statusCode(), response.body());
            }
            
            // 解析响应
            return parseResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error calling Ollama API", e);
            return OllamaResponse.error("调用失败", e.getMessage());
        }
    }
    
    /**
     * 解析Ollama响应
     * @param responseBody 响应体
     * @return OllamaResponse 解析后的响应对象
     */
    private OllamaResponse parseResponse(String responseBody) {
        try {
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (responseJson.has("error")) {
                String errorMessage = responseJson.get("error").getAsString();
                return OllamaResponse.error("Ollama错误", errorMessage);
            }
            
            // 提取响应文本
            String responseText = "";
            if (responseJson.has("response")) {
                responseText = responseJson.get("response").getAsString();
            }
            
            // 处理代码块
            OllamaResponse ollamaResponse = processCodeBlocks(responseText);
            
            // 设置原始响应
            ollamaResponse.setRawResponse(responseBody);
            
            // 设置模型信息
            if (responseJson.has("model")) {
                ollamaResponse.setModel(responseJson.get("model").getAsString());
            }
            
            // 设置完成标志
            if (responseJson.has("done")) {
                ollamaResponse.setDone(responseJson.get("done").getAsBoolean());
            }
            
            return ollamaResponse;
            
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse Ollama response as JSON: {}", responseBody, e);
            // 如果不是JSON格式，直接处理为文本
            return processCodeBlocks(responseBody);
        } catch (Exception e) {
            log.error("Error parsing Ollama response: {}", responseBody, e);
            return OllamaResponse.error("响应解析失败", e.getMessage());
        }
    }
    
    /**
     * 处理响应文本中的代码块
     * @param text 原始文本
     * @return OllamaResponse 处理后的响应对象
     */
    private OllamaResponse processCodeBlocks(String text) {
        if (text == null || text.trim().isEmpty()) {
            return OllamaResponse.success("", text);
        }
        
        OllamaResponse response = new OllamaResponse();
        response.setSuccess(true);
        response.setOriginalText(text);
        
        // 查找代码块
        Matcher matcher = CODE_BLOCK_PATTERN.matcher(text);
        
        if (matcher.find()) {
            // 提取第一个代码块
            String language = matcher.group(1); // 语言标识（可能为null）
            String codeContent = matcher.group(2); // 代码内容
            
            response.setHasCodeBlock(true);
            response.setCodeLanguage(language);
            response.setCodeContent(codeContent != null ? codeContent.trim() : "");
            
            // 移除代码块后的纯文本
            String textWithoutCodeBlocks = text.replaceAll("```[a-zA-Z]*?\\n[\\s\\S]*?```", "").trim();
            response.setCleanText(textWithoutCodeBlocks);
            
            log.debug("Found code block with language: {}, content length: {}", 
                language, codeContent != null ? codeContent.length() : 0);
        } else {
            // 没有代码块
            response.setHasCodeBlock(false);
            response.setCleanText(text);
        }
        
        return response;
    }
    
    /**
     * 获取可用模型列表
     * @return 模型名称列表的JSON字符串
     */
    public String getAvailableModels() {
        try {
            String apiUrl = this.baseUrl + "/api/tags";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                log.warn("Failed to get available models: {}", response.statusCode());
                return "{\"error\":\"Failed to get models\"}";
            }
            
        } catch (Exception e) {
            log.error("Error getting available models", e);
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    /**
     * 检查Ollama服务是否可用
     * @return true如果服务可用
     */
    public boolean isServiceAvailable() {
        try {
            String apiUrl = this.baseUrl + "/api/tags";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
            
        } catch (Exception e) {
            log.debug("Ollama service not available: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Ollama响应对象
     */
    @Data
    public static class OllamaResponse {
        private boolean success;
        private String error;
        private String errorDetail;
        private String originalText;
        private String cleanText;
        private boolean hasCodeBlock;
        private String codeLanguage;
        private String codeContent;
        private String rawResponse;
        private String model;
        private boolean done;
        
        public OllamaResponse() {
            this.success = false;
            this.hasCodeBlock = false;
        }
        
        /**
         * 创建成功响应
         */
        public static OllamaResponse success(String cleanText, String originalText) {
            OllamaResponse response = new OllamaResponse();
            response.success = true;
            response.cleanText = cleanText;
            response.originalText = originalText;
            return response;
        }
        
        /**
         * 创建错误响应
         */
        public static OllamaResponse error(String error, String errorDetail) {
            OllamaResponse response = new OllamaResponse();
            response.success = false;
            response.error = error;
            response.errorDetail = errorDetail;
            return response;
        }
        
        /**
         * 转换为JSON字符串
         */
        public String toJson() {
            return new Gson().toJson(this);
        }
        
        /**
         * 获取主要内容（优先返回代码内容，否则返回清理后的文本）
         */
        public String getMainContent() {
            if (hasCodeBlock && codeContent != null && !codeContent.trim().isEmpty()) {
                return codeContent;
            }
            return cleanText != null ? cleanText : originalText;
        }
    }
}
