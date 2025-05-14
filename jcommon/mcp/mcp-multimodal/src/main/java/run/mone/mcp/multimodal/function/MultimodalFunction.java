package run.mone.mcp.multimodal.function;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.multimodal.service.GuiAgentService;
import run.mone.mcp.multimodal.service.MultimodalService;
import run.mone.mcp.multimodal.gui.GuiAgent;

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
                        "enum": ["takeScreenshot", "runGuiAgent"],
                        "description": "The operation to perform on the user interface takeScreenshot=截屏 runGuiAgent=执行操作gui的指令 "
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
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");

        return Flux.defer(() -> {
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
                    case "typeText" -> multimodalService.typeText(
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
                    case "takeScreenshot" -> multimodalService.captureScreenshotWithRobot(
                            (String) arguments.getOrDefault("filePath", null));
                    case "runGuiAgent" -> runGuiAgent((String) arguments.get("instruction"));
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };
                return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }
    
    private Flux<String> runGuiAgent(String instruction) {
        if (instruction == null || instruction.isEmpty()) {
            return Flux.error(new IllegalArgumentException("Instruction cannot be empty"));
        }
        
        guiAgent.run(instruction);
        return Flux.just("GuiAgent has processed the instruction: " + instruction);
    }

    @Override
    public String getName() {
        return "stream_multimodal";
    }

    @Override
    public String getDesc() {
        return "Execute UI operations including analyzing screenshots, clicking, double-clicking, right-clicking, dragging and dropping, typing text, pressing hotkeys, and running GuiAgent with instructions.";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
} 