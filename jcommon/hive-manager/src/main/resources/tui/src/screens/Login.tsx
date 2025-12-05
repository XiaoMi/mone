import React, { useState } from 'react'
import { Box, Text, useInput } from 'ink'
import TextInput from 'ink-text-input'
import chalk from 'chalk'
import { Header } from '../components/Header'
import { Loading } from '../components/Loading'
import { ErrorMessage } from '../components/ErrorMessage'
import { ConnectionStatus } from '../components/ConnectionStatus'
import { useAppStore } from '../store'
import { login } from '../api/auth'
import { http } from '../api/request'

export const Login: React.FC = () => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [currentField, setCurrentField] = useState<'username' | 'password'>('username')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [showConnectionStatus, setShowConnectionStatus] = useState(false)

  const { setUser, setToken, setScreen } = useAppStore()

  useInput((input, key) => {
    if (loading) return

    if (key.return) {
      if (currentField === 'username' && username) {
        setCurrentField('password')
      } else if (currentField === 'password' && password) {
        handleLogin()
      }
    }

    if (key.tab) {
      setCurrentField(currentField === 'username' ? 'password' : 'username')
    }

    if (input === 't') {
      setShowConnectionStatus(!showConnectionStatus)
    }
  })

  const handleLogin = async () => {
    if (!username || !password) {
      setError('Please enter both username and password')
      return
    }

    setLoading(true)
    setError('')

    try {
      const response = await login(username, password)
      if (response.data.code === 200 && response.data.data) {
        const { user, token } = response.data.data
        setUser(user)
        setToken(token)
        http.setToken(token)
        setScreen('agents')
      } else {
        setError(response.data.message || 'Login failed')
      }
    } catch (err: any) {
      // 详细的错误信息
      let errorMessage = 'Network error'

      if (err.response) {
        // 服务器响应了错误
        errorMessage = `Server Error (${err.response.status}): ${err.response.data?.message || err.response.statusText}`
      } else if (err.request) {
        // 请求已发送但没有收到响应
        errorMessage = `Connection Error: Cannot reach server at ${err.config?.baseURL || 'backend'}`
      } else {
        // 其他错误
        errorMessage = `Error: ${err.message}`
      }

      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <Box flexDirection="column">
        <Header title="LOGIN" showUser={false} />
        <Loading text="Logging in..." />
      </Box>
    )
  }

  return (
    <Box flexDirection="column" padding={1}>
      <Header title="LOGIN" showUser={false} />

      <Box
        flexDirection="column"
        borderStyle="round"
        borderColor="cyan"
        padding={1}
        marginTop={1}
      >
        <Text color="cyan" bold>
          ╔═══ Welcome to Hive Manager TUI ═══╗
        </Text>
        <Box marginTop={1} marginBottom={1}>
          <Text>Please enter your credentials to continue</Text>
        </Box>

        <Box marginTop={1}>
          <Box width={12}>
            <Text bold color={currentField === 'username' ? 'green' : 'white'}>
              Username:
            </Text>
          </Box>
          {currentField === 'username' ? (
            <TextInput
              value={username}
              onChange={setUsername}
              placeholder="Enter username"
            />
          ) : (
            <Text color="gray">{username || '(empty)'}</Text>
          )}
        </Box>

        <Box marginTop={1}>
          <Box width={12}>
            <Text bold color={currentField === 'password' ? 'green' : 'white'}>
              Password:
            </Text>
          </Box>
          {currentField === 'password' ? (
            <TextInput
              value={password}
              onChange={setPassword}
              mask="*"
              placeholder="Enter password"
            />
          ) : (
            <Text color="gray">{'*'.repeat(password.length) || '(empty)'}</Text>
          )}
        </Box>

        <Box marginTop={2} flexDirection="column">
          <Text>
            {chalk.yellow('[Tab]')} Switch field | {chalk.yellow('[Enter]')} Submit | {chalk.yellow('[T]')} Test Connection
          </Text>
        </Box>
      </Box>

      {showConnectionStatus && (
        <Box marginTop={1}>
          <ConnectionStatus />
        </Box>
      )}

      {error && (
        <Box marginTop={1} flexDirection="column">
          <ErrorMessage message={error} />
          <Box marginTop={1} borderStyle="round" borderColor="yellow" padding={1}>
            <Text color="yellow" dimColor>
              💡 Troubleshooting:{'\n'}
              • Check backend is running{'\n'}
              • Verify .env settings: {chalk.cyan(process.env.API_BASE_URL || 'http://localhost:8080')}{'\n'}
              • Test: curl {process.env.API_BASE_URL || 'http://localhost:8080'}/ping
            </Text>
          </Box>
        </Box>
      )}
    </Box>
  )
}
