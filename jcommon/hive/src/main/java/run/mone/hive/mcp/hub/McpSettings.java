
package run.mone.hive.mcp.hub;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.m78.client.util.GsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Getter
public class McpSettings {

    private Map<String, ServerParameters> mcpServers;

    public static McpSettings fromFile(Path path) throws IOException {
        String content = new String(Files.readAllBytes(path));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, McpSettings.class);
    }

    @SneakyThrows
    public static McpSettings fromContent(String content) {
        content = JsonParser.parseString(content).getAsJsonObject().get("mcpServers").toString();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Map<String, ServerParameters>> typeRef = new TypeReference<>() {};
        Map<String, ServerParameters> mcpServers = mapper.readValue(content, typeRef);
        McpSettings ms = new McpSettings();
        ms.setMcpServers(mcpServers);
        return ms;
    }

    public void setMcpServers(Map<String, ServerParameters> mcpServers) {
        this.mcpServers = mcpServers;
    }
}
