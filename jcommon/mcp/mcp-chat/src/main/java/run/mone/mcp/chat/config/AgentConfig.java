package run.mone.mcp.chat.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.Rag;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.mcp.service.WebQuery;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.chat.tool.SystemInfoTool;

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
                //允许自动从知识库获取内容(意图识别的小模型)
                .webQuery(WebQuery.builder().autoWebQuery(true).modelType("bert").version("finetune-bert-20250605-73a29258").build())
                //内部工具(意图识别的小模型)
                .rag(Rag.builder().autoRag(true).modelType("bert").version("finetune-bert-20250605-ed8acbcf").build())
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        //支持网络搜索
                        new TavilySearchTool(),
                        new AttemptCompletionTool(),
                        new SpeechToTextTool(),
                        new SystemInfoTool(),
                        new TextToSpeechTool()))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 20)))
                //30s
                .timeout(30000)
                .build();
    }


}
