package run.mone.mcp.gateway.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.gateway.service.GatewayService;
import run.mone.mcp.gateway.service.bo.ListApiInfoParam;

import java.util.List;
import java.util.Map;

/**
 * Gateway API操作工具，用于查询和管理API信息
 *
 * @author goodjava@qq.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiFunction implements McpFunction {

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
                    },
                    "applications": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description": "List of application names to filter APIs"
                    }
                },
                "required": ["operation", "env"]
            }
            """;

    @Override
    @ReportCallCount(businessName = "gateway-api", description = "Gateway API操作工具调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("ApiFunction arguments: {}", arguments);

        String operation = (String) arguments.get("operation");
        String result;

        try {
            switch (operation) {
                case "listApiInfo" -> {
                    ListApiInfoParam param = new ListApiInfoParam();
                    param.setUrl((String) arguments.get("keyword"));
                    // 支持传入applications参数
                    if (arguments.containsKey("applications")) {
                        Object applicationsObj = arguments.get("applications");
                        if (applicationsObj instanceof List<?> list) {
                            @SuppressWarnings("unchecked")
                            List<String> applications = (List<String>) list;
                            param.setApplications(applications);
                        }
                    }
                    result = gatewayService.listApiInfo((String) arguments.get("env"), param);
                }
                case "detailByUrl" -> {
                    result = gatewayService.detailByUrl((String) arguments.get("env"), (String) arguments.get("url"));
                }
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            }

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result)), 
                    false
            ));
        } catch (Exception e) {
            log.error("执行ApiFunction操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())), 
                    true
            ));
        }
    }

    @Override
    public String getName() {
        return "api_operation";
    }

    @Override
    public String getDesc() {
        return """
                Gateway API操作工具，用于查询和管理API信息。
                
                **使用场景：**
                - 查询API列表信息
                - 根据URL获取API详细信息
                - 管理和搜索Gateway API
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}