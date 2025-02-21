package run.mone.mcp.file.function;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.file.utils.FileFormatUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class FileFormatFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {


    private String fileFormatSchema = """
            {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": ["word_to_pdf", "pdf_to_word"],
                        "description": "The type of file conversion to execute"
                    },
                    "input_file": {
                        "type": "string",
                        "description": "Complete path to input file including filename and extension (e.g., '/path/to/input.md')"
                    },
                    "output_file": {
                        "type": "string",
                        "description": "Complete path where to save the output including filename and extension (required for pdf, docx, rst, latex, epub formats)"
                    }
                }
            }
            """;

    private String name = "file_format_executor";

    private String desc = "Execute file format convert (word2PDF,PDF2word...)";

    public  FileFormatFunction() {

    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String type = (String) args.get("type");
        try {
            switch (type.toLowerCase()) {
                case "word_to_pdf":
                    return executeWordToPDF((String) args.get("input_file"), (String) args.get("output_file"));
                case "pdf_to_word":
                    return executePDFToWord((String) args.get("input_file"), (String) args.get("output_file"));
                default:
                    throw new IllegalArgumentException("Unsupported operation type: " + type);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }


    public McpSchema.CallToolResult executeWordToPDF(String inputPath, String outputPath) {
        try {
            boolean res = FileFormatUtils.convertWordToPdf(inputPath, outputPath);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(String.valueOf(res))),
                    false
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public McpSchema.CallToolResult executePDFToWord(String inputPath, String outputPath) {
        try {
            boolean res = FileFormatUtils.convertPdfToWord(inputPath, outputPath);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(String.valueOf(res))),
                    false
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
