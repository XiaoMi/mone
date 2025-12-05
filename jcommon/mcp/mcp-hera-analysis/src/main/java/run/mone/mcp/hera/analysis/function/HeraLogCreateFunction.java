package run.mone.mcp.hera.analysis.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hera.analysis.service.HeraLogService;


import java.util.List;
import java.util.Map;

@Data
@Slf4j
@Component
public class HeraLogCreateFunction implements McpFunction {

    @Autowired
    private HeraLogService heraLogService;

    /**
     * Function名称
     */
    private String name = "hera_log_create";

    /**
     * Function描述
     */
    private String desc = "对指定的miline的项目和流水线创建Hera日志";

    /**
     * Function参数Schema定义
     */

    private String chaosToolSchema = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": "long",
                        "description": "miline项目ID，数字类型"
                    },
                    "pipelineId": {
                        "type": "long",
                        "description": "环境（流水线）ID，有时在链接中也用envId表示，数字类型"
                    },
                    "tailName": {
                        "type": "string",
                        "description": "创建的Hera日志的名称"
                    },
                    "logPath": {
                        "type": "string",
                        "description": "miline流水线容器的日志路径"
                    }
                },
                "required": ["projectId", "envId"]
            }
            """;

    @Override
    public String getToolScheme() {
        return this.chaosToolSchema;
    }

    @Override
    @ReportCallCount(businessName = "hera-log-create", description = "对指定的miline的项目和流水线创建Hera日志")
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        return Flux.defer(() -> {
            try {
                // 获取必填参数
                Long projectId = getLongParam(args, "projectId", 0L);
                Long pipelineId = getLongParam(args, "pipelineId", 0L);
                String tailName = getStringParam(args, "tailName");
                String logPath = getStringParam(args, "logPath");
                String userName = getStringParam(args, Const.TOKEN_USERNAME);
                String userId = getStringParam(args, Const.TOKEN_USER_ID);

                // 验证必填参数
                if (projectId == 0L) {
                    log.warn("projectId 参数为空或无效");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：projectId不能为空或无效")), true));
                }

                if (pipelineId == 0) {
                    log.warn("pipelineId 参数为空或无效");
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("参数错误：pipelineId不能为空或无效")), true));
                }


                log.info("开始创建Hera日志，projectId: {}, pipelineId: {}, tailName: {}, logPath: {} ",
                        projectId, pipelineId, tailName, logPath);

                // 调用服务查询日志
                String result = heraLogService.createLogByMiline(projectId, pipelineId, tailName, logPath, userName, userId);

                log.info("成功查询Hera日志详情，projectId: {}, pipelineId: {}, tailName: {}, logPath: {} ",
                        projectId, pipelineId, tailName, logPath);

                return createSuccessFlux(result);
            } catch (Exception e) {
                log.error("创建Hera日志失败", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("创建失败：" + e.getMessage())), true));
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

    private Long getLongParam(Map<String, Object> params, String key, Long defaultValue) {
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
}
