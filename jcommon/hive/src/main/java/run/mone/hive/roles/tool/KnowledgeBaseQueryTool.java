package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 知识库查询工具
 *
 * @author assistant
 */
public class KnowledgeBaseQueryTool implements ITool {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseQueryTool.class);
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public KnowledgeBaseQueryTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String getName() {
        return "knowledge_base_query";
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                A knowledge base query tool that searches for information in a configured knowledge base.
                Use this tool when the user needs to find specific information that might be stored in the knowledge base,
                such as company documents, technical specifications, or other structured information.
                
                **When to use:** Choose this tool when the user needs to search for information that is likely to be
                stored in a knowledge base rather than requiring a web search or personal memory.
                
                **Common conversation triggers:**
                1. "看下XX手册，XX问题怎么解决" → Search manual/handbook
                2. "XX文档里XX是怎么说的" → Search specific document content
                3. "XX文档中关于XX的说明" → Search technical documentation
                
                **Output:** The tool will return relevant documents/content from the knowledge base with relevance scores.
                """;
    }

    @Override
    public String parameters() {
        return """
                - query: (required) The search query string
                - knowledge_base_id: (optional) Specific knowledge base ID to search (uses default if not provided)
                - max_results: (optional) Maximum number of results to return (default: 5, max: 20)
                - min_score: (optional) Minimum relevance score for results (default: 0.0)
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: If you are using this tool, you MUST return the search results within the <knowledge_base_query> tag):
                
                Example: Searching for technical documentation
                <knowledge_base_query>
                  <query>API documentation authentication</query>
                </knowledge_base_query>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必需参数
            if (!inputJson.has("query") || StringUtils.isBlank(inputJson.get("query").getAsString())) {
                log.error("知识库查询请求缺少必需的query参数");
                result.addProperty("error", "缺少必需参数'query'");
                return result;
            }

            String query = inputJson.get("query").getAsString();
            log.info("开始知识库查询，查询内容：{}", query);

            // 获取配置参数（直接从环境变量获取）
            String apiUrl = System.getenv("KNOWLEDGE_BASE_API_URL");
            String apiKey = System.getenv("KNOWLEDGE_BASE_API_KEY");
            String knowledgeBaseId = System.getenv("KNOWLEDGE_BASE_ID");

            // 验证必需的配置
            if (StringUtils.isBlank(apiUrl)) {
                log.error("知识库查询缺少API URL配置");
                result.addProperty("error", "缺少知识库API URL配置");
                return result;
            }

            if (StringUtils.isBlank(apiKey)) {
                log.error("知识库查询缺少API Key配置");
                result.addProperty("error", "缺少知识库API Key配置");
                return result;
            }

            // 构建请求参数
            JsonObject requestBody = buildRequestBody(inputJson, knowledgeBaseId);
            
            // 发送HTTP请求
            JsonObject response = sendQueryRequest(apiUrl, apiKey, requestBody);
            log.info("KnowledgeBaseQueryTool query response:{}", response);
            if (response.has("error")) {
                return response;
            }

            // 处理查询结果
            JsonObject processedResult = processQueryResults(response);
            log.info("知识库查询完成，返回{}条结果", 
                processedResult.has("results") ? processedResult.getAsJsonArray("results").size() : 0);
            
            return processedResult;

        } catch (Exception e) {
            log.error("知识库查询处理发生异常", e);
            result.addProperty("error", "查询失败: " + e.getMessage());
            return result;
        }
    }


    private JsonObject buildRequestBody(JsonObject inputJson, String knowledgeBaseId) {
        JsonObject requestBody = new JsonObject();
        
        // 必需参数
        requestBody.addProperty("query", inputJson.get("query").getAsString());
        
        // 知识库ID
        if (StringUtils.isNotBlank(knowledgeBaseId)) {
            requestBody.addProperty("knowledge_base_id", knowledgeBaseId);
        }
        
        requestBody.addProperty("simple", true);
        
        return requestBody;
    }

    private JsonObject sendQueryRequest(String apiUrl, String apiKey, JsonObject requestBody) throws IOException {
        JsonObject result = new JsonObject();
        
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                log.error("知识库API请求失败，状态码：{}，响应：{}", response.code(), responseBody);
                result.addProperty("error", "API请求失败，状态码: " + response.code() + ", 响应: " + responseBody);
                return result;
            }
            
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    private JsonObject processQueryResults(JsonObject apiResponse) {
        JsonObject result = new JsonObject();
        
        // 检查响应是否成功
        boolean success = apiResponse.has("success") && apiResponse.get("success").getAsBoolean();
        result.addProperty("success", success);
        
        if (!success) {
            result.addProperty("error", "知识库查询失败");
            return result;
        }
        
        // 处理新格式的响应 - 包含data对象
        if (apiResponse.has("data") && apiResponse.get("data").isJsonObject()) {
            JsonObject data = apiResponse.getAsJsonObject("data");
            JsonObject processedData = new JsonObject();
            
            // 提取content
            if (data.has("content")) {
                processedData.addProperty("content", data.get("content").getAsString());
            }
            
            // 提取session_id
            if (data.has("session_id")) {
                processedData.addProperty("session_id", data.get("session_id").getAsString());
            }
            
            result.add("data", processedData);
        }
        // 兼容旧格式的响应 - 包含results数组
        else if (apiResponse.has("results") && apiResponse.get("results").isJsonArray()) {
            JsonArray results = apiResponse.getAsJsonArray("results");
            JsonArray processedResults = new JsonArray();
            
            for (int i = 0; i < results.size(); i++) {
                JsonObject item = results.get(i).getAsJsonObject();
                JsonObject processedItem = new JsonObject();
                
                if (item.has("content")) {
                    processedItem.addProperty("content", item.get("content").getAsString());
                }

                processedResults.add(processedItem);
            }
            
            result.add("results", processedResults);
        }
        
        // 添加查询信息
        if (apiResponse.has("query")) {
            result.addProperty("query", apiResponse.get("query").getAsString());
        }
        
        return result;
    }
}