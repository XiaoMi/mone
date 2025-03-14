package run.mone.mcp.high.risk.element.function;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.high.risk.element.http.HttpClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

@Data
@Slf4j
public class HighRiskElementFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "highR_risk_element_executor";

    private String desc = "负责高危元素的若干操作";

    private String ideaPort;

    private String highRiskElementToolSchema = """
            {
                "type": "object",
                "properties": {
                    "params": {
                        "type": "object",
                        "additionalProperties": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            }
                        },
                        "description": "操作类型映射，key为全类名，value为该类下可调用的方法名列表"
                    }
                  },
                "required": ["params"]
            }
            """;

    public HighRiskElementFunction(String port) {
        this.ideaPort = port;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> stringObjectMap) {

        Object paramsObj = stringObjectMap.get("params");
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "highlight_methods");
        
        // 将 Map<String, List<String>> 转换为 JSON 字符串
        if (paramsObj instanceof Map) {
            req.add("params", new com.google.gson.Gson().toJsonTree(paramsObj));
        } else {
            req.addProperty("params", paramsObj.toString());
        }
        JsonObject res = callAthena(ideaPort, req);
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res.toString())), false);
    }

    @SneakyThrows
    public static JsonObject callAthena(String ideaPort, JsonObject req) {
        return new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
    }
}
