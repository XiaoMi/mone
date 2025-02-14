package run.mone.mcp.feishu.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.feishu.model.DocBlock;
import run.mone.mcp.feishu.model.DocContent;
import run.mone.mcp.feishu.model.DocPermission;
import run.mone.mcp.feishu.model.Files;
import run.mone.mcp.feishu.service.FeishuDocService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
@Component
public class FeishuDocFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "feishuDoc";

    private String desc = "Feishu document operations including creating and managing documents";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["createDocument", "addBlock", "getFileInfo", "getDocument", "getRootFolder"],
                        "description": "Operation type for Feishu document. Supported operations:\\n1. createDocument: Create new document, requires title parameter. Example: {'operation': 'createDocument', 'title': 'New Document'}\\n2. addBlock: Add content blocks, requires block parameter with docId and elements. Example: {'operation': 'addBlock', 'block': {'docId': 'docx8H1i9Ko0TPLt', 'elements': {'2': 'content', '3': 'heading'}}}\\n3. getFileInfo: Get document info, requires documentId. Example: {'operation': 'getFileInfo', 'documentId': 'docx8H1i9Ko0TPLt'}\\n4. getDocument: Get document content, requires documentId. Example: {'operation': 'getDocument', 'documentId': 'docx8H1i9Ko0TPLt'}\\n5. getRootFolder: Get root folder token. Example: {'operation': 'getRootFolder'}"
                    },
                    "title": {
                        "type": "string",
                        "description": "Document title for createDocument operation. Example: 'Project Document', 'Meeting Notes'"
                    },
                    "folderToken": {
                        "type": "string",
                        "description": "Parent folder token for document creation. If not provided, document will be created in root folder. Example: 'fldcnxxxxxxxx'"
                    },
                    "documentId": {
                        "type": "string",
                        "description": "Document ID for getFileInfo and getDocument operations. Example: 'docx8H1i9Ko0TPLt'"
                    },
                    "block": {
                        "type": "object",
                        "description": "Document block structure for addBlock operation. Block types: 1=Page, 2=Text, 3=Heading1, 4=Heading2, 5=Heading3, 6=Heading4, 7=Heading5, 8=Heading6, 9=Heading7, 10=Heading8, 11=Heading9, 12=Bullet List, 13=Ordered List, 14=Code Block, 15=Quote. Example: {'docId': 'docx8H1i9Ko0TPLt', 'elements': {'3': 'Document Title', '2': 'Content', '12': 'List Item', '14': 'Code Example'}}"
                    }
                },
                "required": ["operation"]
            }
            """;

    private final FeishuDocService docService;
    private final ObjectMapper objectMapper;

    public FeishuDocFunction(FeishuDocService docService, ObjectMapper objectMapper) {
        this.docService = docService;
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String operation = (String) args.get("operation");
        log.info("Executing Feishu document operation: {}", operation);

        try {
            String result = switch (operation) {
                case "createDocument" -> createDocument(args);
                case "addBlock" -> addBlock(args);
                case "getFileInfo" -> getFileInfo(args);
                case "getDocument" -> getDocument(args);
                case "getRootFolder" -> getRootFolder();
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            log.error("Failed to execute Feishu document operation: {}", e.getMessage());
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String createDocument(Map<String, Object> args) throws Exception {
        String title = (String) args.get("title");
        String folderToken = (String) args.get("folderToken");

        DocContent doc = docService.createDocument(title, folderToken);
        return objectMapper.writeValueAsString(doc);
    }

    private String addBlock(Map<String, Object> args) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> blockData = (Map<String, Object>) args.get("block");
        DocBlock block = objectMapper.convertValue(blockData, DocBlock.class);

        DocBlock createdBlock = docService.createDocumentBlock(block);
        return objectMapper.writeValueAsString(createdBlock);
    }

    private String getFileInfo(Map<String, Object> args) throws Exception {
        String documentId = (String) args.get("documentId");

        Files fileInfo = docService.getFileInfo(documentId);
        return objectMapper.writeValueAsString(fileInfo);
    }

    private String getDocument(Map<String, Object> args) throws Exception {
        String documentId = (String) args.get("documentId");

        DocContent doc = docService.getDocument(documentId);
        return objectMapper.writeValueAsString(doc);
    }

    private String getRootFolder() throws Exception {
        String rootFolderToken = docService.getRootFolderToken();
        return objectMapper.writeValueAsString(Map.of("rootFolderToken", rootFolderToken));
    }

} 