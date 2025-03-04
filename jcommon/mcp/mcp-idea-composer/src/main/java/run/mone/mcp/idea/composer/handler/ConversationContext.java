package run.mone.mcp.idea.composer.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 14:23
 */
public class ConversationContext {

    private final String conversationId;
    private final List<ChatMessage> messages;
    private final LocalDateTime createTime;
    private Object additionalData; // 可以存储任何额外的上下文数据


    public ConversationContext() {
        this.conversationId = UUID.randomUUID().toString();
        this.messages = Collections.synchronizedList(new ArrayList<>());
        this.createTime = LocalDateTime.now();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List<ChatMessage> getMessagesByHandler(String handlerId) {
        return messages.stream()
                .filter(msg -> handlerId.equals(msg.getHandlerId()))
                .toList();
    }

    public void setAdditionalData(Object data) {
        this.additionalData = data;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

}
