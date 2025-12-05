package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.bo.SkillDocument;
import run.mone.hive.prompt.MonerSystemPrompt;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.service.SkillService;

import java.util.List;

/**
 * Skill request tool for retrieving skill definitions
 *
 * This tool allows the AI agent to request and retrieve skill definitions that are stored
 * in the .hive/skills directory. Skills are reusable templates that guide the AI in
 * performing specific tasks consistently.
 *
 * Use this tool when you need to:
 * - Get detailed instructions for a specific skill
 * - Follow a standardized process or checklist
 * - Apply best practices defined in a skill template
 *
 * @author goodjava@qq.com
 * @date 2025/12/1
 */
@Slf4j
public class SkillRequestTool implements ITool {

    public static final String name = "skill_request";

    public SkillRequestTool() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String description() {
        return """
                Request the definition of a specific skill. Skills are reusable templates that provide
                detailed instructions, checklists, and best practices for accomplishing specific tasks.

                **When to use this tool:**
                - When you need detailed guidance for a specific type of task
                - To access standardized processes and checklists
                - To follow best practices defined in skill templates
                - When the user mentions a skill by name
                - To get step-by-step instructions for complex operations

                **What this returns:**
                - Complete skill definition including all instructions
                - Structured guidance for the task
                - Checklists and procedures to follow
                - Best practices and considerations

                **Available skills:**
                Skills are loaded from the .hive/skills directory. You will see a list of available
                skills in the SKILLS section of the system prompt. Each skill has a name and description
                that indicates what it helps you accomplish.
                """;
    }

    @Override
    public String parameters() {
        return """
                - skill_name: (required) The name of the skill you want to retrieve
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
                <skill_request>
                <skill_name>skill-name-here</skill_name>
                %s
                </skill_request>
                """.formatted(taskProgress);
    }

    @Override
    public String example() {
        return """
                Example 1: Request a code review skill
                <skill_request>
                <skill_name>code-review</skill_name>
                </skill_request>

                Example 2: Request an API design skill
                <skill_request>
                <skill_name>api-design</skill_name>
                </skill_request>

                Example 3: Request a database schema design skill
                <skill_request>
                <skill_name>db-schema-design</skill_name>
                </skill_request>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // Validate required parameters
            if (!inputJson.has("skill_name") || StringUtils.isBlank(inputJson.get("skill_name").getAsString())) {
                log.error("skill_request operation missing required skill_name parameter");
                result.addProperty("error", "Missing required parameter 'skill_name'");
                return result;
            }

            String skillName = inputJson.get("skill_name").getAsString();
            log.info("Requesting skill: {}", skillName);

            // Load skills from .hive/skills directory (supports config-based path: roleConfig > spring config > default)
            String hiveCwd = MonerSystemPrompt.hiveCwd(role);
            SkillService skillService = SkillService.getInstance();
            List<SkillDocument> skills = skillService.loadSkills(hiveCwd, role.getRoleConfig());

            if (skills.isEmpty()) {
                log.warn("No skills found in directory: {}/skills", hiveCwd);
                result.addProperty("error", "No skills are currently available. Skills should be placed in .hive/skills/ directory.");
                return result;
            }

            // Find the requested skill
            SkillDocument skill = skillService.getSkillByName(skills, skillName);

            if (skill == null) {
                log.warn("Skill not found: {}", skillName);

                // Provide helpful feedback with available skills
                StringBuilder availableSkills = new StringBuilder("Skill '").append(skillName).append("' not found.\n\n");
                availableSkills.append("Available skills:\n");
                for (SkillDocument s : skills) {
                    availableSkills.append("- ").append(s.getName());
                    if (StringUtils.isNotBlank(s.getDescription())) {
                        availableSkills.append(": ").append(s.getDescription());
                    }
                    availableSkills.append("\n");
                }

                result.addProperty("error", availableSkills.toString());
                return result;
            }

            // Return the skill content
            String skillContent = skill.getContent();
            if (StringUtils.isBlank(skillContent)) {
                log.warn("Skill has no content: {}", skillName);
                result.addProperty("error", "Skill '" + skillName + "' exists but has no content defined.");
                return result;
            }

            // Build the result
            result.addProperty("result", skillContent);
            result.addProperty("skillName", skill.getName());
            result.addProperty("skillDescription", skill.getDescription());
            result.addProperty("skillLocation", skill.getLocation());
            result.addProperty("contentLength", skillContent.length());

            // Add file list (all files in the skill directory)
            if (skill.getFiles() != null && !skill.getFiles().isEmpty()) {
                JsonArray filesArray = new JsonArray();
                for (String filePath : skill.getFiles()) {
                    filesArray.add(filePath);
                }
                result.add("files", filesArray);
                result.addProperty("fileCount", skill.getFiles().size());

                log.info("Successfully retrieved skill: {}, content length: {} characters, file count: {}",
                         skillName, skillContent.length(), skill.getFiles().size());
            } else {
                log.info("Successfully retrieved skill: {}, content length: {} characters",
                         skillName, skillContent.length());
            }

            return result;

        } catch (Exception e) {
            log.error("Exception occurred while executing skill_request operation", e);
            result.addProperty("error", "Failed to execute skill_request operation: " + e.getMessage());
            return result;
        }
    }
}
