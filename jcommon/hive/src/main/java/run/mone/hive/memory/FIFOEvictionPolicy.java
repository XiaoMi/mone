package run.mone.hive.memory;

import run.mone.hive.schema.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * FIFO（先进先出）清退策略
 * 优先清退最早添加的消息
 */
public class FIFOEvictionPolicy implements EvictionPolicy {

    @Override
    public List<Message> selectMessagesToEvict(List<Message> messages, int targetSize) {
        List<Message> toEvict = new ArrayList<>();

        if (messages.size() <= targetSize) {
            return toEvict;
        }

        int evictCount = messages.size() - targetSize;

        // 从最早的消息开始选择清退，但跳过受保护的消息
        for (int i = 0; i < messages.size() && toEvict.size() < evictCount; i++) {
            Message message = messages.get(i);
            if (!isProtected(message)) {
                toEvict.add(message);
            }
        }

        return toEvict;
    }

    @Override
    public String getPolicyName() {
        return "FIFO";
    }

    @Override
    public boolean isProtected(Message message) {
        // 保护系统消息和重要角色的消息
//        if (message.getRole() != null) {
//            String role = message.getRole().toLowerCase();
//            if (role.contains("system") || role.contains("admin") || role.contains("user")) {
//                return true;
//            }
//        }
//
//        // 保护包含重要关键词的消息
//        if (message.getContent() != null) {
//            String content = message.getContent().toLowerCase();
//            if (content.contains("重要") || content.contains("关键") ||
//                    content.contains("important") || content.contains("critical")) {
//                return true;
//            }
//        }

        return false;
    }
}