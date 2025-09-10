# Focus Chain Java Implementation

这是基于Cline Focus Chain功能的Java实现，提供了完整的任务管理和待办列表跟踪功能。

## 📁 项目结构

```
run.mone.hive.task/
├── FocusChainSettings.java      # Focus Chain配置设置
├── TaskState.java               # 任务状态管理
├── Mode.java                    # 任务执行模式枚举
├── FocusChainFileUtils.java     # 文件操作工具类
├── LLM.java                     # LLM大模型调用接口
├── FocusChainPrompts.java       # 提示词模板类
├── FocusChainManager.java       # 核心管理器类
├── TaskCallbacks.java           # 任务回调接口
├── FocusChainExample.java       # 示例使用类
└── README.md                    # 说明文档
```

## 🚀 核心功能

### 1. 自动待办列表管理
- 智能检测何时需要创建或更新待办列表
- 支持Markdown格式的清单语法
- 自动跟踪完成进度

### 2. 模式切换支持
- **Plan模式**: 用于分析和规划任务
- **Act模式**: 用于实际执行任务
- 模式切换时自动触发相应指令

### 3. 文件同步
- 待办列表存储在Markdown文件中
- 支持用户手动编辑文件
- 自动检测文件变化并同步状态

### 4. 智能提醒系统
- 可配置的提醒间隔（默认6次API请求）
- 多种触发条件的智能判断
- 防止任务偏离轨道

## 🎯 使用方式

### 1. 基本初始化

```java
// 创建任务状态
TaskState taskState = new TaskState();

// 创建Focus Chain设置
FocusChainSettings settings = new FocusChainSettings(true, 6);

// 实现LLM接口
LLM llm = new YourLLMImplementation();

// 创建Focus Chain管理器
FocusChainManager manager = new FocusChainManager(
    "task-001", taskState, Mode.PLAN, "./task-dir", settings, llm
);
```

### 2. 设置回调

```java
// 设置消息回调
manager.setSayCallback(message -> {
    System.out.println("Focus Chain: " + message);
});

// 设置状态更新回调
manager.setPostStateToWebviewCallback(() -> {
    // 更新UI状态
});
```

### 3. 启动文件监控

```java
try {
    manager.setupFocusChainFileWatcher();
} catch (IOException e) {
    // 处理异常
}
```

### 4. 检查是否需要Focus Chain指令

```java
if (manager.shouldIncludeFocusChainInstructions()) {
    String instructions = manager.generateFocusChainInstructions();
    // 将指令添加到LLM提示中
}
```

### 5. 处理AI响应

```java
// 假设AI返回了task_progress参数
String taskProgress = extractTaskProgressFromAIResponse(aiResponse);
manager.updateFCListFromToolResponse(taskProgress);
```

## 📋 核心提示词

### 自动TODO列表管理系统提示
```
AUTOMATIC TODO LIST MANAGEMENT

The system automatically manages todo lists to help track task progress:

- Every 10th API request, you will be prompted to review and update the current todo list if one exists
- When switching from PLAN MODE to ACT MODE, you should create a comprehensive todo list for the task
- Todo list updates should be done silently using the task_progress parameter - do not announce these updates to the user
- Use standard Markdown checklist format: "- [ ]" for incomplete items and "- [x]" for completed items
- The system will automatically include todo list context in your prompts when appropriate
- Focus on creating actionable, meaningful steps rather than granular technical details
```

### Plan→Act模式切换强制指令
```
# TODO LIST CREATION REQUIRED - ACT MODE ACTIVATED

**You've just switched from PLAN MODE to ACT MODE!**

**IMMEDIATE ACTION REQUIRED:**
1. Create a comprehensive todo list in your NEXT tool call
2. Use the task_progress parameter to provide the list
3. Format each item using markdown checklist syntax:
	- [ ] For tasks to be done
	- [x] For any tasks already completed

**Your todo list should include:**
   - All major implementation steps
   - Testing and validation tasks
   - Documentation updates if needed
   - Final verification steps
```

## 🔧 触发条件

Focus Chain指令会在以下6种情况下被触发：

1. **Plan模式激活** - 当前处于Plan模式
2. **Plan→Act模式切换** - 刚从Plan模式切换到Act模式
3. **用户手动编辑** - 用户手动编辑了markdown待办文件
4. **到达提醒间隔** - 距离上次更新已达到设定间隔
5. **首次API请求且无列表** - 第一次API请求且没有现有待办列表
6. **多次请求后仍无列表** - 已经进行了2次或更多API请求但仍没有待办列表

## 🎮 运行示例

```bash
# 编译Java文件
javac -d . java-src/run/mone/hive/task/*.java

# 运行示例
java run.mone.hive.task.FocusChainExample
```

## 🔄 与Cline的对应关系

| Cline组件 | Java实现 | 说明 |
|-----------|----------|------|
| FocusChainSettings | FocusChainSettings.java | 配置设置 |
| TaskState | TaskState.java | 任务状态 |
| Mode | Mode.java | 执行模式 |
| file-utils.ts | FocusChainFileUtils.java | 文件操作 |
| FocusChainManager | FocusChainManager.java | 核心管理器 |
| 各种prompts | FocusChainPrompts.java | 提示词模板 |

## 📝 待办列表文件格式

生成的`focus-chain.md`文件格式：

```markdown
# Focus Chain List for Task task-001

<!-- Edit this markdown file to update your focus chain list -->
<!-- Use the format: - [ ] for incomplete items and - [x] for completed items -->

- [x] 分析用户需求
- [x] 设计系统架构
- [ ] 创建项目结构
- [ ] 实现核心功能
- [ ] 编写测试用例
- [ ] 部署到测试环境

<!-- Save this file and the focus chain list will be updated in the task -->
```

## 🎯 设计优势

1. **长期任务连续性保障** - 通过持久化待办列表确保AI不会偏离目标
2. **上下文压缩时的信息保持** - 重要进度信息在上下文压缩时不会丢失
3. **用户可控性和透明度** - 用户可以直接编辑markdown文件
4. **智能提醒机制** - 防止AI在长期任务中失焦
5. **多重保险机制** - 多种触发条件确保系统的鲁棒性

这个Java实现完全保持了Cline Focus Chain的核心功能和设计理念，可以轻松集成到现有的Java项目中。
