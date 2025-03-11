package run.mone.mcp.hammerspoon.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

@Data
@Slf4j
@Component
public class LocateCoordinatesFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    @Autowired
    private LocateCoordinates locateCoordinates;

    private String name = "定位坐标";
    private String desc = "定位图片中指定对象的坐标";
    
    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "objectDescription": {
                        "type": "string",
                        "description": "对于图片中需要定位的对象的描述"
                    },
                    "imageBase64": {
                        "type": "string",
                        "description": "图片的base64编码"
                    }
                    
                },
                "required": ["objectDescription", "imageBase64"]
            }
            """;

    public LocateCoordinatesFunction() {
        
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String objectDescription = (String) args.get("objectDescription");
            String imageBase64 = (String) args.get("imageBase64");
            String result = locateCoordinates.locateCoordinates(objectDescription, imageBase64);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result)),
                false
            );
        } catch (Throwable e) {
            log.error("Error locating coordinates: {}", e.getMessage());
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                true
            );
        }
    }
}