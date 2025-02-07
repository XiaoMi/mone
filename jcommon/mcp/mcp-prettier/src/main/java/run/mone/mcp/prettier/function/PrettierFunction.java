package run.mone.mcp.prettier.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

@Data
@Slf4j
public class PrettierFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "prettierOperation";

    private String desc = "Format text content into Markdown";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "content": {
                        "type": "string",
                        "description": "The text content to format as Markdown"
                    }
                },
                "required": ["content"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> input) {
        try {
            String content = (String) input.get("content");
            if (content == null || content.trim().isEmpty()) {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Content cannot be empty")),
                    true
                );
            }

            String formatted = formatToMarkdown(content);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(formatted)),
                false
            );
        } catch (Exception e) {
            log.error("Failed to format content", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Failed to format content: " + e.getMessage())),
                true
            );
        }
    }

    private String formatToMarkdown(String content) {
        StringBuilder markdown = new StringBuilder();
        String[] lines = content.split("\n");
        boolean inCodeBlock = false;
        boolean inList = false;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            String nextLine = (i + 1 < lines.length) ? lines[i + 1].trim() : "";
            
            // 处理空行
            if (line.isEmpty()) {
                markdown.append("\n");
                inList = false;
                continue;
            }

            // 处理代码块
            if (line.startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                markdown.append(line).append("\n");
                continue;
            }
            
            if (inCodeBlock) {
                markdown.append(line).append("\n");
                continue;
            }

            // 处理标题
            if (line.matches("^#{1,6}\\s.*")) {
                markdown.append(line).append("\n\n");
                continue;
            }

            // 处理无序列表
            if (line.matches("^[\\-\\*]\\s.*")) {
                markdown.append(line).append("\n");
                inList = true;
                continue;
            }

            // 处理有序列表
            if (line.matches("^\\d+\\.\\s.*")) {
                markdown.append(line).append("\n");
                inList = true;
                continue;
            }

            // 处理引用
            if (line.startsWith(">")) {
                markdown.append(line).append("\n");
                continue;
            }

            // 处理行内代码
            if (line.contains("`")) {
                markdown.append(line).append("\n");
                continue;
            }

            // 处理普通段落
            if (!inList) {
                markdown.append(line);
                if (!nextLine.isEmpty() && !nextLine.matches("^[#>\\-\\*\\d\\.].*")) {
                    markdown.append(" ");
                } else {
                    markdown.append("\n\n");
                }
            }
        }

        return markdown.toString().trim();
    }
}
