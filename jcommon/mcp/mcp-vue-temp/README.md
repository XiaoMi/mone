# MCP Vue Template Generator

一个基于 MCP (Model Context Protocol) 的 Vue 前端项目模板生成器。

## 🚀 功能特性

- 自动生成完整的 Vue 3 + TypeScript + Element Plus + Pinia + Vue Router 项目模板
- 支持自定义项目名称和描述
- 包含完整的项目结构和最佳实践
- 支持变量替换，个性化项目内容

## 🛠️ 技术栈

生成的 Vue 项目包含以下技术栈：

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - JavaScript 的超集，提供类型安全
- **Element Plus** - 基于 Vue 3 的桌面端组件库
- **Pinia** - Vue 的状态管理库
- **Vue Router** - Vue.js 官方路由管理器
- **Vite** - 下一代前端构建工具

## 📦 项目结构

```
mcp-vue-temp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── run/mone/mcp/vuetemp/
│   │   │       ├── VueTempAgentBootstrap.java
│   │   │       ├── config/
│   │   │       │   └── AgentConfig.java
│   │   │       ├── function/
│   │   │       │   └── VueTemplateFunction.java
│   │   │       └── service/
│   │   │           └── VueTemplateService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── template/          # Vue 项目模板
│   │           ├── index.html
│   │           ├── package.json
│   │           ├── README.md
│   │           ├── tsconfig.json
│   │           ├── vite.config.ts
│   │           └── src/
│   │               ├── App.vue
│   │               ├── main.ts
│   │               ├── components/
│   │               ├── views/
│   │               ├── router/
│   │               ├── stores/
│   │               ├── styles/
│   │               ├── types/
│   │               └── utils/
│   └── pom.xml
└── README.md
```

## 🎯 使用方法

### 1. 启动 MCP 服务

```bash
cd jcommon/mcp/mcp-vue-temp
mvn clean package
java -jar target/app.jar
```

### 2. 调用生成功能

通过 MCP 协议调用 `generate_vue_template` 功能：

```json
{
  "method": "generate_vue_template",
  "params": {
    "projectName": "my-awesome-vue-app",
    "description": "一个现代化的 Vue 3 项目",
    "outputPath": "/path/to/output"
  }
}
```

### 3. 参数说明

- `projectName` (必需): 项目名称
- `description` (可选): 项目描述，默认为 "Vue 3 + TypeScript + Element Plus + Pinia + Vue Router 项目"
- `outputPath` (必需): 输出路径

## 🔧 配置说明

### application.properties

```properties
# MCP 配置
mcp.transport.type=grpc
mcp.grpc.port=9187
mcp.agent.name=vue-template-generator
mcp.agent.group=staging
mcp.agent.version=1.0

# Hive 配置
hive.manager.reg.switch=true
hive.manager.base-url=http://127.0.0.1:8080
hive.manager.token=${hive_manager_token}

# LLM 配置
mcp.llm=glm_45_air
```

## 📋 生成的项目特性

### 🎯 核心功能
- ✅ Vue 3 组合式 API
- ✅ TypeScript 类型安全
- ✅ Element Plus UI 组件库
- ✅ Pinia 状态管理
- ✅ Vue Router 路由管理
- ✅ Vite 快速构建

### 🎨 开发体验
- ✅ 热重载开发
- ✅ 自动导入组件和 API
- ✅ TypeScript 类型检查
- ✅ 路径别名配置

### 📱 响应式设计
- ✅ 移动端适配
- ✅ 响应式布局
- ✅ 现代化 UI 设计

## 🎮 使用示例

生成的项目包含以下示例页面：

1. **首页** - 展示技术栈和项目信息
2. **关于页面** - 项目介绍
3. **计数器页面** - Pinia 状态管理示例

## 🔧 开发

### 环境要求

- Java 21+
- Maven 3.6+
- Node.js 16+ (用于测试生成的 Vue 项目)

### 构建

```bash
mvn clean package
```

### 运行

```bash
java -jar target/app.jar
```

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

如有问题，请通过以下方式联系：

- 邮箱: goodjava@qq.com
- GitHub: https://github.com/your-username
