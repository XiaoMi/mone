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
import run.mone.mcp.milinenew.params.K8sBatchDeployParams;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * K8s批次部署工具
 * 用于执行K8s流水线的批次部署
 *
 * @author liguanchen
 * @date 2025/12/11
 */
@Slf4j
@Component
public class K8sBatchDeployFunction implements McpFunction {

    @Value("${git.default.username}")
    private String gitUserName;

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "batchNum": {
                        "type": "number",
                        "description": "批次号(必填)"
                    },
                    "pipelineRecordId": {
                        "type": "number",
                        "description": "流水线运行记录ID(必填)"
                    },
                    "pipelineDeployId": {
                        "type": "number",
                        "description": "流水线部署ID(选填) 不填为 null"
                    }
                },
                "required": ["batchNum", "pipelineRecordId"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String K8S_BATCH_DEPLOY_URL = BASE_URL != null ? BASE_URL + "/k8sPipelineBatchDeploy" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public K8sBatchDeployFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @ReportCallCount(businessName = "miline-api-k8sBatchDeploy", description = "miline-api批次部署工具调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("K8sBatchDeploy arguments: {}", arguments);

        try {
            if (BASE_URL == null || K8S_BATCH_DEPLOY_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object batchNumObj = arguments.get("batchNum");
            Object pipelineRecordIdObj = arguments.get("pipelineRecordId");
            Object pipelineDeployId = arguments.get("pipelineDeployId");

            if (batchNumObj == null || StringUtils.isBlank(batchNumObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'batchNum'")),
                        true
                ));
            }
            if (pipelineRecordIdObj == null || StringUtils.isBlank(pipelineRecordIdObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'pipelineRecordId'")),
                        true
                ));
            }
//            if (pipelineDeployIdObj == null || StringUtils.isBlank(pipelineDeployIdObj.toString())) {
//                return Flux.just(new McpSchema.CallToolResult(
//                        List.of(new McpSchema.TextContent("错误：缺少必填参数'pipelineDeployId'")),
//                        true
//                ));
//            }
            String tokenUsername = (String) arguments.get(Const.TOKEN_USERNAME);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", StringUtils.isNotBlank(tokenUsername) ? tokenUsername : gitUserName);
            userMap.put("userType", 0);

            Integer batchNum = convertToInteger(batchNumObj);
            Long pipelineRecordId = convertToLong(pipelineRecordIdObj);
//            Long pipelineDeployId = convertToLong(pipelineDeployIdObj);

            // 构建请求体
            K8sBatchDeployParams params = K8sBatchDeployParams
                    .builder()
                    .batchNum(batchNum)
                    .pipelineRecordId(pipelineRecordId)
                    .build();
//            params.setPipelineDeployId(convertToLong(pipelineDeployId));

            if (pipelineDeployId != null) {
                params.setPipelineDeployId(convertToLong(pipelineDeployId));
            }
            List<Object> requestBody = List.of(userMap, params);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("k8sBatchDeploy request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(K8S_BATCH_DEPLOY_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("k8sBatchDeploy response: {}", responseBody);

                ApiResponse<Boolean> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Boolean.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                Boolean success = apiResponse.getData();
                String resultText = success
                        ? String.format("K8s批次 %d 部署成功", batchNum)
                        : String.format("K8s批次 %d 部署失败", batchNum);

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
            log.error("执行k8s_batch_deploy操作时发生异常", e);
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
        return "k8s_batch_deploy";
    }

    @Override
    public String getDesc() {
        return """
                K8s批次部署工具，用于依次执行流水线的批次部署，批次1、批次2、批次xxxx的发布控制（堡垒批次部署成功后使用）。
                
                **使用场景：**
                - 执行K8s流水线的某个批次部署
                - 控制K8s应用分批次发布
                - 实现K8s应用的灰度发布
                - 降低K8s应用发布风险
                - 批次1、批次2、批次xxxx的发布控制
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
