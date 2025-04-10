package run.mone.mcp.terminal.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.terminal.service.AthenaService;

import java.util.*;
import java.util.function.Function;

@Data
@Slf4j
public class TerminalFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private String name = "stream_terminalOperation";
    private String desc = "终端操作包括打开终端、运行命令、关闭终端和模拟按键操作";
    private String toolScheme = """
        {
            "type": "object",
            "properties": {
                "operation": {
                    "type": "string",
                    "enum": ["open", "execute", "simulate", "close"],
                    "description": "要执行的终端操作类型"
                },
                "command": {
                    "type": "string",
                    "description": "要执行的命令（执行操作时必需）"
                },
                "keys": {
                    "type": "array",
                    "items": {"type": "string"},
                    "description": "要模拟的按键序列（模拟操作时必需）。特殊键使用tmux send-keys支持的模拟按键序列。以下是支持的特殊键及其对应格式：",
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
                                "Enter": "模拟按下回车键",
                                "Escape": "模拟按下Esc键",
                                "Tab": "模拟按下Tab键",
                                "Space": "模拟按下空格键",
                                "Backspace": "模拟按下退格键",
                                "C-x": "模拟按下Ctrl + X（将'x'替换为任何字母或字符）",
                                "M-x": "模拟按下Alt + X（将'x'替换为任何字母或字符）",
                                "Up": "模拟按下上箭头键",
                                "Down": "模拟按下下箭头键",
                                "Left": "模拟按下左箭头键",
                                "Right": "模拟按下右箭头键",
                                "PageUp": "模拟按下Page Up键",
                                "PageDown": "模拟按下Page Down键",
                                "Home": "模拟按下Home键",
                                "End": "模拟按下End键",
                                "F1-F12": "模拟按下功能键F1到F12"
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
                case "execute" -> executeCommand((String)arguments.get("command"));
                case "simulate" -> simulationKeyPresses(((List<String>)arguments.get("keys")).toArray(new String[0]));
                case "close" -> closeTerminal();
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false));

        }catch (Exception e){
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }

    public String openTerminal() {
        try {
            AthenaService.openTerminal();
            return "终端已成功打开";
        } catch (Exception e) {
            log.error("Error opening terminal: " + e.getMessage());
            return "打开终端失败: " + e.getMessage();
        }
    }

    public String executeCommand(String command) {
        try {
            return AthenaService.executeCommandAndGetResult(command, 20000);
        } catch (Exception e) {
            log.error("Error executing command: " + e.getMessage());
            return "执行命令失败: " + e.getMessage();
        }
    }

    public String closeTerminal() {
        try {
            // 由于AthenaService中没有closeTerminal方法，我们可以通过执行exit命令来关闭终端
            AthenaService.openTerminalWithCommand("exit");
            return "终端已成功关闭";
        } catch (Exception e) {
            log.error("Error closing terminal: " + e.getMessage());
            return "关闭终端失败: " + e.getMessage();
        }
    }

    public String simulationKeyPresses(String... keys) {
        try {
            // 将按键序列转换为命令
            StringBuilder command = new StringBuilder();
            for (String key : keys) {
                command.append(key).append(" ");
            }
            // 使用AthenaService执行按键序列
            AthenaService.openTerminalWithCommand(command.toString());
            return "按键序列已成功执行";
        } catch (Exception e) {
            log.error("Error simulating key presses: " + e.getMessage());
            return "执行按键序列失败: " + e.getMessage();
        }
    }
}
