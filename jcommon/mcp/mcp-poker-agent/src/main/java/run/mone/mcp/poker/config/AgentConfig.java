package run.mone.mcp.poker.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.poker.tool.AnalyzeOpponentTool;
import run.mone.mcp.poker.tool.CalculateWinRateTool;
import run.mone.mcp.poker.tool.MakeDecisionTool;

/**
 * @author poker-agent
 * @date 2025/10/21
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名世界级的德州扑克专业牌手和策略分析师")
                .goal("你的目标是通过精确的数学计算、胜率分析和对手建模，帮助用户在德州扑克中做出最优决策，战胜其他玩家")
                .constraints("专注于德州扑克策略分析，提供基于数学和概率的决策建议。使用提供的工具进行胜率计算、策略决策和对手分析")
                .tools(Lists.newArrayList(
                        new CalculateWinRateTool(),
                        new MakeDecisionTool(),
                        new AnalyzeOpponentTool(),
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                        )
                )
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .build();
    }


}
