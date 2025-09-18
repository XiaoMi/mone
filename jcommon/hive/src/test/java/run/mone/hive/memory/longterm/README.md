# 长期记忆模块测试指南

本目录包含了长期记忆模块的各种测试用例，帮助您验证功能是否正常工作。

## 📋 测试文件说明

### 1. `MemoryConfigTest.java` - 配置测试
- **目的**: 测试各种配置类的创建和验证
- **依赖**: 无外部依赖
- **运行**: 可以直接运行，无需任何API key或外部服务

```bash
# 使用Maven运行
mvn test -Dtest=MemoryConfigTest

# 或者直接运行main方法
java -cp target/classes:target/test-classes run.mone.hive.memory.longterm.MemoryConfigTest
```

### 2. `LongTermMemoryQuickStartTest.java` - 快速开始测试
- **目的**: 演示基本使用方式，包含完整的功能流程
- **依赖**: 使用mock配置，不需要真实API
- **运行**: 适合本地快速验证功能架构

```bash
# 使用Maven运行
mvn test -Dtest=LongTermMemoryQuickStartTest

# 或者直接运行main方法
java -cp target/classes:target/test-classes run.mone.hive.memory.longterm.LongTermMemoryQuickStartTest
```

### 3. `MemoryIntegrationTest.java` - 集成测试
- **目的**: 测试与真实API和服务的集成
- **依赖**: 可选的真实API key和外部服务
- **运行**: 支持渐进式测试，根据环境变量决定测试范围

## 🚀 快速开始测试

### 最简单的测试方式

```bash
# 1. 编译项目
mvn compile test-compile

# 2. 运行配置测试（无依赖）
mvn test -Dtest=MemoryConfigTest

# 3. 运行快速开始测试（mock模式）
mvn test -Dtest=LongTermMemoryQuickStartTest
```

### 使用真实API测试

如果您有OpenAI API key，可以运行真实API测试：

```bash
# 设置环境变量
export OPENAI_API_KEY="your-api-key-here"

# 运行集成测试
mvn test -Dtest=MemoryIntegrationTest
```

### 完整功能测试

如果您有完整的环境（包括Qdrant、Neo4j等），可以运行完整测试：

```bash
# 设置所需的环境变量
export OPENAI_API_KEY="your-openai-key"
export ANTHROPIC_API_KEY="your-claude-key"  # 可选
export GOOGLE_API_KEY="your-gemini-key"     # 可选
export ENABLE_GRAPH_TEST="true"             # 启用图存储测试

# 确保服务运行
# - Qdrant: http://localhost:6333
# - Neo4j: bolt://localhost:7687

# 运行所有测试
mvn test -Dtest="run.mone.hive.memory.longterm.*Test"
```

## 🔧 环境准备

### 1. 无依赖测试（推荐开始）
- 不需要任何外部服务
- 不需要API key
- 验证代码结构和配置系统

### 2. 基础API测试
- 需要：OpenAI API key
- 设置：`export OPENAI_API_KEY="sk-..."`
- 验证：LLM和嵌入模型调用

### 3. 向量存储测试
- 需要：Qdrant服务运行在 `localhost:6333`
- 启动：`docker run -p 6333:6333 qdrant/qdrant`

### 4. 图存储测试
- 需要：Neo4j服务运行在 `localhost:7687`
- 启动：`docker run -p 7474:7474 -p 7687:7687 -e NEO4J_AUTH=neo4j/password neo4j`
- 设置：`export ENABLE_GRAPH_TEST="true"`

## 📊 测试覆盖范围

### MemoryConfigTest
- ✅ LLM配置创建和验证
- ✅ 嵌入模型配置创建
- ✅ 向量存储配置创建
- ✅ 图存储配置创建
- ✅ 完整配置组装
- ✅ 配置序列化/反序列化

### LongTermMemoryQuickStartTest
- ✅ 基础记忆添加和搜索
- ✅ 对话记忆处理
- ✅ 异步操作
- ✅ 配置灵活性
- ✅ 图存储功能检查

### MemoryIntegrationTest
- ✅ 真实API调用
- ✅ 多提供商配置
- ✅ 图存储集成
- ✅ 端到端功能验证

## 🐛 故障排除

### 常见问题

1. **编译错误**
   ```bash
   # 确保Java版本正确
   java -version  # 应该是Java 11+
   
   # 清理并重新编译
   mvn clean compile test-compile
   ```

2. **API key问题**
   ```bash
   # 检查环境变量
   echo $OPENAI_API_KEY
   
   # 确保key格式正确
   # OpenAI: sk-...
   # Anthropic: sk-ant-...
   ```

3. **外部服务连接问题**
   ```bash
   # 检查Qdrant
   curl http://localhost:6333/health
   
   # 检查Neo4j
   curl http://localhost:7474
   ```

### 日志级别调整

如果需要更详细的日志：

```bash
# 设置日志级别
export LOGGING_LEVEL_ROOT=DEBUG

# 或者在测试中添加
-Dlogging.level.run.mone.hive.memory.longterm=DEBUG
```

## 🎯 测试建议

1. **从简单开始**: 先运行 `MemoryConfigTest`
2. **逐步增加**: 再运行 `LongTermMemoryQuickStartTest`
3. **真实环境**: 最后运行 `MemoryIntegrationTest`
4. **持续验证**: 在开发过程中经常运行测试

## 📝 测试输出示例

成功的测试输出应该类似：

```
[INFO] === 初始化长期记忆模块测试环境 ===
[INFO] 长期记忆模块初始化完成
[INFO] === 开始基础记忆操作测试 ===
[INFO] 添加用户偏好记忆...
[INFO] 添加结果1: {results=[...]}
[INFO] ✅ 基础记忆操作测试通过
[INFO] 🎉 长期记忆模块快速验证测试全部通过！
```

如果看到类似输出，说明长期记忆模块工作正常！
