package run.mone.mcp.pty4j.function;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.gson.Gson;

/**
 * @author hobo
 * @description: pty4jMcpFunction
 * @date 2025-02-21 09:35
 */
@Slf4j
@Data
public class Pty4jFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "pty4j";

    private String desc = "Terminal operations including executing commands, managing interactive sessions, and handling terminal I/O";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "action": {
                        "type": "string",
                        "description": "Action type for terminal operations:\\n1. execute: Execute a command\\n2. input: Send input to terminal\\n3. resize: Resize terminal window\\n4. getOutput: Get terminal output\\n5. close: Close terminal session"
                    },
                    "command": {
                        "type": "string",
                        "description": "Command to execute in terminal. Example: 'ls -la' or 'vim file.txt'"
                    },
                    "sessionId": {
                        "type": "string",
                        "description": "Unique identifier for terminal session"
                    },
                    "input": {
                        "type": "string",
                        "description": "Input to send to terminal. Examples for vim operations:\\n1. 'i' - Enter insert mode\\n2. 'Hello World' - Type text in insert mode\\n3. '\\u001b' - Press ESC key to exit insert mode\\n4. ':w\\n' - Save file\\n5. ':q\\n' - Quit vim\\n6. ':wq\\n' - Save and quit\\n7. 'dd' - Delete current line\\n8. 'yy' - Copy current line\\n9. 'p' - Paste copied/deleted text\\n10. '/searchtext\\n' - Search for text"
                    },
                    "columns": {
                        "type": "integer",
                        "description": "Number of columns for terminal window"
                    },
                    "rows": {
                        "type": "integer",
                        "description": "Number of rows for terminal window"
                    }
                },
                "required": ["action", "sessionId"]
            }
            """;

    private final Map<String, PtyProcess> processMap = new ConcurrentHashMap<>();
    private final Map<String, StringBuilder> outputBuffers = new ConcurrentHashMap<>();
    private final ExecutorService outputReaderPool = Executors.newCachedThreadPool();

    private static final Set<String> INTERACTIVE_COMMANDS = new HashSet<>(Arrays.asList(
            "vim", "vi", "nano", "emacs", "top", "htop", "tail -f", "watch"
    ));

    private static final int BUFFER_SIZE = 8192;

    // ANSI 转义序列的正则表达式
    private static final Pattern ANSI_ESCAPE_PATTERN = Pattern.compile("\u001B\\[(?:[0-9]{1,3}(?:;[0-9]{1,3})*)?[A-Za-z]|\\[\\?[0-9;]*[A-Za-z=]|\\][0-9;]*");

    // 控制字符的正则表达式
    private static final Pattern CONTROL_CHARS_PATTERN = Pattern.compile("[\\p{Cntrl}&&[^\r\n\t]]");

    // vim 特殊序列的正则表达式
    private static final Pattern VIM_SEQUENCE_PATTERN = Pattern.compile("\\[>[0-9;=]+|\\][0-9;]+|\\[\\?[0-9;]*|=[\"\\w\\.]+|\\d+[hHlmM]|\\[\\?\\d+[hl]|=\\d+");

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {
        String action = (String) params.get("action");
        String sessionId = (String) params.get("sessionId");
        
        if (action == null) {
            return errorResult(sessionId, "Action must be specified");
        }

        try {
            return switch (action) {
                case "execute" -> executeTerminal(params);
                case "input" -> sendInput(params);
                case "resize" -> resizeTerminal(params);
                case "getOutput" -> getOutput(params);
                case "close" -> closeTerminal(params);
                default -> errorResult(sessionId, "Unknown action: " + action);
            };
        } catch (Exception e) {
            log.error("Error executing action: " + action, e);
            return errorResult(sessionId, "Error executing action: " + e.getMessage());
        }
    }

    private boolean isInteractiveCommand(String command) {
        return INTERACTIVE_COMMANDS.stream().anyMatch(cmd ->
                command.trim().startsWith(cmd) || command.trim().endsWith(cmd));
    }

    private Map<String, String> prepareEnvironment() {
        Map<String, String> env = new HashMap<>(System.getenv());
        env.put("TERM", "xterm");
        env.put("LANG", "en_US.UTF-8");
        env.put("LC_ALL", "en_US.UTF-8");
        // 移除 VIMINIT 环境变量，改为在命令行参数中设置
        return env;
    }

    private PtyProcess startProcess(String command, boolean isInteractive) throws IOException {
        String[] commandLine;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            commandLine = new String[]{"cmd.exe", "/c", command};
        } else {
            if (isInteractive && command.startsWith("vim")) {
                // 简化 vim 启动参数
                command = command + " -u NONE" +
                        " --cmd 'set noswapfile'" +
                        " --cmd 'set noruler'" +
                        " --cmd 'set laststatus=0'";
                commandLine = new String[]{"/bin/bash", "-c", command};
            } else {
                commandLine = isInteractive ? command.split("\\s+") :
                        new String[]{"/bin/bash", "-c", command};
            }
        }

        return new PtyProcessBuilder()
                .setCommand(commandLine)
                .setEnvironment(prepareEnvironment())
                .setInitialColumns(120)
                .setInitialRows(30)
                .setRedirectErrorStream(true)
                .start();
    }

    private void startOutputReader(String sessionId, PtyProcess process, boolean isInteractive) {
        StringBuilder outputBuffer = new StringBuilder();
        outputBuffers.put(sessionId, outputBuffer);

        outputReaderPool.submit(() -> {
            try (InputStream input = process.getInputStream()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while (process.isAlive() && (read = input.read(buffer)) != -1) {
                    String content = new String(buffer, 0, read, StandardCharsets.UTF_8);

                    // 格式化输出内容
                    content = formatOutput(content, isInteractive);

                    outputBuffer.append(content);

                    // 限制缓冲区大小
                    if (outputBuffer.length() > BUFFER_SIZE * 2) {
                        outputBuffer.delete(0, outputBuffer.length() - BUFFER_SIZE);
                    }
                }
            } catch (IOException e) {
                log.error("Error reading process output for session: " + sessionId, e);
            }
        });
    }

    private String formatOutput(String content, boolean isInteractive) {
        if (!isInteractive) {
            return content;
        }

        // 移除 ANSI 转义序列和其他特殊序列
        content = ANSI_ESCAPE_PATTERN.matcher(content).replaceAll("");
        content = CONTROL_CHARS_PATTERN.matcher(content).replaceAll("");
        content = VIM_SEQUENCE_PATTERN.matcher(content).replaceAll("");

        // 处理 vim 特有的输出格式
        content = content
                // 移除 vim 版本信息
                .replaceAll("VIM - Vi IMproved .*\\n", "")
                // 移除错误信息
                .replaceAll("(?m)^Error.*$\\n?", "")
                .replaceAll("(?m)^Too many.*$\\n?", "")
                .replaceAll("(?m)^More info.*$\\n?", "")
                // 移除波浪号行（但保留实际内容）
                .replaceAll("(?m)^~\\s*$\\n?", "")
                // 移除状态行
                .replaceAll("(?m)^.*?\\[.*?\\].*?$\\n?", "")
                // 移除文件信息行
                .replaceAll("\"[^\"]+\"\\s*(?:,\\s*)?\\d*\\s*(?:lines?|bytes?|B)\\s*(?:written)?.*?\\n?", "")
                // 移除插入模式提示
                .replaceAll("--\\s*INSERT\\s*--.*?\\n?", "")
                // 移除命令模式提示
                .replaceAll("(?m)^:\\w*$\\n?", "")
                // 移除连续的空行，只保留一个
                .replaceAll("\\n{3,}", "\n\n")
                // 移除开头和结尾的空行
                .replaceAll("^\\s*\\n+", "")
                .replaceAll("\\n+\\s*$", "")
                // 移除行尾空格
                .replaceAll("(?m)\\s+$", "");

        return content.trim();
    }

    private McpSchema.CallToolResult executeTerminal(Map<String, Object> params) {
        String command = (String) params.get("command");
        String sessionId = (String) params.get("sessionId");

        if (command == null || command.trim().isEmpty()) {
            return errorResult(sessionId, "Command cannot be empty");
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return errorResult(null, "Session ID cannot be empty");
        }

        try {
            boolean isInteractive = isInteractiveCommand(command);
            PtyProcess process = startProcess(command, isInteractive);
            processMap.put(sessionId, process);

            startOutputReader(sessionId, process, isInteractive);

            // 等待初始输出
            Thread.sleep(isInteractive ? 1000 : 500);

            String output = outputBuffers.get(sessionId).toString();
            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", sessionId);
            response.put("output", output.isEmpty() ? "Command started successfully" : output);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(new Gson().toJson(response))),
                false
            );
        } catch (Exception e) {
            return errorResult(sessionId, "Failed to execute command: " + e.getMessage());
        }
    }

    private McpSchema.CallToolResult sendInput(Map<String, Object> params) {
        String sessionId = (String) params.get("sessionId");
        String input = (String) params.get("input");

        PtyProcess process = processMap.get(sessionId);
        if (process == null || !process.isAlive()) {
            return errorResult(sessionId, "No active session found");
        }

        try {
            OutputStream output = process.getOutputStream();
            output.write(input.getBytes(StandardCharsets.UTF_8));
            output.flush();
            return successResult(sessionId, "Input sent successfully");
        } catch (IOException e) {
            return errorResult(sessionId, "Failed to send input: " + e.getMessage());
        }
    }

    private McpSchema.CallToolResult resizeTerminal(Map<String, Object> params) {
        String sessionId = (String) params.get("sessionId");
        Integer columns = (Integer) params.get("columns");
        Integer rows = (Integer) params.get("rows");

        if (columns == null || rows == null) {
            return errorResult(sessionId, "Columns and rows must be specified");
        }

        PtyProcess process = processMap.get(sessionId);
        if (process == null || !process.isAlive()) {
            return errorResult(sessionId, "No active session found");
        }

        process.setWinSize(new WinSize(columns, rows));
        return successResult(sessionId, "Terminal resized successfully");
    }

    private McpSchema.CallToolResult getOutput(Map<String, Object> params) {
        String sessionId = (String) params.get("sessionId");
        StringBuilder output = outputBuffers.get(sessionId);

        if (output == null) {
            return errorResult(sessionId, "No output buffer found");
        }

        String formattedOutput = formatOutput(output.toString(), true);
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("output", formattedOutput);
        return new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent(new Gson().toJson(response))),
            false
        );
    }

    private McpSchema.CallToolResult closeTerminal(Map<String, Object> params) {
        String sessionId = (String) params.get("sessionId");
        PtyProcess process = processMap.get(sessionId);

        if (process == null) {
            return errorResult(sessionId, "No terminal found for session: " + sessionId);
        }

        try {
            process.destroy();
            processMap.remove(sessionId);
            outputBuffers.remove(sessionId);
            return successResult(sessionId, "Terminal closed successfully");
        } catch (Exception e) {
            return errorResult(sessionId, "Failed to close terminal: " + e.getMessage());
        }
    }

    private McpSchema.CallToolResult successResult(String sessionId, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("message", message);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(new Gson().toJson(response))),
                false
        );
    }

    private McpSchema.CallToolResult errorResult(String sessionId, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("error", message);
        return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(new Gson().toJson(response))),
                true
        );
    }

    public void shutdown() {
        outputReaderPool.shutdown();
        try {
            if (!outputReaderPool.awaitTermination(5, TimeUnit.SECONDS)) {
                outputReaderPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            outputReaderPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 清理所有进程
        processMap.values().forEach(PtyProcess::destroy);
        processMap.clear();
        outputBuffers.clear();
    }
}
