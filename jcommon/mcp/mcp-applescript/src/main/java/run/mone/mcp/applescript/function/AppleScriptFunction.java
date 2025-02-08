package run.mone.mcp.applescript.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class AppleScriptFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "appleScriptOperation";

    private String desc = "Execute AppleScript commands on macOS";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["restart", "shutdown", "sleep", "logout", "mute", "unmute", "volume", "custom"],
                        "description":"The AppleScript command to execute"
                    },
                    "arguments": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description":"Additional arguments for the command (e.g., volume level)"
                    },
                    "customCommand": {
                        "type": "string",
                        "description": "Custom AppleScript command to execute when command is set to 'custom'"
                    }
                },
                "required": ["command"]
            }
            """;


    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String command = (String) arguments.get("command");
        List<String> args = (List<String>) arguments.get("arguments");
        String customCommand = (String) arguments.get("customCommand");
        log.info("command: {} arguments: {}", command, args);
        try {
            String script;
            if ("custom".equals(command)) {
                script = customCommand;
            } else {
                script = generateAppleScript(command, args);
            }
            String result = executeAppleScript(script);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }


    private String generateAppleScript(String command, List<String> args) {
        switch (command) {
            case "restart":
                return "tell application \"System Events\" to restart";
            case "shutdown":
                return "tell application \"System Events\" to shut down";
            case "sleep":
                return "tell application \"System Events\" to sleep";
            case "logout":
                return "tell application \"System Events\" to log out";
            case "mute":
                return "set volume with output muted";
            case "unmute":
                return "set volume without output muted";
            case "volume":
                if (args != null && !args.isEmpty()) {
                    int volume = Integer.parseInt(args.get(0));
                    return String.format("set volume output volume %d", volume);
                }
                throw new IllegalArgumentException("Volume level not provided");
            case "custom":
                if (args != null && !args.isEmpty()) {
                    return args.get(0);
                }
                throw new IllegalArgumentException("Custom command not provided");
            default:
                throw new IllegalArgumentException("Unsupported command: " + command);
        }
    }


    private String executeAppleScript(String script) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", script);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        StringBuilder errorOutput = new StringBuilder();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            errorOutput.append(errorLine).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("AppleScript exited with code " + exitCode + ": " + errorOutput);
        }

        return output.toString().trim();
    }
}