package run.mone.mcp.time.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

@Data
@Slf4j
public class TimeFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "time_executor";
    private String desc = "Time related operations including getting timezone info and current time";
    private ObjectMapper objectMapper;

    private String timeToolSchema = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["get_zone_info", "get_current_time"],
                        "description": "Time operations"
                    },
                    "zone": {
                        "type": "string",
                        "description": "Time zone ID (e.g. 'America/New_York'). If not provided, system default zone will be used."
                    }
                },
                "required": ["operation"]
            }
            """;


    public TimeFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> params) {

        String operation = (String) params.get("operation");

        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation is required");
        }

        String result;
        try {
            result = switch (operation) {
                case "get_zone_info" -> getZoneInfo();
                case "get_current_time" -> getCurrentTime((String) params.get("zone"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
            
            log.info("Time operation completed successfully: {}", operation);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(result)),
                    false
            );

        } catch (Exception e) {
            log.error("Error executing time operation", e);
            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            );
        }
    }

    private String getZoneInfo() {
        var zoneId = java.time.ZoneId.systemDefault();
        var rules = zoneId.getRules();
        var offset = rules.getOffset(java.time.Instant.now());
        
        return String.format("Current timezone: %s\nZone offset: %s\nDaylight Savings: %s",
            zoneId.getId(),
            offset.toString(),
            rules.isDaylightSavings(java.time.Instant.now()) ? "Yes" : "No"
        );
    }

    private String getCurrentTime(String zoneId) {
        var zone = zoneId != null ? java.time.ZoneId.of(zoneId) : java.time.ZoneId.systemDefault();
        var now = java.time.ZonedDateTime.now(zone);
        return String.format("Current time: %s\nDate: %s\nTime: %s\nTimezone: %s",
            now.toString(),
            now.toLocalDate(),
            now.toLocalTime(),
            now.getZone()
        );
    }
}