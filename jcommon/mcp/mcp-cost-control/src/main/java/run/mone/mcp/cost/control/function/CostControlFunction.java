package run.mone.mcp.cost.control.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;

@Data
@Slf4j

public class CostControlFunction implements McpFunction {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }

    private String costControlToolSchema = """
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

    @Override
    public String getToolScheme() {
        return "";
    }



    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> stringObjectMap) {
        return null;
    }
}
