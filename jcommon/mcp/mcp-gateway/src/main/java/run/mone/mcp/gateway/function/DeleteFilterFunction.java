package run.mone.mcp.gateway.function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.gateway.service.GatewayService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteFilterFunction implements McpFunction {

    private final GatewayService gatewayService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "id": {
                        "type": "string",
                        "description": "The ID of the gateway filter"
                    },
                    "env": {
                        "type": "string",
                        "enum": ["staging","sgpStaging", "online","sgpOnline","eurOnline"],
                        "description": "The Environment for gateway"
                    }
                },
                "required": ["id", "env"]
            }
            """;

    @Override
    public String getName() {
        return "delete_filter";
    }

    @Override
    public String getDesc() {
        return """
                Gateway filter删除工具，用于删除指定环境的filter。
                **使用场景：**
                - 删除中国区测试id为xxx的filter。
                **参数说明：**
                - env网关环境，可选值为staging(中国区测试环境)、sgpStaging(新加坡测试环境)、online(中国区线上环境)、sgpOnline(新加坡线上环境)、eurOnline(欧洲线上环境)，默认值为staging。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    @Override
    @ReportCallCount(businessName = "gateway-filter-delete", description = "Gateway filter删除工具调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("DeleteFilterFunction.apply: {}", arguments);
        String id = getStringValue(arguments, "id");
        if (StringUtils.isBlank(id)) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：缺少必填的filter参数'id'")),
                    true
            ));
        }
        Map<String, Object> params = Map.of(
                "id", id,
                "userName", getStringValue(arguments, Const.TOKEN_USERNAME)
        );
        String result = gatewayService.deleteFilter(getStringValue(arguments, "env"), params);
        return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("删除filter执行结果为："+result)),
                false
        ));
    }

    private String getStringValue(Map<String, Object> arguments, String key) {
        return Optional.ofNullable(arguments.get(key))
                .map(Object::toString)
                .orElse("");
    }
}
