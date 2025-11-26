package run.mone.mcp.milinenew.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 运行Miline流水线的工具，触发指定项目下指定流水线以最新提交执行
 *
 * @author liguanchen
 * @date 2025/11/18
 */
@Slf4j
public class RunPipelineFunction implements McpFunction {
    private final String gitUserName;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "number",
                        "description": "项目ID（必填）"
                    },
                    "pipelineId": {
                        "type": "number",
                        "description": "流水线ID（必填）"
                    }
                },
                "required": ["projectId", "pipelineId"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String RUN_PIPELINE_URL = BASE_URL != null ? BASE_URL + "/runPipelineWithLatestCommit" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public RunPipelineFunction() {
        this(null);
    }

    public RunPipelineFunction(String gitUserName) {
        this.gitUserName = gitUserName;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("RunPipeline arguments: {}", arguments);

        try {
            if (BASE_URL == null || RUN_PIPELINE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object projectIdObj = arguments.get("projectId");
            Object pipelineIdObj = arguments.get("pipelineId");

            if (projectIdObj == null || StringUtils.isBlank(projectIdObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'projectId'")),
                        true
                ));
            }
            if (pipelineIdObj == null || StringUtils.isBlank(pipelineIdObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'pipelineId'")),
                        true
                ));
            }

            Integer projectId = convertToInteger(projectIdObj);
            Integer pipelineId = convertToInteger(pipelineIdObj);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", gitUserName);
            userMap.put("userType", 0);
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

            try (Response response = pipelineClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("runPipeline response: {}", responseBody);

                ApiResponse<Map<String, Object>> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, 
                            objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class))
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                Map<String, Object> data = apiResponse.getData();
                Integer pipelineRecordId = (Integer) data.get("pipelineRecordId");
                String url = (String) data.get("url");
                
                String resultText = String.format("成功触发流水线，执行ID: %d，URL: %s", pipelineRecordId, url);

                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(resultText)),
                        false
                ));
            }
        } catch (NumberFormatException e) {
            log.error("项目ID或流水线ID格式不正确", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：'projectId'与'pipelineId'必须是数字")),
                    true
            ));
        } catch (Exception e) {
            log.error("执行run_pipeline操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 将对象转换为 Integer 类型
     */
    private Integer convertToInteger(Object obj) {
        if (obj instanceof Number n) {
            return n.intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else {
            throw new IllegalArgumentException("无法将 " + obj + " 转换为整数");
        }
    }

    @Override
    public String getName() {
        return "run_pipeline";
    }

    @Override
    public String getDesc() {
        return """
                运行Miline流水线的工具，触发指定项目下指定流水线以最新提交执行。

                **使用场景：**
                - 需要在CI/CD中触发某个流水线
                - 验证最近一次提交是否能通过流水线
                - 集成到自动化流程中进行构建/部署
                - 发布/部署系统
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
