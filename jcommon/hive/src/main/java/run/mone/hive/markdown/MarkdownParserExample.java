package run.mone.hive.markdown;

import java.io.IOException;

/**
 * Example usage of MarkdownParserService
 */
public class MarkdownParserExample {

    public static void main(String[] args) {
        // Create parser service
        MarkdownParserService service = new MarkdownParserService();

        // Example markdown with YAML front matter (like your image)
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

                ## Best practices

                - Use present tense
                - Explain what and why, not how
                """;

        try {
            // Parse the markdown
            MarkdownDocument doc = service.parseMarkdown(markdown);

            // Access definitions (YAML front matter)
            System.out.println("=== Definitions ===");
            System.out.println("Name: " + doc.getDefinitionAsString("name"));
            System.out.println("Description: " + doc.getDefinitionAsString("description"));
            System.out.println();

            // Access content
            System.out.println("=== Content ===");
            System.out.println(doc.getContent());
            System.out.println();

            // Check if has front matter
            System.out.println("Has front matter: " + service.hasFrontMatter(markdown));

            // Parse from file
            // MarkdownDocument fileDoc = service.parseFile("/path/to/your/file.md");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
