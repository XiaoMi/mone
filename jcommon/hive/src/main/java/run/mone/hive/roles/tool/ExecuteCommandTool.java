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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/1/11
 * CLI命令执行工具，支持安全检查和任务进度跟踪
 * 
 * 主要功能：
 * - 支持自定义工作目录执行命令
 * - 自动获取并嵌入完整的 zsh 环境变量（包括 .zshrc 配置）
 * - 环境变量缓存机制，提高性能（5分钟缓存超时）
 * - 安全检查，识别潜在危险命令
 * - 跨平台支持（Windows 使用 cmd，Unix/Linux/macOS 使用 zsh）
 * - 命令执行超时控制
 */
@Slf4j
public class ExecuteCommandTool implements ITool {

    public static final String name = "execute_command";
    
    // 缓存 zsh 环境变量，避免重复获取
    private static volatile Map<String, String> cachedZshEnv = null;
    private static volatile long lastEnvCacheTime = 0;
    private static final long ENV_CACHE_TIMEOUT = 300_000; // 5分钟缓存超时

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
                Prefer to execute complex CLI commands over creating executable scripts, as they are more flexible and easier to run. Commands will be executed in the configured working directory (defaults to current workspace path).
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

            // 获取工作目录（从ReactorRole获取，如果没有则使用默认目录）
            String workingDirectory = getWorkingDirectory(role);
            
            // 执行命令
            JsonObject commandResult = executeCommand(command, timeout, workingDirectory);
            
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
     * 获取工作目录
     * 
     * @param role ReactorRole实例，用于获取配置的工作区路径
     * @return 工作目录路径
     */
    private String getWorkingDirectory(ReactorRole role) {
        try {
            // 尝试从ReactorRole获取工作区路径
            if (role != null) {
                String workspacePath = role.getWorkspacePath();
                if (StringUtils.isNotEmpty(workspacePath)) {
                    File workspaceDir = new File(workspacePath);
                    if (workspaceDir.exists() && workspaceDir.isDirectory()) {
                        log.debug("使用ReactorRole配置的工作目录: {}", workspacePath);
                        return workspacePath;
                    } else {
                        log.warn("ReactorRole配置的工作目录不存在或不是目录: {}", workspacePath);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取ReactorRole工作目录时出错，使用默认目录: {}", e.getMessage());
        }
        
        // 回退到默认的当前工作目录
        String defaultDir = System.getProperty("user.dir");
        log.debug("使用默认工作目录: {}", defaultDir);
        return defaultDir;
    }

    /**
     * 获取 zsh 的完整环境变量（带缓存）
     * 通过执行 'zsh -c env' 命令获取 zsh 的所有环境变量
     * 
     * @return 包含所有 zsh 环境变量的 Map
     */
    private Map<String, String> getZshEnvironment() {
        long currentTime = System.currentTimeMillis();
        
        // 检查缓存是否有效
        if (cachedZshEnv != null && (currentTime - lastEnvCacheTime) < ENV_CACHE_TIMEOUT) {
            log.debug("使用缓存的 zsh 环境变量，包含 {} 个变量", cachedZshEnv.size());
            return new HashMap<>(cachedZshEnv);
        }
        
        Map<String, String> zshEnv = new HashMap<>();
        Process process = null;
        
        try {
            log.debug("开始获取 zsh 环境变量（缓存已过期或不存在）");
            
            // 检查系统是否支持 zsh
            ProcessBuilder testZsh = new ProcessBuilder("zsh", "--version");
            testZsh.redirectErrorStream(true);
            Process testProcess = testZsh.start();
            boolean zshAvailable = testProcess.waitFor(5, TimeUnit.SECONDS) && testProcess.exitValue() == 0;
            
            if (!zshAvailable) {
                log.warn("zsh 不可用，将使用系统默认环境变量");
                return System.getenv();
            }
            
            // 使用 zsh 执行 env 命令获取完整环境变量
            // 同时加载用户的 .zshrc 配置
            ProcessBuilder processBuilder = new ProcessBuilder("zsh", "-l", "-c", "env");
            processBuilder.redirectErrorStream(false);
            
            process = processBuilder.start();
            
            // 读取标准输出（环境变量）
            List<String> envLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    envLines.add(line);
                }
            }
            
            // 读取错误输出（如果有的话）
            List<String> errorLines = new ArrayList<>();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorLines.add(line);
                }
            }
            
            // 等待进程完成
            boolean completed = process.waitFor(10, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                log.warn("获取 zsh 环境变量超时，使用系统默认环境变量");
                return System.getenv();
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.warn("获取 zsh 环境变量失败，退出代码: {}，错误信息: {}", exitCode, String.join("\n", errorLines));
                return System.getenv();
            }
            
            // 解析环境变量
            for (String line : envLines) {
                if (StringUtils.isNotBlank(line) && line.contains("=")) {
                    int equalIndex = line.indexOf("=");
                    String key = line.substring(0, equalIndex);
                    String value = line.substring(equalIndex + 1);
                    
                    // 过滤掉一些可能有问题的环境变量
                    if (StringUtils.isNotBlank(key) && !key.contains(" ") && !key.contains("\t")) {
                        zshEnv.put(key, value);
                    }
                }
            }
            
            log.info("成功获取到 {} 个 zsh 环境变量", zshEnv.size());
            
            // 确保一些重要的环境变量存在
            if (!zshEnv.containsKey("SHELL")) {
                zshEnv.put("SHELL", "/bin/zsh");
            }
            if (!zshEnv.containsKey("HOME")) {
                zshEnv.put("HOME", System.getProperty("user.home"));
            }
            if (!zshEnv.containsKey("USER")) {
                zshEnv.put("USER", System.getProperty("user.name"));
            }
            
            // 更新缓存
            cachedZshEnv = new HashMap<>(zshEnv);
            lastEnvCacheTime = currentTime;
            log.debug("已更新 zsh 环境变量缓存");
            
            return zshEnv;
            
        } catch (Exception e) {
            log.error("获取 zsh 环境变量时发生异常", e);
            // 发生异常时回退到系统环境变量
            return System.getenv();
        } finally {
            // 确保进程被销毁
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    /**
     * 清除 zsh 环境变量缓存
     * 这将强制下次获取时重新从 zsh 读取环境变量
     */
    public static void clearZshEnvironmentCache() {
        cachedZshEnv = null;
        lastEnvCacheTime = 0;
        log.debug("已清除 zsh 环境变量缓存");
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
     * @param workingDirectory 工作目录路径
     * @return 包含执行结果的JsonObject
     */
    private JsonObject executeCommand(String command, int timeout, String workingDirectory) {
        JsonObject result = new JsonObject();
        Process process = null;

        try {
            log.info("执行命令: {}, 工作目录: {}, 超时: {}秒", command, workingDirectory, timeout);

            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // 设置工作目录
            File workDir = new File(workingDirectory);
            if (workDir.exists() && workDir.isDirectory()) {
                processBuilder.directory(workDir);
                log.debug("设置ProcessBuilder工作目录为: {}", workingDirectory);
            } else {
                log.warn("指定的工作目录不存在或不是目录: {}, 使用默认目录", workingDirectory);
                workingDirectory = System.getProperty("user.dir");
            }

            // 根据操作系统设置命令
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
                // 在非 Windows 系统上优先使用 zsh
                processBuilder.command("zsh", "-l", "-c", command);
            }

            // 获取并设置完整的 zsh 环境变量
            Map<String, String> processEnv = processBuilder.environment();
            
            // 清空当前环境变量，使用 zsh 的完整环境
            processEnv.clear();
            
            // 获取 zsh 环境变量
            Map<String, String> zshEnv = getZshEnvironment();
            processEnv.putAll(zshEnv);
            
            // 设置或覆盖关键的工作目录相关环境变量
            processEnv.put("CWD", workingDirectory);
            processEnv.put("PWD", workingDirectory); // Unix系统常用的当前目录环境变量
            
            // 确保 SHELL 环境变量指向 zsh
            if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                processEnv.put("SHELL", "/bin/zsh");
            }
            
            log.debug("使用 {} 个环境变量执行命令", processEnv.size());

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
            result.addProperty("working_directory", workingDirectory);

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
