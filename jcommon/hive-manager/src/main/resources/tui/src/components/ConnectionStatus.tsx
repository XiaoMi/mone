import React, { useState, useEffect } from 'react'
import { Box, Text } from 'ink'
import chalk from 'chalk'
import axios from 'axios'
import { API_BASE_URL } from '../utils/config'

interface ConnectionStatusProps {
  onStatusChange?: (isOnline: boolean) => void
}

export const ConnectionStatus: React.FC<ConnectionStatusProps> = ({ onStatusChange }) => {
  const [isOnline, setIsOnline] = useState<boolean | null>(null)
  const [errorDetails, setErrorDetails] = useState<string>('')

  useEffect(() => {
    checkConnection()
  }, [])

  const checkConnection = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/ping`, {
        timeout: 5000
      })

      const online = response.status === 200 && response.data === 'pong'
      setIsOnline(online)
      setErrorDetails('')
      onStatusChange?.(online)
    } catch (err: any) {
      setIsOnline(false)

      if (err.code === 'ECONNREFUSED') {
        setErrorDetails(`Connection refused - Backend not running at ${API_BASE_URL}`)
      } else if (err.code === 'ETIMEDOUT') {
        setErrorDetails(`Connection timeout - Backend not responding`)
      } else if (err.code === 'ENOTFOUND') {
        setErrorDetails(`Host not found - Check API_BASE_URL in .env`)
      } else {
        setErrorDetails(`Error: ${err.message}`)
      }

      onStatusChange?.(false)
    }
  }

  if (isOnline === null) {
    return (
      <Box>
        <Text color="yellow">⚡ Checking connection...</Text>
      </Box>
    )
  }

  if (!isOnline) {
    return (
      <Box flexDirection="column" borderStyle="round" borderColor="red" padding={1}>
        <Text color="red" bold>
          ❌ Cannot connect to backend
        </Text>
        <Text color="red">{errorDetails}</Text>
        <Box marginTop={1}>
          <Text dimColor>
            Backend URL: {chalk.cyan(API_BASE_URL)}
          </Text>
        </Box>
      </Box>
    )
  }

  return (
    <Box>
      <Text color="green">✅ Connected to backend</Text>
    </Box>
  )
}
