package run.mone.mcp.chaos.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chaos.http.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class ChaosFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "chaos_executor";

    private String desc = "负责混沌故障平台的基本操作";

    public final String executePath = "/chaosApiTask/execute";

    public final String recoverPath = "/chaosApiTask/recover";

    public final String projectPath = "/chaosApiTask/queryProject";

    public final String pipelinePath = "/chaosApiTask/queryPipeline";

    public final String chaosDetailPath = "/chaosApiTask/getChaosTaskDetail";

    public final String chaosListPath = "/chaosApiTask/getTaskList";

    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["get_my_project","get_pipeline","get_chaos_list","get_chaos_detail","recover_chaos","execute_chaos"],
                        "description": "操作类型,get_my_project获取我的服务信息，get_pipeline获取某个服务的流水线信息，get_chaos_list获取混沌故障注入列表,get_chaos_detail获取某个故障注入详情,recover_chaos恢复某个故障注入,"
                    },
                    "projectId": {
                        "type": "string",
                        "description": "要执行混沌故障注入操作的服务ID"
                    },
                    "taskId": {
                        "type": "string",
                        "description": "要获取混沌故障注入详情的任务ID"
                    },
                    "userName":{
                        "type": "string",
                        "description": "操作者用户名"
                    }
                  },
                "required": ["type"]
            }
            """;

    public ChaosFunction() {

    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");

        String host = System.getenv().getOrDefault("CHAOS_HOST", "");
        String userName = (String) args.get("userName");

        try {
            return switch (type.toLowerCase()) {
                case "get_my_project" -> {
                    String projectsResult = getProjects(host, userName);
                    yield new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(projectsResult)), false);
                }
                case "get_pipeline" -> {
                    String pipelineResult = getPipeline(host, userName, (String) args.get("projectId"));
                    yield new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(pipelineResult)), false);
                }
                case "get_chaos_list" -> {
                    String chaosListResult = getChaosList(host, userName, (String) args.get("projectId"));
                    yield new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(chaosListResult)), false);
                }
                case "get_chaos_detail" -> {
                    String chaosDetailResult = getChaosDetail(host, userName, (String) args.get("taskId"));
                    yield new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(chaosDetailResult)), false);
                }
                case "recover_chaos" -> {
                    String recoverResult = recoverChaos(host, userName, (String) args.get("taskId"));
                    yield new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(recoverResult)), false);
                }
                case "execute_chaos" -> {
                    String executeResult = executeChaos(host, userName, (String) args.get("taskId"));
                    yield new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(executeResult)), false);
                }
                default -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("不支持的操作")), true);
            };
        } catch (Exception e) {
            log.error("执行混沌操作失败", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("操作失败：" + e.getMessage())), true);
        }
    }

    // 获取我有权限的项目
    private String getProjects(String host, String userName) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userName", userName);
        try {
            JsonObject jsonObject = new HttpClient().get(host + projectPath, queryParams);

            // 解析原始JSON，提取项目id和name
            JsonArray projectList = new JsonArray();
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray dataArray = jsonObject.getAsJsonArray("data");
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject item = dataArray.get(i).getAsJsonObject();
                    if (item.has("projectVo") && item.get("projectVo").isJsonObject()) {
                        JsonObject projectVo = item.getAsJsonObject("projectVo");
                        if (projectVo.has("id") && projectVo.has("name")) {
                            JsonObject simpleProject = new JsonObject();
                            simpleProject.addProperty("id", projectVo.get("id").getAsLong());
                            simpleProject.addProperty("name", projectVo.get("name").getAsString());
                            projectList.add(simpleProject);
                        }
                    }
                }
            }

            // 构建简化的返回结果
            JsonObject result = new JsonObject();
            result.addProperty("code", jsonObject.has("code") ? jsonObject.get("code").getAsInt() : 0);
            result.add("projects", projectList);

            return result.toString();
        } catch (Exception e) {
            log.error("Failed to get projects for user: {}", userName, e);
            throw new RuntimeException(e);
        }
    }

    // 获取我有权限的pipeline
    public String getPipeline(String host, String userName, String projectId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("projectId", projectId);
        queryParams.put("env", "staging");
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");
            JsonObject jsonObject = new HttpClient().get(host + pipelinePath, queryParams, headerMap);

            // 解析原始JSON，提取流水线id和name
            JsonArray pipelineList = new JsonArray();
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray dataArray = jsonObject.getAsJsonArray("data");
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject item = dataArray.get(i).getAsJsonObject();
                    if (item.has("pipelineDeployDtos") && item.get("pipelineDeployDtos").isJsonArray()) {
                        JsonArray pipelineDeployDtos = item.getAsJsonArray("pipelineDeployDtos");
                        for (int j = 0; j < pipelineDeployDtos.size(); j++) {
                            JsonObject pipelineDto = pipelineDeployDtos.get(j).getAsJsonObject();
                            if (pipelineDto.has("pipelineId") && pipelineDto.has("pipelineName")) {
                                JsonObject simplePipeline = new JsonObject();
                                simplePipeline.addProperty("pipelineId", pipelineDto.get("pipelineId").getAsLong());
                                simplePipeline.addProperty("pipelineName",
                                        pipelineDto.get("pipelineName").getAsString());
                                pipelineList.add(simplePipeline);
                            }
                        }
                    }
                }
            }

            // 构建简化的返回结果
            JsonObject result = new JsonObject();
            result.addProperty("code", jsonObject.has("code") ? jsonObject.get("code").getAsInt() : 0);
            result.add("pipelines", pipelineList);

            return result.toString();
        } catch (Exception e) {
            log.error("Failed to get pipelines for project: {}", projectId, e);
            throw new RuntimeException(e);
        }
    }

    // 获取列表
    public String getChaosList(String host, String userName, String projectId) {
        Map<String, String> queryParams = new HashMap<>();
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");
            JsonObject jsonObject = new HttpClient().post(host + chaosListPath, queryParams, headerMap);

            // 解析原始JSON，提取id和experimentName
            JsonArray simplifiedList = new JsonArray();
            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                JsonObject data = jsonObject.getAsJsonObject("data");
                if (data.has("list") && data.get("list").isJsonArray()) {
                    JsonArray list = data.getAsJsonArray("list");
                    for (int i = 0; i < list.size(); i++) {
                        JsonObject item = list.get(i).getAsJsonObject();
                        if (item.has("id") && item.has("experimentName")) {
                            JsonObject simplifiedItem = new JsonObject();
                            simplifiedItem.addProperty("id", item.get("id").getAsString());
                            simplifiedItem.addProperty("experimentName", item.get("experimentName").getAsString());
                            simplifiedList.add(simplifiedItem);
                        }
                    }
                }
            }

            // 构建简化的返回结果
            JsonObject result = new JsonObject();
            result.addProperty("code", jsonObject.has("code") ? jsonObject.get("code").getAsInt() : 0);
            result.add("experiments", simplifiedList);

            return result.toString();
        } catch (Exception e) {
            log.error("Failed to get chaos list for project: {}", projectId, e);
            throw new RuntimeException(e);
        }
    }

    // 获取详情
    public String getChaosDetail(String host, String userName, String taskId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", taskId);
        try {
            JsonObject jsonObject = new HttpClient().get(host + chaosDetailPath, queryParams);
            return jsonObject.toString();
        } catch (Exception e) {
            log.error("Failed to get chaos detail for task: {}", taskId, e);
            throw new RuntimeException(e);
        }
    }

    // 恢复
    public String recoverChaos(String host, String userName, String taskId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", taskId);
        queryParams.put("userName", userName);
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");
            JsonObject jsonObject = new HttpClient().get(host + recoverPath, queryParams, headerMap);
            return jsonObject.toString();
        } catch (Exception e) {
            log.error("Failed to recover chaos for task: {}", taskId, e);
            throw new RuntimeException(e);
        }
    }

    // 执行混沌实验
    public String executeChaos(String host, String userName, String taskId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", taskId);
        queryParams.put("userName", userName);
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");
            JsonObject jsonObject = new HttpClient().get(host + executePath, queryParams, headerMap);
            return jsonObject.toString();
        } catch (Exception e) {
            log.error("Failed to execute chaos for project: {}", taskId, e);
            throw new RuntimeException(e);
        }
    }

}
