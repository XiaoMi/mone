package run.mone.mcp.vuetemp.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.mcp.vuetemp.function.VueTemplateFunction;
import run.mone.mcp.vuetemp.function.NaturalLangTemplateFunction;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;

/**
 * Vue 模板生成器配置类
 * @author goodjava@qq.com
 * @date 2025/1/15
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名专业的Vue前端开发工程师，擅长使用Vue 3、TypeScript、Element Plus、Pinia、Vue Router等技术栈")
                .goal("你的目标是帮助用户快速生成高质量的Vue前端项目模板，包含完整的项目结构和最佳实践")
                .constraints("生成的代码必须符合Vue 3 + TypeScript + Element Plus + Pinia + Vue Router的技术栈要求，代码要规范、可维护")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new TavilySearchTool(),
                        new MemoryTool(),
                        new AttemptCompletionTool()
                        ))
                .mcpTools(Lists.newArrayList(
                        // 提供通用对话入口，便于客户端以 chat 方式连接
                        new ChatFunction(agentName, 60),
                        // 业务工具：结构化与自然语言两种生成模板方式
                        new VueTemplateFunction(),
                        new NaturalLangTemplateFunction()
                ))
                .build();
    }
}
