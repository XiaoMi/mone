package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.HeraLogDetailService;

import java.util.List;
import java.util.Map;

/**
 * Hera日志详情查询Function
 * <p>
 * 该工具用于查询Hera日志详情，支持按照spaceId、storeId、input、tailName和时间范围等条件进行过滤查询。
 *
 * @author dingtao
 */
@Data
@Slf4j
@Component
public class HeraLogDetailFunction implements McpFunction {

    @Autowired
    private HeraLogDetailService heraLogDetailService;

    /**
     * Function名称
     */
    private String name = "hera_log_detail_query";

    /**
     * Function描述
     */
    private String desc = "查询Hera日志详情信息";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "spaceId": {
                        "type": "integer",
                        "description": "空间ID，数字类型"
                    },
                    "storeId": {
                        "type": "integer",
                        "description": "存储ID，数字类型"
                    },
                    "input": {
                        "type": "string",
                        "description": "搜索输入内容，可能包含双引号等特殊字符"
                    },
                    "tailName": {
                        "type": "string",
                        "description": "tail名称，例如：matrix_activity-main-test"
                    },
                    "startTime": {
                        "type": "string",
                        "description": "查询开始时间，毫秒时间戳字符串，不提供则使用当前时间前1小时"
                    },
                    "endTime": {
                        "type": "string",
                        "description": "查询结束时间，毫秒时间戳字符串，不提供则使用当前时间"
                    }
                },
                "required": ["spaceId", "storeId", "tailName"]
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
                int spaceId = getIntParam(args, "spaceId", 0);
                int storeId = getIntParam(args, "storeId", 0);
                String input = getStringParam(args, "input");
                String tailName = getStringParam(args, "tailName");

                // 验证必填参数
                if (spaceId == 0) {
                    log.warn("spaceId 参数为空或无效");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：spaceId不能为空或无效")), true));
                }

                if (storeId == 0) {
                    log.warn("storeId 参数为空或无效");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：storeId不能为空或无效")), true));
                }

                if (input.isEmpty()) {
                    log.warn("input 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：input不能为空")), true));
                }

                if (tailName.isEmpty()) {
                    log.warn("tailName 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：tailName不能为空")), true));
                }

                // 获取时间参数，如果未提供则使用默认值（最近1小时）
                long currentTime = System.currentTimeMillis();
                String endTime = getStringParam(args, "endTime");
                if (endTime.isEmpty()) {
                    endTime = String.valueOf(currentTime);
                }

                String startTime = getStringParam(args, "startTime");
                if (startTime.isEmpty()) {
                    // 默认1小时前
                    startTime = String.valueOf(currentTime - 3600000);
                }

                log.info("开始查询Hera日志详情，spaceId: {}, storeId: {}, input: {}, tailName: {}, startTime: {}, endTime: {}",
                        spaceId, storeId, input, tailName, startTime, endTime);

                // 调用服务查询日志
                String result = heraLogDetailService.queryLogDetail(spaceId, storeId, input, tailName, startTime, endTime);

                log.info("成功查询Hera日志详情，spaceId: {}, storeId: {}", spaceId, storeId);

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("查询Hera日志详情失败", e);
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
     * 获取Tool的Schema定义
     *
     * @return Schema JSON字符串
     */
    @Override
    public String getToolScheme() {
        return chaosToolSchema;
    }
}
