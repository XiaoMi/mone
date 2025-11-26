package run.mone.hive.memory;

import lombok.Builder;
import lombok.Data;

/**
 * Memory统计信息类
 * 提供内存使用情况的详细统计
 */
@Data
@Builder
public class MemoryStats {

    /**
     * 当前消息总数
     */
    private int totalMessages;

    /**
     * 最大消息数量限制
     */
    private int maxMessages;

    /**
     * 清退次数
     */
    private int evictionCount;

    /**
     * 最后一次清退时间
     */
    private long lastEvictionTime;

    /**
     * 当前使用的清退策略
     */
    private String evictionPolicy;

    /**
     * 估算的内存使用量（字节）
     */
    private long memoryUsageBytes;

    /**
     * 内存使用率（百分比）
     */
    public double getMemoryUsageRatio() {
        if (maxMessages == 0) {
            return 0.0;
        }
        return (double) totalMessages / maxMessages;
    }

    /**
     * 获取格式化的内存使用量
     */
    public String getFormattedMemoryUsage() {
        if (memoryUsageBytes < 1024) {
            return memoryUsageBytes + " B";
        } else if (memoryUsageBytes < 1024 * 1024) {
            return String.format("%.2f KB", memoryUsageBytes / 1024.0);
        } else if (memoryUsageBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", memoryUsageBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", memoryUsageBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 获取最后清退时间的格式化字符串
     */
    public String getFormattedLastEvictionTime() {
        if (lastEvictionTime == 0) {
            return "从未清退";
        }
        return new java.util.Date(lastEvictionTime).toString();
    }

    /**
     * 是否需要清退
     */
    public boolean needsEviction() {
        return getMemoryUsageRatio() > 0.9;
    }

    @Override
    public String toString() {
        return String.format(
                "MemoryStats{messages=%d/%d(%.1f%%), evictions=%d, memory=%s, policy=%s, lastEviction=%s}",
                totalMessages, maxMessages, getMemoryUsageRatio() * 100,
                evictionCount, getFormattedMemoryUsage(), evictionPolicy,
                getFormattedLastEvictionTime());
    }
}