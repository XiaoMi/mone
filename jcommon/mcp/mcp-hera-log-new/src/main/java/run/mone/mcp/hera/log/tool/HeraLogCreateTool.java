package run.mone.mcp.hera.log.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.mcp.hera.log.service.HeraLogService;

@Slf4j
@Component
public class HeraLogCreateTool implements ITool {

    public static final String name = "hera_log_create";

    @Autowired
    private HeraLogService heraLogService;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String description() {
        return """
                为miline流水线创建Hera日志。该工具可以根据projectId、pipelineId(有时也会称为envId)、tailName、logPath创建Hera日志，
                帮助快速定位和分析日志详情。

                **使用场景：**
                - 给定miline流水线的项目id，环境（流水线）id，要创建的Hera日志名称、日志路径进行创建
                - 根据提供的miline流水线的链接创建Hera日志

                **返回信息：**
                - 创建出来的Hera日志的tailId，该tail在公共MCP-Space空间下的公共MCP-store下
            
                **重要提示：**
                - projectId为miline项目ID（数字），必填
                - pipelineId为环境（流水线）ID，有时也称envId（数字），必填
                - tailName为日志尾部名称，选填
                - logPath为容器中的日志存储路径，选填
                如果提取不到tailName或者logPath，就设为空字符串
                """;
    }

    @Override
    public String parameters() {
        return """
                - projectId: (必填) miline中的项目id，数字类型
                - pipelineId: (必填) miline中的项目的流水线id，有时也称envId，数字类型
                - tailName: (可选) 创建的Hera日志的tail的名称，字符串类型
                - logPath: (可选) 被采集的miline容器中的日志路径， 字符串类型
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
                <hera_log_create>
                <projectId>miline项目ID</projectId>
                <pipelineId>miline项目中流水线ID（envId）</pipelineId>
                <tailName>日志tail名称（可选）</tailName>
                <logPath>日志路径（可选）</logPath>
                %s
                </hera_log_create>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                示例 1: 创建日志
                <hera_log_create>
                <projectId>123456</projectId>
                <pipelineId>2265886</pipelineId>
                </hera_log_create>
                示例 2: 创建指定日志路径的日志，并命名为test-tail
                <hera_log_create>
                <projectId>123456</projectId>
                <pipelineId>2265886</pipelineId>
                <tailName>test-tail</tailName>
                <logPath>home/work/log/test/test.log</logPath>
                </hera_log_create>
                """;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必填参数：projectId
            if (!inputJson.has("projectId")) {
                log.error("hera_log_create 操作缺少必填参数 projectId");
                result.addProperty("error", "缺少必填参数 'projectId'");
                return result;
            }

            // 验证必填参数：envId
            if (!inputJson.has("pipelineId")) {
                log.error("hera_log_create 操作缺少必填参数 pipelineId");
                result.addProperty("error", "缺少必填参数 'envId'");
                return result;
            }

            // 获取必填参数
            Long projectId = inputJson.get("projectId").getAsLong();
            Long pipelineId = inputJson.get("pipelineId").getAsLong();

            String tailName = inputJson.has("tailName") &&  !StringUtils.isBlank(inputJson.get("tailName").getAsString()) ? inputJson.get("tailName").getAsString() : "";
            String logPath = inputJson.has("logPath") &&  !StringUtils.isBlank(inputJson.get("logPath").getAsString()) ? inputJson.get("logPath").getAsString() : "";

            log.info("开始创建日志，miline项目id: {}, 流水线id: {}, tail名称: {}， 日志路径: {}", projectId, pipelineId, tailName, logPath);

            // 调用服务查询日志详情
            String logResult = heraLogService.createLogByMiline(projectId, pipelineId, tailName, logPath);

            // 设置成功响应
            result.addProperty("result", logResult);
            result.addProperty("projectId", projectId);
            result.addProperty("pipelineId", pipelineId);
            result.addProperty("tailName", tailName);
            result.addProperty("logPath", logPath);
            result.addProperty("success", true);

            log.info("成功创建Hera日志，projectId: {}, pipelineId: {}", projectId, pipelineId);

            return result;

        } catch (Exception e) {
            log.error("执行 hera_log_create 操作时发生异常", e);
            result.addProperty("error", "创建Hera日志失败：" + e.getMessage());
            result.addProperty("success", false);
            return result;
        }
    }

    @Override
    public boolean show() {
        return true;
    }
}
