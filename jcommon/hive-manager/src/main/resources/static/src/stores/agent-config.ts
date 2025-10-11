import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface AgentConfig {
  owner: string
  workspacePath: string
  roleConfig: {
    workspacePath: string
    workspace: string
    llm: string
    __user_internal_name__: string
  } & Record<string, string>
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
  llmOptions?: Record<string, string>
  interrupted: boolean
}

export const useAgentConfigStore = defineStore('agentConfig', () => {
  const agentConfig = ref<AgentConfig | null>(null)
  const selectedAgentKey = ref<string>('')
  const selectedLlmValue = ref<string>('')
  
  const agentList = computed(() => {
    return agentConfig.value?.systemInfo?.agentList || {}
  })
  
  const selectedAgentName = computed(() => {
    return agentList.value[selectedAgentKey.value] || ''
  })
  
  const llmOptions = computed(() => {
    return agentConfig.value?.llmOptions || {}
  })
  
  const selectedLlmName = computed(() => {
    return llmOptions.value[selectedLlmValue.value] || ''
  })
  
  const setAgentConfig = (config: AgentConfig) => {
    agentConfig.value = config
    // 默认选择第一个agent
    const agentKeys = Object.keys(config.systemInfo.agentList || {})
    if (agentKeys.length > 0 && !selectedAgentKey.value) {
      selectedAgentKey.value = config.roleConfig?.__agent_config_key__ || agentKeys[0]
    }
    // 默认选择第一个LLM模型
    const llmKeys = Object.keys(config.llmOptions || {})
    if (llmKeys.length > 0 && !selectedLlmValue.value) {
      selectedLlmValue.value = config.roleConfig?.llm || config.llmOptions?.[llmKeys[0]] as string || ''
    }
  }
  
  const setSelectedAgent = (key: string) => {
    selectedAgentKey.value = key
  }
  
  const setSelectedLlm = (key: string) => {
    selectedLlmValue.value = key
  }
  
  const clearAgentConfig = () => {
    agentConfig.value = null
    selectedAgentKey.value = ''
    selectedLlmValue.value = ''
  }
  
  return {
    agentConfig,
    selectedAgentKey,
    selectedLlmValue,
    agentList,
    llmOptions,
    selectedAgentName,
    selectedLlmName,
    setAgentConfig,
    setSelectedAgent,
    setSelectedLlm,
    clearAgentConfig
  }
})