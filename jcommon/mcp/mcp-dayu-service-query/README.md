# Dayu 服务查询与限流管理 MCP 模块

基于 Dayu 微服务治理中心的服务查询和限流管理 MCP 模块，提供通过 MCP 协议查询微服务列表和管理服务限流规则的功能。

## 功能特性

### 服务查询功能

- 🔍 **服务名搜索**: 支持按服务名进行模糊搜索
- 📊 **服务列表查询**: 获取服务的详细信息，包括分组、版本、所属应用等
- 🔐 **认证支持**: 支持通过 Token 进行身份验证
- 📄 **分页查询**: 支持分页查询，避免大量数据一次性返回
- 🎯 **精确过滤**: 支持按分组、应用等条件进行过滤

### 服务限流管理功能

- 🚦 **限流规则管理**: 支持创建、查询、更新、删除限流规则
- 🔄 **多种限流类型**: 支持单机限流和集群限流
- 📈 **灵活限流条件**: 支持 QPS 和线程数两种限流条件
- ⚡ **多种限流效果**: 支持快速失败、冷启动、排队等待三种限流效果
- 🛡️ **降级配置**: 支持配置降级服务和降级方法
- 🎛️ **智能路由**: 支持通过自然语言进行限流管理

## 配置说明

### 环境变量

- `DAYU_AUTH_TOKEN`: Dayu 系统的认证 Token（可选）

### 配置文件

在 `application.properties` 中配置：

```properties
# Dayu 微服务治理中心配置
dayu.base-url=http://your-dayu-server:8080
dayu.auth-token=${DAYU_AUTH_TOKEN:}

# MCP 传输配置
mcp.transport.type=stdio
mcp.grpc.port=9187
```

## 使用方法

### 1. 构建模块

```bash
cd jcommon/mcp/mcp-dayu-service-query
mvn clean package
```

### 2. 运行模块

```bash
java -jar target/app.jar
```

### 3. MCP 工具调用

#### 基本服务查询

```json
{
  "name": "dayu_service_query",
  "arguments": {
    "serviceName": "TeslaGatewayService"
  }
}
```

#### 高级查询（带过滤条件）

```json
{
  "name": "dayu_service_query",
  "arguments": {
    "serviceName": "GatewayService",
    "group": "car_online",
    "application": "tesla",
    "page": 1,
    "pageSize": 20,
    "myParticipations": false
  }
}
```

#### 服务限流管理

##### 查询限流规则列表

```json
{
  "name": "dayu_service_limit_flow",
  "arguments": {
    "action": "list",
    "app": "your-app-name"
  }
}
```

##### 创建限流规则

```json
{
  "name": "dayu_service_limit_flow",
  "arguments": {
    "action": "create",
    "app": "your-app-name",
    "service": "your-service-name",
    "method": "your-method-name",
    "clusterMode": false,
    "grade": 1,
    "count": 100,
    "controlBehavior": 0,
    "enabled": true
  }
}
```

##### 更新限流规则

```json
{
  "name": "dayu_service_limit_flow",
  "arguments": {
    "action": "update",
    "app": "your-app-name",
    "id": "rule-id",
    "count": 200,
    "enabled": false
  }
}
```

##### 删除限流规则

```json
{
  "name": "dayu_service_limit_flow",
  "arguments": {
    "action": "delete",
    "app": "your-app-name",
    "id": "rule-id"
  }
}
```

#### 自然语言交互

系统支持通过自然语言进行服务查询和限流管理：

**服务查询示例：**

- "查询服务 TeslaGatewayService"
- "搜索包含 Gateway 的服务"
- "查看应用 tesla 下的所有服务"

**限流管理示例：**

- "查询应用 myapp 的限流规则"
- "为服务 userservice 创建 QPS 限流，阈值 100"
- "更新限流规则，设置集群限流，总体阈值 500"
- "删除限流规则 ID 12345"

## 参数说明

| 参数名           | 类型    | 必填 | 说明                                 |
| ---------------- | ------- | ---- | ------------------------------------ |
| serviceName      | string  | 是   | 要搜索的服务名称，支持模糊匹配       |
| group            | string  | 否   | 服务分组过滤                         |
| application      | string  | 否   | 所属应用过滤                         |
| page             | integer | 否   | 页码，从 1 开始，默认为 1            |
| pageSize         | integer | 否   | 每页大小，默认为 10                  |
| myParticipations | boolean | 否   | 是否只查询我参与的服务，默认为 false |

## 返回结果格式

```
=== Dayu 服务查询结果 ===
总记录数: 13672
当前页: 1/1368
每页大小: 10

服务列表:
服务名                                                          分组                  版本            所属应用                      (实例数: 2)
------                                                          ----                  ----            --------
com.youpin.xiaomi.tesla.service.TeslaGatewayService             1144-92175                           14232-boyfatscaleprop        (实例数: 1)
com.xiaomi.sautumn.serverless.api.sl.FunctionManagerService     1319                                 15735-reportwashendst        (实例数: 1)
com.xiaomi.sautumn.api.service.SautumnService                   1729                                 21188-echo                    (实例数: 1)
```

## 集成到父项目

在父项目的 `pom.xml` 中添加模块：

```xml
<modules>
    <!-- 其他模块... -->
    <module>mcp-dayu-service-query</module>
</modules>
```

## 注意事项

1. 确保 Dayu 微服务治理中心服务可访问
2. 如果启用了认证，需要提供有效的 Token
3. 建议在生产环境中配置合适的超时时间
4. 大量数据查询时建议使用分页功能

## 故障排除

### 常见问题

1. **连接失败**: 检查 `dayu.base-url` 配置是否正确
2. **认证失败**: 检查 `dayu.auth-token` 是否有效
3. **查询超时**: 调整 HTTP 客户端超时配置

### 日志查看

```bash
# 查看详细日志
java -jar target/app.jar --logging.level.run.mone.mcp.dayu=DEBUG
```
