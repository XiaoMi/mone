# 中断命令使用指南

## 功能概述

RoleService 的 `receiveMsg` 方法现在支持通过特定命令来中断 Agent 的执行，并实现智能的中断状态管理。

## 支持的中断命令

### 英文命令
- `/exit` - 退出/中断
- `/stop` - 停止
- `/interrupt` - 中断
- `/cancel` - 取消

### 中文命令
- 包含 "停止" 的消息
- 包含 "中断" 的消息  
- 包含 "取消" 的消息

## 工作流程

### 1. 发送中断命令

当用户发送任何中断命令时：

```java
// 用户发送消息
Message message = Message.builder()
    .content("/exit")  // 或其他中断命令
    .sentFrom("user123")
    .build();

// RoleService 处理
Flux<String> response = roleService.receiveMsg(message);
```

**系统响应：**
```
🛑 Agent user123 已被强制中断
💡 发送任何新命令将自动重置中断状态并继续执行
```

### 2. 重复发送中断命令

如果 Agent 已经处于中断状态，再次发送中断命令：

**系统响应：**
```
⚠️ Agent user123 已经处于中断状态
💡 发送任何非中断命令将自动重置中断状态并继续执行
```

### 3. 自动重置中断状态

当 Agent 处于中断状态时，收到非中断命令会自动重置：

```java
// Agent 当前处于中断状态
// 用户发送正常命令
Message normalMessage = Message.builder()
    .content("帮我写一个Hello World程序")
    .sentFrom("user123")
    .build();

Flux<String> response = roleService.receiveMsg(normalMessage);
```

**系统响应：**
```
🔄 检测到新命令，已自动重置中断状态，继续执行...
[然后正常处理用户请求]
```

## 技术实现细节

### 中断命令检测逻辑

```java
private boolean isInterruptCommand(String content) {
    if (content == null) {
        return false;
    }
    String trimmed = content.trim().toLowerCase();
    return trimmed.equals("/exit") || 
           trimmed.equals("/stop") || 
           trimmed.equals("/interrupt") ||
           trimmed.equals("/cancel") ||
           trimmed.contains("停止") ||
           trimmed.contains("中断") ||
           trimmed.contains("取消");
}
```

### 中断处理流程

1. **检查中断命令** - 优先检查是否为中断命令
2. **处理中断状态** - 根据当前状态给出相应提示
3. **自动重置逻辑** - 非中断命令自动重置中断状态
4. **正常处理** - 继续后续的消息处理流程

## 使用场景

### 1. 用户主动中断

```bash
用户: "帮我分析这个大文件"
系统: [开始分析...]
用户: "/exit"
系统: "🛑 Agent user123 已被强制中断"
```

### 2. 中断后继续

```bash
用户: "/exit"
系统: "🛑 Agent user123 已被强制中断"
用户: "写一个简单的函数"
系统: "🔄 检测到新命令，已自动重置中断状态，继续执行..."
系统: [开始处理新请求]
```

### 3. 中文命令支持

```bash
用户: "停止当前任务"
系统: "🛑 Agent user123 已被强制中断"
用户: "中断执行"
系统: "⚠️ Agent user123 已经处于中断状态"
```

## 与原有中断功能的关系

### 命令级中断 vs API级中断

1. **命令级中断（新功能）**
   - 通过用户消息触发
   - 用户友好的交互方式
   - 自动状态管理

2. **API级中断（原有功能）**
   - 通过编程接口调用
   - 系统级控制
   - 手动状态管理

### 组合使用

```java
// API 中断
roleService.interruptAgent(message);

// 用户稍后发送命令会自动重置
Message newMessage = Message.builder()
    .content("新的任务")
    .sentFrom("user123")
    .build();
    
roleService.receiveMsg(newMessage); // 自动重置并处理
```

## 日志记录

系统会记录所有中断相关的操作：

```
2025-09-22 14:30:15 INFO  Agent user123 收到中断命令，已被中断
2025-09-22 14:31:20 INFO  Agent user123 收到新的非中断命令，自动重置中断状态
```

## 最佳实践

### 1. 用户体验
- 提供清晰的状态反馈
- 支持多语言命令
- 自动化状态管理

### 2. 系统稳定性
- 优雅的中断处理
- 防止状态混乱
- 完整的日志记录

### 3. 开发建议
- 监控中断频率
- 分析用户中断模式
- 优化响应速度

这个功能让用户可以更自然地控制 Agent 的执行，提供了更好的交互体验。
