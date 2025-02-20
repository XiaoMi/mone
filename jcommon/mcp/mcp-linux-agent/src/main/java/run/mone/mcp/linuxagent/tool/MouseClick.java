package run.mone.mcp.linuxagent.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.linuxagent.llm.Gemini;
import run.mone.mcp.linuxagent.McpTool;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class MouseClick implements McpTool {

    private String name = "mouse_click";
    private String desc = "模拟鼠标点击";
    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "object_description": {"type": "string", "description": "图像中对象的描述。必须是一个明确，准确且唯一的位置描述，不能有任何可能在识别过程中造成歧义的内容"}
                },
                "required": ["object_description"]
            }
            """;
    private final Gson gson = new Gson();

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String objectDescription = (String) arguments.get("object_description");
        String linuxServerUrl = System.getenv("LINUX_SERVER_URL");
        String geminiApiBaseUrl = System.getenv("GEMINI_API_BASE_URL");
        String geminiApiKey = System.getenv("GEMINI_API_KEY");
        Gemini gemini = new Gemini(geminiApiBaseUrl, geminiApiKey, linuxServerUrl);

        try {
            Map<String, Object> location = gemini.getObjectLocation(objectDescription);
            if (location.containsKey("error")) {
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent((String) location.get("error"))), true);
            }
            int x = (int) location.get("x");
            int y = (int) location.get("y");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost moveMouseRequest = new HttpPost(linuxServerUrl + "/move_mouse_to");
            moveMouseRequest.setHeader("Content-Type", "application/json");
            moveMouseRequest.setEntity(new StringEntity(gson.toJson(Map.of("x", x, "y", y)), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse moveMouseResponse = httpClient.execute(moveMouseRequest)) {
                // 可以选择检查 moveMouseResponse 的状态码
            }

            HttpGet clickRequest = new HttpGet(linuxServerUrl + "/mouse_click?object_description=" + objectDescription);
            try (CloseableHttpResponse clickResponse = httpClient.execute(clickRequest)) {
                HttpEntity entity = clickResponse.getEntity();
                if (clickResponse.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("Failed to click mouse: " + EntityUtils.toString(entity));
                }
                JsonObject jsonResponse = gson.fromJson(EntityUtils.toString(entity), JsonObject.class);
                String base64Str = jsonResponse.get("data").getAsString();
                String mimeType = jsonResponse.get("mime_type").getAsString();
                String description = jsonResponse.get("description").getAsString();
                String question = jsonResponse.get("question").getAsString();
                return new McpSchema.CallToolResult(List.of(new McpSchema.ImageContent(null, null, "image", base64Str, mimeType), new McpSchema.TextContent(description), new McpSchema.TextContent(question)), false);
            }
        } catch (IOException e) {
            log.error("Error in mouseClick", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error in mouseClick: " + e.getMessage())), true);
        }
    }
}
