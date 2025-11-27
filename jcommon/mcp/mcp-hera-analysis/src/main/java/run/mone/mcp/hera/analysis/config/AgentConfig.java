package run.mone.mcp.hera.analysis.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.hera.analysis.function.*;
import run.mone.mcp.hera.analysis.tool.ApplicationMetricsTool;
import run.mone.mcp.hera.analysis.tool.DubboInterfaceQpsTool;
import run.mone.mcp.hera.analysis.tool.HeraAnalysisTool;
import run.mone.mcp.hera.analysis.tool.LogQueryTool;

/**
 * @author zhangxiaowei6
 * @Date 2025/5/7 16:20
 */

@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;

    @Value("${mcp.port}")
    private String mcpPort;

    @Autowired
    private HeraAnalysisTool heraAnalysisTool;

    @Autowired
    private ApplicationMetricsTool applicationMetricsTool;

    @Autowired
    private DubboInterfaceQpsTool dubboInterfaceQpsTool;

    @Autowired
    private LogQueryTool logQueryTool;

    @Autowired
    private ApplicationMetricsFunction applicationMetricsFunction;

    @Autowired
    private HeraAnalysisFunction heraAnalysisFunction;

    @Autowired
    private DubboInterfaceQpsFunction dubboInterfaceQpsFunction;

    @Autowired
    private LogQueryFunction logQueryFunction;

    @Autowired
    private HeraLogDetailFunction heraLogDetailFunction;

    @Bean
    public RoleMeta roleMeta() {

        ChatFunction chat = new ChatFunction(agentName, 20);
        chat.setDesc("和%s聊天，问问%s关于Hera可观测性的服务监控和链路追踪相关的数据。支持各种形式如：'%s'、'请%s告诉我监控数据'、'让%s帮我看看服务状态'、'%s你知道服务有什么问题'等。支持上下文连续对话。");

        return RoleMeta.builder()
                .name("Hera可观测系统专家")
                .profile("你是Hera可观测系统专家，精通分布式系统的监控和链路追踪，能够帮助用户诊断和解决复杂的系统问题")
                .goal("你的目标是根据用户输入返回Hera中专业的监控数据和链路追踪数据，帮助用户快速定位和解决系统中的异常和性能问题")
                .constraints("不要探讨一些负面的东西,如果用户问你,你可以直接拒绝掉")
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        heraAnalysisTool,
                        applicationMetricsTool,
                        dubboInterfaceQpsTool,
                        logQueryTool
                        ))
                .mcpTools(
                    RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT) 
                        ? Lists.newArrayList(chat) 
                        : Lists.newArrayList(heraLogDetailFunction)
                )
                .meta(ImmutableMap.of(Const.HTTP_PORT, mcpPort,Const.AGENT_SERVER_NAME, agentName))
                .build();
    }
}
