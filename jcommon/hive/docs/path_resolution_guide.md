# Hive 路径解析功能指南

## 概述

本文档描述了在 Hive 项目中新增的路径解析功能，该功能复刻了 Cline 项目中的路径处理机制，能够自动将相对路径转换为绝对路径，确保文件操作工具能够正确执行。

## 核心组件

### 1. PathUtils 工具类

`PathUtils` 类提供了核心的路径处理功能，包括：

- **路径解析**: 将相对路径转换为绝对路径
- **路径规范化**: 统一路径分隔符为正斜杠
- **安全检查**: 确保路径在工作区范围内
- **跨平台兼容**: 处理不同操作系统的路径差异

```java
// 示例用法
String workspacePath = "/project/root";
String relativePath = "src/main/java/App.java";
String absolutePath = PathUtils.resolveWorkspacePath(workspacePath, relativePath, "example");
// 结果: /project/root/src/main/java/App.java
```

### 2. WorkspaceResolver 工作区解析器

`WorkspaceResolver` 类提供了工作区感知的路径解析功能：

- **使用追踪**: 记录路径解析的使用模式
- **调试支持**: 提供详细的追踪日志
- **工作区管理**: 支持多工作区场景的基础设施

```java
WorkspaceResolver resolver = new WorkspaceResolver("/project/root");
String resolved = resolver.resolveWorkspacePath("config/app.properties", "ConfigLoader");
```

### 3. PathResolutionInterceptor 路径解析拦截器

`PathResolutionInterceptor` 类在工具执行前自动处理路径参数：

- **自动检测**: 识别参数中的路径值
- **批量处理**: 同时处理多个路径参数
- **智能判断**: 区分路径和非路径参数

## ReactorRole 集成

### 修改内容

1. **新增工作区路径属性**:
   ```java
   private String workspacePath = System.getProperty("user.dir");
   ```

2. **修改 callTool 方法**:
   ```java
   // 在工具执行前进行路径解析，将相对路径转换为绝对路径
   PathResolutionInterceptor.resolvePathParameters(name, params, extraParam, this.workspacePath);
   ```

3. **新增路径管理方法**:
   ```java
   public void setWorkspacePath(String workspacePath)
   public String getWorkspacePath()
   ```

### 使用示例

```java
ReactorRole role = new ReactorRole("assistant", "default", "1.0", 
    "AI助手", "帮助用户处理文件", "安全操作", 8080, llm, tools, mcpTools);

// 设置工作区路径
role.setWorkspacePath("/project/workspace");

// 现在所有工具调用都会自动解析路径
// 例如：<read_file><path>config/app.properties</path></read_file>
// 会自动转换为绝对路径：/project/workspace/config/app.properties
```

## 支持的工具

路径解析功能自动支持以下工具：

- `read_file` - 文件读取
- `write_to_file` - 文件写入
- `replace_in_file` - 文件替换
- `list_files` - 文件列表
- `search_files` - 文件搜索
- `list_code_definition_names` - 代码定义列表
- `execute_command` - 命令执行

## 支持的参数名

自动识别以下参数名作为路径参数：

- `path` - 主要路径参数
- `file_path` / `filePath` - 文件路径
- `directory` / `dir` - 目录路径
- `folder` - 文件夹路径
- `target` / `source` - 目标/源路径
- `input` / `output` - 输入/输出路径
- `filename` / `file` - 文件名

## 路径检测规则

系统使用以下规则判断参数是否为路径：

1. **参数名匹配**: 参数名在已知路径参数列表中
2. **路径特征**: 包含路径分隔符 (`/` 或 `\`)
3. **相对路径标记**: 以 `./` 或 `../` 开头
4. **文件扩展名**: 包含常见文件扩展名
5. **排除规则**: 排除 URL 和其他非文件路径

## 安全特性

1. **工作区边界检查**: 确保所有路径都在工作区范围内
2. **路径规范化**: 防止路径遍历攻击
3. **存在性验证**: 验证路径的有效性
4. **权限检查**: 确保路径可访问

## 配置选项

### 环境变量

- `hive.workspace.trace=true` - 启用路径解析追踪日志
- `hive.env=development` - 开发模式，启用详细日志

### 自定义配置

```java
// 添加自定义路径参数名
PathResolutionInterceptor.addPathParameterName("custom_path");

// 添加自定义路径相关工具
PathResolutionInterceptor.addPathDependentTool("custom_file_tool");
```

## 调试和监控

### 使用统计

```java
WorkspaceResolver resolver = PathResolutionInterceptor.getWorkspaceResolver();
Map<String, UsageStats> stats = resolver.getUsageStats();
String report = resolver.generateUsageReport();
System.out.println(report);
```

### 追踪日志

启用追踪后，系统会输出详细的路径解析日志：

```
[WORKSPACE-TRACE] ReadFileTool: resolving 'config/app.properties' against '/project/root'
[PathUtils] ReadFileTool.path - Resolved 'config/app.properties' against '/project/root' to '/project/root/config/app.properties'
```

## 测试

项目包含完整的测试套件：

- `PathUtilsTest` - 核心路径工具测试
- `PathResolutionInterceptorTest` - 拦截器功能测试

运行测试：
```bash
mvn test -Dtest=*PathUtils*
```

## 最佳实践

1. **设置工作区路径**: 在创建 ReactorRole 后立即设置正确的工作区路径
2. **使用相对路径**: 在工具调用中使用相对路径，让系统自动解析
3. **监控使用情况**: 在开发阶段启用追踪，了解路径使用模式
4. **测试边界情况**: 测试工作区外路径、不存在路径等情况
5. **保持路径简洁**: 使用简洁的相对路径，避免复杂的路径嵌套

## 示例场景

### 场景1：读取配置文件
```xml
<read_file>
<path>config/application.yml</path>
</read_file>
```
自动解析为: `/workspace/config/application.yml`

### 场景2：列出源码目录
```xml
<list_files>
<path>src/main/java</path>
<recursive>true</recursive>
</list_files>
```
自动解析为: `/workspace/src/main/java`

### 场景3：搜索文件
```xml
<search_files>
<path>.</path>
<regex>\.java$</regex>
</search_files>
```
自动解析为: `/workspace`

## 故障排除

### 常见问题

1. **路径不存在**: 检查相对路径是否正确，文件是否存在
2. **权限错误**: 确保工作区目录有读写权限
3. **路径超出工作区**: 检查是否使用了 `../` 导致路径超出工作区范围
4. **路径格式错误**: 确保使用正确的路径分隔符

### 调试步骤

1. 启用追踪日志: `hive.workspace.trace=true`
2. 检查工作区路径设置: `role.getWorkspacePath()`
3. 查看解析结果: 观察追踪日志中的路径转换
4. 验证文件存在性: 使用 `PathUtils.fileExists()`

## 总结

新的路径解析功能为 Hive 项目提供了强大而安全的文件路径处理能力，确保所有文件操作工具都能正确处理相对路径，同时保持工作区的安全边界。通过自动化的路径解析，用户可以使用简洁的相对路径，而系统会自动处理底层的路径转换逻辑。
