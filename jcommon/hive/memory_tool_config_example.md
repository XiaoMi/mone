# MemoryTool 配置使用示例

## 概述

`MemoryTool` 现在支持通过 `MemoryConfig` 进行自定义配置，可以灵活配置 LLM 提供商、嵌入模型、向量存储和图存储等组件。

## 基本用法

### 1. 使用默认配置

```java
// 使用默认配置
MemoryTool memoryTool = new MemoryTool();
```

### 2. 使用自定义配置

```java
// 创建自定义配置
MemoryConfig memoryConfig = MemoryConfig.builder()
    .llm(MemoryConfig.LlmConfig.builder()
        .providerName("QWEN")
        .model("qwen3-max")
        .responseJsonFormat(true)
        .build())
    .embedder(MemoryConfig.EmbedderConfig.builder()
        .provider("ollama")
        .model("qwen3-embedding:4b")
        .embeddingDims(2560)
        .build())
    .vectorStore(MemoryConfig.VectorStoreConfig.builder()
        .provider("chroma")
        .port(8000)
        .collectionName("my_collection")
        .embeddingModelDims(2560)
        .enable(true)
        .build())
    .graphStore(MemoryConfig.GraphStoreConfig.builder()
        .provider("neo4j")
        .enabled(true)
        .url("bolt://localhost:7687")
        .build())
    .historyDbPath("./my_history.db")
    .version("v1.1")
    .build();

// 使用自定义配置创建工具
MemoryTool memoryTool = new MemoryTool(memoryConfig);
```

### 3. 动态设置配置

```java
// 创建工具实例
MemoryTool memoryTool = new MemoryTool();

// 动态设置配置
MemoryConfig config = MemoryConfig.builder()
    .llm(MemoryConfig.LlmConfig.builder()
        .providerName("OPENAI")
        .model("gpt-4")
        .build())
    .build();

MemoryTool.setMemoryConfig(config);
```

## 配置选项详解

### LLM 配置 (LlmConfig)

```java
MemoryConfig.LlmConfig llmConfig = MemoryConfig.LlmConfig.builder()
    .providerName("QWEN")        // LLM提供商名称
    .model("qwen3-max")          // 模型名称
    .responseJsonFormat(true)    // 是否使用JSON格式响应
    .build();
```

### 嵌入模型配置 (EmbedderConfig)

```java
MemoryConfig.EmbedderConfig embedderConfig = MemoryConfig.EmbedderConfig.builder()
    .provider("ollama")          // 嵌入模型提供商
    .model("qwen3-embedding:4b") // 嵌入模型名称
    .embeddingDims(2560)         // 嵌入向量维度
    .build();
```

### 向量存储配置 (VectorStoreConfig)

```java
MemoryConfig.VectorStoreConfig vectorStoreConfig = MemoryConfig.VectorStoreConfig.builder()
    .provider("chroma")              // 向量存储提供商
    .port(8000)                      // 端口号
    .collectionName("test_collection1") // 集合名称
    .embeddingModelDims(2560)        // 嵌入模型维度
    .embeddingFunction("ollama")     // 嵌入函数
    .model("qwen3-embedding:4b")     // 模型名称
    .enable(true)                    // 是否启用
    .build();
```

### 图存储配置 (GraphStoreConfig)

```java
MemoryConfig.GraphStoreConfig graphStoreConfig = MemoryConfig.GraphStoreConfig.builder()
    .provider("neo4j")                   // 图存储提供商
    .enabled(true)                       // 是否启用
    .url("bolt://localhost:7687")        // 连接URL
    .llm(llmConfig)                      // 图存储专用的LLM配置
    .embedder(embedderConfig)            // 图存储专用的嵌入模型配置
    .build();
```

## 配置转换

内部会自动将 `run.mone.hive.mcp.service.MemoryConfig` 转换为 `run.mone.hive.memory.longterm.config.MemoryConfig`：

```java
// MCP服务配置 -> 长期记忆配置的转换过程
run.mone.hive.mcp.service.MemoryConfig mcpConfig = /* 你的配置 */;
run.mone.hive.memory.longterm.config.MemoryConfig longtermConfig = 
    MemoryConfigConverter.convert(mcpConfig);
```

## 完整示例

```java
public class MemoryToolExample {
    public static void main(String[] args) {
        // 创建完整配置
        MemoryConfig memoryConfig = MemoryConfig.builder()
            .llm(MemoryConfig.LlmConfig.builder()
                .providerName("QWEN")
                .model("qwen3-max")
                .responseJsonFormat(true)
                .build())
            .embedder(MemoryConfig.EmbedderConfig.builder()
                .provider("ollama")
                .model("qwen3-embedding:4b")
                .embeddingDims(2560)
                .build())
            .vectorStore(MemoryConfig.VectorStoreConfig.builder()
                .provider("chroma")
                .port(8000)
                .collectionName("my_memory_collection")
                .embeddingModelDims(2560)
                .embeddingFunction("ollama")
                .model("qwen3-embedding:4b")
                .enable(true)
                .build())
            .graphStore(MemoryConfig.GraphStoreConfig.builder()
                .provider("neo4j")
                .enabled(true)
                .url("bolt://localhost:7687")
                .llm(MemoryConfig.LlmConfig.builder()
                    .providerName("QWEN")
                    .model("qwen3-max")
                    .build())
                .embedder(MemoryConfig.EmbedderConfig.builder()
                    .provider("ollama")
                    .model("qwen3-embedding:4b")
                    .embeddingDims(2560)
                    .build())
                .build())
            .historyDbPath("./my_custom_history.db")
            .version("v1.1")
            .build();

        // 使用配置创建工具
        MemoryTool memoryTool = new MemoryTool(memoryConfig);
        
        // 或者动态设置
        MemoryTool.setMemoryConfig(memoryConfig);
        
        // 现在工具将使用你的自定义配置
    }
}
```

## 注意事项

1. **配置优先级**：自定义配置 > 默认配置文件 > 内置默认值
2. **动态配置**：调用 `setMemoryConfig()` 后，下次初始化时会使用新配置
3. **线程安全**：配置设置是线程安全的
4. **配置验证**：系统会自动验证配置的有效性，无效配置会回退到默认值
5. **资源管理**：更换配置时，旧的资源会被正确释放

## 错误处理

如果配置转换失败，系统会：
1. 记录警告日志
2. 使用默认配置继续运行
3. 不会抛出异常影响正常功能
