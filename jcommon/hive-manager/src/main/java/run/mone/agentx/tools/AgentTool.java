package run.mone.agentx.tools;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.service.AgentService;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.AiMessage;

import java.util.List;
import java.util.stream.Collectors;

import static run.mone.hive.llm.ClaudeProxy.*;

/**
 * 用于查找最合适的 agent 的工具
 */
@Component
public class AgentTool implements ITool {

    @Autowired
    private AgentService agentService;

    private static LLM llm = new LLM(LLMConfig.builder()
            .llmProvider(LLMProvider.CLAUDE_COMPANY)
            .url(getClaudeUrl())
            .version(getClaudeVersion())
            .maxTokens(getClaudeMaxToekns())
            .build());

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

            // 获取所有可用的 agent
            List<AgentWithInstancesDTO> agents = agentService.findAccessibleAgentsWithInstances(1L)
                    .collectList()
                    .block();

            if (agents == null || agents.isEmpty()) {
                result.addProperty("error", "没有找到可用的 agent");
                return result;
            }

            // 过滤出有活跃实例的 agent
            List<AgentWithInstancesDTO> availableAgents = agents.stream()
//                    .filter(agent ->
//                            agent.getInstances() != null &&
//                                    !agent.getInstances().isEmpty() &&
//                                    agent.getInstances().stream().anyMatch(instance -> instance.getIsActive()))
                    .collect(Collectors.toList());

            if (availableAgents.isEmpty()) {
                result.addProperty("error", "没有找到活跃的 agent 实例");
                return result;
            }

            // 构建所有可用 agent 的信息
            StringBuilder agentsInfo = new StringBuilder();
            for (AgentWithInstancesDTO agent : availableAgents) {
                agentsInfo.append("\nAgent ").append(agent.getAgent().getName()).append(":\n");
                agentsInfo.append("- 描述: ").append(agent.getAgent().getDescription()).append("\n");
                if (agent.getAgent().getToolMap() != null) {
                    agentsInfo.append("- 工具: ").append(agent.getAgent().getToolMap()).append("\n");
                }
                if (agent.getAgent().getMcpToolMap() != null) {
                    agentsInfo.append("- MCP工具: ").append(agent.getAgent().getMcpToolMap()).append("\n");
                }
            }

            // 构建提示词
            String prompt = String.format("""
                    请根据以下任务描述，从可用的 agents 中选择最合适的一个。请只返回最匹配的 agent 的名称。

                    任务描述：%s

                    可用的 agents：
                    %s

                    请只返回最匹配的 agent 的名称，不要包含其他内容。
                    """, task, agentsInfo.toString());

            // 调用 LLM 获取最匹配的 agent 名称
            String selectedAgentName = llm.chat(List.of(AiMessage.builder()
                    .role("user")
                    .content(prompt)
                    .build())).trim();

            // 根据名称找到对应的 agent
            AgentWithInstancesDTO selectedAgent = availableAgents.stream()
                    .filter(agent -> agent.getAgent().getName().equals(selectedAgentName))
                    .findFirst()
                    .orElse(availableAgents.get(0)); // 如果没找到，返回第一个

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