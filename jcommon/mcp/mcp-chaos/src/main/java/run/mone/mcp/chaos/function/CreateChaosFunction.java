package run.mone.mcp.chaos.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
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
@Component
public class CreateChaosFunction implements McpFunction {

    private String name = "stream_chaos_creator";

    private String desc = "负责创建不同种类的混沌故障类型";

    public final String createTaskPath = "/chaosApiTask/createChaosTask";

    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "taskType": {
                        "type": "string",
                        "enum": ["1", "2", "11", "4"],
                        "description": "故障类型：cpu(压力测试)传1,内存压力测试传11, network(网络延迟)传2，杀死pod传4，如果不传默认就为1"
                    },
                    "experimentName": {
                        "type": "string",
                        "description": "混沌实验名称，如果不传，默认叫mcp-chaos-create-${秒级时间戳}"
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
                        "description": "实验执行模式：1: 任意数量实例, 2: 全部实例, 3: 指定实例，如果不传默认为1"
                    },
                    "duration": {
                        "type": "string",
                        "description": "实验持续时间（ms），如果不传默认为300000"
                    },
                    "containerNum": {
                        "type": "string",
                        "description": "容器数量，如果不传默认为1"
                    },
                    "depth": {
                        "type": "string",
                        "description": "操作的参数，例如对于cpu就是${depth}核，内存就是${depth}M内存等等，如果不传默认为1"
                    }
                },
                "required": ["taskType", "experimentName", "projectId", "duration","pipelineId","mode","depth","containerNum"]
            }
            """;

    @Override
    @ReportCallCount(businessName = "chaos_create_tools", description = "创建混沌实验工具调用")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> params) {
        return Flux.defer(() -> {
            try {
                // 获取环境变量
                String host = System.getenv().getOrDefault("CHAOS_HOST", "");
                if (host.isEmpty()) {
                    log.warn("CHAOS_HOST 环境变量未设置，使用默认空值");
                }
                
                // 构建请求参数
                Map<String, String> queryParams = buildQueryParams(params);
                log.info("构建的请求参数: {}", queryParams);
                
                // 发送请求
                Map<String, String> headerMap = Map.of("Content-Type", "application/json");
                JsonObject jsonObject = new HttpClient().post(host + createTaskPath, queryParams, headerMap);

                log.info("创建混沌实验成功: {}", jsonObject);
                
                // 返回结果流
                return Flux.just(
                    jsonObject.toString(),
                    "[DONE]"
                ).map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            } catch (Exception e) {
                log.error("创建混沌实验失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("创建混沌实验失败: " + e.getMessage())), true));
            }
        });
    }
    
    /**
     * 从参数映射中构建请求参数
     * @param params 输入参数
     * @return 构建的请求参数映射
     */
    private Map<String, String> buildQueryParams(Map<String, Object> params) {
        String taskType = getStringParam(params, "taskType");
        String depth = getStringParam(params, "depth");
        String operateParam = getOperateParam(taskType, depth);

        String userName = getStringParam(params, Const.USER_INTERNAL_NAME);
        log.info("apply userName create chaos user:{}", userName);
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("taskType", getFinalType(taskType));
        queryParams.put("experimentName", getStringParam(params, "experimentName"));
        queryParams.put("projectId", getStringParam(params, "projectId"));
        queryParams.put("pipelineId", getStringParam(params, "pipelineId"));
        queryParams.put("mode", getStringParam(params, "mode"));
        queryParams.put("projectName", "chaos-mcp");
        queryParams.put("duration", getStringParam(params, "duration"));
        queryParams.put("containerName", "main");
        queryParams.put("createUser", userName);
        queryParams.put("containerNum", getStringParam(params, "containerNum"));
        queryParams.put("operateParam", operateParam);
        
        return queryParams;
    }
    
    /**
     * 安全地从参数映射中获取字符串参数
     * @param params 参数映射
     * @param key 参数键
     * @return 字符串参数值，如果不存在则返回空字符串
     */
    private String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : "";
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

    @Override
    public String getToolScheme() {
        return chaosToolSchema;
    }
}
