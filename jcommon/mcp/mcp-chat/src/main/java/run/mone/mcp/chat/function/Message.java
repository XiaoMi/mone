package run.mone.mcp.chat.function;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;  // "user" or "assistant"
    private String content;
    private Instant time;
    
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.time = Instant.now();
    }
}
