# ExecuteCommandTool zsh 环境变量集成示例

## 功能说明

修改后的 `ExecuteCommandTool` 现在支持自动获取并嵌入完整的 zsh 环境变量，包括：

1. **完整的 zsh 环境**：通过执行 `zsh -l -c env` 获取包含 `.zshrc` 配置的完整环境变量
2. **环境变量缓存**：5分钟缓存机制，避免重复获取，提高性能
3. **自动回退**：如果 zsh 不可用，自动回退到系统默认环境变量
4. **跨平台支持**：Windows 系统使用 cmd，Unix/Linux/macOS 系统使用 zsh

## 主要改进

### 1. zsh 环境变量获取
```java
// 使用 zsh -l -c env 获取完整环境变量
ProcessBuilder processBuilder = new ProcessBuilder("zsh", "-l", "-c", "env");
```

### 2. 环境变量缓存
```java
// 缓存机制，5分钟超时
private static volatile Map<String, String> cachedZshEnv = null;
private static final long ENV_CACHE_TIMEOUT = 300_000; // 5分钟
```

### 3. 命令执行改进
```java
// 使用 zsh 执行命令
processBuilder.command("zsh", "-l", "-c", command);

// 清空并设置完整的 zsh 环境
processEnv.clear();
Map<String, String> zshEnv = getZshEnvironment();
processEnv.putAll(zshEnv);
```

## 使用示例

### 基本命令执行
```json
{
  "command": "echo $PATH",
  "requires_approval": false,
  "timeout": 30
}
```

### 使用 zsh 特性的命令
```json
{
  "command": "which node && node --version",
  "requires_approval": false,
  "timeout": 30
}
```

### 清除环境变量缓存
```java
// 如果需要强制刷新环境变量
ExecuteCommandTool.clearZshEnvironmentCache();
```

## 环境变量包含内容

修改后的工具会自动包含以下环境变量：

- `PATH`：完整的路径配置（包括 `.zshrc` 中添加的路径）
- `HOME`：用户主目录
- `SHELL`：shell 路径（设置为 `/bin/zsh`）
- `USER`：当前用户名
- `PWD`/`CWD`：当前工作目录
- 以及所有其他 zsh 环境变量

## 性能优化

1. **缓存机制**：环境变量获取结果缓存5分钟，避免重复执行
2. **超时控制**：环境变量获取超时10秒，命令执行可自定义超时
3. **错误回退**：如果 zsh 不可用或获取失败，自动使用系统环境变量

## 安全特性

1. **危险命令检测**：识别潜在危险的命令模式
2. **环境变量过滤**：过滤掉可能有问题的环境变量
3. **进程管理**：确保进程正确清理，避免资源泄露

## 日志输出

工具会输出详细的日志信息：
- 环境变量获取过程
- 缓存使用情况
- 命令执行详情
- 错误和警告信息
