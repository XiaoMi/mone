package run.mone.mcp.coder.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.InterruptQuery;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;

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
                .profile("你是一名优秀的软件工程师")
                .goal("你的目标是根据用户的需求写好代码")
                .constraints("不要探讨和代码不想关的东西,如果用户问你,你可以直接拒绝掉")
                //又来做打断判定的
                .interruptQuery(InterruptQuery.builder().version("finetune-qwen-20250909-71039c8b").modelType("qwen").autoInterruptQuery(false).releaseServiceName("bert-is-break").build())
                .tools(Lists.newArrayList(
                        new ListFilesTool(),
                        new ExecuteCommandTool(),
                        new ReadFileTool(),
                        new SearchFilesTool(),
                        new ReplaceInFileTool(),
                        new ListCodeDefinitionNamesTool(),
                        new WriteToFileTool(),
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                        )
                )
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .build();
    }


}
