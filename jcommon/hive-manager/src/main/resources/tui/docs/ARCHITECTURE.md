# Architecture Documentation

## Overview

Hive Manager TUI is a terminal-based user interface built with React (via Ink) that mirrors the functionality of the web-based Hive Manager application. It follows a component-based architecture with centralized state management.

## Technology Stack

### Core
- **Ink** (v5.0.1): React renderer for CLIs
- **React** (v18.2.0): UI library
- **Yoga**: Flexbox layout engine (used internally by Ink)
- **TypeScript** (v5.6.3): Type safety and development experience

### State Management
- **Zustand** (v4.5.0): Lightweight state management

### Network
- **Axios** (v1.7.9): HTTP client for REST API calls
- **ws** (v8.18.0): WebSocket client for real-time chat

### UI/UX
- **chalk** (v5.3.0): Terminal string styling
- **ink-text-input**: Text input component
- **ink-select-input**: Selection menu component
- **ink-spinner**: Loading spinners
- **ink-gradient**: Gradient text
- **ink-big-text**: Large text rendering

### Utilities
- **date-fns** (v3.0.0): Date formatting
- **uuid** (v10.0.0): Unique ID generation

## Architecture Layers

### 1. Presentation Layer (UI Components)

#### Screens (`src/screens/`)
Main application screens that represent full pages:
- `Login.tsx`: Authentication screen
- `AgentList.tsx`: Agent browsing and selection
- `Chat.tsx`: Real-time chat interface
- `TaskList.tsx`: Task management interface

#### Components (`src/components/`)
Reusable UI components:
- `Header.tsx`: Application header with title and user info
- `Box.tsx`: Custom styled container component
- `Loading.tsx`: Loading state indicator
- `StatusBadge.tsx`: Status display component
- `ErrorMessage.tsx`: Error display component
- `SuccessMessage.tsx`: Success notification component
- `Navigation.tsx`: Navigation menu component

### 2. Application Layer

#### App Router (`src/App.tsx`)
- Central routing logic
- Screen rendering based on current state
- Lifecycle management

#### Entry Point (`src/index.tsx`)
- Application initialization
- Terminal setup
- Signal handling (SIGINT, SIGTERM)

### 3. State Management Layer (`src/store/`)

Using Zustand for centralized state management:

```typescript
interface AppState {
  // Authentication
  user: User | null
  token: string | null

  // Navigation
  currentScreen: Screen

  // Agent context
  selectedAgent: Agent | null
  selectedInstance: AgentInstance | null

  // Messages
  messages: Message[]

  // UI state
  loading: boolean
}
```

### 4. Data Layer

#### API Clients (`src/api/`)
- `request.ts`: Base HTTP client with interceptors
- `auth.ts`: Authentication API
- `agent.ts`: Agent management API
- `task.ts`: Task management API

#### Types (`src/types/`)
TypeScript type definitions for:
- Domain models (User, Agent, Task, Message)
- API responses
- Application state

### 5. Utilities Layer (`src/utils/`)
- `config.ts`: Configuration and environment variables
- `format.ts`: Formatting utilities (dates, strings)

## Data Flow

### 1. User Authentication Flow
```
User Input → Login Screen → Auth API → Store Token → Navigate to Agents
```

### 2. Agent Selection Flow
```
Agent List → User Selection → Store Agent/Instance → Navigate to Chat
```

### 3. Chat Message Flow
```
User Input → WebSocket → Message Handler → Update Store → Re-render UI
```

### 4. Task Execution Flow
```
Task List → User Selection → Execute API → Poll Status → Update UI
```

## WebSocket Architecture

### Connection Management
- WebSocket connection established when entering chat screen
- Connection URL format: `ws://host/ws/{agent}:{group}:{version}:{ip}:{port}`
- Automatic cleanup on screen exit

### Message Protocol
```typescript
// Outgoing
{
  mapData: {
    outerTag: 'use_mcp_tool',
    server_name: string,
    tool_name: string,
    arguments: string
  },
  agentId: number,
  agentInstance: object,
  conversationId: string
}

// Incoming
Raw text or structured data parsed and added to message list
```

## Component Communication

### Props Down, Events Up
- Parent components pass data via props
- Child components emit events via callbacks
- Store updates trigger re-renders

### Global State Access
```typescript
// Direct store access in components
const user = useAppStore((state) => state.user)
const setScreen = useAppStore((state) => state.setScreen)
```

## Layout System (Yoga/Flexbox)

Ink uses Yoga for flexbox layout:

```tsx
<Box flexDirection="column" padding={1}>
  <Box justifyContent="space-between">
    {/* Header */}
  </Box>
  <Box flex={1}>
    {/* Content */}
  </Box>
  <Box>
    {/* Footer */}
  </Box>
</Box>
```

## Error Handling

### Levels
1. **Network Errors**: Caught in API layer, displayed in UI
2. **Validation Errors**: Prevented at input level
3. **Runtime Errors**: Caught in try/catch blocks
4. **WebSocket Errors**: Reconnection logic or user notification

### Error Display
- `ErrorMessage` component for consistent error UI
- Context-aware error messages
- User-friendly error descriptions

## Performance Considerations

### Optimization Strategies
1. **Debounced Search**: Prevent excessive API calls
2. **Selective Rendering**: Only render visible messages (last 15)
3. **Memoization**: Component memoization where appropriate
4. **WebSocket Throttling**: Batch frequent updates

### Memory Management
- Cleanup WebSocket connections on unmount
- Clear message history when needed
- Remove event listeners on component cleanup

## Security

### Token Management
- Tokens stored in global memory (runtime only)
- Automatic token injection via HTTP interceptors
- Token cleared on logout or error

### Input Validation
- Client-side validation before API calls
- Sanitized user inputs
- Password masking in UI

## Testing Strategy

### Unit Tests
- Component rendering tests
- Utility function tests
- Store action tests

### Integration Tests
- Screen navigation flows
- API integration tests
- WebSocket communication tests

### E2E Tests
- Full user workflows
- Error scenarios
- Edge cases

## Future Enhancements

### Planned Features
1. File upload support
2. Multi-agent chat
3. Advanced search and filtering
4. Task scheduling
5. Notification system
6. Offline mode
7. Configuration persistence

### Technical Improvements
1. Add comprehensive test coverage
2. Implement logging system
3. Add performance monitoring
4. Enhance error recovery
5. Improve accessibility
6. Add plugin system

## Build and Deployment

### Development
```bash
npm run dev  # Watch mode with tsx
```

### Production
```bash
npm run build  # Build with tsup
npm start      # Run built version
```

### Distribution
```bash
npm install -g .  # Install globally
hive-tui         # Run from anywhere
```

## Configuration

### Environment Variables
- `API_BASE_URL`: Backend API endpoint
- `WS_BASE_URL`: WebSocket endpoint

### Build Configuration
- `tsconfig.json`: TypeScript configuration
- `tsup.config.ts`: Build configuration
- `package.json`: Dependencies and scripts

## Debugging

### Development Tools
1. **tsx**: Fast TypeScript execution
2. **TypeScript**: Type checking
3. **Console logging**: Debug output
4. **Network inspection**: API/WebSocket monitoring

### Common Issues
1. **WebSocket connection failures**: Check backend URL
2. **API errors**: Verify authentication token
3. **Layout issues**: Check terminal size
4. **Input not working**: Verify focus state
