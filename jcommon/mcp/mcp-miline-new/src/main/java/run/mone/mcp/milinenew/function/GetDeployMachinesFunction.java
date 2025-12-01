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
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import scala.Int;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 查看流水线部署机器信息工具
 * 用于获取当前流水线部署的机器列表
 *
 * @author liguanchen
 * @date 2025/12/01
 */
@Slf4j
@Component
public class GetDeployMachinesFunction implements McpFunction {

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
                    "pipelineId": {
                        "type": "number",
                        "description": "流水线ID（必填）"
                    },
                    "executionId": {
                        "type": "number",
                        "description": "流水线ID（选填）"
                    }
                },
                "required": ["projectId", "pipelineId"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GET_DEPLOY_MACHINES_URL = BASE_URL != null ? "http://mione-gw.test.mi.com/mtop/miline" + "/qryDeployCurrentMachines" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GetDeployMachinesFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("GetDeployMachines arguments: {}", arguments);

        try {
            if (BASE_URL == null || GET_DEPLOY_MACHINES_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object projectIdObj = arguments.get("projectId");
            Object pipelineIdObj = arguments.get("pipelineId");
            Object executionId = arguments.get("executionId") == null ? 0 : arguments.get("executionId");

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
            Integer execId = convertToInteger(executionId);

            Map<String, Object> userMap = new HashMap<>();
            List<Object> requestBody = List.of(projectId, pipelineId, execId);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("getDeployMachines request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(GET_DEPLOY_MACHINES_URL)
                    .post(body)
                    .build();

            OkHttpClient deployMachinesClient = client.newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build();

            try (Response response = deployMachinesClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("getDeployMachines response: {}", responseBody);

                ApiResponse<List<Map<String, Object>>> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class))
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                List<Map<String, Object>> data = apiResponse.getData();

                // 格式化机器信息输出
                String resultText = formatMachineInfo(data);
                System.out.println("resultText:");
                System.out.println(resultText);
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
            log.error("执行get_deploy_machines操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 格式化机器信息输出
     */
    private String formatMachineInfo(List<Map<String, Object>> data) {
        StringBuilder sb = new StringBuilder("流水线部署机器信息：\n\n");

        if (data == null || data.isEmpty()) {
            return "暂无部署机器信息";
        }

        // 遍历每台机器的信息
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> machine = data.get(i);
            sb.append(String.format("机器 %d:\n", i + 1));
            machine.forEach((key, value) -> {
                sb.append(String.format("  %s: %s\n", key, value));
            });
            sb.append("\n");
        }

        return sb.toString().trim();
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
        return "get_deploy_machines";
    }

    @Override
    public String getDesc() {
        return """
                查看流水线部署机器信息。
                
                **使用场景：**
                - 获取当前流水线部署的机器列表，以便输出机器信息等操作。
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
