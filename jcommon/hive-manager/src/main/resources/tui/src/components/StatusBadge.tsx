import React from 'react'
import { Text } from 'ink'
import chalk from 'chalk'

interface StatusBadgeProps {
  status: 'running' | 'stopped' | 'pending' | 'completed' | 'failed' | string
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  const getStatusDisplay = () => {
    switch (status.toLowerCase()) {
      case 'running':
        return chalk.green(`[●] ${status.toUpperCase()}`)
      case 'stopped':
        return chalk.gray(`[○] ${status.toUpperCase()}`)
      case 'pending':
        return chalk.yellow(`[◐] ${status.toUpperCase()}`)
      case 'completed':
        return chalk.greenBright(`[✓] ${status.toUpperCase()}`)
      case 'failed':
        return chalk.red(`[✗] ${status.toUpperCase()}`)
      default:
        return chalk.white(`[?] ${status.toUpperCase()}`)
    }
  }

  return <Text>{getStatusDisplay()}</Text>
}
