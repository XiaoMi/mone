package run.mone.mcp.idea.composer.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.idea.composer.function.CodeReviewFunction;
import run.mone.mcp.idea.composer.function.ComposerFunction;
import run.mone.mcp.idea.composer.function.MethodRenameFunction;
import run.mone.mcp.idea.composer.service.IdeaService;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Resource
    private IdeaService ideaService;

    @Bean
    public RoleMeta roleMeta() {
        String ideaPort = System.getenv().getOrDefault("IDEA_PORT", "30000");
        return RoleMeta.builder()
                .profile("你是一名优秀的编程助手")
                .goal("你的目标是更好的帮助程序员完成任务")
                .constraints("不要探讨任何和编码不相关的问题,如果用户问你,你可以直接拒绝掉")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        //文件操作工具
                        new FileTool(),
                        //执行文件工具
                        new ExecuteTool()
                        ))
                //mcp工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName),
                        //生成或者修改大量代码
                        new ComposerFunction(ideaPort),
                        //代码review
                        new CodeReviewFunction(ideaService),
                        //给方法重命名
                        new MethodRenameFunction(ideaService)
                ))
                .build();
    }


}
