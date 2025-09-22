# Hive 上下文压缩功能

## 概述

本模块实现了类似于Cline的智能上下文压缩功能，通过AI驱动的方式对长对话历史进行智能总结和压缩，同时保留关键信息和技术细节。

## 核心特性

### 1. 🤖 AI驱动的智能压缩
- 使用LLM模型对对话历史进行智能分析和总结
- 保留技术细节、代码片段、架构决策等重要信息
- 支持Focus Chain任务进度的持续跟踪

### 2. 🔧 规则基础的优化
- 自动检测和去除重复的文件读取
- 压缩冗余内容，提高token利用率
- 智能截断过长的消息内容

### 3. 📊 自动触发机制
- 基于消息数量和token估算的自动压缩
- 可配置的压缩阈值和策略
- 支持手动触发压缩

## 核心组件

### ContextManager
负责基础的上下文管理和规则优化：
```java
ContextManager contextManager = new ContextManager();
boolean shouldCompress = contextManager.shouldCompactContextWindow(messages, taskState);
```

### AiContextCompressor  
AI驱动的智能压缩器：
```java
AiContextCompressor compressor = new AiContextCompressor(llm);
CompletableFuture<CompressionResult> result = compressor.compressContextAsync(messages, focusChainSettings);
```

### ConversationContextManager
统一的上下文管理接口：
```java
ConversationContextManager manager = new ConversationContextManager(llm);
CompletableFuture<ContextProcessingResult> result = manager.processNewMessage(
    currentMessages, newMessage, taskState, focusChainSettings);
```

### SummarizeTaskCommand
手动压缩命令处理器：
```java
SummarizeTaskCommand command = new SummarizeTaskCommand(llm);
CompletableFuture<SummarizeResult> result = command.executeSummarization(
    messages, taskState, focusChainSettings);
```

## 使用方式

### 1. 基本使用

```java
// 1. 创建LLM实例
LLMConfig config = new LLMConfig();
config.setLlmProvider(LLMProvider.OPENAI);
config.setApiKey("your-api-key");
LLM llm = new LLM(config);

// 2. 创建上下文管理器
ConversationContextManager contextManager = new ConversationContextManager(llm);

// 3. 处理新消息
List<Message> currentMessages = getCurrentMessages();
Message newMessage = createNewMessage("用户输入");
TaskState taskState = new TaskState();
FocusChainSettings focusChainSettings = new FocusChainSettings();

CompletableFuture<ContextProcessingResult> future = contextManager.processNewMessage(
    currentMessages, newMessage, taskState, focusChainSettings);

future.thenAccept(result -> {
    if (result.wasCompressed()) {
        System.out.println("上下文已压缩: " + result.getProcessedMessages().size() + " 条消息");
    }
});
```

### 2. 手动触发压缩

```java
// 通过命令触发
SummarizeTaskCommand command = new SummarizeTaskCommand(contextManager);

// 检查是否需要压缩
if (command.shouldSummarize(messages, taskState)) {
    CompletableFuture<SummarizeResult> result = command.executeSummarization(
        messages, taskState, focusChainSettings);
    
    result.thenAccept(summarizeResult -> {
        if (summarizeResult.isSuccess()) {
            List<Message> compressedMessages = summarizeResult.getProcessedMessages();
            // 使用压缩后的消息继续对话
        }
    });
}
```

### 3. 配置选项

```java
ConversationContextManager manager = new ConversationContextManager(llm, 
    120000,  // maxTokens
    80000,   // compressionThreshold  
    0.8      // compressionRatioThreshold
);

// 配置压缩行为
manager.setEnableAiCompression(true);
manager.setEnableRuleBasedOptimization(true);
manager.setMaxMessagesBeforeCompression(20);
```

## 压缩策略

### 自动压缩触发条件
1. **消息数量**: 达到配置的最大消息数（默认20条）
2. **Token估算**: 超过压缩阈值（默认80,000 tokens）
3. **内容分析**: 检测到大量重复或冗余内容

### 压缩过程
1. **规则优化**: 去除重复文件读取、压缩冗余内容
2. **AI分析**: 使用LLM对对话进行智能分析
3. **结构化总结**: 按照预定义格式生成详细摘要
4. **消息重构**: 创建压缩后的消息列表

### 保留策略
- 保留第一条用户消息（任务描述）
- 生成智能总结作为上下文
- 保留最后几条消息维持连续性
- 保留Focus Chain任务进度

## AI Prompt模板

### 总结任务Prompt
移植自Cline的`summarizeTask`函数，包含：
- 详细的分析指令
- 结构化的输出格式要求
- Focus Chain支持
- 技术细节保留要求

### 继续对话Prompt  
移植自Cline的`continuationPrompt`函数，用于：
- 基于摘要继续对话
- 保持上下文连续性
- 处理特殊命令

## 集成示例

### 与现有Role集成
```java
public class ReactorRole extends Role {
    private ConversationContextManager contextManager;
    
    @Override
    public void init() {
        super.init();
        this.contextManager = new ConversationContextManager(this.llm);
    }
    
    @Override
    protected void processMessage(Message message) {
        // 处理新消息并自动管理上下文
        contextManager.processNewMessage(
            this.getMessageHistory(), 
            message, 
            this.taskState, 
            this.focusChainSettings
        ).thenAccept(result -> {
            // 更新消息历史
            this.updateMessageHistory(result.getProcessedMessages());
        });
    }
}
```

### 与SlashCommand集成
```java
// 在SlashCommandParser中注册
SlashCommandParser parser = new SlashCommandParser();
parser.registerCommand(new SummarizeTaskCommand(contextManager));

// 处理用户输入
if (parser.isSlashCommand(userInput)) {
    SlashCommand command = parser.parseCommand(userInput);
    if (command instanceof SummarizeTaskCommand) {
        // 执行压缩
        ((SummarizeTaskCommand) command).executeSummarization(
            messages, taskState, focusChainSettings);
    }
}
```

## 性能考虑

### Token估算
- 使用简单的字符长度估算（中文约3.5字符/token）
- 支持更精确的token计算接口扩展

### 异步处理
- 所有压缩操作都是异步的，不阻塞主线程
- 支持并发安全的状态管理

### 缓存机制
- 可扩展添加压缩结果缓存
- 支持增量压缩策略

## 错误处理

### 压缩失败回退
- AI压缩失败时自动回退到规则优化
- 规则优化失败时保持原始消息不变

### 异常恢复
- 完善的异常捕获和日志记录
- 状态恢复机制防止数据丢失

## 测试和示例

运行测试示例：
```bash
cd /path/to/hive
mvn test -Dtest=ContextCompressionExample
```

查看详细示例代码：`src/test/java/run/mone/hive/context/ContextCompressionExample.java`

## 扩展点

### 自定义压缩策略
实现`CompressionStrategy`接口来定义自定义压缩逻辑

### 自定义Prompt模板
修改`ContextPrompts`类中的模板来适应特定需求

### 集成其他LLM提供商
通过LLM接口支持不同的AI模型提供商

## 注意事项

1. **API密钥**: 需要配置有效的LLM API密钥
2. **模型选择**: 建议使用支持长上下文的模型（如GPT-4, Claude等）
3. **成本控制**: AI压缩会产生额外的API调用成本
4. **数据隐私**: 确保敏感信息在压缩前得到适当处理

## 更新日志

- v1.0.0: 初始实现，移植Cline的核心压缩功能
- 支持AI驱动的智能压缩和规则基础的优化
- 完整的异步处理和错误恢复机制
