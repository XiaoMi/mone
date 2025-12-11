package run.mone.mcp.miapi.function;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@Slf4j
@Component
public class AddMiApiConfig implements McpFunction {
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "jdkVersion": {
                        "type": "string",
                        "description": "当前java项目的jdk版本"
                    }
                }
            }
            """;

    @Autowired
    private HttpUtils httpUtils;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-add_miapi_config", description = "为当前java项目添加MiApi依赖和相关注解")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        Map<String, Object> params = new HashMap<>();
        params.put("id", "add_config_prompt");
        params.put("userName", Optional.ofNullable((String) arguments.get(Const.TOKEN_USERNAME)).orElse(""));
        try {
            String prompt = httpUtils.request("/mtop/miapi/getConfig", params, String.class);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(prompt)),
                    false
            ));
        } catch (JsonProcessingException e) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(e.getMessage())),
                    true
            ));
        }
    }

    @Override
    public String getName() {
        return "add_miapi_config";
    }

    @Override
    public String getDesc() {
        return """
                在当前项目中增加或更新MiApi依赖配置。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
