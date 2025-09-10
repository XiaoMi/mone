+ 一个Chrome插件,支持mcp工具加载
+ 后端代码:moner-server

# Mone Chrome Extension

一个支持 MCP (Model Context Protocol) 工具加载的 Chrome 插件，提供智能助手功能。

## 项目结构

```
├── plugin/                    # 主插件目录
│   ├── manifest.json         # 插件清单文件
│   ├── background.js         # 后台脚本
│   ├── content.js            # 内容脚本
│   ├── popup.html           # 弹窗页面
│   ├── popup.js             # 弹窗脚本
│   ├── styles.css           # 样式文件
│   ├── images/              # 图标资源
│   ├── managers/            # 各种管理器模块
│   └── popup-vue/           # Vue前端应用
│       ├── src/             # Vue源码
│       ├── dist/            # 构建输出
│       └── package.json     # Vue项目配置
```

## 功能特性

- 🤖 MCP 工具集成
- 📱 侧边栏面板
- 🎯 内容脚本注入
- 📊 DOM 树可视化
- 🔧 多种管理器（书签、历史、下载等）
- 🎨 Vue.js 前端界面
- 📸 截图功能
- 🔔 通知管理

## 快速开始

### 1. 环境准备

确保你已安装：
- Node.js (>=16.0.0)
- pnpm (推荐) 或 npm

### 2. 安装依赖

```bash
# 主项目依赖（如果有）
cd plugin
npm install  # 或 pnpm install

# Vue前端依赖
cd popup-vue
pnpm install
```

### 3. 构建项目

```bash
# 构建Vue前端
cd plugin/popup-vue
pnpm run build-only

# 返回插件根目录
cd ..
```

### 4. 加载到Chrome

1. 打开 Chrome 浏览器
2. 访问 `chrome://extensions/`
3. 开启右上角的"开发者模式"
4. 点击"加载已解压的扩展程序"
5. 选择 `plugin` 文件夹（包含 manifest.json 的目录）
6. 插件加载成功后会显示在扩展列表中

## 使用方法

### 侧边栏使用

1. 点击浏览器工具栏中的 Mone 图标
2. 或者右键页面选择相关菜单项
3. 侧边栏会打开，显示 Vue 应用界面
4. 在界面中可以：
   - 配置 MCP 工具
   - 查看功能列表
   - 与 AI 助手交互

### 快捷功能

- **DOM 树查看器**: 在页面上右键选择查看 DOM 结构
- **截图功能**: 使用快捷键或菜单截取页面
- **书签管理**: 快速保存和管理网页书签
- **历史记录**: 查看和搜索浏览历史

## 开发调试

### 开发环境搭建

```bash
# 1. 克隆项目
git clone <repository-url>
cd mone

# 2. 安装依赖
cd plugin/popup-vue
pnpm install

# 3. 开发模式运行Vue应用
pnpm run dev
```

### 调试步骤

#### 1. 前端调试（Vue应用）

```bash
cd plugin/popup-vue
pnpm run dev
```

- 访问 `http://localhost:5173` 进行界面调试
- 修改代码后会热重载
- 使用浏览器开发者工具调试

#### 2. 插件调试

1. **重新加载插件**：
   - 在 `chrome://extensions/` 页面
   - 找到 Mone 插件，点击刷新按钮

2. **查看后台脚本日志**：
   - 在插件卡片上点击"检查视图: Service Worker"
   - 查看 `background.js` 的控制台输出

3. **调试内容脚本**：
   - 在任意网页上按 F12 打开开发者工具
   - 在 Console 中可以看到 `content.js` 的输出

4. **调试侧边栏**：
   - 打开侧边栏
   - 右键侧边栏内容区域，选择"检查"
   - 可以调试 Vue 应用

### 构建生产版本

```bash
# 构建Vue前端
cd plugin/popup-vue
pnpm run build-only

# 检查dist目录是否生成
ls -la dist/
```

### 常见问题解决

#### 1. "Side panel file path must exist" 错误

```bash
# 确保构建了Vue应用
cd plugin/popup-vue
pnpm run build-only

# 检查文件是否存在
ls -la dist/index.html
```

#### 2. 权限问题

确保 `manifest.json` 中的权限配置正确：
- `activeTab`: 访问当前标签页
- `scripting`: 注入脚本
- `storage`: 本地存储
- `sidePanel`: 侧边栏功能

#### 3. 模块导入错误

检查 `manifest.json` 中的 `web_accessible_resources` 配置是否包含所需文件。

### 开发工作流

1. **修改Vue前端**：
   ```bash
   cd plugin/popup-vue
   pnpm run dev  # 开发模式
   # 修改代码...
   pnpm run build-only  # 构建
   ```

2. **修改插件脚本**：
   - 直接编辑 `background.js`, `content.js` 等
   - 在 `chrome://extensions/` 中重新加载插件

3. **测试功能**：
   - 在不同网页上测试插件功能
   - 检查控制台是否有错误
   - 验证所有权限是否正常工作

## 部署发布

### 打包扩展

1. 确保所有文件都已构建完成
2. 将 `plugin` 文件夹打包成 zip 文件
3. 上传到 Chrome Web Store（需要开发者账号）

### 版本管理

在发布新版本前：
1. 更新 `manifest.json` 中的版本号
2. 更新 `popup-vue/package.json` 中的版本号
3. 构建并测试所有功能
4. 创建 Git 标签

## 技术栈

- **前端**: Vue 3 + TypeScript + Vite
- **样式**: CSS3 + SCSS
- **构建工具**: Vite
- **包管理**: pnpm
- **浏览器API**: Chrome Extensions API v3
- **协议支持**: MCP (Model Context Protocol)

## 贡献指南

1. Fork 项目
2. 创建功能分支: `git checkout -b feature/new-feature`
3. 提交更改: `git commit -am 'Add new feature'`
4. 推送分支: `git push origin feature/new-feature`
5. 提交 Pull Request

## 许可证

[License Type] - 详见 LICENSE 文件

## 联系方式

- Issue: [GitHub Issues](repository-issues-url)
- Email: [contact-email]
