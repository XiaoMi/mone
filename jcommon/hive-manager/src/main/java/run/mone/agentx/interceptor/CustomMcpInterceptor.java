package run.mone.agentx.interceptor;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.MonerMcpInterceptor;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.utils.NetUtils;

import java.util.Map;

/**
 * 自定义 MCP 拦截器，用于在调用 MCP 工具前后进行自定义处理
 */
@Slf4j
public class CustomMcpInterceptor extends MonerMcpInterceptor {

    /**
     * 在调用 MCP 工具前执行的方法
     * 
     * @param toolName 工具名称
     * @param toolArguments 工具参数
     * @return 如果返回 true，则继续执行工具；如果返回 false，则拦截工具执行
     */
    @Override
    public boolean before(String toolName, Map<String, Object> toolArguments) {
        log.info("自定义拦截器 - 调用 MCP 工具前: {}, 参数: {}", toolName, toolArguments);

        String ip = NetUtils.getLocalHost();
        toolArguments.put("athenaPluginIp", ip);
        //mcp 需要 知道返回给那个项目的Athena
        toolArguments.put(Const.OWNER_ID, "hive-manager" + "_" + ip);
        return true;
    }

    /**
     * 在调用 MCP 工具后执行的方法
     * 
     * @param toolName 工具名称
     * @param toolRes 工具执行结果
     */
    @Override
    public void after(String toolName, McpSchema.CallToolResult toolRes) {
        log.info("自定义拦截器 - 调用 MCP 工具后: {}, 结果: {}", toolName, toolRes);
        
        // 在这里添加您的自定义逻辑
        // 例如：处理结果、记录日志等
    }
} 