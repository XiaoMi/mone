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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Collections;

/**
 * @author goodjava@qq.com
 * 优化的CLI命令执行工具，支持智能缓冲机制
 * <p>
 * 主要功能：
 * - 智能输出缓冲：根据输出量和时间自动调整缓冲策略
 * - 快慢命令分别处理：快速命令直接显示结果，长时间命令使用缓冲优化
 * - 实时输出流：支持命令执行过程中的实时输出显示
 * - 调度机制：支持延迟刷新和立即刷新两种模式
 * - 安全检查：识别潜在危险命令
 * - 跨平台支持：Windows 使用 cmd，Unix/Linux/macOS 使用 zsh
 */
@Slf4j
public class ExecuteCommandToolOptimized implements ITool {

    public static final String name = "execute_command";

    // 缓存 zsh 环境变量，避免重复获取
    private static volatile Map<String, String> cachedZshEnv = null;
    private static volatile long lastEnvCacheTime = 0;
    private static final long ENV_CACHE_TIMEOUT = 300_000; // 5分钟缓存超时

    // 进程管理器实例
    private static final ProcessManager processManager = ProcessManager.getInstance();

    // 输出缓冲配置（参考 Cline 的设置）
    private static final int CHUNK_LINE_COUNT = 1;          // 20行触发刷新
    private static final int CHUNK_BYTE_SIZE = 2048;         // 2KB触发刷新
    private static final long CHUNK_DEBOUNCE_MS = 100;       // 100ms延迟刷新
    private static final long COMPLETION_TIMEOUT_MS = 6000;  // 6秒完成超时
    private static final long BUFFER_STUCK_TIMEOUT_MS = 6000; // 6秒缓冲超时

    // 线程池用于异步处理
    private static final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "ExecuteCommand-" + System.currentTimeMillis());
        t.setDaemon(true);
        return t;
    });

    // 调度器用于延迟任务
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "ExecuteCommand-Scheduler-" + System.currentTimeMillis());
        t.setDaemon(true);
        return t;
    });

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
                The system supports intelligent output buffering for optimal performance with both quick and long-running commands.
                """;
    }

    @Override
    public String parameters() {
        return """
                - command: (required) The CLI command to execute. This should be valid for the current operating system. Ensure the command is properly formatted and does not contain any harmful instructions.
                - requires_approval: (required) A boolean indicating whether this command requires explicit user approval before execution in case the user has auto-approve mode enabled. Set to 'true' for potentially impactful operations like installing/uninstalling packages, deleting/overwriting files, system configuration changes, network operations, or any commands that could have unintended side effects. Set to 'false' for safe operations like reading files/directories, running development servers, building projects, and other non-destructive operations.
                - timeout: (optional) Maximum time in seconds to wait for the command to complete. Default is 60 seconds.
                - interactive: (optional) A boolean indicating whether to enable buffered output mode. When true, uses intelligent buffering for better performance. Default is true.
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
                <interactive>true</interactive>
                %s
                </execute_command>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Quick operation - listing files (direct output)
                <execute_command>
                <command>ls -la</command>
                <requires_approval>false</requires_approval>
                <interactive>false</interactive>
                </execute_command>
                
                Example 2: Long-running operation - installing package (with buffering)
                <execute_command>
                <command>npm install express</command>
                <requires_approval>true</requires_approval>
                <interactive>true</interactive>
                <task_progress>
                - [x] Navigate to project directory
                - [x] Install express package
                - [ ] Configure express server
                </task_progress>
                </execute_command>
                
                Example 3: Build operation
                <execute_command>
                <command>mvn clean compile</command>
                <requires_approval>false</requires_approval>
                <interactive>true</interactive>
                </execute_command>
                """;
    }

    @Override
    public JsonObject execute(final ReactorRole role, JsonObject inputJson) {
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
            boolean interactive = inputJson.has("interactive") ?
                    inputJson.get("interactive").getAsBoolean() : true;

            int timeout = inputJson.has("timeout") ?
                    inputJson.get("timeout").getAsInt() : 300; // 默认超时300秒

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

            // 获取工作目录
            String workingDirectory = getWorkingDirectory(role);

            // 执行命令（新的智能缓冲机制）
            JsonObject commandResult = executeCommandWithBuffering(role, command, timeout, workingDirectory, interactive);

            // 添加额外信息到结果中
            commandResult.addProperty("requires_approval", requiresApproval);
            commandResult.addProperty("interactive", interactive);

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
     * 使用智能缓冲机制执行命令
     * 参考 Cline 的实现，支持快慢命令的不同处理策略
     */
    private JsonObject executeCommandWithBuffering(ReactorRole role, String command, int timeout, String workingDirectory, boolean interactive) {
        JsonObject result = new JsonObject();
        Process process = null;
        String processId = null; // 声明在方法级别，确保异常处理时能访问到

        // 输出缓冲相关变量（使用线程安全的容器）
        final List<String> outputBuffer = Collections.synchronizedList(new ArrayList<>());
        final AtomicLong outputBufferSize = new AtomicLong(0);
        final AtomicBoolean chunkEnroute = new AtomicBoolean(false);
        final AtomicReference<ScheduledFuture<?>> chunkTimer = new AtomicReference<>();
        final AtomicReference<ScheduledFuture<?>> bufferStuckTimer = new AtomicReference<>();

        // 状态控制变量
        AtomicBoolean didContinue = new AtomicBoolean(false);
        AtomicBoolean completed = new AtomicBoolean(false);
        List<String> allOutputLines = new ArrayList<>();


        try {
            log.info("执行命令: {}, 工作目录: {}, 超时: {}秒, 交互模式: {}", command, workingDirectory, timeout, interactive);

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

            // 设置环境变量
            setupEnvironment(processBuilder, workingDirectory);

            // 合并标准输出和错误输出
            processBuilder.redirectErrorStream(true);

            // 启动进程
            process = processBuilder.start();
            final Process finalProcess = process;

            // 注册到进程管理器（前台进程，因为这是带缓冲的交互式执行）
            processId = processManager.registerProcess(process, command, workingDirectory, false);

            //发送到前端,用来停止用
            role.getFluxSink().next("<pid>" + processId + "</pid>");

            final String _processId = processId;

            // 缓冲刷新函数（参考 Cline 的 flushBuffer）
            final Runnable flushBuffer = new Runnable() {
                @Override
                public void run() {
                    if (chunkEnroute.get() || outputBuffer.isEmpty()) {
                        return;
                    }

                    synchronized (outputBuffer) {
                        if (outputBuffer.isEmpty()) {
                            return;
                        }

                        String chunk = String.join("\n", outputBuffer);
                        outputBuffer.clear();
                        outputBufferSize.set(0);
                        chunkEnroute.set(true);

                        log.debug("刷新输出缓冲区，内容长度: {} 字符", chunk.length());

                        // 启动缓冲超时检测
                        bufferStuckTimer.set(scheduler.schedule(() -> {
                            log.warn("输出缓冲区可能卡住，强制继续");
                            chunkEnroute.set(false);
                        }, BUFFER_STUCK_TIMEOUT_MS, TimeUnit.MILLISECONDS));

                        try {
                            // 自动继续处理，无需用户交互
                            if (!didContinue.get()) {
                                didContinue.set(true);
                                log.debug("自动继续处理命令输出");
                            }

                            // 输出内容（在实际应用中，这里可以发送到UI或日志）
                            log.info("命令输出: {}", chunk);

                            //发送到前端
                            if (null != role.getFluxSink()) {
                                role.getFluxSink().next("<terminal_append><process_pid>%s</process_pid><process_content>%s</process_content></terminal_append>".formatted(_processId, chunk));
                            }

                        } finally {
                            // 清理超时检测
                            ScheduledFuture<?> timer = bufferStuckTimer.get();
                            if (timer != null) {
                                timer.cancel(false);
                            }
                            chunkEnroute.set(false);

                            // 如果还有积累的输出，递归刷新
                            if (!outputBuffer.isEmpty()) {
                                scheduler.schedule(this, 0, TimeUnit.MILLISECONDS);
                            }
                        }
                    }
                }
            };

            // 调度刷新函数（参考 Cline 的 scheduleFlush）
            final Runnable scheduleFlush = () -> {
                ScheduledFuture<?> timer = chunkTimer.get();
                if (timer != null) {
                    timer.cancel(false);
                }
                chunkTimer.set(scheduler.schedule(flushBuffer, CHUNK_DEBOUNCE_MS, TimeUnit.MILLISECONDS));
            };

            // 异步读取进程输出
            CompletableFuture<Void> outputReader = CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(finalProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        allOutputLines.add(line);
                        
                        // 检查进程是否被分离
                        AtomicBoolean currentDetachFlag = processManager.getDetachFlag(_processId);
                        boolean isDetached = currentDetachFlag != null && currentDetachFlag.get();

                        if (!didContinue.get() && !isDetached) {
                            // 还没有用户确认且未分离，需要缓冲
                            synchronized (outputBuffer) {
                                outputBuffer.add(line);
                                outputBufferSize.addAndGet(line.getBytes().length);

                                // 检查是否需要立即刷新
                                if (outputBuffer.size() >= CHUNK_LINE_COUNT ||
                                        outputBufferSize.get() >= CHUNK_BYTE_SIZE) {
                                    // 立即刷新
                                    log.info("flushBuffer---");
                                    scheduler.execute(flushBuffer);
                                } else {
                                    // 调度延迟刷新
                                    scheduler.execute(scheduleFlush);
                                }
                            }
                        } else {
                            // 用户已经确认继续或进程已分离，直接输出
                            if (isDetached) {
                                log.info("后台进程输出: {}", line);
                            } else {
                                log.info("实时输出: {}", line);
                            }
                            
                            if (null != role.getFluxSink()) {
                                role.getFluxSink().next("<terminal_append><process_pid>%s</process_pid><process_content>%s</process_content></terminal_append>".formatted(_processId, line));
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("读取进程输出时发生异常", e);
                }
            }, executorService);

            // 设置完成超时检测
            final AtomicReference<ScheduledFuture<?>> completionTimer = new AtomicReference<>();
            completionTimer.set(scheduler.schedule(() -> {
                if (!completed.get()) {
                    log.warn("等待命令完成超时");
                }
            }, COMPLETION_TIMEOUT_MS, TimeUnit.MILLISECONDS));

            // 等待进程完成（使用可分离的等待机制）
            AtomicBoolean detachFlag = processManager.getDetachFlag(processId);
            boolean processCompleted = processManager.waitWithDetachment(finalProcess, detachFlag, timeout);
            completed.set(true);

            // 清理定时器
            ScheduledFuture<?> compTimer = completionTimer.get();
            if (compTimer != null) {
                compTimer.cancel(false);
            }
            ScheduledFuture<?> chunkTmr = chunkTimer.get();
            if (chunkTmr != null) {
                chunkTmr.cancel(false);
            }

            if (!processCompleted) {
                // 检查是否是被分离还是超时
                boolean wasDetached = detachFlag != null && detachFlag.get();
                
                if (wasDetached) {
                    // 进程被分离，不杀死进程，让它继续在后台运行
                    log.info("命令执行被分离，进程将继续在后台运行: {}", command);
                    result.addProperty("message", "进程已分离到后台继续运行");
                    result.addProperty("detached", true);
                    result.addProperty("success", true);
                    result.addProperty("background_process_id", processId);
                    
                    // 进程被分离，更新进程管理器中的状态
                    ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                    if (processInfo != null) {
                        processInfo.setStatus("detached");
                        // 确保进程标记为后台运行
                        processInfo.setBackground(true);
                    }
                    
                    // 通知前端进程已分离到后台
                    if (null != role.getFluxSink()) {
                        role.getFluxSink().next("<terminal_append><process_pid>%s</process_pid><process_content>%s</process_content></terminal_append>"
                                .formatted(processId, "\n[进程已分离到后台继续运行，PID: " + processId + "]\n"));
                    }
                    
                } else {
                    // 超时，强制终止进程
                    finalProcess.destroyForcibly();
                    log.warn("命令执行超时 ({}秒): {}", timeout, command);
                    result.addProperty("error", "命令执行超时 (" + timeout + "秒)");
                    result.addProperty("timeout", true);
                    
                    // 进程超时被终止，更新进程管理器中的状态
                    ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                    if (processInfo != null) {
                        processInfo.setStatus("timeout");
                    }
                }

                return result;
            }

            // 等待输出读取完成
            outputReader.get(5, TimeUnit.SECONDS);

            // 最后刷新剩余的缓冲区内容
            if (!didContinue.get() && !outputBuffer.isEmpty()) {
                ScheduledFuture<?> finalChunkTmr = chunkTimer.get();
                if (finalChunkTmr != null) {
                    finalChunkTmr.cancel(false);
                }
                flushBuffer.run();
            }

            // 获取退出代码
            int exitCode = finalProcess.exitValue();

            // 构建最终输出
            StringBuilder output = new StringBuilder();
            for (String line : allOutputLines) {
                output.append(line).append("\n");
            }

            result.addProperty("exit_code", exitCode);
            result.addProperty("output", output.toString());
            result.addProperty("command", command);
            result.addProperty("working_directory", workingDirectory);
            result.addProperty("completed", true);
            result.addProperty("interactive_mode", interactive);
            result.addProperty("process_id", processId);
            result.addProperty("pid", finalProcess.pid());


            if (exitCode == 0) {
                log.info("命令执行成功，退出代码: {}, 输出行数: {}", exitCode, allOutputLines.size());
                result.addProperty("success", true);
            } else {
                log.warn("命令执行完成，但退出代码非零: {}", exitCode);
                result.addProperty("success", false);
            }

            // 更新进程管理器中的状态
            ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
            if (processInfo != null) {
                processInfo.setStatus(exitCode == 0 ? "completed" : "failed");
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
        } catch (Exception e) {
            log.error("命令执行时发生异常", e);
            result.addProperty("error", "命令执行失败: " + e.getMessage());
            result.addProperty("success", false);

            // 更新进程状态为错误
            if (processId != null) {
                ProcessManager.ProcessInfo processInfo = processManager.getProcessInfo(processId);
                if (processInfo != null) {
                    processInfo.setStatus("error");
                }
            }
        } finally {
            // 清理资源，但不杀死分离的进程
            if (process != null && process.isAlive()) {
                // 检查进程是否被分离
                AtomicBoolean detachFlag = processManager.getDetachFlag(processId);
                boolean wasDetached = detachFlag != null && detachFlag.get();
                
                if (!wasDetached) {
                    // 只有未分离的进程才强制终止
                    process.destroyForcibly();
                    log.debug("清理未分离的进程: {}", processId);
                } else {
                    log.info("保留分离的后台进程: {}", processId);
                }
            }
            
            ScheduledFuture<?> cleanupChunkTmr = chunkTimer.get();
            if (cleanupChunkTmr != null) {
                cleanupChunkTmr.cancel(false);
            }
            ScheduledFuture<?> bufferTmr = bufferStuckTimer.get();
            if (bufferTmr != null) {
                bufferTmr.cancel(false);
            }
        }

        return result;
    }


    /**
     * 设置进程环境变量
     */
    private void setupEnvironment(ProcessBuilder processBuilder, String workingDirectory) {
        Map<String, String> processEnv = processBuilder.environment();
        processEnv.clear();

        // 获取 zsh 环境变量
        Map<String, String> zshEnv = getZshEnvironment();
        processEnv.putAll(zshEnv);

        // 设置工作目录相关环境变量
        processEnv.put("CWD", workingDirectory);
        processEnv.put("PWD", workingDirectory);

        // 确保 SHELL 环境变量指向 zsh
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            processEnv.put("SHELL", "/bin/zsh");
        }

        log.debug("使用 {} 个环境变量执行命令", processEnv.size());
    }

    /**
     * 获取工作目录
     */
    private String getWorkingDirectory(ReactorRole role) {
        try {
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

        String defaultDir = System.getProperty("user.dir");
        log.debug("使用默认工作目录: {}", defaultDir);
        return defaultDir;
    }

    /**
     * 获取 zsh 的完整环境变量（带缓存）
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

            // 等待进程完成
            boolean completed = process.waitFor(10, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                log.warn("获取 zsh 环境变量超时，使用系统默认环境变量");
                return System.getenv();
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.warn("获取 zsh 环境变量失败，退出代码: {}", exitCode);
                return System.getenv();
            }

            // 解析环境变量
            for (String line : envLines) {
                if (StringUtils.isNotBlank(line) && line.contains("=")) {
                    int equalIndex = line.indexOf("=");
                    String key = line.substring(0, equalIndex);
                    String value = line.substring(equalIndex + 1);

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
            return System.getenv();
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    /**
     * 检查是否为危险命令
     */
    private boolean isDangerousCommand(String command) {
        String lowerCommand = command.toLowerCase().trim();

        String[] dangerousPatterns = {
                "rm -rf", "del /f", "format", "fdisk",
                "dd if=", "mkfs", "shutdown", "reboot",
                "sudo rm", "sudo del", "chmod 777",
                "curl.*|.*sh", "wget.*|.*sh",
                ">/dev/", "2>/dev/null",
        };

        for (String pattern : dangerousPatterns) {
            if (lowerCommand.contains(pattern.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 清除 zsh 环境变量缓存
     */
    public static void clearZshEnvironmentCache() {
        cachedZshEnv = null;
        lastEnvCacheTime = 0;
        log.debug("已清除 zsh 环境变量缓存");
    }

    // ============ 进程管理便捷方法 ============

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
     * 停止指定进程
     *
     * @param processId 进程ID
     * @return 是否成功停止
     */
    public static boolean stopProcess(String processId) {
        return processManager.stopProcess(processId);
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
     * 检查指定进程是否还在运行
     *
     * @param processId 进程ID
     * @return 是否在运行
     */
    public static boolean isProcessRunning(String processId) {
        return processManager.isProcessRunning(processId);
    }

    /**
     * 停止所有进程
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
        return processManager.killAllProcesses();
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
     * 分离指定进程（让进程转为后台运行，结束当前tool调用）
     *
     * @param processId 进程ID
     * @return 是否成功设置分离标志
     */
    public static boolean detachProcess(String processId) {
        return processManager.detachProcess(processId);
    }
    
    /**
     * 分离所有进程
     *
     * @return 成功分离的进程数量
     */
    public static int detachAllProcesses() {
        return processManager.detachAllProcesses();
    }
    
    /**
     * 获取进程的分离标志
     *
     * @param processId 进程ID
     * @return 分离标志，如果进程不存在返回null
     */
    public static AtomicBoolean getDetachFlag(String processId) {
        return processManager.getDetachFlag(processId);
    }
    
    /**
     * 使用可分离的等待机制等待进程完成
     *
     * @param processId 进程ID
     * @param timeoutSeconds 超时时间（秒）
     * @return true表示进程正常完成，false表示超时或被分离
     */
    public static boolean waitWithDetachment(String processId, long timeoutSeconds) {
        return processManager.waitWithDetachment(processId, timeoutSeconds);
    }

    /**
     * 关闭线程池资源和停止所有进程
     */
    public static void shutdown() {
        log.info("ExecuteCommandToolOptimized 开始关闭，停止所有进程...");

        // 停止所有进程
        int stoppedProcesses = processManager.stopAllProcesses(true);
        if (stoppedProcesses > 0) {
            log.info("已停止 {} 个进程", stoppedProcesses);
        }

        // 关闭线程池
        executorService.shutdown();
        scheduler.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("ExecuteCommandToolOptimized 线程池已关闭");
    }
}
