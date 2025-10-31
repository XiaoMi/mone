package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.ProcessManager;

/**
 * 分离进程命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class DetachCommand extends BaseCommand {

    public DetachCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return message.trim().toLowerCase().startsWith("/detach");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            String[] parts = message.split("\\s+");
            
            if (parts.length < 2) {
                String helpText = """
                        分离进程命令格式错误！
                        支持的格式：
                        - /detach <processId> - 分离指定进程
                        - /detach all - 分离所有进程  
                        - /detach list - 列出所有进程
                        """;
                return Flux.just(createErrorResult(helpText));
            }
            
            String action = parts[1].toLowerCase();
            ProcessManager processManager = ProcessManager.getInstance();
            
            switch (action) {
                case "list":
                    // 列出所有进程
                    return Flux.just(createSuccessResult(processManager.getAllProcessesStatus().toString()));
                    
                case "all":
                    // 分离所有进程
                    int detachedCount = processManager.detachAllProcesses();
                    return Flux.just(createSuccessResult("已分离 " + detachedCount + " 个进程到后台运行"));
                    
                default:
                    // 分离指定进程
                    String processId = parts[1];
                    boolean success = processManager.detachProcess(processId);
                    
                    if (success) {
                        ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                        String processDetails = processInfo != null ? 
                                String.format("进程 %s (PID: %d, 命令: %s) 已分离到后台运行", 
                                        processId, processInfo.getPid(), processInfo.getCommand()) :
                                String.format("进程 %s 已分离到后台运行", processId);
                        
                        return Flux.just(createSuccessResult(processDetails));
                    } else {
                        return Flux.just(createErrorResult("分离进程失败：未找到进程 " + processId));
                    }
            }
        } catch (Exception e) {
            log.error("分离进程操作失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("分离进程操作失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/detach";
    }

    @Override
    public String getCommandDescription() {
        return "分离进程";
    }
}
