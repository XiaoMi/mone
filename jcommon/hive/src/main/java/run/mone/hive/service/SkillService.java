package run.mone.hive.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import run.mone.hive.bo.SkillDocument;
import run.mone.hive.configs.Const;
import run.mone.hive.markdown.MarkdownDocument;
import run.mone.hive.markdown.MarkdownParserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service for loading and managing skill definitions
 * Skills are typically stored in the .hive/skills directory
 *
 * Supports multiple path sources with priority:
 * 1. roleConfig dynamic configuration (highest priority)
 * 2. Spring configuration (hive.skills.path)
 * 3. Default: hiveCwd + skills directory
 */
@Slf4j
public class SkillService {

    private static final String SKILLS_DIR = "skills";
    private final MarkdownParserService markdownParser;

    /**
     * Spring configuration for skills path
     */
    @Setter
    @Value("${hive.skills.path:}")
    private String springSkillsPath;

    public SkillService() {
        this.markdownParser = new MarkdownParserService();
    }

    /**
     * Load all skills from the .hive/skills directory
     *
     * @param hiveCwd The .hive directory path
     * @return List of skill documents
     */
    public List<SkillDocument> loadSkills(String hiveCwd) {
        return loadSkills(hiveCwd, null);
    }

    /**
     * Load all skills with support for config-based path
     *
     * Priority:
     * 1. roleConfig dynamic configuration (skillsPath key) - highest priority
     * 2. Spring configuration (hive.skills.path)
     * 3. Default: hiveCwd + skills directory
     *
     * @param hiveCwd    The .hive directory path (used as fallback)
     * @param roleConfig The role configuration map containing dynamic settings
     * @return List of skill documents
     */
    public List<SkillDocument> loadSkills(String hiveCwd, Map<String, String> roleConfig) {
        List<SkillDocument> skills = new ArrayList<>();

        // Determine skills path with priority
        String skillsPath = resolveSkillsPath(hiveCwd, roleConfig);

        if (StringUtils.isBlank(skillsPath)) {
            log.debug("Skills path is empty, no skills to load");
            return skills;
        }

        File skillsDir = new File(skillsPath);
        if (!skillsDir.exists() || !skillsDir.isDirectory()) {
            log.debug("Skills directory does not exist: {}", skillsPath);
            return skills;
        }

        // Get all .md files in the skills directory and subdirectories
        List<File> skillFileList = new ArrayList<>();

        File[] entries = skillsDir.listFiles();
        if (entries == null || entries.length == 0) {
            log.debug("No files found in: {}", skillsPath);
            return skills;
        }

        for (File entry : entries) {
            if (entry.isDirectory()) {
                // Look for .md files in subdirectory
                File[] mdFiles = entry.listFiles((dir, name) -> name.toLowerCase().endsWith(".md"));
                if (mdFiles != null && mdFiles.length > 0) {
                    skillFileList.addAll(Arrays.asList(mdFiles));
                }
            } else if (entry.getName().toLowerCase().endsWith(".md")) {
                // Also include .md files directly in skills directory
                skillFileList.add(entry);
            }
        }

        if (skillFileList.isEmpty()) {
            log.debug("No skill files found in: {}", skillsPath);
            return skills;
        }

        // Parse each skill file
        for (File skillFile : skillFileList) {
            try {
                SkillDocument skill = parseSkillFile(skillFile);
                if (skill != null) {
                    skills.add(skill);
                    log.debug("Loaded skill: {} from {}", skill.getName(), skillFile.getName());
                }
            } catch (Exception e) {
                log.warn("Failed to parse skill file: {}, error: {}", skillFile.getName(), e.getMessage());
            }
        }

        return skills;
    }

    /**
     * Parse a skill markdown file
     * Expected format:
     * ---
     * name: skill-name
     * description: Skill description
     * ---
     * Skill content here...
     *
     * @param skillFile Skill file
     * @return SkillDocument or null if parsing fails
     */
    private SkillDocument parseSkillFile(File skillFile) throws IOException {
        String content = Files.readString(skillFile.toPath());
        MarkdownDocument md = markdownParser.parseMarkdown(content);

        String name = md.getDefinitionAsString("name");
        String description = md.getDefinitionAsString("description");

        if (StringUtils.isBlank(name)) {
            log.warn("Skill file missing 'name' field: {}", skillFile.getName());
            return null;
        }

        // Get the skill directory (parent of the skill file)
        File skillDir = skillFile.getParentFile();

        // Collect all files in the skill directory (recursively)
        List<String> files = collectFilesRecursively(skillDir);

        return SkillDocument.builder()
                .name(name)
                .description(StringUtils.isNotBlank(description) ? description : "")
                .location(skillDir.getAbsolutePath())
                .content(md.getContent())
                .files(files)
                .build();
    }

    /**
     * Recursively collect all files in a directory (excluding hidden files/directories starting with '.')
     *
     * @param directory Directory to scan
     * @return List of absolute file paths
     */
    private List<String> collectFilesRecursively(File directory) {
        List<String> filePaths = new ArrayList<>();

        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return filePaths;
        }

        File[] entries = directory.listFiles();
        if (entries == null) {
            return filePaths;
        }

        for (File entry : entries) {
            // Skip hidden files and directories (starting with '.')
            if (entry.getName().startsWith(".")) {
                continue;
            }

            if (entry.isFile()) {
                // Add file's absolute path
                filePaths.add(entry.getAbsolutePath());
            } else if (entry.isDirectory()) {
                // Recursively collect files from subdirectory
                filePaths.addAll(collectFilesRecursively(entry));
            }
        }

        return filePaths;
    }

    /**
     * Format skills for prompt inclusion
     * Returns a formatted string describing available skills and how to use them
     *
     * @param skills List of skills
     * @return Formatted skill information
     */
    public String formatSkillsForPrompt(List<SkillDocument> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Available Skills\n\n");
        sb.append("You have access to the following skills. ");
        sb.append("To request a skill definition, use the XML format:\n\n");
        sb.append("```xml\n");
        sb.append("<skill_request>\n");
        sb.append("<skill_name>skill-name-here</skill_name>\n");
        sb.append("</skill_request>\n");
        sb.append("```\n\n");
        sb.append("### Skill List:\n\n");

        for (SkillDocument skill : skills) {
            sb.append("**").append(skill.getName()).append("**\n");
            if (StringUtils.isNotBlank(skill.getDescription())) {
                sb.append("- Description: ").append(skill.getDescription()).append("\n");
            }
            sb.append("- Location: ").append(skill.getLocation()).append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Get skill by name
     *
     * @param skills List of all skills
     * @param name   Skill name to find
     * @return SkillDocument or null if not found
     */
    public SkillDocument getSkillByName(List<SkillDocument> skills, String name) {
        if (skills == null || StringUtils.isBlank(name)) {
            return null;
        }

        return skills.stream()
                .filter(skill -> name.equals(skill.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Resolve skills path with priority:
     * 1. roleConfig dynamic configuration (skillsPath key) - highest priority
     * 2. Spring configuration (hive.skills.path)
     * 3. Default: hiveCwd + skills directory
     *
     * @param hiveCwd    The .hive directory path (used as fallback)
     * @param roleConfig The role configuration map containing dynamic settings
     * @return The resolved skills path
     */
    private String resolveSkillsPath(String hiveCwd, Map<String, String> roleConfig) {
        // Priority 1: roleConfig dynamic configuration (highest priority)
        if (roleConfig != null && roleConfig.containsKey(Const.SKILLS_PATH_KEY)) {
            String configPath = roleConfig.get(Const.SKILLS_PATH_KEY);
            if (StringUtils.isNotBlank(configPath)) {
                log.debug("Using skills path from roleConfig: {}", configPath);
                return configPath;
            }
        }

        // Priority 2: Spring configuration
        if (StringUtils.isNotBlank(springSkillsPath)) {
            log.debug("Using skills path from Spring configuration: {}", springSkillsPath);
            return springSkillsPath;
        }

        // Priority 3: Default - hiveCwd + skills directory
        if (StringUtils.isBlank(hiveCwd)) {
            return null;
        }

        String defaultPath = hiveCwd
                + (hiveCwd.endsWith(File.separator) ? "" : File.separator)
                + SKILLS_DIR;
        log.debug("Using default skills path: {}", defaultPath);
        return defaultPath;
    }
}
