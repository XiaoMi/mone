package run.mone.hive.memory.longterm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 消息数据模型
 * 对应mem0处理的对话消息格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    /**
     * 消息角色类型
     */
    public enum Role {
        USER("user"),
        ASSISTANT("assistant"),
        SYSTEM("system"),
        TOOL("tool");
        
        private final String value;
        
        Role(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Role fromString(String value) {
            for (Role role : Role.values()) {
                if (role.value.equalsIgnoreCase(value)) {
                    return role;
                }
            }
            return USER; // 默认为用户角色
        }
    }
    
    /**
     * 消息角色
     */
    private Role role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息名称/标识符
     */
    private String name;
    
    /**
     * 工具调用ID
     */
    private String toolCallId;
    
    /**
     * 工具调用信息
     */
    private Map<String, Object> toolCalls;
    
    /**
     * 额外元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 从Map创建Message
     */
    public static Message fromMap(Map<String, Object> messageMap) {
        Message.MessageBuilder builder = Message.builder();
        
        if (messageMap.containsKey("role")) {
            String roleStr = (String) messageMap.get("role");
            builder.role(Role.fromString(roleStr));
        }
        
        if (messageMap.containsKey("content")) {
            builder.content((String) messageMap.get("content"));
        }
        
        if (messageMap.containsKey("name")) {
            builder.name((String) messageMap.get("name"));
        }
        
        if (messageMap.containsKey("tool_call_id")) {
            builder.toolCallId((String) messageMap.get("tool_call_id"));
        }
        
        if (messageMap.containsKey("tool_calls")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> toolCalls = (Map<String, Object>) messageMap.get("tool_calls");
            builder.toolCalls(toolCalls);
        }
        
        return builder.build();
    }
    
    /**
     * 转换为Map格式
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.HashMap<>();
        
        if (role != null) {
            map.put("role", role.getValue());
        }
        
        if (content != null) {
            map.put("content", content);
        }
        
        if (name != null) {
            map.put("name", name);
        }
        
        if (toolCallId != null) {
            map.put("tool_call_id", toolCallId);
        }
        
        if (toolCalls != null) {
            map.put("tool_calls", toolCalls);
        }
        
        return map;
    }
    
    /**
     * 判断是否为系统消息
     */
    public boolean isSystemMessage() {
        return role == Role.SYSTEM;
    }
    
    /**
     * 判断是否为用户消息
     */
    public boolean isUserMessage() {
        return role == Role.USER;
    }
    
    /**
     * 判断是否为助手消息
     */
    public boolean isAssistantMessage() {
        return role == Role.ASSISTANT;
    }
}
