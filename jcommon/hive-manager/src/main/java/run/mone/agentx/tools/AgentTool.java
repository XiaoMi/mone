package run.mone.agentx.tools;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.service.AgentService;
import run.mone.hive.roles.tool.ITool;

/**
 * 用于查找最合适的 agent 的工具
 */
@Component
public class AgentTool implements ITool {

    @Autowired
    private AgentService agentService;

    @Override
    public String getName() {
        return "find_agent";
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
                一个用于查找最合适的 agent 的工具。
                当用户需要执行特定任务时，可以使用此工具来查找最匹配的 agent。
                            
                **使用场景：**
                - 当用户需要执行特定任务时
                - 当需要查找具有特定能力的 agent 时
                            
                **输出：** 工具将返回最匹配的 agent 信息。
                """;
    }

    @Override
    public String parameters() {
        return """
                - task: (必填) 需要执行的任务描述
                """;
    }

    @Override
    public String usage() {
        return """
                (注意：使用此工具时，必须在 <chat> 标签内返回 agent 信息):

                示例: 查找能处理数据分析任务的 agent
                <chat>
                  <task>需要处理大量数据并生成分析报告</task>
                  <result>
                    找到匹配的 agent:
                    - 名称: DataAnalysisAgent
                    - 描述: 专业的数据分析 agent，擅长处理大规模数据并生成分析报告
                    - 状态: 活跃
                  </result>
                </chat>
                """;
    }

    @Override
    public JsonObject execute(JsonObject inputJson) {
        JsonObject result = new JsonObject();
        try {
            String task = inputJson.has("task") ? inputJson.get("task").getAsString() : "";

            if (task.isEmpty()) {
                result.addProperty("error", "任务描述不能为空");
                return result;
            }

            // 使用AgentService查找最合适的agent
            AgentWithInstancesDTO selectedAgent = agentService.findMostSuitableAgent(task).block();

            if (selectedAgent == null) {
                result.addProperty("error", "没有找到可用的 agent");
                return result;
            }

            // 构建结果
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("找到匹配的 agent:\n");
            infoBuilder.append("- 名称: ").append(selectedAgent.getAgent().getName()).append("\n");
            infoBuilder.append("- 描述: ").append(selectedAgent.getAgent().getDescription()).append("\n");
            infoBuilder.append("- 状态: ").append(selectedAgent.getAgent().getState() == 1 ? "活跃" : "非活跃");

            // 添加工具信息
            if (selectedAgent.getAgent().getToolMap() != null) {
                infoBuilder.append("\n- 工具: ").append(selectedAgent.getAgent().getToolMap());
            }
            if (selectedAgent.getAgent().getMcpToolMap() != null) {
                infoBuilder.append("\n- MCP工具: ").append(selectedAgent.getAgent().getMcpToolMap());
            }

            result.addProperty("result", infoBuilder.toString());
            return result;

        } catch (Exception e) {
            result.addProperty("error", "查找 agent 时出错: " + e.getMessage());
            return result;
        }
    }
}