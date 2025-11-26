package run.mone.mcp.milinenew.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Git代码生成工具
 * 用于在Miline平台为指定项目生成Git仓库代码
 *
 * @author goodjava@qq.com
 * @date 2025/1/17
 */
@Slf4j
public class GenerateGitCodeFunction implements McpFunction {
    @Value("${git.email.suffix}")
    private String gitUserName;
    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "number",
                        "description": "项目ID（必填）"
                    },
                    "env": {
                        "type": "string",
                        "description": "环境（必填）"
                    }
                },
                "required": ["projectId", "env"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GENERATE_GIT_CODE_URL = BASE_URL + "/generateCode";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GenerateGitCodeFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("GenerateGitCode arguments: {}", arguments);

        try {
            // 验证必填参数
            Object projectIdObj = arguments.get("projectId");
            Object envObj = arguments.get("env");

            if (projectIdObj == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'projectId'")),
                        true
                ));
            }

            if (envObj == null || StringUtils.isBlank(envObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'env'")),
                        true
                ));
            }

            // 解析参数
            Integer projectId = convertToInteger(projectIdObj);
            String env = envObj.toString();

            // 构建请求体
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("baseUserName", gitUserName);
            requestMap.put("userType", 0);

            List<Object> requestBody = List.of(requestMap, projectId, env);

            log.info("generateGitCode request: {}", requestBody);

            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(GENERATE_GIT_CODE_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = null;
                if (response.body() != null) {
                    responseBody = response.body().string();
                }
                log.info("generateGitCode response: {}", responseBody);

                ApiResponse<GitCodeGenerationResult> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, GitCodeGenerationResult.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                log.info("generateGitCode response: {}", apiResponse);
                String resultText = String.format(
                        "生成代码成功！\n" +
                                "- Git仓库地址: %s\n" +
                                "- 流水线名称: %s\n" +
                                "- Git名称: %s",
                        apiResponse.getData().getGitUrl(),
                        apiResponse.getData().getPipelineName(),
                        apiResponse.getData().getGitName()
                );

                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(resultText)),
                        false
                ));
            }
        } catch (NumberFormatException e) {
            log.error("项目ID格式不正确", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：'projectId'必须是数字")),
                    true
            ));
        } catch (Exception e) {
            log.error("执行generate_git_code操作时发生异常", e);
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
        return "generate_git_code";
    }

    @Override
    public String getDesc() {
        return """
                Git代码生成工具，为指定项目生成初始化的Git仓库代码。

                **使用场景：**
                - 为新创建的项目生成初始代码结构
                - 初始化项目的Git仓库
                - 自动创建项目模板代码
                - 创建完成后自动提交代码
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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GitCodeGenerationResult {
        private String pipelineName;
        private String gitUrl;
        private String gitName;
    }
}
