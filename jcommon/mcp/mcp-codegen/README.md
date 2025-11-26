# MCP Codegen Agent

一个专门用于生成代码项目的MCP Agent，支持生成Spring Boot业务项目和MCP Agent项目。

## 功能特性

### 1. 生成Spring Boot业务项目 (BizGen)

生成完整的企业级Spring Boot项目，包含：
- ✅ Spring Boot 3.x 基础框架
- ✅ Spring Security + JWT 认证
- ✅ JPA + H2 数据库
- ✅ 用户认证体系（User, UserRepository, UserService）
- ✅ 完整的配置类（Security, WebMvc, App）
- ✅ JWT 认证过滤器和工具类
- ✅ 统一 API 响应格式
- ✅ 完整的用户DTO
- ✅ RESTful 控制器
- ✅ @AuthUser 注解和参数解析器
- ✅ HTTP 日志记录切面
- ✅ 全局异常处理
- ✅ Logback 日志配置
- ✅ Hive Agent 配置

### 2. 生成MCP Agent项目 (AgentGen)

生成基于Hive框架的MCP Agent项目，包含：
- ✅ Hive MCP 框架集成
- ✅ gRPC 服务支持
- ✅ Spring Boot 启动类
- ✅ Agent 配置类（Profile、Goal、Constraints）
- ✅ application.properties 配置文件
- ✅ logback.xml 日志配置
- ✅ README.md 项目文档

## 使用方法

### 配置

在 Cursor 的 MCP 配置中添加：

```json
{
  "codegen-mcp": {
    "command": "java",
    "args": [
      "-jar",
      "/path/to/jcommon/mcp/mcp-codegen/target/app.jar"
    ]
  }
}
```

### 编译项目

```bash
cd /path/to/jcommon/mcp/mcp-codegen
mvn clean package
```

### 运行Agent

```bash
java -jar target/app.jar
```

## Agent配置

- **Agent名称**: codegen
- **Agent分组**: staging
- **gRPC端口**: 9187
- **角色定位**: 代码生成专家
- **目标**: 生成高质量的项目代码框架
- **LLM模型**: qwen

## 提供的Function

### 1. generate_biz_project

生成完整的Spring Boot业务项目。

**参数**：
- `projectPath`: 项目生成路径（必需）
- `projectName`: 项目名称（必需）
- `groupId`: Maven GroupId（必需）
- `packageName`: 包名（必需）
- `author`: 作者（必需）
- `versionId`: 版本号（必需）
- `description`: 项目描述（必需）
- `springBootVersion`: Spring Boot版本（可选，默认3.2.0）
- `javaVersion`: Java版本（可选，默认21）
- `serverPort`: 服务端口（可选，默认8080）
- `dbName`: 数据库名称（可选，默认使用项目名）
- `jwtSecret`: JWT密钥（可选）
- `jwtExpiration`: JWT过期时间（可选）

**示例**：
```
帮我生成一个电商项目，项目名叫 my-shop，包名用 run.mone.shop
```

### 2. generate_agent_project

生成基于Hive的MCP Agent项目。

**参数**：
- `projectPath`: 项目生成路径（必需）
- `projectName`: 项目名称（必需）
- `groupId`: Maven GroupId（必需）
- `packageName`: 包名（必需）
- `author`: 作者（必需）
- `versionId`: 版本号（必需）
- `agentName`: Agent名称（必需）
- `agentGroup`: Agent分组（必需）
- `agentProfile`: Agent简介（必需）
- `agentGoal`: Agent目标（必需）
- `agentConstraints`: Agent约束（必需）
- `parentGroupId`: 父项目GroupId（可选）
- `parentArtifactId`: 父项目ArtifactId（可选）
- `parentVersion`: 父项目版本（可选）
- `grpcPort`: gRPC端口（可选）
- `hiveManagerUrl`: Hive Manager地址（可选）
- `llmModel`: LLM模型（可选）
- `javaVersion`: Java版本（可选）

**示例**：
```
帮我生成一个测试Agent，这个Agent专门用来编写单元测试
```

## 使用示例

### 生成业务项目

```
用户：帮我生成一个在线书店项目，项目名叫 bookstore

AI会自动调用 generate_biz_project 函数，智能推断参数：
- projectPath: /tmp/biz-projects
- projectName: bookstore
- groupId: run.mone
- packageName: run.mone.bookstore
- author: 当前用户
- versionId: 1.0.0
- description: Online Bookstore System
```

### 生成Agent项目

```
用户：生成一个专门处理Excel文件的MCP Agent

AI会自动调用 generate_agent_project 函数，智能推断参数：
- projectName: mcp-excel-processor
- agentName: excel-processor
- agentProfile: 你是一名Excel数据处理专家
- agentGoal: 你的目标是帮助用户高效处理Excel文件
- agentConstraints: 只处理Excel相关的任务
```

## 环境要求

- Java 21+
- Maven 3.6+
- Spring Boot 2.7.14

## 作者

goodjava@qq.com

## 日期

2025/10/7


