package run.mone.mcp.miline.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GetPipelineMachinesTool implements ITool {

    public static final String name = "get_machines";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GET_MACHINES_URL = BASE_URL + "/qryDeployInfo";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GetPipelineMachinesTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
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
                获取流水线运行机器信息的工具，查询指定项目下指定流水线的部署机器信息。
                
                **使用场景：**
                - 查看流水线部署到哪些机器上
                - 监控流水线的运行环境
                - 排查部署问题时的环境信息
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) 项目ID
                - pipelineId: (必填) 流水线ID
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
                <get_machines>
                <projectId>项目ID</projectId>
                <pipelineId>流水线ID</pipelineId>
                %s
                </get_machines>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例: 获取流水线机器信息
                <get_machines>
                <projectId>12345</projectId>
                <pipelineId>67890</pipelineId>
                </get_machines>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            if (!inputJson.has("projectId") || StringUtils.isBlank(inputJson.get("projectId").getAsString())) {
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }
            if (!inputJson.has("pipelineId") || StringUtils.isBlank(inputJson.get("pipelineId").getAsString())) {
                result.addProperty("error", "缺少必填参数'pipelineId'");
                return result;
            }

            Long projectId = Long.parseLong(inputJson.get("projectId").getAsString());
            Long pipelineId = Long.parseLong(inputJson.get("pipelineId").getAsString());

            // 构建请求体，将参数作为数组发送
            JsonArray requestBody = new JsonArray();
            requestBody.add(projectId);
            requestBody.add(pipelineId);
            
            String requestBodyStr = requestBody.toString();
            log.info("getMachines request body: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                requestBodyStr,
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(GET_MACHINES_URL)
                .post(body)
                .build();

            OkHttpClient machinesClient = client.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();

            try (Response response = machinesClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("getMachines response: {}", responseBody);

                ApiResponse<Object> apiResponse = objectMapper.readValue(
                    responseBody,
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Object.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                String machinesJson = objectMapper.writeValueAsString(apiResponse.getData());
                result.addProperty("machines", machinesJson);
                
                // 提取deployMachines中的所有IP地址
                List<String> ipList = extractDeployMachineIps(machinesJson);
                result.addProperty("deployMachineIps", String.join(",", ipList));
                result.addProperty("result", "成功获取机器信息");
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("项目ID或流水线ID格式不正确", e);
            result.addProperty("error", "'projectId'与'pipelineId'必须是数字");
            return result;
        } catch (Exception e) {
            log.error("执行get_machines操作时发生异常", e);
            result.addProperty("error", "执行操作失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 从机器信息JSON中提取deployMachines的所有IP地址
     * @param machinesJson 机器信息的JSON字符串
     * @return IP地址列表
     */
    private List<String> extractDeployMachineIps(String machinesJson) {
        List<String> ipList = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(machinesJson);
            JsonNode deployMachinesNode = rootNode.get("deployMachines");
            
            if (deployMachinesNode != null && deployMachinesNode.isArray()) {
                for (JsonNode machineNode : deployMachinesNode) {
                    JsonNode ipNode = machineNode.get("ip");
                    if (ipNode != null && !ipNode.isNull()) {
                        String ip = ipNode.asText();
                        if (StringUtils.isNotBlank(ip)) {
                            ipList.add(ip);
                        }
                    }
                }
            }
            
            log.info("提取到{}个部署机器IP地址: {}", ipList.size(), ipList);
        } catch (Exception e) {
            log.error("解析机器信息JSON时发生异常", e);
        }
        return ipList;
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