# Focus Chain 集成到任务执行循环

本文档详细说明了Focus Chain如何集成到主要的任务执行循环中，对应Cline中的核心逻辑。

## 🔄 集成架构概览

```
用户输入 → 任务执行循环 → LLM处理 → 工具调用 → Focus Chain更新 → 循环继续
    ↑                                                        ↓
    ←─────────── Focus Chain指令注入 ←─────────────────────────
```

## 🎯 关键集成点

### 1. 指令注入点 (loadContext)

**位置**: `TaskExecutionLoop.loadContext()` 方法  
**对应Cline**: `src/core/task/index.ts:2249-2259`

```java
// 关键代码段
if (focusChainManager != null && focusChainManager.shouldIncludeFocusChainInstructions()) {
    String focusChainInstructions = focusChainManager.generateFocusChainInstructions();
    contentParts.add(focusChainInstructions);
    
    // 重置计数器和标志
    taskState.resetApiRequestsSinceLastTodoUpdate();
    taskState.setTodoListWasUpdatedByUser(false);
}
```

**触发条件**:
- Plan模式
- 从Plan模式切换到Act模式
- 用户手动编辑了列表
- 达到提醒间隔（默认6次请求）
- 首次API请求且无现有列表
- 多次请求后仍无列表

### 2. 工具响应处理点 (executeToolCall)

**位置**: `TaskExecutionLoop.executeToolCall()` 方法  
**对应Cline**: `src/core/task/ToolExecutor.ts:380-382`

```java
// 关键代码段
if (focusChainSettings.isEnabled() && toolCall.hasTaskProgressParameter()) {
    String taskProgress = toolCall.getTaskProgress();
    focusChainManager.updateFCListFromToolResponse(taskProgress);
}
```

**处理逻辑**:
1. 检查工具调用是否包含`task_progress`参数
2. 如果包含，提取进度信息
3. 更新Focus Chain列表
4. 保存到磁盘文件
5. 发送UI更新消息

### 3. 任务完成处理点 (handleAttemptCompletion)

**位置**: `TaskExecutionLoop.handleAttemptCompletion()` 方法  
**对应Cline**: `src/core/task/tools/handlers/AttemptCompletionHandler.ts:100-102`

```java
// 关键代码段
if (focusChainSettings.isEnabled() && taskProgress != null) {
    focusChainManager.updateFCListFromToolResponse(taskProgress);
}
```

**特殊处理**:
- 在用户响应之前更新Focus Chain
- 分析未完成项目用于遥测
- 确保任务完成状态正确记录

## 📊 状态管理

### TaskState 状态字段

```java
// Focus Chain相关状态
private int apiRequestCount = 0;                    // 总API请求次数
private int apiRequestsSinceLastTodoUpdate = 0;     // 自上次todo更新后的请求次数
private String currentFocusChainChecklist = null;   // 当前Focus Chain列表
private boolean todoListWasUpdatedByUser = false;   // 用户是否手动更新了列表

// Plan模式状态
private boolean didRespondToPlanAskBySwitchingMode = false;  // 是否通过模式切换响应

// 任务执行状态
private int consecutiveMistakeCount = 0;            // 连续错误次数
private boolean didRejectTool = false;              // 是否拒绝了工具调用
private boolean abort = false;                      // 是否中止任务
```

### 状态更新时机

1. **API请求计数**: 每次调用LLM时递增
2. **Todo更新计数**: 每次API请求递增，Focus Chain更新时重置
3. **当前列表**: 每次收到`task_progress`参数时更新
4. **用户更新标志**: 检测到用户手动编辑文件时设置

## 🔧 执行流程详解

### 主执行循环

```java
public void initiateTaskLoop(String userContent) {
    String nextUserContent = userContent;
    boolean includeFileDetails = true;
    
    while (!taskState.isAbort()) {
        // 1. 递归处理请求
        boolean didEndLoop = recursivelyMakeRequests(nextUserContent, includeFileDetails);
        includeFileDetails = false;
        
        if (didEndLoop) {
            break;
        } else {
            // 2. 如果没有使用工具，强制继续
            nextUserContent = "Please continue with the task...";
            taskState.incrementConsecutiveMistakeCount();
        }
        
        // 3. 检查最大请求限制
        if (taskState.getApiRequestCount() >= MAX_REQUESTS_PER_TASK) {
            break;
        }
    }
}
```

### 递归请求处理

```java
public boolean recursivelyMakeRequests(String userContent, boolean includeFileDetails) {
    // 1. 增加计数
    taskState.incrementApiRequestCount();
    taskState.incrementApiRequestsSinceLastTodoUpdate();
    
    // 2. 加载上下文（关键的Focus Chain注入点）
    String processedContent = loadContext(userContent, includeFileDetails);
    
    // 3. 解析斜杠命令
    SlashCommandParser.ParseResult parseResult = 
        commandParser.parseSlashCommands(processedContent, focusChainSettings);
    
    // 4. 发送到LLM
    String llmResponse = llm.sendMessage(parseResult.getProcessedText());
    
    // 5. 处理响应和工具调用（关键的Focus Chain更新点）
    boolean didUseTools = processLLMResponse(llmResponse);
    
    // 6. 决定是否继续递归
    if (shouldContinueRecursion(llmResponse)) {
        return recursivelyMakeRequests(extractNextUserContent(llmResponse), false);
    }
    
    return true;
}
```

## 🎨 设计模式

### 1. 观察者模式
- **TaskCallbacks**: 定义回调接口
- **FocusChainManager**: 通过回调通知状态变化
- **TaskExecutionLoop**: 监听并响应状态变化

### 2. 策略模式
- **SlashCommand**: 定义命令处理策略
- **DeepPlanningCommand**: 具体的Deep Planning策略
- **SlashCommandParser**: 上下文管理器

### 3. 状态模式
- **TaskState**: 封装任务状态
- **Mode**: 定义执行模式（Plan/Act）
- **FocusChainManager**: 根据状态决定行为

## 🔍 调试和监控

### 日志输出示例

```
[TASK_LOOP] 开始处理任务...
[TASK_LOOP] API请求计数: 1
[FOCUS_CHAIN] 检查指令注入条件: true (首次请求)
[FOCUS_CHAIN] 生成Focus Chain指令 (1247字符)
[TASK_LOOP] Focus Chain指令已注入
[LLM] 发送请求到大模型...
[LLM] 收到响应，包含工具调用
[TOOL_EXECUTOR] 执行工具调用: task_progress
[FOCUS_CHAIN] 更新进度列表: 6项任务，0项完成
[FOCUS_CHAIN] 保存到文件: ./demo/focus-chain.md
[WEBVIEW] 发送UI更新消息
```

### 性能监控

- **API请求频率**: 监控每分钟的请求次数
- **Focus Chain更新频率**: 跟踪todo列表更新间隔
- **任务完成率**: 统计完成的任务百分比
- **错误率**: 监控连续错误和工具拒绝率

## 🚀 使用示例

### 基本使用

```java
// 1. 创建组件
TaskState taskState = new TaskState();
FocusChainSettings settings = new FocusChainSettings(true, 6);
FocusChainManager focusChainManager = new FocusChainManager(/*...*/);
TaskExecutionLoop taskLoop = new TaskExecutionLoop(/*...*/);

// 2. 启动任务
taskLoop.initiateTaskLoop("创建用户管理系统");
```

### 高级配置

```java
// 自定义Focus Chain设置
FocusChainSettings customSettings = new FocusChainSettings(
    true,    // 启用Focus Chain
    3        // 每3次请求提醒一次
);

// 自定义回调处理
TaskCallbacks callbacks = new TaskCallbacks() {
    @Override
    public void say(String type, String message) {
        logger.info("[{}] {}", type, message);
    }
    
    @Override
    public void onProgressUpdated(String taskId, String progress) {
        // 发送到外部监控系统
        monitoringService.updateProgress(taskId, progress);
    }
};
```

## 🔧 扩展点

### 1. 自定义工具处理器

```java
public class CustomToolHandler {
    public void handleToolCall(ToolCall toolCall) {
        // 自定义工具调用逻辑
        if (toolCall.getName().equals("custom_tool")) {
            // 处理自定义工具
            String result = processCustomTool(toolCall);
            
            // 更新Focus Chain（如果需要）
            if (toolCall.hasTaskProgressParameter()) {
                focusChainManager.updateFCListFromToolResponse(
                    toolCall.getTaskProgress()
                );
            }
        }
    }
}
```

### 2. 自定义状态监听器

```java
public class TaskStateListener {
    public void onApiRequestCountChanged(int newCount) {
        // 响应API请求计数变化
        if (newCount % 10 == 0) {
            logger.info("已处理 {} 个API请求", newCount);
        }
    }
    
    public void onFocusChainUpdated(String newList) {
        // 响应Focus Chain更新
        FocusChainCounts counts = parseFocusChainListCounts(newList);
        metrics.updateCompletionRate(counts.getCompletionRate());
    }
}
```

## 📈 最佳实践

1. **合理设置提醒间隔**: 根据任务复杂度调整`remindClineInterval`
2. **监控状态变化**: 实时跟踪任务进度和Focus Chain状态
3. **处理异常情况**: 优雅处理LLM响应异常和文件操作失败
4. **性能优化**: 避免频繁的文件I/O操作
5. **用户体验**: 提供清晰的进度反馈和状态信息

这个集成设计完全保持了Cline的原有逻辑，同时提供了Java环境下的完整实现。通过这种方式，Focus Chain可以无缝地集成到任何基于Java的任务执行系统中。
