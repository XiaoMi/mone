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
public class CheckMiApiConfig implements McpFunction {

    @Autowired
    private HttpUtils httpUtils;
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

    @Override
    @ReportCallCount(businessName = "miapi-api-check_miapi_config", description = "检查当前java项目MiApi配置")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        Map<String, Object> params = new HashMap<>();
        params.put("id", "check_prompt");
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
        return "check_miapi_config";
    }

    @Override
    public String getDesc() {
        return """
                检查当前java项目中有关MiApi依赖配置和使用是否正确，不进行MiApi配置添加或更新操作。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
