# Deep Planning LLM 升级说明

## 概述

本次修改将 `DeepPlanningExample.java` 中的 MockLLM 替换为实际的 LLM 服务调用，使 Deep Planning 功能能够真正与大语言模型进行交互。

## 主要修改内容

### 1. 替换 MockLLM 为真实 LLM 实现

- **移除**: `MockLLM` 静态内部类
- **新增**: `createRealLLM()` 方法，使用 `LLMTaskProcessorImpl` 创建真实的 LLM 服务
- **新增**: `createFallbackLLM()` 方法，提供回退的模拟实现

### 2. 配置支持

- 支持通过环境变量配置 API 密钥：`DEEPSEEK_API_KEY`
- 支持通过环境变量配置 API 地址：`DEEPSEEK_API_URL`
- 支持通过系统属性配置：`deepseek.api.key`, `deepseek.api.url`
- 默认使用 DeepSeek 模型：`deepseek-chat`

### 3. 错误处理和回退机制

- 在真实 LLM 创建失败时，自动回退到模拟实现
- 提供详细的错误信息和配置提示
- 连接测试确保 LLM 服务可用

### 4. 改进的用户体验

- 显示 LLM 配置说明
- 实时状态反馈（真实 LLM vs 回退模拟）
- 更智能的回退模拟响应

## 使用方法

### 1. 配置环境变量（推荐）

```bash
export DEEPSEEK_API_KEY="your-deepseek-api-key"
export DEEPSEEK_API_URL="https://api.deepseek.com"  # 可选
```

### 2. 或使用系统属性

```bash
java -Ddeepseek.api.key="your-api-key" -Ddeepseek.api.url="https://api.deepseek.com" \
     -cp target/classes run.mone.hive.task.DeepPlanningExample
```

### 3. 运行示例

```bash
cd /Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/hive
mvn compile
java -cp target/classes run.mone.hive.task.DeepPlanningExample
```

## 技术实现

### 核心组件

1. **LLMTaskProcessorImpl**: 实际的 LLM 任务处理器，封装了 `run.mone.hive.llm.LLM` 的功能
2. **LLMConfig**: LLM 配置类，支持多种配置选项
3. **回退机制**: 当真实 LLM 不可用时，提供智能的模拟响应

### 配置选项

- `model`: "deepseek-chat"
- `llmProvider`: LLMProvider.DEEPSEEK
- `temperature`: 0.1 (创造性较低，更准确)
- `maxTokens`: 4000
- `debug`: true (启用调试模式)

## 回退模拟功能

当真实 LLM 不可用时，回退实现提供以下智能响应：

1. **Step 1 - 调研阶段**: 模拟代码库分析结果
2. **Step 2 - 问题讨论**: 生成相关的澄清问题
3. **Step 3 - 实施计划**: 创建详细的实施计划文档
4. **Step 4 - 任务创建**: 生成任务进度列表

## 注意事项

1. 确保网络连接正常，能够访问 DeepSeek API
2. API 密钥需要有足够的配额
3. 如果使用企业网络，可能需要配置代理
4. 回退模拟仅用于演示，实际使用建议配置真实 LLM

## 未来扩展

- 支持更多 LLM 提供商（OpenAI、Claude 等）
- 添加 LLM 性能监控和统计
- 支持流式响应
- 添加 LLM 缓存机制
