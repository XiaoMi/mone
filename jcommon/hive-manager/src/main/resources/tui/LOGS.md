# 日志查看指南

## 📋 日志类型

TUI 应用有两种日志输出方式：

### 1. 终端输出（实时）
- 运行 `npm run dev` 时，所有输出直接显示在终端
- 包括应用状态、错误信息、调试信息

### 2. 日志文件（持久化）
- 自动保存到 `logs/` 目录
- 文件名格式：`tui-YYYY-MM-DD.log`
- 每天一个日志文件

## 🔍 查看日志的方法

### 方法 1：查看终端输出

**普通模式**（只显示重要信息）：
```bash
npm run dev
```

**调试模式**（显示详细日志）：
```bash
npm run dev:debug
# 或
DEBUG=true npm run dev
```

### 方法 2：查看日志文件

**使用日志查看器（推荐）**：
```bash
npm run logs
# 或
./scripts/view-logs.sh
```

交互式菜单：
```
1) 查看最新 50 行
2) 查看完整日志
3) 实时跟踪日志 (tail -f)
4) 搜索日志
5) 清理日志
```

**快速查看最新日志**：
```bash
tail -f logs/tui-$(date +%Y-%m-%d).log
# 或
npm run logs:tail
```

**查看指定日期的日志**：
```bash
cat logs/tui-2024-12-04.log
```

**搜索日志**：
```bash
grep "ERROR" logs/tui-2024-12-04.log
grep -i "websocket" logs/tui-*.log
```

## 📊 日志级别

日志按级别分类：

| 级别 | 说明 | 示例 |
|------|------|------|
| **DEBUG** | 详细调试信息 | WebSocket 消息内容 |
| **INFO** | 一般信息 | API 请求/响应 |
| **WARN** | 警告信息 | 连接超时 |
| **ERROR** | 错误信息 | 请求失败、异常 |

### 日志格式

```
[2024-12-04T10:30:45.123Z] [INFO] API Request: GET /api/agent/list
[2024-12-04T10:30:45.456Z] [INFO] API Response: GET /api/agent/list - 200
[2024-12-04T10:30:50.789Z] [DEBUG] WebSocket receive: {"type":"message","data":"..."}
[2024-12-04T10:31:00.000Z] [ERROR] WebSocket error {"message":"Connection refused"}
```

## 🛠️ 调试技巧

### 1. 启用详细日志

编辑 `.env` 添加：
```env
DEBUG=true
NODE_ENV=development
```

或直接运行：
```bash
DEBUG=true npm run dev
```

### 2. 过滤特定日志

**只看 ERROR**：
```bash
grep "\[ERROR\]" logs/tui-*.log
```

**只看 API 请求**：
```bash
grep "API Request" logs/tui-*.log
```

**只看 WebSocket**：
```bash
grep "WebSocket" logs/tui-*.log
```

### 3. 实时监控

在一个终端运行应用：
```bash
npm run dev
```

在另一个终端实时查看日志：
```bash
npm run logs:tail
```

### 4. 分析错误

查找所有错误并统计：
```bash
grep "\[ERROR\]" logs/tui-*.log | wc -l
```

查看最近的错误：
```bash
grep "\[ERROR\]" logs/tui-*.log | tail -n 10
```

## 📁 日志文件位置

```
tui/
└── logs/
    ├── tui-2024-12-01.log
    ├── tui-2024-12-02.log
    ├── tui-2024-12-03.log
    └── tui-2024-12-04.log  ← 今天的日志
```

## 🧹 清理日志

**清理所有日志**：
```bash
npm run logs:clear
# 或
rm -rf logs/*.log
```

**只保留最近 7 天的日志**：
```bash
find logs/ -name "*.log" -mtime +7 -delete
```

## 🔧 自定义日志

如果需要添加自定义日志，在代码中使用：

```typescript
import { logger } from './utils/logger'

// 调试信息
logger.debug('Debug message', { data: someData })

// 一般信息
logger.info('User logged in', { username: 'user' })

// 警告
logger.warn('Connection slow', { latency: 5000 })

// 错误
logger.error('Failed to connect', { error: err })

// API 请求
logger.apiRequest('GET', '/api/agents', { params })

// WebSocket 消息
logger.wsMessage('send', messageData)
```

## 📈 日志分析示例

### 统计 API 调用次数
```bash
grep "API Request" logs/tui-*.log | wc -l
```

### 查看失败的 API 请求
```bash
grep "API Response.*[45][0-9][0-9]" logs/tui-*.log
```

### 分析 WebSocket 连接问题
```bash
grep -E "WebSocket (Connect|Disconnect|error)" logs/tui-*.log
```

### 查看用户操作流程
```bash
grep -E "(Login|Logout|Agent.*select|Message.*send)" logs/tui-*.log
```

## ⚠️ 注意事项

1. **日志文件会持续增长**
   - 定期清理旧日志
   - 或使用日志轮转工具

2. **调试模式会产生大量日志**
   - 生产环境建议关闭 DEBUG
   - 只在开发/调试时使用

3. **敏感信息**
   - 日志可能包含 token、密码等
   - 不要分享日志文件给未授权人员
   - 建议在 `.gitignore` 中排除 logs/

4. **性能影响**
   - 频繁写日志可能影响性能
   - 根据需要调整日志级别

## 🆘 常见问题

### Q: 没有日志文件生成？
A: 检查 `logs/` 目录是否存在，应用是否有写入权限

### Q: 日志太多看不过来？
A: 使用 grep 过滤，或只在调试时启用 DEBUG 模式

### Q: 如何只看最新日志？
A: 使用 `tail -f` 或 `npm run logs:tail`

### Q: 日志文件占用空间太大？
A: 定期运行 `npm run logs:clear` 清理

## 📚 相关命令速查

```bash
# 查看日志
npm run logs              # 交互式查看器
npm run logs:tail         # 实时跟踪
npm run logs:clear        # 清理日志

# 运行模式
npm run dev              # 普通模式
npm run dev:debug        # 调试模式（详细日志）

# 手动查看
tail -f logs/tui-*.log   # 实时跟踪
less logs/tui-*.log      # 分页查看
grep "ERROR" logs/*.log  # 搜索错误
```
