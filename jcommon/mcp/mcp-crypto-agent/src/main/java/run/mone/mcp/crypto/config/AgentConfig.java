package run.mone.mcp.crypto.crypto.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.crypto.tool.CryptoTool;

/**
 * @author hive
 * @date 2025/10/10 10:47
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名虚拟币交易Agent，可以查询余额和执行交易")
                .goal("你的目标是帮助用户查询虚拟币余额和执行内部交易")
                .constraints("只能调用指定的三个API接口，不能执行其他操作")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new CryptoTool(),
                        new AttemptCompletionTool()
                        )
                )
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .build();
    }


}

