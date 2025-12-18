package run.mone.mcp.miline.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Get Pipeline Detail Function
 *
 * 用于根据流水线ID查询流水线的详细信息
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class GetPipelineDetailFunction implements McpFunction {

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GET_PIPELINE_DETAIL_URL = BASE_URL + "/pipeline/detail";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    /**
     * Function名称
     */
    private String name = "get_pipeline_detail";

    /**
     * Function描述
     */
    private String desc = "根据项目ID和流水线ID查询流水线的详细信息";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "string",
                        "description": "项目ID"
                    },
                    "pipelineId": {
                        "type": "string",
                        "description": "流水线ID"
                    }
                },
                "required": ["projectId", "pipelineId"]
            }
            """;

    public GetPipelineDetailFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "get-pipeline-detail", description = "查询流水线详细信息")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String projectIdStr = getStringParam(args, "projectId");
                String pipelineIdStr = getStringParam(args, "pipelineId");

                // 验证必填参数
                if (projectIdStr.isEmpty()) {
                    log.warn("projectId 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：projectId不能为空")), true));
                }

                if (pipelineIdStr.isEmpty()) {
                    log.warn("pipelineId 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：pipelineId不能为空")), true));
                }

                // 转换为Long类型
                Long projectId;
                Long pipelineId;
                try {
                    projectId = Long.parseLong(projectIdStr);
                    pipelineId = Long.parseLong(pipelineIdStr);
                } catch (NumberFormatException e) {
                    log.error("ID格式不正确", e);
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：ID必须是数字")), true));
                }

                log.info("开始查询流水线详情，projectId: {}, pipelineId: {}", projectId, pipelineId);

                // 查询流水线详情
                Map<String, Object> pipelineDetail = getPipelineDetail(projectId, pipelineId);

                if (pipelineDetail != null) {
                    // 将详情转换为格式化的JSON字符串
                    String detailJson = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(pipelineDetail);
                    String resultMsg = "成功查询流水线详情:\n" + detailJson;
                    log.info("成功查询流水线详情");
                    return createSuccessFlux(resultMsg);
                } else {
                    log.error("未找到流水线详情");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("未找到流水线详情")), true));
                }

            } catch (Exception e) {
                log.error("查询流水线详情操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("查询流水线详情失败：" + e.getMessage())), true));
            }
        });
    }

    /**
     * 查询流水线详情
     *
     * @param projectId  项目ID
     * @param pipelineId 流水线ID
     * @return 流水线详情Map
     * @throws Exception 查询异常
     */
    private Map<String, Object> getPipelineDetail(Long projectId, Long pipelineId) throws Exception {
        // 构建请求体
        List<Object> requestBody = List.of(projectId, pipelineId);

        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        log.info("getPipelineDetail request: {}", requestBodyStr);

        RequestBody body = RequestBody.create(
                requestBodyStr,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(GET_PIPELINE_DETAIL_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            log.info("getPipelineDetail response: {}", responseBody);

            // 解析API响应
            ApiResponse<Map<String, Object>> apiResponse = objectMapper.readValue(
                    responseBody,
                    objectMapper.getTypeFactory().constructParametricType(
                            ApiResponse.class,
                            objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
                    )
            );

            if (apiResponse.getCode() != 0) {
                throw new Exception("API error: " + apiResponse.getMessage());
            }

            return apiResponse.getData();
        }
    }

    /**
     * 创建成功响应的Flux
     * @param result 操作结果
     * @return 包含结果和完成标记的Flux
     */
    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false)
        );
    }

    /**
     * 安全地从参数映射中获取字符串参数
     * @param params 参数映射
     * @param key 参数键
     * @return 字符串参数值，如果不存在则返回空字符串
     */
    private String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * API响应封装
     */
    @Data
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
    }
}
