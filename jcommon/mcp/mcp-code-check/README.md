# MCP Code Check - 网关Filter代码安全检查工具

## 项目简介

MCP Code Check 是一个专门用于网关Filter代码安全检查的MCP（Model Context Protocol）服务。该工具能够分析Java代码中的安全风险、循环复杂度和日志打印问题，并基于10分制评分系统给出代码质量评估。

## 功能特性

### 🔒 安全风险检测
- **高风险操作**：检测 `Runtime.exec()`、`ProcessBuilder`、`System.exit()` 等危险操作
- **反射操作**：识别 `Class.forName()`、`setAccessible()` 等反射调用
- **文件操作**：检测文件读写、网络连接等潜在风险
- **SQL注入**：识别可能的SQL注入风险点

### 🔄 循环复杂度分析
- **for循环**：检测可能影响网关性能的for循环
- **while循环**：识别潜在的死循环风险
- **do-while循环**：分析循环逻辑复杂度

### 📝 日志输出检测
- **控制台输出**：检测 `System.out.print`、`System.err.print` 等
- **日志框架**：识别各种日志框架的输出语句
- **性能影响**：评估日志输出对网关性能的影响

### 📊 评分系统
- **10分制评分**：满分10分，发现问题相应减分
- **问题分级**：HIGH、MEDIUM、LOW三个严重程度级别
- **详细报告**：提供具体的问题描述和减分理由

## 技术架构

- **Java 21**：使用最新的Java版本
- **Spring Boot 2.7.14**：基于Spring Boot框架
- **JavaParser**：用于Java代码的AST解析
- **Hive MCP Framework**：基于内部MCP框架构建

## 快速开始

### 1. 编译项目
```bash
cd mcp-code-check
mvn clean compile
```

### 2. 运行服务
```bash
mvn spring-boot:run
```

### 3. 使用工具

#### 全面代码检查
```json
{
  "code": "public class TestFilter implements Filter { public void doFilter() { while(true) { System.out.println(\"processing\"); Runtime.getRuntime().exec(\"ls\"); } } }",
  "check_type": "all"
}
```

#### 仅安全检查
```json
{
  "code": "public class Test { public void test() { Class.forName(\"com.example.Test\"); } }",
  "check_type": "security"
}
```

#### 严格模式检查
```json
{
  "code": "public class Test { public void test() { for(int i=0; i<100; i++) { log.info(\"test\"); } } }",
  "check_type": "all",
  "strict_mode": true
}
```

## 检查类型

| 检查类型 | 说明 | 示例 |
|---------|------|------|
| `all` | 全面检查（默认） | 安全风险 + 循环 + 日志 |
| `security` | 仅安全检查 | 反射、文件操作、命令执行等 |
| `loops` | 仅循环检查 | for、while、do-while循环 |
| `logs` | 仅日志检查 | System.out、日志框架输出 |

## 评分规则

### 安全风险扣分
- **高风险操作**（Runtime.exec等）：扣3-4分
- **中等风险操作**（反射等）：扣2-3分
- **低风险操作**（文件操作等）：扣1-2分

### 循环复杂度扣分
- **for循环**：扣1-2分
- **while/do-while循环**：扣2-3分

### 日志输出扣分
- **System.out/err**：扣1-2分
- **日志框架输出**：扣1分

### 严格模式
启用严格模式后，所有扣分项的分数会相应增加。

## 返回格式

```json
{
  "score": 7,
  "total_score": 10,
  "summary": "代码检查完成，得分：7/10。发现问题：安全风险(1个) 循环复杂度(1个) 日志输出(1个)",
  "issues": {
    "issue_1": {
      "type": "SECURITY_RISK",
      "description": "发现潜在安全风险：Runtime.getRuntime().exec",
      "severity": "HIGH",
      "deduction": 3
    },
    "issue_2": {
      "type": "LOOP_COMPLEXITY", 
      "description": "发现while循环，需要注意死循环风险",
      "severity": "HIGH",
      "deduction": 2
    },
    "issue_3": {
      "type": "LOG_OUTPUT",
      "description": "发现日志输出：System.out.print，可能影响网关性能",
      "severity": "MEDIUM", 
      "deduction": 1
    }
  }
}
```

## 配置说明

### application.properties
```properties
# 服务端口
server.port=8080

# MCP代理名称
mcp.agent.name=code-check-agent

# 日志级别
logging.level.run.mone.mcp.codecheck=INFO
```

## 最佳实践

### 1. 网关Filter代码建议
- 避免使用循环，特别是while循环
- 不要在Filter中执行系统命令
- 减少日志输出，特别是System.out
- 避免使用反射操作

### 2. 代码审查流程
1. 提交代码前先进行全面检查
2. 对于得分低于8分的代码，建议重构
3. 高风险问题必须修复后才能部署
4. 定期对现有代码进行安全审查

## 扩展开发

### 添加新的检查规则
1. 在 `SECURITY_RISK_PATTERNS` 中添加新的安全风险模式
2. 在 `LOG_PATTERNS` 中添加新的日志模式
3. 实现对应的检查逻辑

### 自定义评分规则
修改 `getSecurityRiskDeduction()` 等方法来调整评分策略。

## 许可证

本项目基于内部许可证，仅供内部使用。

## 联系方式

如有问题或建议，请联系：goodjava@qq.com
