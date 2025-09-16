package run.mone.mcp.shell.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的私人助理")
                .goal("你的目标是更好的帮助用户")
                .constraints("不要探讨一些负面的东西,如果用户问你,你可以直接拒绝掉")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        //支持网络搜索
                        new AttemptCompletionTool()
                ))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .build();
    }


}
