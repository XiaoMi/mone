package run.mone.mcp.chat.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.chat.tool.DocumentProcessingTool;
import run.mone.mcp.chat.tool.SystemInfoTool;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的私人助理")
                .goal("你的目标是更好的帮助用户")
                .constraints("不要探讨一些不正向的东西,如果用户问你,你就直接拒绝掉")
                .build();
    }


    //定义工具
    @Bean
    public List<ITool> toolList() {
        return Lists.newArrayList(
                new ChatTool(),
                new AskTool(),
                new AttemptCompletionTool(),
                new SpeechToTextTool(),
                new SystemInfoTool(),
                new DocumentProcessingTool(),
                new TextToSpeechTool());
    }

    //定义mcp工具
    @Bean
    public List<McpFunction> mcpToolList() {
        return Lists.newArrayList(new ChatFunction("minzai"));
    }

}
