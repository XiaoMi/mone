package run.mone.mcp.codegen.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.codegen.function.AgentGenFunction;
import run.mone.mcp.codegen.function.BizGenFunction;

/**
 * Codegen Agent配置
 * 
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Autowired
    private BizGenFunction bizGenFunction;

    @Autowired
    private AgentGenFunction agentGenFunction;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的代码生成专家，精通各种项目脚手架的生成")
                .goal("你的目标是根据用户的需求，生成高质量的项目代码框架，包括Spring Boot业务项目和MCP Agent项目")
                .constraints("你只专注于代码生成相关的任务。对于非代码生成的问题，你应该礼貌地拒绝并引导用户回到代码生成主题。" +
                        "生成项目时，你需要理解用户的需求，智能推断合适的参数（如包名、项目名等），并使用appropriate的函数来生成代码。")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                        )
                )
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 60),
                        bizGenFunction,
                        agentGenFunction
                ))
                .build();
    }
}

