package run.mone.mcp.multimodal.function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.multimodal.service.MultimodalService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MultimodalFunction implements McpFunction {

    private final MultimodalService multimodalService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["analyzeScreenshot", "click", "doubleClick", "rightClick", "dragAndDrop", "typeText", "pressHotkey"],
                        "description": "The operation to perform on the user interface"
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
                            ((Number) arguments.get("x")).intValue(),
                            ((Number) arguments.get("y")).intValue());
                    case "doubleClick" -> multimodalService.doubleClick(
                            ((Number) arguments.get("x")).intValue(),
                            ((Number) arguments.get("y")).intValue());
                    case "rightClick" -> multimodalService.rightClick(
                            ((Number) arguments.get("x")).intValue(),
                            ((Number) arguments.get("y")).intValue());
                    case "dragAndDrop" -> multimodalService.dragAndDrop(
                            ((Number) arguments.get("startX")).intValue(),
                            ((Number) arguments.get("startY")).intValue(),
                            ((Number) arguments.get("endX")).intValue(),
                            ((Number) arguments.get("endY")).intValue());
                    case "typeText" -> multimodalService.typeText(
                            (String) arguments.get("text"));
                    case "pressHotkey" -> multimodalService.pressHotkey(
                            (List<String>) arguments.getOrDefault("keys", Collections.emptyList()));
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };
                return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }

    @Override
    public String getName() {
        return "multimodal";
    }

    @Override
    public String getDesc() {
        return "Execute UI operations including analyzing screenshots, clicking, double-clicking, right-clicking, dragging and dropping, typing text, and pressing hotkeys.";
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
} 