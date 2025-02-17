package run.mone.mcp.applescript.function;

import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppleScriptFunctionTest {
    private final AppleScriptFunction appleScriptFunction = new AppleScriptFunction();

    @Test
    void testLockScreenCommand() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "open location \"https://www.google.com\"");
        McpSchema.CallToolResult result = appleScriptFunction.apply(arguments);
        assertFalse(result.isError());
    }

    @Test
    void testLockScreenCommandError() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");
        arguments.put("customCommand", "error \"Test error\"");
        McpSchema.CallToolResult result = appleScriptFunction.apply(arguments);
        assertTrue(result.isError());
    }

    //获取当天日历日程的单测
    @Test
    void testGetTodaysCalendarEvents() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("command", "custom");

        // AppleScript to get today's events, normalized to midnight
        String appleScript = "tell application \"Calendar\"\n" +
                "    -- Get the current date.\n" +
                "    set today to current date\n" +
                "    -- Set the time of 'today' to midnight (00:00:00).\n" +
                "    set time of today to 0\n" +
                "\n" +
                "    repeat with cal in calendars\n" +
                "        set eventList to \"\"\n" +
                "        tell cal\n" +
                "            -- Get events that start on the current day.\n" +
                "            set theEvents to every event whose start date ≥ today and start date < (today + 1 * days)\n" +
                "            repeat with e in theEvents\n" +
                "                -- Format the date to be more readable (YYYY-MM-DD).\n" +
                "                set eventList to eventList & summary of e & \" (\" & my formatDate(start date of e) & \" - \" & my formatDate(end date of e) & \")\" & return\n" +
                "            end repeat\n" +
                "        end tell\n" +
                "        if eventList is not \"\" then\n" +
                "            log \"Calendar: \" & name of cal & return & eventList\n" +
                "        end if\n" +
                "    end repeat\n" +
                "end tell\n" +
                "\n" +
                "-- Helper function to format the date as YYYY-MM-DD\n" +
                "on formatDate(theDate)\n" +
                "    set the yearStr to year of theDate as text\n" +
                "    set theMonthNum to (month of theDate) as integer\n" +
                "    set the monthStr to text -2 thru -1 of (\"0\" & theMonthNum)\n" +
                "    set the dayStr to text -2 thru -1 of (\"0\" & day of theDate)\n" +
                "    return yearStr & \"-\" & monthStr & \"-\" & dayStr\n" +
                "end formatDate";

        arguments.put("customCommand", appleScript);

        McpSchema.CallToolResult result = appleScriptFunction.apply(arguments);

        System.out.println(result);
        assertFalse(result.isError(), "AppleScript execution should not result in an error.");
    }
}