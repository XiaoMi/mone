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
import run.mone.mcp.dayu.servicequery.function.DayuServiceLimitFlowFunction;
import run.mone.mcp.dayu.servicequery.function.DayuServiceLimitFlowRouterFunction;
import run.mone.mcp.dayu.servicequery.function.ThinkingLimitFlowRouterFunction;
import run.mone.mcp.dayu.servicequery.function.RealThinkingRouterFunction;

@Configuration
public class AgentConfig {

    @Bean
    public RoleMeta roleMeta(DayuServiceQueryFunction dayuServiceQueryFunction, 
                           DayuServiceLimitFlowFunction dayuServiceLimitFlowFunction) {
        return RoleMeta.builder()
                .profile("你是 Dayu 微服务治理助手。工作方式：\n"
                        + "1) 先分点思考（短句，每步一行），再决定是否调用工具；\n"
                        + "2) 缺少参数时只追问缺失项，不下结论、不直接执行；\n"
                        + "3) 一律逐条流式返回（每个思考点单独输出）。\n\n"
                        + "Few-shot 示例：\n"
                        + "用户：查询dayu的服务限流\n"
                        + "思考：\n- 识别为限流查询\n- 可能缺少 app/service 具体名称\n- 追问缺失参数：应用或服务名\n"
                        + "追问：请提供应用或服务名称。\n\n"
                        + "用户：为dayu的order-service 创建限流 qps=100\n"
                        + "思考：\n- 识别为创建限流\n- 抽取 app=dayu service=order-service qps=100\n- 参数完整，可执行\n")
                .goal("帮助用户以自然语言完成：服务检索、应用/服务的限流规则查询/创建/更新/删除，并在信息不足时进行精准追问")
                .constraints("仅处理与 Dayu 服务查询与限流相关的问题；严格遵循‘先思考、再工具’与‘缺参先问’；所有输出尽量简短、中文、逐条流式。")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                ))
                // 添加服务查询和限流功能的路由器和功能
                .mcpTools(Lists.newArrayList(
                        // 1) 先接入通用 ChatFunction，允许 LLM 产生思考/决策
                        new ChatFunction("dayu-service-query", 256),
                        // 2) 限流思考优先：优先捕获“限流/流控/熔断”等自然语言并返回思考过程
                        new ThinkingLimitFlowRouterFunction(dayuServiceLimitFlowFunction),
                        // 3) 服务查询的思考路由器
                        new RealThinkingRouterFunction(dayuServiceQueryFunction),
                        // 4) 兜底的聊天路由器（并内置对限流语义的优先转交）
                        new DayuChatRouterFunction(dayuServiceQueryFunction, new ThinkingLimitFlowRouterFunction(dayuServiceLimitFlowFunction)),
                        // 5) 实际功能
                        dayuServiceQueryFunction,
                        new DayuServiceLimitFlowRouterFunction(dayuServiceLimitFlowFunction),
                        dayuServiceLimitFlowFunction
                ))
                .build();
    }

    @Bean
    public DayuServiceQueryFunction dayuServiceQueryFunction(
            @Value("${dayu.base-url:}") String baseUrl,
            @Value("${dayu.auth-token:}") String token,
            @Value("${dayu.cookie:}") String dayuCookie,
            @Value("${hive.manager.cookie:}") String hiveCookie) {
        String cookie = (dayuCookie != null && !dayuCookie.isBlank()) ? dayuCookie : hiveCookie;
        return new DayuServiceQueryFunction(baseUrl, token, cookie);
    }

    @Bean
    public DayuServiceLimitFlowFunction dayuServiceLimitFlowFunction(
            @Value("${dayu.base-url:}") String baseUrl,
            @Value("${dayu.auth-token:}") String token,
            @Value("${dayu.cookie:}") String dayuCookie,
            @Value("${hive.manager.cookie:}") String hiveCookie) {
        String cookie = (dayuCookie != null && !dayuCookie.isBlank()) ? dayuCookie : hiveCookie;
        return new DayuServiceLimitFlowFunction(baseUrl, token, cookie);
    }
}


