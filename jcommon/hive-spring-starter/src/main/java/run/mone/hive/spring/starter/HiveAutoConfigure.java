package run.mone.hive.spring.starter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.transport.streamable.HttpServletStreamableServerTransport;
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
import java.util.Map;

/**
 * Hive Spring Starter 自动配置类
 * 
 * 通过配置项 hive.starter.enabled 控制是否启用（默认：true）
 * 设置为 false 时，整个 starter 将不会生效
 * 
 * @author goodjava@qq.com
 */
@Configuration
@Slf4j
@ConditionalOnProperty(name = "hive.starter.enabled", havingValue = "true", matchIfMissing = true)
public class HiveAutoConfigure {

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Value("${enable.auth:false}")
    private String enableAuth;

    //传输协议
    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "grpc")
    GrpcServerTransport grpcServerTransport() {
        GrpcServerTransport transport = new GrpcServerTransport(grpcPort);
        transport.setOpenAuth(true);
        return transport;
    }

    @Bean
    @ConditionalOnProperty(name = "mcp.transport.type", havingValue = "http")
    HttpServletStreamableServerTransport httpServerTransport() {
        HttpServletStreamableServerTransport transport =HttpServletStreamableServerTransport.builder()
                .mcpEndpoint("/mcp")
                .build();
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
    public RoleService roleService(LLM llm, HiveManagerService hiveManagerService, RoleMeta roleMeta, ServerMcpTransport transport, ApplicationContext applicationContext) {
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
                roleMeta,
                transport,
                applicationContext
        );
    }

    //Mcp Server
    @Bean
    public McpServer mcpServer(RoleService roleService, ServerMcpTransport transport, Map<String, String> meta, RoleMeta roleMeta) {
        List<McpFunction> mcpTools = roleMeta.getMcpTools();
        mcpTools.forEach(it -> it.setRoleService(roleService));
        meta.put("name", roleMeta.getName());
        meta.put("profile", roleMeta.getProfile());
        meta.put("goal", roleMeta.getGoal());
        meta.put("constraints", roleMeta.getConstraints());
        meta.put("workflow", roleMeta.getWorkflow());
        meta.put("http.enable.auth", enableAuth);
        meta.putAll(roleMeta.getMeta());
        return new McpServer(transport, mcpTools, meta);
    }

}
