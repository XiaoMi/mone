package run.mone.agentx.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ConfigRequest {
    private String key;
    private String value;
    private Map<String, String> configs;
} 