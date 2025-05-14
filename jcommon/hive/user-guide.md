# 项目文档

## 概述

Hive 是一个基于大语言模型（LLM）的智能代理框架，灵感来源于蜜蜂社会的集体智能。框架的核心理念是：一只蜜蜂可能很简单，但一群蜜蜂协同工作时能展现出惊人的智能。该项目借鉴了 MetaGpt 和 Spring AI/Cline 等优秀开源项目的设计思想，构建了一套完整的多角色协作系统。

## 核心理念

- **团队协作**：通过多个AI角色协同工作解决复杂问题
- **思考-行动循环**：采用 think->act->think->act 的思考路径
- **角色专业化**：每个角色专注于特定领域或技能
- **可扩展性**：支持自定义角色、动作和工具
- **成本控制**：内置预算管理机制，确保LLM使用成本可控

## 系统架构

### 系统组件图

```
+-------------------+     +-------------------+     +-------------------+
|       Team        |---->|    Environment    |---->|     Context       |
+-------------------+     +-------------------+     +-------------------+
         |                        |                         |
         v                        v                         v
+-------------------+     +-------------------+     +-------------------+
|       Role        |---->|      Action       |---->|       LLM         |
+-------------------+     +-------------------+     +-------------------+
     /        \                /        \               /        \
+--------+  +--------+  +--------+  +--------+  +--------+  +--------+
|Writer  |  |Architect|  |WritePlan|  |CodeGen |  |OpenAI  |  |Claude  |
+--------+  +--------+  +--------+  +--------+  +--------+  +--------+
```

### 核心组件

1. **Team**：团队是各个角色的容器，负责协调各角色之间的通信和工作流程
2. **Role**：角色代表不同的专业人员，如架构师、程序员、数据库专家等
3. **Action**：角色执行的具体行动，如编写计划、分析架构、编写代码等
4. **Environment**：环境是角色交互的空间，管理消息传递和状态
5. **LLM**：大语言模型接口，支持多种模型如 OpenAI、Claude、Gemini 等
6. **Context**：上下文管理，维护角色和动作的执行环境和状态
7. **Message**：消息系统，角色之间通过消息进行通信

### 关键模块详解

#### 1. 角色系统 (roles)

角色是Hive的核心组件，代表着不同的专业人员：

- **Role**：所有角色的基类，提供基础功能和通信能力
  - 管理消息队列和处理
  - 支持行动选择和执行
  - 提供反应模式（REACT/ORDER）

- **ReactorRole**：响应式角色，能够根据环境变化动态调整行为
  - 实现更复杂的事件响应模式
  - 支持事件驱动的行为触发

- **常见专业角色**：
  - **Writer**：专业写作角色，处理文档和内容创作
  - **Architect**：架构设计师，负责系统架构规划
  - **DatabaseAssistant**：数据库专家，处理数据库设计和查询优化
  - **Teacher**：教学角色，提供教育和指导
  - **Coordinator**：协调者，管理多角色合作
  - **Human**：代表人类用户的角色，接受和处理用户输入

#### 2. 行动系统 (actions)

行动代表角色能够执行的具体任务，是角色能力的外在表现：

- **Action**：所有行动的基类，定义行动的基本接口和属性
  - 包含名称、描述和执行逻辑
  - 支持异步执行返回CompletableFuture

- **ActionGraph**：行动图，组织和管理复杂的行动序列
  - 支持DAG（有向无环图）结构
  - 实现行动之间的依赖关系和流程控制

- **ActionNode**：行动节点，行动图的基本单元
  - 包含输入、输出和执行逻辑
  - 支持条件执行和错误处理

- **专业行动类型**：
  - **WritePlan**：编写计划和方案
  - **WriteDesign**：设计系统架构和组件
  - **AnalyzeArchitecture**：分析现有架构
  - **UserRequirement**：处理用户需求
  - **ActionSelectionAction**：行动选择器
  - **HumanConfirmAction**：获取人类确认
  - **WriteCodePlanAndChange**：代码规划和实现
  - **各领域专用行动**：如数据库操作、教学计划等

#### 3. 语言模型接口 (llm)

语言模型接口是与AI大模型交互的核心组件：

- **LLM**：语言模型基类，提供统一的接口
  - 支持文本生成、聊天、多模态输入
  - 处理模型响应和错误
  - 支持流式输出(stream)和JSON格式化输出

- **LLMProvider**：语言模型提供商枚举
  - 支持多种主流模型（OpenAI、Claude、Gemini等）
  - 提供模型URL和默认配置

- **LLMConfig**：模型配置类
  - 支持temperature、max_tokens等参数
  - 管理API密钥和端点URL
  - 调整模型行为（流式输出、JSON格式等）

- **特殊功能支持**：
  - 多模态输入（文本+图像）
  - 语音合成和识别
  - Web搜索增强

#### 4. 上下文系统 (context)

管理角色和任务的上下文信息，是状态保持的关键：

- **Context**：上下文基类
  - 维护默认LLM和成本管理器
  - 支持序列化和反序列化
  - 设置语言偏好

- **RoleContext**：角色上下文，管理角色状态
  - 维护消息历史和状态
  - 支持反应模式配置
  - 管理当前任务和行动

- **ActionContext**：行动上下文，提供行动执行环境
  - 传递行动参数和结果
  - 支持上下文共享和传递

- **专业上下文**：
  - **CodingContext**：编码相关上下文
  - **TestingContext**：测试相关上下文
  - **其他领域专用上下文**

#### 5. 消息系统 (schema)

消息是角色之间通信的基本单位：

- **Message**：消息基类
  - 包含内容、角色、发送方和接收方
  - 支持元数据和额外参数
  - 处理消息路由和接收者判断

- **ActionReq**：行动请求
  - 封装行动请求参数
  - 支持参数验证和默认值

- **AiMessage**：AI消息
  - 专用于与LLM交互的消息格式
  - 支持模型特定参数

#### 6. 规划系统 (planner)

规划系统帮助团队规划和优化工作流程：

- **Planner**：规划器基类
  - 生成工作计划和任务分解
  - 评估任务优先级和依赖关系

- **PlanningStrategy**：规划策略接口
  - 定义不同的规划方法
  - 支持策略切换和适配

- **TeamBuilder**：团队构建器
  - 基于任务需求组建最优团队
  - 评估角色能力和适配度

## 使用指南

### 基本使用流程

1. 创建一个团队（Team）并设置上下文（Context）
2. 招募角色（hire）并进行投资（invest）
3. 运行项目（runProject）并指定启动角色
4. 运行会话循环（run）进行多轮对话

### 示例代码

#### 基础示例：创建团队并执行任务

```java
// 创建上下文和团队
Context context = new Context();
LLMConfig config = LLMConfig.builder()
    .llmProvider(LLMProvider.OPENAI)
    .model("gpt-4")
    .temperature(0.7)
    .maxTokens(2000)
    .build();
context.setDefaultLLM(new LLM(config));
Team team = new Team(context);

// 招募角色
team.hire(
    new Architect("架构师"),
    new Writer("技术作家"),
    new DatabaseAssistant()
);

// 设置预算
team.invest(20.0);

// 运行项目
team.runProject("开发一个电子商务网站，包含商品管理、用户管理和订单处理功能", "Human", "Architect");

// 运行会话循环
List<Message> results = team.run(10, "", "", true).join();
```

#### 高级示例：自定义角色和行动

```java
// 创建自定义角色
class SecurityExpert extends Role {
    public SecurityExpert() {
        super("安全专家", "我是网络安全专家，专注于系统安全评估和加固");
        
        // 初始化角色
        init();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 设置行动
        setActions(
            new SecurityAuditAction(),
            new VulnerabilityAssessmentAction(),
            new SecurityRecommendationAction()
        );
    }
}

// 自定义行动
class SecurityAuditAction extends Action {
    public SecurityAuditAction() {
        super("安全审计", "对系统进行全面安全审计");
    }
    
    @Override
    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        // 行动实现逻辑
        return CompletableFuture.supplyAsync(() -> {
            // 执行安全审计
            String auditResult = "安全审计完成，发现以下安全问题：...";
            
            return Message.builder()
                .content(auditResult)
                .role(this.getRole().getName())
                .causeBy(this.getClass().getName())
                .build();
        });
    }
}

// 使用自定义角色
team.hire(new SecurityExpert());
```

### 环境配置

#### 系统要求

- JDK 17 或更高版本
- Maven 3.6+ 用于构建
- 足够的内存资源（建议4GB以上）

#### 构建与运行

1. 克隆项目
   ```bash
   git clone https://github.com/your-repo/hive.git
   cd hive
   ```

2. 使用Maven构建
   ```bash
   mvn clean package
   ```

3. 运行应用
   ```bash
   java -jar target/app.jar
   ```

4. IDE调试运行（添加必要参数）
   ```bash
   -Deditable.java.test.console=true
   ```

### 配置参数

#### LLM配置

```java
LLMConfig config = LLMConfig.builder()
    .llmProvider(LLMProvider.OPENAI)  // 提供商：OPENAI, CLAUDE, DEEPSEEK, GOOGLE
    .model("gpt-4")                   // 模型名称
    .temperature(0.7)                 // 温度参数
    .maxTokens(2000)                  // 最大tokens
    .url("https://api.openai.com/v1/chat/completions")  // API URL
    .token("your-api-key")            // API密钥
    .stream(true)                     // 是否使用流式输出
    .json(false)                      // 是否强制JSON输出
    .build();
```

## 高级功能

### 消息路由与处理

消息路由是系统的核心机制，决定了消息如何从一个角色传递到另一个角色：

```java
// 创建一个消息并发送
Message message = Message.builder()
    .content("我需要一个数据库设计方案")
    .role("Human")
    .sentFrom("用户")
    .sendTo(Lists.newArrayList("DatabaseAssistant"))
    .causeBy(UserRequirement.class.getName())
    .build();

// 发布消息
team.publishMessage(message);
```

消息处理流程：
1. 消息被发布到环境（Environment）
2. 环境根据sendTo和其他属性路由消息
3. 目标角色接收消息并放入队列
4. 角色处理消息并执行相应行动
5. 行动结果作为新消息发布，形成对话循环

### 行动图与复杂工作流

行动图（ActionGraph）用于构建复杂的工作流：

```java
// 创建行动图
ActionGraph graph = new ActionGraph();

// 创建行动节点
ActionNode analyzeNode = new ActionNode(new AnalyzeArchitecture());
ActionNode designNode = new ActionNode(new WriteDesign());
ActionNode codeNode = new ActionNode(new WriteCodePlanAndChange());

// 添加节点依赖关系
graph.addNode(analyzeNode);
graph.addNode(designNode);
graph.addNode(codeNode);

// 设置依赖关系
graph.addEdge(analyzeNode, designNode);
graph.addEdge(designNode, codeNode);

// 执行行动图
graph.execute();
```

### 多模态输入与输出

Hive支持多模态输入和输出，包括文本、图像和语音：

```java
// 创建包含图像的消息
Message imageMessage = Message.builder()
    .content("这是图像描述")
    .images(Lists.newArrayList("image_url_or_base64"))
    .role("User")
    .build();

// 语音生成
byte[] speechData = llm.generateSpeech("这是要转换为语音的文本", "alloy", "output.mp3");
```

### 异步执行与并行处理

Hive大量使用CompletableFuture实现异步处理：

```java
// 异步执行角色任务
CompletableFuture<List<Message>> future = team.run(5, "实现登录功能", "Programmer", false);

// 添加回调处理
future.thenAccept(messages -> {
    messages.forEach(msg -> System.out.println(msg.getContent()));
});

// 等待完成
List<Message> results = future.join();
```

## API参考

### 主要类

#### Team

团队是角色的容器，管理角色协作和消息流。

主要方法：
- `hire(Role... roles)`: 招募角色
- `invest(double investment)`: 设置预算
- `runProject(String idea, String sendFrom, String sendTo)`: 启动项目
- `run(int rounds)`: 运行指定轮数的对话循环
- `publishMessage(Message message)`: 发布消息到环境

#### Role

角色代表各种专业人士，是系统的核心组件。

主要方法：
- `setActions(List<Action> actions)`: 设置角色可执行的行动
- `react()`: 响应环境消息并执行行动
- `putMessage(Message message)`: 接收消息
- `processMessage(Message message)`: 处理消息内容

#### Environment

环境是角色交互的场所，负责消息路由和状态管理。

主要方法：
- `publishMessage(Message message)`: 发布消息到环境
- `getHistory()`: 获取消息历史
- `addRoles(List<Role> roleList)`: 添加角色到环境
- `startDialogue(String content, String roleFrom, String roleTo)`: 启动对话

#### LLM

语言模型接口，提供与各种AI模型的交互能力。

主要方法：
- `chat(String prompt)`: 与模型进行单轮对话
- `chat(List<AiMessage> msgList)`: 与模型进行多轮对话
- `ask(String prompt)`: 异步请求模型回答
- `generateSpeech(String text)`: 生成语音

#### Context

上下文管理器，维护系统状态和配置。

主要方法：
- `llm()`: 获取默认语言模型
- `serialize()`: 序列化上下文
- `deserialize(Map<String, Object> context)`: 反序列化上下文

#### Action

行动是角色能够执行的具体任务。

主要方法：
- `run(ActionReq map, ActionContext context)`: 执行行动
- `getDescription()`: 获取行动描述
- `setRole(Role role)`: 设置行动所属角色

## 最佳实践

### 角色设计原则

1. **单一职责**: 每个角色应有明确的专业领域和职责
2. **行动明确**: 角色的行动应该具体、可执行，避免过于宽泛
3. **适当描述**: 角色描述应详细说明其专长和限制
4. **上下文共享**: 角色间应通过消息系统共享上下文，避免直接调用

### 行动设计原则

1. **原子性**: 行动应尽量小且独立，便于组合
2. **参数清晰**: 输入参数和返回值应有明确定义
3. **异常处理**: 妥善处理可能的异常情况
4. **异步友好**: 支持异步执行和并行处理

### 系统优化建议

1. **控制消息量**: 避免生成太多无意义的消息
2. **精简提示词**: 设计高效的提示词减少token消耗
3. **缓存结果**: 对重复或相似查询使用缓存
4. **合理预算**: 根据任务复杂度设置合理预算

## 常见问题解答

1. **如何选择合适的LLM模型？**
   - 简单任务：可以使用较小模型如GPT-3.5
   - 复杂任务：推荐使用高级模型如GPT-4、Claude 3或同等能力模型
   - 特殊任务：选择领域专用模型或具有相关能力的模型

2. **如何优化模型使用成本？**
   - 使用投资（invest）功能设置预算
   - 减少不必要的消息交互
   - 适当降低温度参数减少冗余输出
   - 对重复任务使用缓存

3. **如何调试角色行为？**
   - 设置LLMConfig的debug为true查看详细日志
   - 检查环境的消息历史（Environment.getHistory()）
   - 使用specializations来优化角色的任务匹配

4. **如何处理并发和异步问题？**
   - 合理使用CompletableFuture的组合函数
   - 注意消息处理顺序和依赖关系
   - 使用线程安全的数据结构存储共享状态

## 贡献指南

我们欢迎社区贡献，以下是参与方式：

1. **代码贡献**
   - Fork项目并创建分支
   - 开发新功能或修复Bug
   - 提交Pull Request

2. **文档贡献**
   - 改进现有文档
   - 添加示例和教程
   - 翻译文档

3. **测试与反馈**
   - 报告Bug和问题
   - 提出功能建议
   - 编写单元测试

## 技术细节

### 依赖库

- **日志**: Logback
- **HTTP客户端**: OkHttp
- **JSON处理**: Gson、Jackson
- **异步处理**: Java CompletableFuture
- **反应式编程**: Reactor Core
- **模板引擎**: Beetl
- **Web框架**: Spring系列 (可选)

### 可扩展点

1. **新角色**: 继承Role类创建专业角色
2. **新行动**: 继承Action类添加行动能力
3. **新LLM提供商**: 扩展LLMProvider和LLM类
4. **新上下文类型**: 扩展Context类创建专用上下文
