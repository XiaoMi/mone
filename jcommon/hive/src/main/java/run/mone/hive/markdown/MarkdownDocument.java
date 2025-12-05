package run.mone.hive.markdown;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a markdown document with front matter definitions and content
 */
public class MarkdownDocument {

    /**
     * Front matter definitions (YAML metadata)
     * Common fields: name, description, etc.
     */
    private Map<String, Object> definitions;

    /**
     * Main markdown content (excluding front matter)
     */
    private String content;

    /**
     * Raw markdown text including front matter
     */
    private String rawMarkdown;

    public MarkdownDocument() {
        this.definitions = new HashMap<>();
    }

    public MarkdownDocument(Map<String, Object> definitions, String content) {
        this.definitions = definitions != null ? definitions : new HashMap<>();
        this.content = content;
    }

    public Map<String, Object> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, Object> definitions) {
        this.definitions = definitions;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawMarkdown() {
        return rawMarkdown;
    }

    public void setRawMarkdown(String rawMarkdown) {
        this.rawMarkdown = rawMarkdown;
    }

    /**
     * Get a specific definition value
     */
    public Object getDefinition(String key) {
        return definitions.get(key);
    }

    /**
     * Get definition as String
     */
    public String getDefinitionAsString(String key) {
        Object value = definitions.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Put a definition
     */
    public void putDefinition(String key, Object value) {
        this.definitions.put(key, value);
    }

    @Override
    public String toString() {
        return "MarkdownDocument{" +
                "definitions=" + definitions +
                ", contentLength=" + (content != null ? content.length() : 0) +
                '}';
    }
}
