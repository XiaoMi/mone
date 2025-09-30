package run.mone.hive.memory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.memory.longterm.config.MemoryConfig;
import run.mone.hive.memory.longterm.config.YamlConfigLoader;
import run.mone.hive.memory.longterm.core.Memory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 长期记忆管理器
 * 负责管理ReactorRole的长期记忆功能
 */
@Slf4j
@Data
public class LongTermMemoryManager {

    private Memory longTermMemory;
    private final String roleName;
    private MemoryConfig memoryConfig;

    public LongTermMemoryManager(String roleName) {
        this.roleName = roleName;
        initializeLongTermMemory();
    }


    /**
     * 初始化长期记忆系统
     */
    private void initializeLongTermMemory() {
        try {
            // 如果没有提供配置，尝试从配置文件加载
            if (this.memoryConfig == null) {
                this.memoryConfig = loadMemoryConfig();
            }

            // 使用配置创建Memory实例
            if (this.memoryConfig != null) {
                this.longTermMemory = new Memory(this.memoryConfig);
                log.info("长期记忆系统初始化成功 for role: {} (使用配置文件)", this.roleName);
            } else {
                // 如果配置加载失败，使用默认配置
                this.longTermMemory = new Memory();
                log.info("长期记忆系统初始化成功 for role: {} (使用默认配置)", this.roleName);
            }
        } catch (Exception e) {
            log.warn("长期记忆系统初始化失败 for role: {}, error: {}", this.roleName, e.getMessage());
            this.longTermMemory = null;
        }
    }

    /**
     * 加载记忆配置
     * 按优先级尝试不同的配置源
     */
    private MemoryConfig loadMemoryConfig() {
        try {
            // 1. 尝试从系统属性指定的配置文件加载
            String configPath = System.getProperty("hive.memory.config");
            if (configPath != null) {
                log.info("从系统属性指定的配置文件加载记忆配置: {}", configPath);
                return YamlConfigLoader.loadFromFile(configPath);
            }

            // 2. 尝试从环境变量指定的配置文件加载
            String envConfigPath = System.getenv("HIVE_MEMORY_CONFIG");
            if (envConfigPath != null) {
                log.info("从环境变量指定的配置文件加载记忆配置: {}", envConfigPath);
                return YamlConfigLoader.loadFromFile(envConfigPath);
            }

            // 3. 尝试从类路径加载默认配置文件
            log.info("从类路径加载默认记忆配置文件: memory-config.yml");
            return YamlConfigLoader.loadFromClasspath("memory-config.yml");

        } catch (Exception e) {
            log.warn("加载记忆配置失败，将使用默认配置: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 手动添加记忆
     */
    public CompletableFuture<Map<String, Object>> addMemory(String content, String userId, String agentId, String sessionId, Map<String, Object> metadata) {
        if (this.longTermMemory == null) {
            return CompletableFuture.completedFuture(Map.of("error", "长期记忆系统未初始化"));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.longTermMemory.add(content, userId, agentId, sessionId, metadata, true, null, null);
            } catch (Exception e) {
                log.error("添加记忆失败: {}", e.getMessage(), e);
                return Map.of("error", e.getMessage());
            }
        });
    }

    /**
     * 手动搜索记忆
     */
    public CompletableFuture<Map<String, Object>> searchMemory(String query, String userId, String agentId, String sessionId, int limit, double threshold) {
        if (this.longTermMemory == null) {
            return CompletableFuture.completedFuture(Map.of("error", "长期记忆系统未初始化"));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.longTermMemory.search(query, userId, agentId, sessionId, limit, null, threshold);
            } catch (Exception e) {
                log.error("搜索记忆失败: {}", e.getMessage(), e);
                return Map.of("error", e.getMessage());
            }
        });
    }

    /**
     * 获取所有记忆
     */
    public CompletableFuture<Map<String, Object>> getAllMemories(String userId, String agentId, String sessionId, int limit) {
        if (this.longTermMemory == null) {
            return CompletableFuture.completedFuture(Map.of("error", "长期记忆系统未初始化"));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.longTermMemory.getAll(userId, agentId, sessionId, null, limit);
            } catch (Exception e) {
                log.error("获取所有记忆失败: {}", e.getMessage(), e);
                return Map.of("error", e.getMessage());
            }
        });
    }

    /**
     * 重置长期记忆
     */
    public CompletableFuture<Boolean> resetMemory() {
        if (this.longTermMemory == null) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                this.longTermMemory.reset();
                log.info("长期记忆已重置 for role: {}", this.roleName);
                return true;
            } catch (Exception e) {
                log.error("重置长期记忆失败: {}", e.getMessage(), e);
                return false;
            }
        });
    }

    /**
     * 关闭长期记忆资源
     */
    public void close() {
        if (longTermMemory != null) {
            try {
                longTermMemory.close();
                log.info("长期记忆资源已关闭 for role: {}", this.roleName);
            } catch (Exception e) {
                log.warn("关闭长期记忆资源时出错: {}", e.getMessage());
            }
        }
    }
}