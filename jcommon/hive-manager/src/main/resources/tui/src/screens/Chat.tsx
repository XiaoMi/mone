import React, { useState, useEffect, useRef } from 'react'
import { Box, Text, useInput } from 'ink'
import TextInput from 'ink-text-input'
import chalk from 'chalk'
import { Header } from '../components/Header'
import { Loading } from '../components/Loading'
import { ErrorMessage } from '../components/ErrorMessage'
import { useAppStore } from '../store'
import WebSocket from 'ws'
import { v4 as uuidv4 } from 'uuid'
import { WS_BASE_URL, API_BASE_URL } from '../utils/config'
import { logger } from '../utils/logger'

export const Chat: React.FC = () => {
  const [inputMessage, setInputMessage] = useState('')
  const [inputMode, setInputMode] = useState(false)
  const [connecting, setConnecting] = useState(true)
  const [error, setError] = useState('')
  const [wsStatus, setWsStatus] = useState<'connecting' | 'connected' | 'disconnected' | 'error'>('connecting')
  const [wsUrl, setWsUrl] = useState('')
  const wsRef = useRef<WebSocket | null>(null)
  const conversationIdRef = useRef(uuidv4())

  const { selectedAgent, selectedInstance, user, messages, addMessage, setScreen, clearMessages } = useAppStore()

  useInput((input, key) => {
    if (inputMode) {
      if (key.escape) {
        setInputMode(false)
        setInputMessage('')
      }
      if (key.return && inputMessage.trim()) {
        sendMessage(inputMessage)
        setInputMessage('')
        setInputMode(false)
      }
      return
    }

    if (input === 'q') {
      disconnectWebSocket()
      setScreen('agents')
    }
    if (input === 'i' || input === 'm') {
      setInputMode(true)
    }
    if (input === 'c') {
      clearMessages()
    }
    if (key.escape) {
      disconnectWebSocket()
      setScreen('agents')
    }
  })

  const connectWebSocket = () => {
    if (!selectedAgent || !selectedInstance) {
      setError('No agent or instance selected')
      setConnecting(false)
      return
    }

    // 构建 clientId (uuid) - 格式：name:group:version:ip:port
    // const clientId = `${selectedAgent.name}:${selectedAgent.group}:${selectedAgent.version}:${selectedInstance.ip}:${selectedInstance.port}`
    const clientId = uuidv4()

    // WebSocket URL - 参考前端实现：/api/manager/ws/agent/chat?clientId={uuid}
    const url = `${WS_BASE_URL}/ws/agent/chat?clientId=${clientId}`
    setWsUrl(url)

    logger.wsConnect(url)
    logger.info('Connecting to WebSocket', { clientId, url })

    try {
      const ws = new WebSocket(url)

      ws.on('open', () => {
        logger.info('WebSocket connection opened successfully')
        setConnecting(false)
        setWsStatus('connected')
        setError('')
        addMessage({
          type: 'md',
          author: {
            username: selectedAgent.name,
            cname: selectedAgent.name,
            avatar: ''
          },
          meta: { role: 'ASSISTANT' },
          data: {
            text: `Connected to ${selectedAgent.name}. How can I help you?`
          }
        })
      })

      ws.on('message', (data: Buffer) => {
        const message = data.toString()
        logger.wsMessage('receive', message.substring(0, 200))
        handleWebSocketMessage(message)
      })

      ws.on('error', (err: any) => {
        logger.error('WebSocket error occurred', {
          message: err.message,
          code: err.code,
          type: err.type,
          url,
          clientId,
          agent: selectedAgent.name,
          instance: `${selectedInstance.ip}:${selectedInstance.port}`
        })

        let errorMessage = 'WebSocket connection failed\n\n'

        if (err.code === 'ECONNREFUSED') {
          errorMessage += `${chalk.red('●')} Connection Refused\n` +
                         `${chalk.yellow('URL:')} ${url}\n` +
                         `${chalk.yellow('Cause:')} Backend WebSocket server not accessible\n` +
                         `${chalk.yellow('Check:')}\n` +
                         `  • Is backend running on port 8080?\n` +
                         `  • Is agent instance ${selectedInstance.ip}:${selectedInstance.port} active?\n` +
                         `  • Try: curl http://localhost:8080/ping`
        } else if (err.code === 'ENOTFOUND') {
          errorMessage += `${chalk.red('●')} Host Not Found\n` +
                         `${chalk.yellow('URL:')} ${url}\n` +
                         `${chalk.yellow('Cause:')} Cannot resolve hostname\n` +
                         `${chalk.yellow('Check:')} Verify WS_BASE_URL in .env`
        } else if (err.code === 'ETIMEDOUT') {
          errorMessage += `${chalk.red('●')} Connection Timeout\n` +
                         `${chalk.yellow('URL:')} ${url}\n` +
                         `${chalk.yellow('Cause:')} Server not responding\n` +
                         `${chalk.yellow('Check:')} Network connectivity or firewall`
        } else if (err.message?.includes('401') || err.message?.includes('Unauthorized')) {
          errorMessage += `${chalk.red('●')} Unauthorized\n` +
                         `${chalk.yellow('Cause:')} Invalid or expired token\n` +
                         `${chalk.yellow('Action:')} Try re-login (press Esc then login again)`
        } else if (err.message?.includes('404')) {
          errorMessage += `${chalk.red('●')} WebSocket Endpoint Not Found\n` +
                         `${chalk.yellow('URL:')} ${url}\n` +
                         `${chalk.yellow('Cause:')} Backend WebSocket handler not registered\n` +
                         `${chalk.yellow('Expected:')} /api/manager/ws/agent/chat`
        } else {
          errorMessage += `${chalk.red('●')} Unknown Error\n` +
                         `${chalk.yellow('Message:')} ${err.message}\n` +
                         `${chalk.yellow('Code:')} ${err.code || 'N/A'}\n` +
                         `${chalk.yellow('Type:')} ${err.type || 'N/A'}\n` +
                         `${chalk.yellow('URL:')} ${url}\n` +
                         `${chalk.yellow('ClientId:')} ${clientId}`
        }

        setError(errorMessage)
        setWsStatus('error')
        setConnecting(false)
      })

      ws.on('close', (code: number, reason: Buffer) => {
        const reasonStr = reason.toString() || 'No reason provided'
        logger.wsDisconnect(url, `Code: ${code}, Reason: ${reasonStr}`)

        setWsStatus('disconnected')
        setConnecting(false)

        if (code !== 1000) { // 1000 = normal closure
          const errorMessage = `${chalk.yellow('WebSocket Closed Unexpectedly')}\n` +
                              `${chalk.gray('Code:')} ${code}\n` +
                              `${chalk.gray('Reason:')} ${reasonStr}\n` +
                              `${chalk.gray('URL:')} ${url}`
          setError(errorMessage)
        }
      })

      wsRef.current = ws
    } catch (err: any) {
      logger.error('Failed to create WebSocket', err)

      const errorMessage = `${chalk.red('Failed to Create WebSocket')}\n` +
                          `${chalk.yellow('Error:')} ${err.message}\n` +
                          `${chalk.yellow('URL:')} ${url}\n` +
                          `${chalk.yellow('Agent:')} ${selectedAgent.name}\n` +
                          `${chalk.yellow('Instance:')} ${selectedInstance.ip}:${selectedInstance.port}\n` +
                          `${chalk.yellow('Stack:')} ${err.stack?.split('\n')[0] || 'N/A'}`

      setError(errorMessage)
      setWsStatus('error')
      setConnecting(false)
    }
  }

  const disconnectWebSocket = () => {
    if (wsRef.current) {
      wsRef.current.close()
      wsRef.current = null
    }
  }

  const handleWebSocketMessage = (data: string) => {
    // Parse the WebSocket message and add it to the message list
    try {
      // Simple parsing - in real implementation, you'd handle the full message format
      if (!selectedAgent) return

      addMessage({
        type: 'md',
        author: {
          username: selectedAgent.name,
          cname: selectedAgent.name,
          avatar: ''
        },
        meta: { role: 'ASSISTANT' },
        data: { text: data }
      })
    } catch (err) {
      console.error('Failed to parse message:', err)
    }
  }

  const sendMessage = (text: string) => {
    if (!wsRef.current || !selectedAgent || !selectedInstance || !user) {
      logger.warn('Cannot send message: missing WebSocket or user data')
      return
    }

    // Check WebSocket state
    if (wsRef.current.readyState !== WebSocket.OPEN) {
      setError(`Cannot send message: WebSocket is ${wsRef.current.readyState === WebSocket.CONNECTING ? 'still connecting' : 'not connected'}`)
      logger.warn('WebSocket not ready', { readyState: wsRef.current.readyState })
      return
    }

    // Add user message to UI
    addMessage({
      type: 'md',
      author: {
        username: user.username,
        cname: user.cname,
        avatar: ''
      },
      meta: { role: 'USER' },
      data: { text }
    })

    // Send to WebSocket
    const payload = {
      mapData: {
        outerTag: 'use_mcp_tool',
        server_name: `${selectedAgent.name}:${selectedAgent.group}:${selectedAgent.version}:${selectedInstance.ip}:${selectedInstance.port}`,
        tool_name: `stream_${selectedAgent.name}_chat`,
        arguments: JSON.stringify({
          message: text,
          __owner_id__: user.username
        })
      },
      agentId: selectedAgent.id,
      agentInstance: selectedInstance,
      conversationId: conversationIdRef.current
    }

    try {
      wsRef.current.send(JSON.stringify(payload))
      logger.wsMessage('send', { message: text, payload })
    } catch (err: any) {
      logger.error('Failed to send WebSocket message', err)
      setError(`Failed to send message: ${err.message}`)
    }
  }

  useEffect(() => {
    connectWebSocket()
    return () => {
      disconnectWebSocket()
    }
  }, [])

  if (connecting) {
    return (
      <Box flexDirection="column">
        <Header title="CHAT" />
        <Loading text="Connecting to agent..." />
      </Box>
    )
  }

  if (!selectedAgent || !selectedInstance) {
    return (
      <Box flexDirection="column">
        <Header title="CHAT" />
        <ErrorMessage message="No agent or instance selected" />
      </Box>
    )
  }

  const getStatusColor = () => {
    switch (wsStatus) {
      case 'connected': return 'green'
      case 'connecting': return 'yellow'
      case 'disconnected': return 'gray'
      case 'error': return 'red'
      default: return 'white'
    }
  }

  const getStatusIcon = () => {
    switch (wsStatus) {
      case 'connected': return '●'
      case 'connecting': return '◐'
      case 'disconnected': return '○'
      case 'error': return '✗'
      default: return '?'
    }
  }

  return (
    <Box flexDirection="column" padding={1}>
      <Header title="CHAT" />

      <Box
        borderStyle="round"
        borderColor="cyan"
        padding={1}
        marginBottom={1}
        justifyContent="space-between"
      >
        <Box>
          <Text color={getStatusColor()}>
            {getStatusIcon()} {wsStatus.toUpperCase()}
          </Text>
          <Text> | Agent: {chalk.green(selectedAgent.name)}</Text>
          <Text dimColor> ({selectedInstance.ip}:{selectedInstance.port})</Text>
        </Box>
        <Text>
          {chalk.yellow('[I]')} Input | {chalk.yellow('[C]')} Clear | {chalk.yellow('[Esc]')} Back
        </Text>
      </Box>

      {error && (
        <Box marginBottom={1} flexDirection="column">
          <ErrorMessage message={error} />
          <Box marginTop={1} borderStyle="round" borderColor="yellow" padding={1}>
            <Text color="yellow" dimColor>
              💡 Debug Info:{'\n'}
              • WS URL: {chalk.cyan(wsUrl)}{'\n'}
              • API Base: {chalk.cyan(API_BASE_URL)}{'\n'}
              • WS Base: {chalk.cyan(WS_BASE_URL)}{'\n'}
              • Check logs: npm run logs:tail
            </Text>
          </Box>
        </Box>
      )}

      <Box
        borderStyle="round"
        borderColor="blue"
        flexDirection="column"
        padding={1}
        height={20}
        overflow="hidden"
      >
        {messages.length === 0 ? (
          <Text dimColor>No messages yet. Press 'I' to start chatting...</Text>
        ) : (
          messages.slice(-15).map((msg, idx) => (
            <Box key={idx} flexDirection="column" marginBottom={1}>
              <Text bold color={msg.meta.role === 'USER' ? 'green' : 'cyan'}>
                {msg.author.cname}:
              </Text>
              <Text>{msg.data.text || msg.data.content}</Text>
            </Box>
          ))
        )}
      </Box>

      {inputMode && (
        <Box
          marginTop={1}
          borderStyle="round"
          borderColor="green"
          padding={1}
        >
          <Text bold>Message: </Text>
          <TextInput
            value={inputMessage}
            onChange={setInputMessage}
            placeholder="Type your message..."
          />
          <Text dimColor> (Enter to send, Esc to cancel)</Text>
        </Box>
      )}
    </Box>
  )
}
