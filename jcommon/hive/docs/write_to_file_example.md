# WriteToFileTool 使用示例

这个文档展示了如何使用新实现的 `WriteToFileTool` 工具。

## 工具简介

`WriteToFileTool` 是一个Java版本的文件写入工具，参考了Cline项目中的 `write_to_file` 工具实现。它用于将内容写入指定路径的文件，如果文件存在则覆盖，如果不存在则创建。

## 主要特性

1. **文件创建**: 创建新文件并写入内容
2. **文件覆盖**: 完全覆盖现有文件的内容
3. **自动创建目录**: 自动创建写入文件所需的任何目录
4. **内容预处理**: 自动移除markdown代码块标记等
5. **多种文件类型支持**: 支持文本文件、源代码、配置文件等
6. **UTF-8编码**: 支持各种字符集和特殊字符

## 使用方法

### 1. 添加工具到ReactorRole

```java
ReactorRole role = new ReactorRole();
WriteToFileTool writeToFileTool = new WriteToFileTool();
role.addTool(writeToFileTool);
```

### 2. 创建新的Java类文件

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/java/com/example/HelloWorld.java");
input.addProperty("content", """
    package com.example;
    
    public class HelloWorld {
        public static void main(String[] args) {
            System.out.println("Hello, World!");
        }
    }
    """);

JsonObject result = writeToFileTool.execute(role, input);
```

### 3. 创建配置文件

```java
JsonObject input = new JsonObject();
input.addProperty("path", "src/main/resources/application.properties");
input.addProperty("content", """
    server.port=8080
    spring.datasource.url=jdbc:mysql://localhost:3306/mydb
    spring.datasource.username=root
    spring.datasource.password=password
    logging.level.com.example=DEBUG
    """);

JsonObject result = writeToFileTool.execute(role, input);
```

### 4. 创建JSON配置文件

```java
JsonObject input = new JsonObject();
input.addProperty("path", "config/frontend-config.json");
input.addProperty("content", """
    {
      "apiEndpoint": "https://api.example.com",
      "theme": {
        "primaryColor": "#007bff",
        "secondaryColor": "#6c757d",
        "fontFamily": "Arial, sans-serif"
      },
      "features": {
        "darkMode": true,
        "notifications": true,
        "analytics": false
      },
      "version": "1.0.0"
    }
    """);

JsonObject result = writeToFileTool.execute(role, input);
```

### 5. 创建HTML文件

```java
JsonObject input = new JsonObject();
input.addProperty("path", "public/index.html");
input.addProperty("content", """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>My Web Application</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
            .container { max-width: 800px; margin: 0 auto; }
            h1 { color: #333; }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Welcome to My Web Application</h1>
            <p>This is a sample HTML page created with WriteToFileTool.</p>
        </div>
    </body>
    </html>
    """);

JsonObject result = writeToFileTool.execute(role, input);
```

### 6. 创建脚本文件

```java
JsonObject input = new JsonObject();
input.addProperty("path", "scripts/build.sh");
input.addProperty("content", """
    #!/bin/bash
    
    echo "Starting build process..."
    
    # Clean previous build
    rm -rf target/
    
    # Compile Java sources
    mvn clean compile
    
    # Run tests
    mvn test
    
    # Package application
    mvn package
    
    echo "Build completed successfully!"
    """);

JsonObject result = writeToFileTool.execute(role, input);
```

## 工具参数

### 必需参数

- **path**: (字符串) 要写入的文件路径（相对于当前工作目录）
- **content**: (字符串) 要写入文件的完整内容

### 返回结果

成功时返回的JsonObject包含：
- `result`: 操作结果描述
- `operation`: 操作类型（"创建" 或 "覆盖"）
- `path`: 文件路径
- `contentLength`: 内容长度
- `fileExisted`: 文件是否已存在

失败时返回：
- `error`: 错误信息描述

## 内容预处理功能

工具会自动处理以下情况：

1. **Markdown代码块标记移除**：
   ```
   输入: ```java\ncode content\n```
   输出: code content
   ```

2. **多余换行符处理**：
   - 移除末尾多余的换行符
   - 保持内容的原始格式

## 使用场景

### 1. 项目初始化
- 创建项目结构文件
- 生成配置文件
- 创建初始源代码文件

### 2. 代码生成
- 自动生成Java类
- 创建接口定义
- 生成数据传输对象(DTO)

### 3. 配置管理
- 创建应用配置文件
- 生成环境特定配置
- 创建部署脚本

### 4. 文档生成
- 创建README文件
- 生成API文档
- 创建用户手册

## 注意事项

1. **完整内容要求**: 必须提供文件的完整预期内容，不能有截断或省略
2. **文件覆盖**: 如果文件已存在，会完全覆盖原有内容
3. **目录创建**: 会自动创建必要的父目录
4. **编码格式**: 使用UTF-8编码保存文件
5. **路径安全**: 建议使用相对路径，避免路径遍历攻击

## 与replace_in_file的区别

| 特性 | WriteToFileTool | ReplaceInFileTool |
|------|-----------------|-------------------|
| 用途 | 创建新文件或完全重写文件 | 对现有文件进行部分修改 |
| 内容处理 | 完整文件内容 | SEARCH/REPLACE块 |
| 适用场景 | 文件创建、完全重写 | 精确的部分修改 |
| 风险 | 会覆盖整个文件 | 只修改匹配的部分 |

## 错误处理

常见错误情况：

1. **缺少参数**: path或content参数未提供
2. **路径是目录**: 指定的路径指向现有目录
3. **权限问题**: 无法创建目录或写入文件
4. **IO错误**: 磁盘空间不足等系统级错误

## 测试

项目包含了完整的单元测试，位于：
`src/test/java/run/mone/hive/roles/tool/WriteToFileToolTest.java`

测试覆盖了：
- 文件创建和覆盖
- 目录自动创建
- 各种文件类型
- 特殊字符处理
- 内容预处理
- 错误情况处理

## 集成到系统

要在系统中使用此工具：

1. 将工具添加到ReactorRole
2. 在系统提示中包含工具描述
3. 确保工具在适当的上下文中可用

```java
// 示例集成代码
ReactorRole role = new ReactorRole();
role.addTool(new WriteToFileTool());
```

这个工具完全兼容Cline项目的write_to_file工具规范，可以无缝集成到现有的AI助手系统中。
