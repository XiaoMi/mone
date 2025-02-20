package run.mone.mcp.linuxagent.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.linuxagent.McpTool;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class KeyboardInputString implements McpTool {

    private String name = "keyboard_input_string";
    private String desc = "模拟键盘输入一个字符串";
    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "text": {"type": "string", "description": "要输入的字符串"}
                },
                "required": ["text"]
            }
            """;
    private final Gson gson = new Gson();

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String text = (String) arguments.get("text");
        String linuxServerUrl = System.getenv("LINUX_SERVER_URL");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(linuxServerUrl + "/keyboard_input_string");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(gson.toJson(Map.of("text", text)), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Failed to input string: " + EntityUtils.toString(entity));
            }
            JsonObject jsonResponse = gson.fromJson(EntityUtils.toString(entity), JsonObject.class);
            String base64Str = jsonResponse.get("data").getAsString();
            String mimeType = jsonResponse.get("mime_type").getAsString();
            String description = jsonResponse.get("description").getAsString();
            String question = jsonResponse.get("question").getAsString();
            return new McpSchema.CallToolResult(List.of(new McpSchema.ImageContent(null, null, "image", base64Str, mimeType), new McpSchema.TextContent(description), new McpSchema.TextContent(question)), false);
        } catch (IOException e) {
            log.error("Error in keyboardInputString", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error in keyboardInputString: " + e.getMessage())), true);
        }
    }
}
