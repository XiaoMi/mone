package run.mone.hive.memory;

import run.mone.hive.schema.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * The most basic memory: super-memory with eviction support
 */
@Data
@Slf4j
public class Memory {
    private static final String IGNORED_MESSAGE_ID = "IGNORED";

    private List<Message> storage;
    private Map<String, List<Message>> index;
    private boolean ignoreId;

    // 清退机制相关字段
    private MemoryConfig config;
    private ScheduledExecutorService evictionScheduler;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile long lastEvictionTime = 0;
    private volatile int evictionCount = 0;

    public Memory() {
        this(MemoryConfig.defaultConfig());
    }

    public Memory(MemoryConfig config) {
        this.storage = new ArrayList<>();
        this.index = new HashMap<>();
        this.ignoreId = false;
        this.config = config;

        // 启动自动清退调度器
        if (config.isAutoEvictionEnabled()) {
            startEvictionScheduler();
        }

        log.info("Memory initialized with config: maxMessages={}, evictionPolicy={}",
                config.getMaxMessages(), config.getEvictionPolicy().getPolicyName());
    }

    /**
     * Add a new message to storage, while updating the index
     */
    public void add(Message message) {
        lock.writeLock().lock();
        try {
            if (ignoreId) {
                message.setId(IGNORED_MESSAGE_ID);
            }

            if (!storage.contains(message)) {
                // 设置创建时间
                if (message.getCreateTime() == 0) {
                    message.setCreateTime(System.currentTimeMillis());
                }

                storage.add(message);

                if (message.getCauseBy() != null) {
                    index.computeIfAbsent(message.getCauseBy(), k -> new ArrayList<>())
                            .add(message);
                }

                // 记录LRU访问（如果使用LRU策略）
                recordAccess(message.getId());

                // 检查是否需要清退
                checkAndTriggerEviction();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add multiple messages at once
     */
    public void addBatch(Collection<Message> messages) {
        lock.writeLock().lock();
        try {
            messages.forEach(this::addInternal);
            checkAndTriggerEviction();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 内部添加方法，不加锁
     */
    private void addInternal(Message message) {
        if (ignoreId) {
            message.setId(IGNORED_MESSAGE_ID);
        }

        if (!storage.contains(message)) {
            if (message.getCreateTime() == 0) {
                message.setCreateTime(System.currentTimeMillis());
            }

            storage.add(message);

            if (message.getCauseBy() != null) {
                index.computeIfAbsent(message.getCauseBy(), k -> new ArrayList<>())
                        .add(message);
            }

            recordAccess(message.getId());
        }
    }

    /**
     * Return all messages of a specified role
     */
    public List<Message> getByRole(String role) {
        lock.readLock().lock();
        try {
            List<Message> result = storage.stream()
                    .filter(message -> role.equals(message.getRole()))
                    .collect(Collectors.toList());

            // 记录访问
            result.forEach(msg -> recordAccess(msg.getId()));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Return all messages containing specified content
     */
    public List<Message> getByContent(String content) {
        lock.readLock().lock();
        try {
            List<Message> result = storage.stream()
                    .filter(message -> message.getContent() != null &&
                            message.getContent().contains(content))
                    .collect(Collectors.toList());

            // 记录访问
            result.forEach(msg -> recordAccess(msg.getId()));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Delete the newest message from storage
     */
    public Message deleteNewest() {
        lock.writeLock().lock();
        try {
            if (storage.isEmpty()) {
                return null;
            }

            Message newestMsg = storage.remove(storage.size() - 1);
            removeFromIndex(newestMsg);
            removeAccessRecord(newestMsg.getId());
            return newestMsg;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Delete a specific message
     */
    public void delete(Message message) {
        lock.writeLock().lock();
        try {
            if (ignoreId) {
                message.setId(IGNORED_MESSAGE_ID);
            }

            storage.remove(message);
            removeFromIndex(message);
            removeAccessRecord(message.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clear all storage and index
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            storage.clear();
            index.clear();
            clearAccessRecords();
            log.info("Memory cleared, all messages removed");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Return the number of messages in storage
     */
    public int count() {
        lock.readLock().lock();
        try {
            return storage.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Try to recall all messages containing a specified keyword
     */
    public List<Message> tryRemember(String keyword) {
        lock.readLock().lock();
        try {
            List<Message> result = storage.stream()
                    .filter(message -> message.getContent() != null &&
                            message.getContent().contains(keyword))
                    .collect(Collectors.toList());

            // 记录访问
            result.forEach(msg -> recordAccess(msg.getId()));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Return the most recent k memories, return all when k=0
     */
    public List<Message> getRecent(int k) {
        lock.readLock().lock();
        try {
            List<Message> result;
            if (k <= 0 || k >= storage.size()) {
                result = new ArrayList<>(storage);
            } else {
                result = storage.subList(storage.size() - k, storage.size());
            }

            // 记录访问
            result.forEach(msg -> recordAccess(msg.getId()));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Find news (previously unseen messages) from the most recent k memories
     */
    public List<Message> findNews(List<Message> observed, int k) {
        lock.readLock().lock();
        try {
            List<Message> alreadyObserved = getRecent(k);
            return observed.stream()
                    .filter(msg -> !alreadyObserved.contains(msg))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Return all messages triggered by a specified Action
     */
    public List<Message> getByAction(String action) {
        lock.readLock().lock();
        try {
            List<Message> result = index.getOrDefault(action, new ArrayList<>());
            // 记录访问
            result.forEach(msg -> recordAccess(msg.getId()));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Return all messages triggered by specified Actions
     */
    public List<Message> getByActions(Set<String> actions) {
        lock.readLock().lock();
        try {
            List<Message> result = actions.stream()
                    .filter(index::containsKey)
                    .flatMap(action -> index.get(action).stream())
                    .collect(Collectors.toList());

            // 记录访问
            result.forEach(msg -> recordAccess(msg.getId()));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<? extends Message> get() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(this.storage);
        } finally {
            lock.readLock().unlock();
        }
    }

    // 添加一个方法,获取最后一条Message(class)
    public Message getLastMessage() {
        lock.readLock().lock();
        try {
            if (storage.isEmpty()) {
                return null;
            }
            Message lastMessage = storage.get(storage.size() - 1);
            recordAccess(lastMessage.getId());
            return lastMessage;
        } finally {
            lock.readLock().unlock();
        }
    }

    // ==================== 清退机制相关方法 ====================

    /**
     * 手动触发清退
     * 
     * @return 被清退的消息数量
     */
    public int triggerEviction() {
        lock.writeLock().lock();
        try {
            return performEviction();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 强制清退到指定数量
     * 
     * @param targetSize 目标消息数量
     * @return 被清退的消息数量
     */
    public int evictToSize(int targetSize) {
        lock.writeLock().lock();
        try {
            if (storage.size() <= targetSize) {
                return 0;
            }

            List<Message> toEvict = config.getEvictionPolicy()
                    .selectMessagesToEvict(new ArrayList<>(storage), targetSize);

            return evictMessages(toEvict);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 检查并触发清退
     */
    private void checkAndTriggerEviction() {
        if (storage.size() >= config.getTriggerMessageCount()) {
            performEviction();
        }
    }

    /**
     * 执行清退操作
     */
    private int performEviction() {
        int currentSize = storage.size();
        int targetSize = config.getTargetMessageCount();

        if (currentSize <= targetSize) {
            return 0;
        }

        log.info("Starting eviction: current size={}, target size={}", currentSize, targetSize);

        List<Message> toEvict = config.getEvictionPolicy()
                .selectMessagesToEvict(new ArrayList<>(storage), targetSize);

        int evictedCount = evictMessages(toEvict);

        lastEvictionTime = System.currentTimeMillis();
        evictionCount++;

        log.info("Eviction completed: evicted {} messages, remaining {} messages",
                evictedCount, storage.size());

        return evictedCount;
    }

    /**
     * 清退指定的消息列表
     */
    private int evictMessages(List<Message> toEvict) {
        int evictedCount = 0;

        for (Message message : toEvict) {
            if (storage.remove(message)) {
                removeFromIndex(message);
                removeAccessRecord(message.getId());
                evictedCount++;
            }
        }

        return evictedCount;
    }

    /**
     * 从索引中移除消息
     */
    private void removeFromIndex(Message message) {
        if (message.getCauseBy() != null) {
            List<Message> causedMessages = index.get(message.getCauseBy());
            if (causedMessages != null) {
                causedMessages.remove(message);
                if (causedMessages.isEmpty()) {
                    index.remove(message.getCauseBy());
                }
            }
        }
    }

    /**
     * 记录消息访问（用于LRU策略）
     */
    private void recordAccess(String messageId) {
        if (config.getEvictionPolicy() instanceof LRUEvictionPolicy) {
            ((LRUEvictionPolicy) config.getEvictionPolicy()).recordAccess(messageId);
        }
    }

    /**
     * 移除访问记录
     */
    private void removeAccessRecord(String messageId) {
        if (config.getEvictionPolicy() instanceof LRUEvictionPolicy) {
            ((LRUEvictionPolicy) config.getEvictionPolicy()).removeAccessRecord(messageId);
        }
    }

    /**
     * 清空所有访问记录
     */
    private void clearAccessRecords() {
        if (config.getEvictionPolicy() instanceof LRUEvictionPolicy) {
            // 清空LRU策略的访问记录
            LRUEvictionPolicy lruPolicy = (LRUEvictionPolicy) config.getEvictionPolicy();
            lruPolicy.clearAllAccessRecords();
        }
    }

    /**
     * 启动清退调度器
     */
    private void startEvictionScheduler() {
        if (evictionScheduler == null || evictionScheduler.isShutdown()) {
            evictionScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Memory-Eviction-Scheduler");
                t.setDaemon(true);
                return t;
            });

            evictionScheduler.scheduleAtFixedRate(
                    this::checkAndTriggerEviction,
                    config.getEvictionCheckInterval(),
                    config.getEvictionCheckInterval(),
                    TimeUnit.MILLISECONDS);

            log.info("Eviction scheduler started with interval: {} ms", config.getEvictionCheckInterval());
        }
    }

    /**
     * 停止清退调度器
     */
    public void stopEvictionScheduler() {
        if (evictionScheduler != null && !evictionScheduler.isShutdown()) {
            evictionScheduler.shutdown();
            try {
                if (!evictionScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    evictionScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                evictionScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Eviction scheduler stopped");
        }
    }

    /**
     * 获取内存统计信息
     */
    public MemoryStats getStats() {
        lock.readLock().lock();
        try {
            return MemoryStats.builder()
                    .totalMessages(storage.size())
                    .maxMessages(config.getMaxMessages())
                    .evictionCount(evictionCount)
                    .lastEvictionTime(lastEvictionTime)
                    .evictionPolicy(config.getEvictionPolicy().getPolicyName())
                    .memoryUsageBytes(estimateMemoryUsage())
                    .build();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 估算内存使用量
     */
    private long estimateMemoryUsage() {
        long totalSize = 0;
        for (Message message : storage) {
            // 粗略估算每个消息的内存占用
            totalSize += estimateMessageSize(message);
        }
        return totalSize;
    }

    /**
     * 估算单个消息的内存占用
     */
    private long estimateMessageSize(Message message) {
        long size = 200; // 基础对象大小

        if (message.getContent() != null) {
            size += message.getContent().length() * 2; // 字符串占用
        }

        if (message.getRole() != null) {
            size += message.getRole().length() * 2;
        }

        if (message.getCauseBy() != null) {
            size += message.getCauseBy().length() * 2;
        }

        return size;
    }

    /**
     * 更新配置
     */
    public void updateConfig(MemoryConfig newConfig) {
        lock.writeLock().lock();
        try {
            boolean needRestartScheduler = this.config.isAutoEvictionEnabled() != newConfig.isAutoEvictionEnabled()
                    || this.config.getEvictionCheckInterval() != newConfig.getEvictionCheckInterval();

            this.config = newConfig;

            if (needRestartScheduler) {
                stopEvictionScheduler();
                if (newConfig.isAutoEvictionEnabled()) {
                    startEvictionScheduler();
                }
            }

            log.info("Memory config updated: {}", newConfig);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 关闭Memory，清理资源
     */
    public void shutdown() {
        stopEvictionScheduler();
        log.info("Memory shutdown completed");
    }
}