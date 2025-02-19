package run.mone.mcp.docparsing.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.docparsing.model.DocParsingResult;
import run.mone.mcp.docparsing.service.DocParsingService;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
public class DocParsingFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    
    private String name = "docparsing_executor";
    private String desc = "Parse documents to text content";
    private List<String> allowedDirectories;
    private ObjectMapper objectMapper;
    
    private String docParsingToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["parse_pdf", "parse_doc", "parse_docx"],
                        "description": "Type of document parsing operation to execute"
                    },
                    "path": {
                        "type": "string",
                        "description": "Path to the document file"
                    }
                },
                "required": ["operation", "path"]
            }
            """;
    
    public DocParsingFunction(List<String> allowedDirectories, ObjectMapper objectMapper) {
        this.allowedDirectories = allowedDirectories.stream()
                .map(this::normalizePath)
                .collect(Collectors.toList());
        this.objectMapper = objectMapper;
    }

    private String normalizePath(String path) {
        return Paths.get(path).normalize().toString();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {
        try {
            String operation = (String) params.get("operation");
            String path = (String) params.get("path");

            if (operation == null || operation.trim().isEmpty()) {
                throw new IllegalArgumentException("Operation is required");
            }

            if (path == null || path.trim().isEmpty()) {
                throw new IllegalArgumentException("Path is required");
            }
            
            switch (operation) {
                case "parse_pdf":
                    return handleParsePdf(path);
                case "parse_doc":
                    return handleParseDoc(path);
                case "parse_docx":
                    return handleParseDocx(path);
                default:
                    return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Unknown operation: " + operation)),
                        true
                    );
            }
        } catch (Exception e) {
            log.error("Failed to handle document parsing request", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                true
            );
        }
    }
    
    private McpSchema.CallToolResult handleParsePdf(String path) {
        try {
            DocParsingService docParsingService = new DocParsingService();
            DocParsingResult result = docParsingService.parsePdf(path);
            if (result.isSuccess()) {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.getContent())), 
                    false
                );
            } else {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.getError())), 
                    true
                );
            }
        } catch (Exception e) {
            log.error("Failed to parse PDF: {}", path, e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("PDF parsing failed: " + e.getMessage())), 
                true
            );
        }
    }
    
    private McpSchema.CallToolResult handleParseDoc(String path) {
        try {
            DocParsingService docParsingService = new DocParsingService();
            DocParsingResult result = docParsingService.parseDoc(path);
            if (result.isSuccess()) {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.getContent())), 
                    false
                );
            } else {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.getError())), 
                    true
                );
            }
        } catch (Exception e) {
            log.error("Failed to parse DOC: {}", path, e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("DOC parsing failed: " + e.getMessage())), 
                true
            );
        }
    }
    
    private McpSchema.CallToolResult handleParseDocx(String path) {
        try {
            DocParsingService docParsingService = new DocParsingService();
            DocParsingResult result = docParsingService.parseDocx(path);
            if (result.isSuccess()) {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.getContent())), 
                    false
                );
            } else {
                return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result.getError())), 
                    true
                );
            }
        } catch (Exception e) {
            log.error("Failed to parse DOCX: {}", path, e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("DOCX parsing failed: " + e.getMessage())), 
                true
            );
        }
    }
} 