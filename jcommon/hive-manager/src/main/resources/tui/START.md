# 🚀 构建和启动指南

## 快速启动（推荐）

### 方式一：使用自动化脚本

```bash
# 1. 进入 TUI 目录
cd /home/mason/disk1/Workspaces/github/mone-original/jcommon/hive-manager/src/main/resources/tui

# 2. 运行自动化设置脚本
./scripts/setup.sh

# 3. 启动开发模式
npm run dev
```

### 方式二：手动步骤

```bash
# 1. 进入 TUI 目录
cd /home/mason/disk1/Workspaces/github/mone-original/jcommon/hive-manager/src/main/resources/tui

# 2. 安装依赖
npm install

# 3. 配置环境变量
cp .env.example .env

# 4. 编辑 .env 文件（可选，使用默认配置也可以）
nano .env  # 或使用你喜欢的编辑器

# 5. 启动开发模式
npm run dev
```

## 详细步骤

### 步骤 1: 安装依赖

```bash
cd /home/mason/disk1/Workspaces/github/mone-original/jcommon/hive-manager/src/main/resources/tui

npm install
```

**预期输出：**
```
added 234 packages in 15s
```

### 步骤 2: 配置环境

```bash
cp .env.example .env
```

编辑 `.env` 文件：

```env
# API 基础 URL（修改为您的后端地址）
API_BASE_URL=http://localhost:8080/agent-manager

# WebSocket 基础 URL（修改为您的后端地址）
WS_BASE_URL=ws://localhost:8080/agent-manager
```

**注意：** 如果后端不在本地或端口不同，请修改这些 URL。

### 步骤 3: 启动应用

#### 开发模式（推荐用于开发）

```bash
npm run dev
```

**特点：**
- 支持热重载
- 即时看到代码更改
- 包含完整的错误信息
- 直接运行 TypeScript 代码

**预期输出：**
```
╔═══════════════════════════════════════════╗
║           HIVE MANAGER                    ║
╚═══════════════════════════════════════════╝

╔═══ Welcome to Hive Manager TUI ═══╗
Username: _
```

#### 生产模式

```bash
# 1. 先构建
npm run build

# 2. 运行构建产物
npm start
```

**构建输出位置：** `dist/index.js`

### 步骤 4: 登录使用

启动后，您会看到登录界面：

```
╔═══ LOGIN ═══╗

╔═══ Welcome to Hive Manager TUI ═══╗
Please enter your credentials to continue

Username: _
Password:

[Tab] Switch field | [Enter] Submit
```

**操作步骤：**
1. 输入用户名
2. 按 `Tab` 切换到密码字段
3. 输入密码
4. 按 `Enter` 登录

## 不同启动方式对比

| 方式 | 命令 | 用途 | 热重载 | 速度 |
|-----|------|------|--------|------|
| 开发模式 | `npm run dev` | 开发调试 | ✅ | 快 |
| 生产构建 | `npm run build` | 打包发布 | ❌ | 慢 |
| 生产运行 | `npm start` | 运行构建版本 | ❌ | 快 |
| 全局安装 | `npm install -g .` | 系统命令 | ❌ | 快 |

## 全局安装（可选）

如果您想在任何地方运行 `hive-tui` 命令：

```bash
# 在 tui 目录下执行
npm run build
npm install -g .

# 现在可以在任何地方运行
hive-tui
```

**卸载全局安装：**
```bash
npm uninstall -g hive-manager-tui
```

## 常见问题排查

### 问题 1: 依赖安装失败

**症状：**
```
npm ERR! code ERESOLVE
```

**解决方案：**
```bash
# 清理缓存
npm cache clean --force

# 删除 node_modules 和 lock 文件
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

### 问题 2: TypeScript 类型错误

**症状：**
```
error TS2307: Cannot find module...
```

**解决方案：**
```bash
# 运行类型检查
npm run type-check

# 如果有错误，检查 tsconfig.json 配置
```

### 问题 3: 无法连接到后端

**症状：**
登录时显示 "Network error"

**解决方案：**
1. 确认后端服务正在运行
2. 检查 `.env` 中的 URL 配置
3. 测试后端连接：
   ```bash
   curl http://localhost:8080/agent-manager/api/health
   ```

### 问题 4: WebSocket 连接失败

**症状：**
聊天界面显示 "WebSocket connection failed"

**解决方案：**
1. 确认 Agent 实例正在运行
2. 检查 `.env` 中的 `WS_BASE_URL`
3. 检查防火墙设置

### 问题 5: 端口被占用

如果后端使用不同端口，修改 `.env`：

```env
API_BASE_URL=http://localhost:9090/agent-manager
WS_BASE_URL=ws://localhost:9090/agent-manager
```

### 问题 6: 终端显示异常

**症状：**
- 出现乱码
- 布局错乱
- 颜色不显示

**解决方案：**
1. 确保终端支持 UTF-8
2. 使用现代终端模拟器（iTerm2、Windows Terminal、Alacritty）
3. 增大终端窗口尺寸（建议至少 80x24）

## 开发调试

### 启用详细日志

修改代码添加调试输出：

```typescript
// 在需要调试的地方添加
console.log('Debug:', data)
console.error('Error:', error)
```

开发模式下可以在终端看到输出。

### 监控 WebSocket 消息

在 `src/screens/Chat.tsx` 中：

```typescript
ws.on('message', (data: Buffer) => {
  console.log('Received:', data.toString())
  handleWebSocketMessage(data.toString())
})
```

### 检查 API 请求

在 `src/api/request.ts` 中的拦截器添加日志：

```typescript
this.instance.interceptors.request.use(
  (config) => {
    console.log('Request:', config.method, config.url)
    return config
  }
)
```

## 性能优化

### 生产构建优化

```bash
# 构建前检查类型
npm run type-check

# 构建
npm run build

# 检查构建大小
ls -lh dist/
```

### 减少包体积

如果需要优化包大小，可以在 `tsup.config.ts` 中配置：

```typescript
export default defineConfig({
  entry: ['src/index.tsx'],
  format: ['esm'],
  minify: true,  // 启用压缩
  treeshake: true, // 启用 tree shaking
})
```

## 运行环境要求

- **Node.js**: >= 18.0.0
- **npm**: >= 9.0.0
- **终端**: 支持 UTF-8 和 ANSI 颜色
- **操作系统**: Linux, macOS, Windows (WSL/Git Bash)

### 检查环境

```bash
# 检查 Node.js 版本
node -v
# 应该显示 v18.x.x 或更高

# 检查 npm 版本
npm -v
# 应该显示 9.x.x 或更高

# 检查终端支持
echo $TERM
# 应该显示 xterm-256color 或类似
```

## 下一步

启动成功后：

1. 📖 阅读 [README.md](README.md) 了解功能
2. ⌨️ 熟悉键盘快捷键
3. 💬 尝试与 Agent 聊天
4. 📋 查看和执行任务
5. 🔧 根据需要自定义配置

## 获取帮助

如果遇到问题：

1. 查看本文档的"常见问题排查"部分
2. 阅读 [docs/QUICKSTART.md](docs/QUICKSTART.md)
3. 检查 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
4. 查看终端错误信息

祝您使用愉快！🎉
