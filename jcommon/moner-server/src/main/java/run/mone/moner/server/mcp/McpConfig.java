package run.mone.moner.server.mcp;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.EnumMap;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class McpConfig {
    
    public String getMcpPath(FromType fromType) {
        return fromType.getFilePath();
    }

    public String getMcpModelPath(FromType fromType) {
        return fromType.getModelFilePath();
    }

    public String getMcpDir() {
        return System.getProperty("user.home") + "/.mcp";
    }
    
    public Map<FromType, String> getAllMcpPaths() {
        return Arrays.stream(FromType.values())
                .collect(Collectors.toMap(
                    type -> type,
                    FromType::getFilePath,
                    (a, b) -> a,  // 如果有重复的key，保留第一个
                    () -> new EnumMap<>(FromType.class)
                ));
    }

    public Map<FromType, String> getAllMcpModelPaths() {
        return Arrays.stream(FromType.values())
                .collect(Collectors.toMap(
                    type -> type,
                    FromType::getModelFilePath,
                    (a, b) -> a,  // 如果有重复的key，保留第一个
                    () -> new EnumMap<>(FromType.class)
                ));
    }
} 