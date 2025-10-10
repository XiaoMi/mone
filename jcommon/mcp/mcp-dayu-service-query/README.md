# Dayu 服务查询 MCP 模块

基于 Dayu 微服务治理中心的服务查询 MCP 模块，提供通过 MCP 协议查询微服务列表的功能。

## 功能特性

- 🔍 **服务名搜索**: 支持按服务名进行模糊搜索
- 📊 **服务列表查询**: 获取服务的详细信息，包括分组、版本、所属应用等
- 🔐 **认证支持**: 支持通过 Token 进行身份验证
- 📄 **分页查询**: 支持分页查询，避免大量数据一次性返回
- 🎯 **精确过滤**: 支持按分组、应用等条件进行过滤

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
