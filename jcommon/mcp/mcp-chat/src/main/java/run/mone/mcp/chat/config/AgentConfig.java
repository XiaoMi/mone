package run.mone.mcp.chat.config;

import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.Rag;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.hive.configs.Const;
import run.mone.mcp.chat.function.AddTwoNumbersFunction;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;

    @Autowired
    private AddTwoNumbersFunction addTwoNumbersFunction;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的私人助理")
                .goal("你的目标是更好的帮助用户,和给用户提供情绪价值")
                .constraints("不要探讨一些负面的东西,如果用户和你讨论,你可以直接拒绝掉")
                //允许自动从知识库获取内容(意图识别的小模型)
//                .webQuery(WebQuery.builder().autoWebQuery(false).modelType("bert").version("finetune-bert-20250605-73a29258").build())
                //内部工具(意图识别的小模型)
                .rag(Rag.builder().autoRag(false).modelType("bert").version("finetune-bert-20250605-ed8acbcf").build())
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new TavilySearchTool(),
                        new KnowledgeBaseQueryTool(),
//                        new MemoryTool(RoleMemoryConfig.builder()
//                                .graphStore(RoleMemoryConfig.GraphStoreConfig.builder()
//                                        .llm(RoleMemoryConfig.LlmConfig.builder().providerName(LLMProvider.QWEN.name()).model("qwen3-max").build())
//                                        .build()).build()),
                        new AttemptCompletionTool()
                ))
                .mcpTools(
                        RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT) ?
                                Lists.newArrayList(
                                        new ChatFunction(agentName, 60)
                                ) : Lists.newArrayList(addTwoNumbersFunction))
                .meta(ImmutableMap.of(Const.HTTP_PORT,"8081",Const.AGENT_SERVER_NAME,"chat_server", Const.HTTP_ENABLE_AUTH, "true"))
                .build();
    }


}
