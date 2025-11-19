package run.mone.mcp.hera.analysis.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.analysis.service.LogQueryService;

/**
 * 日志查询工具
 * <p>
 * 该工具用于查询指定项目和环境下的日志信息。
 * 可以根据日志级别、时间范围等条件进行过滤查询。
 * <p>
 * 使用场景：
 * - 查询应用的错误日志
 * - 分析特定时间段的日志信息
 * - 排查线上问题
 * - 监控应用运行状态
 *
 * @author dingtao
 * @date 2025/1/18
 */
@Slf4j
@Component
public class LogQueryTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "log_query";

    /**
     * 日志查询服务
     */
    @Autowired
    private LogQueryService logQueryService;

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
                查询指定项目和环境下的日志信息。该工具可以根据日志级别、时间范围等条件进行过滤查询，
                帮助快速定位和分析应用日志。

                **使用场景：**
                - 查询应用的错误日志（ERROR级别）
                - 查询警告日志（WARN级别）
                - 分析特定时间段的日志信息
                - 排查线上问题和异常
                - 监控应用运行状态
                - 追踪问题发生的时间线

                **返回信息：**
                - 符合条件的日志记录
                - 日志的详细信息（时间、级别、内容等）

                **重要提示：**
                - level为日志级别（ERROR、WARN、INFO、DEBUG等）
                - projectId为项目ID（数字）
                - envId为环境ID（数字）
                - startTime和endTime为毫秒时间戳
                - 如果不提供时间参数，默认查询最近1小时的日志
                """;
    }

    @Override
    public String parameters() {
        return """
                - level: (必填) 日志级别，可选值：ERROR、WARN、INFO、DEBUG等
                - projectId: (必填) 项目ID，数字类型
                - envId: (必填) 环境ID，数字类型
                - startTime: (可选) 查询开始时间，毫秒时间戳，不提供则使用当前时间前1小时
                - endTime: (可选) 查询结束时间，毫秒时间戳，不提供则使用当前时间
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
                <log_query>
                <level>日志级别</level>
                <projectId>项目ID</projectId>
                <envId>环境ID</envId>
                <startTime>开始时间戳（可选）</startTime>
                <endTime>结束时间戳（可选）</endTime>
                %s
                </log_query>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 查询项目的ERROR级别日志（使用默认时间范围）
                <log_query>
                <level>ERROR</level>
                <projectId>301316</projectId>
                <envId>1170008</envId>
                </log_query>

                示例 2: 查询指定时间段的WARN级别日志
                <log_query>
                <level>WARN</level>
                <projectId>301316</projectId>
                <envId>1170008</envId>
                <startTime>1763515783000</startTime>
                <endTime>1763519383000</endTime>
                </log_query>

                示例 3: 查询INFO级别日志
                <log_query>
                <level>INFO</level>
                <projectId>301316</projectId>
                <envId>1170008</envId>
                <startTime>1763515783000</startTime>
                <endTime>1763519383000</endTime>
                </log_query>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数：level
            if (!inputJson.has("level") || StringUtils.isBlank(inputJson.get("level").getAsString())) {
                log.error("log_query 操作缺少必填参数 level");
                result.addProperty("error", "缺少必填参数 'level'");
                return result;
            }

            // 验证必填参数：projectId
            if (!inputJson.has("projectId")) {
                log.error("log_query 操作缺少必填参数 projectId");
                result.addProperty("error", "缺少必填参数 'projectId'");
                return result;
            }

            // 验证必填参数：envId
            if (!inputJson.has("envId")) {
                log.error("log_query 操作缺少必填参数 envId");
                result.addProperty("error", "缺少必填参数 'envId'");
                return result;
            }

            // 获取必填参数
            String level = inputJson.get("level").getAsString().trim().toUpperCase();
            int projectId = inputJson.get("projectId").getAsInt();
            int envId = inputJson.get("envId").getAsInt();

            if (level.isEmpty()) {
                log.warn("level 参数为空");
                result.addProperty("error", "参数错误：level不能为空");
                return result;
            }

            // 获取时间参数，如果未提供则使用默认值（最近1小时）
            long endTime = inputJson.has("endTime")
                    ? inputJson.get("endTime").getAsLong()
                    : System.currentTimeMillis();

            long startTime = inputJson.has("startTime")
                    ? inputJson.get("startTime").getAsLong()
                    : endTime - 3600000; // 默认查询最近1小时（毫秒）

            log.info("开始查询日志，level: {}, projectId: {}, envId: {}, startTime: {}, endTime: {}",
                    level, projectId, envId, startTime, endTime);

            // 调用服务查询日志
            String logResult = logQueryService.queryLogs(level, projectId, envId, startTime, endTime);

            // 设置成功响应
            result.addProperty("result", logResult);
            result.addProperty("level", level);
            result.addProperty("projectId", projectId);
            result.addProperty("envId", envId);
            result.addProperty("startTime", startTime);
            result.addProperty("endTime", endTime);
            result.addProperty("success", true);

            log.info("成功查询日志，level: {}, projectId: {}, envId: {}", level, projectId, envId);

            return result;

        } catch (Exception e) {
            log.error("执行 log_query 操作时发生异常", e);
            result.addProperty("error", "查询日志失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
