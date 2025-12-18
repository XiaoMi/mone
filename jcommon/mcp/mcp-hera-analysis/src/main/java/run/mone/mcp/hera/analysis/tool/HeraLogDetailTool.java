package run.mone.mcp.hera.analysis.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.analysis.service.HeraLogDetailService;

/**
 * Hera日志详情查询工具
 * <p>
 * 该工具用于查询Hera日志详情信息。
 * 可以根据spaceId、storeId、input、tailName和时间范围等条件进行查询。
 * <p>
 * 使用场景：
 * - 查询特定空间和存储的日志详情
 * - 根据关键词搜索日志内容
 * - 分析特定时间段的日志
 * - 排查线上问题
 *
 * @author dingtao
 * @date 2025/1/18
 */
@Slf4j
@Component
public class HeraLogDetailTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "hera_log_detail_query";

    /**
     * Hera日志详情查询服务
     */
    @Autowired
    private HeraLogDetailService heraLogDetailService;

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
                查询Hera日志详情信息。该工具可以根据spaceId、storeId、input、tailName和时间范围等条件进行查询，
                帮助快速定位和分析日志详情。

                **使用场景：**
                - 查询特定空间和存储的日志详情
                - 根据关键词或traceId搜索日志内容
                - 分析特定时间段的日志
                - 排查线上问题和异常
                - 追踪问题发生的时间线

                **返回信息：**
                - 符合条件的日志详情记录
                - 日志的详细信息（时间、内容等）

                **重要提示：**
                - spaceId为空间ID（数字），必填
                - storeId为存储ID（数字），必填
                - input为搜索输入内容，可能包含双引号等特殊字符，必填
                - tailName为日志尾部名称，必填
                - startTime和endTime为毫秒时间戳字符串
                - 如果不提供时间参数，默认查询最近1小时的日志
                """;
    }

    @Override
    public String parameters() {
        return """
                - spaceId: (必填) 空间ID，数字类型
                - storeId: (必填) 存储ID，数字类型
                - input: (必填) 搜索输入内容，可能包含双引号等特殊字符
                - tailName: (必填) 日志尾部名称，例如：test-tail-name
                - startTime: (可选) 查询开始时间，毫秒时间戳字符串，不提供则使用当前时间前1小时
                - endTime: (可选) 查询结束时间，毫秒时间戳字符串，不提供则使用当前时间
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
                <hera_log_detail_query>
                <spaceId>空间ID</spaceId>
                <storeId>存储ID</storeId>
                <input>搜索输入内容</input>
                <tailName>日志尾部名称</tailName>
                <startTime>开始时间戳（可选）</startTime>
                <endTime>结束时间戳（可选）</endTime>
                %s
                </hera_log_detail_query>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 查询日志详情（使用默认时间范围）
                <hera_log_detail_query>
                <spaceId>90123</spaceId>
                <storeId>90456</storeId>
                <input>"468647c3c9bc5f23c7a151b0ab711234"</input>
                <tailName>test-tail-name</tailName>
                </hera_log_detail_query>

                示例 2: 查询指定时间段的日志详情
                <hera_log_detail_query>
                <spaceId>90036</spaceId>
                <storeId>90204</storeId>
                <input>"468647c3c9bc5f23c7a151b0ab71c63d"</input>
                <tailName>test-tail-name</tailName>
                <startTime>1764110685906</startTime>
                <endTime>1764139485906</endTime>
                </hera_log_detail_query>

                示例 3: 搜索包含特殊字符的日志
                <hera_log_detail_query>
                <spaceId>90036</spaceId>
                <storeId>90204</storeId>
                <input>"error: connection timeout"</input>
                <tailName>app-service-prod</tailName>
                <startTime>1764110685906</startTime>
                <endTime>1764139485906</endTime>
                </hera_log_detail_query>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数：spaceId
            if (!inputJson.has("spaceId")) {
                log.error("hera_log_detail_query 操作缺少必填参数 spaceId");
                result.addProperty("error", "缺少必填参数 'spaceId'");
                return result;
            }

            // 验证必填参数：storeId
            if (!inputJson.has("storeId")) {
                log.error("hera_log_detail_query 操作缺少必填参数 storeId");
                result.addProperty("error", "缺少必填参数 'storeId'");
                return result;
            }

            // 验证必填参数：input
            if (!inputJson.has("input") || StringUtils.isBlank(inputJson.get("input").getAsString())) {
                log.error("hera_log_detail_query 操作缺少必填参数 input");
                result.addProperty("error", "缺少必填参数 'input'");
                return result;
            }

            // 验证必填参数：tailName
            if (!inputJson.has("tailName") || StringUtils.isBlank(inputJson.get("tailName").getAsString())) {
                log.error("hera_log_detail_query 操作缺少必填参数 tailName");
                result.addProperty("error", "缺少必填参数 'tailName'");
                return result;
            }

            // 获取必填参数
            int spaceId = inputJson.get("spaceId").getAsInt();
            int storeId = inputJson.get("storeId").getAsInt();
            String input = inputJson.get("input").getAsString().trim();
            String tailName = inputJson.get("tailName").getAsString().trim();

            // 获取时间参数，如果未提供则使用默认值（最近1小时）
            long currentTime = System.currentTimeMillis();
            String endTime = inputJson.has("endTime") && !StringUtils.isBlank(inputJson.get("endTime").getAsString())
                    ? inputJson.get("endTime").getAsString().trim()
                    : String.valueOf(currentTime);

            String startTime = inputJson.has("startTime") && !StringUtils.isBlank(inputJson.get("startTime").getAsString())
                    ? inputJson.get("startTime").getAsString().trim()
                    : String.valueOf(currentTime - 3600000); // 默认查询最近1小时（毫秒）

            // 获取分页参数，默认值：page=1, pageSize=20
            int page = inputJson.has("page") ? inputJson.get("page").getAsInt() : 1;
            int pageSize = inputJson.has("pageSize") ? inputJson.get("pageSize").getAsInt() : 20;

            log.info("开始查询Hera日志详情，spaceId: {}, storeId: {}, input: {}, tailName: {}, startTime: {}, endTime: {}, page: {}, pageSize: {}",
                    spaceId, storeId, input, tailName, startTime, endTime, page, pageSize);

            // 调用服务查询日志详情
            String logResult = heraLogDetailService.queryLogDetail(spaceId, storeId, input, tailName, startTime, endTime, page, pageSize);

            // 设置成功响应
            result.addProperty("result", logResult);
            result.addProperty("spaceId", spaceId);
            result.addProperty("storeId", storeId);
            result.addProperty("input", input);
            result.addProperty("tailName", tailName);
            result.addProperty("startTime", startTime);
            result.addProperty("endTime", endTime);
            result.addProperty("page", page);
            result.addProperty("pageSize", pageSize);
            result.addProperty("success", true);

            log.info("成功查询Hera日志详情，spaceId: {}, storeId: {}, page: {}, pageSize: {}", spaceId, storeId, page, pageSize);

            return result;

        } catch (Exception e) {
            log.error("执行 hera_log_detail_query 操作时发生异常", e);
            result.addProperty("error", "查询Hera日志详情失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}