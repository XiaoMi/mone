# RoleCommand注解使用示例

## 概述

`@RoleCommand`注解用于标记Role命令处理类，被标记的类将被自动扫描并注册到`RoleCommandFactory`中。

## 注解参数

- `value`: 命令名称，如果不指定则使用类名
- `description`: 命令描述
- `priority`: 命令优先级，数值越小优先级越高（默认100）

## 使用示例

### 1. 基本使用

```java
@Slf4j
@RoleCommand(value = "/ping", description = "测试连接状态，返回pong响应", priority = 1)
public class PingCommand extends RoleBaseCommand {
    
    public PingCommand(RoleService roleService) {
        super(roleService);
    }
    
    @Override
    public boolean matches(Message message) {
        // 实现匹配逻辑
        return message.getContent().equals("/ping");
    }
    
    @Override
    public void execute(Message message, FluxSink<String> sink, String from, ReactorRole role) {
        // 实现命令执行逻辑
        sink.next("pong");
        sink.complete();
    }
    
    @Override
    public String getCommandName() {
        return "/ping";
    }
    
    @Override
    public String getCommandDescription() {
        return "测试连接状态，返回pong响应";
    }
}
```

### 2. 高级使用

```java
@Slf4j
@RoleCommand(value = "/config", description = "获取当前Agent配置信息", priority = 10)
public class GetConfigCommand extends RoleBaseCommand {
    
    public GetConfigCommand(RoleService roleService) {
        super(roleService);
    }
    
    // ... 实现方法
}
```

## 工作原理

1. **ApplicationContextHolder**: 静态持有Spring的ApplicationContext，用于在非Spring管理的类中获取Bean
2. **自动扫描**: `RoleCommandFactory`在初始化时会自动扫描所有带`@RoleCommand`注解的Bean
3. **优先级排序**: 命令按priority值排序，数值越小优先级越高
4. **避免重复注册**: 如果命令已经手动注册过，将跳过自动注册

## 配置要求

确保Spring容器能够扫描到带`@RoleCommand`注解的类：

```java
@ComponentScan(basePackages = "run.mone.hive.mcp.service.command")
```

## 注意事项

1. 被注解的类必须继承`RoleBaseCommand`
2. 类必须被Spring管理（通过`@Component`或其他方式）
3. `ApplicationContextHolder`必须正确初始化
4. 优先级相同的命令，执行顺序不确定

## 迁移指南

### 从手动注册迁移到注解

**之前的方式：**
```java
public RoleCommandFactory(RoleService roleService) {
    registerCommand(new PingCommand(roleService));
    registerCommand(new GetConfigCommand(roleService));
    // ...
}
```

**新的方式：**
```java
@RoleCommand(value = "/ping", priority = 1)
public class PingCommand extends RoleBaseCommand {
    // ...
}

@RoleCommand(value = "/config", priority = 10)  
public class GetConfigCommand extends RoleBaseCommand {
    // ...
}
```

工厂类会自动扫描和注册这些命令。
