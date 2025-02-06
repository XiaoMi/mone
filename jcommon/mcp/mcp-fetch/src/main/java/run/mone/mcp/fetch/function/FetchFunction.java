
package run.mone.mcp.fetch.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class FetchFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "fetchOperation";

    private String desc = "Fetch content from a provided URL using HTTP GET request";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "url": {
                        "type": "string",
                        "description": "The URL to fetch content from"
                    }
                },
                "required": ["url"]
            }
            """;

    private RestTemplate restTemplate;

    public FetchFunction() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String url = (String) arguments.get("url");

        log.info("Fetching content from URL: {}", url);

        try {
            String result = restTemplate.getForObject(url, String.class);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }
}
