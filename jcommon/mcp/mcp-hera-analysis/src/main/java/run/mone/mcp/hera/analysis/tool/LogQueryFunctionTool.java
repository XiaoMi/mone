package run.mone.mcp.hera.analysis.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.analysis.function.LogQueryFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * 流式日志查询工具
 * <p>
 * 该工具用于查询指定项目和流水线下的日志信息。
 * 可以根据日志级别、时间范围、traceId等条件进行过滤查询。
 * <p>
 * 使用场景：
 * - 查询应用的错误日志
 * - 分析特定时间段的日志信息
 * - 根据traceId追踪请求链路
 * - 排查线上问题
 * - 监控应用运行状态
 *
 * @author dingtao
 * @date 2025/12/09
 */
@Slf4j
@Component
public class LogQueryFunctionTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "stream_log_query";

    /**
     * 日志查询Function
     */
    @Autowired
    private LogQueryFunction logQueryFunction;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                查询指定项目和流水线（环境）下的日志信息。该工具可以根据日志级别、时间范围、链路ID等条件进行过滤查询，
                帮助快速定位和分析应用日志。

                **使用场景：**
                - 查询应用的错误日志（ERROR级别）
                - 查询警告日志（WARN级别）
                - 查询所有级别的日志（不指定level）
                - 根据traceId追踪特定请求的完整调用链路
                - 分析特定时间段的日志信息
                - 排查线上问题和异常
                - 监控应用运行状态
                - 追踪问题发生的时间线

                **返回信息：**
                - 符合条件的日志记录
                - 日志的详细信息（时间、级别、内容等）

                **重要提示：**
                - level为日志级别（ERROR、WARN、INFO、DEBUG等），可选参数。如果需要查询错误日志，则传入ERROR；如果需要查询所有日志，则不传
                - traceId为链路追踪ID（32位由0-9a-f组成的字符串），可选参数，用于追踪特定请求
                - logIp为机器IP或容器IP，可选参数，用于筛选特定机器的日志
                - projectId为项目ID（数字）
                - pipelineId为流水线ID（数字）
                - startTime和endTime为毫秒时间戳
                - 如果不提供时间参数，默认查询最近1小时的日志
                """;
    }

    @Override
    public String parameters() {
        return """
                - level: (可选) 日志级别，可选值：ERROR、WARN、INFO、DEBUG等。如果需要查询错误日志，则传入ERROR；如果需要查询所有日志，则不传
                - projectId: (必填) 项目ID，数字类型
                - pipelineId: (必填) 流水线ID，数字类型
                - startTime: (可选) 查询开始时间，毫秒时间戳，不提供则使用当前时间前1小时
                - endTime: (可选) 查询结束时间，毫秒时间戳，不提供则使用当前时间
                - traceId: (可选) 链路追踪ID，32位由0-9a-f组成的字符串，用于追踪特定请求的完整调用链路
                - logIp: (可选) 机器IP或容器IP，用于筛选特定机器的日志
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                任务进度记录（可选）
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <stream_log_query>
                <level>日志级别（可选）</level>
                <projectId>项目ID</projectId>
                <pipelineId>流水线ID</pipelineId>
                <startTime>开始时间戳（可选）</startTime>
                <endTime>结束时间戳（可选）</endTime>
                <traceId>链路追踪ID（可选）</traceId>
                <logIp>机器IP或容器IP（可选）</logIp>
                %s
                </stream_log_query>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 查询项目的ERROR级别日志（使用默认时间范围）
                <stream_log_query>
                <level>ERROR</level>
                <projectId>301316</projectId>
                <pipelineId>1170008</pipelineId>
                </stream_log_query>

                示例 2: 查询指定时间段的WARN级别日志
                <stream_log_query>
                <level>WARN</level>
                <projectId>301316</projectId>
                <pipelineId>1170008</pipelineId>
                <startTime>1763515783000</startTime>
                <endTime>1763519383000</endTime>
                </stream_log_query>

                示例 3: 查询所有级别的日志（不指定level）
                <stream_log_query>
                <projectId>301316</projectId>
                <pipelineId>1170008</pipelineId>
                <startTime>1763515783000</startTime>
                <endTime>1763519383000</endTime>
                </stream_log_query>

                示例 4: 根据traceId追踪特定请求的日志
                <stream_log_query>
                <projectId>301316</projectId>
                <pipelineId>1170008</pipelineId>
                <traceId>4dd8195561b938dc11a05881173a9263</traceId>
                </stream_log_query>

                示例 5: 查询特定traceId的ERROR日志
                <stream_log_query>
                <level>ERROR</level>
                <projectId>301316</projectId>
                <pipelineId>1170008</pipelineId>
                <traceId>4dd8195561b938dc11a05881173a9263</traceId>
                <startTime>1763515783000</startTime>
                <endTime>1763519383000</endTime>
                </stream_log_query>

                示例 6: 查询特定机器IP的日志
                <stream_log_query>
                <projectId>301316</projectId>
                <pipelineId>1170008</pipelineId>
                <logIp>192.168.1.100</logIp>
                </stream_log_query>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数：projectId
            if (!inputJson.has("projectId")) {
                log.error("stream_log_query 操作缺少必填参数 projectId");
                result.addProperty("error", "缺少必填参数 'projectId'");
                return result;
            }

            // 验证必填参数：pipelineId
            if (!inputJson.has("pipelineId")) {
                log.error("stream_log_query 操作缺少必填参数 pipelineId");
                result.addProperty("error", "缺少必填参数 'pipelineId'");
                return result;
            }

            // 获取必填参数
            int projectId = inputJson.get("projectId").getAsInt();
            int pipelineId = inputJson.get("pipelineId").getAsInt();

            // 获取可选参数：level
            String level = null;
            if (inputJson.has("level") && !StringUtils.isBlank(inputJson.get("level").getAsString())) {
                level = inputJson.get("level").getAsString().trim().toUpperCase();
            }

            // 获取可选参数：traceId
            String traceId = null;
            if (inputJson.has("traceId") && !StringUtils.isBlank(inputJson.get("traceId").getAsString())) {
                traceId = inputJson.get("traceId").getAsString().trim();
                // 验证traceId格式（32位，由0-9a-f组成）
                if (!traceId.matches("^[0-9a-fA-F]{32}$")) {
                    log.warn("traceId 格式不正确：{}", traceId);
                    result.addProperty("error", "参数错误：traceId必须是32位由0-9a-f组成的字符串");
                    return result;
                }
            }

            // 获取可选参数：logIp
            String logIp = null;
            if (inputJson.has("logIp") && !StringUtils.isBlank(inputJson.get("logIp").getAsString())) {
                logIp = inputJson.get("logIp").getAsString().trim();
            }

            // 获取时间参数，如果未提供则使用默认值（最近1小时）
            long endTime = inputJson.has("endTime")
                    ? inputJson.get("endTime").getAsLong()
                    : System.currentTimeMillis();

            long startTime = inputJson.has("startTime")
                    ? inputJson.get("startTime").getAsLong()
                    : endTime - 3600000; // 默认查询最近1小时（毫秒）

            log.info("开始查询日志，level: {}, projectId: {}, pipelineId: {}, startTime: {}, endTime: {}, traceId: {}, logIp: {}",
                    level, projectId, pipelineId, startTime, endTime, traceId, logIp);

            // 构建Function参数
            Map<String, Object> functionArgs = new HashMap<>();
            if (level != null) {
                functionArgs.put("level", level);
            }
            functionArgs.put("projectId", projectId);
            functionArgs.put("pipelineId", pipelineId);
            functionArgs.put("startTime", startTime);
            functionArgs.put("endTime", endTime);
            if (traceId != null) {
                functionArgs.put("traceId", traceId);
            }
            if (logIp != null) {
                functionArgs.put("logIp", logIp);
            }

            // 调用Function的apply方法
            Flux<McpSchema.CallToolResult> fluxResult = logQueryFunction.apply(functionArgs);

            // 将Flux转换为同步结果
            McpSchema.CallToolResult toolResult = fluxResult.blockFirst();

            if (toolResult == null) {
                log.error("Function返回空结果");
                result.addProperty("error", "查询失败：未返回结果");
                return result;
            }

            // 检查是否有错误
            if (toolResult.isError()) {
                String errorMsg = extractTextFromContent(toolResult.content());
                log.error("Function执行失败: {}", errorMsg);
                result.addProperty("error", errorMsg);
                return result;
            }

            // 提取结果文本
            String logResult = extractTextFromContent(toolResult.content());

            // 设置成功响应
            result.addProperty("result", logResult);
            if (level != null) {
                result.addProperty("level", level);
            }
            if (traceId != null) {
                result.addProperty("traceId", traceId);
            }
            if (logIp != null) {
                result.addProperty("logIp", logIp);
            }
            result.addProperty("projectId", projectId);
            result.addProperty("pipelineId", pipelineId);
            result.addProperty("startTime", startTime);
            result.addProperty("endTime", endTime);
            result.addProperty("success", true);

            log.info("成功查询日志，level: {}, projectId: {}, pipelineId: {}, traceId: {}, logIp: {}", level, projectId, pipelineId, traceId, logIp);

            return result;

        } catch (Exception e) {
            log.error("执行 stream_log_query 操作时发生异常", e);
            result.addProperty("error", "查询日志失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }

    /**
     * 从Content列表中提取文本内容
     *
     * @param contents Content列表
     * @return 提取的文本内容
     */
    private String extractTextFromContent(java.util.List<McpSchema.Content> contents) {
        if (contents == null || contents.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (McpSchema.Content content : contents) {
            if (content instanceof McpSchema.TextContent textContent) {
                sb.append(textContent.text());
            }
        }
        return sb.toString();
    }
}
