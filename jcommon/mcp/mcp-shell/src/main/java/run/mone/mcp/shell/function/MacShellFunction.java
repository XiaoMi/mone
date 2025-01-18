package run.mone.mcp.shell.function;

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
public class MacShellFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "macShellOperation";

    private String desc = "Mac shell operations including common shell commands";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["ls", "pwd", "cat", "echo", "mkdir", "rm", "cp", "mv", "grep", "find","custom"],
                        "description":"The shell command to execute"
                    },
                    "arguments": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description":"The arguments for the command"
                    },
                    "customCommand": {
                        "type": "string",
                        "description": "Custom shell command to execute when command is set to 'custom'"
                    },
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
        if ("custom".equals(command)) {
            command = customCommand;
            args = null;
        }

        try {
            String result = executeCommand(command, args);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);

        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String executeCommand(String command, List<String> args) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command().add(command);

        if ("custom".equals(command)) {
            processBuilder.command("sh", "-c", command);
        } else {
            processBuilder.command().add(command);
            if (args != null) {
                processBuilder.command().addAll(args);
            }
        }

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Command exited with code " + exitCode);
        }

        return output.toString();
    }
}