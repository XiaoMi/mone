package run.mone.mcp.miapi.function;

import lombok.extern.slf4j.Slf4j;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class IndexInfoFunction implements McpFunction {

    @Autowired
    private HttpUtils httpUtils;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectName": {
                        "type": "string",
                        "description": "项目名称（非必填）"
                    }
                }
            }
            """;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-query_index_list", description = "查询接口集合列表")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        try {
            if (BASE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: gateway_host 环境变量未设置")),
                        true
                ));
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("projectName", arguments.get("projectName"));
            userMap.put("userName", Optional.ofNullable((String) arguments.get(Const.TOKEN_USERNAME)).orElse(""));
            String resultText = httpUtils.request("/mtop/miapi/getIndexList", userMap, List.class);
            resultText = String.format("查询到的接口集合列表为: %s", resultText);
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
        return "query_index_list";
    }

    @Override
    public String getDesc() {
        return """
                根据项目名或用户查询接口集合列表
                如：帮我查询我有权限的接口集合。
                如：帮我查询mi-api的接口集合。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
