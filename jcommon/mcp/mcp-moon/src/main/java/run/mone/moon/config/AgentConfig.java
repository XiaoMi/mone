package run.mone.moon.config;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.moon.function.MoonCreateFunction;
import run.mone.moon.function.MoonGetFunction;
import run.mone.moon.function.MoonQueryFunction;
/**
 * @author zhangxiaowei6
 * @Date 2025/5/7 16:20
 */

@DependsOn("dubboConfiguration")
@Configuration
public class AgentConfig {
    private String agentName = "mione_moon";

    @Resource
    ApplicationConfig applicationConfig;
    @Resource
    RegistryConfig registryConfig;
    @Value("${moon.dubbo.group}")
    private String group;

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
                        new TextToSpeechTool()))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName,30), new MoonCreateFunction(applicationConfig, registryConfig, group),new MoonGetFunction(applicationConfig, registryConfig, group),new MoonQueryFunction(applicationConfig, registryConfig, group)))
                .build();
    }
}
