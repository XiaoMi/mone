package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.HeraAnalysisService;

import java.util.List;
import java.util.Map;

@Data
@Slf4j
@Component
public class HeraAnalysisFunction implements McpFunction {

    @Autowired
    private HeraAnalysisService heraAnalysisService;

    private String name = "stream_hera_analysis";

    private String desc = "根据traceId分析trace链路上异常或者慢查询出现的根本原因";

    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "traceId": {
                        "type": "string",
                        "description": "traceId，为32位0-9和a-f组成的随机字符串"
                    }
                  },
                "required": ["traceId"]
            }
            """;


    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                String traceId = getStringParam(args, "traceId");

                if (traceId.isEmpty()) {
                    log.warn("traceId 为空");
                }

                String result = heraAnalysisService.analyzeTraceRoot(traceId, "online");

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("执行混沌操作失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("操作失败：" + e.getMessage())), true));
            }
        });
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

    @Override
    public String getToolScheme() {
        return chaosToolSchema;
    }
}
