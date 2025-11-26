# Dayu 服务限流功能

## 功能概述

本模块在原有的 Dayu 服务查询功能基础上，新增了服务限流管理功能，支持通过 MCP (Model Context Protocol) 接口对 Dayu 微服务治理中心的限流规则进行增删改查操作。

## 主要功能

### 1. 限流规则查询

- 查询指定应用的所有限流规则
- 支持按服务名、方法名过滤
- 显示限流规则的详细配置信息

### 2. 限流规则创建

- 创建新的限流规则
- 支持单机限流和集群限流
- 支持 QPS 和线程数两种限流条件
- 支持快速失败、冷启动、排队等待三种限流效果

### 3. 限流规则更新

- 更新现有限流规则的配置
- 支持启用/禁用限流规则
- 支持修改限流阈值和效果

### 4. 限流规则删除

- 删除指定的限流规则
- 支持按规则 ID 删除

## 使用方法

### 通过 MCP 接口调用

#### 1. 查询限流规则列表

```json
{
  "action": "list",
  "app": "your-app-name"
}
```

#### 2. 创建限流规则

```json
{
  "action": "create",
  "app": "your-app-name",
  "service": "your-service-name",
  "method": "your-method-name",
  "clusterMode": false,
  "grade": 1,
  "count": 100,
  "controlBehavior": 0,
  "strategy": 0,
  "enabled": true
}
```

#### 3. 更新限流规则

```json
{
  "action": "update",
  "app": "your-app-name",
  "id": "rule-id",
  "service": "your-service-name",
  "count": 200,
  "enabled": false
}
```

#### 4. 删除限流规则

```json
{
  "action": "delete",
  "app": "your-app-name",
  "id": "rule-id"
}
```

### 通过自然语言交互

系统支持通过自然语言进行限流管理，例如：

- "查询应用 myapp 的限流规则"
- "为服务 userservice 创建 QPS 限流，阈值 100"
- "更新限流规则，设置集群限流，总体阈值 500"
- "删除限流规则 ID 12345"

## 参数说明

### 基本参数

- `action`: 操作类型，支持 `list`、`create`、`update`、`delete`、`detail`
- `app`: 应用名称（必填）
- `service`: 服务名称
- `method`: 方法名称
- `id`: 限流规则 ID（更新/删除/详情时必填）

### 限流配置参数

- `clusterMode`: 限流类型
  - `false`: 单机限流
  - `true`: 集群限流
- `grade`: 条件类型
  - `0`: 线程数
  - `1`: QPS
- `count`: 阈值
- `controlBehavior`: 限流效果
  - `0`: 快速失败
  - `1`: 冷启动
  - `2`: 排队等待
- `strategy`: 限流模式
  - `0`: 直接限流
- `thresholdType`: 阈值模式（仅集群限流时有效）
  - `0`: 单机均摊
  - `1`: 总体阈值
- `warmUpPeriodSec`: 预热时长（秒，冷启动时有效）
- `maxQueueingTimeMs`: 超时时间（毫秒，排队等待时有效）
- `fallbackClass`: 降级服务类名
- `fallbackMethod`: 降级方法名
- `enabled`: 是否启用限流规则

## 配置说明

### 环境变量

- `DAYU_BASE_URL`: Dayu 服务基础 URL
- `DAYU_AUTH_TOKEN`: 认证 Token
- `DAYU_COOKIE`: Cookie 信息（可选）

### 系统属性

- `dayu.base-url`: Dayu 服务基础 URL
- `dayu.auth-token`: 认证 Token
- `dayu.cookie`: Cookie 信息（可选）

## 示例场景

### 场景 1：为高并发服务设置限流

```json
{
  "action": "create",
  "app": "user-service",
  "service": "UserService",
  "method": "getUserInfo",
  "clusterMode": true,
  "grade": 1,
  "count": 1000,
  "controlBehavior": 0,
  "strategy": 0,
  "thresholdType": 1,
  "enabled": true
}
```

### 场景 2：设置冷启动限流

```json
{
  "action": "create",
  "app": "order-service",
  "service": "OrderService",
  "method": "createOrder",
  "clusterMode": false,
  "grade": 1,
  "count": 100,
  "controlBehavior": 1,
  "strategy": 0,
  "warmUpPeriodSec": 10,
  "enabled": true
}
```

### 场景 3：设置排队等待限流

```json
{
  "action": "create",
  "app": "payment-service",
  "service": "PaymentService",
  "method": "processPayment",
  "clusterMode": false,
  "grade": 1,
  "count": 50,
  "controlBehavior": 2,
  "strategy": 0,
  "maxQueueingTimeMs": 5000,
  "enabled": true
}
```

## 注意事项

1. 创建限流规则时，`service` 参数是必填的
2. 更新或删除限流规则时，`id` 参数是必填的
3. 集群限流需要配置 `thresholdType` 参数
4. 冷启动限流需要配置 `warmUpPeriodSec` 参数
5. 排队等待限流需要配置 `maxQueueingTimeMs` 参数
6. 所有时间参数的单位要正确（秒/毫秒）

## 错误处理

系统会返回详细的错误信息，包括：

- 参数验证错误
- 网络请求错误
- 服务器响应错误
- 业务逻辑错误

请根据错误信息进行相应的处理。
