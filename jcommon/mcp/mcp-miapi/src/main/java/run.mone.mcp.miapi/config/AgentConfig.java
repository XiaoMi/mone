package run.mone.mcp.miapi.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.miapi.function.*;

@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;

    @Autowired
    private IndexDetailFunction indexDetailFunction;
    @Autowired
    private IndexInfoFunction indexInfoFunction;
    @Autowired
    private MiApiFunction miApiFunction;
    @Autowired
    private ProjectListFunction projectListFunction;
    @Autowired
    private SearchApiFunction searchApiFunction;

    @Autowired
    private DubboTestFunction dubboTestFunction;

    @Autowired
    private AddMiApiConfig addMiApiConfig;

    @Autowired
    private CheckMiApiConfig checkMiApiConfig;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("你是一名优秀的miapi助手")
                .goal("你的目标是更好的帮助用户")
                .constraints("专注于提供帮助")
                //内部工具
                .tools(Lists.newArrayList(
                                new ChatTool(),
                                new AskTool(),
                                new AttemptCompletionTool()
                        )
                )
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(
                        RoleMeta.RoleMode.valueOf(agentMode).equals(RoleMeta.RoleMode.AGENT)
                                ? Lists.newArrayList(new ChatFunction("new-miapi", 20))
                                : Lists.newArrayList(projectListFunction,miApiFunction, searchApiFunction, indexInfoFunction, indexDetailFunction, dubboTestFunction, addMiApiConfig, checkMiApiConfig)
                )
                .workflow("""
                    你是智能化系统，可以根据用户输入的项目名称查询项目信息
                """)
                .meta(ImmutableMap.of(Const.HTTP_PORT,"8084",Const.AGENT_SERVER_NAME,"miapi_server", Const.HTTP_ENABLE_AUTH, "true"))
                .build();
    }

}
