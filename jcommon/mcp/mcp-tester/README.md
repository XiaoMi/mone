# MCP Tester

MCP Tester 是一个基于 MCP (Model Context Protocol) 的智能单元测试生成工具。它可以分析 Java 项目的源代码，自动生成规范的单元测试代码。

## 功能特性

- **智能分析**: 自动分析源代码结构，提取类、方法、依赖等信息
- **自动生成**: 为每个公共方法生成测试用例框架
- **多框架支持**: 支持 JUnit 4/5 和 Mockito 等主流测试框架
- **批量处理**: 支持为整个项目或单个文件生成测试
- **最佳实践**: 生成的测试代码遵循测试最佳实践和编码规范

## 配置说明

在 `application.properties` 中配置：

```properties
# 测试框架配置
test.framework=junit5              # 可选: junit4, junit5
test.mock.framework=mockito        # Mock 框架
test.coverage.threshold=80         # 目标代码覆盖率
test.output.path=src/test/java     # 测试文件输出路径
```

## 使用方式

### 1. 为整个项目生成测试

```json
{
  "tool": "generate_unit_test",
  "arguments": {
    "project_path": "/path/to/your/project"
  }
}
```

### 2. 为单个文件生成测试

```json
{
  "tool": "generate_unit_test",
  "arguments": {
    "project_path": "/path/to/your/project",
    "source_file_path": "src/main/java/com/example/MyClass.java"
  }
}
```

### 3. 自定义测试类名称

```json
{
  "tool": "generate_unit_test",
  "arguments": {
    "project_path": "/path/to/your/project",
    "source_file_path": "src/main/java/com/example/MyClass.java",
    "test_name": "MyClassCustomTest"
  }
}
```

## 生成的测试代码示例

```java
package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MyClass 的单元测试
 * 自动生成 by mcp-tester
 */
public class MyClassTest {

    @InjectMocks
    private MyClass myClass;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMethodName() {
        // TODO: 实现测试逻辑
        // 1. 准备测试数据
        // 2. 执行被测方法
        // 3. 验证结果
        fail("测试未实现");
    }
}
```

## 工作流程

1. **分析项目**: 扫描项目目录，识别需要测试的源文件
2. **解析源码**: 提取类名、方法、依赖等信息
3. **生成测试**: 为每个方法创建测试用例框架
4. **写入文件**: 将测试代码保存到指定目录，保持包结构

## Agent 角色定位

MCP Tester Agent 是一名专业的测试工程师，具备以下能力：

- 熟悉 Java 测试框架（JUnit、Mockito）
- 了解测试最佳实践和设计模式
- 能够分析代码并识别测试场景
- 注重代码质量和测试覆盖率

## 开发和构建

```bash
# 构建项目
mvn clean package

# 运行
java -jar target/app.jar
```

## 依赖要求

- JDK 21+
- Maven 3.6+
- Hive Framework 1.6.2+

## 注意事项

1. 生成的测试代码是框架性的，需要手动补充具体的测试逻辑
2. 建议在生成后根据实际业务场景完善测试用例
3. 对于复杂的业务逻辑，可能需要手动添加更多测试场景
4. 确保项目结构符合 Maven 标准布局

## 后续优化方向

- [ ] 支持更多测试框架（TestNG、Spock 等）
- [ ] 智能生成测试数据
- [ ] 分析业务逻辑自动生成断言
- [ ] 集成代码覆盖率分析
- [ ] 支持 Kotlin、Scala 等 JVM 语言
- [ ] 生成集成测试和端到端测试

## License

Apache License 2.0
