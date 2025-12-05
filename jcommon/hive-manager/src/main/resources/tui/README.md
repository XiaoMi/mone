# Hive Manager TUI

A Terminal User Interface (TUI) for Hive Manager - Agent Management and Chat System, built with Ink and React.

## Features

This TUI application replicates the functionality of the web-based Hive Manager interface in the terminal:

### 🔐 Authentication
- User login with username/password
- Session management with token-based authentication

### 🤖 Agent Management
- Browse and search available agents
- Filter by favorites
- View agent details and status
- Real-time agent instance status (running/stopped)
- Connect to running agents for chat

### 💬 Chat Interface
- Real-time WebSocket-based chat with agents
- Message history display
- Stream responses from agents
- Support for commands and special operations

### 📋 Task Management
- View task list
- Execute tasks
- Monitor task status (pending, running, completed, failed)
- Task filtering and search

## Prerequisites

- Node.js >= 18.0.0
- npm or yarn
- Access to Hive Manager backend API

## Installation

1. Navigate to the TUI directory:
```bash
cd src/main/resources/tui
```

2. Install dependencies:
```bash
npm install
```

3. Configure environment variables:
```bash
cp .env.example .env
```

Edit `.env` to set your API endpoints:
```env
API_BASE_URL=http://localhost:8080/agent-manager
WS_BASE_URL=ws://localhost:8080/agent-manager
```

## Usage

### Development Mode

Run in development with hot reload:
```bash
npm run dev
```

### Build

Build the application:
```bash
npm run build
```

### Production

Run the built version:
```bash
npm start
```

Or install globally and run:
```bash
npm install -g .
hive-tui
```

## Keyboard Shortcuts

### Global
- `Q` - Quit application
- `Esc` - Go back to previous screen
- `Tab` - Switch between fields/options
- `↑/↓` - Navigate lists
- `Enter` - Select/Submit

### Login Screen
- `Tab` - Switch between username and password
- `Enter` - Submit login

### Agent List
- `S` - Search agents
- `F` - Toggle favorite filter
- `R` - Refresh list
- `Enter` - Select agent and enter chat

### Chat Screen
- `I` or `M` - Enter message input mode
- `C` - Clear chat history
- `Esc` - Exit to agent list

### Task List
- `R` - Refresh task list
- `Enter` - Execute selected task

## Project Structure

```
tui/
├── src/
│   ├── api/              # API clients
│   │   ├── auth.ts       # Authentication API
│   │   ├── agent.ts      # Agent management API
│   │   ├── task.ts       # Task management API
│   │   └── request.ts    # HTTP client
│   ├── components/       # Reusable UI components
│   │   ├── Box.tsx       # Custom box component
│   │   ├── Header.tsx    # Application header
│   │   ├── Loading.tsx   # Loading spinner
│   │   ├── StatusBadge.tsx
│   │   └── ErrorMessage.tsx
│   ├── screens/          # Screen components
│   │   ├── Login.tsx     # Login screen
│   │   ├── AgentList.tsx # Agent list screen
│   │   ├── Chat.tsx      # Chat screen
│   │   └── TaskList.tsx  # Task list screen
│   ├── store/            # State management
│   │   └── index.ts      # Zustand store
│   ├── types/            # TypeScript types
│   │   └── index.ts
│   ├── utils/            # Utility functions
│   │   ├── config.ts     # Configuration
│   │   └── format.ts     # Formatting utilities
│   ├── App.tsx           # Main App component
│   └── index.tsx         # Entry point
├── package.json
├── tsconfig.json
└── README.md
```

## Technology Stack

- **Ink** - React for CLI applications
- **Yoga** - Flexbox layout engine (used by Ink)
- **React** - UI library
- **Zustand** - State management
- **Axios** - HTTP client
- **ws** - WebSocket client
- **chalk** - Terminal styling
- **TypeScript** - Type safety

## Features Comparison

| Feature | Web UI | TUI |
|---------|--------|-----|
| User Authentication | ✅ | ✅ |
| Agent List/Search | ✅ | ✅ |
| Agent Favorites | ✅ | ✅ |
| Real-time Chat | ✅ | ✅ |
| WebSocket Support | ✅ | ✅ |
| Task Management | ✅ | ✅ |
| Task Execution | ✅ | ✅ |
| Responsive Design | ✅ | ✅ |
| Dark/Light Theme | ✅ | ⚠️ (Terminal-dependent) |
| Image Display | ✅ | ⚠️ (Limited) |
| File Upload | ✅ | ⚠️ (Planned) |

## Development

### Type Checking
```bash
npm run type-check
```

### Building
```bash
npm run build
```

The build output will be in the `dist/` directory.

## Troubleshooting

### WebSocket Connection Issues
- Ensure the backend server is running
- Check that WS_BASE_URL in `.env` is correct
- Verify firewall settings allow WebSocket connections

### API Connection Issues
- Verify API_BASE_URL in `.env` points to the correct backend
- Check network connectivity
- Ensure backend CORS settings allow your requests

### Display Issues
- Ensure your terminal supports UTF-8 and ANSI colors
- Try increasing terminal window size
- Some features may require a modern terminal emulator

## Contributing

1. Follow the existing code style
2. Add TypeScript types for new features
3. Test in different terminal emulators
4. Update documentation for new features

## License

[Same as parent project]

## Credits

Built with:
- [Ink](https://github.com/vadimdemedes/ink) - React for CLIs
- [Yoga](https://github.com/facebook/yoga) - Cross-platform layout engine
