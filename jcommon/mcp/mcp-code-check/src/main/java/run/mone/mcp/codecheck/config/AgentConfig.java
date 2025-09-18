package run.mone.mcp.codecheck.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.Rag;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.mcp.service.WebQuery;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.codecheck.tool.CodeSecurityCheckTool;

/**
 * 代码安全检查MCP配置
 * 
 * @author goodjava@qq.com
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name:code-check-agent}")
    private String agentName;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名专业的代码安全审查专家，专门负责网关Filter代码的安全检查和质量评估")
                .goal("你的目标是帮助开发者识别代码中的安全风险、性能问题和潜在缺陷，提供专业的代码质量评估")
                .constraints("专注于代码安全分析，不讨论与代码审查无关的话题。对于发现的问题，要给出具体的改进建议")
                // 禁用自动网络查询和RAG，专注于代码分析
                .webQuery(WebQuery.builder().autoWebQuery(false).build())
                .rag(Rag.builder().autoRag(false).build())
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool(),
                        // 核心代码检查工具
                        new CodeSecurityCheckTool()))
                // MCP工具
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                // 30秒超时
                .timeout(30000)
                .build();
    }
}
