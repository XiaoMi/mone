package run.mone.mcp.miline.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RunPipelineTool implements ITool {

    public static final String name = "run_pipeline";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String RUN_PIPELINE_URL = BASE_URL + "/st/startPipeline";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public RunPipelineTool() {
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
                运行Miline流水线的工具，触发指定项目下指定流水线以最新提交执行。
                
                **使用场景：**
                - 需要在CI/CD中触发某个流水线
                - 验证最近一次提交是否能通过流水线
                - 集成到自动化流程中进行构建/部署
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) 项目ID
                - pipelineId: (必填) 流水线ID（或者是envId）
                - userName: (可选) 用户名，可以从gitName获取
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
                <run_pipeline>
                <projectId>项目ID</projectId>
                <pipelineId>流水线ID</pipelineId>
                <userName>用户名(可选)</userName>
                %s
                </run_pipeline>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 运行流水线(使用默认用户名)
                <run_pipeline>
                <projectId>12345</projectId>
                <pipelineId>67890</pipelineId>
                </run_pipeline>

                示例2: 运行流水线(指定用户名)
                <run_pipeline>
                <projectId>12345</projectId>
                <pipelineId>67890</pipelineId>
                <userName>zhangsan</userName>
                </run_pipeline>
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

            Integer projectId = Integer.parseInt(inputJson.get("projectId").getAsString());
            Integer pipelineId = Integer.parseInt(inputJson.get("pipelineId").getAsString());

            // 读取可选的 userName 参数，如果未提供则使用默认值 "wangmin17"
            String userName = "wangmin17";
            if (inputJson.has("userName") && !StringUtils.isBlank(inputJson.get("userName").getAsString())) {
                userName = inputJson.get("userName").getAsString();
            }

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
                            result.addProperty("result", "调用成功：" + apiResponse.getMessage());
                            result.addProperty("code", apiResponse.getCode());
                            result.addProperty("message", "调用成功");
                            result.addProperty("attempts", attempt);
                            return result;
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
            result.addProperty("error", String.format("重试%d次后仍未成功，最后错误: %s", maxRetries, lastErrorMessage));
            result.addProperty("attempts", maxRetries);
            return result;

        } catch (NumberFormatException e) {
            log.error("项目ID或流水线ID格式不正确", e);
            result.addProperty("error", "'projectId'与'pipelineId'必须是数字");
            return result;
        } catch (Exception e) {
            log.error("执行run_pipeline操作时发生异常", e);
            result.addProperty("error", "执行操作失败: " + e.getMessage());
            return result;
        }
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