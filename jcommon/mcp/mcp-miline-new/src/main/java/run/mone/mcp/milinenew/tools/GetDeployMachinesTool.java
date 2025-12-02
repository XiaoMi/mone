package run.mone.mcp.milinenew.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GetDeployMachinesTool implements ITool {

    public static final String name = "get_deploy_machines";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GET_DEPLOY_MACHINES = BASE_URL != null ? "http://mione-gw.test.mi.com/mtop/miline" + "/qryDeployCurrentMachines" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GetDeployMachinesTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                查看流水线部署机器信息。
                
                **使用场景：**
                - 获取当前流水线部署的机器列表，以便输出机器信息等操作。
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) 项目ID
                - pipelineId: (必填) 流水线ID
                - pipelineRecordId: (可填) 流水线运行记录ID
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                Checklist here (optional)
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <get_deploy_machines>
                <projectId>项目ID</projectId>
                <pipelineId>流水线ID</pipelineId>
                <pipelineRecordId>流水线记录ID（可选）</pipelineRecordId>
                %s
                </get_deploy_machines>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例: 查看流水线部署机器信息
                <get_deploy_machines>
                <projectId>12345</projectId>
                <pipelineId>67890</pipelineId>
                <pipelineRecordId>111213</pipelineRecordId>
                </get_deploy_machines>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            if (BASE_URL == null || GET_DEPLOY_MACHINES == null) {
                result.addProperty("error", "配置错误: req_base_url 环境变量未设置");
                log.error("req_base_url 环境变量未设置，无法执行流水线操作");
                return result;
            }
            if (!inputJson.has("projectId") || StringUtils.isBlank(inputJson.get("projectId").getAsString())) {
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }
            if (!inputJson.has("pipelineId") || StringUtils.isBlank(inputJson.get("pipelineId").getAsString())) {
                result.addProperty("error", "缺少必填参数'pipelineId'");
                return result;
            }

            Integer projectId = Integer.parseInt(inputJson.get("projectId").getAsString());
            Integer pipelineId = Integer.parseInt(inputJson.get("pipelineId").getAsString());
            Integer pipelineRecordId = inputJson.has("pipelineRecordId") ? Integer.parseInt(inputJson.get("pipelineRecordId").getAsString()) : 0;

            Map<String, Object> userMap = new HashMap<>();
            List<Object> requestBody = List.of(projectId, pipelineId, pipelineRecordId);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("get_deploy_machines request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(GET_DEPLOY_MACHINES)
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
                log.info("get_deploy_machines response: {}", responseBody);

                ApiResponse<Object> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Object.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                Object data = apiResponse.getData();
                String resultText;

                if (data instanceof Map) {
                    resultText = formatMachineInfo((Map<String, Object>) data);
                } else if (data instanceof List) {
                    try {
                        resultText = formatMachineList((List<Map<String, Object>>) data);
                    } catch (ClassCastException e) {
                        log.error("数据格式错误: List 中的元素不是 Map 类型", e);
                        resultText = "数据格式错误: List 中的元素不是 Map 类型";
                    }
                } else {
                    resultText = "未知的数据格式: " + data;
                }

                result.addProperty("result", resultText);
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("项目ID或流水线ID格式不正确", e);
            result.addProperty("error", "'projectId'与'pipelineId'必须是数字");
            return result;
        } catch (Exception e) {
            log.error("执行get_deploy_machines操作时发生异常", e);
            result.addProperty("error", "执行操作失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 格式化机器信息输出
     */
    private String formatMachineInfo(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder("流水线部署机器信息：\n\n");

        if (data == null || data.isEmpty()) {
            return "暂无部署机器信息";
        }

        data.forEach((key, value) -> {
            sb.append(String.format("  %s: %s\n", key, value));
        });
        sb.append("\n");

        return sb.toString().trim();
    }

    /**
     * 格式化机器列表信息输出
     */
    private String formatMachineList(List<Map<String, Object>> machineList) {
        StringBuilder sb = new StringBuilder("流水线部署机器信息列表：\n\n");

        if (machineList == null || machineList.isEmpty()) {
            return "暂无部署机器信息";
        }

        for (Map<String, Object> machine : machineList) {
            sb.append("机器信息:\n");
            machine.forEach((key, value) -> {
                sb.append(String.format("  %s: %s\n", key, value));
            });
            sb.append("\n");
        }

        return sb.toString().trim();
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