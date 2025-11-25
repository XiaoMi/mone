package run.mone.mcp.milinenew.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CreateProjectTool implements ITool {
    @Value("${git.email.suffix}")
    private String gitUserName;
    public static final String name = "create_project";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String CREATE_PROJECT_URL = BASE_URL != null ? BASE_URL + "/createProject" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public CreateProjectTool() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
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
                创建Miline项目的工具，用于在Miline平台创建新项目。
                
                **使用场景：**
                - 需要创建新的Miline项目
                - 初始化项目配置和基本信息
                - 设置项目的生成参数和域名
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectName: (必填) 项目名称，将同时作为name和gitName
                - env: (可选) 环境类型，默认为staging
                - baseUserName: (可选) 用户名，默认为liguanchen
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
                <create_project>
                <projectName>项目名称</projectName>
                <env>环境类型</env>
                <domain>git.n.xiaomi.com</domain>
                %s
                </create_project>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例: 创建项目
                <create_project>
                <projectName>我的新项目</projectName>
                <env>staging</env>
                <domain>git.n.xiaomi.com</domain>
                </create_project>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            if (BASE_URL == null || CREATE_PROJECT_URL == null) {
                result.addProperty("error", "配置错误: req_base_url 环境变量未设置");
                log.error("req_base_url 环境变量未设置，无法执行创建项目操作");
                return result;
            }
            // 验证必填参数
            if (!inputJson.has("projectName") || StringUtils.isBlank(inputJson.get("projectName").getAsString())) {
                result.addProperty("error", "缺少必填参数'projectName'");
                return result;
            }

            // 获取项目名，如果包含路径则提取最后一部分
            String projectNameRaw = inputJson.get("projectName").getAsString();
            String projectName = new File(projectNameRaw).getName();
            if (!projectNameRaw.equals(projectName)) {
                log.info("从路径中提取项目名: {} -> {}", projectNameRaw, projectName);
            }
            String gitName = projectName; // gitName与projectName相同
            String env = inputJson.has("env") && !StringUtils.isBlank(inputJson.get("env").getAsString())
                    ? inputJson.get("env").getAsString()
                    : "staging";
            String baseUserName = inputJson.has("baseUserName") && !StringUtils.isBlank(inputJson.get("baseUserName").getAsString())
                    ? inputJson.get("baseUserName").getAsString()
                    : gitUserName;

            // 构建第一个对象：MoneContext
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("baseUserName", baseUserName);
            userMap.put("userType", 0);

            // 构建第二个对象：ProjectCreateDto（固定字段 + 用户自定义字段）
            Map<String, Object> projectCreateDto = new HashMap<>();
            // 用户自定义字段
            projectCreateDto.put("name", projectName);
            projectCreateDto.put("gitName", gitName);
            // 固定字段
            projectCreateDto.put("domain", "git.n.xiaomi.com");
            projectCreateDto.put("gitGroup", "cefe");
            projectCreateDto.put("desc", "");
            // projectGen固定配置
            Map<String, Object> projectGen = new HashMap<>();
            projectGen.put("type", "spring-java21-fe");
            projectGen.put("need", false);
            projectGen.put("gen", false);
            projectCreateDto.put("projectGen", projectGen);
            // tpcNode固定配置
            Map<String, Object> tpcNode = new HashMap<>();
            tpcNode.put("parentId", 382);
            projectCreateDto.put("tpcNode", tpcNode);
            // costSharing固定配置
            Map<String, String> costSharing = new HashMap<>();
            costSharing.put("fullIamId", "2;3;43;26801;26802");
            costSharing.put("fullIamName", "xiaomi;b2c;systech;fe-dev;nginx");
            projectCreateDto.put("costSharing", costSharing);
            // 固定字段
            projectCreateDto.put("fullIamId", "2;3;43;26801;26802");
            projectCreateDto.put("fullIamName", "xiaomi;b2c;systech;fe-dev;nginx");

            // 构建请求体：[MoneContext, ProjectCreateDto, env]
            List<Object> requestBody = List.of(userMap, projectCreateDto, env);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("createProject request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );
            
            Request request = new Request.Builder()
                    .url(CREATE_PROJECT_URL)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "OkHttp")
                    .build();

            OkHttpClient projectClient = client.newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();

            try (Response response = projectClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("createProject response: {}", responseBody);

                ApiResponse<CreateProjectData> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, CreateProjectData.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                if (apiResponse.getData() != null) {
                    CreateProjectData data = apiResponse.getData();
                    ProjectVo projectVo = data.getProjectVo();
                    NodeVo nodeVo = data.getNodeVo();
                    
                    if (projectVo != null) {
                        result.addProperty("projectId", projectVo.getId() != null ? projectVo.getId().toString() : "");
                        result.addProperty("projectName", projectVo.getName() != null ? projectVo.getName() : projectName);
                        result.addProperty("gitAddress", projectVo.getGitAddress() != null ? projectVo.getGitAddress() : "");
                        result.addProperty("gitGroup", projectVo.getGitGroup() != null ? projectVo.getGitGroup() : "");
                        result.addProperty("gitName", projectVo.getGitName() != null ? projectVo.getGitName() : "");
                        result.addProperty("env", "staging");
                    }
                    
                    if (nodeVo != null && nodeVo.getId() != null) {
                        result.addProperty("nodeId", nodeVo.getId().toString());
                    }
                    
                    String resultMsg = String.format("成功创建项目，项目ID: %s", 
                            projectVo != null && projectVo.getId() != null ? projectVo.getId() : "未知");
                    if (nodeVo != null && nodeVo.getId() != null) {
                        resultMsg += String.format("，节点ID: %s", nodeVo.getId());
                    }
                    result.addProperty("result", resultMsg);
                } else {
                    result.addProperty("result", "项目创建成功");
                }
                return result;
            }
        } catch (Exception e) {
            log.error("执行create_project操作时发生异常", e);
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
    private static class CreateProjectData {
        private ProjectVo projectVo;
        private NodeVo nodeVo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ProjectVo {
        private Long id;
        private String name;
        private String description;
        private String gitAddress;
        private String gitGroup;
        private String gitName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NodeVo {
        private Long id;
        private String nodeName;
        private String desc;
    }
}

