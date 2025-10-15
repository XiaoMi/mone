package run.mone.mcp.vue3.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.vue3.function.Vue3TemplateFunction;
import run.mone.mcp.vue3.service.Vue3TemplateService;

/**
 * Vue3模板生成器Agent配置
 * @author mcp-vue3
 * @date 2025/1/27
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Autowired
    private Vue3TemplateService vue3TemplateService;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名专业的Vue3前端开发工程师，擅长使用Vue3、TypeScript、Pinia、Vue Router等现代前端技术栈")
                .goal("你的目标是帮助用户快速生成高质量的Vue3组件、页面和项目模板，提供最佳实践和现代化的代码结构")
                .constraints("专注于Vue3相关的前端开发，提供符合Vue3最佳实践的代码模板，不要涉及其他技术栈")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()))
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 60),
                        new Vue3TemplateFunction(vue3TemplateService)))
                .build();
    }
}
