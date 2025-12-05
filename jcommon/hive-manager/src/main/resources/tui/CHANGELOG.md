# Changelog

All notable changes to the Hive Manager TUI project will be documented in this file.

## [1.0.0] - 2024-12-04

### Added
- Initial release of Hive Manager TUI
- User authentication (login/logout)
- Agent list with search and filter functionality
- Real-time chat with agents via WebSocket
- Task management (list and execute)
- Keyboard navigation throughout the application
- Responsive terminal layout using Ink and Yoga
- State management with Zustand
- API integration with Hive Manager backend
- Comprehensive documentation

### Features
- **Login Screen**: Username/password authentication with token management
- **Agent List**: Browse, search, and filter agents with real-time status
- **Chat Interface**: WebSocket-based real-time chat with message history
- **Task Management**: View and execute tasks with status tracking
- **Keyboard Shortcuts**: Full keyboard navigation support
- **Error Handling**: Graceful error handling and user feedback
- **Loading States**: Visual feedback for async operations

### Technical
- Built with React, Ink, and TypeScript
- Flexbox layout with Yoga
- WebSocket support for real-time communication
- HTTP client with Axios
- State management with Zustand
- Modular component architecture
