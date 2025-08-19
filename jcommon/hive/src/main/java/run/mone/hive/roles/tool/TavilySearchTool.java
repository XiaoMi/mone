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
 * Tavily 搜索工具
 *
 * @author assistant
 */
public class TavilySearchTool implements ITool {

    private static final Logger log = LoggerFactory.getLogger(TavilySearchTool.class);
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String TAVILY_API_URL = "https://api.tavily.com/search";

    public TavilySearchTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String getName() {
        return "tavily_search";
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
                A powerful web search tool powered by Tavily API that provides real-time information from the internet.
                Use this tool when the user needs to search for current information, news, facts, or any content that requires up-to-date web search results.
                
                **When to use:** Choose this tool when the user needs to find current information that may not be in your training data,
                such as recent news, current events, latest developments, or when they explicitly ask to search the web.
                
                **Output:** The tool will return search results including titles, URLs, content snippets, and optionally a direct answer.
                """;
    }

    @Override
    public String parameters() {
        return """
                - query: (required) The search query string
                - topic: (optional) Search topic, can be "general" or "news" (default: "general")
                - search_depth: (optional) Search depth, can be "basic" or "advanced" (default: "basic")
                - max_results: (optional) Maximum number of search results to return (default: 5, max: 20)
                - include_answer: (optional) Whether to include a direct answer (default: true)
                - include_raw_content: (optional) Whether to include raw content (default: false)
                - days: (optional) Time range in days for news search (default: 7)
                - country: (optional) Country code for localized search (e.g., "US", "CN")
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: If you are using this tool, you MUST return the search results within the <tavily_search> tag):
                
                Example: Searching for information about Leo Messi
                <tavily_search>
                  <query>Leo Messi latest news</query>
                  <topic>general</topic>
                  <max_results>5</max_results>
                  <include_answer>true</include_answer>
                </tavily_search>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必需参数
            if (!inputJson.has("query") || StringUtils.isBlank(inputJson.get("query").getAsString())) {
                log.error("Tavily搜索请求缺少必需的query参数");
                result.addProperty("error", "缺少必需参数'query'");
                return result;
            }

            String query = inputJson.get("query").getAsString();
            log.info("开始Tavily搜索，查询内容：{}", query);

            inputJson.addProperty("api_token",System.getenv("TAVILY_KEY"));

            // 验证 API Token
            if (!inputJson.has("api_token") || StringUtils.isBlank(inputJson.get("api_token").getAsString())) {
                log.error("Tavily搜索请求缺少必需的api_token参数");
                result.addProperty("error", "缺少必需参数'api_token'");
                return result;
            }

            String apiToken = inputJson.get("api_token").getAsString();

            // 构建请求参数
            JsonObject requestBody = buildRequestBody(inputJson);
            
            // 发送HTTP请求
            JsonObject response = sendSearchRequest(apiToken, requestBody);
            
            if (response.has("error")) {
                return response;
            }

            // 处理搜索结果
            JsonObject processedResult = processSearchResults(response);
            log.info("Tavily搜索完成，返回{}条结果", 
                processedResult.has("results") ? processedResult.getAsJsonArray("results").size() : 0);
            
            return processedResult;

        } catch (Exception e) {
            log.error("Tavily搜索处理发生异常", e);
            result.addProperty("error", "搜索失败: " + e.getMessage());
            return result;
        }
    }

    private JsonObject buildRequestBody(JsonObject inputJson) {
        JsonObject requestBody = new JsonObject();
        
        // 必需参数
        requestBody.addProperty("query", inputJson.get("query").getAsString());
        
        // 可选参数，设置默认值
        requestBody.addProperty("topic", inputJson.has("topic") ? 
            inputJson.get("topic").getAsString() : "general");
        requestBody.addProperty("search_depth", inputJson.has("search_depth") ? 
            inputJson.get("search_depth").getAsString() : "basic");
        requestBody.addProperty("max_results", inputJson.has("max_results") ? 
            inputJson.get("max_results").getAsInt() : 5);
        requestBody.addProperty("include_answer", inputJson.has("include_answer") ? 
            inputJson.get("include_answer").getAsBoolean() : true);
        requestBody.addProperty("include_raw_content", inputJson.has("include_raw_content") ? 
            inputJson.get("include_raw_content").getAsBoolean() : false);
        requestBody.addProperty("include_images", false);
        requestBody.addProperty("include_image_descriptions", false);
        requestBody.addProperty("days", inputJson.has("days") ? 
            inputJson.get("days").getAsInt() : 7);
        
        // 可选的国家参数
        if (inputJson.has("country") && !StringUtils.isBlank(inputJson.get("country").getAsString())) {
            requestBody.addProperty("country", inputJson.get("country").getAsString());
        }
        
        // 空数组参数
        requestBody.add("include_domains", new JsonArray());
        requestBody.add("exclude_domains", new JsonArray());
        requestBody.addProperty("chunks_per_source", 3);
        requestBody.add("time_range", null);
        
        return requestBody;
    }

    private JsonObject sendSearchRequest(String apiToken, JsonObject requestBody) throws IOException {
        JsonObject result = new JsonObject();
        
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(TAVILY_API_URL)
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                log.error("Tavily API请求失败，状态码：{}，响应：{}", response.code(), responseBody);
                result.addProperty("error", "API请求失败，状态码: " + response.code() + ", 响应: " + responseBody);
                return result;
            }
            
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    private JsonObject processSearchResults(JsonObject apiResponse) {
        JsonObject result = new JsonObject();
        
        // 添加直接答案（如果有）
        if (apiResponse.has("answer") && !apiResponse.get("answer").isJsonNull()) {
            result.addProperty("answer", apiResponse.get("answer").getAsString());
        }
        
        // 处理搜索结果
        if (apiResponse.has("results") && apiResponse.get("results").isJsonArray()) {
            JsonArray results = apiResponse.getAsJsonArray("results");
            JsonArray processedResults = new JsonArray();
            
            for (int i = 0; i < results.size(); i++) {
                JsonObject item = results.get(i).getAsJsonObject();
                JsonObject processedItem = new JsonObject();
                
                if (item.has("title")) {
                    processedItem.addProperty("title", item.get("title").getAsString());
                }
                if (item.has("url")) {
                    processedItem.addProperty("url", item.get("url").getAsString());
                }
                if (item.has("content")) {
                    processedItem.addProperty("content", item.get("content").getAsString());
                }
                if (item.has("score")) {
                    processedItem.addProperty("score", item.get("score").getAsDouble());
                }
                if (item.has("published_date")) {
                    processedItem.addProperty("published_date", item.get("published_date").getAsString());
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