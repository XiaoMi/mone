package run.mone.mcp.multimodal.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.multimodal.gui.GuiAgent;
import run.mone.mcp.multimodal.service.MultimodalService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class MultimodalFunction implements McpFunction {

    private final MultimodalService multimodalService;
    private final GuiAgent guiAgent;

    @Autowired
    public MultimodalFunction(MultimodalService multimodalService,
                              GuiAgent guiAgent) {
        this.multimodalService = multimodalService;
        this.guiAgent = guiAgent;
    }

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["runGuiAgent"],
                        "description": "The operation to perform on the user interface runGuiAgent=执行操作gui的指令(内部会自动截图,然后分析) "
                    },
                    "imageBase64": {
                        "type": "string",
                        "description": "Base64 encoded image of the screen for analysis"
                    },
                    "instruction": {
                        "type": "string",
                        "description": "User instruction for analyzing the screenshot"
                    },
                    "x": {
                        "type": "integer",
                        "description": "X coordinate for clicking operations"
                    },
                    "y": {
                        "type": "integer",
                        "description": "Y coordinate for clicking operations"
                    },
                    "startX": {
                        "type": "integer",
                        "description": "Starting X coordinate for drag operations"
                    },
                    "startY": {
                        "type": "integer",
                        "description": "Starting Y coordinate for drag operations"
                    },
                    "endX": {
                        "type": "integer",
                        "description": "Ending X coordinate for drag operations"
                    },
                    "endY": {
                        "type": "integer",
                        "description": "Ending Y coordinate for drag operations"
                    },
                    "text": {
                        "type": "string",
                        "description": "Text to type"
                    },
                    "keys": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description": "List of keys to press simultaneously as a hotkey combination"
                    },
                    "filePath": {
                        "type": "string",
                        "description": "The file path to save the screenshot"
                    },
                    "appName": {
                        "type": "string",
                        "description": "应用名称或别名（可选）。如果提供，会在执行 runGuiAgent 操作前先激活该应用窗口并自动切换到应用所在的Space，确保截图和操作针对正确的窗口。支持的别名：chrome/谷歌浏览器、微信/wechat/wx、华泰证券/华泰/huatai、cursor、idea、终端/terminal、访达/finder等。注意：此功能仅支持 macOS 系统"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        try {
            Flux<String> result = switch (operation) {
                case "analyzeScreenshot" -> multimodalService.analyzeScreenshot(
                        (String) arguments.get("imageBase64"),
                        (String) arguments.get("instruction"));
                case "click" -> multimodalService.click(
                        Integer.parseInt((String) arguments.get("x")),
                        Integer.parseInt((String) arguments.get("y")));
                case "doubleClick" -> multimodalService.doubleClick(
                        Integer.parseInt((String) arguments.get("x")),
                        Integer.parseInt((String) arguments.get("y")));
                case "rightClick" -> multimodalService.rightClick(
                        Integer.parseInt((String) arguments.get("x")),
                        Integer.parseInt((String) arguments.get("y")));
                case "dragAndDrop" -> multimodalService.dragAndDrop(
                        Integer.parseInt((String) arguments.get("startX")),
                        Integer.parseInt((String) arguments.get("startY")),
                        Integer.parseInt((String) arguments.get("endX")),
                        Integer.parseInt((String) arguments.get("endY")));
                case "typeText" -> multimodalService.typeTextV2(
                        (String) arguments.get("text"));
                case "pressHotkey" -> {
                    Object keysObj = arguments.get("keys");
                    List<String> keys;
                    if (keysObj instanceof String) {
                        // Handle case where keys is a comma-separated string
                        keys = List.of(((String) keysObj).split(","));
                    } else {
                        // Handle case where keys is already a list
                        keys = (List<String>) keysObj;
                    }
                    yield multimodalService.pressHotkey(keys != null ? keys : Collections.emptyList());
                }
                case "takeScreenshot" -> {
                    String filePath = (String) arguments.getOrDefault("filePath", null);
                    String appName = (String) arguments.get("appName");
                    
                    // 如果指定了应用名，则先激活应用再截图
                    if (appName != null && !appName.isEmpty()) {
                        yield multimodalService.captureScreenshotWithAppActivation(appName, filePath);
                    } else {
                        yield multimodalService.captureScreenshotWithRobot(filePath);
                    }
                }

                //执行指令(用户的需求)
                case "runGuiAgent" -> runGuiAgent(
                        (String) arguments.get("instruction"),
                        (String) arguments.get("appName"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }

    private Flux<String> runGuiAgent(String instruction, String appName) {
        if (instruction == null || instruction.isEmpty()) {
            return Flux.error(new IllegalArgumentException("Instruction cannot be empty"));
        }
        
        return Flux.create(sink -> {
            try {
                sink.next("准备执行 GUI Agent\n");
                
                // 如果提供了应用名称，先激活应用窗口
                if (appName != null && !appName.isEmpty()) {
                    sink.next("正在激活应用: " + appName + "\n");
                    String activationResult = multimodalService.activateApplication(appName).blockFirst();
                    sink.next(activationResult + "\n");
                    
                    if (activationResult != null && activationResult.contains("成功")) {
                        sink.next("应用激活成功，等待窗口稳定...\n");
                        // 额外等待确保窗口完全显示和稳定
                        Thread.sleep(500);
                    } else {
                        sink.next("应用激活失败或不支持，继续执行截图分析...\n");
                    }
                }
                
                sink.next("开始执行 GUI Agent 任务\n");
                guiAgent.run(instruction, sink);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 重新设置中断状态
                sink.error(new RuntimeException("窗口激活被中断: " + e.getMessage(), e));
            } catch (Exception e) {
                sink.error(new RuntimeException("执行失败: " + e.getMessage(), e));
            }
        });
    }

    @Override
    public String getName() {
        return "stream_multimodal";
    }

    @Override
    public String getDesc() {
        return "Execute UI operations including analyzing screenshots, clicking, double-clicking, right-clicking, dragging and dropping, typing text, pressing hotkeys, and running GuiAgent with instructions. Supports optional application window activation before operations (macOS only).";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
} 