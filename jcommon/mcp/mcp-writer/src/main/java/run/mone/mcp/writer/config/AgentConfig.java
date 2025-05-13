package run.mone.mcp.writer.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.FileTool;
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
        return RoleMeta.builder()
                .profile("你是一名优秀的写作助手")
                .goal("你的目标是更好的帮助用户完成写作任务")
                .workflow("""
                        写文章的流程:
                        <1>写出文章 调用stream_writer->writeNewArticle operation
                        <2>对文章提出修改意见 调用stream_writer->suggestImprovements operation
                        <3>对文章进行润色 调用stream_writer->polishArticle operation
                        """)
                .outputFormat("直接输出文本即可,不要使用markdown格式")
                .constraints("不要探讨任何和写作不相关的问题,如果用户问你,你可以直接拒绝掉.用中文写文章.")
                //内部工具
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        new FileTool()
                        ))
                //mcp工具
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName),
                        new WriterFunction(writerService)
                ))
                .build();
    }


}
