package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.ApplicationMetricsService;

import java.util.List;
import java.util.Map;

/**
 * 应用指标监控查询Function
 *
 * @author dingtao
 */
@Data
@Slf4j
@Component
public class ApplicationMetricsFunction implements McpFunction {

    @Autowired
    private ApplicationMetricsService applicationMetricsService;

    /**
     * Function名称
     */
    private String name = "stream_application_metrics";

    /**
     * Function描述
     */
    private String desc = "查询指定应用近一分钟的指标监控数据";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "application": {
                        "type": "string",
                        "description": "需要查询的项目ID和项目名称的组合"
                    }
                  },
                "required": ["application"]
            }
            """;

    /**
     * 执行Function逻辑
     *
     * @param args 参数映射
     * @return 包含查询结果的Flux流
     */
    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取application参数
                String application = getStringParam(args, "application");

                if (application.isEmpty()) {
                    log.warn("application 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：application不能为空")), true));
                }

                // 调用服务获取指标数据
                String result = applicationMetricsService.getApplicationMetrics(application);

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("查询应用指标失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("查询失败：" + e.getMessage())), true));
            }
        });
    }
    
    /**
     * 创建成功响应的Flux
     *
     * @param result 操作结果
     * @return 包含结果和完成标记的Flux
     */
    private Flux<McpSchema.CallToolResult> createSuccessFlux(String result) {
        return Flux.just(
            new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false)
//            new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[DONE]")), false)
        );
    }
    
    /**
     * 安全地从参数映射中获取字符串参数
     *
     * @param params 参数映射
     * @param key 参数键
     * @return 字符串参数值，如果不存在则返回空字符串
     */
    private String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? value.toString().replace("-", "_") : "";
    }

    /**
     * 获取Tool的Schema定义
     *
     * @return Schema JSON字符串
     */
    @Override
    public String getToolScheme() {
        return chaosToolSchema;
    }
}

