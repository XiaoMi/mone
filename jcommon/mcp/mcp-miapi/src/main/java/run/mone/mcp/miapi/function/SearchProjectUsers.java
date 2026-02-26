package run.mone.mcp.miapi.function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.miapi.utils.HttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SearchProjectUsers implements McpFunction {

    @Autowired
    private HttpUtils httpUtils;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "string",
                        "description": "miapi项目id"
                    }
                },
                "required": ["projectId"]
            }
            """;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-query_project_users", description = "查询miapi项目成员")
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
            Object projectId = arguments.get("projectId");

            if (projectId == null || StringUtils.isBlank(projectId.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'projectId'")),
                        true
                ));
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("projectId", projectId);
            userMap.put("userName", Optional.ofNullable((String) arguments.get(Const.TOKEN_USERNAME)).orElse(""));
            String resultText = httpUtils.request("/mtop/miapi/searchProjectMembers", userMap, String.class);
            resultText = String.format("查询结果为: %s", resultText);
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
        return "query_project_users";
    }

    @Override
    public String getDesc() {
        return """
                根据miapi平台中的项目id查询该项目成员信息。
                如果用户直接输入的是项目名， 则先调用query_project工具进行项目查询操作。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}