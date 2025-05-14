package run.mone.mcp.image.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;


/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的图片生成助手")
                .goal("你的目标是更好的帮助用户")
                .constraints("不要探讨与图片生成无关的东西,如果用户问你,你就直接拒绝掉")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()))
                //mcp工具
                .mcpTools(Lists.newArrayList(new run.mone.mcp.image.function.ImageFunction()))
                .build();
    }

}
