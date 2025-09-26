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
import java.util.concurrent.CopyOnWriteArrayList;
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
    
    // 进程管理器实例
    private static final ProcessManager processManager = ProcessManager.getInstance();

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
                - timeout: (optional) Maximum time in seconds to wait for the command to complete. Default is 60 seconds. For background processes, this is the time to wait for startup confirmation.
                - background: (optional) A boolean indicating whether to run the command in background mode for long-running processes like servers. When true, the command starts in background and returns immediately with process info. Default is false. (mvn spring-boot:run  这个就是需要长时间运行的)
                - task_progress: (optional) A checklist showing task progress after this tool use is completed.
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
            <task_progress>
            Checklist here (optional)
            </task_progress>
            """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
            <execute_command>
            <command>ls -la</command>
            <requires_approval>false</requires_approval>
            <timeout>30</timeout>
            <background>false</background>
            %s
            </execute_command>
            """.formatted(taskProgress);
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
                
                Example 4: Long-running background process (Spring Boot server)
                <execute_command>
                <command>mvn spring-boot:run</command>
                <requires_approval>false</requires_approval>
                <timeout>60</timeout>
                <background>true</background>
                <task_progress>
                - [x] Start Spring Boot application in background
                - [ ] Verify application is running
                </task_progress>
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
            boolean background = inputJson.has("background") ? 
                    inputJson.get("background").getAsBoolean() : false;
            
            int timeout = inputJson.has("timeout") ?
                    inputJson.get("timeout").getAsInt() : 10; // 默认超时60秒

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
            
            // 根据是否后台执行选择不同的执行方式
            JsonObject commandResult;
            if (background) {
                commandResult = executeCommandInBackground(command, timeout, workingDirectory);
            } else {
                commandResult = executeCommand(command, timeout, workingDirectory);
            }
            
            // 添加额外信息到结果中
            commandResult.addProperty("requires_approval", requiresApproval);
            commandResult.addProperty("background", background);
            
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
     * 在后台执行命令，适用于长期运行的进程
     *
     * @param command 要执行的命令
     * @param timeout 启动确认超时时间（秒）
     * @param workingDirectory 工作目录路径
     * @return 包含进程信息的JsonObject
     */
    private JsonObject executeCommandInBackground(String command, int timeout, String workingDirectory) {
        JsonObject result = new JsonObject();
        Process process = null;

        try {
            log.info("后台执行命令: {}, 工作目录: {}, 启动超时: {}秒", command, workingDirectory, timeout);

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
                processBuilder.command("zsh", "-l", "-c", command);
            }

            // 获取并设置完整的 zsh 环境变量
            Map<String, String> processEnv = processBuilder.environment();
            processEnv.clear();
            Map<String, String> zshEnv = getZshEnvironment();
            processEnv.putAll(zshEnv);
            
            processEnv.put("CWD", workingDirectory);
            processEnv.put("PWD", workingDirectory);
            
            if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                processEnv.put("SHELL", "/bin/zsh");
            }

            log.debug("使用 {} 个环境变量执行后台命令", processEnv.size());

            // 不合并错误输出，分别处理
            processBuilder.redirectErrorStream(false);

            // 启动进程
            process = processBuilder.start();
            final Process finalProcess = process; // 创建 final 引用供 lambda 使用
            
            // 注册到进程管理器
            String processId = processManager.registerProcess(process, command, workingDirectory, true);
            
            // 读取启动阶段的输出（限时读取）
            final List<String> startupOutput = new CopyOnWriteArrayList<>();
            final List<String> startupErrors = new CopyOnWriteArrayList<>();
            
            final long startTime = System.currentTimeMillis();
            final long timeoutMillis = timeout * 1000L;
            
            // 使用非阻塞方式读取启动输出
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(finalProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null && 
                           (System.currentTimeMillis() - startTime) < timeoutMillis) {
                        startupOutput.add(line);
                        // 检测到启动成功的关键词就停止读取
                        if (isStartupSuccessIndicator(line)) {
                            log.info("检测到启动成功标识: {}", line);
                            break;
                        }
                    }
                } catch (IOException e) {
                    log.debug("读取后台进程输出时出错: {}", e.getMessage());
                }
            });
            
            Thread errorReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(finalProcess.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null && 
                           (System.currentTimeMillis() - startTime) < timeoutMillis) {
                        startupErrors.add(line);
                    }
                } catch (IOException e) {
                    log.debug("读取后台进程错误输出时出错: {}", e.getMessage());
                }
            });

            outputReader.start();
            errorReader.start();
            
            // 等待读取线程完成或超时
            outputReader.join(timeoutMillis);
            errorReader.join(timeoutMillis);

            // 检查进程状态
            boolean isRunning = process.isAlive();
            
            if (!isRunning) {
                // 进程已经退出，可能启动失败
                int exitCode = process.exitValue();
                // 进程管理器会自动更新状态，这里不需要手动移除
                
                result.addProperty("success", false);
                result.addProperty("error", "进程启动后立即退出，退出代码: " + exitCode);
                result.addProperty("exit_code", exitCode);
                
                // 包含启动输出和错误信息
                if (!startupOutput.isEmpty()) {
                    result.addProperty("startup_output", String.join("\n", startupOutput));
                }
                if (!startupErrors.isEmpty()) {
                    result.addProperty("startup_errors", String.join("\n", startupErrors));
                }
                
                log.warn("后台进程启动失败，立即退出: {}", command);
            } else {
                // 进程正在运行
                result.addProperty("success", true);
                result.addProperty("process_id", processId);
                result.addProperty("pid", process.pid());
                result.addProperty("running", true);
                result.addProperty("message", "命令已在后台启动");
                
                // 包含启动阶段的输出
                if (!startupOutput.isEmpty()) {
                    result.addProperty("startup_output", String.join("\n", startupOutput));
                }
                
                log.info("后台进程启动成功: {} (进程ID: {}, PID: {})", command, processId, process.pid());
            }
            
            result.addProperty("command", command);
            result.addProperty("working_directory", workingDirectory);

        } catch (IOException e) {
            log.error("后台执行命令IO异常", e);
            result.addProperty("error", "后台执行命令IO异常: " + e.getMessage());
            result.addProperty("success", false);
        } catch (InterruptedException e) {
            log.error("后台命令执行被中断", e);
            result.addProperty("error", "后台命令执行被中断: " + e.getMessage());
            result.addProperty("success", false);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("后台执行命令时发生异常", e);
            result.addProperty("error", "后台执行命令失败: " + e.getMessage());
            result.addProperty("success", false);
        }

        return result;
    }

    /**
     * 检查输出行是否包含启动成功的标识
     */
    private boolean isStartupSuccessIndicator(String line) {
        if (StringUtils.isBlank(line)) {
            return false;
        }
        
        String lowerLine = line.toLowerCase();
        return lowerLine.contains("started") && (
                lowerLine.contains("application") ||
                lowerLine.contains("server") ||
                lowerLine.contains("tomcat") ||
                lowerLine.contains("spring") ||
                lowerLine.contains("boot")
        ) || lowerLine.contains("listening on") ||
           lowerLine.contains("server started") ||
           lowerLine.contains("ready to accept connections");
    }

    /**
     * 停止后台进程
     * 
     * @param processId 进程ID
     * @return 停止是否成功
     */
    public static boolean stopBackgroundProcess(String processId) {
        return processManager.stopProcess(processId);
    }

    /**
     * 获取所有后台进程的状态
     * 
     * @return 进程状态信息的JsonObject
     */
    public static JsonObject getBackgroundProcessesStatus() {
        return processManager.getBackgroundProcessesStatus();
    }

    /**
     * 停止所有后台进程
     * 
     * @return 停止的进程数量
     */
    public static int stopAllBackgroundProcesses() {
        return processManager.stopAllBackgroundProcesses();
    }

    /**
     * 检查指定进程是否还在运行
     * 
     * @param processId 进程ID
     * @return 是否在运行
     */
    public static boolean isBackgroundProcessRunning(String processId) {
        return processManager.isProcessRunning(processId);
    }
    
    // ============ 新增的便捷进程管理方法 ============
    
    /**
     * 获取进程管理器实例
     * 
     * @return ProcessManager实例
     */
    public static ProcessManager getProcessManager() {
        return processManager;
    }
    
    /**
     * 获取所有进程的状态
     * 
     * @return 包含所有进程状态的JsonObject
     */
    public static JsonObject getAllProcessesStatus() {
        return processManager.getAllProcessesStatus();
    }
    
    /**
     * 强制终止指定进程
     * 
     * @param processId 进程ID
     * @return 是否成功终止
     */
    public static boolean killProcess(String processId) {
        return processManager.killProcess(processId);
    }
    
    /**
     * 停止所有进程（包括前台和后台）
     * 
     * @return 停止的进程数量
     */
    public static int stopAllProcesses() {
        return processManager.stopAllProcesses();
    }
    
    /**
     * 强制终止所有进程
     * 
     * @return 终止的进程数量
     */
    public static int killAllProcesses() {
        return processManager.stopAllProcesses(true);
    }
    
    /**
     * 根据命令查找进程
     * 
     * @param command 命令字符串（支持部分匹配）
     * @return 匹配的进程ID列表
     */
    public static List<String> findProcessesByCommand(String command) {
        return processManager.findProcessesByCommand(command);
    }
    
    /**
     * 获取运行中的进程数量
     * 
     * @return 运行中的进程数量
     */
    public static int getRunningProcessCount() {
        return processManager.getRunningProcessCount();
    }
    
    /**
     * 获取后台进程数量
     * 
     * @return 后台进程数量
     */
    public static int getBackgroundProcessCount() {
        return processManager.getBackgroundProcessCount();
    }
    
    /**
     * 清理已停止的进程记录
     * 
     * @return 清理的进程数量
     */
    public static int cleanupStoppedProcesses() {
        return processManager.cleanupStoppedProcesses();
    }
    
    /**
     * 自动清理已完成的进程记录
     * 
     * @return 清理的进程数量
     */
    public static int autoCleanupCompletedProcesses() {
        return processManager.autoCleanupCompletedProcesses();
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
        String processId = null; // 声明在方法级别，确保异常处理时能访问到

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
            
            // 注册到进程管理器（前台进程）
            processId = processManager.registerProcess(process, command, workingDirectory, false);

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
                result.addProperty("timeout", true);
                
                // 进程超时被终止，更新进程管理器中的状态
                if (processId != null) {
                    ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                    if (processInfo != null) {
                        processInfo.setStatus("timeout");
                    }
                }
                
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
            
            // 更新进程管理器中的状态
            if (processId != null) {
                ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                if (processInfo != null) {
                    processInfo.setStatus(exitCode == 0 ? "completed" : "failed");
                }
            }

        } catch (IOException e) {
            log.error("执行命令IO异常", e);
            result.addProperty("error", "执行命令IO异常: " + e.getMessage());
            result.addProperty("success", false);
            
            // 更新进程状态为错误
            if (processId != null) {
                ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                if (processInfo != null) {
                    processInfo.setStatus("error");
                }
            }
        } catch (InterruptedException e) {
            log.error("命令执行被中断", e);
            result.addProperty("error", "命令执行被中断: " + e.getMessage());
            result.addProperty("success", false);
            Thread.currentThread().interrupt();
            
            // 更新进程状态为中断
            if (processId != null) {
                ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                if (processInfo != null) {
                    processInfo.setStatus("interrupted");
                }
            }
        } finally {
            // 确保进程被销毁
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }

        return result;
    }
}

/*
 * 后台执行功能使用说明：
 * 
 * 1. 启动后台进程：
 *    <execute_command>
 *    <command>mvn spring-boot:run</command>
 *    <requires_approval>false</requires_approval>
 *    <timeout>60</timeout>
 *    <background>true</background>
 *    </execute_command>
 *    
 *    返回结果包含：
 *    - process_id: 后台进程的唯一标识符
 *    - pid: 系统进程ID
 *    - startup_output: 启动阶段的输出日志
 * 
 * 2. 管理后台进程：
 *    - ExecuteCommandTool.stopBackgroundProcess(processId): 停止指定进程
 *    - ExecuteCommandTool.getBackgroundProcessesStatus(): 获取所有后台进程状态
 *    - ExecuteCommandTool.stopAllBackgroundProcesses(): 停止所有后台进程
 *    - ExecuteCommandTool.isBackgroundProcessRunning(processId): 检查进程是否运行
 * 
 * 3. 适用场景：
 *    - Spring Boot 应用: mvn spring-boot:run
 *    - Node.js 应用: npm start, npm run dev
 *    - Python 服务: python app.py
 *    - 开发服务器: webpack-dev-server, vite dev
 *    - 数据库服务: mysqld, redis-server
 * 
 * 4. 启动成功检测：
 *    系统会自动检测以下关键词来判断启动成功：
 *    - "started application/server/tomcat/spring/boot"
 *    - "listening on"
 *    - "server started"
 *    - "ready to accept connections"
 */
