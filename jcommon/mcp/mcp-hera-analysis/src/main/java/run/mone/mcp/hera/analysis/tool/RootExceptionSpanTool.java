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
import run.mone.mcp.hera.analysis.function.RootExceptionSpanFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * 根因Span查询工具
 * <p>
 * 该工具用于根据traceId智能分析trace链路，提取异常根因节点的相关信息用于代码修复。
 * 会自动识别异常节点，并提取项目ID、环境ID和异常堆栈信息。
 * <p>
 * 使用场景：
 * - 智能分析trace链路中的异常根因
 * - 提取异常节点的详细信息
 * - 辅助代码修复和问题定位
 * - 快速识别分布式系统中的错误源头
 *
 * @author dingtao
 * @date 2025/12/09
 */
@Slf4j
@Component
public class RootExceptionSpanTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "root_exception_span";

    /**
     * 根因Span查询Function
     */
    @Autowired
    private RootExceptionSpanFunction rootExceptionSpanFunction;

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
                根据traceId智能分析trace链路，提取异常根因节点的相关信息用于代码修复。
                该工具会自动识别链路中的异常节点，并提取项目ID、环境ID和异常堆栈等关键信息。

                **使用场景：**
                - 智能分析trace链路中的异常根因
                - 提取异常节点的详细信息（项目ID、环境ID、堆栈）
                - 辅助开发人员进行代码修复和问题定位
                - 快速识别分布式系统中的错误源头
                - 获取异常上下文信息用于问题诊断

                **分析能力：**
                - 自动遍历trace链路节点
                - 智能识别异常发生的根因节点
                - 提取异常堆栈和错误信息
                - 返回项目和环境标识

                **重要提示：**
                - traceId必须是32位由0-9和a-f组成的字符串
                - env必须是staging（预发环境）或online（线上环境）
                - 确保traceId对应的链路数据已完整采集
                """;
    }

    @Override
    public String parameters() {
        return """
                - traceId: (必填) 追踪ID，32位由0-9和a-f组成的随机字符串
                - env: (必填) 环境标识，可选值：staging（预发环境）、online（线上环境）
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
                <root_exception_span>
                <traceId>32位追踪ID</traceId>
                <env>环境标识（staging或online）</env>
                %s
                </root_exception_span>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 分析线上环境的异常trace
                <root_exception_span>
                <traceId>a1b2c3d4e5f6789012345678abcdef01</traceId>
                <env>online</env>
                </root_exception_span>

                示例 2: 分析预发环境的异常trace
                <root_exception_span>
                <traceId>fedcba9876543210f6e5d4c3b2a10000</traceId>
                <env>staging</env>
                </root_exception_span>

                示例 3: 提取异常根因节点信息
                <root_exception_span>
                <traceId>123456789abcdef0123456789abcdef0</traceId>
                <env>online</env>
                </root_exception_span>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数：traceId
            if (!inputJson.has("traceId") || StringUtils.isBlank(inputJson.get("traceId").getAsString())) {
                log.error("root_exception_span 操作缺少必填参数 traceId");
                result.addProperty("error", "缺少必填参数 'traceId'");
                return result;
            }

            // 验证必填参数：env
            if (!inputJson.has("env") || StringUtils.isBlank(inputJson.get("env").getAsString())) {
                log.error("root_exception_span 操作缺少必填参数 env");
                result.addProperty("error", "缺少必填参数 'env'");
                return result;
            }

            // 获取参数
            String traceId = inputJson.get("traceId").getAsString().trim();
            String env = inputJson.get("env").getAsString().trim();

            // 验证参数
            if (traceId.isEmpty()) {
                log.warn("traceId 参数为空");
                result.addProperty("error", "参数错误：traceId不能为空");
                return result;
            }

            if (env.isEmpty()) {
                log.warn("env 参数为空");
                result.addProperty("error", "参数错误：env不能为空");
                return result;
            }

            // 验证traceId格式
            if (!isValidTraceId(traceId)) {
                log.warn("traceId 格式不正确: {}", traceId);
                result.addProperty("error", "参数错误：traceId必须是32位由0-9和a-f组成的字符串");
                return result;
            }

            // 验证env参数
            if (!env.equals("staging") && !env.equals("online")) {
                log.warn("Invalid env parameter: {}", env);
                result.addProperty("error", "参数错误：env必须是staging或online");
                return result;
            }

            log.info("开始分析根因Span，traceId: {}, env: {}", traceId, env);

            // 构建Function参数
            Map<String, Object> functionArgs = new HashMap<>();
            functionArgs.put("traceId", traceId);
            functionArgs.put("env", env);

            // 调用Function的apply方法
            Flux<McpSchema.CallToolResult> fluxResult = rootExceptionSpanFunction.apply(functionArgs);

            // 将Flux转换为同步结果
            McpSchema.CallToolResult toolResult = fluxResult.blockFirst();

            if (toolResult == null) {
                log.error("Function返回空结果");
                result.addProperty("error", "分析失败：未返回结果");
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
            String analysisResult = extractTextFromContent(toolResult.content());

            // 设置成功响应
            result.addProperty("result", analysisResult);
            result.addProperty("success", true);

            log.info("成功分析根因Span，traceId: {}, env: {}", traceId, env);

            return result;

        } catch (Exception e) {
            log.error("执行 root_exception_span 操作时发生异常", e);
            result.addProperty("error", "分析根因Span失败：" + e.getMessage());
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
}
