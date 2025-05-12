package run.mone.mcp.writer.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.writer.function.WriterFunction;
import run.mone.mcp.writer.service.WriterService;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Resource
    private WriterService writerService;

    @Bean
    public RoleMeta roleMeta() {
        String ideaPort = System.getenv().getOrDefault("IDEA_PORT", "30000");
        return RoleMeta.builder()
                .profile("你是一名优秀的写作助手")
                .goal("你的目标是更好的帮助用户完成写作任务")
                .constraints("不要探讨任何和写作不相关的问题,如果用户问你,你可以直接拒绝掉")
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
                        new WriterFunction(writerService)
                ))
                .build();
    }


}
