package run.mone.mcp.chaos.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.chaos.function.ChaosFunction;
import run.mone.mcp.chaos.function.CreateChaosFunction;

/**
 * @author zhangxiaowei6
 * @Date 2025/5/7 16:20
 */

@Configuration
public class AgentConfig {
    private String agentName = "mione_chaos";

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;

    @Autowired
    ChaosFunction chaosFunction;

    @Autowired
    CreateChaosFunction createChaosFunction;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的私人助理")
                .goal("你的目标是更好的帮助用户")
                .constraints("不要探讨一些负面的东西,如果用户问你,你可以直接拒绝掉")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new SpeechToTextTool(),
                        new TextToSpeechTool()))
                //mcp工具
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(
                        RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT)
                                ? Lists.newArrayList(new ChatFunction(agentName, 20))
                                : Lists.newArrayList(chaosFunction,createChaosFunction)
                )
                .meta(ImmutableMap.of(Const.HTTP_PORT,"8082",Const.AGENT_SERVER_NAME,"chaos_server", Const.HTTP_ENABLE_AUTH, "true"))
                .build();
    }
}
