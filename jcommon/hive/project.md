# Hive 项目介绍

## 项目概述

Hive 是一个基于 Java 17 的多智能体框架（Multi-Agent Framework），专门设计用于构建智能化的协作系统。该项目借鉴了 MetaGPT、Spring AI 和 Cline 等优秀项目的设计理念，实现了一个功能完备的智能体生态系统。

### 核心理念
- **团队协作**: "一只蜜蜂很傻，但一群蜜蜂很聪明"
- **思考路径**: think -> act -> think -> act
- **角色驱动**: 通过不同角色（Role）协作完成复杂任务

## 技术架构

### 主要依赖
- **Java 17**: 核心运行环境
- **gRPC**: 分布式通信协议
- **Spring Boot**: Web 框架支持
- **Akka**: 并发处理
- **Jackson**: JSON 处理
- **Reactor**: 响应式编程
- **OkHttp**: HTTP 客户端

### 项目结构

```
src/main/java/run/mone/hive/
├── actions/           # 行为定义
├── llm/              # 大语言模型集成
├── roles/            # 角色定义
├── mcp/              # Model Context Protocol
├── memory/           # 记忆管理
├── schema/           # 数据模型
├── task/             # 任务管理
├── planner/          # 规划策略
└── utils/            # 工具类
```

## 核心组件详解

### 1. Role（角色系统）
**文件位置**: `run.mone.hive.roles.Role`

Role 是整个框架的核心抽象，代表一个智能体角色。每个角色都具备：

**核心属性**:
- `name`: 角色名称
- `profile`: 角色描述
- `goal`: 目标定义
- `constraints`: 约束条件
- `actions`: 可执行的行为列表
- `llm`: 关联的大语言模型

**核心方法**:
```java
// 观察环境变化
protected int observe()

// 思考下一步行动
protected int think()

// 执行行为
protected CompletableFuture<Message> act(ActionContext context)

// 运行角色
public CompletableFuture<Message> run()
```

**使用示例**:
```java
Role architect = new Role("Architect", "系统架构师")
    .setActions(new AnalyzeArchitecture(), new WriteDesign())
    .setLlm(llm);
```

### 2. Action（行为系统）
**文件位置**: `run.mone.hive.actions.Action`

Action 定义了角色可以执行的具体行为。

**核心属性**:
- `name`: 行为名称
- `description`: 行为描述
- `function`: 执行逻辑

**使用示例**:
```java
Action chatAction = new Action("chat", "与用户聊天");
chatAction.setFunction((req, action, context) -> {
    // 执行聊天逻辑
    return Message.builder().content("回复内容").build();
});
```

### 3. LLM（大语言模型）
**文件位置**: `run.mone.hive.llm.LLM`

提供与各种大语言模型的集成能力。

**支持的模型**:
- OpenAI GPT 系列
- Claude
- Google Gemini
- 豆包
- 月之暗面
- DeepSeek

**核心功能**:
```java
// 同步聊天
String response = llm.chat("你好");

// 流式聊天
llm.chat(messages, (content, jsonObj) -> {
    // 处理流式响应
});

// 多模态支持
String result = llm.call(textAndImageMessage, systemPrompt);
```

### 4. Team（团队管理）
**文件位置**: `run.mone.hive.Team`

管理多个角色的协作。

**核心功能**:
```java
Team team = new Team(context);

// 雇佣角色
team.hire(architect, programmer, tester);

// 投资预算
team.invest(100.0);

// 启动项目
team.runProject("开发一个网站", "Human", "Architect");
```

### 5. Memory（记忆系统）
**文件位置**: `run.mone.hive.memory.Memory`

提供智能的记忆管理和消息存储。

**核心特性**:
- 支持 FIFO 和 LRU 清退策略
- 自动内存管理
- 索引和检索功能

**使用示例**:
```java
Memory memory = new Memory(MemoryConfig.defaultConfig());
memory.add(message);
List<Message> recent = memory.getRecent(10);
```

### 6. MCP（Model Context Protocol）
**文件位置**: `run.mone.hive.mcp.server.McpServer`

实现了标准的 Model Context Protocol，支持工具调用、资源访问和提示模板。

**使用示例**:
```java
McpServer server = McpServer.using(transport)
    .serverInfo("my-server", "1.0.0")
    .tool(calculatorTool, calculatorHandler)
    .resource(fileResource, fileHandler)
    .async();
```

## 主要功能特性

### 1. 智能意图识别
LLM 类提供了强大的意图识别功能：
```java
IntentClassificationResult result = llm.classifyIntent(
    userInput, 
    Arrays.asList("聊天", "任务执行", "信息查询")
);
```

### 2. 情感AI主动聊天
支持基于聊天历史的主动沟通判断：
```java
EmotionalChatDecisionResult decision = llm.shouldInitiateChat(
    chatHistory, 
    currentTime
);
```

### 3. 模型复杂度自适应
根据任务复杂度自动选择合适的模型：
```java
ModelComplexityResult complexity = llm.classifyModelComplexity(prompt);
```

### 4. 多模态支持
支持文本、图像等多种输入格式：
```java
LLMCompoundMsg msg = LLMCompoundMsg.builder()
    .content("分析这张图片")
    .parts(imageParts)
    .build();
String result = llm.call(msg, systemPrompt);
```

## 使用示例

### 基础使用
```java
// 1. 创建上下文
Context context = new Context();

// 2. 创建团队
Team team = new Team(context);

// 3. 创建角色
Role programmer = new Role("Programmer", "程序员")
    .setActions(new WriteCode(), new TestCode());

// 4. 雇佣角色
team.hire(programmer);

// 5. 启动项目
team.runProject("开发一个计算器", "Human", "Programmer");
```

### 高级使用
```java
// 创建自定义行为
Action customAction = new Action("custom", "自定义行为");
customAction.setFunction((req, action, context) -> {
    // 自定义逻辑
    String result = processTask(req.getMessage().getContent());
    return Message.builder().content(result).build();
});

// 创建具有特定能力的角色
Role analyst = new Role("Analyst", "数据分析师", 
    "分析数据并提供洞察", "必须基于数据事实")
    .setActions(customAction)
    .setSpecializations(Arrays.asList("数据分析", "统计建模"));
```

## 配置说明

### application.properties
```properties
# 启用 SSE 支持
sse.enabled=true
spring.mvc.async.request-timeout=-1
```

### 环境变量
项目支持通过环境变量配置各种 LLM 的 API Key：
- `OPENAI_API_KEY`: OpenAI API 密钥
- `CLAUDE_API_KEY`: Claude API 密钥
- `GEMINI_API_KEY`: Gemini API 密钥

## 部署和运行

### 本地开发
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package
```

### 生产部署
```bash
# 启动应用
java -jar target/hive-1.6.2-jdk21-SNAPSHOT.jar
```

### MCP 服务器模式
```json
{
  "my-server": {
    "command": "java",
    "args": [
      "-jar",
      "/path/to/hive/target/app.jar"
    ]
  }
}
```

## 扩展开发

### 自定义角色
```java
public class CustomRole extends Role {
    public CustomRole() {
        super("CustomRole", "自定义角色");
        // 初始化自定义逻辑
    }
    
    @Override
    protected void doReact(ActionContext ac) {
        // 自定义反应逻辑
        super.doReact(ac);
    }
}
```

### 自定义行为
```java
public class CustomAction extends Action {
    public CustomAction() {
        super("custom", "自定义行为");
        setFunction(this::execute);
    }
    
    private Message execute(ActionReq req, Action action, ActionContext context) {
        // 实现自定义行为逻辑
        return Message.builder()
            .content("执行结果")
            .role(req.getRole().getName())
            .build();
    }
}
```

## 注意事项

1. **内存管理**: 项目包含自动内存管理机制，但大量消息处理时需要注意配置合适的清退策略
2. **并发安全**: Memory 类使用读写锁保证线程安全
3. **成本控制**: Team 类提供预算管理功能，避免 API 调用超支
4. **错误处理**: 所有异步操作都包含完善的异常处理机制

## 版本信息

- **当前版本**: 1.6.2-jdk21-SNAPSHOT
- **Java 版本**: 17+
- **构建工具**: Maven 3.6+

## 许可证

本项目基于开源许可证发布，具体许可证信息请查看项目根目录的 LICENSE 文件。

---

*本文档基于 Hive 项目源码分析生成，如有疑问请参考源码或联系开发团队。*
