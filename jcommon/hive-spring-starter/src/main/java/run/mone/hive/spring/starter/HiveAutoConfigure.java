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
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.hive.roles.tool.ITool;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Configuration
@Slf4j
public class HiveAutoConfigure {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    //大模型
    @Bean
    @ConditionalOnMissingBean
    public LLM llm() {
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
    public RoleService roleService(LLM llm, HiveManagerService hiveManagerService, List<ITool> toolList, List<McpFunction> functionList) {
        if (CollectionUtils.isEmpty(toolList)) {
            toolList.addAll(Lists.newArrayList(
                    new ChatTool(),
                    new AskTool(),
                    new AttemptCompletionTool()));
        }
        return new RoleService(llm,
                toolList,
                functionList.stream().map(it ->
                        new McpSchema.Tool(it.getName(), it.getDesc(), it.getToolScheme())
                ).toList(),
                hiveManagerService
        );
    }


    //Mcp Server
    @Bean
    public McpServer mcpServer(RoleService roleService, ServerMcpTransport transport, List<McpFunction> functions, Map<String, String> meta) {
        functions.forEach(it -> it.setRoleService(roleService));
        return new McpServer(transport, functions, meta);
    }


}
