import React, { useState, useEffect } from 'react'
import { Box, Text, useInput } from 'ink'
import SelectInput from 'ink-select-input'
import TextInput from 'ink-text-input'
import chalk from 'chalk'
import { Header } from '../components/Header'
import { Loading } from '../components/Loading'
import { ErrorMessage } from '../components/ErrorMessage'
import { StatusBadge } from '../components/StatusBadge'
import { useAppStore } from '../store'
import { getAgentList } from '../api/agent'
import type { AgentListItem } from '../types'
import { formatDate, truncate } from '../utils/format'

export const AgentList: React.FC = () => {
  const [agents, setAgents] = useState<AgentListItem[]>([])
  const [filteredAgents, setFilteredAgents] = useState<AgentListItem[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [searchQuery, setSearchQuery] = useState('')
  const [searchMode, setSearchMode] = useState(false)
  const [filterType, setFilterType] = useState<'all' | 'favorite'>('all')

  const { setScreen, setAgent, setInstance } = useAppStore()

  useInput((input, key) => {
    if (searchMode) {
      if (key.escape) {
        setSearchMode(false)
      }
      return
    }

    if (input === 'q') {
      process.exit(0)
    }
    if (input === 's') {
      setSearchMode(true)
    }
    if (input === 'f') {
      setFilterType(filterType === 'all' ? 'favorite' : 'all')
    }
    if (input === 'r') {
      fetchAgents()
    }
    if (key.escape) {
      setScreen('agents')
    }
  })

  const fetchAgents = async () => {
    setLoading(true)
    setError('')
    try {
      const response = await getAgentList(searchQuery, filterType === 'favorite')
      if (response.data.code === 200) {
        setAgents(response.data.data || [])
      } else {
        setError(response.data.message || 'Failed to fetch agents')
      }
    } catch (err: any) {
      setError(err.message || 'Network error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAgents()
  }, [filterType, searchQuery])

  useEffect(() => {
    setFilteredAgents(agents)
  }, [agents])

  const handleSelect = (item: any) => {
    const agentItem = agents.find((a) => a.agent.id === item.value)
    if (agentItem) {
      if (agentItem.instances && agentItem.instances.length > 0) {
        setAgent(agentItem.agent)
        setInstance(agentItem.instances[0])
        setScreen('chat')
      } else {
        setError('Agent has no running instances')
      }
    }
  }

  const items = filteredAgents.map((item) => ({
    label: `${chalk.cyan(item.agent.name)} ${item.isFavorite ? chalk.yellow('★') : ''} - ${truncate(item.agent.description, 40)} [${item.instances.length > 0 ? chalk.green('RUNNING') : chalk.gray('STOPPED')}]`,
    value: item.agent.id,
  }))

  if (loading) {
    return (
      <Box flexDirection="column">
        <Header title="AGENTS" />
        <Loading text="Loading agents..." />
      </Box>
    )
  }

  return (
    <Box flexDirection="column" padding={1}>
      <Header title="AGENTS" />

      <Box
        borderStyle="round"
        borderColor="cyan"
        flexDirection="column"
        padding={1}
        marginBottom={1}
      >
        <Box justifyContent="space-between">
          <Text>
            Total: {chalk.green(agents.length)} | Filter: {chalk.yellow(filterType.toUpperCase())}
          </Text>
          <Text>
            {chalk.yellow('[S]')} Search | {chalk.yellow('[F]')} Filter | {chalk.yellow('[R]')} Refresh | {chalk.yellow('[Q]')} Quit
          </Text>
        </Box>

        {searchMode && (
          <Box marginTop={1}>
            <Text bold>Search: </Text>
            <TextInput
              value={searchQuery}
              onChange={setSearchQuery}
              placeholder="Type to search..."
            />
            <Text dimColor> (Press ESC to cancel)</Text>
          </Box>
        )}
      </Box>

      {error && (
        <Box marginBottom={1}>
          <ErrorMessage message={error} />
        </Box>
      )}

      {items.length === 0 ? (
        <Box
          borderStyle="round"
          borderColor="yellow"
          padding={2}
          justifyContent="center"
        >
          <Text color="yellow">No agents found</Text>
        </Box>
      ) : (
        <Box
          borderStyle="round"
          borderColor="cyan"
          flexDirection="column"
          paddingX={1}
        >
          <SelectInput items={items} onSelect={handleSelect} />
        </Box>
      )}

      <Box marginTop={1} borderStyle="round" borderColor="blue" padding={1}>
        <Text dimColor>
          Use {chalk.yellow('↑/↓')} arrows to navigate, {chalk.yellow('Enter')} to select agent for chat
        </Text>
      </Box>
    </Box>
  )
}
