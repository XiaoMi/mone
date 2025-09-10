package run.mone.hive.task;

/**
 * 任务回调接口
 * 定义各种回调方法
 */
public interface TaskCallbacks {
    
    /**
     * 发送消息回调
     * @param type 消息类型
     * @param message 消息内容
     */
    void say(String type, String message);
    
    /**
     * 更新状态到WebView
     */
    void postStateToWebview();
    
    /**
     * 任务完成回调
     * @param taskId 任务ID
     * @param success 是否成功
     */
    default void onTaskCompleted(String taskId, boolean success) {
        // 默认空实现
    }
    
    /**
     * 进度更新回调
     * @param taskId 任务ID
     * @param progress 进度信息
     */
    default void onProgressUpdated(String taskId, String progress) {
        // 默认空实现
    }
}
