# TaskManager 虚拟线程配置说明

## 概述

TaskManager 现在支持使用 JDK 21 的虚拟线程(协程)来提升并发性能。虚拟线程是轻量级线程，可以创建大量线程而不会消耗过多的系统资源。

## 配置方式

### 1. 启用虚拟线程

在 `application.properties` 或 `application.yml` 中添加以下配置：

**application.properties:**
```properties
# 启用虚拟线程支持（默认为 false）
task.virtual.thread.enabled=true
```

**application.yml:**
```yaml
task:
  virtual:
    thread:
      enabled: true
```

### 2. 虚拟线程调度器配置

当启用虚拟线程时，系统会自动设置虚拟线程调度器的并行度为 200。这意味着虚拟线程将使用最多 200 个平台线程来执行任务。

此配置通过以下系统属性设置：
```java
System.setProperty("jdk.virtualThreadScheduler.parallelism", "200");
```

## 工作模式

### 传统线程池模式（默认）

当 `task.virtual.thread.enabled=false` 或未配置时：
- 主线程池：500 核心线程，500 最大线程
- 回调线程池：500 核心线程，500 最大线程
- 队列容量：50000

### 虚拟线程模式

当 `task.virtual.thread.enabled=true` 时：
- 使用 `Executors.newVirtualThreadPerTaskExecutor()` 创建虚拟线程池
- 每个任务都在一个新的虚拟线程中执行
- 虚拟线程绑定到最多 200 个平台线程上
- 无队列容量限制（虚拟线程可以按需创建）

## 性能优势

1. **更高的并发能力**：可以创建数百万个虚拟线程而不会耗尽系统资源
2. **更低的内存占用**：虚拟线程比传统线程占用更少的内存
3. **更好的可扩展性**：适合 I/O 密集型任务
4. **简化的编程模型**：使用阻塞式 API 而不牺牲性能

## 使用建议

### 适合使用虚拟线程的场景

- I/O 密集型任务（数据库查询、HTTP 请求等）
- 需要大量并发任务的场景
- 任务执行时间较长但大部分时间在等待 I/O

### 不适合使用虚拟线程的场景

- CPU 密集型任务（大量计算）
- 使用 ThreadLocal 存储大量数据的场景
- 需要精确控制线程数量的场景

## 系统要求

- **JDK 版本**：JDK 21 或更高版本
- **Spring Boot 版本**：建议使用 3.x 或更高版本

## 监控和调试

### 查看当前模式

应用启动时会在日志中打印当前使用的模式：
```
启用虚拟线程模式
```
或
```
使用传统线程池模式
```

### 性能监控

可以通过以下方式监控虚拟线程的使用情况：

1. 使用 JFR (Java Flight Recorder) 记录虚拟线程事件
2. 使用 JMX 监控线程池状态
3. 查看应用日志中的任务执行情况

## 迁移指南

### 从传统线程池迁移到虚拟线程

1. 确保 JDK 版本为 21 或更高
2. 在配置文件中添加 `task.virtual.thread.enabled=true`
3. 重启应用
4. 监控应用性能和稳定性
5. 根据实际情况调整配置

### 回退到传统线程池

如果遇到问题，可以随时回退：
1. 设置 `task.virtual.thread.enabled=false`
2. 重启应用

## 常见问题

### Q: 虚拟线程会影响现有代码吗？

A: 不会。代码层面完全兼容，只是底层执行机制不同。

### Q: 可以动态切换模式吗？

A: 不可以。需要重启应用才能切换模式。

### Q: 虚拟线程数量有限制吗？

A: 虚拟线程本身没有数量限制，但受限于平台线程数量（本配置中为 200）和系统资源。

### Q: 如何验证虚拟线程是否生效？

A: 查看启动日志，应该看到 "启用虚拟线程模式" 的提示。

## 示例配置

### 开发环境（使用传统线程池）
```properties
task.virtual.thread.enabled=false
```

### 生产环境（使用虚拟线程）
```properties
task.virtual.thread.enabled=true
```

## 技术细节

### 虚拟线程调度器并行度

调度器并行度设置为 200 意味着：
- 最多 200 个平台线程用于执行虚拟线程
- 虚拟线程会在这 200 个平台线程上进行调度
- 适合大多数应用场景，可以根据实际情况调整

如需修改并行度，可以在启动前设置系统属性：
```bash
java -Djdk.virtualThreadScheduler.parallelism=300 -jar your-app.jar
```

## 参考资料

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [Java 21 Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
