package run.mone.mcp.idea.composer.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 14:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private String id;
    private String role;  // system, user, assistant
    private String content;
    private LocalDateTime timestamp;
    private String handlerId; // 标识是哪个handler产生的消息

    public ChatMessage(String role, String content, String handlerId) {
        this.id = UUID.randomUUID().toString();
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.handlerId = handlerId;
    }

}
