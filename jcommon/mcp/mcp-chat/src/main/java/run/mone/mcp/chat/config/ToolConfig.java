package run.mone.mcp.chat.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.chat.tool.DocumentProcessingTool;
import run.mone.mcp.chat.tool.SystemInfoTool;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class ToolConfig {


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
