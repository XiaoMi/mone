package run.mone.hive.memory;

import run.mone.hive.schema.Message;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LRU（最近最少使用）清退策略
 * 优先清退最近最少被访问的消息
 */
public class LRUEvictionPolicy implements EvictionPolicy {

    // 记录消息的访问时间
    private final Map<String, Long> accessTimes = new ConcurrentHashMap<>();

    @Override
    public List<Message> selectMessagesToEvict(List<Message> messages, int targetSize) {
        List<Message> toEvict = new ArrayList<>();

        if (messages.size() <= targetSize) {
            return toEvict;
        }

        int evictCount = messages.size() - targetSize;

        // 按访问时间排序，最少使用的在前面
        List<Message> sortedMessages = new ArrayList<>(messages);
        sortedMessages
                .sort(Comparator.comparingLong(msg -> accessTimes.getOrDefault(msg.getId(), msg.getCreateTime())));

        // 选择最少使用的消息进行清退，但跳过受保护的消息
        for (Message message : sortedMessages) {
            if (toEvict.size() >= evictCount) {
                break;
            }
            if (!isProtected(message)) {
                toEvict.add(message);
            }
        }

        return toEvict;
    }

    @Override
    public String getPolicyName() {
        return "LRU";
    }

    /**
     * 记录消息被访问
     * 
     * @param messageId 消息ID
     */
    public void recordAccess(String messageId) {
        accessTimes.put(messageId, System.currentTimeMillis());
    }

    /**
     * 清理已删除消息的访问记录
     * 
     * @param messageId 消息ID
     */
    public void removeAccessRecord(String messageId) {
        accessTimes.remove(messageId);
    }

    /**
     * 清空所有访问记录
     */
    public void clearAllAccessRecords() {
        accessTimes.clear();
    }

    @Override
    public boolean isProtected(Message message) {
        // 保护系统消息和重要角色的消息
        if (message.getRole() != null) {
            String role = message.getRole().toLowerCase();
            if (role.contains("system") || role.contains("admin") || role.contains("user")) {
                return true;
            }
        }

        // 保护最近创建的消息（1小时内）
        long oneHourAgo = System.currentTimeMillis() - 3600000; // 1小时
        if (message.getCreateTime() > oneHourAgo) {
            return true;
        }

        // 保护包含重要关键词的消息
        if (message.getContent() != null) {
            String content = message.getContent().toLowerCase();
            if (content.contains("重要") || content.contains("关键") ||
                    content.contains("important") || content.contains("critical")) {
                return true;
            }
        }

        return false;
    }
}