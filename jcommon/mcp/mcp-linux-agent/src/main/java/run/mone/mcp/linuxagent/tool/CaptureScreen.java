package run.mone.mcp.linuxagent.tool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
public class CaptureScreen implements McpTool {

    private String name = "capture_screen";
    private String desc = "全屏截图";
    private String toolScheme = "{\"type\": \"object\"}";
    private final Gson gson = new Gson();

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String linuxServerUrl = System.getenv("LINUX_SERVER_URL");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(linuxServerUrl + "/capture_screen");
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Failed to capture screen: " + EntityUtils.toString(entity));
            }
            JsonObject jsonResponse = gson.fromJson(EntityUtils.toString(entity), JsonObject.class);
            String base64Str = jsonResponse.get("data").getAsString();
            String mimeType = jsonResponse.get("mime_type").getAsString();
            String description = jsonResponse.get("description").getAsString();
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(description), new McpSchema.ImageContent(null, null, "image", base64Str, mimeType)), false);
        } catch (IOException e) {
            log.error("Error capturing screen", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error capturing screen: " + e.getMessage())), true);
        }
    }
}
