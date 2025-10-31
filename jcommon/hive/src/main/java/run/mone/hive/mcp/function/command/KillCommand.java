package run.mone.hive.mcp.function.command;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.tool.ProcessManager;

import java.util.List;

/**
 * 杀死进程命令处理类
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class KillCommand extends BaseCommand {

    public KillCommand(RoleService roleService) {
        super(roleService);
    }

    @Override
    public boolean matches(String message) {
        return message.trim().toLowerCase().startsWith("/kill");
    }

    @Override
    public Flux<McpSchema.CallToolResult> execute(String clientId, String userId, String agentId, String ownerId, String message, long timeout) {
        try {
            String[] parts = message.split("\\s+");
            ProcessManager processManager = ProcessManager.getInstance();
            
            // 如果只有 /kill，默认杀死所有进程（兼容旧格式）
            if (parts.length == 1) {
                int killedCount = processManager.killAllProcesses();
                return Flux.just(createSuccessResult("已杀死 " + killedCount + " 个进程"));
            }
            
            String action = parts[1].toLowerCase();
            
            switch (action) {
                case "list":
                    // 列出所有进程
                    return Flux.just(createSuccessResult(processManager.getAllProcessesStatus().toString()));
                    
                case "all":
                    // 杀死所有进程
                    int killedCount = processManager.killAllProcesses();
                    return Flux.just(createSuccessResult("已杀死 " + killedCount + " 个进程"));
                    
                default:
                    // 杀死指定进程
                    String processId = parts[1];
                    
                    // 先获取进程信息用于显示
                    ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                    if (processInfo == null) {
                        return Flux.just(createErrorResult("杀死进程失败：未找到进程 " + processId));
                    }
                    
                    boolean success = processManager.killProcess(processId);
                    
                    if (success) {
                        String processDetails = String.format("进程 %s (PID: %d, 命令: %s) 已被杀死", 
                                processId, processInfo.getPid(), processInfo.getCommand());
                        return Flux.just(createSuccessResult(processDetails));
                    } else {
                        return Flux.just(createErrorResult("杀死进程失败：进程 " + processId + " 可能已经停止或无法终止"));
                    }
            }
        } catch (Exception e) {
            log.error("杀死进程操作失败: {}", e.getMessage(), e);
            return Flux.just(createErrorResult("杀死进程操作失败: " + e.getMessage()));
        }
    }

    @Override
    public String getCommandName() {
        return "/kill";
    }

    @Override
    public String getCommandDescription() {
        return "杀死进程";
    }
}
