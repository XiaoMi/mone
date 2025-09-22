package run.mone.hive.memory;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
import run.mone.hive.memory.longterm.core.Memory;
import run.mone.hive.memory.longterm.config.MemoryConfig;
import run.mone.hive.memory.longterm.config.YamlConfigLoader;
import run.mone.hive.mcp.service.MemoryQuery;
import run.mone.hive.schema.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 长期记忆管理器
 * 负责管理ReactorRole的长期记忆功能
 * 
 */
@Slf4j
public class LongTermMemoryManager {

    private Memory longTermMemory;
    private final String roleName;
    private MemoryConfig memoryConfig;

    public LongTermMemoryManager(String roleName) {
        this.roleName = roleName;
        initializeLongTermMemory();
    }

    public LongTermMemoryManager(String roleName, MemoryConfig config) {
        this.roleName = roleName;
        this.memoryConfig = config;
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
     * 查询长期记忆
     */
    public String queryLongTermMemory(Message msg, MemoryQuery memoryQuery, FluxSink sink) {
        if (this.longTermMemory == null) {
            return "";
        }

        try {
            // 做意图识别，判断是否需要搜索记忆
            String classify = getMemoryIntentClassification(msg, memoryQuery);
            if (!classify.equals("是")) {
                return "";
            }

            if (sink != null) {
                sink.next("从长期记忆获取信息\n");
            }

            // 获取搜索参数
            String userId = memoryQuery.getUserId();
            String agentId = memoryQuery.getAgentId();
            String sessionId = memoryQuery.getSessionId();
            int maxResults = memoryQuery.getMaxResults();
            double threshold = memoryQuery.getThreshold();

            // 如果没有配置userId，使用role名称作为默认agentId
            if (userId == null && agentId == null) {
                agentId = this.roleName;
            }

            // 搜索相关记忆
            Map<String, Object> searchResult = this.longTermMemory.search(
                    msg.getContent(),  // query
                    userId,           // userId
                    agentId,          // agentId
                    sessionId,        // sessionId
                    maxResults,       // limit
                    null,             // filters
                    threshold         // threshold
            );

            // 先保存当前对话到记忆中
            saveCurrentConversationToMemory(msg, userId, agentId, sessionId);

            // 处理搜索结果
            if (searchResult != null && searchResult.containsKey("results")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) searchResult.get("results");
                
                if (results != null && !results.isEmpty()) {
                    StringBuilder memoryContext = new StringBuilder();
                    memoryContext.append("===========\n长期记忆中的相关内容:\n");
                    
                    for (int i = 0; i < Math.min(results.size(), maxResults); i++) {
                        Map<String, Object> result = results.get(i);
                        Object memory = result.get("memory");
                        if (memory != null) {
                            memoryContext.append(String.format("%d. %s\n", i + 1, memory));
                        }
                    }
                    
                    memoryContext.append("===========\n");
                    log.info("从长期记忆中找到 {} 条相关记录", results.size());
                    return memoryContext.toString();
                }
            }

            log.debug("长期记忆中没有找到相关内容");
            return "";

        } catch (Exception e) {
            log.error("查询长期记忆时出错: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * 获取记忆搜索的意图分类
     */
    private String getMemoryIntentClassification(Message msg, MemoryQuery memoryQuery) {
        // 如果配置了意图识别服务，可以在这里调用
        // TODO 暂时使用简单的关键词匹配
        String content = msg.getContent().toLowerCase();
        if (content.contains("记住") || content.contains("还记得") || content.contains("之前说过") || 
            content.contains("历史") || content.contains("以前") || content.contains("曾经") ||
            content.contains("上次") || content.contains("记录") || content.contains("回忆")) {
            return "是";
        }
        
        // 默认对所有消息都搜索记忆（可根据需要调整）
        return "是";
    }

    /**
     * 保存当前对话到长期记忆
     */
    private void saveCurrentConversationToMemory(Message msg, String userId, String agentId, String sessionId) {
        if (this.longTermMemory == null) {
            return;
        }

        try {
            // 异步保存到记忆中，避免阻塞对话流程
            CompletableFuture.runAsync(() -> {
                try {
                    // 构建记忆内容
                    String memoryContent = String.format("用户问题: %s", msg.getContent());
                    
                    // 添加元数据
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("role", msg.getRole());
                    metadata.put("timestamp", System.currentTimeMillis());
                    metadata.put("roleAgent", this.roleName);
                    if (msg.getCauseBy() != null) {
                        metadata.put("causeBy", msg.getCauseBy());
                    }

                    // 保存到记忆
                    this.longTermMemory.add(
                            memoryContent,
                            userId,
                            agentId,
                            sessionId,
                            metadata,
                            true,  // infer
                            null,  // memoryType
                            null   // prompt
                    );

                    log.debug("已保存对话到长期记忆: {}", memoryContent);
                } catch (Exception e) {
                    log.warn("保存对话到长期记忆失败: {}", e.getMessage());
                }
            });
        } catch (Exception e) {
            log.warn("启动保存记忆任务失败: {}", e.getMessage());
        }
    }

    /**
     * 获取长期记忆实例
     */
    public Memory getLongTermMemory() {
        return this.longTermMemory;
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
     * 获取当前使用的记忆配置
     */
    public MemoryConfig getMemoryConfig() {
        return this.memoryConfig;
    }

    /**
     * 获取配置信息摘要
     */
    public String getConfigSummary() {
        if (this.memoryConfig == null) {
            return "使用默认配置";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("记忆配置摘要:\n");
        
        if (this.memoryConfig.getLlm() != null) {
            summary.append("- LLM: ").append(this.memoryConfig.getLlm().getProvider())
                   .append(" (").append(this.memoryConfig.getLlm().getModel()).append(")\n");
        }
        
        if (this.memoryConfig.getEmbedder() != null) {
            summary.append("- 嵌入模型: ").append(this.memoryConfig.getEmbedder().getProvider())
                   .append(" (").append(this.memoryConfig.getEmbedder().getModel()).append(")\n");
        }
        
        if (this.memoryConfig.getVectorStore() != null) {
            summary.append("- 向量存储: ").append(this.memoryConfig.getVectorStore().getProvider())
                   .append(" (").append(this.memoryConfig.getVectorStore().getCollectionName()).append(")\n");
        }
        
        if (this.memoryConfig.getGraphStore() != null && this.memoryConfig.getGraphStore().isEnabled()) {
            summary.append("- 图存储: ").append(this.memoryConfig.getGraphStore().getProvider()).append(" (已启用)\n");
        }
        
        return summary.toString();
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