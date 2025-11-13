# ReactorRole 中断功能使用指南

## 功能概述

新增的中断功能允许强制停止 ReactorRole 的执行，包括在 think、observe、act 阶段以及 LLM 调用过程中的中断。

## 核心特性

### 1. 原子中断控制
- 使用 `AtomicBoolean interrupted` 变量控制中断状态
- 线程安全，支持并发操作
- 在关键执行点进行中断检查

### 2. 多阶段中断支持
- **think 阶段**: 在开始思考前检查中断状态
- **observe 阶段**: 在等待消息和处理消息前检查中断状态  
- **act 阶段**: 在执行动作前、调用LLM前、执行工具前检查中断状态
- **LLM 流式响应**: 在接收每个响应片段时检查中断状态

### 3. 优雅的中断处理
- 中断时会向用户发送提示信息
- 自动关闭流式输出连接
- 记录详细的中断日志

## API 接口

### ReactorRole 方法

```java
// 强制中断 Role 执行
public void interrupt()

// 重置中断标志，允许重新执行
public void resetInterrupt()  

// 检查是否被中断
public boolean isInterrupted()
```

### RoleService 方法

```java
// 中断指定 Agent
public Mono<String> interruptAgent(Message message)

// 重置指定 Agent 的中断状态
public Mono<String> resetAgentInterrupt(Message message)

// 获取 Agent 中断状态
public Mono<String> getAgentInterruptStatus(Message message)

// 中断所有 Agent
public Mono<String> interruptAllAgents()
```

## 使用示例

### 1. 直接中断 ReactorRole

```java
ReactorRole role = new ReactorRole(...);
// 启动执行
role.run();

// 在其他线程中中断
role.interrupt();

// 重置后重新开始
role.resetInterrupt();
```

### 2. 通过 RoleService 中断

```java
@Autowired
private RoleService roleService;

// 中断特定用户的 Agent
Message interruptMsg = Message.builder()
    .sentFrom("user123")
    .build();
    
String result = roleService.interruptAgent(interruptMsg).block();
System.out.println(result); // "Agent user123 已被强制中断"

// 重置中断状态
String resetResult = roleService.resetAgentInterrupt(interruptMsg).block();
System.out.println(resetResult); // "Agent user123 中断状态已重置，可以重新开始执行"
```

### 3. 检查中断状态

```java
// 检查单个 Agent 状态
String status = roleService.getAgentInterruptStatus(interruptMsg).block();
System.out.println(status); // "Agent user123 状态: 正常运行" 或 "Agent user123 状态: 已中断"

// 检查 Role 实例状态
if (role.isInterrupted()) {
    System.out.println("Role 当前处于中断状态");
}
```

## 中断时机

### 自动中断点
1. **think() 方法开始时**
2. **observe() 方法开始和接收消息后**
3. **act() 方法开始时**
4. **调用 LLM 前**
5. **LLM 流式响应的每个数据包**
6. **执行工具前**

### 用户触发中断
- 通过 API 调用 `interrupt()` 方法
- 通过 RoleService 的中断接口
- 系统异常或超时自动触发

## 注意事项

### 1. 中断的即时性
- 中断检查在关键执行点进行
- LLM 调用过程中的中断依赖于流式响应的频率
- 长时间运行的工具执行可能无法立即响应中断

### 2. 状态管理
- 中断后需要手动调用 `resetInterrupt()` 才能重新执行
- 中断状态会持续保持，直到被显式重置
- 建议在创建新对话时重置中断状态

### 3. 异常处理
- 中断过程中可能出现的 FluxSink 异常会被捕获并记录
- 不会影响中断功能的正常执行

## 实际应用场景

1. **用户取消操作**: 用户在 Web 界面点击取消按钮
2. **超时保护**: 防止 Agent 执行时间过长
3. **资源保护**: 在系统资源紧张时主动中断低优先级任务
4. **调试和测试**: 在开发过程中快速停止执行进行调试

## 日志监控

中断相关的日志会包含以下信息：
- 中断触发的具体阶段
- 中断的 Role 名称
- 中断和重置的时间戳
- 中断原因（如果有）

通过监控这些日志可以分析系统的中断模式和性能表现。
