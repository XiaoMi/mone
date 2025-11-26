package run.mone.mcp.miline.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.ArrayList;

@Data
@Slf4j
public class MilineFunction implements McpFunction {

    private String name = "stream_miline_executor";
    private String desc = "Miline CICD platform operations including managing project members and running pipelines";

    private String toolSchema = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["addMember", "removeMember", "listMembers", "runPipeline"],
                        "description": "The operation type for Miline project management"
                    },
                    "projectId": {
                        "type": "string",
                        "description": "The ID of the target project"
                    },
                    "username": {
                        "type": "string",
                        "description": "Username of the member to add/remove"
                    },
                    "pipelineId": {
                        "type": "string",
                        "description": "The ID of the pipeline to run"
                    }
                },
                "required": ["command", "projectId"]
            }
            """;

    private static final String BASE_URL = System.getenv("req_base_url")+"/mtop/miline";
    private static final String GET_MEMBERS_URL = BASE_URL + "/getProjectMembers";
    private static final String MODIFY_MEMBERS_URL = BASE_URL + "/modifyMember";
    private static final String RUN_PIPELINE_URL = BASE_URL + "/startPipelineWithLatestCommit";
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Override
    public String getToolScheme() {
        return toolSchema;
    }

    @Data
    private static class ProjectMember {
        private String account;
        private Integer userType;
        private Integer roleType;
        private Integer projectId;
        private Integer type;
    }

    @Data
    private static class ModifyMemberRequest {
        private Integer outId;
        private String account;
        private Integer userType;
        private Integer roleType;
        private List<UserParam> userParams;
    }

    @Data
    private static class UserParam {
        private String account;
        private Integer userType;
    }
    
    public MilineFunction() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Data
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
    }

    private List<ProjectMember> getProjectMembers(Integer projectId) throws Exception {
        List<Map<String, Object>> requestBody = List.of(Map.of(
            "account", "admin",
            "userType", 0,
            "roleType", 0,
            "projectIds", List.of(projectId)
        ));

        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        log.info("getProjectMembers request: {}", requestBodyStr);

        RequestBody body = RequestBody.create(
            requestBodyStr,
            MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(GET_MEMBERS_URL)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            log.info("getProjectMembers response: {}", responseBody);

            ApiResponse<List<ProjectMember>> apiResponse = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructParametricType(
                    ApiResponse.class,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ProjectMember.class)
                )
            );

            if (apiResponse.getCode() != 0) {
                throw new Exception("API error: " + apiResponse.getMessage());
            }

            return apiResponse.getData();
        }
    }

    private boolean modifyProjectMembers(ModifyMemberRequest request) throws Exception {
        List<ModifyMemberRequest> requestBody = List.of(request);

        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        log.info("modifyProjectMembers request: {}", requestBodyStr);

        RequestBody body = RequestBody.create(
            requestBodyStr,
            MediaType.parse("application/json; charset=utf-8")
        );

        Request httpRequest = new Request.Builder()
            .url(MODIFY_MEMBERS_URL)
            .post(body)
            .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            log.info("modifyProjectMembers response: {}", responseBody);

            ApiResponse<Object> apiResponse = objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Object.class)
            );

            if (apiResponse.getCode() != 0) {
                throw new Exception("API error: " + apiResponse.getMessage());
            }

            return true;
        }
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            switch (command) {
                case "addMember":
                    return addProjectMember(args);
                case "removeMember":
                    return removeProjectMember(args);
                case "listMembers":
                    return listProjectMembers(args);
                case "runPipeline":
                    return runPipeline(args);
                default:
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Unknown command: " + command)),
                            true
                    ));
            }
        } catch (Exception e) {
            log.error("Error executing miline command", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            ));
        }
    }

    private Flux<McpSchema.CallToolResult> addProjectMember(Map<String, Object> args) {
        try {
            Integer projectId = Integer.parseInt((String) args.get("projectId"));
            String username = (String) args.get("username");
            if (username == null || username.isEmpty()) {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Username is required for adding member")),
                    true
                ));
            }

            // 1. 获取当前项目成员
            List<ProjectMember> currentMembers = getProjectMembers(projectId);
            
            // 2. 检查成员是否已存在
            boolean memberExists = currentMembers.stream()
                .anyMatch(member -> member.getAccount().equals(username));
            
            if (memberExists) {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Member " + username + " already exists in project")),
                    true
                ));
            }

            // 3. 构建新的成员列表
            List<UserParam> newUserParams = new ArrayList<>();
            // 添加现有成员
            for (ProjectMember member : currentMembers) {
                UserParam param = new UserParam();
                param.setAccount(member.getAccount());
                param.setUserType(member.getUserType());
                newUserParams.add(param);
            }
            // 添加新成员
            UserParam newMember = new UserParam();
            newMember.setAccount(username);
            newMember.setUserType(0);
            newUserParams.add(newMember);

            // 4. 修改成员列表
            ModifyMemberRequest request = new ModifyMemberRequest();
            request.setOutId(projectId);
            request.setAccount("admin");
            request.setUserType(0);
            request.setRoleType(0);
            request.setUserParams(newUserParams);

            // 5. 调用修改接口
            boolean success = modifyProjectMembers(request);
            
            if (success) {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Successfully added member " + username + " to project " + projectId)),
                    false
                ));
            } else {
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Failed to add member " + username)),
                    true
                ));
            }

        } catch (Exception e) {
            log.error("Error adding project member", e);
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                true
            ));
        }
    }

    private Flux<McpSchema.CallToolResult> removeProjectMember(Map<String, Object> args) {
        // TODO: Implement remove member logic
        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent("Remove member function not implemented yet")),
            true
        ));
    }

    private Flux<McpSchema.CallToolResult> listProjectMembers(Map<String, Object> args) {
        // TODO: Implement list members logic
        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent("List members function not implemented yet")),
            true
        ));
    }

    private Flux<McpSchema.CallToolResult> runPipeline(Map<String, Object> args) {
        try {
            // 修改类型转换逻辑
            Integer projectId = Integer.parseInt((String) args.get("projectId"));
            Integer pipelineId = Integer.parseInt((String) args.get("pipelineId"));
            
            // 修改请求体格式
            Map<String, String> userMap = Map.of("baseUserName", "wangmin17");
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

            try (Response response = pipelineClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                log.info("runPipeline response: {}", responseBody);

                ApiResponse<Integer> apiResponse = objectMapper.readValue(
                    responseBody,
                    objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Integer.class)
                );

                if (apiResponse.getCode() != 0) {
                    throw new Exception("API error: " + apiResponse.getMessage());
                }

                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(
                        String.format("Successfully started pipeline. Execution ID: %d", apiResponse.getData())
                    )),
                    false
                ));
            }
        } catch (Exception e) {
            log.error("Error running pipeline", e);
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                true
            ));
        }
    }
}