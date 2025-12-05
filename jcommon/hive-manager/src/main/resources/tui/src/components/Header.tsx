import React from 'react'
import { Box, Text } from 'ink'
import Gradient from 'ink-gradient'
import BigText from 'ink-big-text'
import chalk from 'chalk'
import { useAppStore } from '../store'

interface HeaderProps {
  title?: string
  showUser?: boolean
}

export const Header: React.FC<HeaderProps> = ({ title = 'HIVE MANAGER', showUser = true }) => {
  const user = useAppStore((state) => state.user)

  return (
    <Box flexDirection="column" marginBottom={1}>
      <Gradient name="passion">
        <BigText text={title} font="tiny" />
      </Gradient>
      {showUser && user && (
        <Box justifyContent="space-between" marginTop={1}>
          <Text>
            {chalk.cyan('●')} User: {chalk.green(user.username)} ({user.cname})
          </Text>
          <Text>
            {chalk.yellow('[Tab]')} Navigate | {chalk.yellow('[Esc]')} Back | {chalk.yellow('[Q]')} Quit
          </Text>
        </Box>
      )}
      <Box>
        <Text>{chalk.cyan('━'.repeat(70))}</Text>
      </Box>
    </Box>
  )
}
