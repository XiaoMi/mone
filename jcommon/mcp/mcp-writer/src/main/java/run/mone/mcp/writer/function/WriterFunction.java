package run.mone.mcp.writer.function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.writer.service.WriterService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WriterFunction implements McpFunction {

    private final WriterService writerService;

    private static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["expandArticle", "summarizeArticle", "writeNewArticle", "polishArticle", "suggestImprovements", "createOutline", "editArticle", "translateText", "generateCreativeIdeas", "createCharacterProfile", "analyzeWritingStyle", "generateSeoContent", "createResearchSummary", "rewriteForAudience", "generateDialogue", "createMetaphorsAndAnalogies", "tellJoke"],
                        "description": "The writing operation to perform"
                    },
                    "originalRequest": {
                        "type": "string",
                        "description": "The user's original request or query to help AI better understand the intent"
                    },
                    "article": {
                        "type": "string",
                        "description": "The article content for operations that require an existing article"
                    },
                    "topic": {
                        "type": "string",
                        "description": "The topic for writing a new article or creating an outline"
                    },
                    "instructions": {
                        "type": "string",
                        "description": "Editing instructions for the editArticle operation"
                    },
                    "text": {
                        "type": "string",
                        "description": "The text content for operations like translateText or analyzeWritingStyle"
                    },
                    "targetLanguage": {
                        "type": "string",
                        "description": "The target language for translation"
                    },
                    "numberOfIdeas": {
                        "type": "integer",
                        "description": "Number of ideas to generate for generateCreativeIdeas operation"
                    },
                    "characterDescription": {
                        "type": "string",
                        "description": "Description for creating a character profile"
                    },
                    "keyword": {
                        "type": "string",
                        "description": "Keyword for SEO content generation"
                    },
                    "contentType": {
                        "type": "string",
                        "description": "Type of content to generate for SEO (e.g., blog post, product description)"
                    },
                    "researchText": {
                        "type": "string",
                        "description": "Research content to summarize"
                    },
                    "content": {
                        "type": "string",
                        "description": "Content to rewrite for a specific audience"
                    },
                    "targetAudience": {
                        "type": "string",
                        "description": "Target audience for content rewriting"
                    },
                    "scenario": {
                        "type": "string",
                        "description": "Scenario for dialogue generation"
                    },
                    "numberOfExchanges": {
                        "type": "string",
                        "description": "Number of dialogue exchanges to generate"
                    },
                    "concept": {
                        "type": "string",
                        "description": "Concept for creating metaphors and analogies"
                    },
                    "count": {
                        "type": "string",
                        "description": "Number of metaphors/analogies to generate"
                    },
                    "jokeType": {
                        "type": "string",
                        "description": "Type of joke to tell (e.g., political, workplace, family-friendly)"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String originalRequest = (String) arguments.get("originalRequest");

        return Flux.defer(() -> {
            try {
                Flux<String> result = switch (operation) {
                    case "expandArticle" -> writerService.expandArticle((String) arguments.get("article"), originalRequest);
                    case "summarizeArticle" -> writerService.summarizeArticle((String) arguments.get("article"), originalRequest);
                    case "writeNewArticle" -> writerService.writeNewArticle((String) arguments.get("topic"), arguments);
                    case "polishArticle" -> writerService.polishArticle((String) arguments.get("article"));
                    case "suggestImprovements" -> writerService.suggestImprovements((String) arguments.get("article"));
                    case "createOutline" -> writerService.createOutline((String) arguments.get("topic"));
                    case "editArticle" ->
                            writerService.editArticle((String) arguments.get("article"), (String) arguments.get("instructions"));
                    case "translateText" ->
                            writerService.translateText((String) arguments.get("text"), (String) arguments.get("targetLanguage"));
                    case "generateCreativeIdeas" -> writerService.generateCreativeIdeas(
                            (String) arguments.get("topic"),
                            arguments.get("numberOfIdeas"));
                    case "createCharacterProfile" ->
                            writerService.createCharacterProfile((String) arguments.get("characterDescription"));
                    case "analyzeWritingStyle" -> writerService.analyzeWritingStyle((String) arguments.get("text"));
                    case "generateSeoContent" -> writerService.generateSeoContent(
                            (String) arguments.get("keyword"),
                            (String) arguments.get("contentType"));
                    case "createResearchSummary" ->
                            writerService.createResearchSummary((String) arguments.get("researchText"));
                    case "rewriteForAudience" -> writerService.rewriteForAudience(
                            (String) arguments.get("content"),
                            (String) arguments.get("targetAudience"));
                    case "generateDialogue" -> writerService.generateDialogue(
                            (String) arguments.get("scenario"),
                            ((Number) arguments.get("numberOfExchanges")).intValue());
                    case "createMetaphorsAndAnalogies" -> writerService.createMetaphorsAndAnalogies(
                            (String) arguments.get("concept"),
                            ((String) arguments.get("count")));
                    case "tellJoke" -> writerService.tellJoke((String) arguments.get("jokeType"));
                    default -> throw new IllegalArgumentException("Unknown operation: " + operation);
                };
                return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
            } catch (Exception e) {
                return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
            }
        });
    }

    public String getName() {
        return "stream_writer";
    }

    public String getDesc() {
        return "Perform various writing operations including expanding, summarizing, writing new articles, polishing, suggesting improvements, creating outlines, editing articles, translating text, generating creative ideas, creating character profiles, analyzing writing styles, generating SEO content, creating research summaries, rewriting for specific audiences, generating dialogues, creating metaphors and analogies, and telling jokes.";
    }


    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}