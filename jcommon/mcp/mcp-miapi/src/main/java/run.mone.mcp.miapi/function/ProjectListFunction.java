package run.mone.mcp.miapi.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import run.mone.hive.configs.Const;
import run.mone.mcp.miapi.utils.HttpUtils;

@Slf4j
@Component
public class ProjectListFunction implements McpFunction {
    @Autowired
    private HttpUtils httpUtils;
    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-query_my_project", description = "查询我有权限的项目")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        try {
            if (BASE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: gateway_host 环境变量未设置")),
                        true
                ));
            }

            String userName = Optional.ofNullable((String) arguments.get(Const.TOKEN_USERNAME)).orElse("");
            if (userName == null || userName.isEmpty()) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：用户信息获取失败")),
                        true
                ));
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userName", userName);
            String resultText = httpUtils.request("/mtop/miapi/getMyProjectList", userMap, List.class);
            resultText = String.format("我参与的miapi项目信息: %s", resultText);
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
        return "query_my_project";
    }

    @Override
    public String getDesc() {
        return """
                查询我有权限的miapi项目或查询我的项目列表。
                """;
    }

    @Override
    public String getToolScheme() {
        return """
            {
                "type": "object",
                "properties": {
                    "userName": {
                        "type": "string",
                        "description": "用户名(非必填)"
                    }
                }
            }
            """;
    }
}