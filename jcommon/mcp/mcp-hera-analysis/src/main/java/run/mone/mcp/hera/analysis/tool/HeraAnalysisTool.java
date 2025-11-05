package run.mone.mcp.hera.analysis.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.analysis.service.HeraAnalysisService;

/**
 * Hera链路分析工具
 * <p>
 * 该工具用于根据traceId分析trace链路上异常或慢查询出现的根本原因。
 * 通过AI智能分析，帮助开发人员快速定位问题根源，提高问题排查效率。
 * <p>
 * 使用场景：
 * - 分析异常trace链路，定位错误根因
 * - 分析慢查询trace链路，找出性能瓶颈
 * - 排查分布式系统中的链路问题
 * - 辅助故障诊断和性能优化
 *
 * @author dingtao
 * @date 2025/1/16
 */
@Slf4j
@Component
public class HeraAnalysisTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "hera_analysis";

    /**
     * Hera分析服务
     */
    @Autowired
    private HeraAnalysisService heraAnalysisService;

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
                根据traceId智能分析trace链路上异常或慢查询出现的根本原因。该工具利用AI能力，
                深度分析分布式链路追踪数据，帮助快速定位问题根源。
                
                **使用场景：**
                - 分析异常trace链路，快速定位错误根因
                - 分析慢查询trace链路，找出性能瓶颈所在
                - 排查分布式系统中的调用链路问题
                - 辅助开发人员进行故障诊断
                - 支持性能优化和系统调优
                
                **分析能力：**
                - 自动识别链路中的异常节点
                - 分析慢查询的原因和影响范围
                - 提供问题根因的详细说明
                - 给出优化建议和解决方案
                
                **重要提示：**
                - traceId必须是32位由0-9和a-f组成的随机字符串
                - 默认分析线上环境（online）的trace数据
                - 确保traceId对应的链路数据已经采集完整
                """;
    }

    @Override
    public String parameters() {
        return """
                - traceId: (必填) 追踪ID，32位由0-9和a-f组成的随机字符串
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
                <hera_analysis>
                <traceId>32位追踪ID</traceId>
                %s
                </hera_analysis>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 分析异常trace链路
                <hera_analysis>
                <traceId>a1b2c3d4e5f6789012345678abcdef01</traceId>
                </hera_analysis>
                
                示例 2: 分析慢查询trace链路
                <hera_analysis>
                <traceId>fedcba9876543210f6e5d4c3b2a10000</traceId>
                </hera_analysis>
                
                示例 3: 排查分布式链路问题
                <hera_analysis>
                <traceId>123456789abcdef0123456789abcdef0</traceId>
                </hera_analysis>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("traceId") || StringUtils.isBlank(inputJson.get("traceId").getAsString())) {
                log.error("hera_analysis 操作缺少必填参数 traceId");
                result.addProperty("error", "缺少必填参数 'traceId'");
                return result;
            }

            // 获取traceId参数
            String traceId = inputJson.get("traceId").getAsString().trim();
            
            if (traceId.isEmpty()) {
                log.warn("traceId 参数为空");
                result.addProperty("error", "参数错误：traceId不能为空");
                return result;
            }

            // 验证traceId格式（32位16进制字符串）
            if (!isValidTraceId(traceId)) {
                log.warn("traceId 格式不正确: {}", traceId);
                result.addProperty("error", "参数错误：traceId必须是32位由0-9和a-f组成的字符串");
                return result;
            }

            log.info("开始分析trace链路，traceId: {}", traceId);

            // 调用服务分析trace根本原因，默认环境为online
            String analysisResult = heraAnalysisService.analyzeTraceRoot(traceId, "online");

            // 设置成功响应
            result.addProperty("result", analysisResult);
            result.addProperty("traceId", traceId);
            result.addProperty("environment", "online");
            result.addProperty("success", true);

            log.info("成功分析trace链路，traceId: {}", traceId);

            return result;

        } catch (Exception e) {
            log.error("执行 hera_analysis 操作时发生异常", e);
            result.addProperty("error", "分析trace链路失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }

    /**
     * 验证traceId格式是否正确
     * traceId应该是32位由0-9和a-f组成的字符串
     *
     * @param traceId 追踪ID
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

