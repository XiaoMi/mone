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
import run.mone.mcp.milinenew.params.UserBatchParams;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 灰度部署(堡垒批次发布)工具
 * 用于执行流水线的灰度批次发布
 *
 * @author liguanchen
 * @date 2025/12/11
 */
@Slf4j
@Component
public class UserBatchFunction implements McpFunction {

    @Value("${git.default.username}")
    private String gitUserName;

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "number",
                        "description": "项目ID(必填)"
                    },
                    "pipelineId": {
                        "type": "number",
                        "description": "流水线ID(必填)"
                    },
                    "pipelineRecordId": {
                        "type": "number",
                        "description": "流水线运行记录ID(必填)"
                    },
            
                    "operation": {
                        "type": "number",
                        "description": "操作类型(必填),默认为1"
                    },
                    "forceCheck": {
                        "type": "boolean",
                        "description": "是否强制检查(选填),默认为false"
                    }
                },
                "required": ["projectId", "pipelineId", "pipelineRecordId"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String USER_BATCH_URL = BASE_URL != null ? BASE_URL + "/userBatch" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public UserBatchFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @ReportCallCount(businessName = "miline-mcp-userBatch", description = "miline-mcp-灰度部署")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("UserBatch arguments: {}", arguments);

        try {
            if (BASE_URL == null || USER_BATCH_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object projectIdObj = arguments.get("projectId");
            Object pipelineIdObj = arguments.get("pipelineId");
            Object pipelineRecordIdObj = arguments.get("pipelineRecordId");
//            Object batchNumObj = 0;
            Object operationObj = arguments.get("operation");
            Object forceCheckObj = arguments.get("forceCheck");

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
            if (pipelineRecordIdObj == null || StringUtils.isBlank(pipelineRecordIdObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'pipelineRecordId'")),
                        true
                ));
            }
//            if (batchNumObj == null || StringUtils.isBlank(batchNumObj.toString())) {
//                return Flux.just(new McpSchema.CallToolResult(
//                        List.of(new McpSchema.TextContent("错误：缺少必填参数'batchNum'")),
//                        true
//                ));
//            }

            Long projectId = convertToLong(projectIdObj);
            Long pipelineId = convertToLong(pipelineIdObj);
            Long pipelineRecordId = convertToLong(pipelineRecordIdObj);
//            Integer batchNum = convertToInteger(batchNumObj);
            Integer operation = operationObj != null ? convertToInteger(operationObj) : 1;
            Boolean forceCheck = forceCheckObj != null ? Boolean.parseBoolean(forceCheckObj.toString()) : false;

            // 构建请求体
            String tokenUsername = (String) arguments.get(Const.TOKEN_USERNAME);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", StringUtils.isNotBlank(tokenUsername) ? tokenUsername : gitUserName);
            userMap.put("userType", 0);

            UserBatchParams params = UserBatchParams.builder()
                    .projectId(projectId)
                    .pipelineId(pipelineId)
                    .pipelineRecordId(pipelineRecordId)
                    .batchNum(0)
                    .operation(operation)
                    .forceCheck(forceCheck)
                    .build();
            List<Object> requestBody = List.of(userMap, params);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("userBatch request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(USER_BATCH_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("userBatch response: {}", responseBody);

                ApiResponse<Boolean> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Boolean.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                Boolean success = apiResponse.getData();
                String resultText = success
                        ? String.format("堡垒批次部署批次执行成功，后续请直接进行k8s批次部署")
                        : String.format("灰度部署批次执行失败");

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
            log.error("执行user_batch操作时发生异常", e);
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
        return "user_batch";
    }

    @Override
    public String getDesc() {
        return """
                灰度部署-(堡垒批次确认)工具，仅用于执行流水线的堡垒批次的确认工作。
                
                **使用场景：**
                - 执行灰度部署的某个批次
                - 控制流水线分批次发布
                - 实现灰度发布策略
                - 降低发布风险
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

