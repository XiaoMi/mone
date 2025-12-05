# mcp-miline-new

基于 Hive MCP 框架的 Agent 项目

## 项目信息

- **Agent名称**: miline-new
- **项目名称**: mcp-miline-new

## 快速开始

### 1. 配置环境变量

在启动前需要配置 Hive Manager 的 token：

```bash
export hive_manager_token=your_token_here
export hive_manager_base_url=your_base_url_here
```

### 2. 编译项目

```bash
mvn clean package
```

### 3. 运行项目

```bash
java -jar target/app.jar
```

或者直接运行：

```bash
mvn spring-boot:run
```

## 配置说明

主要配置文件位于 `src/main/resources/application.properties`：

- `mcp.agent.name`: Agent名称
- `mcp.agent.group`: Agent分组（dev/staging/production）
- `mcp.grpc.port`: gRPC服务端口
- `hive.manager.base-url`: Hive Manager的基础URL
- `mcp.llm`: 使用的LLM模型

## 项目结构

```
mcp-miline-new/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── run/mone/mcp/milinenew/
│   │   │       ├── MilineNewMcpBootstrap.java      # 启动类
│   │   │       └── config/
│   │   │           └── AgentConfig.java # Agent配置
│   │   └── resources/
│   │       └── application.properties   # 应用配置
│   └── test/
└── pom.xml
```

## 日志文件

日志文件默认保存在：`~/mcp/miline-new.log`

## 依赖说明

本项目依赖以下核心组件：

- Spring Boot: 应用框架
- Hive: MCP Agent框架
- Hive Spring Starter: Spring Boot集成

## 开发指南

### 修改Agent行为

修改 `AgentConfig.java` 中的 RoleMeta 配置：

- `profile`: Agent的角色定位
- `goal`: Agent的目标
- `constraints`: Agent的约束条件
- `tools`: Agent可以使用的工具列表

### 添加新的工具

在 `AgentConfig.java` 的 `tools` 列表中添加新的工具实例。

## 许可证

本项目遵循 Apache License 2.0 许可证。

