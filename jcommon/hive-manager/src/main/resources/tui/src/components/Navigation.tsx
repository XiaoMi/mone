import React from 'react'
import { Box, Text } from 'ink'
import SelectInput from 'ink-select-input'
import chalk from 'chalk'
import { useAppStore } from '../store'
import type { Screen } from '../types'

export const Navigation: React.FC = () => {
  const setScreen = useAppStore((state) => state.setScreen)
  const user = useAppStore((state) => state.user)

  if (!user) return null

  const items = [
    { label: chalk.cyan('📋 Agent List'), value: 'agents' as Screen },
    { label: chalk.green('💬 Chat'), value: 'chat' as Screen },
    { label: chalk.yellow('📝 Tasks'), value: 'tasks' as Screen },
    { label: chalk.red('🚪 Logout'), value: 'login' as Screen },
  ]

  return (
    <Box
      borderStyle="round"
      borderColor="cyan"
      flexDirection="column"
      padding={1}
    >
      <Text bold color="cyan">
        Navigation Menu
      </Text>
      <Box marginTop={1}>
        <SelectInput items={items} onSelect={(item) => setScreen(item.value)} />
      </Box>
    </Box>
  )
}
