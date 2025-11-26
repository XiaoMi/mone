package run.mone.hive.memory;

import run.mone.hive.schema.Message;
import java.util.List;

/**
 * 消息清退策略接口
 * 定义了不同的消息清退算法
 */
public interface EvictionPolicy {

    /**
     * 根据策略选择需要清退的消息
     * 
     * @param messages   当前所有消息
     * @param targetSize 目标保留的消息数量
     * @return 需要清退的消息列表
     */
    List<Message> selectMessagesToEvict(List<Message> messages, int targetSize);

    /**
     * 获取策略名称
     * 
     * @return 策略名称
     */
    String getPolicyName();

    /**
     * 判断消息是否应该被保护（不被清退）
     * 
     * @param message 消息
     * @return true表示应该保护，false表示可以清退
     */
    default boolean isProtected(Message message) {
        return false;
    }
}