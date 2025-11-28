package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.RootExceptionSpanService;

import java.util.List;
import java.util.Map;

/**
 * 根因Span查询Function
 * <p>
 * 该工具用于根据traceId智能分析trace链路，提取异常根因节点的相关信息用于代码修复。
 * 会自动识别异常节点，并提取项目ID、环境ID和异常堆栈信息。
 *
 * @author dingtao
 */
@Data
@Slf4j
@Component
public class RootExceptionSpanFunction implements McpFunction {

    @Autowired
    private RootExceptionSpanService rootExceptionSpanService;

    /**
     * Function名称
     */
    private String name = "root_exception_span";

    /**
     * Function描述
     */
    private String desc = "根据traceId智能分析trace链路，提取异常根因节点的相关信息用于代码修复";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "traceId": {
                        "type": "string",
                        "description": "追踪ID，32位由0-9和a-f组成的随机字符串"
                    },
                    "env": {
                        "type": "string",
                        "description": "环境标识，可选值：staging（预发环境）、online（线上环境）",
                        "enum": ["staging", "online"]
                    }
                },
                "required": ["traceId", "env"]
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
                // 获取必填参数
                String traceId = getStringParam(args, "traceId");
                String env = getStringParam(args, "env");

                // 验证必填参数
                if (traceId.isEmpty()) {
                    log.warn("traceId 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：traceId不能为空")), true));
                }

                if (env.isEmpty()) {
                    log.warn("env 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：env不能为空")), true));
                }

                // 验证traceId格式（32位由0-9和a-f组成的字符串）
                if (!isValidTraceId(traceId)) {
                    log.warn("Invalid traceId format: {}", traceId);
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：traceId必须是32位由0-9和a-f组成的字符串")), true));
                }

                // 验证env参数
                if (!env.equals("staging") && !env.equals("online")) {
                    log.warn("Invalid env parameter: {}", env);
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：env必须是staging或online")), true));
                }

                log.info("开始分析根因Span，traceId: {}, env: {}", traceId, env);

                // 调用服务查询根因span
                String result = rootExceptionSpanService.queryRootExceptionSpan(traceId, env);

                log.info("成功分析根因Span，traceId: {}, env: {}", traceId, env);

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("分析根因Span失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("分析失败：" + e.getMessage())), true));
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
        return value != null ? value.toString() : "";
    }

    /**
     * 验证traceId格式
     * TraceId应该是32位由0-9和a-f组成的字符串
     *
     * @param traceId trace ID
     * @return 格式是否正确
     */
    private boolean isValidTraceId(String traceId) {
        if (traceId == null || traceId.length() != 32) {
            return false;
        }
        // 检查是否只包含0-9和a-f字符
        return traceId.matches("[0-9a-fA-F]{32}");
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
