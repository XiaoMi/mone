# Hive长期记忆模块 (Long-term Memory)

本模块是对mem0 Python库的Java版本复刻，为AI智能体提供了强大的长期记忆能力。它支持多种LLM提供商、嵌入模型和向量存储后端，可以智能地存储、检索和管理AI系统的记忆。

## 🎯 核心特性

### 📊 **多层级记忆支持**
- **用户记忆** (User Memory) - 存储用户相关的个人信息和偏好
- **代理记忆** (Agent Memory) - 存储AI代理的学习和经验
- **会话记忆** (Session Memory) - 存储特定会话或运行的上下文
- **过程记忆** (Procedural Memory) - 存储操作流程和程序知识

### 🤖 **多LLM提供商支持**
- **OpenAI** (GPT-4, GPT-3.5, etc.) ✅ 已实现
- **Claude** (Anthropic) ✅ 已实现 - 支持Claude-3.5系列
- **Gemini** (Google) ✅ 已实现 - 支持Gemini-1.5系列
- **Ollama** (本地模型) ✅ 已实现 - 支持本地部署模型
- **Groq** 🚧 接口已定义
- **Azure OpenAI** 🚧 接口已定义
- **AWS Bedrock** 🚧 接口已定义
- **Together AI** 🚧 接口已定义
- **DeepSeek** 🚧 接口已定义
- **xAI (Grok)** 🚧 接口已定义

### 🧠 **多嵌入模型支持**
- **OpenAI Embeddings** (text-embedding-3-small/large) ✅ 已实现
- **Hugging Face** Transformers ✅ 已实现 - 支持各种开源模型
- **Ollama** Embeddings ✅ 已实现 - 支持本地嵌入模型
- **Azure OpenAI** Embeddings 🚧 接口已定义
- **Google Vertex AI** 🚧 接口已定义
- **AWS Bedrock** Embeddings 🚧 接口已定义

### 🗄️ **多向量存储后端**
- **Qdrant** 🚧 架构已实现，待完善
- **Chroma** ✅ 已实现
- **Weaviate** 🚧 接口已定义
- **Pinecone** 🚧 接口已定义
- **FAISS** 🚧 接口已定义
- **Elasticsearch** 🚧 接口已定义
- **Redis** 🚧 接口已定义
- **PgVector** 🚧 接口已定义
- **Milvus** 🚧 接口已定义

### 🕸️ **图数据库支持**
- **Neo4j** 🚧 接口已定义
- **Memgraph** 🚧 接口已定义
- **Kuzu** ✅ 已实现
- **Neptune** 🚧 接口已定义

## 🏗️ 架构设计

```
run.mone.hive.memory.longterm/
├── config/              # 配置管理
│   ├── MemoryConfig.java       # 主配置类
│   ├── LlmConfig.java          # LLM配置
│   ├── EmbedderConfig.java     # 嵌入模型配置
│   ├── VectorStoreConfig.java  # 向量存储配置
│   └── GraphStoreConfig.java   # 图数据库配置
├── core/                # 核心功能
│   ├── MemoryBase.java         # 记忆基础接口
│   └── Memory.java             # 记忆核心实现
├── model/               # 数据模型
│   ├── MemoryItem.java         # 记忆项模型
│   └── Message.java            # 消息模型
├── llm/                 # LLM集成
│   ├── LLMBase.java            # LLM基础接口
│   ├── LLMFactory.java         # LLM工厂
│   └── impl/                   # LLM实现
├── embeddings/          # 嵌入向量处理
│   ├── EmbeddingBase.java      # 嵌入基础接口
│   ├── EmbeddingFactory.java   # 嵌入工厂
│   └── impl/                   # 嵌入实现
├── vectorstore/         # 向量存储
│   ├── VectorStoreBase.java    # 向量存储接口
│   ├── VectorStoreFactory.java # 向量存储工厂
│   └── impl/                   # 向量存储实现
├── graph/               # 图存储
│   ├── GraphStoreBase.java     # 图存储接口
│   ├── GraphStoreFactory.java  # 图存储工厂
│   ├── GraphTools.java         # 图存储工具定义
│   ├── GraphUtils.java         # 图存储工具类
│   └── impl/                   # 图存储实现
│       ├── Neo4jGraphStore.java    # Neo4j实现
│       └── MemgraphGraphStore.java # Memgraph实现
├── storage/             # 历史存储
│   └── HistoryManager.java     # 历史记录管理
├── utils/               # 工具类
│   ├── MessageParser.java      # 消息解析器
│   └── MemoryUtils.java        # 记忆工具类
└── examples/            # 使用示例
    ├── MemoryExample.java            # 基础示例代码
    ├── GraphMemoryExample.java       # 图存储示例代码
    └── ComprehensiveMemoryExample.java # 综合功能示例
```

## 🚀 快速开始

### 1. 基础使用

```java
// 使用默认配置
Memory memory = new Memory();

// 添加记忆
String userId = "user_123";
Map<String, Object> result = memory.add(
    "用户喜欢喝咖啡", 
    userId, null, null, null, true, null, null
);

// 搜索记忆
Map<String, Object> searchResult = memory.search(
    "用户的饮品偏好", 
    userId, null, null, 5, null, null
);

// 关闭资源
memory.close();
```

### 2. 自定义配置

```java
// 创建自定义配置
MemoryConfig config = MemoryConfig.builder()
    .llm(LlmConfig.builder()
        .provider(LlmConfig.Provider.OPENAI)
        .model("gpt-4o-mini")
        .apiKey("your-api-key")
        .build())
    .embedder(EmbedderConfig.builder()
        .provider(EmbedderConfig.Provider.OPENAI)
        .model("text-embedding-3-small")
        .build())
    .vectorStore(VectorStoreConfig.builder()
        .provider(VectorStoreConfig.Provider.CHROMA)
        .host("localhost")
        .port(6333)
        .build())
    .build();

Memory memory = new Memory(config);
```

### 3. 对话记忆

```java
// 添加对话记忆
List<Map<String, Object>> conversation = Arrays.asList(
    Map.of("role", "user", "content", "我住在北京"),
    Map.of("role", "assistant", "content", "好的，我记住了您住在北京")
);

memory.add(conversation, userId, null, null, null, true, null, null);
```

### 4. 过程记忆

```java
// 创建过程记忆
memory.add(conversation, null, agentId, null, 
    Map.of("topic", "技术讨论"), 
    true, "procedural_memory", null);
```

### 5. 图存储支持

```java
// 创建自定义模型配置
LlmConfig llmConfig = LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-4o")  // 可以指定不同模型
                .baseUrl("模型地址")
                .apiKey("模型key")
                .customHeaders(Map.of("自定义header", "header值"))
                .build();
        
// 创建存储配置
MemoryConfig config = MemoryConfig.builder()
    .llm(llmConfig)
    .embedder(EmbedderConfig.builder()
        .provider(EmbedderConfig.Provider.OPENAI)
        .model("text-embedding-3-small") // 可以指定不同模型
        .baseUrl("模型地址")
        .apiKey("模型key")
        .customHeaders(Map.of("自定义header", "header值"))
        .build())
    .vectorStore(VectorStoreConfig.builder()
        .provider(VectorStoreConfig.Provider.CHROMA)
        .collectionName("test_collection")
        .path(tempDir.resolve("vector").toString())
        .embeddingModelDims(1536)
        .embeddingFunction(ChromaVectorStore.OPENAI_EMBEDDING_FUNCTION)
        .apiKey("embedding模型key")
        .baseUrl("embedding模型地址")
        .build())
    .graphStore(GraphStoreConfig.builder()
        .provider(GraphStoreConfig.Provider.KUZU)
        // 可以本地持久化存储或者内存模式运行
        // .url(tempDir.resolve("graph").toString()) // 指定存储路径
        // .url(":memory:") // 内存模式
        .llm(llmConfig)
        .enabled(true)
        .build())
    .historyDbPath(tempDir.resolve("history.db").toString())
    .build(); 

memory = new Memory(config);
log.info("本地存储Memory实例创建成功");

// 添加包含实体关系的记忆
memory.add("张三是北京大学的教授，他住在海淀区。李四是他的学生。", 
          userId, null, null, null, true, null, null);
```

### 6. 异步操作

```java
// 异步添加记忆
CompletableFuture<Map<String, Object>> future = memory.addAsync(
    "异步添加的记忆", userId, null, null, null, true, null, null
);

future.thenAccept(result -> {
    System.out.println("异步操作完成: " + result);
});
```

## 🔧 配置选项

### LLM配置

```java
LlmConfig llmConfig = LlmConfig.builder()
    .provider(LlmConfig.Provider.OPENAI)  // 提供商
    .model("gpt-4o-mini")                 // 模型名称
    .apiKey("your-api-key")               // API密钥
    .baseUrl("https://api.openai.com/v1") // 基础URL
    .temperature(0.1)                     // 温度参数
    .maxTokens(4000)                      // 最大令牌数
    .topP(1.0)                           // top_p参数
    .enableVision(false)                  // 是否启用视觉
    .build();
```

### 嵌入模型配置

```java
EmbedderConfig embedderConfig = EmbedderConfig.builder()
    .provider(EmbedderConfig.Provider.OPENAI)  // 提供商
    .model("text-embedding-3-small")           // 模型名称
    .apiKey("your-api-key")                    // API密钥
    .embeddingDims(1536)                       // 向量维度
    .build();
```

### 向量存储配置

```java
VectorStoreConfig vectorConfig = VectorStoreConfig.builder()
    .provider(VectorStoreConfig.Provider.QDRANT)  // 提供商
    .collectionName("memory_collection")          // 集合名称
    .host("localhost")                            // 主机地址
    .port(6333)                                   // 端口
    .embeddingModelDims(1536)                     // 向量维度
    .build();
```

### 图存储配置

```java
// Neo4j配置
GraphStoreConfig neo4jConfig = GraphStoreConfig.builder()
    .provider(GraphStoreConfig.Provider.NEO4J)   // 提供商
    .url("bolt://localhost:7687")                // 连接URL
    .username("neo4j")                           // 用户名
    .password("password")                        // 密码
    .database("neo4j")                           // 数据库名
    .enabled(true)                               // 是否启用
    .config(Map.of(
        "base_label", true,                      // 使用基础标签
        "custom_prompt", "Extract person and location entities"  // 自定义提示词
    ))
    .build();

// Memgraph配置
GraphStoreConfig memgraphConfig = GraphStoreConfig.builder()
    .provider(GraphStoreConfig.Provider.MEMGRAPH)
    .url("bolt://localhost:7687")
    .username("memgraph")
    .password("memgraph")
    .enabled(true)
    .build();
```

## 🎮 API参考

### 核心方法

| 方法 | 描述 | 参数 |
|------|------|------|
| `add()` | 添加记忆 | messages, userId, agentId, runId, metadata, infer, memoryType, prompt |
| `search()` | 搜索记忆 | query, userId, agentId, runId, limit, filters, threshold |
| `get()` | 获取单个记忆 | memoryId |
| `getAll()` | 获取所有记忆 | userId, agentId, runId, filters, limit |
| `update()` | 更新记忆 | memoryId, data |
| `delete()` | 删除记忆 | memoryId |
| `deleteAll()` | 删除所有记忆 | userId, agentId, runId |
| `history()` | 获取记忆历史 | memoryId |
| `reset()` | 重置存储 | 无 |

### 异步方法

所有核心方法都有对应的异步版本，方法名后加`Async`后缀。

## 🔍 内存工作原理

### 1. 事实提取
- 使用LLM从对话中提取关键事实
- 支持自定义事实提取提示词
- 过滤无关信息，只保留重要内容

### 2. 记忆去重与更新
- 检索现有相关记忆
- 使用LLM判断是否需要添加、更新或删除
- 支持记忆的智能合并和冲突解决

### 3. 向量存储
- 将记忆转换为高维向量
- 支持语义相似度搜索
- 提供多种向量存储后端选择

### 4. 历史追踪
- 记录所有记忆变更历史
- 支持记忆恢复和审计
- 提供详细的操作日志

## 🛡️ 错误处理

```java
try {
    Memory memory = new Memory();
    // 记忆操作
} catch (IllegalArgumentException e) {
    // 配置错误
    log.error("配置错误: {}", e.getMessage());
} catch (RuntimeException e) {
    // 运行时错误 (API调用失败等)
    log.error("运行时错误: {}", e.getMessage());
} finally {
    if (memory != null) {
        memory.close();
    }
}
```

## 🔄 完整示例

查看 [`src/test/java/run/mone/hive/memory/longterm/examples/MemoryExample.java`](../../../../../../../test/java/run/mone/hive/memory/longterm/examples/MemoryExample.java) 文件获取完整的使用示例，包括：
- 基础使用示例
- 自定义配置示例
- 异步操作示例
- 记忆管理示例
- 参考: [src/test/java/run/mone/hive/memory/longterm/README.md](../../../../../../../test/java/run/mone/hive/memory/longterm/README.md)

## 🚧 开发状态

- ✅ **已完成**: 
  - 核心架构和配置管理
  - LLM提供商: OpenAI、Claude、Gemini、Ollama
  - 嵌入模型: OpenAI、HuggingFace、Ollama
  - 图数据库支持: Kuzu架构
  - 向量存储架构: Chroma架构
  - 异步操作和记忆管理
  - 完整示例和文档
- 🚧 **进行中**: 其他向量存储后端、其他图数据库实现
- 📋 **计划中**: 高级记忆策略、知识图谱推理、多模态支持

## 🤝 贡献指南

1. 实现新的LLM提供商
2. 添加向量存储后端
3. 优化记忆检索算法
4. 完善文档和示例

## 📄 许可证

本项目遵循与hive项目相同的许可证。

---

**注意**: 这是mem0 Python库的Java移植版本，保持了原有的API设计理念，同时适应了Java的编程范式。某些功能可能需要相应的依赖库支持。
