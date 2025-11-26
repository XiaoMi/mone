package run.mone.mcp.image.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.ClaudeProxy;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.image.http.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class ImageFunction implements McpFunction {

    private String name = "stream_image_chat";

    private String desc = "Generate image from text prompt using Imagen 3.0";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "message": {
                        "type": "string",
                        "description": "Text prompt to generate the image"
                    }
                },
                "required": ["message"]
            }
            """;

    private static HttpClient httpClient = new HttpClient();
    private static final Gson gson = new Gson();
    private static final String API_URL = "https://europe-west1-aiplatform.googleapis.com/v1/projects/b2c-mione-gcp-copilot/locations/europe-west1/publishers/google/models/imagen-3.0-generate-002:predict";

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        return Flux.defer(() -> {
            try {
                String prompt = (String) arguments.get("message");
                log.info("Generating image for prompt: {}", prompt);
                String result = generateImage(prompt);
                return Flux.just(
                        new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false),
                        new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("[END]")), false)
                );
            } catch (Exception e) {
                log.error("Failed to generate image", e);
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Operation failed: " + e.getMessage())), true));
            }
        });
    }

    private String getAuthToken() {
        return ClaudeProxy.getClaudeKey("Claude-3.7-Sonnet-us");
    }

    public String generateImage(String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();
        JsonObject instance = new JsonObject();
        instance.addProperty("prompt", prompt);
        
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sampleCount", 1);
        
        requestBody.add("instances", gson.toJsonTree(new Object[]{instance}));
        requestBody.add("parameters", parameters);

        String response = httpClient.post(API_URL, gson.toJson(requestBody), Map.of(
            "Authorization", "Bearer " + getAuthToken(),
            "Content-Type", "application/json"
        ));

        JsonObject responseJson = gson.fromJson(response, JsonObject.class);
        return responseJson.toString();
    }
}
