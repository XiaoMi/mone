package run.mone.mcp.miapi.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.bo.ApiResponse;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MiApiFunction implements McpFunction {
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

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public MiApiFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
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
            String params = objectMapper.writeValueAsString(userMap);
            log.info("projectName request: {}", params);
            RequestBody body = RequestBody.create(
                    params,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/mtop/miapitest/getProjectByName")
                    .post(body)
                    .build();

            OkHttpClient miapiClient = client.newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build();

            try (Response response = miapiClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("miapi mcp response: {}", responseBody);

                ApiResponse<Map<String,Object>> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Map.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                String resultText = String.format("miapi项目信息: %s", apiResponse.getData());

                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(resultText)),
                        false
                ));
            }
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
        return "miapi_project";
    }

    @Override
    public String getDesc() {
        return """
                根据项目(组)名称，查询miapi项目信息。
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}