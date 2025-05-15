# Hive Client

这是一个用于与Hive Task API进行交互的Java客户端。

## 功能特性

- 创建任务
- 获取任务信息
- 获取任务列表
- 更新任务状态
- 更新任务结果
- 执行任务
- 获取任务状态

## 安装

确保在pom.xml中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.12.0</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.9.1</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.12.0</version>
    </dependency>
</dependencies>
```

## 使用方法

### 创建客户端

```java
// 使用默认URL创建客户端
HiveClient client = new HiveClient();

// 或使用自定义URL
HiveClient client = new HiveClient("http://your-hive-server:8080");

// 设置认证令牌
client.withToken("your-jwt-token");
```

### 创建任务

```java
HiveClient.Task task = HiveClient.Task.builder()
        .username("username")
        .clientAgentId(1L)
        .serverAgentId(2L)
        .status("PENDING")
        .build();

HiveClient.Task createdTask = client.createTask(task);
```

### 获取任务

```java
HiveClient.Task task = client.getTask("task-uuid");
```

### 获取任务列表

```java
// 获取所有任务
List<HiveClient.Task> tasks = client.getTasks();

// 根据客户端代理ID获取任务
List<HiveClient.Task> tasks = client.getTasks(clientAgentId, null);

// 根据服务端代理ID获取任务
List<HiveClient.Task> tasks = client.getTasks(null, serverAgentId);
```

### 更新任务状态

```java
HiveClient.Task updatedTask = client.updateTaskStatus("task-uuid", "RUNNING");
```

### 更新任务结果

```java
HiveClient.Task updatedTask = client.updateTaskResult("task-uuid", "{\"result\": \"success\"}");
```

### 执行任务

```java
HiveClient.TaskExecutionInfo taskInfo = HiveClient.TaskExecutionInfo.builder()
        .taskId("task-id")
        .userName("username")
        .metadata(Map.of("param1", "value1", "param2", "value2"))
        .build();

HiveClient.Task executedTask = client.executeTask(taskInfo);
```

### 获取任务状态

```java
Map<String, Object> status = client.getTaskStatus("task-uuid");
```

## 错误处理

所有方法都可能抛出IOException异常，建议进行适当的异常处理：

```java
try {
    HiveClient.Task task = client.getTask("task-uuid");
    // 处理任务
} catch (IOException e) {
    // 处理异常
    log.error("获取任务失败", e);
}
```

## 辅助类

项目还提供了TaskClient辅助类，它封装了HiveClient并提供了更简单的接口，同时内置了异常处理：

```java
TaskClient taskClient = new TaskClient("http://localhost:8080")
        .withToken("your-jwt-token");

// 创建任务
HiveClient.Task task = taskClient.createTask("username", 1L, 2L);

// 执行任务
Map<String, String> metadata = new HashMap<>();
metadata.put("param1", "value1");
metadata.put("param2", "value2");
HiveClient.Task executedTask = taskClient.executeTask("task-id", "username", metadata);
``` 