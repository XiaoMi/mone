package run.mone.mcp.milinenew.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.milinenew.params.CancelPipelineParams;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 取消流水线工具
 * 用于取消正在运行的流水线
 *
 * @author liguanchen
 * @date 2025/12/17
 */
@Slf4j
@Component
public class CancelPipelineFunction implements McpFunction {

    @Value("${git.default.username}")
    private String gitUserName;

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "pipelineId": {
                        "type": "number",
                        "description": "流水线ID（必填）"
                    },
                    "pipelineRecordId": {
                        "type": "number",
                        "description": "流水线运行记录ID（必填）"
                    }
                },
                "required": ["pipelineId", "pipelineRecordId"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String CANCEL_PIPELINE_URL = BASE_URL != null ? BASE_URL + "/cancelPipeline" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public CancelPipelineFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @ReportCallCount(businessName = "miline-mcp-cancelPipeline", description = "miline-mcp-取消流水线")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("CancelPipeline arguments: {}", arguments);

        try {
            if (BASE_URL == null || CANCEL_PIPELINE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object pipelineIdObj = arguments.get("pipelineId");
            Object pipelineRecordIdObj = arguments.get("pipelineRecordId");

            if (pipelineIdObj == null || StringUtils.isBlank(pipelineIdObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'pipelineId'")),
                        true
                ));
            }
            if (pipelineRecordIdObj == null || StringUtils.isBlank(pipelineRecordIdObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'pipelineRecordId'")),
                        true
                ));
            }

            Long pipelineId = convertToLong(pipelineIdObj);
            Long pipelineRecordId = convertToLong(pipelineRecordIdObj);
            String tokenUsername = (String) arguments.get(Const.TOKEN_USERNAME);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", StringUtils.isNotBlank(tokenUsername) ? tokenUsername : gitUserName);
            userMap.put("userType", 0);

            CancelPipelineParams params = CancelPipelineParams.builder()
                    .pipelineId(pipelineId)
                    .pipelineRecordId(pipelineRecordId)
                    .build();

            List<Object> requestBody = List.of(userMap, params);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("cancelPipeline request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(CANCEL_PIPELINE_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("cancelPipeline response: {}", responseBody);

                ApiResponse<Boolean> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Boolean.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                Boolean success = apiResponse.getData();
                String resultText = success 
                    ? String.format("成功取消流水线，流水线ID: %d，运行记录ID: %d", pipelineId, pipelineRecordId)
                    : String.format("取消流水线失败，流水线ID: %d，运行记录ID: %d", pipelineId, pipelineRecordId);

                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(resultText)),
                        !success
                ));
            }
        } catch (NumberFormatException e) {
            log.error("参数格式不正确", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：参数格式必须是数字")),
                    true
            ));
        } catch (Exception e) {
            log.error("执行cancel_pipeline操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 将对象转换为 Long 类型
     */
    private Long convertToLong(Object obj) {
        if (obj instanceof Number n) {
            return n.longValue();
        } else if (obj instanceof String) {
            return Long.parseLong((String) obj);
        } else {
            throw new IllegalArgumentException("无法将 " + obj + " 转换为长整数");
        }
    }

    @Override
    public String getName() {
        return "cancel_pipeline";
    }

    @Override
    public String getDesc() {
        return """
                取消流水线工具，用于取消正在运行的流水线。
                
                **使用场景：**
                - 取消正在运行的流水线
                - 停止错误的部署流程
                - 中断长时间运行的任务
                - 释放流水线占用的资源
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
        private String detailMsg;
    }
}
