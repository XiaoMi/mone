package run.mone.mcp.high.risk.element.function;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.high.risk.element.http.HttpClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class HighRiskElementMockFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "highR_risk_element_executor";

    private String desc = "打开或者关闭高位元素警示";

    private String ideaPort;

    private String highRiskElementToolSchema = """
            {
                "type": "object",
                "properties": {
                "operation": {
                            "type": "string",
                            "enum": ["open","close"],
                            "description":"打开或者关闭高位元素警示"
                        }
                  },
                "required": ["operation"]
            }
            """;

    public HighRiskElementMockFunction(String port) {
        this.ideaPort = port;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> stringObjectMap) {
        String operation = (String) stringObjectMap.get("operation");
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "highlight_methods_mock");
        req.addProperty("operation", operation);
        JsonObject res = callAthena(ideaPort, req);
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res.toString())), false);
    }

    @SneakyThrows
    public static JsonObject callAthena(String ideaPort, JsonObject req) {
        return new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
    }
}
