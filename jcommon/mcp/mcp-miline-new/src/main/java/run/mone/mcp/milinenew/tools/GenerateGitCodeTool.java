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

import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.ArrayList;
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
@Component
public class GenerateGitCodeTool implements ITool {

    @Value("${git.default.username}")
    private String gitUserName;
    
    public static final String name = "generate_git_code";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GENERATE_GIT_CODE_URL = BASE_URL + "/generateCode";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GenerateGitCodeTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
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
                Git代码生成工具，为指定项目生成初始化的Git仓库代码。
                
                **使用场景：**
                - 为新创建的项目生成初始代码结构
                - 初始化项目的Git仓库
                - 自动创建项目模板代码
                - 创建完成后自动提交代码
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) 项目ID
                - env: (必填) 环境
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
                <generate_git_code>
                <projectId>项目ID</projectId>
                <env>环境</env>
                </generate_git_code>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 使用默认配置生成Java Maven项目代码
                <generate_git_code>
                <projectId>12345</projectId>
                <projectName>my-service</projectName>
                <env>staging</env>
                </generate_git_code>
                """;
    }

    @Override
    @ReportCallCount(businessName = "miline-agent-generateGitCode", description = "miline-agent-生成Git仓库代码")
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            // 验证必填参数
            if (!inputJson.has("projectId") || StringUtils.isBlank(inputJson.get("projectId").getAsString())) {
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }
//            if (!inputJson.has("projectName") || StringUtils.isBlank(inputJson.get("projectName").getAsString())) {
//                result.addProperty("error", "缺少必填参数'projectName'");
//                return result;
//            }

            // 解析参数
            Integer projectId = Integer.parseInt(inputJson.get("projectId").getAsString());
            String env = inputJson.get("env").getAsString();
//            String projectName = inputJson.get("projectName").getAsString();

//            // 可选参数
//            String projectType = inputJson.has("projectType") && StringUtils.isNotBlank(inputJson.get("projectType").getAsString())
//                    ? inputJson.get("projectType").getAsString() : "java-maven";
//            String templateId = inputJson.has("templateId") && StringUtils.isNotBlank(inputJson.get("templateId").getAsString())
//                    ? inputJson.get("templateId").getAsString() : null;
//            String gitBranch = inputJson.has("gitBranch") && StringUtils.isNotBlank(inputJson.get("gitBranch").getAsString())
//                    ? inputJson.get("gitBranch").getAsString() : "master";
//            String description = inputJson.has("description") && StringUtils.isNotBlank(inputJson.get("description").getAsString())
//                    ? inputJson.get("description").getAsString() : "";

            // 构建请求体
            Map<String, Object> requestMap = new HashMap<>();
//            requestMap.put("projectId", projectId);
//            requestMap.put("projectName", projectName);
//            requestMap.put("projectType", projectType);
//            requestMap.put("gitBranch", gitBranch);
//            requestMap.put("description", description);
            requestMap.put("baseUserName", gitUserName);
            requestMap.put("userType", 0);
            // 可以从环境变量或配置中获取

//            if (StringUtils.isNotBlank(templateId)) {
//                requestMap.put("templateId", templateId);
//            }

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

//                GitCodeGenerationResult data = apiResponse.getData();
//                result.addProperty("gitUrl", data.getGitUrl());
//                result.addProperty("commitId", data.getCommitId());
//                result.addProperty("branch", data.getBranch());
//                result.addProperty("result", String.format(
//                        "成功生成Git代码！\n" +
//                                "- Git仓库地址: %s\n" +
//                                "- 初始提交ID: %s\n" +
//                                "- 分支: %s\n" +
//                                "- 项目类型: %s",
//                        data.getGitUrl(),
//                        data.getCommitId(),
//                        data.getBranch()
////                    projectType
//                ));
                log.info("generateGitCode response: {}", apiResponse);
                result.addProperty("msg", "生成代码成功");
                result.addProperty("gitUrl", apiResponse.getData().gitUrl);
                result.addProperty("pipelineName", apiResponse.getData().pipelineName);
                result.addProperty("gitName", apiResponse.getData().gitName);
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("项目ID格式不正确", e);
            result.addProperty("error", "'projectId'必须是数字");
            return result;
        } catch (Exception e) {
            log.error("执行generate_git_code操作时发生异常", e);
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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GitCodeGenerationResult {
        private String pipelineName;
        private String gitUrl;
        private String gitName;
        
    }
}
