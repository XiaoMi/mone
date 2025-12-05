package run.mone.hive.markdown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownParserServiceTest {

    private MarkdownParserService service;

    @BeforeEach
    void setUp() {
        service = new MarkdownParserService();
    }

    @Test
    void testParseMarkdownWithFrontMatter() throws IOException {
        String markdown = """
                ---
                name: generating-commit-messages
                description: Generates clear commit messages from git diffs. Use when writing commit
                ---

                # Generating Commit Messages

                ## Instructions

                1. Run `git diff --staged` to see changes
                2. I'll suggest a commit message with:
                   - Summary under 50 characters
                   - Detailed description
                   - Affected components
                """;

        MarkdownDocument doc = service.parseMarkdown(markdown);

        assertNotNull(doc);
        assertNotNull(doc.getDefinitions());
        assertEquals("generating-commit-messages", doc.getDefinitionAsString("name"));
        assertTrue(doc.getDefinitionAsString("description").contains("Generates clear commit messages"));

        assertNotNull(doc.getContent());
        assertTrue(doc.getContent().contains("# Generating Commit Messages"));
        assertTrue(doc.getContent().contains("## Instructions"));
    }

    @Test
    void testParseMarkdownWithoutFrontMatter() throws IOException {
        String markdown = """
                # Generating Commit Messages

                ## Instructions

                1. Run `git diff --staged` to see changes
                """;

        MarkdownDocument doc = service.parseMarkdown(markdown);

        assertNotNull(doc);
        assertTrue(doc.getDefinitions().isEmpty());
        assertEquals(markdown.trim(), doc.getContent());
    }

    @Test
    void testHasFrontMatter() {
        String withFrontMatter = """
                ---
                name: test
                ---
                content
                """;

        String withoutFrontMatter = "# Just content";

        assertTrue(service.hasFrontMatter(withFrontMatter));
        assertFalse(service.hasFrontMatter(withoutFrontMatter));
    }

    @Test
    void testExtractFrontMatter() throws IOException {
        String markdown = """
                ---
                name: test-name
                description: test description
                version: 1.0
                ---
                content
                """;

        Map<String, Object> frontMatter = service.extractFrontMatter(markdown);

        assertNotNull(frontMatter);
        assertEquals("test-name", frontMatter.get("name"));
        assertEquals("test description", frontMatter.get("description"));
        assertEquals(1.0, frontMatter.get("version")); // YAML parses 1.0 as Double
    }

    @Test
    void testExtractContent() {
        String markdown = """
                ---
                name: test
                ---

                # Main Content

                This is the main content.
                """;

        String content = service.extractContent(markdown);

        assertNotNull(content);
        assertTrue(content.contains("# Main Content"));
        assertTrue(content.contains("This is the main content."));
        assertFalse(content.contains("---"));
    }

    @Test
    void testParseEmptyMarkdown() throws IOException {
        MarkdownDocument doc = service.parseMarkdown("");

        assertNotNull(doc);
        assertTrue(doc.getDefinitions().isEmpty());
    }

    @Test
    void testParseNullMarkdown() throws IOException {
        MarkdownDocument doc = service.parseMarkdown(null);

        assertNotNull(doc);
        assertTrue(doc.getDefinitions().isEmpty());
    }

    @Test
    void testParseFile() throws IOException {
        // Create a temporary markdown file
        Path tempFile = Files.createTempFile("test", ".md");
        try {
            String content = """
                    ---
                    name: test-file
                    description: Test file description
                    ---

                    # Test Content

                    This is test content from file.
                    """;
            Files.writeString(tempFile, content);

            MarkdownDocument doc = service.parseFile(tempFile);

            assertNotNull(doc);
            assertEquals("test-file", doc.getDefinitionAsString("name"));
            assertEquals("Test file description", doc.getDefinitionAsString("description"));
            assertTrue(doc.getContent().contains("# Test Content"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testComplexYamlFrontMatter() throws IOException {
        String markdown = """
                ---
                name: complex-example
                description: A complex example with multiple fields
                tags:
                  - java
                  - markdown
                  - parser
                author:
                  name: John Doe
                  email: john@example.com
                version: 2.0.1
                ---

                # Complex Example
                """;

        MarkdownDocument doc = service.parseMarkdown(markdown);

        assertNotNull(doc);
        assertEquals("complex-example", doc.getDefinitionAsString("name"));

        // Test nested structure
        Object author = doc.getDefinition("author");
        assertNotNull(author);
        assertTrue(author instanceof Map);
    }
}
