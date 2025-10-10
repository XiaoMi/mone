package run.mone.mcp.dayu.servicequery.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.service.RoleMeta;
import run.mone.hive.roles.tool.*;
import run.mone.mcp.dayu.servicequery.function.DayuServiceQueryFunction;
import run.mone.mcp.dayu.servicequery.function.DayuChatRouterFunction;

@Configuration
public class AgentConfig {

    @Bean
    public RoleMeta roleMeta(DayuServiceQueryFunction dayuServiceQueryFunction) {
        return RoleMeta.builder()
                .profile("你是一名优秀的 Dayu 微服务治理助手")
                .goal("根据服务名检索服务列表，并提供相关辅助信息")
                .constraints("仅回答与 Dayu 微服务服务查询相关的问题")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                ))
                // 用路由器替换默认对话，自动把“查询服务”意图路由到 dayu_service_query
                .mcpTools(Lists.newArrayList(new DayuChatRouterFunction(dayuServiceQueryFunction), dayuServiceQueryFunction))
                .build();
    }

    @Bean
    public DayuServiceQueryFunction dayuServiceQueryFunction(
            @Value("${dayu.base-url:}") String baseUrl,
            @Value("${dayu.auth-token:}") String token) {
        return new DayuServiceQueryFunction(baseUrl, token);
    }
}


