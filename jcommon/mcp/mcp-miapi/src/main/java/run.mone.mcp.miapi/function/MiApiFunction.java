package run.mone.mcp.miapi.function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.miapi.utils.HttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MiApiFunction implements McpFunction {

    @Autowired
    private HttpUtils httpUtils;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectName": {
                        "type": "string",
                        "description": "项目(组)名称（必填）"
                    }
                },
                "required": ["projectName"]
            }
            """;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-query_project", description = "查询项目")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        try {
            if (BASE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: gateway_host 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object projectName = arguments.get("projectName");

            if (projectName == null || StringUtils.isBlank(projectName.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'projectName'")),
                        true
                ));
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("projectName", projectName);
            String resultText = httpUtils.request("/mtop/miapi/getProjectByName", userMap, Map.class);
            resultText = String.format("miapi项目信息: %s", resultText);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(resultText)),
                    false
            ));
        } catch (Exception e) {
            log.error("执行miapi操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    @Override
    public String getName() {
        return "query_project";
    }

    @Override
    public String getDesc() {
        return """
                根据项目(组)名称，查询miapi项目信息。
                如：帮我查询mock-server项目信息。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}