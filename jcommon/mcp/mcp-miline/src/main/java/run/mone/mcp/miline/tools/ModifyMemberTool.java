package run.mone.mcp.miline.tools;

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

/**
 * 项目成员管理工具，用于添加和删除Miline项目成员
 * <p>
 * 此工具提供对Miline项目成员的管理功能，包括：
 * - 添加项目成员
 * - 删除项目成员
 * - 查询项目成员列表
 * <p>
 * 使用场景：
 * - 需要为项目添加新的开发人员或测试人员
 * - 需要移除已离职或转岗的项目成员
 * - 需要查看当前项目的成员列表
 */
@Slf4j
public class ModifyMemberTool implements ITool {

    public static final String name = "modify_member";
    private static final String BASE_URL = System.getenv("req_base_url");
    private static final String GET_MEMBERS_URL = BASE_URL + "/getProjectMembers";
    private static final String MODIFY_MEMBERS_URL = BASE_URL + "/modifyMember";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ModifyMemberTool() {
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
                管理Miline项目成员的工具，支持添加、删除和查询项目成员。
                
                **使用场景：**
                - 需要为项目添加新的开发人员或测试人员
                - 需要移除已离职或转岗的项目成员
                - 需要查看当前项目的成员列表
                
                **重要说明：**
                - 添加成员需要提供项目ID和用户名
                - 删除成员需要提供项目ID和用户名
                - 查询成员列表只需要提供项目ID
                - 所有操作都需要有相应的权限
                """;
    }

    @Override
    public String parameters() {
        return """
                - operation: (必填) 操作类型，可选值：add（添加成员）、remove（删除成员）、list（查询成员列表）
                - projectId: (必填) 项目ID
                - username: (添加/删除操作必填) 用户名
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
                <modify_member>
                <operation>add, remove 或 list</operation>
                <projectId>项目ID</projectId>
                <username>用户名（添加/删除操作时必填）</username>
                %s
                </modify_member>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例1: 添加项目成员
                <modify_member>
                <operation>add</operation>
                <projectId>12345</projectId>
                <username>zhangsan</username>
                </modify_member>
                
                示例2: 删除项目成员
                <modify_member>
                <operation>remove</operation>
                <projectId>12345</projectId>
                <username>lisi</username>
                </modify_member>
                
                示例3: 查询项目成员列表
                <modify_member>
                <operation>list</operation>
                <projectId>12345</projectId>
                </modify_member>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("operation") || StringUtils.isBlank(inputJson.get("operation").getAsString())) {
                log.error("modify_member操作缺少必填参数operation");
                result.addProperty("error", "缺少必填参数'operation'");
                return result;
            }

            if (!inputJson.has("projectId") || StringUtils.isBlank(inputJson.get("projectId").getAsString())) {
                log.error("modify_member操作缺少必填参数projectId");
                result.addProperty("error", "缺少必填参数'projectId'");
                return result;
            }

            String operation = inputJson.get("operation").getAsString();
            String projectId = inputJson.get("projectId").getAsString();
            Integer projectIdInt = Integer.parseInt(projectId);

            switch (operation) {
                case "add":
                    if (!inputJson.has("username") || StringUtils.isBlank(inputJson.get("username").getAsString())) {
                        log.error("添加成员操作缺少必填参数username");
                        result.addProperty("error", "添加成员操作缺少必填参数'username'");
                        return result;
                    }
                    String username = inputJson.get("username").getAsString();
                    return addMember(projectIdInt, username);
                case "remove":
                    if (!inputJson.has("username") || StringUtils.isBlank(inputJson.get("username").getAsString())) {
                        log.error("删除成员操作缺少必填参数username");
                        result.addProperty("error", "删除成员操作缺少必填参数'username'");
                        return result;
                    }
                    String removeUsername = inputJson.get("username").getAsString();
                    return removeMember(projectIdInt, removeUsername);
                case "list":
                    return listMembers(projectIdInt);
                default:
                    log.error("无效的操作类型: {}", operation);
                    result.addProperty("error", "无效的操作类型: " + operation + "。支持的操作类型: add, remove, list");
                    return result;
            }

        } catch (NumberFormatException e) {
            log.error("项目ID格式不正确", e);
            result.addProperty("error", "项目ID必须是数字");
            return result;
        } catch (Exception e) {
            log.error("执行modify_member操作时发生异常", e);
            result.addProperty("error", "执行操作失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 获取项目成员列表
     */
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

    /**
     * 修改项目成员
     */
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

    /**
     * 添加项目成员
     */
    private JsonObject addMember(Integer projectId, String username) {
        JsonObject result = new JsonObject();
        
        try {
            // 1. 获取当前项目成员
            List<ProjectMember> currentMembers = getProjectMembers(projectId);
            
            // 2. 检查成员是否已存在
            boolean memberExists = currentMembers.stream()
                .anyMatch(member -> member.getAccount().equals(username));
            
            if (memberExists) {
                result.addProperty("result", "成员 " + username + " 已存在于项目中");
                return result;
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
                result.addProperty("result", "成功添加成员 " + username + " 到项目 " + projectId);
            } else {
                result.addProperty("error", "添加成员 " + username + " 失败");
            }
            
            return result;
        } catch (Exception e) {
            log.error("添加项目成员时发生错误", e);
            result.addProperty("error", "添加成员失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除项目成员
     */
    private JsonObject removeMember(Integer projectId, String username) {
        JsonObject result = new JsonObject();
        
        try {
            // 1. 获取当前项目成员
            List<ProjectMember> currentMembers = getProjectMembers(projectId);
            
            // 2. 检查成员是否存在
            boolean memberExists = currentMembers.stream()
                .anyMatch(member -> member.getAccount().equals(username));
            
            if (!memberExists) {
                result.addProperty("result", "成员 " + username + " 不存在于项目中");
                return result;
            }

            // 3. 构建新的成员列表（排除要删除的成员）
            List<UserParam> newUserParams = new ArrayList<>();
            for (ProjectMember member : currentMembers) {
                if (!member.getAccount().equals(username)) {
                    UserParam param = new UserParam();
                    param.setAccount(member.getAccount());
                    param.setUserType(member.getUserType());
                    newUserParams.add(param);
                }
            }

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
                result.addProperty("result", "成功从项目 " + projectId + " 中移除成员 " + username);
            } else {
                result.addProperty("error", "移除成员 " + username + " 失败");
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除项目成员时发生错误", e);
            result.addProperty("error", "删除成员失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 查询项目成员列表
     */
    private JsonObject listMembers(Integer projectId) {
        JsonObject result = new JsonObject();
        
        try {
            // 获取项目成员
            List<ProjectMember> members = getProjectMembers(projectId);
            
            // 构建结果
            JsonArray membersArray = new JsonArray();
            for (ProjectMember member : members) {
                JsonObject memberObj = new JsonObject();
                memberObj.addProperty("account", member.getAccount());
                memberObj.addProperty("userType", member.getUserType());
                memberObj.addProperty("roleType", member.getRoleType());
                membersArray.add(memberObj);
            }
            
            result.add("members", membersArray);
            result.addProperty("totalCount", members.size());
            result.addProperty("projectId", projectId);
            
            // 生成格式化输出
            StringBuilder formattedOutput = new StringBuilder();
            formattedOutput.append("项目 ").append(projectId).append(" 的成员列表：\n\n");
            
            if (members.isEmpty()) {
                formattedOutput.append("(项目没有成员)");
            } else {
                for (int i = 0; i < members.size(); i++) {
                    ProjectMember member = members.get(i);
                    formattedOutput.append(i + 1).append(". ");
                    formattedOutput.append(member.getAccount());
                    formattedOutput.append(" (类型: ").append(getUserTypeDesc(member.getUserType())).append(")");
                    formattedOutput.append(" (角色: ").append(getRoleTypeDesc(member.getRoleType())).append(")");
                    formattedOutput.append("\n");
                }
            }
            
            result.addProperty("result", formattedOutput.toString());
            
            return result;
        } catch (Exception e) {
            log.error("查询项目成员时发生错误", e);
            result.addProperty("error", "查询成员失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 获取用户类型描述
     */
    private String getUserTypeDesc(Integer userType) {
        return switch (userType) {
            case 0 -> "普通用户";
            case 1 -> "管理员";
            default -> "未知类型(" + userType + ")";
        };
    }
    
    /**
     * 获取角色类型描述
     */
    private String getRoleTypeDesc(Integer roleType) {
        return switch (roleType) {
            case 0 -> "开发";
            case 1 -> "测试";
            case 2 -> "产品";
            case 3 -> "运维";
            default -> "其他角色(" + roleType + ")";
        };
    }

    /**
     * 项目成员信息
     */
    @Data
    private static class ProjectMember {
        private String account;
        private Integer userType;
        private Integer roleType;
        private Integer projectId;
        private Integer type;
    }

    /**
     * 修改成员请求
     */
    @Data
    private static class ModifyMemberRequest {
        private Integer outId;
        private String account;
        private Integer userType;
        private Integer roleType;
        private List<UserParam> userParams;
    }

    /**
     * 用户参数
     */
    @Data
    private static class UserParam {
        private String account;
        private Integer userType;
    }
    
    /**
     * API响应
     */
    @Data
    private static class ApiResponse<T> {
        private int code;
        private T data;
        private String message;
    }
}