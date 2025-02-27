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
public class ExecuteCommand implements McpTool {

    private String name = "execute_command";
    private String desc = "执行控制台命令并返回所有标准输出和标准错误";
    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "command": {"type": "string", "description": "要执行的命令"}
                },
                "required": ["command"]
            }
            """;
    private final Gson gson = new Gson();

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String command = (String) arguments.get("command");
        String linuxServerUrl = System.getenv("LINUX_SERVER_URL");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(linuxServerUrl + "/execute_command");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(gson.toJson(Map.of("command", command)), ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Failed to execute command: " + EntityUtils.toString(entity));
            }
            JsonObject jsonResponse = gson.fromJson(EntityUtils.toString(entity), JsonObject.class);
            String output = jsonResponse.get("output").getAsString();
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(output)), false);
        } catch (IOException e) {
            log.error("Error in executeCommand", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error in executeCommand: " + e.getMessage())), true);
        }
    }
}
