package run.mone.mcp.excel.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.excel.service.ExcelService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class ExcelFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final ExcelService excelService;
    private String name = "excelOperation";
    private String desc = "Excel operations including creating charts, reading/writing data";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["createBarChart"],
                        "description": "The operation to perform on Excel"
                    },
                    "filePath": {
                        "type": "string",
                        "description": "Path to the Excel file"
                    },
                    "sheetName": {
                        "type": "string",
                        "description": "Name of the worksheet"
                    },
                    "chartTitle": {
                        "type": "string",
                        "description": "Title of the chart"
                    },
                    "categoryAxisTitle": {
                        "type": "string",
                        "description": "Title for category axis"
                    },
                    "valueAxisTitle": {
                        "type": "string",
                        "description": "Title for value axis"
                    },
                    "categories": {
                        "type": "array",
                        "items": {"type": "string"},
                        "description": "Categories for the chart"
                    },
                    "values": {
                        "type": "array",
                        "items": {"type": "number"},
                        "description": "Values for the chart"
                    }
                },
                "required": ["operation", "filePath"]
            }
            """;

    public ExcelFunction(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String filePath = (String) arguments.get("filePath");

        try {
            String result = switch (operation) {
                case "createBarChart" -> excelService.createBarChart(
                        filePath,
                        (String) arguments.get("sheetName"),
                        (String) arguments.get("chartTitle"),
                        (String) arguments.get("categoryAxisTitle"),
                        (String) arguments.get("valueAxisTitle"),
                        (List<String>) arguments.get("categories"),
                        (List<Number>) arguments.get("values")
                );
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            log.error("Error executing Excel operation", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }
} 