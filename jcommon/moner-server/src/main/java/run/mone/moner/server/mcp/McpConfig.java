package run.mone.moner.server.mcp;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class McpConfig {
    @Getter
    private final Map<FromType, String> mcpPaths;
    
    public McpConfig() {
        mcpPaths = new EnumMap<>(FromType.class);
        mcpPaths.put(FromType.ATHENA, System.getProperty("user.home") + "/.mcp/athena_mcp_settings.json");
        mcpPaths.put(FromType.CHROME, System.getProperty("user.home") + "/.mcp/chrome_mcp_settings.json");
    }

    public String getMcpPath(FromType fromType) {
        return mcpPaths.getOrDefault(fromType, mcpPaths.get(FromType.ATHENA));
    }

    public String getMcpDir() {
        return System.getProperty("user.home") + "/.mcp";
    }
} 