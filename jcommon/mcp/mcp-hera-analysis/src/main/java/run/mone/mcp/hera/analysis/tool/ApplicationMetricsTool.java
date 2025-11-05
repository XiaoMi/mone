package run.mone.mcp.hera.analysis.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.analysis.service.ApplicationMetricsService;

/**
 * 应用指标监控查询工具
 * <p>
 * 该工具用于查询指定应用近一分钟的监控指标数据，包括最大QPS和平均QPS。
 * 可以帮助用户快速了解应用的运行状况和性能指标。
 * <p>
 * 使用场景：
 * - 监控应用的实时QPS性能
 * - 分析应用的负载情况
 * - 排查应用性能问题
 * - 评估应用的处理能力
 *
 * @author dingtao
 * @date 2025/1/16
 */
@Slf4j
@Component
public class ApplicationMetricsTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "application_metrics";

    /**
     * 应用指标监控服务
     */
    @Autowired
    private ApplicationMetricsService applicationMetricsService;

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
                查询指定应用近一分钟的指标监控数据工具。该工具可以获取应用的实时性能指标，
                包括最大请求QPS（每秒查询率）和平均请求QPS。
                
                **使用场景：**
                - 实时监控应用的QPS性能表现
                - 分析应用在特定时间段的负载情况
                - 排查应用性能瓶颈和异常
                - 评估应用的处理能力和稳定性
                - 支持运维人员快速定位性能问题
                
                **返回信息：**
                - 应用名称
                - 近一分钟的最大请求QPS
                - 近一分钟的平均请求QPS
                
                **重要提示：**
                - application参数必须是项目ID和项目名称的组合格式
                - 查询的时间范围固定为近一分钟
                - 返回的QPS值会自动取整为整数
                """;
    }

    @Override
    public String parameters() {
        return """
                - application: (必填) 需要查询的应用标识，格式为项目ID和项目名称的组合，他们使用下划线连接，例如 "12345_my_service"
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
                <application_metrics>
                <application>应用标识</application>
                %s
                </application_metrics>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 查询生产环境应用的指标
                <application_metrics>
                <application>prod_user_service</application>
                </application_metrics>
                
                示例 2: 查询测试环境应用的指标
                <application_metrics>
                <application>test_order_service</application>
                </application_metrics>
                
                示例 3: 查询特定项目的应用指标
                <application_metrics>
                <application>12345_payment_gateway</application>
                </application_metrics>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数
            if (!inputJson.has("application") || StringUtils.isBlank(inputJson.get("application").getAsString())) {
                log.error("application_metrics 操作缺少必填参数 application");
                result.addProperty("error", "缺少必填参数 'application'");
                return result;
            }

            // 获取application参数，将横线替换为下划线
            String application = inputJson.get("application").getAsString().replace("-", "_");
            
            if (application.isEmpty()) {
                log.warn("application 参数为空");
                result.addProperty("error", "参数错误：application不能为空");
                return result;
            }

            log.info("开始查询应用指标，application: {}", application);

            // 调用服务获取指标数据
            String metricsResult = applicationMetricsService.getApplicationMetrics(application);

            // 设置成功响应
            result.addProperty("result", metricsResult);
            result.addProperty("application", application);
            result.addProperty("success", true);

            log.info("成功查询应用指标，application: {}, result: {}", application, metricsResult);

            return result;

        } catch (Exception e) {
            log.error("执行 application_metrics 操作时发生异常", e);
            result.addProperty("error", "查询应用指标失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}

