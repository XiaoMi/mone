package run.mone.mcp.chaos.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.chaos.http.HttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

// 处理不同种类型的混沌故障演练
@Data
@Slf4j
public class CreateChaosFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "chaos_creator";

    private String desc = "负责创建不同种类的混沌故障类型";

    public final String createTaskPath = "/chaosApiTask/createChaosTask";

    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "taskType": {
                        "type": "string",
                        "enum": ["1", "2", "11", "4"],
                        "description": "故障类型：cpu(压力测试)传1,内存压力测试传11, network(网络延迟)传2，杀死pod传4"
                    },
                    "experimentName": {
                        "type": "string",
                        "description": "混沌实验名称"
                    },
                    "pipelineId": {
                        "type": "string",
                        "description": "流水线ID"
                    },
                    "projectId": {
                        "type": "string",
                        "description": "要执行混沌故障注入操作的服务ID"
                    },
                    "mode": {
                        "type": "string",
                        "enum": ["1", "2", "3"],
                        "description": "实验执行模式：1: 任意数量实例, 2: 全部实例, 3: 指定实例"
                    },
                    "duration": {
                        "type": "string",
                        "description": "实验持续时间（秒）"
                    },
                    "containerNum": {
                        "type": "string",
                        "description": "容器数量"
                    },
                    "userName": {
                        "type": "string",
                        "description": "操作者用户名"
                    },
                    "depth": {
                        "type": "string",
                        "description": "操作的参数，例如对于cpu就是${depth}核，内存就是${depth}M内存等等"
                    }
                },
                "required": ["taskType", "experimentName", "projectId", "duration","pipelineId","mode","depth","containerNum"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {
        try {
            // 获取环境变量
            String host = System.getenv().getOrDefault("CHAOS_HOST", "");
            String userName = (String) params.get("userName");

            // 构建请求参数
            Map<String, String> queryParams = new HashMap<>();

            String taskType = (String) params.get("taskType");
            String depth = (String) params.get("depth");
            String operateParam = getOperateParam(taskType, depth);

            queryParams.put("taskType", getFinalType(taskType));
            queryParams.put("experimentName", (String) params.get("experimentName"));
            queryParams.put("projectId", (String) params.get("projectId"));
            queryParams.put("pipelineId", (String) params.get("pipelineId"));
            queryParams.put("mode", (String) params.get("mode"));
            queryParams.put("projectName", "chaos-mcp");
            queryParams.put("duration", (String) params.get("duration"));
            queryParams.put("containerName", "main");
            queryParams.put("createUser",userName);
            queryParams.put("containerNum",(String) params.get("containerNum"));
            queryParams.put("operateParam", operateParam);

            // 发送请求
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/json");
            JsonObject jsonObject = new HttpClient().post(host + createTaskPath, queryParams, headerMap);

            log.info("创建混沌实验成功: {}", jsonObject);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(jsonObject.toString())), false);
        } catch (Exception e) {
            log.error("创建混沌实验失败", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("创建混沌实验失败: " + e.getMessage())),
                    true);
        }
    }

    private String getOperateParam(String chaosType,String depth) {
        return switch (chaosType) {
            // 杀死pod
            case "4" -> "{\"type\":\"killContainer\"}";
            // cpu压力
            case "1" -> "{\"cpuNum\":\"" + depth + "\"}";
            // 内存压力
            case "11" ->
                    "{\"vmNum\":1,\"vmBytes\":\"" + depth + "\",\"vmUnit\":\"GiB\",\"vmTimeOut\":\"10\",\"vmTimeOutUnit\":\"s\"}";
            // 网络延迟
            case "2" -> "{\"delayTime\":\"" + depth + "\"}";
            default -> "{\"type\":\"killContainer\"}";
        };
    }

    // 故障实验的类型映射，获取最终的类型
    private String getFinalType(String chaosType) {
        return switch (chaosType) {
            case "4" -> "4";
            case "1" -> "1";
            case "2" -> "2";
            case "11" -> "1";
            default -> "2";
        };
    }
}
