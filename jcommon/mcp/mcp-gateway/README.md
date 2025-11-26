# mcp-gateway

基于 Hive MCP 框架的 Gateway Agent 项目

## 项目信息

- **Agent名称**: gateway
- **项目名称**: mcp-gateway
- **端口**: 9790 (gRPC), 8083 (HTTP)

## 快速开始

### 1. 配置环境变量

在启动前需要配置以下环境变量：

```bash
export hive_manager_token=your_token_here
export hive_manager_base_url=your_base_url_here
export GATEWAY_URL='{"staging":"http://staging-gateway.com","online":"http://online-gateway.com"}'
export GATEWAY_API_KEY=your_api_key
export GATEWAY_API_USER=your_username
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

- `mcp.agent.name`: Agent名称（gateway）
- `mcp.agent.group`: Agent分组（staging）
- `mcp.grpc.port`: gRPC服务端口（9790）
- `mcp.transport.type`: 传输协议类型（http）
- `hive.manager.base-url`: Hive Manager的基础URL
- `mcp.llm`: 使用的LLM模型（deepseek）

## 项目结构

```
mcp-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── run/mone/mcp/gateway/
│   │   │       ├── GatewayMcpBootstrap.java          # 启动类
│   │   │       ├── config/
│   │   │       │   └── GatewayAgentConfig.java       # Agent配置
│   │   │       ├── function/
│   │   │       │   └── ApiFunction.java              # API操作Function
│   │   │       ├── service/
│   │   │       │   ├── GatewayService.java           # Gateway服务
│   │   │       │   └── bo/
│   │   │       │       └── ListApiInfoParam.java     # 参数对象
│   │   │       └── http/
│   │   │           └── HttpClient.java               # HTTP客户端
│   │   └── resources/
│   │       └── application.properties                # 应用配置
│   └── test/
└── pom.xml
```

## 功能说明

### ApiFunction

Gateway API操作工具，支持以下操作：

1. **listApiInfo** - 查询API列表信息
   - 参数：
     - `operation`: "listApiInfo"
     - `env`: 环境类型（staging/online）
     - `keyword`: 模糊搜索关键字（可选）

2. **detailByUrl** - 根据URL获取API详细信息
   - 参数：
     - `operation`: "detailByUrl"
     - `env`: 环境类型（staging/online）
     - `url`: API的URL地址

## 依赖说明

本项目依赖以下核心组件：

- Spring Boot: 应用框架
- Hive: MCP Agent框架（1.6.2-jdk21-SNAPSHOT）
- Hive Spring Starter: Spring Boot集成（1.6.0-jdk21-SNAPSHOT）
- OkHttp: HTTP客户端（4.9.1）

## 日志文件

日志文件默认保存在：`~/mcp/gateway.log`

## 开发指南

### 修改Agent行为

修改 `GatewayAgentConfig.java` 中的 RoleMeta 配置：

- `profile`: Agent的角色定位
- `goal`: Agent的目标
- `constraints`: Agent的约束条件
- `mcpTools`: Agent可以使用的MCP工具列表
- `workflow`: Agent的工作流程

### 添加新的Function

1. 在 `function` 包下创建新的类，实现 `McpFunction` 接口
2. 在 `GatewayAgentConfig` 的 `mcpTools` 列表中添加新的Function实例

## 重构说明

本项目已按照 `mcp-miline-new` 的架构模式进行重构：

### 主要变更

1. **ApiFunction** - 从 `Function<Map<String, Object>, McpSchema.CallToolResult>` 改为实现 `McpFunction` 接口
   - 返回类型从 `CallToolResult` 改为 `Flux<CallToolResult>`
   - 添加了日志记录
   - 优化了错误处理

2. **配置架构** - 采用 Hive Spring Starter 自动配置
   - 删除了旧的 `GatewayMcpServer` 手动配置
   - 删除了旧的 `GatewayMcpConfig`
   - 新增 `GatewayAgentConfig` 配置 RoleMeta Bean
   - Spring Boot 会通过 `hive-spring-starter` 自动配置和启动 MCP 服务器

3. **依赖管理** - 添加必要的依赖
   - 添加 `hive-spring-starter` 依赖
   - 更新 `hive` 版本到 1.6.2-jdk21-SNAPSHOT

4. **应用配置** - 更新 application.properties
   - 添加 MCP 传输配置（http/grpc）
   - 添加 Hive Manager 注册配置
   - 添加 Agent 元信息配置

### 架构优势

- **自动配置**: 通过 Spring Boot Auto Configuration 自动配置 MCP 服务器
- **统一标准**: 与其他 MCP 项目保持一致的架构风格
- **易于扩展**: 添加新功能只需创建新的 Function 类并注册到 RoleMeta
- **灵活部署**: 支持 HTTP 和 gRPC 两种传输协议

## 许可证

本项目遵循 Apache License 2.0 许可证。
