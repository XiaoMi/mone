# Vue3模板生成器 MCP

这是一个用于生成Vue3组件、页面和项目模板的MCP（Model Context Protocol）模块。

## 功能特性

- 🚀 生成Vue3组件模板
- 📄 生成Vue3页面模板  
- 🏗️ 生成完整的Vue3项目结构
- ⚡ 支持Composition API
- 🔷 支持TypeScript
- 🍍 支持Pinia状态管理
- 🛣️ 支持Vue Router
- ⚡ 支持Vite构建工具

## 支持的操作

### 1. 生成Vue3组件 (generateComponent)

生成单个Vue3组件模板，支持以下参数：

- `componentName`: 组件名称
- `template`: 自定义模板内容
- `script`: 自定义脚本内容
- `style`: 自定义样式内容
- `useCompositionAPI`: 是否使用Composition API (默认: true)
- `useTypeScript`: 是否使用TypeScript (默认: false)
- `usePinia`: 是否使用Pinia状态管理 (默认: false)
- `useRouter`: 是否使用Vue Router (默认: false)

### 2. 生成Vue3页面 (generatePage)

生成Vue3页面模板，支持以下参数：

- `pageName`: 页面名称
- `layout`: 页面布局类型
- `useTypeScript`: 是否使用TypeScript (默认: false)
- `usePinia`: 是否使用Pinia状态管理 (默认: false)
- `useRouter`: 是否使用Vue Router (默认: true)

### 3. 生成Vue3项目 (generateProject)

生成完整的Vue3项目结构，支持以下参数：

- `projectName`: 项目名称
- `useTypeScript`: 是否使用TypeScript (默认: false)
- `usePinia`: 是否使用Pinia状态管理 (默认: false)
- `useRouter`: 是否使用Vue Router (默认: true)
- `useVite`: 是否使用Vite构建工具 (默认: true)

## 使用示例

### 生成基础组件
```json
{
  "operation": "generateComponent",
  "componentName": "UserCard",
  "useTypeScript": true,
  "usePinia": true
}
```

### 生成页面
```json
{
  "operation": "generatePage",
  "pageName": "UserProfile",
  "useTypeScript": true,
  "useRouter": true
}
```

### 生成项目
```json
{
  "operation": "generateProject",
  "projectName": "my-vue3-app",
  "useTypeScript": true,
  "usePinia": true,
  "useRouter": true,
  "useVite": true
}
```

## 配置

在 `application.properties` 中配置：

```properties
mcp.agent.name=vue3-template-generator
mcp.agent.group=frontend
mcp.grpc.port=9087
```

## 启动

```bash
mvn spring-boot:run
```

## 技术栈

- Spring Boot 3.4.1
- Vue 3
- TypeScript (可选)
- Pinia (可选)
- Vue Router (可选)
- Vite (可选)

## 依赖

- Java 17+
- Maven 3.6+
- Spring Boot 3.4.1
- Reactor Core 3.7.0
