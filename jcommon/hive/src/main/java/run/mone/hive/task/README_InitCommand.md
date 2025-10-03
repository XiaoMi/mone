# Init Command Implementation

这是基于Claude Code `/init` 指令的Java实现，用于分析代码库并生成MCODE.md文件，帮助未来的Mone Code实例更好地理解项目。

## 📁 项目结构

```
run.mone.hive.task/
├── SlashCommand.java              # 斜杠命令接口
├── SlashCommandParser.java        # 命令解析器
├── InitCommand.java               # Init命令实现
├── InitProcessor.java             # Init处理器
├── InitExample.java               # 使用示例
└── README_InitCommand.md          # 本文档
```

## 🚀 核心功能

### 1. Init命令特性
- **智能分析**: 自动分析项目结构、构建配置、现有文档
- **MCODE.md生成**: 创建专门为Mone Code设计的项目指南
- **改进建议**: 如果MCODE.md已存在，提供改进建议
- **全面覆盖**: 包含开发命令、架构概述、项目结构

### 2. 八步骤分析流程

#### Step 1: 检查现有MCODE.md
```java
private String checkExistingMcode(String workingDirectory) {
    Path mcodePath = Paths.get(workingDirectory, "MCODE.md");
    if (Files.exists(mcodePath)) {
        // 读取现有内容，准备改进建议
        return Files.readString(mcodePath);
    }
    return null;
}
```

#### Step 2: 分析项目结构
- 识别主要编程语言和框架
- 分析关键目录和用途
- 确定入口点和架构模式
- 识别关键模块和组件

#### Step 3: 查找构建配置
支持的构建文件类型：
- **Java**: `pom.xml`, `build.gradle`, `build.gradle.kts`
- **Node.js**: `package.json`, `package-lock.json`, `yarn.lock`
- **Python**: `requirements.txt`, `setup.py`, `pyproject.toml`
- **Go**: `go.mod`
- **Rust**: `Cargo.toml`
- **Docker**: `Dockerfile`, `docker-compose.yml`
- **其他**: `Makefile`, `CMakeLists.txt`

#### Step 4: 查找现有文档
搜索的文档类型：
- `README.md`, `README.rst`, `README.txt`
- `CHANGELOG.md`, `CONTRIBUTING.md`
- `.cursorrules`, `.cursor/rules/`
- `.github/copilot-instructions.md`
- `docs/`, `documentation/` 目录

#### Step 5: 分析关键源文件
- 主入口点（main方法、index文件等）
- 核心业务逻辑文件
- 配置类或模块
- 重要接口或抽象类
- 关键工具或辅助类

#### Step 6: 识别开发命令
自动识别：
- **构建命令**: 编译、构建、打包
- **测试命令**: 运行所有测试、单个测试、测试覆盖率
- **代码质量**: 代码风格检查、格式化
- **运行命令**: 启动应用、开发服务器
- **安装命令**: 依赖安装、环境设置
- **部署命令**: 部署相关命令

#### Step 7: 生成MCODE.md内容
使用LLM生成结构化的MCODE.md内容，包含：
- 必需的头部信息
- 开发命令列表
- 高级架构概述
- 项目特定信息
- 避免通用建议

#### Step 8: 创建MCODE.md文件
```java
private String createMcodeFile(String workingDirectory, String content) throws IOException {
    Path mcodePath = Paths.get(workingDirectory, "MCODE.md");
    
    // 确保内容以正确的头部开始
    if (!content.startsWith("# MCODE.md")) {
        content = "# MCODE.md\n\n" +
                 "This file provides guidance to Mone Code (run.mone/code) when working with code in this repository.\n\n" +
                 content;
    }
    
    Files.writeString(mcodePath, content);
    return mcodePath.toString();
}
```

## 📋 生成的MCODE.md结构

### 标准头部
```markdown
# MCODE.md

This file provides guidance to Mone Code (run.mone/code) when working with code in this repository.
```

### 主要内容部分
1. **Development Commands** - 常用开发命令
2. **Architecture Overview** - 架构概述
3. **Project Structure** - 项目结构
4. **Key Entry Points** - 关键入口点
5. **Important Notes** - 重要说明

### 示例MCODE.md内容
```markdown
# MCODE.md

This file provides guidance to Mone Code (run.mone/code) when working with code in this repository.

## Development Commands

### Build and Compile
```bash
mvn clean compile
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest
```

### Running the Application
```bash
# Development mode
mvn spring-boot:run
```

## Architecture Overview

This is a Spring Boot application following MVC architecture:
- **Controllers**: Handle HTTP requests
- **Services**: Business logic layer
- **Repositories**: Data access layer
- **Models**: Data entities

## Project Structure
```
src/
├── main/java/
│   ├── Application.java          # Main entry point
│   ├── controller/               # REST controllers
│   ├── service/                  # Business logic
│   └── repository/               # Data access
└── test/java/                    # Test classes
```
```

## 🔧 使用方法

### 1. 基本使用
```java
// 创建InitProcessor
InitProcessor initProcessor = new InitProcessor(llm, callbacks, focusChainManager);

// 执行Init流程
String mcodeFilePath = initProcessor.executeInit(workingDirectory);
```

### 2. 通过斜杠命令使用
```
/init
```

### 3. 在XML标签中使用
```xml
<task>
/init
</task>
```

## 🎯 设计特点

### 1. 智能分析
- 自动识别项目类型和构建系统
- 分析现有文档和配置
- 理解项目架构和结构

### 2. 避免重复
- 如果MCODE.md已存在，提供改进建议
- 不包含显而易见的通用建议
- 专注于项目特定信息

### 3. 全面覆盖
- 包含所有必要的开发命令
- 提供高级架构概述
- 整合现有文档的重要信息

### 4. 标准化输出
- 统一的MCODE.md格式
- 清晰的章节结构
- 易于阅读和维护

## 🔄 与现有系统的集成

### 1. 斜杠命令系统
- 继承`SlashCommand`接口
- 自动注册到`SlashCommandParser`
- 支持XML标签内使用

### 2. 工具链集成
- 使用`list_files`工具探索项目结构
- 使用`read_file`工具读取配置文件
- 使用`search_files`工具查找特定文件
- 使用`write_to_file`工具创建MCODE.md

### 3. LLM集成
- 通过`LLMTaskProcessor`调用LLM
- 支持多种分析提示词
- 生成结构化的MCODE.md内容

## 📈 扩展性

### 1. 添加新的构建系统支持
在`BUILD_FILE_PATTERNS`中添加新的文件模式：
```java
private static final List<String> BUILD_FILE_PATTERNS = List.of(
    "pom.xml", "build.gradle", "package.json",
    "new_build_system.conf"  // 添加新的构建系统
);
```

### 2. 自定义分析逻辑
重写`InitProcessor`中的分析方法：
```java
private String customAnalysis(String workingDirectory) {
    // 自定义分析逻辑
    return customResult;
}
```

### 3. 扩展MCODE.md模板
修改`generateMcodeContent`方法中的提示词：
```java
String contentPrompt = String.format(
    "Generate MCODE.md with custom sections: %s",
    customRequirements
);
```

这个实现提供了一个完整的、可扩展的/init命令系统，能够自动分析代码库并生成高质量的MCODE.md文件，帮助Mone Code更好地理解和操作项目。