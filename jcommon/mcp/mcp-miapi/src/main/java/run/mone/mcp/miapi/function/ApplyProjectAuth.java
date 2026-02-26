package run.mone.mcp.miapi.function;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class ApplyProjectAuth implements McpFunction {
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "string",
                        "description": "miapi项目id"
                    }
                }
            }
            """;

    @Autowired
    private HttpUtils httpUtils;

    private static final String BASE_URL = System.getenv("gateway_host");

    @Override
    @ReportCallCount(businessName = "miapi-api-apply_project_auth", description = "申请项目权限")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("miapi mcp arguments: {}", arguments);
        Object projectId = arguments.get("projectId");
        if (projectId == null) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：缺少必填参数'projectId'")),
                    true
            ));
        }


        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("userName", Optional.ofNullable((String) arguments.get(Const.TOKEN_USERNAME)).orElse(""));
        try {
            String prompt = httpUtils.request("/mtop/miapi/applyProjectAuth", params, Object.class);
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
        return "apply_project_auth";
    }

    @Override
    public String getDesc() {
        return """
                根据miapi项目id申请项目权限。
                工具说明：用户没有主动发起项目权限申请，则不允许自动执行此工具。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
