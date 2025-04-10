package run.mone.mcp.knowledge.base.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.knowledge.base.entity.BaseVO;
import run.mone.mcp.knowledge.base.entity.VectorQueryResponse;
import run.mone.mcp.knowledge.base.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class KnowledgeBaseQueryFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private String name = "stream_KnowledgeBaseQuery";

    private String desc = "Query knowledge base";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {"query": {
                        "type": "string",
                        "description":"Query to be executed in the knowledge base"
                    }
                },
                "required": ["query"]
            }
            """;

    private static HttpClient httpClient = new HttpClient();
    private static final Gson gson = new Gson();

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                String query = (String) arguments.get("query");
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
            String url = getHost() + "/api/knowledgeFile/query";
            JsonObject req = new JsonObject();
            req.addProperty("queryText", query);
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

//    // 测试
//    public static void main(String[] args) {
//        KnowledgeBaseQueryFunction knowledgeBaseQueryFunction = new KnowledgeBaseQueryFunction();
//        String result = knowledgeBaseQueryFunction.queryKnowledgeFile("小米汽车大兴区的4s店有哪些");
//        System.out.println(result);
//    }
}
