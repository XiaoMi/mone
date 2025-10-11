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
                .profile("你是Dayu微服务治理专家，擅长服务发现和限流管理")
                .goal("帮助用户通过自然语言完成服务查询、限流规则管理，提供精准的微服务治理支持")
                .workflow("""
                        服务治理流程:
                        <1>理解用户意图 -> 分析服务/限流需求
                        <2>参数提取 -> 识别app、service、method等关键信息  
                        <3>智能推理 -> 根据上下文补全缺失参数
                        <4>执行操作 -> 调用相应工具完成服务查询或限流管理
                        """)
                .constraints("专注Dayu服务治理领域；参数不足时主动追问；输出简洁明了；支持自然语言交互")
                .tools(Lists.newArrayList(
                        new ChatTool(),
                        new AskTool(),
                        new AttemptCompletionTool()
                ))
                // 优化工具配置：参考优秀MCP项目的最佳实践
                .mcpTools(Lists.newArrayList(
                        // 1) 先接入通用 ChatFunction，允许 LLM 产生思考/决策
                        new ChatFunction("dayu-service-query", 256),
                        // 2) 限流思考优先：优先捕获“限流/流控/熔断”等自然语言并返回思考过程
                        new ThinkingLimitFlowRouterFunction(dayuServiceLimitFlowFunction),
                        // 3) 通用思考路由器 - 处理服务查询思考
                        new RealThinkingRouterFunction(dayuServiceQueryFunction),
                        // 4) 兜底聊天路由器 - 处理其他对话
                        new DayuChatRouterFunction(dayuServiceQueryFunction, new ThinkingLimitFlowRouterFunction(dayuServiceLimitFlowFunction)),
                        // 5) 核心功能工具
                        dayuServiceQueryFunction,
                        new DayuServiceLimitFlowRouterFunction(dayuServiceLimitFlowFunction),
                        dayuServiceLimitFlowFunction
                ))
                .build();
    }

    @Bean
    public DayuServiceQueryFunction dayuServiceQueryFunction(
            @Value("${dayu.service.base-url:}") String baseUrl,
            @Value("${dayu.auth-token:}") String token) {
        return new DayuServiceQueryFunction(baseUrl, token);
    }

    @Bean
    public DayuServiceLimitFlowFunction dayuServiceLimitFlowFunction(
            @Value("${dayu.limit-flow.base-url:}") String baseUrl,
            @Value("${dayu.auth-token:}") String token) {
        return new DayuServiceLimitFlowFunction(baseUrl, token);
    }
}


