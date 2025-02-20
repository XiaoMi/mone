package run.mone.mcp.mermaid.function;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.mermaid.service.MermaidService;

@Data
@Slf4j
public class MermaidFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "mermaid_executor";
    private String desc = "mermaid executor";
    private ObjectMapper objectMapper;

    private String mermaidToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["mermaid_convert"],
                        "description": "Convert Mermaid diagram code to image and save to Downloads folder or specified absolute path. \
                        This tool generates an image file from the diagram and saves it to the system Downloads folder or a specified absolute path.\
                        The filename can be provided with or without extension - if the extension is missing or doesn't match, \
                        the output format, the correct extension will be automatically added."
                    },
                    "mermaid_code": {
                        "type": "string",
                        "description": "Mermaid code not include ```mermaid``` block."
                    },
                    "output": {
                        "type": "string",
                        "description": "Output file name include extension. It should be either svg, png or pdf. Optional, default png. If not specified, the file will be saved to the system Downloads folder."
                    }
                },
                "required": ["operation", "mermaid_code", "output"]
            }
            """;

    public MermaidFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {
        String mmdc = System.getenv().getOrDefault("MMDC", "");
        
        String operation = (String) params.get("operation");
        String mermaid_code = (String) params.get("mermaid_code");
        String output = (String) params.get("output");

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation is required");
        }

        if (mermaid_code == null || mermaid_code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mermaid code is required");
        }

        if (output == null || output.trim().isEmpty()) {
            throw new IllegalArgumentException("Output code is required");
        }

        try {
            String outputPath;
            if (output.contains("/") || output.contains("\\")) {
                // 用户提供了完整路径
                outputPath = output;
            } else {
                // 用户只提供了文件名，使用下载文件夹
                String downloadFolder = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
                outputPath = downloadFolder + output;
            }

            boolean status = MermaidService.convert(mmdc, mermaid_code, outputPath);

            if (!status) {
                log.error("Convert failed");
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Convert failed")),
                        true
                );
            }
            log.error("Convert sucess");
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Convert success, output: " + outputPath + ".")),
                    false
            );
        } catch (Exception e) {
            log.error("Convert failed", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
}