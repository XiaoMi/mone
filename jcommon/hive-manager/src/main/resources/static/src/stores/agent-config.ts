import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface AgentConfig {
  owner: string
  workspacePath: string
  roleConfig: {
    workspacePath: string
    workspace: string
    __user_internal_name__: string
  }
  systemInfo: {
    delay: number
    mcpPath: string
    mcpServerList: string
    agentList: Record<string, string>
  }
  clientId: string
  roleMeta: {
    goal: string
    workflow: string
    profile: string
    roleType: string
    constraints: string
    outputFormat: string
  }
  roleState: string
  interrupted: boolean
}

export const useAgentConfigStore = defineStore('agentConfig', () => {
  const agentConfig = ref<AgentConfig | null>(null)
  const selectedAgentKey = ref<string>('')
  
  const agentList = computed(() => {
    return agentConfig.value?.systemInfo?.agentList || {}
  })
  
  const selectedAgentName = computed(() => {
    return agentList.value[selectedAgentKey.value] || ''
  })
  
  const setAgentConfig = (config: AgentConfig) => {
    agentConfig.value = config
    // 默认选择第一个agent
    const keys = Object.keys(config.systemInfo.agentList || {})
    if (keys.length > 0 && !selectedAgentKey.value) {
      selectedAgentKey.value = keys[0]
    }
  }
  
  const setSelectedAgent = (key: string) => {
    selectedAgentKey.value = key
  }
  
  const clearAgentConfig = () => {
    agentConfig.value = null
    selectedAgentKey.value = ''
  }
  
  return {
    agentConfig,
    selectedAgentKey,
    agentList,
    selectedAgentName,
    setAgentConfig,
    setSelectedAgent,
    clearAgentConfig
  }
})