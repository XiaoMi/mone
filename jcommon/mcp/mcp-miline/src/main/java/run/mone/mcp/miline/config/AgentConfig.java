package run.mone.mcp.miline.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.miline.function.MilineFunction;


/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的miline平台操作助手")
                .goal("你的目标是更好的帮助用户，比如项目成员管理、流水线运行等操作")
                .constraints("不要探讨与miline平台无关的东西,如果用户问你,你就直接拒绝掉")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction("miline", 20), new MilineFunction()))
                .build();
    }

}
