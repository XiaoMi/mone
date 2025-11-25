package run.mone.mcp.hera.analysis.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.analysis.service.DubboInterfaceQpsService;

/**
 * Dubbo接口QPS查询工具
 * <p>
 * 该工具用于查询应用下具体某个Dubbo接口在指定时间段内的QPS数据。
 * 可以帮助用户快速了解Dubbo接口的调用情况和性能表现。
 * <p>
 * 使用场景：
 * - 监控Dubbo接口的实时调用QPS
 * - 分析接口的负载趋势
 * - 排查接口性能问题
 * - 评估接口的处理能力
 *
 * @author dingtao
 * @date 2025/1/18
 */
@Slf4j
@Component
public class DubboInterfaceQpsTool implements ITool {

    /**
     * 工具名称
     */
    public static final String name = "dubbo_interface_qps";

    /**
     * Dubbo接口QPS查询服务
     */
    @Autowired
    private DubboInterfaceQpsService dubboInterfaceQpsService;

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
                查询应用下具体某个Dubbo接口方法在指定时间段内的调用QPS数据。该工具可以获取接口方法的调用性能指标，
                包括最大QPS、平均QPS和最小QPS。

                **使用场景：**
                - 实时监控Dubbo接口方法的QPS性能表现
                - 分析接口方法在特定时间段的调用负载情况
                - 排查接口性能瓶颈和异常
                - 评估接口的处理能力和稳定性
                - 支持运维人员快速定位性能问题

                **返回信息：**
                - 应用名称
                - Dubbo服务名称
                - Dubbo方法名称
                - 服务区域
                - 查询时间范围
                - 时间段内的最大QPS
                - 时间段内的平均QPS
                - 时间段内的最小QPS

                **重要提示：**
                - appName为应用名称
                - serviceName为完整的Dubbo服务类名（含包名）
                - methodName为具体的方法名
                - serverZone为服务区域，不提供则默认为"cn"
                - startTimeSec和endTimeSec为Unix时间戳（秒）
                - 如果不提供时间参数，默认查询最近1小时的数据
                """;
    }

    @Override
    public String parameters() {
        return """
                - appName: (必填) 应用名称，例如 "gis"
                - serviceName: (必填) Dubbo服务的完整类名，例如 "com.xiaomi.goods.gis.api.BatchedInfoService"
                - methodName: (必填) Dubbo方法名，例如 "getBatchedMultiInfo"
                - serverZone: (可选) 服务器区域，例如 "cn"，不提供则默认为"cn"
                - startTimeSec: (可选) 查询开始时间，Unix时间戳（秒），不提供则使用当前时间前1小时
                - endTimeSec: (可选) 查询结束时间，Unix时间戳（秒），不提供则使用当前时间
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
                <dubbo_interface_qps>
                <appName>应用名称</appName>
                <serviceName>Dubbo服务完整类名</serviceName>
                <methodName>Dubbo方法名</methodName>
                <serverZone>服务器区域（可选）</serverZone>
                <startTimeSec>开始时间戳（可选）</startTimeSec>
                <endTimeSec>结束时间戳（可选）</endTimeSec>
                %s
                </dubbo_interface_qps>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 查询GIS服务BatchedInfoService的getBatchedMultiInfo方法QPS（使用默认时间范围）
                <dubbo_interface_qps>
                <appName>gis</appName>
                <serviceName>com.xiaomi.goods.gis.api.BatchedInfoService</serviceName>
                <methodName>getBatchedMultiInfo</methodName>
                </dubbo_interface_qps>

                示例 2: 查询用户服务接口指定时间段和区域的QPS
                <dubbo_interface_qps>
                <appName>user-service</appName>
                <serviceName>com.example.service.UserService</serviceName>
                <methodName>getUserInfo</methodName>
                <serverZone>cn</serverZone>
                <startTimeSec>1705564800</startTimeSec>
                <endTimeSec>1705568400</endTimeSec>
                </dubbo_interface_qps>

                示例 3: 查询订单服务接口的QPS（指定美国区域）
                <dubbo_interface_qps>
                <appName>order-service</appName>
                <serviceName>com.example.order.OrderService</serviceName>
                <methodName>createOrder</methodName>
                <serverZone>us</serverZone>
                </dubbo_interface_qps>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数：appName
            if (!inputJson.has("appName") || StringUtils.isBlank(inputJson.get("appName").getAsString())) {
                log.error("dubbo_interface_qps 操作缺少必填参数 appName");
                result.addProperty("error", "缺少必填参数 'appName'");
                return result;
            }

            // 验证必填参数：serviceName
            if (!inputJson.has("serviceName") || StringUtils.isBlank(inputJson.get("serviceName").getAsString())) {
                log.error("dubbo_interface_qps 操作缺少必填参数 serviceName");
                result.addProperty("error", "缺少必填参数 'serviceName'");
                return result;
            }

            // 验证必填参数：methodName
            if (!inputJson.has("methodName") || StringUtils.isBlank(inputJson.get("methodName").getAsString())) {
                log.error("dubbo_interface_qps 操作缺少必填参数 methodName");
                result.addProperty("error", "缺少必填参数 'methodName'");
                return result;
            }

            // 获取必填参数
            String appName = inputJson.get("appName").getAsString().trim();
            String serviceName = inputJson.get("serviceName").getAsString().trim();
            String methodName = inputJson.get("methodName").getAsString().trim();

            if (appName.isEmpty()) {
                log.warn("appName 参数为空");
                result.addProperty("error", "参数错误：appName不能为空");
                return result;
            }

            appName = appName.replace("-", "_");

            if (serviceName.isEmpty()) {
                log.warn("serviceName 参数为空");
                result.addProperty("error", "参数错误：serviceName不能为空");
                return result;
            }

            if (methodName.isEmpty()) {
                log.warn("methodName 参数为空");
                result.addProperty("error", "参数错误：methodName不能为空");
                return result;
            }

            // 获取可选参数：serverZone，默认为 "cn"
            String serverZone = inputJson.has("serverZone") && !StringUtils.isBlank(inputJson.get("serverZone").getAsString())
                    ? inputJson.get("serverZone").getAsString().trim()
                    : "cn";

            // 获取时间参数，如果未提供则使用默认值（最近1小时）
            long endTimeSec = inputJson.has("endTimeSec") && !StringUtils.isBlank(inputJson.get("endTimeSec").getAsString())
                    ? inputJson.get("endTimeSec").getAsLong()
                    : System.currentTimeMillis() / 1000;

            long startTimeSec = inputJson.has("startTimeSec") && !StringUtils.isBlank(inputJson.get("startTimeSec").getAsString())
                    ? inputJson.get("startTimeSec").getAsLong()
                    : endTimeSec - 3600; // 默认查询最近1小时

            log.info("开始查询Dubbo接口QPS，appName: {}, serviceName: {}, methodName: {}, serverZone: {}, startTimeSec: {}, endTimeSec: {}",
                    appName, serviceName, methodName, serverZone, startTimeSec, endTimeSec);

            // 调用服务获取QPS数据
            String qpsResult = dubboInterfaceQpsService.getDubboInterfaceQps(
                    appName, serviceName, methodName, serverZone, startTimeSec, endTimeSec);

            // 设置成功响应
            result.addProperty("result", qpsResult);
            result.addProperty("appName", appName);
            result.addProperty("serviceName", serviceName);
            result.addProperty("methodName", methodName);
            result.addProperty("serverZone", serverZone);
            result.addProperty("startTimeSec", startTimeSec);
            result.addProperty("endTimeSec", endTimeSec);
            result.addProperty("success", true);

            log.info("成功查询Dubbo接口QPS，appName: {}, serviceName: {}, methodName: {}", appName, serviceName, methodName);

            return result;

        } catch (Exception e) {
            log.error("执行 dubbo_interface_qps 操作时发生异常", e);
            result.addProperty("error", "查询Dubbo接口QPS失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }
}
