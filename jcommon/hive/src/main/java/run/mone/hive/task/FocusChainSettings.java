package run.mone.hive.task;

/**
 * Focus Chain配置设置
 * 对应Cline中的FocusChainSettings接口
 */
public class FocusChainSettings {
    
    /**
     * 是否启用Focus Chain功能
     */
    private boolean enabled;
    
    /**
     * 提醒Cline更新待办列表的间隔（API请求次数）
     * 默认值: 6
     * 范围: 1-100
     */
    private int remindClineInterval;
    
    public FocusChainSettings() {
        this.enabled = false;
        this.remindClineInterval = 6;
    }
    
    public FocusChainSettings(boolean enabled, int remindClineInterval) {
        this.enabled = enabled;
        this.remindClineInterval = Math.max(1, Math.min(100, remindClineInterval));
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getRemindClineInterval() {
        return remindClineInterval;
    }
    
    public void setRemindClineInterval(int remindClineInterval) {
        this.remindClineInterval = Math.max(1, Math.min(100, remindClineInterval));
    }
    
    @Override
    public String toString() {
        return "FocusChainSettings{" +
                "enabled=" + enabled +
                ", remindClineInterval=" + remindClineInterval +
                '}';
    }
}
