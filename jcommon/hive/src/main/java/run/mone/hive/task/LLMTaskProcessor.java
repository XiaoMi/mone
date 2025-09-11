package run.mone.hive.task;

/**
 * LLM任务处理接口
 * 这里只定义接口，具体实现由调用方提供
 */
public interface LLMTaskProcessor {
    
    /**
     * 发送消息给大模型
     * @param message 要发送的消息内容
     * @return 大模型的响应
     */
    String sendMessage(String message);
    
    /**
     * 发送系统提示和用户消息
     * @param systemPrompt 系统提示
     * @param userMessage 用户消息
     * @return 大模型的响应
     */
    String sendMessage(String systemPrompt, String userMessage);
    
    /**
     * 检查是否支持task_progress参数
     * @return true如果支持，false否则
     */
    default boolean supportsTaskProgress() {
        return true;
    }
}
