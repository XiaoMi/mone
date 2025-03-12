package run.mone.mcp.idea.composer.handler;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 14:24
 */
@Data
public class ChatMessage {

    private final String id;
    private final String role;  // system, user, assistant
    private final String content;
    private final LocalDateTime timestamp;
    private final String handlerId; // 标识是哪个handler产生的消息

    public ChatMessage(String role, String content, String handlerId) {
        this.id = UUID.randomUUID().toString();
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.handlerId = handlerId;
    }

}
