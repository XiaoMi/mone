package run.mone.hive.memory;

import lombok.Builder;
import lombok.Data;

/**
 * Memory配置类
 * 用于配置内存管理的各种参数
 */
@Data
@Builder
public class MemoryConfig {

    /**
     * 最大消息数量，超过此数量将触发清退
     */
    @Builder.Default
    private int maxMessages = 1000;

    /**
     * 清退触发阈值，当消息数量达到maxMessages * triggerThreshold时触发清退
     */
    @Builder.Default
    private double triggerThreshold = 0.9;

    /**
     * 清退目标比例，清退后保留的消息数量为maxMessages * targetRatio
     */
    @Builder.Default
    private double targetRatio = 0.7;

    /**
     * 清退策略
     */
    @Builder.Default
    private EvictionPolicy evictionPolicy = new FIFOEvictionPolicy();

    /**
     * 是否启用自动清退
     */
    @Builder.Default
    private boolean autoEvictionEnabled = true;

    /**
     * 清退检查间隔（毫秒）
     */
    @Builder.Default
    private long evictionCheckInterval = 60000; // 1分钟

    /**
     * 是否启用内存使用监控
     */
    @Builder.Default
    private boolean memoryMonitoringEnabled = true;

    /**
     * 内存使用警告阈值（字节）
     */
    @Builder.Default
    private long memoryWarningThreshold = 100 * 1024 * 1024; // 100MB

    /**
     * 获取清退触发的消息数量
     */
    public int getTriggerMessageCount() {
        return (int) (maxMessages * triggerThreshold);
    }

    /**
     * 获取清退后的目标消息数量
     */
    public int getTargetMessageCount() {
        return (int) (maxMessages * targetRatio);
    }

    /**
     * 创建默认配置
     */
    public static MemoryConfig defaultConfig() {
        return MemoryConfig.builder().build();
    }

    /**
     * 创建高容量配置
     */
    public static MemoryConfig highCapacityConfig() {
        return MemoryConfig.builder()
                .maxMessages(5000)
                .triggerThreshold(0.95)
                .targetRatio(0.8)
                .evictionPolicy(new LRUEvictionPolicy())
                .build();
    }

    /**
     * 创建低内存配置
     */
    public static MemoryConfig lowMemoryConfig() {
        return MemoryConfig.builder()
                .maxMessages(200)
                .triggerThreshold(0.8)
                .targetRatio(0.6)
                .evictionPolicy(new FIFOEvictionPolicy())
                .memoryWarningThreshold(20 * 1024 * 1024) // 20MB
                .autoEvictionEnabled(false)
                .build();
    }
}