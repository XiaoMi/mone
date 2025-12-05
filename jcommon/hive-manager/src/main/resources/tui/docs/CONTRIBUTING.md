# Contributing to Hive Manager TUI

Thank you for your interest in contributing! This document provides guidelines for contributing to the project.

## Getting Started

1. **Fork the repository**
2. **Clone your fork**
   ```bash
   git clone https://github.com/your-username/repo.git
   cd repo/src/main/resources/tui
   ```
3. **Install dependencies**
   ```bash
   npm install
   ```
4. **Run in development mode**
   ```bash
   npm run dev
   ```

## Development Workflow

### 1. Create a Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

Branch naming conventions:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation changes
- `refactor/` - Code refactoring
- `test/` - Adding or updating tests

### 2. Make Your Changes

- Follow the existing code style
- Add TypeScript types for new code
- Update documentation as needed
- Test your changes thoroughly

### 3. Test Your Changes

```bash
# Type checking
npm run type-check

# Manual testing
npm run dev
```

### 4. Commit Your Changes

Follow conventional commits format:

```bash
git commit -m "feat: add agent search functionality"
git commit -m "fix: resolve WebSocket connection issue"
git commit -m "docs: update API documentation"
```

Commit message format:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `style:` - Formatting
- `refactor:` - Code refactoring
- `test:` - Tests
- `chore:` - Maintenance

### 5. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a pull request on GitHub.

## Code Style Guidelines

### TypeScript

```typescript
// ✅ Good
interface User {
  id: number
  username: string
}

const getUser = async (id: number): Promise<User> => {
  // implementation
}

// ❌ Bad
const getUser = async (id) => {
  // no types
}
```

### React Components

```typescript
// ✅ Good
interface Props {
  title: string
  onSubmit: () => void
}

export const MyComponent: React.FC<Props> = ({ title, onSubmit }) => {
  return (
    <Box>
      <Text>{title}</Text>
    </Box>
  )
}

// ❌ Bad
export const MyComponent = (props: any) => {
  // implementation
}
```

### Naming Conventions

- **Components**: PascalCase - `AgentList`, `ChatWindow`
- **Functions**: camelCase - `fetchAgents`, `handleSubmit`
- **Files**: PascalCase for components, camelCase for utilities
- **Constants**: UPPER_SNAKE_CASE - `API_BASE_URL`

### File Organization

```
src/
├── components/      # Reusable UI components
├── screens/         # Full screen views
├── api/            # API clients
├── store/          # State management
├── types/          # TypeScript types
├── utils/          # Utility functions
└── hooks/          # Custom React hooks
```

## Adding New Features

### 1. New Screen

```typescript
// src/screens/NewScreen.tsx
import React from 'react'
import { Box, Text } from 'ink'
import { Header } from '../components/Header'

export const NewScreen: React.FC = () => {
  return (
    <Box flexDirection="column" padding={1}>
      <Header title="NEW SCREEN" />
      {/* Your content */}
    </Box>
  )
}
```

### 2. New API Endpoint

```typescript
// src/api/newApi.ts
import { http } from './request'
import type { ApiResponse } from '../types'

export const getNewData = async () => {
  return http.get<ApiResponse<DataType>>('/api/new-endpoint')
}
```

### 3. New Component

```typescript
// src/components/NewComponent.tsx
import React from 'react'
import { Box, Text } from 'ink'

interface Props {
  // Define props
}

export const NewComponent: React.FC<Props> = ({ /* props */ }) => {
  return (
    <Box>
      {/* Component content */}
    </Box>
  )
}
```

### 4. Update Store (if needed)

```typescript
// src/store/index.ts
interface AppState {
  // Add new state
  newData: NewType | null
  setNewData: (data: NewType) => void
}

export const useAppStore = create<AppState>((set) => ({
  // existing state...
  newData: null,
  setNewData: (data) => set({ newData: data }),
}))
```

## Testing Guidelines

### Manual Testing Checklist

- [ ] Login/logout works
- [ ] Agent list loads and displays correctly
- [ ] Search and filter function
- [ ] Chat messages send and receive
- [ ] WebSocket connection establishes
- [ ] Tasks execute properly
- [ ] Keyboard shortcuts work
- [ ] Error handling works
- [ ] UI renders correctly in different terminal sizes

### Edge Cases to Test

- [ ] Empty states (no agents, no tasks, no messages)
- [ ] Network errors
- [ ] Invalid credentials
- [ ] WebSocket disconnection
- [ ] Long text/messages
- [ ] Special characters in input
- [ ] Rapid key presses
- [ ] Terminal resize

## Documentation

When adding features, update:

1. **README.md** - If user-facing
2. **API.md** - If adding API calls
3. **ARCHITECTURE.md** - If changing structure
4. **QUICKSTART.md** - If affecting getting started
5. **Inline comments** - For complex logic

## Common Issues and Solutions

### Issue: Types not resolving
```bash
# Clear and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Issue: Build fails
```bash
# Check type errors
npm run type-check
```

### Issue: WebSocket not connecting
- Check backend is running
- Verify WS_BASE_URL in .env
- Check agent instance is running

## Pull Request Guidelines

### PR Title Format

```
feat: add agent search functionality
fix: resolve WebSocket reconnection issue
docs: update API documentation
```

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
How has this been tested?

## Screenshots (if applicable)
Terminal screenshots of the feature

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No new warnings
- [ ] Works in different terminal emulators
```

## Review Process

1. **Automated checks** (if configured)
   - Type checking
   - Build success

2. **Code review**
   - At least one approval required
   - Address review comments

3. **Testing**
   - Reviewer tests the changes
   - Verify in different environments

4. **Merge**
   - Squash and merge
   - Delete branch

## Getting Help

- Check existing documentation
- Review similar code in the codebase
- Ask questions in PR comments
- Open a discussion issue

## Code of Conduct

- Be respectful and constructive
- Welcome newcomers
- Focus on the code, not the person
- Assume good intentions

## Recognition

Contributors will be acknowledged in:
- CHANGELOG.md
- GitHub contributors page
- Release notes (for significant contributions)

Thank you for contributing! 🎉
