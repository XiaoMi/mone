package run.mone.mcp.minimaxrealtime.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.minimaxrealtime.function.MinimaxRealtimeFunction;

/**
 * @author renqingfu
 * @Date 2025/5/22 16:20
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name:minimax-realtime-agent}")
    private String agentName;

    @Autowired
    private MinimaxRealtimeFunction minimaxRealtimeFunction;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名支持实时语音和文本对话的AI助理，基于MiniMax Realtime API")
                .goal("你的目标是通过实时语音和文本交互，为用户提供流畅、自然的对话体验")
                .constraints("请确保对话内容积极正面，如遇到不当请求请礼貌拒绝。在语音对话中保持自然的语调和节奏。")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new TextToSpeechTool(),
                        new SpeechToTextTool())
                )
                //mcp工具
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 20), 
                        minimaxRealtimeFunction))
                .build();
    }
} 