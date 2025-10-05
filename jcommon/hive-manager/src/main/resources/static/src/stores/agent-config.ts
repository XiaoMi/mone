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
  llmOptions?: Record<string, string>
  interrupted: boolean
}

export const useAgentConfigStore = defineStore('agentConfig', () => {
  const agentConfig = ref<AgentConfig | null>(null)
  const selectedAgentKey = ref<string>('')
  const selectedLlmKey = ref<string>('')
  
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
    return llmOptions.value[selectedLlmKey.value] || ''
  })
  
  const setAgentConfig = (config: AgentConfig) => {
    agentConfig.value = config
    // 默认选择第一个agent
    const agentKeys = Object.keys(config.systemInfo.agentList || {})
    if (agentKeys.length > 0 && !selectedAgentKey.value) {
      selectedAgentKey.value = agentKeys[0]
    }
    // 默认选择第一个LLM模型
    const llmKeys = Object.keys(config.llmOptions || {})
    if (llmKeys.length > 0 && !selectedLlmKey.value) {
      selectedLlmKey.value = "qwen"// || llmKeys[0]
    }
  }
  
  const setSelectedAgent = (key: string) => {
    selectedAgentKey.value = key
  }
  
  const setSelectedLlm = (key: string) => {
    selectedLlmKey.value = key
  }
  
  const clearAgentConfig = () => {
    agentConfig.value = null
    selectedAgentKey.value = ''
    selectedLlmKey.value = ''
  }
  
  return {
    agentConfig,
    selectedAgentKey,
    selectedLlmKey,
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