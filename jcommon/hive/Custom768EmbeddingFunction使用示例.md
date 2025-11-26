# Custom768EmbeddingFunction 使用示例

## 概述

`Custom768EmbeddingFunction` 是一个实现了 ChromaDB `EmbeddingFunction` 接口的自定义768维嵌入函数。它支持调用自定义的768维嵌入服务，并确保所有嵌入向量都是768维。

## 主要特性

- ✅ **ChromaDB兼容**：完全实现 `tech.amikos.chromadb.embeddings.EmbeddingFunction` 接口
- ✅ **固定768维输出**：确保所有嵌入向量都是768维
- ✅ **灵活的API格式支持**：支持多种响应格式（embeddings数组、embedding字段、OpenAI格式等）
- ✅ **批量处理**：支持单个和批量文档嵌入，自动回退机制
- ✅ **错误处理**：完善的异常处理和零向量回退
- ✅ **配置灵活**：支持API密钥、自定义请求头等配置
- ✅ **维度调整**：自动调整不同维度的向量到768维

## 快速开始

### 1. 基本使用

```java
import run.mone.hive.memory.longterm.embeddings.impl.Custom768EmbeddingFunction;
import tech.amikos.chromadb.Embedding;
import tech.amikos.chromadb.embeddings.EFException;

// 创建嵌入函数实例
Custom768EmbeddingFunction embeddingFunction = new Custom768EmbeddingFunction(
    "your-model-name",
    "http://your-embedding-service:8080"
);

// 嵌入单个查询
try {
    Embedding queryEmbedding = embeddingFunction.embedQuery("这是一个测试查询");
    System.out.println("向量维度: " + queryEmbedding.getVector().size()); // 输出: 768
} catch (EFException e) {
    e.printStackTrace();
}

// 嵌入多个文档
List<String> documents = Arrays.asList("文档1", "文档2", "文档3");
try {
    List<Embedding> documentEmbeddings = embeddingFunction.embedDocuments(documents);
    System.out.println("嵌入了 " + documentEmbeddings.size() + " 个文档");
} catch (EFException e) {
    e.printStackTrace();
}
```

### 2. 带API密钥的使用

```java
// 使用API密钥
Custom768EmbeddingFunction embeddingFunction = new Custom768EmbeddingFunction(
    "your-model-name",
    "http://your-embedding-service:8080",
    "your-api-key"
);

// 使用数组形式的文档
String[] documents = {"文档1", "文档2", "文档3"};
List<Embedding> embeddings = embeddingFunction.embedDocuments(documents);
```

### 3. 高级配置

```java
import java.util.HashMap;
import java.util.Map;

// 自定义请求头
Map<String, String> customHeaders = new HashMap<>();
customHeaders.put("X-Custom-Header", "your-value");
customHeaders.put("User-Agent", "YourApp/1.0");

Custom768EmbeddingFunction embeddingFunction = new Custom768EmbeddingFunction(
    "your-model-name",
    "http://your-embedding-service:8080",
    "your-api-key",
    customHeaders
);

// 测试连接
if (embeddingFunction.testConnection()) {
    System.out.println("连接成功！");
} else {
    System.out.println("连接失败！");
}

// 获取服务信息
var serviceInfo = embeddingFunction.getServiceInfo();
System.out.println("服务信息: " + serviceInfo);
```

## API 接口规范

### 单个文本嵌入端点

**URL:** `POST /embed`

**请求格式：**
```json
{
  "model": "your-model-name",
  "input": "要嵌入的文本"
}
```

**响应格式（支持以下任一格式）：**

格式1 - embeddings数组：
```json
{
  "embeddings": [
    [0.1, 0.2, 0.3, ..., 0.768]
  ]
}
```

格式2 - embedding字段：
```json
{
  "embedding": [0.1, 0.2, 0.3, ..., 0.768]
}
```

格式3 - OpenAI格式：
```json
{
  "data": [
    {
      "embedding": [0.1, 0.2, 0.3, ..., 0.768]
    }
  ]
}
```

### 批量文本嵌入端点（可选）

**URL:** `POST /embed/batch`

**请求格式：**
```json
{
  "model": "your-model-name",
  "input": ["文本1", "文本2", "文本3"]
}
```

**响应格式：**
```json
{
  "embeddings": [
    [0.1, 0.2, 0.3, ..., 0.768],
    [0.4, 0.5, 0.6, ..., 0.768],
    [0.7, 0.8, 0.9, ..., 0.768]
  ]
}
```

### 服务信息接口（可选）

**URL:** `GET /info`

**响应格式：**
```json
{
  "model": "your-model-name",
  "dimensions": 768,
  "provider": "custom",
  "version": "1.0.0"
}
```

## 错误处理

### 1. API错误响应

```json
{
  "error": "错误描述信息"
}
```

### 2. 异常处理

- **EFException**: 嵌入过程中的错误会抛出此异常
- **网络错误**: 自动重试和回退机制
- **维度不匹配**: 自动调整到768维
- **空输入**: 返回零向量

### 3. 维度调整策略

- 如果返回的向量维度 > 768：自动截断到768维
- 如果返回的向量维度 < 768：用0填充到768维
- 会在日志中记录调整信息

## 在ChromaDB中使用

### 1. 创建ChromaDB集合

```java
import tech.amikos.chromadb.Client;
import tech.amikos.chromadb.Collection;

// 创建ChromaDB客户端
Client client = new Client("http://localhost:8000");

// 使用自定义嵌入函数创建集合
Custom768EmbeddingFunction embeddingFunction = new Custom768EmbeddingFunction(
    "your-model",
    "http://your-embedding-service:8080"
);

Collection collection = client.createCollection(
    "my_collection",
    null, // metadata
    true, // get_or_create
    embeddingFunction
);
```

### 2. 添加文档

```java
// 添加文档到集合
List<String> documents = Arrays.asList("文档1", "文档2", "文档3");
List<String> ids = Arrays.asList("id1", "id2", "id3");

collection.add(
    null, // embeddings (会自动通过EmbeddingFunction生成)
    null, // metadatas
    documents,
    ids
);
```

### 3. 查询文档

```java
// 查询相似文档
Collection.QueryResponse results = collection.query(
    Arrays.asList("查询文本"), // query_texts
    5, // n_results
    null, // where
    null, // query_embeddings
    null, // where_document
    null  // include
);

System.out.println("找到 " + results.getDocuments().size() + " 个相关文档");
```

## 示例服务实现

这里提供一个简单的 Flask 服务示例：

```python
from flask import Flask, request, jsonify
import numpy as np

app = Flask(__name__)

@app.route('/embed', methods=['POST'])
def embed():
    data = request.json
    text = data.get('input', '')
    model = data.get('model', 'default')
    
    # 这里应该是您的实际嵌入逻辑
    # 示例：返回随机768维向量
    embedding = np.random.rand(768).tolist()
    
    return jsonify({
        'embedding': embedding
    })

@app.route('/embed/batch', methods=['POST'])
def embed_batch():
    data = request.json
    texts = data.get('input', [])
    model = data.get('model', 'default')
    
    embeddings = []
    for text in texts:
        # 实际的批量嵌入逻辑
        embedding = np.random.rand(768).tolist()
        embeddings.append(embedding)
    
    return jsonify({
        'embeddings': embeddings
    })

@app.route('/info', methods=['GET'])
def info():
    return jsonify({
        'model': 'custom-768-embedding',
        'dimensions': 768,
        'provider': 'custom',
        'version': '1.0.0'
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
```

## 完整示例

```java
package example;

import run.mone.hive.memory.longterm.embeddings.impl.Custom768EmbeddingFunction;
import tech.amikos.chromadb.Client;
import tech.amikos.chromadb.Collection;
import tech.amikos.chromadb.Embedding;
import tech.amikos.chromadb.embeddings.EFException;

import java.util.Arrays;
import java.util.List;

public class Custom768EmbeddingExample {
    
    public static void main(String[] args) {
        try {
            // 1. 创建自定义嵌入函数
            Custom768EmbeddingFunction embeddingFunction = new Custom768EmbeddingFunction(
                "my-768-model",
                "http://localhost:8080",
                "your-api-key"
            );
            
            // 2. 测试连接
            if (!embeddingFunction.testConnection()) {
                System.err.println("无法连接到嵌入服务");
                return;
            }
            
            // 3. 单个查询嵌入
            Embedding queryEmbedding = embeddingFunction.embedQuery("搜索查询");
            System.out.println("查询向量维度: " + queryEmbedding.getVector().size());
            
            // 4. 批量文档嵌入
            List<String> documents = Arrays.asList(
                "这是第一个文档的内容",
                "这是第二个文档的内容",
                "这是第三个文档的内容"
            );
            
            List<Embedding> documentEmbeddings = embeddingFunction.embedDocuments(documents);
            System.out.println("成功嵌入 " + documentEmbeddings.size() + " 个文档");
            
            // 5. 在ChromaDB中使用
            Client client = new Client("http://localhost:8000");
            
            Collection collection = client.createCollection(
                "test_collection",
                null,
                true,
                embeddingFunction
            );
            
            // 添加文档
            collection.add(
                null, // 嵌入向量会自动生成
                null, // 元数据
                documents,
                Arrays.asList("doc1", "doc2", "doc3")
            );
            
            // 查询相似文档
            Collection.QueryResponse results = collection.query(
                Arrays.asList("搜索查询"),
                2,
                null, null, null, null
            );
            
            System.out.println("找到相似文档: " + results.getDocuments());
            
        } catch (EFException e) {
            System.err.println("嵌入错误: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("其他错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## 故障排查

### 常见问题

1. **连接失败**
   - 检查 API URL 是否正确
   - 确认服务是否启动
   - 检查网络连接

2. **认证失败**
   - 验证 API 密钥是否正确
   - 检查自定义请求头是否设置正确

3. **维度不匹配**
   - 检查服务返回的向量维度
   - 查看日志中的调整信息

4. **响应格式错误**
   - 确认服务返回的JSON格式符合规范
   - 检查是否包含 error 字段

### 日志级别

- `INFO`：初始化和基本操作信息
- `WARN`：维度调整和回退操作
- `ERROR`：API错误和异常情况
- `DEBUG`：详细的服务信息（需要启用）

这个实现为您提供了一个完整的、生产就绪的768维自定义嵌入解决方案，完全兼容ChromaDB！