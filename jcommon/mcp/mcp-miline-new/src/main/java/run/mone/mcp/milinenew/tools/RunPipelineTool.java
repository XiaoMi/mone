package run.mone.mcp.milinenew.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.milinenew.params.RunPipelineParams;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RunPipelineTool implements ITool {

    @Value("${git.email.suffix}")
    private String gitUserName;

    public static final String name = "run_pipeline";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String RUN_PIPELINE_URL = BASE_URL != null ? "http://mione-gw.test.mi.com/mtop/miline" + "/runPipelineWithLatestCommit" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public RunPipelineTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
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
                - pipelineId: (必填) 流水线ID
                - commitId: (选填) git的commitId，不填则为最新一次的commitId
                - runType: (选填) 运行类型，不填则为commitId模式，值可以为commitId=>commitId或者应用变更=>changes
                - changeIds: (选填) changeIds，为字符串形式，如1,2,3，多个值用逗号分隔，仅runType为changes时有效（不提供也可以）
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
                %s
                </run_pipeline>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例: 运行流水线
                <run_pipeline>
                <projectId>12345</projectId>
                <pipelineId>67890</pipelineId>
                </run_pipeline>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            if (BASE_URL == null || RUN_PIPELINE_URL == null) {
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
            String commitId = inputJson.has("commitId") && StringUtils.isNotBlank(inputJson.get("commitId").getAsString())
                    ? inputJson.get("commitId").getAsString()
                    : null;
            String runType = inputJson.has("runType") && StringUtils.isNotBlank(inputJson.get("runType").getAsString())
                    ? inputJson.get("runType").getAsString()
                    : "commitId";
            String changeIds = inputJson.has("changeIds") && StringUtils.isNotBlank(inputJson.get("changeIds").getAsString()) ?
                    inputJson.get("commitId").getAsString() : null;


            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", gitUserName);
            userMap.put("userType", 0);

            RunPipelineParams params = RunPipelineParams.builder()
                    .projectId(projectId)
                    .pipelineId(pipelineId)
                    .commitId(commitId)
                    .runType(runType)
                    .changeIds(changeIds != null ? Arrays.stream(changeIds.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .toList() : Collections.emptyList())
                    .build();

            List<Object> requestBody = List.of(userMap, params);
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
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
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

                result.addProperty("pipelineRecordId", pipelineRecordId);
                result.addProperty("url", url);
                result.addProperty("result", String.format("成功触发流水线，执行ID: %d，URL: %s", pipelineRecordId, url));
                return result;
            }
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