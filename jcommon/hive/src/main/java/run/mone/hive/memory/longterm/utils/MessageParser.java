package run.mone.hive.memory.longterm.utils;

import run.mone.hive.memory.longterm.model.Message;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 消息解析工具类
 * 处理各种消息格式的解析
 */
public class MessageParser {
    
    /**
     * 解析消息对象为消息列表
     * 
     * @param messages 可以是字符串、Map或List
     * @return 消息列表
     */
    public static List<Message> parseMessages(Object messages) {
        List<Message> messageList = new ArrayList<>();
        
        if (messages instanceof String) {
            // 字符串消息转换为用户消息
            messageList.add(Message.builder()
                .role(Message.Role.USER)
                .content((String) messages)
                .build());
                
        } else if (messages instanceof Map) {
            // 单个消息Map
            @SuppressWarnings("unchecked")
            Map<String, Object> messageMap = (Map<String, Object>) messages;
            messageList.add(Message.fromMap(messageMap));
            
        } else if (messages instanceof List) {
            // 消息列表
            @SuppressWarnings("unchecked")
            List<Object> messagesList = (List<Object>) messages;
            
            for (Object msgObj : messagesList) {
                if (msgObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> messageMap = (Map<String, Object>) msgObj;
                    messageList.add(Message.fromMap(messageMap));
                } else if (msgObj instanceof String) {
                    messageList.add(Message.builder()
                        .role(Message.Role.USER)
                        .content((String) msgObj)
                        .build());
                }
            }
        } else {
            throw new IllegalArgumentException("Messages must be String, Map, or List");
        }
        
        return messageList;
    }
    
    /**
     * 将消息列表转换为字符串格式
     * 
     * @param messages 消息列表
     * @return 格式化的字符串
     */
    public static String parseMessagesToString(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        
        for (Message message : messages) {
            if (message.isSystemMessage()) {
                continue; // 跳过系统消息
            }
            
            sb.append(message.getRole().getValue())
              .append(": ")
              .append(message.getContent())
              .append("\n");
        }
        
        return sb.toString().trim();
    }
    
    /**
     * 过滤掉系统消息
     * 
     * @param messages 原始消息列表
     * @return 过滤后的消息列表
     */
    public static List<Message> filterSystemMessages(List<Message> messages) {
        return messages.stream()
            .filter(msg -> !msg.isSystemMessage())
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 处理视觉消息（占位符实现）
     * 
     * @param messages 消息列表
     * @return 处理后的消息列表
     */
    public static List<Message> parseVisionMessages(List<Message> messages) {
        // TODO: 实现视觉消息处理逻辑
        return messages;
    }
}
