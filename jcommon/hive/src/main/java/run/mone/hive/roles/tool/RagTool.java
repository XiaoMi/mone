package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.ReactorRole;

/**
 * RAG 知识库搜索工具
 *
 * @author assistant
 */
public class RagTool implements ITool {

    private static final Logger log = LoggerFactory.getLogger(RagTool.class);

    @Override
    public String getName() {
        return "rag_search";
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
                A knowledge base search tool that queries internal RAG (Retrieval-Augmented Generation) system for relevant information.
                Use this tool when you need to search for specific knowledge or information stored in the organization's knowledge base.
                
                **When to use:** Choose this tool when the user needs to find information from internal documents, policies, procedures, 
                or any content that has been indexed in the knowledge base system.
                
                **Output:** The tool will return the most relevant content from the knowledge base based on the search query.
                """;
    }

    @Override
    public String parameters() {
        return """
                - query: (required) The search query string
                - topK: (optional) Maximum number of search results to return (default: 5, max: 20)
                - threshold: (optional) Similarity threshold for search results (default: 0.5, range: 0.0-1.0)
                - tag: (optional) Tag filter for search results (default: empty)
                - tenant: (optional) Tenant identifier for multi-tenant systems (default: "1")
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: If you are using this tool, you MUST return the search results within the <rag_search> tag):
                
                Example: Searching for information about Nacos
                <rag_search>
                  <query>nacos是什么</query>
                  <topK>5</topK>
                  <threshold>0.5</threshold>
                  <tag></tag>
                  <tenant>1</tenant>
                </rag_search>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必需参数
            if (!inputJson.has("query") || StringUtils.isBlank(inputJson.get("query").getAsString())) {
                log.error("RAG搜索请求缺少必需的query参数");
                result.addProperty("error", "缺少必需参数'query'");
                return result;
            }

            String query = inputJson.get("query").getAsString();
            log.info("开始RAG搜索，查询内容：{}", query);

            // 获取环境变量中的 RAG URL
            String ragUrl = System.getenv("RAG_URL");
            if (StringUtils.isBlank(ragUrl)) {
                log.error("RAG搜索请求缺少必需的RAG_URL环境变量");
                result.addProperty("error", "缺少必需的RAG_URL环境变量");
                return result;
            }

            // 设置默认参数
            int topK = inputJson.has("topK") ? inputJson.get("topK").getAsInt() : 5;
            double threshold = inputJson.has("threshold") ? inputJson.get("threshold").getAsDouble() : 0.5;
            String tag = inputJson.has("tag") ? inputJson.get("tag").getAsString() : "";
            String tenant = inputJson.has("tenant") ? inputJson.get("tenant").getAsString() : "1";

            // 创建 LLM 实例用于调用 RAG 接口
            LLM llm = new LLM(LLMConfig.builder()
                    .llmProvider(LLMProvider.KNOWLEDGE_BASE)
                    .url(ragUrl + "/rag/query")
                    .build());

            // 调用 RAG 查询接口
            String ragResponse = llm.queryRag(query, topK, threshold, tag, tenant);

            // 解析 RAG 响应
            JsonObject responseJson = JsonParser.parseString(ragResponse).getAsJsonObject();

            // 检查响应是否成功
            if (!responseJson.has("data") || responseJson.get("data").isJsonNull()) {
                log.warn("RAG搜索未返回有效数据，查询：{}", query);
                result.addProperty("content", "知识库中未找到相关内容");
                result.addProperty("query", query);
                return result;
            }

            // 提取第一个结果的内容
            String content = responseJson.get("data").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("content").getAsString();

            // 构建返回结果
            String formattedResult = "===========\n" + "知识库中的内容:" + "\n" + content + "\n";

            result.addProperty("content", formattedResult);
            result.addProperty("query", query);
            result.addProperty("source", "knowledge_base");

            log.info("RAG搜索完成，查询：{}，返回内容长度：{}", query, content.length());

            return result;

        } catch (Exception e) {
            log.error("RAG搜索处理发生异常", e);
            result.addProperty("error", "知识库搜索失败: " + e.getMessage());
            return result;
        }
    }
}