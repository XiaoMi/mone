package run.mone.hive.a2a.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 表示用户或代理之间的消息交换
 */
@Data
public class Message {
    @JsonProperty("role")
    private String role; // "user" or "agent"
    
    @JsonProperty("parts")
    private List<Part> parts;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    /**
     * 检查role是否为有效值
     */
    public void validate() {
        if (role == null || (!role.equals("user") && !role.equals("agent"))) {
            throw new IllegalArgumentException("Role must be either 'user' or 'agent'");
        }
        
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("Message must contain at least one part");
        }
    }
} 