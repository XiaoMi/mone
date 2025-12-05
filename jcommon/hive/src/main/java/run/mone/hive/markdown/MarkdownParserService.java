package run.mone.hive.markdown;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for parsing markdown files with YAML front matter
 * Supports markdown files with definitions (YAML front matter) and content sections
 */
public class MarkdownParserService {

    private static final Pattern FRONT_MATTER_PATTERN = Pattern.compile(
            "^---\\s*\\n(.*?)\\n---\\s*\\n(.*)$",
            Pattern.DOTALL
    );

    private final ObjectMapper yamlMapper;

    public MarkdownParserService() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Parse markdown file from file path
     *
     * @param filePath path to markdown file
     * @return parsed MarkdownDocument
     * @throws IOException if file reading or parsing fails
     */
    public MarkdownDocument parseFile(String filePath) throws IOException {
        return parseFile(Path.of(filePath));
    }

    /**
     * Parse markdown file from Path
     *
     * @param path path to markdown file
     * @return parsed MarkdownDocument
     * @throws IOException if file reading or parsing fails
     */
    public MarkdownDocument parseFile(Path path) throws IOException {
        String content = Files.readString(path);
        return parseMarkdown(content);
    }

    /**
     * Parse markdown content string
     *
     * @param markdown markdown content with optional YAML front matter
     * @return parsed MarkdownDocument
     * @throws IOException if YAML parsing fails
     */
    public MarkdownDocument parseMarkdown(String markdown) throws IOException {
        if (markdown == null || markdown.trim().isEmpty()) {
            return new MarkdownDocument();
        }

        MarkdownDocument document = new MarkdownDocument();
        document.setRawMarkdown(markdown);

        Matcher matcher = FRONT_MATTER_PATTERN.matcher(markdown);

        if (matcher.matches()) {
            // Extract YAML front matter
            String yamlContent = matcher.group(1);
            String mainContent = matcher.group(2);

            // Parse YAML definitions
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> definitions = yamlMapper.readValue(yamlContent, Map.class);
                document.setDefinitions(definitions);
            } catch (IOException e) {
                throw new IOException("Failed to parse YAML front matter: " + e.getMessage(), e);
            }

            // Set main content
            document.setContent(mainContent.trim());
        } else {
            // No front matter found, treat entire content as markdown
            document.setContent(markdown.trim());
        }

        return document;
    }

    /**
     * Check if markdown has front matter
     *
     * @param markdown markdown content
     * @return true if front matter exists
     */
    public boolean hasFrontMatter(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return false;
        }
        return FRONT_MATTER_PATTERN.matcher(markdown).matches();
    }

    /**
     * Extract only front matter from markdown
     *
     * @param markdown markdown content
     * @return front matter as Map, empty map if no front matter exists
     * @throws IOException if YAML parsing fails
     */
    public Map<String, Object> extractFrontMatter(String markdown) throws IOException {
        Matcher matcher = FRONT_MATTER_PATTERN.matcher(markdown);
        if (matcher.matches()) {
            String yamlContent = matcher.group(1);
            @SuppressWarnings("unchecked")
            Map<String, Object> definitions = yamlMapper.readValue(yamlContent, Map.class);
            return definitions;
        }
        return Map.of();
    }

    /**
     * Extract only content (without front matter) from markdown
     *
     * @param markdown markdown content
     * @return content without front matter
     */
    public String extractContent(String markdown) {
        Matcher matcher = FRONT_MATTER_PATTERN.matcher(markdown);
        if (matcher.matches()) {
            return matcher.group(2).trim();
        }
        return markdown != null ? markdown.trim() : "";
    }
}
