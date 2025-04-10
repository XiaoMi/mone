package run.mone.mcp.terminal.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

@Data
@Slf4j
public class TerminalFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private String name = "terminalOperation";
    private String desc = "Terminal operations include opening the terminal, running commands, closing the terminal, and simulating key operations";
    private String toolScheme = """
        {
            "type": "object",
            "properties": {
                "operation": {
                    "type": "string",
                    "enum": ["open", "execute", "simulate", "close"],
                    "description": "The type of terminal operation to be performed"
                },
                "command": {
                    "type": "string",
                    "description": "The command to be executed (required for execute operation)"
                },
                "windowId": {
                    "type": "string",
                    "description": "Terminal window ID (Required when execute/close/simulate operation)"
                },
                "keys": {
                    "type": "array",
                    "items": {"type": "string"},
                    "description": "Key sequence to simulate (required when simulating operations). Special keys use the analog key sequences supported by tmux send-keys. Below is a list of supported special keys and their corresponding formats:",
                    "enumExamples": [
                                "Enter",
                                "Escape",
                                "Tab",
                                "Space",
                                "Backspace",
                                "C-a",
                                "C-b",
                                "M-a",
                                "Up",
                                "Down",
                                "Left",
                                "Right",
                                "PageUp",
                                "PageDown",
                                "Home",
                                "End",
                                "F1",
                                "F2",
                                "...",
                                "F12"
                            ],
                            "specialKeysMapping": {
                                "Enter": "Simulates pressing the Enter key.",
                                "Escape": "Simulates pressing the Esc key.",
                                "Tab": "Simulates pressing the Tab key.",
                                "Space": "Simulates pressing the Space key.",
                                "Backspace": "Simulates pressing the Backspace key.",
                                "C-x": "Simulates pressing Ctrl + X (replace 'x' with any letter or character).",
                                "M-x": "Simulates pressing Alt + X (replace 'x' with any letter or character).",
                                "Up": "Simulates pressing the Up Arrow key.",
                                "Down": "Simulates pressing the Down Arrow key.",
                                "Left": "Simulates pressing the Left Arrow key.",
                                "Right": "Simulates pressing the Right Arrow key.",
                                "PageUp": "Simulates pressing the Page Up key.",
                                "PageDown": "Simulates pressing the Page Down key.",
                                "Home": "Simulates pressing the Home key.",
                                "End": "Simulates pressing the End key.",
                                "F1-F12": "Simulates pressing the function keys F1 through F12."
                            }
                }
            },
            "required": ["operation"]
        }
        """;


    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String)arguments.get("operation");
        try{
            String result = switch (operation){
                case "open" -> openTerminal();
                case "execute" -> executeCommand((String)arguments.get("command"), (String)arguments.get("windowId"));
                case "simulate" -> simulationKeyPresses((String) arguments.get("windowId"), ((List<String>)arguments.get("keys")).toArray(new String[0]));
                case "close" -> closeTerminal((String) arguments.get("windowId"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false));

        }catch (Exception e){
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }

    private static final String FILE_PREFIX = "/terminal_output_";
    private static final String SESSION_PREFIX =  "session_";
    private final String TEMPORARY_FILE_PATH;

    public TerminalFunction(){
        TEMPORARY_FILE_PATH = System.getenv().getOrDefault("TEMPORARY_FILE_PATH", "/tmp");
    }

    public String openTerminal() {
        try {
            String appleScript =
                    "tell application \"Terminal\"\n" +
                            "activate\n" +
                            "    delay 2\n" +
                            "    do script \"\"\n" +
                            "    set windowId to id of front window\n" +
                            "end tell";
            Process process = new ProcessBuilder("osascript", "-e", appleScript)
                    .start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String terminalWindowId = reader.readLine();
            process.waitFor();
            if (terminalWindowId == null || terminalWindowId.isEmpty()) {
               return "终端id为空，请关闭终端再重新打开！";
            }
            // 创建临时文件路径，确保每个终端窗口都有一个唯一的文件
            String outputFilePath = TEMPORARY_FILE_PATH + FILE_PREFIX + terminalWindowId + ".log";
            // 创建空的临时文件
            Files.createFile(Paths.get(outputFilePath));
            Thread.sleep(2000);
            String sessionName = SESSION_PREFIX + terminalWindowId;
            // AppleScript，用于启动 tmux 会话并记录输出
            String tmuxScript =
                    String.format("tell application \"Terminal\"\n" +
                            "    do script \"tmux new-session -d -s " + sessionName + " '/bin/zsh' \" in window id %s \n" +
                            //"    delay 2\n" +
                            "    do script \"tmux pipe-pane -o -t " + sessionName + " 'cat > " +outputFilePath + " '\" in window id %s \n" +
                            //"    delay 2\n" +
                            "    do script \"tmux attach -t " + sessionName + " \" in window id %s \n" +
                            "end tell", terminalWindowId, terminalWindowId,terminalWindowId);
            Process tmuxProcess = new ProcessBuilder("osascript", "-e", tmuxScript)
                    .start();
            tmuxProcess.waitFor();

            return terminalWindowId;

        } catch (IOException | InterruptedException e) {
            log.error("Error opening terminal: " + e.getMessage());
        }
        return null;
    }

    public String executeCommand(String command, String windowId){
        if (windowId == null || windowId.isEmpty()) {
            return "终端未开启，请打开终端！";
        }
        String outputFilePath = TEMPORARY_FILE_PATH + FILE_PREFIX + windowId + ".log";
        String res = "";
        try {
            FileOutputStream fos = new FileOutputStream(outputFilePath);
            fos.close();
            String appleScript = String.format(  "tell application \"Terminal\"\n" +
                    "do script \"%s\" in window id %s\n" +
                    "end tell", command, windowId);
            ProcessBuilder pb = new ProcessBuilder("osascript", "-e", appleScript);
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                log.error("Command exited with error code: " + exitCode);
            }
            Thread.sleep(1000);
            res = readAndFilterOutput(outputFilePath);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command: " + e.getMessage());
        }
        return res;
    }

    public String readAndFilterOutput(String outputFilePath) {
        StringBuilder filteredOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFilePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Filter out ANSI escape codes and other unnecessary content
                line = line.replaceAll("\\u001B\\[[;\\d]*[a-zA-Z]", "");
                line = line.replaceAll("\u0000", "");
                if (!line.trim().isEmpty()) {
                    filteredOutput.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filteredOutput.toString();
    }

    public String closeTerminal(String windowId){
        try {
            String sessionName = SESSION_PREFIX + windowId;
            String appleScript =
                    String.format("tell application \"Terminal\"\n" +
                            "if exists window id %s then\n" +
                            "    do script \"tmux kill-session -t %s\" in window id %s\n" +
                            "    delay 1\n" +
                            "    close (every window whose id is %s)\n" +
                            "end if\n" +
                            "end tell", windowId, sessionName, windowId, windowId);
            ProcessBuilder pb = new ProcessBuilder("osascript", "-e", appleScript);
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                return "关闭终端失败，请重试：" + exitCode;
            }
            // 删除对应的临时文件

            String outputFilePath = TEMPORARY_FILE_PATH + FILE_PREFIX + windowId + ".log";
            Files.deleteIfExists(Paths.get(outputFilePath));
        } catch (IOException | InterruptedException e) {
            log.error("Error opening terminal: {}" , e.getMessage());
        }
        return "终端窗口" + windowId + "成功关闭！";
    }

    public String simulationKeyPresses(String windowId, String... keys){
        if (windowId == null || windowId.isEmpty()) {
            return "无此终端窗口";
        }
        String res = "";
        try {
            String sessionName = SESSION_PREFIX + windowId;

            StringBuilder tmuxCommand = new StringBuilder();
            for (String key : keys) {
                tmuxCommand.append(String.format("tmux send-keys -t %s '%s' ", sessionName, key));
            }
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", tmuxCommand.toString());
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                String err = new String(p.getErrorStream().readAllBytes());
                return "发送击键失败：" + exitCode + ", " + err;
            }
            Thread.sleep(1000);

            String outputFilePath = TEMPORARY_FILE_PATH +  FILE_PREFIX + windowId + ".log";
            res =  readAndFilterOutput(outputFilePath);
        } catch (Exception e) {
            log.error("Error sending keystrokes via tmux: {}", e.getMessage());
        }
        return res;
    }


}
