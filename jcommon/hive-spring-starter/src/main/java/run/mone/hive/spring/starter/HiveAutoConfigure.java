package run.mone.hive.spring.starter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.service.HiveManagerService;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.ITool;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static run.mone.hive.llm.ClaudeProxy.*;

/**
 * @author goodjava@qq.com
 */
@Configuration
@Slf4j
public class HiveAutoConfigure {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Value("${mcp.llm:CLAUDE_COMPANY}")
    private String llmType;

    //大模型
    @Bean
    @ConditionalOnMissingBean
    public LLM llm() {
        llmType = llmType.toLowerCase(Locale.ROOT);
        if (LLMProvider.CLAUDE_COMPANY.name().equalsIgnoreCase(llmType)) {
            LLMConfig config = LLMConfig.builder()
                    .llmProvider(LLMProvider.CLAUDE_COMPANY)
                    .url(getClaudeUrl())
                    .version(getClaudeVersion())
                    .maxTokens(getClaudeMaxToekns())
                    .build();
            return new LLM(config);
        }
        //使用deepseek 原生的v3
        if (LLMProvider.DEEPSEEK.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            return new LLM(LLMConfig.builder().llmProvider(LLMProvider.DEEPSEEK).build());
        }
        //使用字节的deepseek v3
        if (LLMProvider.DOUBAO_DEEPSEEK_V3.name().toLowerCase(Locale.ROOT).equals(llmType)) {
            return new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
        }
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
        config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
        return new LLM(config);
    }

    //传输协议
    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        GrpcServerTransport transport = new GrpcServerTransport(grpcPort);
        transport.setOpenAuth(true);
        return transport;
    }

    //注册类
    @Bean
    @ConditionalOnMissingBean
    public HiveManagerService hiveManagerService() {
        return new HiveManagerService();
    }

    //角色管理
    @Bean
    @ConditionalOnMissingBean
    public RoleService roleService(LLM llm, HiveManagerService hiveManagerService, RoleMeta roleMeta) {
        List<ITool> toolList = roleMeta.getTools();
        List<McpFunction> mcpTools = roleMeta.getMcpTools();

        if (CollectionUtils.isEmpty(toolList)) {
            toolList.addAll(Lists.newArrayList(
                    new ChatTool(),
                    new AskTool(),
                    new AttemptCompletionTool()
            ));
        }
        return new RoleService(llm,
                toolList,
                mcpTools.stream().map(it ->
                        new McpSchema.Tool(it.getName(), it.getDesc(), it.getToolScheme())
                ).toList(),
                mcpTools,
                hiveManagerService,
                roleMeta
        );
    }

    //Mcp Server
    @Bean
    public McpServer mcpServer(RoleService roleService, ServerMcpTransport transport, Map<String, String> meta, RoleMeta roleMeta) {
        List<McpFunction> mcpTools = roleMeta.getMcpTools();
        mcpTools.forEach(it -> it.setRoleService(roleService));
        return new McpServer(transport, mcpTools, meta);
    }

}
