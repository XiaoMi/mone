# Quick Start Guide

Get up and running with Hive Manager TUI in 5 minutes!

## 🔍 快速测试后端连接

### 测试 1: Ping 后端

```bash
curl http://localhost:8080/agent-manager/ping
```

**期望结果**: `pong`

### 测试 2: 登录接口

```bash
curl -X POST http://localhost:8080/agent-manager/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","password":"your-password"}'
```

**期望结果**: 返回 JSON 包含 user 和 token

### 测试 3: 使用自动测试脚本

```bash
npm run test:backend
```

## 📡 TUI 调用的接口

- **登录**: `POST /api/v1/users/login` ✅
- **Agent列表**: `GET /api/v1/agents/list` ✅
- **任务列表**: `GET /api/v1/tasks` ✅
- **Ping**: `GET /ping` ✅

## Prerequisites

Before you begin, ensure you have:
- ✅ Node.js 18+ installed
- ✅ npm or yarn package manager
- ✅ Access to Hive Manager backend API
- ✅ A terminal emulator with UTF-8 and color support

## Installation Steps

### 1. Navigate to the TUI Directory

```bash
cd /home/mason/disk1/Workspaces/github/mone-original/jcommon/hive-manager/src/main/resources/tui
```

### 2. Install Dependencies

```bash
npm install
```

This will install all required packages including:
- Ink and React
- Zustand for state management
- Axios for HTTP requests
- WebSocket client
- And more...

### 3. Configure Environment

Copy the example environment file:

```bash
cp .env.example .env
```

Edit `.env` to match your backend configuration:

```env
API_BASE_URL=http://localhost:8080/agent-manager
WS_BASE_URL=ws://localhost:8080/agent-manager
```

### 4. Start the Application

#### Development Mode (with hot reload)
```bash
npm run dev
```

#### Production Mode
```bash
# Build first
npm run build

# Then run
npm start
```

## First Time Usage

### 1. Login Screen

When you first launch the app, you'll see the login screen:

```
╔═══ Welcome to Hive Manager TUI ═══╗

Username: _
Password:

[Tab] Switch field | [Enter] Submit
```

- Type your username
- Press `Tab` to switch to password
- Type your password (it will be masked)
- Press `Enter` to login

### 2. Agent List

After successful login, you'll see the agent list:

```
╔═══ AGENTS ═══╗

Total: 5 | Filter: ALL

[S] Search | [F] Filter | [R] Refresh | [Q] Quit

❯ MyAgent ★ - A helpful assistant [RUNNING]
  DataAgent - Process data [STOPPED]
  ...
```

**Keyboard Shortcuts:**
- `↑/↓` - Navigate through agents
- `Enter` - Select agent and start chat
- `S` - Search agents
- `F` - Toggle favorite filter
- `R` - Refresh list
- `Q` - Quit application

### 3. Chat Interface

Once you select a running agent:

```
╔═══ CHAT ═══╗

● Agent: MyAgent

[I] Input | [C] Clear | [Esc] Back

────────────────────────────────────
MyAgent:
Hello! How can I help you today?

You:
What can you do?

MyAgent:
I can help you with various tasks...
────────────────────────────────────
```

**Keyboard Shortcuts:**
- `I` or `M` - Enter message input mode
- `C` - Clear chat history
- `Esc` - Return to agent list

### 4. Task Management

From the agent list, you can also view tasks:

```
╔═══ TASKS ═══╗

Total Tasks: 3

[R] Refresh | [Esc] Back | [Q] Quit

❯ Data Processing - Process user data [PENDING]
  Model Training - Train ML model [RUNNING]
  Report Generation - Create reports [COMPLETED]
```

**Keyboard Shortcuts:**
- `↑/↓` - Navigate tasks
- `Enter` - Execute selected task
- `R` - Refresh task list
- `Esc` - Go back

## Common Use Cases

### Use Case 1: Quick Agent Chat

```bash
# 1. Start the app
npm run dev

# 2. Login with credentials
Username: your-username
Password: ********

# 3. Select an agent (↑/↓ + Enter)

# 4. Start chatting (Press 'I')
Message: Hello, what can you do?

# 5. Exit (Esc → Q)
```

### Use Case 2: Execute a Task

```bash
# 1. Login

# 2. Navigate to tasks (use navigation or direct access)

# 3. Select task (↑/↓ + Enter)

# 4. Monitor execution status

# 5. Return to agent list (Esc)
```

### Use Case 3: Search and Filter Agents

```bash
# 1. From agent list, press 'S' for search

# 2. Type search term
Search: data

# 3. Press 'Esc' to exit search

# 4. Press 'F' to toggle favorite filter
```

## Troubleshooting

### Connection Errors

**Problem:** "Failed to connect to API"

**Solution:**
1. Check if backend is running
2. Verify `.env` configuration
3. Test API endpoint manually:
   ```bash
   curl http://localhost:8080/agent-manager/api/health
   ```

### WebSocket Issues

**Problem:** "WebSocket connection failed"

**Solution:**
1. Ensure agent instance is running
2. Check WS_BASE_URL in `.env`
3. Verify firewall/proxy settings

### Display Issues

**Problem:** Garbled characters or broken layout

**Solution:**
1. Ensure terminal supports UTF-8
2. Increase terminal window size
3. Try a different terminal emulator (iTerm2, Windows Terminal, etc.)

### Authentication Failed

**Problem:** "Login failed" error

**Solution:**
1. Verify username and password
2. Check if backend authentication service is running
3. Look for error messages in terminal

## Tips and Tricks

### Productivity Tips

1. **Use Keyboard Shortcuts**: Learn the shortcuts for faster navigation
2. **Keep Terminal Open**: Run in a dedicated terminal tab
3. **Favorite Agents**: Mark frequently used agents as favorites
4. **Clear Chat**: Use `C` to clear chat history when switching topics

### Advanced Usage

1. **Global Installation**: Install globally for system-wide access
   ```bash
   npm install -g .
   hive-tui
   ```

2. **Alias Creation**: Add to your shell config
   ```bash
   echo "alias hive='cd /path/to/tui && npm run dev'" >> ~/.bashrc
   ```

3. **Multiple Instances**: Run multiple TUI instances for different agents

## Next Steps

- 📖 Read the full [README.md](../README.md)
- 🏗️ Explore [ARCHITECTURE.md](ARCHITECTURE.md)
- 🔧 Customize the configuration
- 💡 Check out advanced features

## Getting Help

If you encounter issues:

1. Check this quick start guide
2. Review the main README
3. Check the ARCHITECTURE documentation
4. Review error messages carefully
5. Verify backend is running and accessible

## Video Tutorial

(Coming soon - screenshots and video walkthrough)

## Keyboard Reference Card

```
┌─────────────────────────────────────────┐
│        HIVE MANAGER TUI SHORTCUTS       │
├─────────────────────────────────────────┤
│ GLOBAL                                  │
│  Q       Quit application               │
│  Esc     Go back                        │
│  Tab     Switch fields                  │
│  ↑/↓     Navigate lists                 │
│  Enter   Select/Submit                  │
├─────────────────────────────────────────┤
│ AGENT LIST                              │
│  S       Search                         │
│  F       Toggle favorites               │
│  R       Refresh                        │
├─────────────────────────────────────────┤
│ CHAT                                    │
│  I/M     Input message                  │
│  C       Clear history                  │
├─────────────────────────────────────────┤
│ TASKS                                   │
│  R       Refresh                        │
│  Enter   Execute task                   │
└─────────────────────────────────────────┘
```

Happy chatting! 🚀
