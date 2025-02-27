package run.mone.mcp.gateway.function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.gateway.service.GatewayService;
import run.mone.mcp.gateway.service.bo.ListApiInfoParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ApiFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final GatewayService gatewayService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "env": {
                        "type": "string",
                        "enum": ["staging", "online"],
                        "description": "The Environment for gateway"
                    },
                    "keyword": {
                        "type": "string",
                        "description": "fuzzy search keyword"
                    }
                },
                "required": ["env", "keyword"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String result;

        try {
            ListApiInfoParam param = new ListApiInfoParam();
            param.setUrl((String) arguments.get("keyword"));
            result = gatewayService.listApiInfo((String) arguments.get("env"), param);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    public String getName() {
        return "ApiOperation";
    }

    public String getDesc() {
        return "Perform various api operations including creating, updating, search api infos.";
    }


    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}