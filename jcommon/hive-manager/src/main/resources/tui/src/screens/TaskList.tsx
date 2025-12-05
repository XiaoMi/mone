import React, { useState, useEffect } from 'react'
import { Box, Text, useInput } from 'ink'
import SelectInput from 'ink-select-input'
import chalk from 'chalk'
import { Header } from '../components/Header'
import { Loading } from '../components/Loading'
import { ErrorMessage } from '../components/ErrorMessage'
import { StatusBadge } from '../components/StatusBadge'
import { useAppStore } from '../store'
import { getTaskList, executeTask } from '../api/task'
import type { Task } from '../types'
import { formatDate, truncate } from '../utils/format'

export const TaskList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [executingId, setExecutingId] = useState<string | null>(null)

  const { setScreen } = useAppStore()

  useInput((input, key) => {
    if (input === 'q') {
      process.exit(0)
    }
    if (input === 'r') {
      fetchTasks()
    }
    if (key.escape) {
      setScreen('agents')
    }
  })

  const fetchTasks = async () => {
    setLoading(true)
    setError('')
    try {
      const response = await getTaskList()
      if (response.data.code === 200) {
        setTasks(response.data.data || [])
      } else {
        setError(response.data.message || 'Failed to fetch tasks')
      }
    } catch (err: any) {
      setError(err.message || 'Network error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchTasks()
  }, [])

  const handleExecute = async (taskUuid: string) => {
    const task = tasks.find((t) => t.taskUuid === taskUuid)
    if (!task) return

    setExecutingId(taskUuid)
    setError('')

    try {
      const response = await executeTask({
        id: taskUuid,
        metadata: {
          input: task.description,
          serverAgentId: task.serverAgentId
        }
      })

      if (response.data.code === 200) {
        // Refresh task list
        await fetchTasks()
      } else {
        setError(response.data.message || 'Failed to execute task')
      }
    } catch (err: any) {
      setError(err.message || 'Network error')
    } finally {
      setExecutingId(null)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'running':
        return 'green'
      case 'completed':
        return 'blue'
      case 'failed':
        return 'red'
      default:
        return 'yellow'
    }
  }

  const items = tasks.map((task) => ({
    label: `${chalk.cyan(task.title)} - ${truncate(task.description, 35)} [${chalk[getStatusColor(task.status)](task.status.toUpperCase())}]`,
    value: task.taskUuid,
  }))

  if (loading) {
    return (
      <Box flexDirection="column">
        <Header title="TASKS" />
        <Loading text="Loading tasks..." />
      </Box>
    )
  }

  return (
    <Box flexDirection="column" padding={1}>
      <Header title="TASKS" />

      <Box
        borderStyle="round"
        borderColor="cyan"
        flexDirection="column"
        padding={1}
        marginBottom={1}
      >
        <Box justifyContent="space-between">
          <Text>
            Total Tasks: {chalk.green(tasks.length)}
          </Text>
          <Text>
            {chalk.yellow('[R]')} Refresh | {chalk.yellow('[Esc]')} Back | {chalk.yellow('[Q]')} Quit
          </Text>
        </Box>
      </Box>

      {error && (
        <Box marginBottom={1}>
          <ErrorMessage message={error} />
        </Box>
      )}

      {executingId && (
        <Box marginBottom={1}>
          <Loading text="Executing task..." />
        </Box>
      )}

      {items.length === 0 ? (
        <Box
          borderStyle="round"
          borderColor="yellow"
          padding={2}
          justifyContent="center"
        >
          <Text color="yellow">No tasks found</Text>
        </Box>
      ) : (
        <Box
          borderStyle="round"
          borderColor="cyan"
          flexDirection="column"
          paddingX={1}
        >
          <SelectInput
            items={items}
            onSelect={(item) => handleExecute(item.value)}
          />
        </Box>
      )}

      <Box marginTop={1} borderStyle="round" borderColor="blue" padding={1}>
        <Text dimColor>
          Use {chalk.yellow('↑/↓')} arrows to navigate, {chalk.yellow('Enter')} to execute task
        </Text>
      </Box>
    </Box>
  )
}
