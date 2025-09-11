package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/1/11
 * CLI命令执行工具，支持安全检查和任务进度跟踪
 */
@Slf4j
public class ExecuteCommandTool implements ITool {

    public static final String name = "execute_command";

    @Override
    public boolean completed() {
        return true;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String description() {
        return """
                Request to execute a CLI command on the system. Use this when you need to perform system operations or run specific commands to accomplish any step in the user's task. 
                You must tailor your command to the user's system and provide a clear explanation of what the command does. For command chaining, use the appropriate chaining syntax for the user's shell. 
                Prefer to execute complex CLI commands over creating executable scripts, as they are more flexible and easier to run. Commands will be executed in the current working directory.
                """;
    }

    @Override
    public String parameters() {
        return """
                - command: (required) The CLI command to execute. This should be valid for the current operating system. Ensure the command is properly formatted and does not contain any harmful instructions.
                - requires_approval: (required) A boolean indicating whether this command requires explicit user approval before execution in case the user has auto-approve mode enabled. Set to 'true' for potentially impactful operations like installing/uninstalling packages, deleting/overwriting files, system configuration changes, network operations, or any commands that could have unintended side effects. Set to 'false' for safe operations like reading files/directories, running development servers, building projects, and other non-destructive operations.
                - timeout: (optional) Maximum time in seconds to wait for the command to complete. Default is 60 seconds.
                - task_progress: (optional) A checklist showing task progress after this tool use is completed.
                """;
    }

    @Override
    public String usage() {
        return """
                <execute_command>
                <command>ls -la</command>
                <requires_approval>false</requires_approval>
                <timeout>30</timeout>
                <task_progress>
                - [x] List directory contents
                - [ ] Process files
                </task_progress>
                </execute_command>
                """;
    }

    @Override
    public String example() {
        return """
                Example 1: Safe operation - listing files (no approval required)
                <execute_command>
                <command>ls -la</command>
                <requires_approval>false</requires_approval>
                </execute_command>
                
                Example 2: Potentially impactful operation - installing package (requires approval)
                <execute_command>
                <command>npm install express</command>
                <requires_approval>true</requires_approval>
                <task_progress>
                - [x] Navigate to project directory
                - [x] Install express package
                - [ ] Configure express server
                </task_progress>
                </execute_command>
                
                Example 3: Building project (safe operation)
                <execute_command>
                <command>mvn clean compile</command>
                <requires_approval>false</requires_approval>
                <timeout>120</timeout>
                </execute_command>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 检查必要参数
            if (!inputJson.has("command") || StringUtils.isBlank(inputJson.get("command").getAsString())) {
                log.error("执行命令请求缺少必需的command参数");
                result.addProperty("error", "缺少必需参数'command'");
                return result;
            }

            if (!inputJson.has("requires_approval")) {
                log.error("执行命令请求缺少必需的requires_approval参数");
                result.addProperty("error", "缺少必需参数'requires_approval'");
                return result;
            }

            String command = inputJson.get("command").getAsString();
            boolean requiresApproval = inputJson.get("requires_approval").getAsBoolean();
            
            int timeout = inputJson.has("timeout") ?
                    inputJson.get("timeout").getAsInt() : 60; // 默认超时60秒

            // 处理任务进度（可选）
            String taskProgress = inputJson.has("task_progress") ? 
                    inputJson.get("task_progress").getAsString() : null;

            // 安全检查：如果需要批准且是危险操作，记录警告
            if (requiresApproval) {
                log.warn("执行需要批准的命令: {}", command);
                if (isDangerousCommand(command)) {
                    log.warn("检测到潜在危险命令，建议谨慎执行: {}", command);
                }
            }

            // 执行命令
            JsonObject commandResult = executeCommand(command, timeout);
            
            // 添加额外信息到结果中
            commandResult.addProperty("requires_approval", requiresApproval);
            
            if (taskProgress != null) {
                commandResult.addProperty("task_progress", taskProgress);
            }

            return commandResult;

        } catch (Exception e) {
            log.error("执行命令时发生异常", e);
            result.addProperty("error", "执行命令失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 检查是否为危险命令
     * 
     * @param command 要检查的命令
     * @return 如果是危险命令返回true
     */
    private boolean isDangerousCommand(String command) {
        String lowerCommand = command.toLowerCase().trim();
        
        // 危险命令模式列表
        String[] dangerousPatterns = {
            "rm -rf", "del /f", "format", "fdisk", 
            "dd if=", "mkfs", "shutdown", "reboot",
            "sudo rm", "sudo del", "chmod 777",
            "curl.*|.*sh", "wget.*|.*sh", // 下载并执行脚本
            ">/dev/", "2>/dev/null", // 重定向到设备
        };
        
        for (String pattern : dangerousPatterns) {
            if (lowerCommand.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 执行系统命令并获取输出
     *
     * @param command 要执行的命令
     * @param timeout 超时时间（秒）
     * @return 包含执行结果的JsonObject
     */
    private JsonObject executeCommand(String command, int timeout) {
        JsonObject result = new JsonObject();
        Process process = null;

        try {
            String currentDir = System.getProperty("user.dir");
            log.info("执行命令: {}, 当前目录: {}, 超时: {}秒", command, currentDir, timeout);

            ProcessBuilder processBuilder = new ProcessBuilder();

            // 根据操作系统设置命令
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                processBuilder.command("sh", "-c", command);
            }

            // 继承系统环境变量
            Map<String, String> processEnv = processBuilder.environment();
            
            // 确保CWD环境变量已设置（当前工作目录）
            processEnv.put("CWD", System.getProperty("user.dir"));

            // 合并标准输出和错误输出
            processBuilder.redirectErrorStream(true);

            // 启动进程
            process = processBuilder.start();

            // 读取输出
            List<String> outputLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                }
            }

            // 等待进程完成，设置超时
            boolean completed = process.waitFor(timeout, TimeUnit.SECONDS);

            if (!completed) {
                // 超时，强制终止进程
                process.destroyForcibly();
                log.warn("命令执行超时 ({}秒): {}", timeout, command);
                result.addProperty("error", "命令执行超时 (" + timeout + "秒)");
                return result;
            }

            // 获取退出代码
            int exitCode = process.exitValue();

            // 构建结果
            StringBuilder output = new StringBuilder();
            for (String line : outputLines) {
                output.append(line).append("\n");
            }

            result.addProperty("exit_code", exitCode);
            result.addProperty("output", output.toString());
            result.addProperty("command", command);

            if (exitCode == 0) {
                log.info("命令执行成功，退出代码: {}", exitCode);
                result.addProperty("success", true);
            } else {
                log.warn("命令执行完成，但退出代码非零: {}", exitCode);
                result.addProperty("success", false);
            }

        } catch (IOException e) {
            log.error("执行命令IO异常", e);
            result.addProperty("error", "执行命令IO异常: " + e.getMessage());
            result.addProperty("success", false);
        } catch (InterruptedException e) {
            log.error("命令执行被中断", e);
            result.addProperty("error", "命令执行被中断: " + e.getMessage());
            result.addProperty("success", false);
            Thread.currentThread().interrupt();
        } finally {
            // 确保进程被销毁
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }

        return result;
    }
}
