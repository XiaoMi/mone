package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.DubboInterfaceQpsService;

import java.util.List;
import java.util.Map;

/**
 * Dubbo接口QPS查询Function
 * <p>
 * 该工具用于查询应用下具体某个Dubbo接口在指定时间段内的QPS数据。
 * 可以帮助用户快速了解Dubbo接口的调用情况和性能表现。
 *
 * @author dingtao
 */
@Data
@Slf4j
@Component
public class DubboInterfaceQpsFunction implements McpFunction {

    @Autowired
    private DubboInterfaceQpsService dubboInterfaceQpsService;

    /**
     * Function名称
     */
    private String name = "stream_dubbo_interface_qps";

    /**
     * Function描述
     */
    private String desc = "查询应用下具体某个Dubbo接口方法在指定时间段内的调用QPS数据";

    /**
     * Function参数Schema定义
     */
    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "projectName": {
                        "type": "string",
                        "description": "应用名称，例如 'gis'"
                    },
                    "dubboServiceName": {
                        "type": "string",
                        "description": "Dubbo服务的完整类名，例如 'com.xiaomi.goods.gis.api.BatchedInfoService'"
                    },
                    "dubboMethod": {
                        "type": "string",
                        "description": "Dubbo方法名，例如 'getBatchedMultiInfo'"
                    },
                    "serverZone": {
                        "type": "string",
                        "description": "服务器区域，例如 'cn'，不提供则默认为'cn'"
                    },
                    "startTime": {
                        "type": "number",
                        "description": "查询开始时间，Unix时间戳（毫秒），不提供则使用当前时间前1小时"
                    },
                    "endTime": {
                        "type": "number",
                        "description": "查询结束时间，Unix时间戳（毫秒），不提供则使用当前时间"
                    }
                },
                "required": ["projectName", "dubboServiceName", "dubboMethod"]
            }
            """;

    /**
     * 执行Function逻辑
     *
     * @param args 参数映射
     * @return 包含查询结果的Flux流
     */
    @Override
    @ReportCallCount(businessName = "hera-dubbo-interface-qps", description = "查询应用下具体某个Dubbo接口方法在指定时间段内的调用QPS数据")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取必填参数
                String projectName = getStringParam(args, "projectName");
                String dubboServiceName = getStringParam(args, "dubboServiceName");
                String dubboMethod = getStringParam(args, "dubboMethod");

                // 验证必填参数
                if (projectName.isEmpty()) {
                    log.warn("projectName 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：projectName不能为空")), true));
                }

                if (dubboServiceName.isEmpty()) {
                    log.warn("dubboServiceName 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：dubboServiceName不能为空")), true));
                }

                if (dubboMethod.isEmpty()) {
                    log.warn("dubboMethod 参数为空");
                    return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("参数错误：dubboMethod不能为空")), true));
                }

                // 处理projectName中的横线
                projectName = projectName.replace("-", "_");

                // 获取可选参数：serverZone，默认为 "cn"
                String serverZone = getStringParam(args, "serverZone");
                if (serverZone.isEmpty()) {
                    serverZone = "cn";
                }

                // 获取时间参数，如果未提供则使用默认值（最近1小时）
                long endTime = getLongParam(args, "endTime", System.currentTimeMillis());
                long startTime = getLongParam(args, "startTime", endTime - 3600 * 1000);

                endTime = endTime / 1000;
                startTime = startTime / 1000;

                log.info("开始查询Dubbo接口QPS，projectName: {}, dubboServiceName: {}, dubboMethod: {}, serverZone: {}, startTime: {}, endTime: {}",
                        projectName, dubboServiceName, dubboMethod, serverZone, startTime, endTime);

                // 调用服务获取QPS数据
                String result = dubboInterfaceQpsService.getDubboInterfaceQps(
                        projectName, dubboServiceName, dubboMethod, serverZone, startTime, endTime);

                log.info("成功查询Dubbo接口QPS，projectName: {}, dubboServiceName: {}, dubboMethod: {}", projectName, dubboServiceName, dubboMethod);

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("查询Dubbo接口QPS失败", e);
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
