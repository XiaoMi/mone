package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.roles.ReactorRole;

/**
 * Thinking tool for expressing analysis and reasoning process
 * <p>
 * This tool allows the AI to explicitly express its thought process, analysis,
 * and reasoning before taking actions. It helps in:
 * - Breaking down complex problems
 * - Analyzing available information and identifying gaps
 * - Evaluating different approaches
 * - Making informed decisions about next steps
 * <p>
 * This is a non-executable tool used purely for communication and clarity.
 *
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class ThinkingTool implements ITool {

    public static final String name = "thinking";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        // This tool doesn't need actual execution, it's for expressing thought process
        return false;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                Use this tool to express your internal thought process, analysis, and reasoning before taking action.
                This helps assess available information, evaluate approaches, and make informed decisions.
                """;
    }

    @Override
    public String parameters() {
        return """
                - content: (required) Your thought process and analysis
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                Checklist here (optional)
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <thinking>
                <content>Your detailed thought process and analysis here</content>
                %s
                </thinking>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Analyzing task and choosing tools
                <thinking>
                <content>
                I need to assess what information I have and what I need:
                - Current task: Add validation method to UserService
                - Known: File location is src/main/java/service/UserService.java
                - Unknown: Existing validation patterns in the codebase
                - Next step: Read the file first to understand current structure
                </content>
                </thinking>

                Example 2: Evaluating approaches
                <thinking>
                <content>
                Two options for this implementation:
                1. Modify existing class - simpler but class is already large
                2. Create new service - better design but more setup
                Decision: Option 2 for better maintainability
                </content>
                </thinking>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Validate required parameters
            if (!inputJson.has("content") || StringUtils.isBlank(inputJson.get("content").getAsString())) {
                log.warn("thinking operation missing content parameter");
                result.addProperty("error", "Missing required parameter 'content'");
                return result;
            }

            String content = inputJson.get("content").getAsString();

            // Since this is a non-executable tool, we just acknowledge the thought process
            result.addProperty("acknowledged", true);
            result.addProperty("content", content);
            result.addProperty("message", "Thought process recorded");

            log.debug("Thinking process: {}", content);

            return result;

        } catch (Exception e) {
            log.error("Exception occurred while processing thinking operation", e);
            result.addProperty("error", "Failed to process thinking operation: " + e.getMessage());
            return result;
        }
    }
}