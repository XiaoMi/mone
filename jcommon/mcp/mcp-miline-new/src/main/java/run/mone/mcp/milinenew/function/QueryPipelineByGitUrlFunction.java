package run.mone.mcp.milinenew.function;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 根据Git URL查询流水线的工具，用于获取指定Git仓库下的所有流水线列表及项目ID。
 * <p>
 * **使用场景：**
 * - 需要查询某个Git仓库关联的所有流水线
 * - 获取项目ID和流水线信息进行后续操作
 * - 在部署前查询可用的流水线
 *
 * @author liguanchen
 * @date 2025/12/05
 */
@Slf4j
@Component
public class QueryPipelineByGitUrlFunction implements McpFunction {

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "gitUrl": {
                        "type": "string",
                        "description": "Git仓库地址（必填）"
                    },
                    "env": {
                        "type": "string",
                        "description": "环境类型，可选值为staging、pre、prod，默认为staging"
                    }
                },
                "required": ["gitUrl"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String QUERY_PIPELINE_URL = BASE_URL != null ? BASE_URL + "/queryPipeLineByGitUrl" : null;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public QueryPipelineByGitUrlFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("QueryPipelineByGitUrl arguments: {}", arguments);

        try {
            if (BASE_URL == null || QUERY_PIPELINE_URL == null) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：配置错误: req_base_url 环境变量未设置")),
                        true
                ));
            }

            // 验证必填参数
            Object gitUrlObj = arguments.get("gitUrl");
            if (gitUrlObj == null || StringUtils.isBlank(gitUrlObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'gitUrl'")),
                        true
                ));
            }

            String gitUrl = gitUrlObj.toString();
            String env = arguments.get("env") != null ? arguments.get("env").toString() : "staging";

            // 构建请求参数
            List<Object> requestBody = List.of(gitUrl, env);
            String requestBodyStr = objectMapper.writeValueAsString(requestBody);
            log.info("queryPipelineByGitUrl request: {}", requestBodyStr);

            RequestBody body = RequestBody.create(
                    requestBodyStr,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(QUERY_PIPELINE_URL)
                    .post(body)
                    .build();

            OkHttpClient queryClient = client.newBuilder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .build();

            try (Response response = queryClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("queryPipelineByGitUrl response: {}", responseBody);

                ApiResponse<ProjectPipelineListDto> apiResponse = objectMapper.readValue(
                        responseBody,
                        objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, ProjectPipelineListDto.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                ProjectPipelineListDto data = apiResponse.getData();
                String resultText = formatPipelineInfo(data);
                log.info("queryPipelineByGitUrl data: {}", resultText);

                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(resultText)),
                        false
                ));
            }
        } catch (Exception e) {
            log.error("执行query_pipeline_by_git_url操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    /**
     * 格式化流水线信息输出
     */
    private String formatPipelineInfo(ProjectPipelineListDto data) {
        if (data == null) {
            return "未查询到相关流水线信息";
        }

        StringBuilder sb = new StringBuilder("流水线查询结果：\n\n");

        if (data.getId() != null) {
            sb.append(String.format("项目ID: %s\n", data.getId()));
        }

        if (data.getPipelineList() != null && !data.getPipelineList().isEmpty()) {
            sb.append(String.format("\n共找到 %d 条流水线：\n\n", data.getPipelineList().size()));

            int index = 1;
            for (PipelineInfo pipeline : data.getPipelineList()) {
                sb.append(String.format("流水线 %d:\n", index++));
                sb.append(String.format("  - 流水线ID: %s\n", pipeline.getId()));
                if (StringUtils.isNotBlank(pipeline.getPipelineName())) {
                    sb.append(String.format("  - 流水线名称: %s\n", pipeline.getPipelineName()));
                }
                if (StringUtils.isNotBlank(pipeline.getPipelineCname())) {
                    sb.append(String.format("  - 流水线中文名: %s\n", pipeline.getPipelineCname()));
                }
                if (StringUtils.isNotBlank(pipeline.getGitBranch())) {
                    sb.append(String.format("  - Git分支: %s\n", pipeline.getGitBranch()));
                }
                if (StringUtils.isNotBlank(pipeline.getEnv())) {
                    sb.append(String.format("  - 环境: %s\n", pipeline.getEnv()));
                }
                sb.append(String.format("  - 流水线状态: %d\n", pipeline.getStatus()));
                sb.append(String.format("  - 流水线类型: %d\n", pipeline
                        .getType()));
                if (StringUtils.isNotBlank(pipeline.getDesc())) {
                    sb.append(String.format("  - 流水线描述: %s\n", pipeline.getDesc()));
                }
                if (StringUtils.isNotBlank(pipeline.getGitUrl())) {
                    sb.append(String.format("  - Git地址: %s\n", pipeline.getGitUrl()));
                }
                if (StringUtils.isNotBlank(pipeline.getDeployEnvGroup())) {
                    sb.append(String.format("  - 部署环境组: %s\n", pipeline.getDeployEnvGroup()));
                }
                sb.append(String.format("  - 创建时间: %d\n", pipeline.getCreateTime()));
                sb.append(String.format("  - 更新时间: %d\n", pipeline.getUpdateTime()));
                if (StringUtils.isNotBlank(pipeline.getCreator())) {
                    sb.append(String.format("  - 创建人: %s\n", pipeline.getCreator()));
                }
                

                sb.append("\n");
            }
        } else {
            sb.append("\n暂无流水线信息\n");
        }

        return sb.toString().trim();
    }

    @Override
    public String getName() {
        return "query_pipeline_by_git_url";
    }

    @Override
    public String getDesc() {
        return """
                根据Git URL查询流水线的工具，用于获取指定Git仓库下的所有流水线全部信息及项目ID。
                
                **使用场景：**
                - 需要查询某个Git仓库关联的所有流水线的全部信息
                - 获取项目ID和流水线信息进行后续操作
                - 在部署前查询可用的流水线
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
    public static class ProjectPipelineListDto {
        private Long id;
        private List<PipelineInfo> pipelineList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PipelineInfo {
        private String env;

        private long id;

        private long projectId;

        private long parentPipelineId;

        private String pipelineName;

        private String pipelineCname;

        private int status;

        private int type;

        private String desc;

        private String gitUrl;

        private String gitBranch;

        private String deployEnvGroup;

        private long createTime;

        private long updateTime;

        private String creator;
    }
}
