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
                    "operation": {
                        "type": "string",
                        "enum": ["listApiInfo", "detailByUrl"],
                        "description": "The api operation to perform"
                    },
                    "env": {
                        "type": "string",
                        "enum": ["staging", "online"],
                        "description": "The Environment for gateway"
                    },
                    "keyword": {
                        "type": "string",
                        "description": "fuzzy search keyword"
                    },
                    "url": {
                        "type": "string",
                        "description": "api url"
                    }
                },
                "required": ["operation", "env"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {

        String operation = (String) arguments.get("operation");
        String result;

        try {
            switch (operation) {
                case "listApiInfo" -> {
                    ListApiInfoParam param = new ListApiInfoParam();
                    param.setUrl((String) arguments.get("keyword"));
                    result = gatewayService.listApiInfo((String) arguments.get("env"), param);
                }
                case "detailByUrl" -> {
                    result = gatewayService.detailByUrl((String) arguments.get("env"), (String) arguments.get("url"));
                }
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

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