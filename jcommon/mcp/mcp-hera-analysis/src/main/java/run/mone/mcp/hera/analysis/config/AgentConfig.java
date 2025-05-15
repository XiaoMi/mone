package run.mone.mcp.hera.analysis.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.SpeechToTextTool;
import run.mone.hive.roles.tool.TextToSpeechTool;
import run.mone.mcp.chat.tool.SystemInfoTool;
import run.mone.mcp.hera.analysis.function.HeraAnalysisFunction;

/**
 * @author zhangxiaowei6
 * @Date 2025/5/7 16:20
 */

@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Autowired
    private HeraAnalysisFunction heraAnalysisFunction;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的私人助理")
                .goal("你的目标是更好的帮助用户")
                .constraints("不要探讨一些负面的东西,如果用户问你,你可以直接拒绝掉")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new SpeechToTextTool(),
                        new SystemInfoTool(),
                        new TextToSpeechTool()))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName), heraAnalysisFunction))
                .build();
    }
}
