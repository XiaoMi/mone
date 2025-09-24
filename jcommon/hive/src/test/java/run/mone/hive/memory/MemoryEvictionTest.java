package run.mone.hive.memory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import run.mone.hive.schema.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Memory清退机制测试类
 */
@Slf4j
public class MemoryEvictionTest {

    private Memory memory;

    @BeforeEach
    void setUp() {
        // 使用低内存配置进行测试
        MemoryConfig config = MemoryConfig.lowMemoryConfig();
        memory = new Memory(config);
    }

    @AfterEach
    void tearDown() {
        if (memory != null) {
            memory.shutdown();
        }
    }

    @Test
    void testFIFOEviction() {
        log.info("测试FIFO清退策略");

        // 配置FIFO策略
        MemoryConfig config = MemoryConfig.builder()
                .maxMessages(100)
                .triggerThreshold(0.8) // 8个消息时触发
                .targetRatio(0.6) // 清退到6个消息
                .evictionPolicy(new FIFOEvictionPolicy())
                .autoEvictionEnabled(false) // 手动触发
                .build();

        memory.updateConfig(config);

        // 添加12个消息
        for (int i = 0; i < 12; i++) {
            Message msg = Message.builder()
                    .content("消息内容 " + i)
                    .role(i < 3 ? "system" : "user") // 前3个是系统消息，应该被保护
                    .build();
            memory.add(msg);
        }

        assertEquals(12, memory.count());

        // 手动触发清退
        int evictedCount = memory.triggerEviction();
        log.info("清退了 {} 个消息", evictedCount);

        // 验证清退后的数量
        assertTrue(memory.count() <= 6);

        // 验证系统消息被保护
        long systemMessageCount = memory.getByRole("system").size();
        assertTrue(systemMessageCount > 0, "系统消息应该被保护");

        log.info("FIFO清退测试完成，剩余消息数: {}", memory.count());
    }

    @Test
    void testLRUEviction() {
        log.info("测试LRU清退策略");

        // 配置LRU策略
        MemoryConfig config = MemoryConfig.builder()
                .maxMessages(8)
                .triggerThreshold(0.75) // 6个消息时触发
                .targetRatio(0.5) // 清退到4个消息
                .evictionPolicy(new LRUEvictionPolicy())
                .autoEvictionEnabled(false)
                .build();

        memory.updateConfig(config);

        // 添加消息
        for (int i = 0; i < 8; i++) {
            Message msg = Message.builder()
                    .content("LRU测试消息 " + i)
                    .role("user")
                    .build();
            memory.add(msg);
        }

        // 访问前几个消息，使它们成为最近使用的
        memory.getByContent("LRU测试消息 0");
        memory.getByContent("LRU测试消息 1");
        memory.getByContent("LRU测试消息 2");

        // 手动触发清退
        int evictedCount = memory.triggerEviction();
        log.info("LRU清退了 {} 个消息", evictedCount);

        // 验证最近访问的消息仍然存在
        assertFalse(memory.getByContent("LRU测试消息 0").isEmpty());
        assertFalse(memory.getByContent("LRU测试消息 1").isEmpty());
        assertFalse(memory.getByContent("LRU测试消息 2").isEmpty());

        log.info("LRU清退测试完成，剩余消息数: {}", memory.count());
    }

    @Test
    void testAutoEviction() throws InterruptedException {
        log.info("测试自动清退机制");

        // 配置自动清退
        MemoryConfig config = MemoryConfig.builder()
                .maxMessages(5)
                .triggerThreshold(0.8) // 4个消息时触发
                .targetRatio(0.6) // 清退到3个消息
                .evictionPolicy(new FIFOEvictionPolicy())
                .autoEvictionEnabled(true)
                .evictionCheckInterval(1000) // 1秒检查一次
                .build();

        Memory autoMemory = new Memory(config);

        try {
            // 快速添加消息
            for (int i = 0; i < 6; i++) {
                Message msg = Message.builder()
                        .content("自动清退测试消息 " + i)
                        .role("user")
                        .build();
                autoMemory.add(msg);
            }

            log.info("添加消息后，当前消息数: {}", autoMemory.count());

            // 等待自动清退触发
            TimeUnit.SECONDS.sleep(2);

            log.info("等待后，当前消息数: {}", autoMemory.count());

            // 验证自动清退生效
            assertTrue(autoMemory.count() <= 3, "自动清退应该将消息数量控制在目标范围内");

        } finally {
            autoMemory.shutdown();
        }

        log.info("自动清退测试完成");
    }

    @Test
    void testMemoryStats() {
        log.info("测试内存统计功能");

        // 添加一些消息
        for (int i = 0; i < 5; i++) {
            Message msg = Message.builder()
                    .content("统计测试消息 " + i + " - 这是一个较长的消息内容用于测试内存使用量估算")
                    .role("user")
                    .build();
            memory.add(msg);
        }

        // 获取统计信息
        MemoryStats stats = memory.getStats();

        assertNotNull(stats);
        assertEquals(5, stats.getTotalMessages());
        assertTrue(stats.getMemoryUsageBytes() > 0);

        log.info("内存统计信息: {}", stats);
        log.info("格式化内存使用量: {}", stats.getFormattedMemoryUsage());
        log.info("内存使用率: {:.2f}%", stats.getMemoryUsageRatio() * 100);

        // 触发清退
        memory.triggerEviction();

        // 获取清退后的统计信息
        MemoryStats statsAfterEviction = memory.getStats();
        assertTrue(statsAfterEviction.getEvictionCount() > 0);

        log.info("清退后统计信息: {}", statsAfterEviction);
    }

    @Test
    void testEvictToSize() {
        log.info("测试强制清退到指定大小");

        // 添加10个消息
        for (int i = 0; i < 10; i++) {
            Message msg = Message.builder()
                    .content("强制清退测试消息 " + i)
                    .role("user")
                    .build();
            memory.add(msg);
        }

        assertEquals(10, memory.count());

        // 强制清退到5个消息
        int evictedCount = memory.evictToSize(5);

        assertTrue(evictedCount > 0);
        assertTrue(memory.count() <= 5);

        log.info("强制清退了 {} 个消息，剩余 {} 个消息", evictedCount, memory.count());
    }

    @Test
    void testConfigUpdate() {
        log.info("测试配置更新");

        // 初始配置
        MemoryStats initialStats = memory.getStats();
        log.info("初始配置: maxMessages={}", initialStats.getMaxMessages());

        // 更新配置
        MemoryConfig newConfig = MemoryConfig.builder()
                .maxMessages(50)
                .triggerThreshold(0.9)
                .targetRatio(0.7)
                .evictionPolicy(new LRUEvictionPolicy())
                .autoEvictionEnabled(true)
                .build();

        memory.updateConfig(newConfig);

        // 验证配置更新
        MemoryStats updatedStats = memory.getStats();
        assertEquals(50, updatedStats.getMaxMessages());
        assertEquals("LRU", updatedStats.getEvictionPolicy());

        log.info("配置更新成功: {}", updatedStats);
    }
}