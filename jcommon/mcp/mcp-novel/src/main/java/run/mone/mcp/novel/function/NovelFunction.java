package run.mone.mcp.novel.function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.novel.service.NovelService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class NovelFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final NovelService novelService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["summarizeNovel", "outlineImitation", "styleImitation"],
                        "description": "The novel operation to perform"
                    },
                    "novel": {
                        "type": "string",
                        "description": "一篇情节完整，人物生动的小说"
                    },
                    "outline": {
                        "type": "string",
                        "description": "小说大纲"
                    },
                    "textToChange": {
                        "type": "string",
                        "description": "需要修改的原文"
                    },
                    "copyTextStyle": {
                        "type": "string",
                        "description": "原始风格示例"
                    }               
                },
                "required": ["operation"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {

        String operation = (String) arguments.get("operation");
        String result;

        try {
            switch (operation) {
                case "summarizeNovel" -> {
                    result = novelService.extractPlotPoints((String) arguments.get("novel"));
                }
                case "outlineImitation" -> {
                    result = novelService.outlineImitation((String) arguments.get("outline"));
                }
                case "styleImitation" -> {
                    result = novelService.styleImitation((String) arguments.get("textToChange"), (String) arguments.get("copyTextStyle"));
                }
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    public String getName() {
        return "NovelOperation";
    }

    public String getDesc() {
        return "Perform various novel operations including summarizeNovel, writeNovel.";
    }


    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}