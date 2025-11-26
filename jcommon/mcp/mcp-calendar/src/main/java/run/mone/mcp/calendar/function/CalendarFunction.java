package run.mone.mcp.calendar.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class CalendarFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {
    private String name = "stream_calendarOperation";
    private String desc = "Mac Calendar operations including create, list, delete events";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {
                        "type": "string",
                        "enum": ["create", "list", "delete"],
                        "description": "The operation type for calendar"
                    },
                    "title": {
                        "type": "string",
                        "description": "Event title"
                    },
                    "startDate": {
                        "type": "string",
                        "description": "Event start date (format: yyyy-MM-dd HH:mm)"
                    },
                    "endDate": {
                        "type": "string",
                        "description": "Event end date (format: yyyy-MM-dd HH:mm)"
                    },
                    "calendar": {
                        "type": "string",
                        "description": "Calendar name (default: Calendar)"
                    },
                    "location": {
                        "type": "string",
                        "description": "Event location"
                    },
                    "notes": {
                        "type": "string",
                        "description": "Event notes"
                    }
                },
                "required": ["command"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        try {
            String command = (String) args.get("command");
            switch (command) {
                case "create":
                    return createEvent(args);
                case "list":
                    return listEvents(args);
                case "delete":
                    return deleteEvent(args);
                default:
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Unknown command: " + command)),
                            true
                    ));
            }
        } catch (Exception e) {
            log.error("Error executing calendar command", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                    true
            ));
        }
    }

    private List<String> getAvailableCalendars() throws Exception {
        String script = """
            tell application "Calendar"
                set calNames to {}
                repeat with calItem in calendars
                    set end of calNames to name of calItem
                end repeat
                return calNames
            end tell
            """;
        
        String result = executeAppleScript(script);
        if (result.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 处理 AppleScript 返回的列表格式
        result = result.replaceAll("[{}]", "");
        return Arrays.asList(result.split(",\\s*"));
    }

    private Flux<McpSchema.CallToolResult> createEvent(Map<String, Object> args) throws Exception {
        // 先获取可用的日历列表
        List<String> availableCalendars = getAvailableCalendars();
        String defaultCalendar = availableCalendars.isEmpty() ? "Calendar" : availableCalendars.get(0);
        
        String title = (String) args.get("title");
        String startDate = (String) args.get("startDate");
        String endDate = (String) args.get("endDate");
        String calendar = (String) args.getOrDefault("calendar", defaultCalendar);
        String location = (String) args.getOrDefault("location", "");
        String notes = (String) args.getOrDefault("notes", "");

        // 检查日历是否存在
        if (!availableCalendars.contains(calendar)) {
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: Calendar '" + calendar + "' not found. Available calendars: " + availableCalendars)),
                true
            ));
        }

        String formattedStartDate = formatDateForAppleScript(startDate);
        String formattedEndDate = formatDateForAppleScript(endDate);

        String script = String.format("""
            tell application "Calendar"
                tell calendar "%s"
                    make new event at end with properties {summary:"%s", start date:date "%s", end date:date "%s", location:"%s", description:"%s"}
                end tell
            end tell
            """, calendar, title, formattedStartDate, formattedEndDate, location, notes);

        executeAppleScript(script);

        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent("Successfully created event: " + title)),
            false
        ));
    }

    private Flux<McpSchema.CallToolResult> listEvents(Map<String, Object> args) throws Exception {
        List<String> availableCalendars = getAvailableCalendars();
        if (availableCalendars.isEmpty()) {
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("No calendars found")),
                true
            ));
        }

        String calendar = (String) args.getOrDefault("calendar", availableCalendars.get(0));
        if (!availableCalendars.contains(calendar)) {
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Calendar '" + calendar + "' not found. Available calendars: " + availableCalendars)),
                true
            ));
        }

        String startDate = (String) args.getOrDefault("startDate", "");
        String script;
        
        if (startDate.isEmpty()) {
            script = String.format("""
                tell application "Calendar"
                    tell calendar "%s"
                        set eventList to {}
                        repeat with evt in events
                            set eventInfo to {summary of evt, start date of evt, end date of evt, location of evt, description of evt}
                            set end of eventList to eventInfo
                        end repeat
                        return eventList
                    end tell
                end tell
                """, calendar);
        } else {
            String formattedStartDate = formatDateForAppleScript(startDate);
            script = String.format("""
                tell application "Calendar"
                    tell calendar "%s"
                        set eventList to {}
                        repeat with evt in (events whose start date > date "%s")
                            set eventInfo to {summary of evt, start date of evt, end date of evt, location of evt, description of evt}
                            set end of eventList to eventInfo
                        end repeat
                        return eventList
                    end tell
                end tell
                """, calendar, formattedStartDate);
        }

        String result = executeAppleScript(script);
        
        // 解析 AppleScript 返回的列表格式
        result = result.replaceAll("[{}]", "");
        String[] events = result.split("\\},\\s*\\{");
        
        StringBuilder formattedResult = new StringBuilder();
        formattedResult.append(String.format("Events in calendar '%s':\n", calendar));
        
        for (String event : events) {
            if (!event.trim().isEmpty()) {
                String[] eventInfo = event.split(",\\s*");
                if (eventInfo.length >= 5) {
                    formattedResult.append("\n- Title: ").append(eventInfo[0])
                                 .append("\n  Start: ").append(eventInfo[1])
                                 .append("\n  End: ").append(eventInfo[2])
                                 .append("\n  Location: ").append(eventInfo[3].isEmpty() ? "N/A" : eventInfo[3])
                                 .append("\n  Notes: ").append(eventInfo[4].isEmpty() ? "N/A" : eventInfo[4])
                                 .append("\n");
                }
            }
        }

        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent(formattedResult.toString())),
            false
        ));
    }

    private Flux<McpSchema.CallToolResult> deleteEvent(Map<String, Object> args) throws Exception {
        // 先获取可用的日历列表
        List<String> availableCalendars = getAvailableCalendars();
        if (availableCalendars.isEmpty()) {
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("No calendars found")),
                true
            ));
        }

        String title = (String) args.get("title");
        String calendar = (String) args.getOrDefault("calendar", availableCalendars.get(0));

        // 检查日历是否存在
        if (!availableCalendars.contains(calendar)) {
            return Flux.just(new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Calendar '" + calendar + "' not found. Available calendars: " + availableCalendars)),
                true
            ));
        }

        String script = String.format("""
            tell application "Calendar"
                tell calendar "%s"
                    delete (events whose summary is "%s")
                end tell
            end tell
            """, calendar, title);

        executeAppleScript(script);

        return Flux.just(new McpSchema.CallToolResult(
            List.of(new McpSchema.TextContent("Successfully deleted event: " + title)),
            false
        ));
    }

    protected String executeAppleScript(String script) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("osascript");
        command.add("-e");
        command.add(script);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        if (process.waitFor() != 0) {
            throw new Exception("AppleScript execution failed: " + output);
        }

        return output.toString().trim();
    }

    private String formatDateForAppleScript(String dateStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}