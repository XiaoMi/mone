# Deep Planning Java Implementation

这是基于Cline Deep Planning功能的Java实现，提供了完整的四步骤深度规划流程。

## 📁 项目结构

```
run.mone.hive.task/
├── SlashCommand.java              # 斜杠命令接口
├── SlashCommandParser.java        # 命令解析器
├── DeepPlanningCommand.java       # Deep Planning命令实现
├── NewTaskCommand.java            # 新任务命令
├── CompactCommand.java            # 压缩命令
├── NewRuleCommand.java            # 新规则命令
├── ReportBugCommand.java          # 报告错误命令
├── TaskCreator.java               # 任务创建器
├── DeepPlanningProcessor.java     # Deep Planning处理器
├── DeepPlanningExample.java       # 完整示例程序
└── README_DeepPlanning.md         # 本文档
```

## 🚀 核心功能

### 1. 斜杠命令解析
- **SlashCommandParser**: 解析XML标签中的斜杠命令
- **支持的命令**: `/deep-planning`, `/newtask`, `/compact`, `/smol`, `/newrule`, `/reportbug`
- **智能匹配**: 支持正则表达式匹配和优先级处理

### 2. Deep Planning四步骤流程

#### Step 1: Silent Investigation（静默调研）
```java
// 自动生成调研命令（根据操作系统适配）
# Unix/Linux
find . -type f -name "*.java" | head -30 | cat
grep -r "class|function|def" --include="*.java" . | cat

# Windows PowerShell  
Get-ChildItem -Recurse -Include "*.java" | Select-Object -First 30
Get-ChildItem -Recurse -Include "*.java" | Select-String -Pattern "class|function"
```

#### Step 2: Discussion and Questions（讨论问题）
- 生成针对性的技术决策问题
- 支持用户交互式回答
- 记录澄清结果用于后续规划

#### Step 3: Create Implementation Plan（创建实施计划）
- 生成结构化的`implementation_plan.md`文件
- 包含8个标准化章节：Overview, Types, Files, Functions, Classes, Dependencies, Testing, Implementation Order
- 提供详细的实施指导

#### Step 4: Create Implementation Task（创建实施任务）
- 自动生成可跟踪的任务步骤
- 与Focus Chain无缝集成
- 支持任务进度管理

### 3. Focus Chain集成
- **自动转换**: Deep Planning生成的步骤自动转为Focus Chain待办列表
- **进度跟踪**: 实时更新任务完成状态
- **文件同步**: 支持用户手动编辑markdown文件

## 🎯 使用方式

### 1. 基本使用

```java
// 创建组件
SlashCommandParser parser = new SlashCommandParser();
FocusChainSettings settings = new FocusChainSettings(true, 6);

// 解析命令
String input = "<task>/deep-planning 添加用户认证功能</task>";
SlashCommandParser.ParseResult result = parser.parseSlashCommands(input, settings);

// 获取生成的提示词
String prompt = result.getProcessedText();
```

### 2. 完整Deep Planning流程

```java
// 初始化处理器
DeepPlanningProcessor processor = new DeepPlanningProcessor(llm, callbacks, focusChainManager);

// 执行Deep Planning
String taskId = processor.executeDeepPlanning(
    "添加用户认证功能，支持JWT令牌和基于角色的访问控制",
    "./project-directory"
);
```

### 3. 任务创建

```java
// 创建任务创建器
TaskCreator creator = new TaskCreator(llm, callbacks);

// 从规划结果创建任务
List<String> taskProgress = Arrays.asList(
    "Create authentication interfaces",
    "Implement JWT token handling", 
    "Add role-based access control",
    "Write comprehensive tests"
);

String taskId = creator.createNewTask(
    "Implement user authentication system",
    taskProgress,
    "./implementation_plan.md"
);
```

## 🧪 运行示例

```bash
# 编译Java文件
cd java-src
javac run/mone/hive/task/*.java

# 运行Deep Planning示例
java run.mone.hive.task.DeepPlanningExample
```

## 📋 生成的文件示例

### implementation_plan.md
```markdown
# Implementation Plan

[Overview]
Implement user authentication with JWT tokens and role-based access control.

This implementation will enhance the existing system by adding secure authentication
mechanisms while maintaining compatibility with current architecture patterns.

[Types]  
Define new data structures and interfaces for authentication.

- AuthenticationRequest: Login credentials
- AuthenticationResponse: JWT token and user info
- UserRole: Enum for role-based permissions
- JwtClaims: Token payload structure

[Files]
File modifications required for the implementation.

New files to be created:
- src/main/java/auth/AuthenticationService.java
- src/main/java/auth/JwtTokenManager.java
- src/main/java/auth/RoleBasedAccessControl.java

[Functions]
Function modifications and additions.

New functions:
- authenticateUser(credentials) in AuthenticationService
- generateJwtToken(user) in JwtTokenManager
- checkPermission(user, resource) in RoleBasedAccessControl

[Implementation Order]
Step-by-step implementation sequence.

1. Create authentication data structures
2. Implement JWT token management
3. Add role-based access control
4. Integrate with existing endpoints
5. Add comprehensive testing
6. Update security documentation
```

### focus-chain.md
```markdown
# Focus Chain List for Task deep-planning-demo

- [ ] Create authentication data structures
- [ ] Implement JWT token management  
- [ ] Add role-based access control
- [ ] Integrate with existing endpoints
- [ ] Add comprehensive testing
- [ ] Update security documentation
```

## 🔧 扩展功能

### 1. 自定义命令
```java
public class CustomCommand implements SlashCommand {
    @Override
    public String getName() {
        return "custom";
    }
    
    @Override
    public String execute(String input, FocusChainSettings settings) {
        return "Custom command implementation";
    }
}

// 注册自定义命令
parser.registerCommand(new CustomCommand());
```

### 2. LLM集成
```java
public class RealLLM implements LLMTaskProcessor {
    @Override
    public String sendMessage(String message) {
        // 调用实际的大模型API
        return callLLMAPI(message);
    }
}
```

### 3. 回调定制
```java
TaskCallbacks customCallbacks = new TaskCallbacks() {
    @Override
    public void say(String type, String message) {
        // 自定义消息处理逻辑
        logToFile(type, message);
        notifyUI(type, message);
    }
};
```

## 🎨 设计特点

### 1. 模块化架构
- **命令解析**: 独立的解析器支持扩展
- **处理器分离**: 每个功能独立的处理器类
- **接口驱动**: 通过接口实现松耦合

### 2. 操作系统适配
- **命令自适应**: 根据操作系统生成不同的shell命令
- **路径处理**: 跨平台的文件路径处理
- **编码支持**: UTF-8编码确保中文支持

### 3. Focus Chain集成
- **无缝对接**: 与Focus Chain系统完美集成
- **双向同步**: 支持程序和用户双向更新
- **进度可视化**: 实时显示任务完成状态

### 4. 错误处理
- **异常捕获**: 完整的异常处理机制
- **回退策略**: 失败时的优雅降级
- **状态恢复**: 支持任务状态恢复

## 🔄 与Cline的对应关系

| Cline组件 | Java实现 | 说明 |
|-----------|----------|------|
| parseSlashCommands | SlashCommandParser | 命令解析 |
| deepPlanningToolResponse | DeepPlanningCommand | Deep Planning提示生成 |
| NewTaskHandler | TaskCreator | 任务创建 |
| FocusChainManager | FocusChainManager | Focus Chain集成 |
| 四步骤流程 | DeepPlanningProcessor | 完整流程协调 |

## 🎯 最佳实践

1. **命令设计**: 保持命令简洁明确，避免复杂参数
2. **提示优化**: 根据实际使用情况调整提示词模板
3. **错误处理**: 为每个步骤添加适当的错误处理
4. **性能考虑**: 大型项目调研时注意命令执行时间
5. **用户体验**: 提供清晰的进度反馈和状态信息

这个Java实现完全保持了Cline Deep Planning的核心设计理念，同时提供了良好的扩展性和可维护性。
