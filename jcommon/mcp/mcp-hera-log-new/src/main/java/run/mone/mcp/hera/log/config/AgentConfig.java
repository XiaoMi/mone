package run.mone.mcp.hera.log.config;

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
import run.mone.mcp.hera.log.function.HeraLogCreateFunction;
import run.mone.mcp.hera.log.service.HeraLogService;
import run.mone.mcp.hera.log.tool.HeraLogCreateTool;

/**
 * @author xueshan
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
    private HeraLogCreateTool heraLogCreateTool;

    @Autowired
    private HeraLogCreateFunction heraLogCreateFunction;

    @Bean
    public RoleMeta roleMeta() {

        ChatFunction chat = new ChatFunction(agentName, 20);
        chat.setDesc("和%s聊天，问问%s关于Hera日志创建的miline流水线的信息。支持各种形式如：'miline链接'、'请%s创建Hera日志'");

        return RoleMeta.builder()
                .name("Hera日志管理专家")
                .profile("你是Hera日志专家可以为用户创建Hera日志")
                .goal("你的目标是根据用户输入的关于miline流水线的配置为其创建Hera日志")
                .constraints("不要探讨一些负面的东西,如果用户问你,你可以直接拒绝掉")
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        heraLogCreateTool
                        ))
                .mcpTools(
                    RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT) 
                        ? Lists.newArrayList(chat) 
                        : Lists.newArrayList(heraLogCreateFunction)
                )
                .meta(ImmutableMap.of(Const.HTTP_PORT, mcpPort,Const.AGENT_SERVER_NAME, agentName))
                .build();
    }
}
