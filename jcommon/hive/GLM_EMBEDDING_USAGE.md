# GLM Embedding 使用指南

## 概述

`GlmEmbedding` 是智谱AI GLM嵌入模型的Java实现，支持将文本转换为高维向量表示，用于语义搜索、相似度计算等任务。

## 特性

- 支持单个文本和批量文本嵌入
- 自动错误处理和重试机制
- 支持自定义请求头和配置
- 完整的测试覆盖
- 与现有嵌入框架无缝集成

## 快速开始

### 1. 基本配置

```java
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.embeddings.impl.GlmEmbedding;

// 创建配置
EmbedderConfig config = EmbedderConfig.builder()
    .provider(EmbedderConfig.Provider.GLM)
    .model("embedding-3")
    .apiKey("your-api-key-here")
    .baseUrl("https://open.bigmodel.cn/api/paas/v4")
    .embeddingDims(1024)
    .build();

// 创建嵌入实例
GlmEmbedding embedding = new GlmEmbedding(config);
```

### 2. 使用默认配置

```java
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;

// 使用默认配置创建GLM嵌入实例
EmbeddingBase embedding = EmbeddingFactory.createGlmDefault();

// 或者使用配置创建
EmbedderConfig config = EmbedderConfig.glmDefault();
config.setApiKey("your-api-key-here");
EmbeddingBase embedding = EmbeddingFactory.create(config);
```

## 使用示例

### 单个文本嵌入

```java
// 嵌入单个文本
String text = "你好，今天天气怎么样";
List<Double> vector = embedding.embed(text, "add");

System.out.println("嵌入向量维度: " + vector.size());
System.out.println("前5个维度值: " + vector.subList(0, 5));
```

### 批量文本嵌入

```java
// 批量嵌入多个文本
List<String> texts = Arrays.asList(
    "你好，今天天气怎么样",
    "Hello, how are you?",
    "机器学习是人工智能的一个分支"
);

List<List<Double>> vectors = embedding.embedBatch(texts, "add");

for (int i = 0; i < texts.size(); i++) {
    System.out.println("文本: " + texts.get(i));
    System.out.println("向量维度: " + vectors.get(i).size());
}
```

### 使用便捷方法

```java
// 使用默认的"add"动作
List<Double> vector = embedding.embed("测试文本");

// 使用枚举类型指定动作
List<Double> vector = embedding.embed("测试文本", EmbeddingBase.MemoryAction.SEARCH);
```

## 配置选项

### 基本配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| provider | Provider | GLM | 嵌入提供商 |
| model | String | embedding-3 | 模型名称 |
| apiKey | String | null | API密钥 |
| baseUrl | String | https://open.bigmodel.cn/api/paas/v4 | API基础URL |
| embeddingDims | int | 1024 | 嵌入向量维度 |

### 高级配置

```java
EmbedderConfig config = EmbedderConfig.builder()
    .provider(EmbedderConfig.Provider.GLM)
    .model("embedding-3")
    .apiKey("your-api-key")
    .baseUrl("https://open.bigmodel.cn/api/paas/v4")
    .embeddingDims(1024)
    .customHeaders(Map.of(
        "User-Agent", "MyApp/1.0",
        "X-Custom-Header", "custom-value"
    ))
    .config(Map.of(
        "timeout", 30000,
        "retries", 3
    ))
    .build();
```

## 支持的模型

| 模型名称 | 维度 | 说明 |
|----------|------|------|
| embedding-3 | 1024 | 最新的嵌入模型 |
| embedding-2 | 1024 | 上一代嵌入模型 |

```java
// 获取支持的模型列表
List<String> models = GlmEmbedding.getSupportedModels();

// 获取模型的默认维度
int dims = GlmEmbedding.getModelDimensions("embedding-3");
```

## 错误处理

```java
try {
    List<Double> vector = embedding.embed("测试文本");
    // 处理成功结果
} catch (RuntimeException e) {
    if (e.getMessage().contains("API key")) {
        // 处理API密钥错误
        System.err.println("API密钥无效或未设置");
    } else if (e.getMessage().contains("rate limit")) {
        // 处理速率限制
        System.err.println("请求频率过高，请稍后重试");
    } else {
        // 处理其他错误
        System.err.println("嵌入生成失败: " + e.getMessage());
    }
}
```

## 连接测试

```java
// 测试API连接
boolean isConnected = embedding.testConnection();
if (isConnected) {
    System.out.println("GLM API连接正常");
} else {
    System.out.println("GLM API连接失败");
}

// 验证API密钥
boolean isValidKey = embedding.validateApiKey();
if (isValidKey) {
    System.out.println("API密钥有效");
} else {
    System.out.println("API密钥无效");
}
```

## 性能优化

### 批量处理

对于大量文本，建议使用批量嵌入以提高性能：

```java
// 推荐：批量处理
List<String> texts = Arrays.asList(/* 大量文本 */);
List<List<Double>> vectors = embedding.embedBatch(texts, "add");

// 不推荐：逐个处理
for (String text : texts) {
    List<Double> vector = embedding.embed(text, "add");
    // 处理单个向量
}
```

### 连接池配置

GLM嵌入使用Java 11的HttpClient，支持连接池和超时配置：

```java
// HttpClient会自动管理连接池
// 超时时间在请求中设置为3分钟（单个请求）或5分钟（批量请求）
```

## 集成示例

### 与向量数据库集成

```java
// 创建嵌入实例
EmbeddingBase embedding = EmbeddingFactory.createGlmDefault();
embedding.getConfig().setApiKey("your-api-key");

// 嵌入文档
List<String> documents = Arrays.asList(
    "文档1内容",
    "文档2内容",
    "文档3内容"
);

List<List<Double>> vectors = embedding.embedBatch(documents, "add");

// 存储到向量数据库
for (int i = 0; i < documents.size(); i++) {
    String docId = "doc_" + i;
    List<Double> vector = vectors.get(i);
    // vectorStore.insert(docId, vector, documents.get(i));
}

// 搜索相似文档
String query = "搜索查询";
List<Double> queryVector = embedding.embed(query, "search");
// List<SearchResult> results = vectorStore.search(queryVector, 10);
```

## 注意事项

1. **API密钥安全**: 不要在代码中硬编码API密钥，建议使用环境变量或配置文件
2. **速率限制**: 注意GLM API的速率限制，避免过于频繁的请求
3. **错误重试**: 对于网络错误，建议实现指数退避重试机制
4. **维度一致性**: 确保所有嵌入向量使用相同的维度设置
5. **文本长度**: 注意GLM API对输入文本长度的限制

## 环境变量配置

```bash
# 设置API密钥
export GLM_API_KEY="your-api-key-here"

# 设置自定义API端点（可选）
export GLM_BASE_URL="https://open.bigmodel.cn/api/paas/v4"
```

```java
// 从环境变量读取配置
String apiKey = System.getenv("GLM_API_KEY");
String baseUrl = System.getenv("GLM_BASE_URL");

EmbedderConfig config = EmbedderConfig.builder()
    .provider(EmbedderConfig.Provider.GLM)
    .apiKey(apiKey)
    .baseUrl(baseUrl != null ? baseUrl : "https://open.bigmodel.cn/api/paas/v4")
    .build();
```

## 故障排除

### 常见问题

1. **401 Unauthorized**: 检查API密钥是否正确设置
2. **429 Too Many Requests**: 降低请求频率或实现重试机制
3. **500 Internal Server Error**: GLM服务暂时不可用，稍后重试
4. **网络超时**: 检查网络连接或增加超时时间

### 调试日志

启用详细日志以便调试：

```java
// 在logback.xml或log4j配置中设置
<logger name="run.mone.hive.memory.longterm.embeddings.impl.GlmEmbedding" level="DEBUG"/>
```

这样就完成了GLM嵌入模型的完整实现和文档。

