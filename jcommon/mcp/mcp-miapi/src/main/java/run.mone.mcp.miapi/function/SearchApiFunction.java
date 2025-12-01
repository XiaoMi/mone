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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SearchApiFunction implements McpFunction {
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

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public SearchApiFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
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
            userMap.put("userName", "");
            String params = objectMapper.writeValueAsString(userMap);
            log.info("keyword request: {}", params);
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(
                    params.getBytes(StandardCharsets.UTF_8),
                    JSON
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/mtop/miapitest/getApiList")
                    .post(body)
                    .build();

            OkHttpClient miapiClient = client.newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
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

                String resultText = String.format("查询到的接口信息为: %s", apiResponse.getData());

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