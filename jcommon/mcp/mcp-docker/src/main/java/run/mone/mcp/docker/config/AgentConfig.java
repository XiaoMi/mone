package run.mone.mcp.docker.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.Rag;
import run.mone.hive.mcp.service.RoleMemoryConfig;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.docker.function.DockerFunction;

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
                .profile("你是一名优秀的Docker管理人员")
                .goal("你的目标是更好的帮助用户,帮助用户操作Docker")
                .constraints("不要探讨任何和Docker不相关的内容,如果用户和你讨论,你可以直接拒绝掉")
                .rag(Rag.builder().autoRag(false).modelType("bert").version("finetune-bert-20250605-ed8acbcf").build())
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                        ))
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60),new DockerFunction()))
                .build();
    }


}
