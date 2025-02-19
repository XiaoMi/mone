package run.mone.mcp.calendar.function;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalendarFunctionTest {

    private CalendarFunction calendarFunction;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        calendarFunction = new CalendarFunction();
    }

    @Test
    void testCreateEvent() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        
        Map<String, Object> args = new HashMap<>();
        args.put("command", "create");
        args.put("title", "Test Meeting");
        args.put("startDate", startTime.format(formatter));
        args.put("endDate", endTime.format(formatter));
        args.put("location", "Test Room");
        args.put("notes", "Test Notes");

        // 执行测试
        McpSchema.CallToolResult result = calendarFunction.apply(args);

        // 打印结果
        System.out.println("Create Event Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testListEvents() {
        // 准备测试数据
        Map<String, Object> args = new HashMap<>();
        args.put("command", "list");
        args.put("startDate", LocalDateTime.now().format(formatter));

        // 执行测试
        McpSchema.CallToolResult result = calendarFunction.apply(args);

        // 打印结果
        System.out.println("List Events Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testDeleteEvent() {
        // 先创建一个事件
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = startTime.plusHours(1);
        
        Map<String, Object> createArgs = new HashMap<>();
        createArgs.put("command", "create");
        createArgs.put("title", "Event To Delete");
        createArgs.put("startDate", startTime.format(formatter));
        createArgs.put("endDate", endTime.format(formatter));
        
        calendarFunction.apply(createArgs);

        // 删除该事件
        Map<String, Object> deleteArgs = new HashMap<>();
        deleteArgs.put("command", "delete");
        deleteArgs.put("title", "Event To Delete");

        McpSchema.CallToolResult result = calendarFunction.apply(deleteArgs);

        // 打印结果
        System.out.println("Delete Event Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testInvalidCommand() {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "invalid");

        McpSchema.CallToolResult result = calendarFunction.apply(args);

        // 打印结果
        System.out.println("Invalid Command Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testMissingRequiredFields() {
        // 创建事件时缺少必要字段
        Map<String, Object> args = new HashMap<>();
        args.put("command", "create");
        // 没有提供 title 和 dates

        McpSchema.CallToolResult result = calendarFunction.apply(args);

        // 打印结果
        System.out.println("Missing Fields Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testDateFormatValidation() {
        Map<String, Object> args = new HashMap<>();
        args.put("command", "create");
        args.put("title", "Test Event");
        args.put("startDate", "invalid-date-format");
        args.put("endDate", "invalid-date-format");

        McpSchema.CallToolResult result = calendarFunction.apply(args);

        // 打印结果
        System.out.println("Invalid Date Format Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testCreateEventWithDefaultValues() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        
        Map<String, Object> args = new HashMap<>();
        args.put("command", "create");
        args.put("title", "Test Event");
        args.put("startDate", startTime.format(formatter));
        args.put("endDate", endTime.format(formatter));
        // 不提供 location 和 notes，使用默认值

        McpSchema.CallToolResult result = calendarFunction.apply(args);

        // 打印结果
        System.out.println("Create Event with Defaults Result: " + gson.toJson(result));
        
        // 验证结果
        assertNotNull(result);
    }

    @Test
    void testSimpleAppleScript() throws Exception {
        String script = """
            tell application "Calendar"
                get name of calendars
            end tell
            """;
        
        String result = calendarFunction.executeAppleScript(script);
        System.out.println("Available calendars: " + result);
        assertNotNull(result);
    }
} 