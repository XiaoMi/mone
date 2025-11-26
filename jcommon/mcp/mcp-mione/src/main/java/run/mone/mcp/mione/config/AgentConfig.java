package run.mone.mcp.mione.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;

/**
 * @author shanwb
 * @date 2025/11/25
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Value("${mcp.agent.mode:AGENT}")
    private String agentMode;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .profile("""
                        你是一名优秀的MiOne助手.MiOne是一套研发效能平台，该平台有以下核心组件：
                         - miline: 负责代码开发及CI/CD能力
                         - code-fix：基于OzHera可观测指标定位服务问题并修复上线
                         - miapi-agent：提供api查询及对api进行压力测试能力
                         - scaling：提供服务扩缩容能力
                         - mione_chaos：提供故障注入能力
                         - dayu-agent：提供服务限流查询和修改限流规则能力
                       
                         你的clientId=1212
                        """)
                .goal("你的目标是更好的帮助用户解决关于MiOne平台的问题")
                .constraints("专注于解决MiOne问题")
                //内部工具
                .tools(Lists.newArrayList(
                                new ChatTool(),
                                new AskTool(),
                                new AttemptCompletionTool()
                        )
                )
                .mode(RoleMeta.RoleMode.valueOf(agentMode))
                .mcpTools(Lists.newArrayList(new ChatFunction(agentName, 60)))
                .workflow("""
                    - 扩缩容类需求：
                      + 仅限于scaling提供的能力去解决
                      + 先get_k8s_base_info查看服务基础信息 -> get_basic_monitoring查看监控数据 -> k8s_scale_operation执行扩缩容操作
                    - 故障注入类需求
                      + 仅限于mione_chaos提供的能力去解决
                    - 接口压力测试类需求
                      + 仅限于miapi-agent提供的能力去解决
                    - 代码开发及部署类需求
                      + 仅限于miline提供的能力去解决
                    - 代码修复类需求
                      + 直接转发原始需求给 code-fix，你不要做任何任务拆分，code-fix内部会完整拆分和闭环
                      + 基于code-fix的返回结果 判断这次修复是否成功. 不论结果如何都结束流程AttemptCompletionTool
                    - 其它类需求
                      + 请基于你的专业知识进行问题回答
                """)
                .build();
    }

}


