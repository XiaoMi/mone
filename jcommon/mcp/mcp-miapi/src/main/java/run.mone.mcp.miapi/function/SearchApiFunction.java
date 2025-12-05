package run.mone.mcp.miapi.function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class SearchApiFunction implements McpFunction {

    @Autowired
    private HttpUtils httpUtils;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "keyword": {
                        "type": "string",
                        "description": "接口关键字"
                    },
                    "protocol": {
                        "type": "string",
                        "description": "接口类型，http为1，dubbo为3，可不指定类型"
                    }
                },
                "required": ["keyword"]
            }
            """;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-query_api", description = "查询接口详情")
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
            Object keyword = arguments.get("keyword");
            Object protocol = arguments.get("protocol");

            if (keyword == null || StringUtils.isBlank(keyword.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'keyword'")),
                        true
                ));
            }

            if (protocol == null || StringUtils.isBlank(protocol.toString())) {
                protocol = "";
            }

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("keyword", keyword);
            userMap.put("protocol", protocol);
            userMap.put("userName", Optional.ofNullable((String) arguments.get(Const.TOKEN_USERNAME)).orElse(""));
            String resultText = httpUtils.request("/mtop/miapi/getApiList", userMap, Map.class);
            resultText = String.format("查询到的接口信息为: %s", resultText);
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
        return "query_api";
    }

    @Override
    public String getDesc() {
        return """
                根据关键字（path或apiName）查询接口信息。
                如：帮我查询dubbo的user接口。
                如：帮我查询http的user接口。
                如：帮我查询userinfo接口。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}