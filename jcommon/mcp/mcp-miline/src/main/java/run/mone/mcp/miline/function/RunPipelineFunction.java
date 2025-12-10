package run.mone.mcp.miline.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * Run Pipeline Function
 *
 * 运行Miline流水线的工具，触发指定项目下指定流水线以最新提交执行
 *
 * @author generated
 * @date 2025-12-08
 */
@Data
@Slf4j
@Component
public class RunPipelineFunction implements McpFunction {

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String RUN_PIPELINE_URL = BASE_URL + "/st/startPipeline";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    /**
     * Function名称
     */
    private String name = "run_pipeline";

    /**
     * Function描述
     */
    private String desc = "运行Miline流水线，触发指定项目下指定流水线执行";

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
                        "description": "流水线ID（或者是envId）"
                    },
                    "userName": {
                        "type": "string",
                        "description": "用户名，可以从gitName获取（可选，默认为wangmin17）"
                    }
                },
                "required": ["projectId", "pipelineId"]
            }
            """;

    public RunPipelineFunction() {
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
    @ReportCallCount(businessName = "run-pipeline", description = "运行Miline流水线")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取参数
                String projectIdStr = getStringParam(args, "projectId");
                String pipelineIdStr = getStringParam(args, "pipelineId");
                String userName = getStringParam(args, "userName");

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

                // 转换为Integer类型
                Integer projectId;
                Integer pipelineId;
                try {
                    projectId = Integer.parseInt(projectIdStr);
                    pipelineId = Integer.parseInt(pipelineIdStr);
                } catch (NumberFormatException e) {
                    log.error("ID格式不正确", e);
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：projectId与pipelineId必须是数字")), true));
                }

                // 设置默认用户名
                if (userName.isEmpty()) {
                    userName = "wangmin17";
                }

                log.info("开始运行流水线，projectId: {}, pipelineId: {}, userName: {}",
                        projectId, pipelineId, userName);

                // 执行运行流水线操作
                String result = runPipeline(projectId, pipelineId, userName);

                log.info("成功运行流水线");
                return createSuccessFlux(result);

            } catch (Exception e) {
                log.error("运行流水线操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("运行流水线失败：" + e.getMessage())), true));
            }
        });
    }

    /**
     * 运行流水线（包含重试机制）
     *
     * @param projectId  项目ID
     * @param pipelineId 流水线ID
     * @param userName   用户名
     * @return 执行结果描述
     * @throws Exception 执行异常
     */
    private String runPipeline(Integer projectId, Integer pipelineId, String userName) throws Exception {
        Map<String, String> userMap = Map.of("baseUserName", userName, "userType", "0");
        List<Object> requestBody = List.of(userMap, projectId, pipelineId);
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        log.info("runPipeline request: {}", requestBodyStr);

        RequestBody body = RequestBody.create(
                requestBodyStr,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(RUN_PIPELINE_URL)
                .post(body)
                .build();

        OkHttpClient pipelineClient = client.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();

        // 重试机制：最多重试10次
        int maxRetries = 10;
        String lastErrorMessage = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("尝试第 {} 次调用流水线接口 (共{}次)", attempt, maxRetries);

                try (Response response = pipelineClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        lastErrorMessage = "HTTP response code: " + response.code();
                        log.warn("第 {} 次调用失败: {}", attempt, lastErrorMessage);
                        continue;
                    }

                    if (response.body() == null) {
                        lastErrorMessage = "响应体为空";
                        log.warn("第 {} 次调用失败: {}", attempt, lastErrorMessage);
                        continue;
                    }

                    String responseBody = response.body().string();
                    log.info("runPipeline response (attempt {}): {}", attempt, responseBody);

                    ApiResponse<Integer> apiResponse = objectMapper.readValue(
                            responseBody,
                            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Integer.class)
                    );

                    // 检查是否满足成功条件：code == 1 且 message == "有运行中pipeline, 需先关闭"
                    if (apiResponse.getCode() == 1 &&
                            "有运行中pipeline, 需先关闭".equals(apiResponse.getMessage())) {
                        log.info("第 {} 次调用成功：检测到有运行中的流水线", attempt);
                        return String.format("调用成功（第%d次尝试）：%s", attempt, apiResponse.getMessage());
                    }

                    // 记录未满足成功条件的响应
                    lastErrorMessage = String.format("code=%d, message=%s",
                            apiResponse.getCode(), apiResponse.getMessage());
                    log.warn("第 {} 次调用返回但未满足成功条件: {}", attempt, lastErrorMessage);

                    // 如果不是最后一次尝试，等待一段时间后重试
                    if (attempt < maxRetries) {
                        Thread.sleep(500); // 等待500ms后重试
                    }
                }
            } catch (IOException e) {
                lastErrorMessage = "IO异常: " + e.getMessage();
                log.warn("第 {} 次调用发生IO异常: {}", attempt, e.getMessage());
                if (attempt < maxRetries) {
                    Thread.sleep(500); // 等待500ms后重试
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }

        // 重试10次都没有成功
        log.error("重试 {} 次后仍未成功，最后错误: {}", maxRetries, lastErrorMessage);
        throw new Exception(String.format("重试%d次后仍未成功，最后错误: %s", maxRetries, lastErrorMessage));
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
        private String detailMsg;
    }
}
