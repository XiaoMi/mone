package run.mone.hive.mcp.client;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Claude Code CLI Client
 * 通过 shell 命令调用 claude code CLI 并获取结果
 */
@Slf4j
public class ClaudeCodeClient {

    // 缓存 zsh 环境变量，避免重复获取
    private static volatile Map<String, String> cachedZshEnv = null;
    private static volatile long lastEnvCacheTime = 0;
    private static final long ENV_CACHE_TIMEOUT = 300_000; // 5分钟缓存超时

    public static final String DEFAULT_CLAUDE_CMD = "/Users/zhangzhiyong/.nvm/versions/node/v20.19.4/bin/claude";
    public static final long DEFAULT_TIMEOUT_SECONDS = 300;

    private final String claudeCmd;
    private final long timeoutSeconds;
    private final String workingDirectory;

    public ClaudeCodeClient() {
        this(DEFAULT_CLAUDE_CMD, DEFAULT_TIMEOUT_SECONDS, null);
    }

    public ClaudeCodeClient(String claudeCmd, long timeoutSeconds, String workingDirectory) {
        this.claudeCmd = claudeCmd;
        this.timeoutSeconds = timeoutSeconds;
        this.workingDirectory = workingDirectory;
    }

    /**
     * 执行 claude code 命令
     *
     * @param prompt 提示词/问题
     * @return 执行结果
     */
    public ClaudeCodeResult execute(String prompt) {
        return execute(prompt, false, null);
    }

    /**
     * 执行 claude code 命令（带上下文）
     *
     * @param prompt           提示词/问题
     * @param continueContext  是否继续上下文 (-c 参数)
     * @param conversationId   会话ID (--conversation-id 参数)
     * @return 执行结果
     */
    public ClaudeCodeResult execute(String prompt, boolean continueContext, String conversationId) {
        List<String> command = buildCommand(prompt, continueContext, conversationId);
        return executeCommand(command);
    }

    /**
     * 处理 prompt 参数
     *
     * @param prompt 原始提示词
     * @return 处理后的提示词
     */
    private String escapePrompt(String prompt) {
        if (prompt == null || prompt.isEmpty()) {
            return "";
        }
        return prompt;
    }

    private List<String> buildCommand(String prompt, boolean continueContext, String conversationId) {
        List<String> command = new ArrayList<>();
        command.add(claudeCmd);
        
        if (continueContext) {
            command.add("-c");
        }

        if (conversationId != null && !conversationId.isEmpty()) {
            command.add("--conversation-id");
            command.add(conversationId);
        }

        command.add("-p");
        command.add(escapePrompt(prompt));
        
        return command;
    }

    /**
     * 设置进程环境变量
     */
    private void setupEnvironment(ProcessBuilder processBuilder) {
        Map<String, String> processEnv = processBuilder.environment();
        Map<String, String> zshEnv = getZshEnvironment();
        processEnv.putAll(zshEnv);

        // 打印关键环境变量用于调试
        String anthropicBaseUrl = processEnv.get("ANTHROPIC_BASE_URL");
        String anthropicAuthToken = processEnv.get("ANTHROPIC_AUTH_TOKEN");
        
        log.info("====== 关键环境变量 ======");
        log.info("ANTHROPIC_BASE_URL: {}", anthropicBaseUrl != null ? anthropicBaseUrl : "未设置");
        log.info("ANTHROPIC_AUTH_TOKEN: {}", anthropicAuthToken != null ? (anthropicAuthToken.substring(0, Math.min(20, anthropicAuthToken.length())) + "...") : "未设置");
        log.info("========================");

        if (workingDirectory != null) {
            processEnv.put("CWD", workingDirectory);
            processEnv.put("PWD", workingDirectory);
        }

        if (!processEnv.containsKey("SHELL")) {
            processEnv.put("SHELL", "/bin/zsh");
        }

        log.debug("使用 {} 个环境变量执行命令", processEnv.size());
    }

    /**
     * 获取 zsh 的完整环境变量（带缓存）
     */
    private Map<String, String> getZshEnvironment() {
        long currentTime = System.currentTimeMillis();

        if (cachedZshEnv != null && (currentTime - lastEnvCacheTime) < ENV_CACHE_TIMEOUT) {
            log.debug("使用缓存的 zsh 环境变量，包含 {} 个变量", cachedZshEnv.size());
            return new HashMap<>(cachedZshEnv);
        }

        Map<String, String> zshEnv = new HashMap<>();
        Process process = null;

        try {
            log.debug("开始获取 zsh 环境变量（缓存已过期或不存在）");

            ProcessBuilder testZsh = new ProcessBuilder("zsh", "--version");
            testZsh.redirectErrorStream(true);
            Process testProcess = testZsh.start();
            boolean zshAvailable = testProcess.waitFor(5, TimeUnit.SECONDS) && testProcess.exitValue() == 0;

            if (!zshAvailable) {
                log.warn("zsh 不可用，将使用系统默认环境变量");
                return System.getenv();
            }

            ProcessBuilder processBuilder = new ProcessBuilder("zsh", "-i", "-c", "env");
            processBuilder.redirectErrorStream(false);

            process = processBuilder.start();

            List<String> envLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    envLines.add(line);
                }
            }

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

            for (String line : envLines) {
                if (line != null && !line.trim().isEmpty() && line.contains("=")) {
                    int equalIndex = line.indexOf("=");
                    String key = line.substring(0, equalIndex);
                    String value = line.substring(equalIndex + 1);

                    if (key != null && !key.trim().isEmpty() && !key.contains(" ") && !key.contains("\t")) {
                        zshEnv.put(key, value);
                    }
                }
            }

            log.info("成功获取到 {} 个 zsh 环境变量", zshEnv.size());

            if (!zshEnv.containsKey("SHELL")) {
                zshEnv.put("SHELL", "/bin/zsh");
            }
            if (!zshEnv.containsKey("HOME")) {
                zshEnv.put("HOME", System.getProperty("user.home"));
            }
            if (!zshEnv.containsKey("USER")) {
                zshEnv.put("USER", System.getProperty("user.name"));
            }

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

    private ClaudeCodeResult executeCommand(List<String> command) {
        log.info("===== 开始执行 claude code 命令 =====");
        log.info("命令: {}", String.join(" ", command));
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            setupEnvironment(processBuilder);
            processBuilder.redirectErrorStream(true);

            if (workingDirectory != null) {
                processBuilder.directory(new java.io.File(workingDirectory));
                log.info("工作目录: {}", workingDirectory);
            }

            Process process = processBuilder.start();
            log.info("进程已启动");
            process.getOutputStream().close();

            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!completed) {
                log.error("命令执行超时");
                process.destroyForcibly();
                return ClaudeCodeResult.builder()
                        .success(false)
                        .exitCode(-1)
                        .output(output.toString())
                        .error("Command timed out after " + timeoutSeconds + " seconds")
                        .build();
            }

            int exitCode = process.exitValue();
            log.info("进程退出码: {}", exitCode);
            log.info("输出长度: {} 字符", output.length());

            return ClaudeCodeResult.builder()
                    .success(exitCode == 0)
                    .exitCode(exitCode)
                    .output(output.toString().trim())
                    .error(error.toString().trim())
                    .build();
            
        } catch (InterruptedException e) {
            log.error("命令执行被中断", e);
            Thread.currentThread().interrupt();
            return ClaudeCodeResult.builder()
                    .success(false)
                    .exitCode(-1)
                    .output("")
                    .error("命令执行被中断: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("命令执行异常", e);
            return ClaudeCodeResult.builder()
                    .success(false)
                    .exitCode(-1)
                    .output("")
                    .error("命令执行异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Claude Code 执行结果
     */
    @Data
    @Builder
    public static class ClaudeCodeResult {
        private boolean success;
        private int exitCode;
        private String output;
        private String error;

        @Override
        public String toString() {
            return "ClaudeCodeResult{" +
                    "success=" + success +
                    ", exitCode=" + exitCode +
                    ", output='" + (output != null ? output.substring(0, Math.min(100, output.length())) + "..." : "null") + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

    /**
     * 清除 zsh 环境变量缓存
     */
    public static void clearZshEnvironmentCache() {
        cachedZshEnv = null;
        lastEnvCacheTime = 0;
        log.debug("已清除 zsh 环境变量缓存");
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        ClaudeCodeClient client = new ClaudeCodeClient();
        ClaudeCodeResult result = client.execute("上下文里都有什么？", true, null);
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Output: " + result.getOutput());
    }
}