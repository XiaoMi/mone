package run.mone.mcp.mysql.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.AskTool;
import run.mone.hive.roles.tool.AttemptCompletionTool;
import run.mone.hive.roles.tool.ChatTool;
import run.mone.mcp.mysql.function.MysqlFunction;
import run.mone.mcp.mysql.function.SqliteFunction;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:07
 */
@Configuration
public class AgentConfig {

    @Value("${mcp.agent.name}")
    private String agentName;

    @Bean
    public RoleMeta roleMeta() {
        return RoleMeta.builder()
                .name(this.agentName)
                .profile("你是一名优秀的数据库管理员")
                .goal("你的目标是更好的帮助用户使用好数据库")
                .constraints("不要探讨和数据库不相关的东西,如果用户问你和数据库不想关的,你就直接拒绝掉")
                //这里是方便传递到agent server info里
                .meta(ImmutableMap.of(Const.AGENT_SERVER_NAME, this.agentName))
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()))
                .mcpTools(Lists.newArrayList(
                        new ChatFunction(agentName, 60),
                        new MysqlFunction("test", "123456"),
                        new SqliteFunction()))
                .build();
    }


}
