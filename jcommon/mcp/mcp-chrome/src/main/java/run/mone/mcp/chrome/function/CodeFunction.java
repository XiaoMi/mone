package run.mone.mcp.chrome.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class CodeFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "CodeAction";
    private String desc = "代码TOOL(如果你发现可以通过javascript代码可以来实现的功能,你直接生成代码然后放到code中,如果你需要通过code来实现,可以使用这个工具)，code的格式是一段可执行的javascript代码,类似:alert(123);";
    private final ObjectMapper objectMapper;

    public CodeFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "Name of the code action"
                    },
                    "code": {
                        "type": "string",
                        "description": "JavaScript code to execute"
                    }
                },
                "required": ["name", "code"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String name = (String) args.get("name");
            String code = (String) args.get("code");
            
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("执行代码: " + code)),
                false
            );
        } catch (Exception e) {
            log.error("执行JavaScript代码失败", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            );
        }
    }
} 