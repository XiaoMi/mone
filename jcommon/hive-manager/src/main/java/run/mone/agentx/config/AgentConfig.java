package run.mone.agentx.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.agentx.tools.AgentTool;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;

/**
 * @author goodjava@qq.com
 * @date 2025/5/6 10:33
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Autowired
    private AgentTool agentTool;

    //管理agent的agent
    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名Agent管理员")
                .goal("你的目标是更好的帮助用户治理Agent")
                .constraints("不要探讨任何和Agent不相关的东西,如果用户问你,你可以直接拒绝掉")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        agentTool))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 20)))
                .build();
    }

}
