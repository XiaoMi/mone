package run.mone.moner.server.config;

import com.google.common.collect.Lists;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.Rag;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.moner.server.role.tool.*;

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
                .profile("你是一个浏览器助手")
                .goal("你的目标是更好的帮助用户,完成用户在浏览器中的操作")
                .constraints("不要探讨一些负面的东西,如果用户和你讨论,你可以直接拒绝掉")
                //允许自动从知识库获取内容(意图识别的小模型)
//                .webQuery(WebQuery.builder().autoWebQuery(false).modelType("bert").version("finetune-bert-20250605-73a29258").build())
                //内部工具(意图识别的小模型)
                .rag(Rag.builder().autoRag(false).modelType("bert").version("finetune-bert-20250605-ed8acbcf").build())
                .tools(Lists.newArrayList(
                        // 通用会话与流程工具
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new TavilySearchTool(),
                        new KnowledgeBaseQueryTool(),
                        // Chrome 专用内部工具（用于在系统提示中展示，用法返回 XML，让前端执行）
                        new OpenTabActionTool(),
                        new OperationActionTool(),
                        new ScrollActionTool(),
                        new GetContentActionTool(),
                        new FullPageActionTool(),
                        new CodeActionTool(),
                        new ClickAfterRefreshTool(),
                        new MemoryActionTool(),
                        new ProcessActionTool()
                        ))
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .build();
    }


}
