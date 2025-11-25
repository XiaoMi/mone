package run.mone.mcp.milinenew.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 创建Miline项目的工具，用于在Miline平台创建新项目
 *
 * @author goodjava@qq.com
 * @date 2025/1/17
 */
@Slf4j
public class CreateProjectFunction implements McpFunction {

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectName": {
                        "type": "string",
                        "description": "项目名称，将同时作为name和gitName（必填）"
                    },
                    "env": {
                        "type": "string",
                        "description": "环境类型，默认为staging"
                    },
                    "baseUserName": {
                        "type": "string",
                        "description": "用户名，默认为liguanchen"
                    }
                },
                "required": ["projectName"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String CREATE_PROJECT_URL = BASE_URL != null ? BASE_URL + "/createProject" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public CreateProjectFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("CreateProject arguments: {}", arguments);

        try {
            if (BASE_URL == null || CREATE_PROJECT_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object projectNameObj = arguments.get("projectName");
            if (projectNameObj == null || StringUtils.isBlank(projectNameObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'projectName'")),
                        true
                ));
            }

            // 获取项目名，如果包含路径则提取最后一部分
            String projectNameRaw = projectNameObj.toString();
            String projectName = new File(projectNameRaw).getName();
            if (!projectNameRaw.equals(projectName)) {
                log.info("从路径中提取项目名: {} -> {}", projectNameRaw, projectName);
            }
            String gitName = projectName; // gitName与projectName相同

            String env = arguments.containsKey("env") && !StringUtils.isBlank(arguments.get("env").toString())
                    ? arguments.get("env").toString()
                    : "staging";
            String baseUserName = arguments.containsKey("baseUserName") && !StringUtils.isBlank(arguments.get("baseUserName").toString())
                    ? arguments.get("baseUserName").toString()
                    : "liguanchen";

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
            projectGen.put("type", "spring-java21");
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

                StringBuilder resultText = new StringBuilder("项目创建成功！\n");
                if (apiResponse.getData() != null) {
                    CreateProjectData data = apiResponse.getData();
                    ProjectVo projectVo = data.getProjectVo();
                    NodeVo nodeVo = data.getNodeVo();

                    if (projectVo != null) {
                        if (projectVo.getId() != null) {
                            resultText.append("- 项目ID: ").append(projectVo.getId()).append("\n");
                        }
                        if (projectVo.getName() != null) {
                            resultText.append("- 项目名称: ").append(projectVo.getName()).append("\n");
                        }
                        if (projectVo.getGitAddress() != null) {
                            resultText.append("- Git地址: ").append(projectVo.getGitAddress()).append("\n");
                        }
                        if (projectVo.getGitGroup() != null) {
                            resultText.append("- Git组: ").append(projectVo.getGitGroup()).append("\n");
                        }
                        if (projectVo.getGitName() != null) {
                            resultText.append("- Git名称: ").append(projectVo.getGitName()).append("\n");
                        }
                    }

                    if (nodeVo != null && nodeVo.getId() != null) {
                        resultText.append("- 节点ID: ").append(nodeVo.getId()).append("\n");
                    }
                }

                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(resultText.toString())),
                        false
                ));
            }
        } catch (Exception e) {
            log.error("执行create_project操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    @Override
    public String getName() {
        return "create_project";
    }

    @Override
    public String getDesc() {
        return """
                创建Miline项目的工具，用于在Miline平台创建新项目。

                **使用场景：**
                - 需要创建新的Miline项目
                - 初始化项目配置和基本信息
                - 设置项目的生成参数和域名
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
