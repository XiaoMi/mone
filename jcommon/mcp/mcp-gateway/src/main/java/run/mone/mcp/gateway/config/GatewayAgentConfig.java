package run.mone.mcp.gateway.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.mcp.gateway.function.ApiFunction;
import run.mone.mcp.gateway.function.DeleteFilterFunction;
import run.mone.mcp.gateway.function.FilterFunction;
import run.mone.mcp.gateway.function.ReferencedFunction;

/**
 * Gateway Agent配置类
 *
 * @author goodjava@qq.com
 */
@Configuration
public class GatewayAgentConfig {

    @Autowired
    private ApiFunction apiFunction;

    @Autowired
    private FilterFunction filterFunction;

    @Autowired
    private ReferencedFunction referencedFunction;

    @Autowired
    private DeleteFilterFunction deleteFilterFunction;

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;


    @Value("${mcp.agent.name}")
    private String agentName;

    @Value("${mcp.http.port}")
    private String httpPort;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的网关Gateway助手")
                .goal("你的目标是更好的帮助用户管理和查询网关Gateway API信息")
                .constraints("专注于提供网关Gateway API相关的帮助")
                // 内部工具列表为空，使用默认工具
                .tools(Lists.newArrayList())
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(
                        RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT)
                                ? Lists.newArrayList(new ChatFunction(agentName, 20))
                                : Lists.newArrayList(apiFunction, filterFunction, referencedFunction, deleteFilterFunction)
                )
                .workflow("""
                        你是网关Gateway智能化助手，严格按照以下步骤执行：
                            - 理解用户的查询需求
                            - 根据需求选择合适的操作（listApiInfo或detailByUrl）
                            - 执行查询并返回结果
                            - 如有需要，提供进一步的帮助和建议
                        """)
                .meta(ImmutableMap.of(
                        Const.HTTP_PORT, httpPort,
                        Const.AGENT_SERVER_NAME, agentName,
                        "http.enable.auth", "true"
                ))
                .build();
    }
}

