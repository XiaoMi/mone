package run.mone.mcp.cost.control.function;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.cost.control.http.HttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j

public class CostControlFunction implements McpFunction {

    public final String getOneServiceCostPath = "/hera-api/hera-api-mimeter/mimeter/getOneServiceCost";

    private String name = "stream_mione_cost_control";

    private String desc = "用来查询服务某段时间资源占用情况，以及给出建议配置值";

    public CostControlFunction() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    private String costControlToolSchema = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "string",
                        "description": "要查询的服务的id"
                    },
                    "projectName": {
                        "type": "string",
                        "description": "要查询的服务的name"
                    },
                    "timeType":{
                        "type": "string",
                        "enum": ["1","2","3","4","5"],
                        "description": "时间类别,1为7d 2为3d 3为1d 4为最近1h 5为30d"
                    }
                  },
                "required": ["type"]
            }
            """;

    @Override
    public String getToolScheme() {
        return costControlToolSchema;
    }



    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                String projectId = getStringParam(args, "projectId");
                String projectName = getStringParam(args, "projectName");
                String timeType = getStringParam(args, "timeType");
                String userName = getStringParam(args, Const.USER_INTERNAL_NAME);
                log.info("cost control agent apply userName:{}, projectId:{},projectName:{},timeType:{}", userName,projectId,projectName,timeType);

                String host = System.getenv("COST_CONTROL_HOST");
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("projectId", projectId);
                queryParams.put("projectName", projectName);
                queryParams.put("timeType", timeType);
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                JsonObject jsonObject = new HttpClient().post(host + getOneServiceCostPath, queryParams, headerMap);

                String result = jsonObject.toString();
                log.info("执行资源查询操作成功");
                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("执行服务资源查询操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("操作失败：" + e.getMessage())), true));
            }
        });
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

    /**
     * 创建成功响应的Flux
     * @param result 操作结果
     * @return 包含结果和完成标记的Flux
     */
    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
        );
    }
}
