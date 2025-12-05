package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.LogQueryService;

import java.util.List;
import java.util.Map;

/**
 * 日志查询Function
 * <p>
 * 该工具用于查询指定项目和环境下的日志信息。
 * 可以根据日志级别、时间范围等条件进行过滤查询。
 *
 * @author dingtao
 */
@Data
@Slf4j
@Component
public class LogQueryFunction implements McpFunction {

    @Autowired
    private LogQueryService logQueryService;

    /**
     * Function名称
     */
    private String name = "stream_log_query";

    /**
     * Function描述
     */
    private String desc = "查询指定项目和环境(流水线)下的日志信息";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "level": {
                        "type": "string",
                        "description": "日志级别，可选值：ERROR、WARN、INFO、DEBUG等。如果需要查询错误日志，则传入ERROR；如果需要查询所有日志，则不传"
                    },
                    "projectId": {
                        "type": "integer",
                        "description": "项目ID，数字类型"
                    },
                    "pipelineId": {
                        "type": "integer",
                        "description": "流水线ID，数字类型"
                    },
                    "startTime": {
                        "type": "number",
                        "description": "查询开始时间，毫秒时间戳，不提供则使用当前时间前1小时"
                    },
                    "endTime": {
                        "type": "number",
                        "description": "查询结束时间，毫秒时间戳，不提供则使用当前时间"
                    },
                    "traceId": {
                        "type": "string",
                        "description": "链路追踪ID，32位由0-9a-f组成的字符串，用于追踪特定请求的完整调用链路"
                    }
                },
                "required": ["projectId", "pipelineId"]
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
                int projectId = getIntParam(args, "projectId", 0);
                int pipelineId = getIntParam(args, "pipelineId", 0);

                // 验证必填参数
                if (projectId == 0) {
                    log.warn("projectId 参数为空或无效");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：projectId不能为空或无效")), true));
                }

                if (pipelineId == 0) {
                    log.warn("pipelineId 参数为空或无效");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：pipelineId不能为空或无效")), true));
                }

                // 获取可选参数：level
                String level = getStringParam(args, "level");
                if (!level.isEmpty()) {
                    level = level.toUpperCase();
                } else {
                    level = null;
                }

                // 获取可选参数：traceId
                String traceId = getStringParam(args, "traceId");
                if (!traceId.isEmpty()) {
                    // 验证traceId格式（32位，由0-9a-f组成）
                    if (!traceId.matches("^[0-9a-fA-F]{32}$")) {
                        log.warn("traceId 格式不正确：{}", traceId);
                        return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：traceId必须是32位由0-9a-f组成的字符串")), true));
                    }
                } else {
                    traceId = null;
                }

                // 获取时间参数，如果未提供则使用默认值（最近1小时）
                long endTime = getLongParam(args, "endTime", System.currentTimeMillis());
                long startTime = getLongParam(args, "startTime", endTime - 3600000); // 默认1小时（毫秒）

                log.info("开始查询日志，level: {}, projectId: {}, pipelineId: {}, startTime: {}, endTime: {}, traceId: {}",
                        level, projectId, pipelineId, startTime, endTime, traceId);

                // 调用服务查询日志
                String result = logQueryService.queryLogs(level, projectId, pipelineId, startTime, endTime, traceId);

                log.info("成功查询日志，level: {}, projectId: {}, pipelineId: {}, traceId: {}", level, projectId, pipelineId, traceId);

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("查询日志失败", e);
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
     * 安全地从参数映射中获取整型参数
     *
     * @param params 参数映射
     * @param key 参数键
     * @param defaultValue 默认值
     * @return 整型参数值，如果不存在则返回默认值
     */
    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("参数 {} 的值 {} 无法转换为整型，使用默认值 {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 安全地从参数映射中获取长整型参数
     *
     * @param params 参数映射
     * @param key 参数键
     * @param defaultValue 默认值
     * @return 长整型参数值，如果不存在则返回默认值
     */
    private long getLongParam(Map<String, Object> params, String key, long defaultValue) {
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            log.warn("参数 {} 的值 {} 无法转换为长整型，使用默认值 {}", key, value, defaultValue);
            return defaultValue;
        }
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
