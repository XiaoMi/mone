import React, { useEffect } from 'react'
import { Box, useApp } from 'ink'
import { useAppStore } from './store'
import { Login } from './screens/Login'
import { AgentList } from './screens/AgentList'
import { Chat } from './screens/Chat'
import { TaskList } from './screens/TaskList'

export const App: React.FC = () => {
  const { exit } = useApp()
  const currentScreen = useAppStore((state) => state.currentScreen)

  useEffect(() => {
    // Handle cleanup on exit
    return () => {
      // Cleanup logic here
    }
  }, [])

  const renderScreen = () => {
    switch (currentScreen) {
      case 'login':
        return <Login />
      case 'agents':
        return <AgentList />
      case 'chat':
        return <Chat />
      case 'tasks':
        return <TaskList />
      default:
        return <Login />
    }
  }

  return (
    <Box flexDirection="column">
      {renderScreen()}
    </Box>
  )
}
