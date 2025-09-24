package run.mone.mcp.knowledge.base.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.knowledge.base.entity.BaseVO;
import run.mone.mcp.knowledge.base.entity.VectorQueryResponse;
import run.mone.mcp.knowledge.base.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class KnowledgeBaseQueryFunction implements McpFunction {

    private String name = "stream_knowledge-base_chat";

    private String desc = "Query knowledge base";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {"message": {
                        "type": "string",
                        "description":"Query to be executed in the knowledge base"
                    }
                },
                "required": ["message"]
            }
            """;

    private static HttpClient httpClient = new HttpClient();
    private static final Gson gson = new Gson();

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                String query = (String) arguments.get("message");
                log.info("query: {}", query);
                String result = queryKnowledgeFile(query);
                return Flux.just(
                        new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                        new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
                );
            } catch (Exception e) {
                log.error("执行知识库查询失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("操作失败：" + e.getMessage())), true));
            }
        });
    }

    private String getHost () {
        return System.getenv().getOrDefault("API_HOST", "http://127.0.0.1:8083");
    }

    // 调用知识库接口
    public String queryKnowledgeFile(String query){
        String result = "";
        try {
            String url = getHost() + "/rag/query";
            JsonObject req = new JsonObject();
            req.addProperty("query", query);
            req.addProperty("topK", 5);
            req.addProperty("threshold", 0.5);
            req.addProperty("tag", "");
            req.addProperty("tenant", "1");
            String response = httpClient.post(url, gson.toJson(req));
            BaseVO<List<VectorQueryResponse>> list = gson.fromJson(response,
                    new TypeToken<BaseVO<List<VectorQueryResponse>>>(){}.getType());
            // 包装结果
            List<String> referList = new ArrayList<>();
            for (VectorQueryResponse vectorQueryResponse : list.getData()) {
                referList.add(vectorQueryResponse.getContent());
            }
            result = gson.toJson(referList);
        } catch (IOException e) {
            result = "error: " + e.getMessage();
        }
        return result;
    }

}
