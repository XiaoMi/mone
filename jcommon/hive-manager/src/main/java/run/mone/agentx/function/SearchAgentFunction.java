package run.mone.agentx.function;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.service.AgentService;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/10/10
 * 搜索最合适Agent的MCP Function
 */
@RequiredArgsConstructor
@Data
@Slf4j
public class SearchAgentFunction implements McpFunction {

    private final AgentService agentService;

    private RoleService roleService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "task": {
                        "type": "string",
                        "description": "The task description for which you need to find a suitable agent."
                    }
                },
                "required": ["task"]
            }
            """;

    @Override
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("search_agent arguments:{}", arguments);
        
        try {
            String task = (String) arguments.get("task");
            
            if (task == null || task.trim().isEmpty()) {
                String errorMessage = "任务描述不能为空";
                return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(errorMessage)), 
                    true
                ));
            }

            log.info("开始搜索适合任务的Agent: {}", task);

            // 调用AgentService的findMostSuitableAgent方法
            return Flux.from(agentService.findMostSuitableAgent(task))
                    .map(selectedAgent -> {
                        if (selectedAgent == null) {
                            String errorMessage = "没有找到可用的Agent";
                            log.warn("未找到适合任务的Agent: {}", task);
                            return new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(errorMessage)), 
                                true
                            );
                        }

                        // 构建结果信息
                        StringBuilder infoBuilder = new StringBuilder();
                        infoBuilder.append("找到最合适的Agent:\n");
                        infoBuilder.append("名称: ").append(selectedAgent.getAgent().getName()).append("\n");
                        infoBuilder.append("组: ").append(selectedAgent.getAgent().getGroup()).append("\n");
                        infoBuilder.append("版本: ").append(selectedAgent.getAgent().getVersion()).append("\n");
                        infoBuilder.append("描述: ").append(selectedAgent.getAgent().getDescription()).append("\n");

                        if (selectedAgent.getAgent().getProfile() != null) {
                            infoBuilder.append("角色: ").append(selectedAgent.getAgent().getProfile()).append("\n");
                        }
                        if (selectedAgent.getAgent().getGoal() != null) {
                            infoBuilder.append("目标: ").append(selectedAgent.getAgent().getGoal()).append("\n");
                        }
                        if (selectedAgent.getAgent().getConstraints() != null) {
                            infoBuilder.append("约束: ").append(selectedAgent.getAgent().getConstraints()).append("\n");
                        }

                        // 添加实例信息
                        if (selectedAgent.getInstances() != null && !selectedAgent.getInstances().isEmpty()) {
                            infoBuilder.append("活跃实例数: ").append(selectedAgent.getInstances().size()).append("\n");
                        }

                        log.info("成功找到适合任务的Agent: {}", selectedAgent.getAgent().getName());
                        
                        return new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(infoBuilder.toString())), 
                            false
                        );
                    })
                    .onErrorResume(e -> {
                        log.error("搜索Agent时出错", e);
                        String errorMessage = "搜索Agent时出错: " + e.getMessage();
                        return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(errorMessage)), 
                            true
                        ));
                    });

        } catch (Exception e) {
            log.error("处理搜索Agent请求时出错", e);
            String errorMessage = "处理请求时出错: " + e.getMessage();
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(errorMessage)), 
                true
            ));
        }
    }

    @Override
    public String getName() {
        return "search_agent";
    }

    @Override
    public String getDesc() {
        return "搜索最合适的Agent。根据任务描述，从所有可用的Agent中找出最匹配的一个。支持各种形式的任务描述。";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
