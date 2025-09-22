# ReactorRole 上下文压缩功能集成

## 概述

ReactorRole现已集成了完整的上下文压缩功能，能够智能管理长对话历史，通过AI驱动的压缩和规则优化来保持最佳的对话体验。

## 🚀 核心功能

### 1. 自动上下文压缩
- **智能触发**: 基于消息数量和token估算自动触发压缩
- **AI总结**: 使用LLM模型对对话历史进行智能分析和总结
- **无缝集成**: 压缩过程完全透明，不影响正常对话流程

### 2. 手动压缩命令
支持多种压缩命令格式：
- `/compress` - 标准压缩命令
- `/compact` - 紧凑压缩命令  
- `/summarize` - 总结命令
- `/smol` - 简化命令
- `压缩对话` - 中文命令
- `总结对话` - 中文总结命令

### 3. 规则优化
- **去重文件读取**: 自动检测并去除重复的文件读取内容
- **内容压缩**: 智能压缩冗余信息
- **结构保持**: 保持对话的逻辑结构和连贯性

## 📋 集成详情

### 新增字段
```java
// 上下文管理器 - 负责prompt压缩
private ConversationContextManager contextManager;

// 任务状态 - 用于上下文压缩
private TaskState taskState;
```

### 初始化代码
```java
// 在构造函数中初始化
this.taskState = new TaskState();
this.contextManager = new ConversationContextManager(this.llm);

// 配置压缩参数
this.contextManager.setEnableAiCompression(true);
this.contextManager.setEnableRuleBasedOptimization(true);
this.contextManager.setMaxMessagesBeforeCompression(15);
```

### 核心处理流程
```java
// 在observe()方法中添加
//放到记忆中
this.putMemory(msg);

// 处理上下文压缩
processContextCompression(msg);
```

## 🔧 配置选项

### 通过roleConfig配置
```java
Map<String, String> config = new HashMap<>();
config.put("enableAiCompression", "true");           // 启用AI压缩
config.put("enableRuleBasedOptimization", "true");   // 启用规则优化
config.put("maxMessagesBeforeCompression", "20");    // 压缩阈值
config.put("workspacePath", "/path/to/workspace");   // 工作区路径

role.setRoleConfig(config);
role.initConfig();
```

### 配置参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `enableAiCompression` | boolean | true | 是否启用AI智能压缩 |
| `enableRuleBasedOptimization` | boolean | true | 是否启用规则基础优化 |
| `maxMessagesBeforeCompression` | int | 15 | 触发压缩的消息数阈值 |

## 📊 使用示例

### 基本使用
```java
// 1. 创建ReactorRole
LLM llm = new LLM(llmConfig);
ReactorRole role = new ReactorRole("AI助手", new CountDownLatch(1), llm);

// 2. 配置压缩功能
Map<String, String> config = new HashMap<>();
config.put("enableAiCompression", "true");
config.put("maxMessagesBeforeCompression", "10");
role.setRoleConfig(config);
role.initConfig();

// 3. 正常使用，压缩会自动触发
role.putMessage(Message.builder().content("用户消息").role("user").build());
```

### 手动压缩
```java
// 检查上下文状态
ConversationContextManager.ContextStats stats = role.getContextStats();
System.out.println("当前消息数: " + stats.getMessageCount());

// 手动触发压缩
role.manualCompressContext().thenAccept(success -> {
    if (success) {
        System.out.println("压缩成功!");
    }
});
```

### 通过命令压缩
```java
// 用户发送压缩命令
Message compressCommand = Message.builder()
    .content("/compress")
    .role("user")
    .build();

role.putMessage(compressCommand);
// 系统会自动识别并执行压缩
```

## 🔍 监控和调试

### 获取上下文统计
```java
ConversationContextManager.ContextStats stats = role.getContextStats();
if (stats != null) {
    System.out.println("消息数量: " + stats.getMessageCount());
    System.out.println("总字符数: " + stats.getTotalCharacters());  
    System.out.println("估算tokens: " + stats.getEstimatedTokens());
    System.out.println("需要压缩: " + stats.needsCompression());
}
```

### 检查压缩状态
```java
// 检查是否正在压缩
boolean isCompressing = role.isContextCompressing();

// 检查是否完成压缩
boolean completed = role.getTaskState().isDidCompleteContextCompression();
```

### 日志监控
压缩过程会产生详细的日志：
```
INFO  - 上下文已压缩: 原始消息数=25, 压缩后消息数=8
INFO  - 应用了上下文规则优化
INFO  - 手动压缩成功: 20 -> 6 消息
```

## 🎯 压缩策略

### 自动触发条件
1. **消息数量**: 达到`maxMessagesBeforeCompression`设置的阈值
2. **Token估算**: 超过默认的token限制
3. **内容分析**: 检测到大量重复或冗余内容

### 压缩保留策略
- ✅ 保留第一条用户消息（任务描述）
- ✅ 生成智能AI总结作为上下文
- ✅ 保留最后几条消息维持连续性
- ✅ 保留Focus Chain任务进度
- ✅ 保留重要的技术细节和代码片段

### 压缩过程
1. **规则优化**: 去除重复文件读取、压缩冗余内容
2. **AI分析**: 使用LLM对对话进行智能分析
3. **结构化总结**: 按照预定义格式生成详细摘要
4. **消息重构**: 创建压缩后的消息列表
5. **内存更新**: 更新角色记忆中的消息历史

## ⚠️ 注意事项

### 1. API成本
- AI压缩会产生额外的LLM API调用
- 建议在生产环境中合理设置压缩阈值
- 可以通过`enableAiCompression=false`关闭AI压缩，只使用规则优化

### 2. 性能考虑
- 压缩过程是异步的，不会阻塞主对话流程
- 大量消息的压缩可能需要较长时间
- 建议监控压缩频率，避免过于频繁的压缩

### 3. 数据一致性
- 压缩会修改角色的记忆内容
- 确保在压缩完成前不要并发修改记忆
- 压缩失败时会保持原有消息不变

## 🔧 故障排除

### 常见问题

**Q: 压缩命令没有响应？**
A: 检查LLM配置是否正确，确保API密钥有效

**Q: 压缩后丢失了重要信息？**
A: AI会尽力保留重要信息，但可以调整压缩阈值或关闭自动压缩

**Q: 压缩过程中出现异常？**
A: 检查日志中的详细错误信息，通常是网络或API配置问题

### 调试建议
1. 启用DEBUG日志级别查看详细过程
2. 使用`getContextStats()`监控压缩效果
3. 测试手动压缩功能验证配置正确性

## 📈 性能优化建议

1. **合理设置阈值**: 根据实际使用场景调整`maxMessagesBeforeCompression`
2. **选择性启用**: 在资源受限环境中可以只启用规则优化
3. **监控API使用**: 跟踪压缩频率和API调用成本
4. **缓存配置**: 考虑添加压缩结果缓存机制

## 🚀 扩展功能

ReactorRole的压缩功能可以进一步扩展：
- 自定义压缩策略
- 压缩结果缓存
- 压缩效果统计
- 多语言压缩支持
- 特定领域的压缩优化

通过这些增强功能，ReactorRole现在具备了企业级的长对话处理能力！
