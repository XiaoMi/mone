package run.mone.mcp.milinenew.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.milinenew.tools.RunPipelineTool;
import run.mone.mcp.milinenew.tools.CreateProjectTool;

/**
 * @author goodjava@qq.com
 * @date 2025/1/1
 */
@Configuration
public class AgentConfig {

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的miline助手")
                .goal("你的目标是更好的帮助用户")
                .constraints("专注于提供帮助")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new RunPipelineTool(),
                        new CreateProjectTool(),
                        new AttemptCompletionTool()))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction("miline-new", 20)))
                .build();
    }

}

