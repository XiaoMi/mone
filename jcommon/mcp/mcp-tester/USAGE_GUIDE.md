# MCP Tester 使用指南

## 快速开始

### 1. 配置环境变量

在启动前需要配置以下环境变量：

```bash
export agent_name=mcp-tester
export hive_manager_base_url=http://your-hive-manager-url
export hive_manager_token=your-token
```

### 2. 编译和打包

```bash
cd mcp-tester
mvn clean package
```

### 3. 启动服务

```bash
java -jar target/app.jar
```

或使用环境变量启动：

```bash
java -Dagent_name=mcp-tester \
     -Dhive_manager_base_url=http://your-hive-manager-url \
     -Dhive_manager_token=your-token \
     -jar target/app.jar
```

## 使用场景

### 场景 1: 为整个项目生成单元测试

当你有一个 Java 项目，想要快速为所有源文件生成单元测试骨架时：

```xml
<generate_unit_test>
<project_path>/home/user/my-java-project</project_path>
</generate_unit_test>
```

**期望结果**：
- 扫描 `src/main/java` 目录下的所有 `.java` 文件
- 为每个源文件在 `src/test/java` 对应位置生成测试文件
- 保持原有的包结构

### 场景 2: 为单个类生成测试

当你只想为某个特定的类生成测试时：

```xml
<generate_unit_test>
<project_path>/home/user/my-java-project</project_path>
<source_file_path>src/main/java/com/example/UserService.java</source_file_path>
</generate_unit_test>
```

**期望结果**：
- 只为 `UserService.java` 生成测试
- 测试类名为 `UserServiceTest`
- 测试文件位置：`src/test/java/com/example/UserServiceTest.java`

### 场景 3: 自定义测试类名称

如果你想自定义测试类的名称：

```xml
<generate_unit_test>
<project_path>/home/user/my-java-project</project_path>
<source_file_path>src/main/java/com/example/UserService.java</source_file_path>
<test_name>UserServiceIntegrationTest</test_name>
</generate_unit_test>
```

**期望结果**：
- 测试类名为 `UserServiceIntegrationTest`
- 其他行为与场景 2 相同

## 生成的测试代码示例

假设有以下源代码：

```java
package com.example;

public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }
}
```

生成的测试代码将如下：

```java
package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

/**
 * Calculator 的单元测试
 * 自动生成 by mcp-tester
 */
public class CalculatorTest {

    @InjectMocks
    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAdd() {
        // TODO: 实现测试逻辑
        // 1. 准备测试数据
        // 2. 执行被测方法
        // 3. 验证结果
        fail("测试未实现");
    }

    @Test
    public void testSubtract() {
        // TODO: 实现测试逻辑
        // 1. 准备测试数据
        // 2. 执行被测方法
        // 3. 验证结果
        fail("测试未实现");
    }

}
```

## 工作流程

1. **接收请求**: Agent 接收到生成测试的请求
2. **验证路径**: 检查项目路径是否存在
3. **扫描文件**: 根据参数决定扫描整个项目还是单个文件
4. **分析源码**: 提取包名、类名、方法等信息
5. **生成测试**: 基于模板生成测试代码
6. **写入文件**: 创建测试文件目录并写入测试代码
7. **返回结果**: 返回生成结果和统计信息

## 配置选项

在 `application.properties` 中可以配置：

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| `test.framework` | 测试框架 (junit4/junit5) | junit5 |
| `test.mock.framework` | Mock 框架 | mockito |
| `test.coverage.threshold` | 目标覆盖率 | 80 |
| `test.output.path` | 测试输出路径 | src/test/java |
| `mcp.llm` | LLM 模型 | deepseek |
| `mcp.grpc.port` | gRPC 端口 | 9587 |

## 最佳实践

### 1. 项目结构要求

确保你的项目遵循 Maven 标准结构：

```
my-project/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/example/
│   │           └── MyClass.java
│   └── test/
│       └── java/
│           └── com/example/
│               └── MyClassTest.java
└── pom.xml
```

### 2. 生成后的工作

生成的测试代码是骨架，需要：

1. **补充测试数据**: 根据实际业务场景准备测试数据
2. **实现测试逻辑**: 编写具体的测试步骤
3. **添加断言**: 验证方法执行结果
4. **处理 Mock**: 对于有依赖的类，配置 Mock 对象
5. **运行测试**: 确保测试通过

### 3. 测试覆盖率

建议：
- 为每个公共方法至少生成一个测试
- 覆盖正常场景和异常场景
- 对边界值进行测试
- 使用代码覆盖率工具验证

### 4. 命名规范

- 测试类名：`{ClassName}Test`
- 测试方法名：`test{MethodName}`
- 特殊场景：`test{MethodName}_When{Condition}_Then{ExpectedResult}`

## 故障排查

### 问题 1: 项目路径不存在

**错误信息**: "项目路径不存在或不是目录"

**解决方法**:
- 检查路径是否正确
- 确保使用绝对路径
- 检查文件系统权限

### 问题 2: 找不到源文件

**错误信息**: "源文件不存在"

**解决方法**:
- 确认文件路径正确
- 检查项目结构是否符合 Maven 标准
- 尝试使用相对路径 `src/main/java/...`

### 问题 3: 编译失败

**解决方法**:
- 检查依赖是否正确安装
- 确认 JDK 版本 (需要 JDK 21+)
- 运行 `mvn clean install` 重新构建

## 扩展功能（计划中）

- [ ] 支持 TestNG 框架
- [ ] 智能生成测试数据
- [ ] 自动生成 Mock 对象配置
- [ ] 集成代码覆盖率分析
- [ ] 支持 Kotlin 和 Scala
- [ ] AI 辅助生成断言逻辑

## 技术支持

如遇问题，请查看：
- [项目 README](./README.md)
- [Hive Framework 文档](https://github.com/goodjava/hive)
- 提交 Issue 到项目仓库

## 示例项目

参考示例项目结构：

```
example-project/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   ├── UserService.java
│                   ├── OrderService.java
│                   └── PaymentService.java
└── pom.xml
```

运行测试生成后：

```
example-project/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               ├── UserService.java
│   │               ├── OrderService.java
│   │               └── PaymentService.java
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   ├── UserServiceTest.java
│                   ├── OrderServiceTest.java
│                   └── PaymentServiceTest.java
└── pom.xml
```
