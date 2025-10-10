<template>
  <div class="container">
    <div class="instance-select">
      <div class="selector-group">
        <el-select
          v-model="selectedIp"
          placeholder="请选择实例IP"
          class="ip-select"
          popper-class="instance-select-popper"
        >
          <el-option
            v-for="item in list"
            :key="item.id"
            :label="`${item.ip}:${item.port}`"
            :value="item.ip"
          />
        </el-select>

        <!-- Agent选择器 -->
        <el-select
          v-if="Object.keys(agentList).length > 0"
          v-model="selectedAgentKey"
          placeholder="请选择Agent"
          class="agent-select"
          popper-class="instance-select-popper"
          @change="handleAgentChange"
        >
          <el-option
            v-for="(name, key) in agentList"
            :key="key"
            :label="name"
            :value="key"
          />
        </el-select>

        <!-- LLM模型选择器 -->
        <el-select
          v-if="Object.keys(llmOptions).length > 0"
          v-model="selectedLlmValue"
          placeholder="请选择模型"
          class="llm-select"
          popper-class="instance-select-popper"
          @change="handleLlmChange"
        >
          <el-option
            v-for="(name, key) in llmOptions"
            :key="key"
            :label="key"
            :value="name"
          />
        </el-select>
      </div>
      <div class="right-btns">
        <el-tooltip class="instance-select-tooltip" effect="dark" content="配置" placement="top">
          <el-icon size="14px" color="var(--el-color-primary)" @click="handleOpenConfig"
            ><Setting
          /></el-icon>
        </el-tooltip>
        <McpManager v-if="onExecuteMcpCommand" :onExecuteMcpCommand="onExecuteMcpCommand" />
        <el-tooltip
          class="instance-select-tooltip"
          effect="dark"
          content="终止对话"
          placement="top"
        >
          <el-icon size="16px" color="var(--el-color-warning)" @click="handleStopMsg">
            <svg
              t="1758526773421"
              class="icon"
              viewBox="0 0 1024 1024"
              version="1.1"
              xmlns="http://www.w3.org/2000/svg"
              p-id="2595"
              width="16"
              height="16"
            >
              <path
                d="M783.058824 602.352941a210.823529 210.823529 0 1 1 0 421.647059 210.823529 210.823529 0 0 1 0-421.647059z m-121.072942 139.444706a140.528941 140.528941 0 0 0 192.451765 192.451765l-192.512-192.451765z m121.072942-69.150118c-26.081882 0-50.477176 7.047529-71.378824 19.395765l192.512 192.512a140.528941 140.528941 0 0 0-121.072941-211.907765zM451.764706 60.235294c232.869647 0 421.647059 161.792 421.647059 361.411765 0 44.995765-9.637647 88.124235-27.105883 127.879529a271.058824 271.058824 0 0 0-332.137411 229.616941 485.857882 485.857882 0 0 1-193.656471-13.854117l-132.999529 79.570823a60.235294 60.235294 0 0 1-91.136-54.512941l0.783058-6.987294 23.070118-138.420706C63.789176 583.499294 30.117647 505.976471 30.117647 421.647059c0-199.619765 188.777412-361.411765 421.647059-361.411765zM271.058824 361.411765a60.235294 60.235294 0 1 0 0 120.470588 60.235294 60.235294 0 0 0 0-120.470588z m180.705882 0a60.235294 60.235294 0 1 0 0 120.470588 60.235294 60.235294 0 0 0 0-120.470588z m180.705882 0a60.235294 60.235294 0 1 0 0 120.470588 60.235294 60.235294 0 0 0 0-120.470588z"
                fill="currentColor"
                p-id="2596"
              ></path>
            </svg>
          </el-icon>
        </el-tooltip>

        <el-tooltip
          class="instance-select-tooltip"
          effect="dark"
          content="清除历史记录"
          placement="top"
        >
          <el-icon size="14px" color="var(--el-color-warning)" @click="handleClearHistory"
            ><Delete
          /></el-icon>
        </el-tooltip>
        <el-icon size="16px" color="var(--el-color-danger)" @click="confirmOffline"
          ><SwitchButton
        /></el-icon>
      </div>

      <!-- 配置对话框 -->
      <el-dialog
        v-model="configDialogVisible"
        title="实例配置"
        width="80%"
        :close-on-click-modal="false"
      >
        <div class="config-list">
          <div v-for="(item, index) in configList" :key="index" class="config-item">
            <el-input v-model="item.key" placeholder="键" />
            <el-input v-model="item.value" placeholder="值" />
            <el-button type="danger" circle @click="removeConfig(index)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
        <el-button type="primary" @click="addConfig">添加配置</el-button>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="configDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmitConfig">确认</el-button>
          </span>
        </template>
      </el-dialog>


    </div>
    <TokenUsage :used-tokens="calToken" :total-tokens="tokenUsage.totalTokens" />
  </div>
</template>

<script setup lang="ts">
import TokenUsage from '@/components/Chat/components/tokenUsage/index.vue'
import { useUserStore } from '@/stores/user'
import { computed, ref, watch, watchEffect } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useChatContextStore } from '@/stores/chat-context'
import { Setting, Delete } from '@element-plus/icons-vue'
import {
  getAgentConfigs,
  setBatchAgentConfig,
  deleteAgentConfig,
} from '@/api/agent'
import { useAgentConfigStore } from '@/stores/agent-config'
import McpManager from './McpManager.vue'

// Components
defineOptions({
  components: {
    McpManager,
  },
})

// 定义实例接口
interface Instance {
  id: string
  ip: string
  port: number
  agentId?: string
}

const { getInstance, setSelectedInstance } = useUserStore()
const { setMessageList, tokenUsage, resetTokenUsage } = useChatContextStore()
const agentConfigStore = useAgentConfigStore()

const selectedAgentKey = computed({
  get() {
    return agentConfigStore.selectedAgentKey
  },
  set(value) {
    agentConfigStore.setSelectedAgent(value)
  }
})

const agentList = computed(() => {
  const list = agentConfigStore.agentList
  console.log('agentList computed:', list)
  return list
})

const llmOptions = computed(() => {
  const options = agentConfigStore.llmOptions
  console.log('llmOptions computed:', options)
  return options || {}
})

const selectedLlmValue = computed({
  get() {
    const options = llmOptions.value || {}
    const selectedKey = agentConfigStore.selectedLlmKey
    return (selectedKey && options[selectedKey]) || ''
  },
  set(value: string) {
    const options = llmOptions.value || {}
    const matchedEntry = Object.entries(options).find(([, optionValue]) => optionValue === value)
    if (matchedEntry) {
      agentConfigStore.setSelectedLlm(matchedEntry[0])
    } else if (!value) {
      agentConfigStore.setSelectedLlm('')
    }
  }
})

const selectedIp = ref('')
const  calToken = computed(() => {
  if (tokenUsage.inputTokens + tokenUsage.outputTokens - tokenUsage.compressedTokens <= 0) return 0
  return tokenUsage.inputTokens + tokenUsage.outputTokens - tokenUsage.compressedTokens
})
const props = defineProps({
  onClearHistory: {
    type: Function,
    required: true,
  },
  onOffline: {
    type: Function,
    required: true,
  },
  onStopMsg: {
    type: Function,
    required: true,
  },
  onSwitchAgent: {
    type: Function,
    required: false,
  },
  onSwitchLlm: {
    type: Function,
    required: false,
  },
  onExecuteMcpCommand: {
    type: Function,
    required: false,
  },
  onExecuteSystemCommand: {
    type: Function,
    required: false,
  },
})
const list = computed(() => {
  return getInstance()
})

watch(
  () => selectedIp.value,
  (newIp) => {
    setSelectedInstance(list.value?.find((item: Instance) => item.ip === newIp))
  }
)

// 添加 watchEffect 来设置默认值
watchEffect(() => {
  if (list.value && list.value.length > 0 && !selectedIp.value) {
    selectedIp.value = list.value[0].ip
  }
})

const confirmOffline = () => {
  ElMessageBox.confirm('确定要下线该实例吗？', '确认下线', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      setMessageList([])
      props.onOffline?.()
    })
    .catch(() => {
      // 用户取消操作
    })
}

const handleStopMsg = () => {
  props.onStopMsg?.()
}

const handleClearHistory = () => {
  setMessageList([])
  resetTokenUsage()
  props.onClearHistory?.()
}



// 配置相关
const configDialogVisible = ref(false)
const configList = ref<Array<{ key: string; value: string }>>([])
const loading = ref(false)

const handleOpenConfig = async () => {
  const selectedInstance = getInstance()?.find((item: Instance) => item.ip === selectedIp.value)
  if (!selectedInstance?.agentId) {
    ElMessage.error('未找到当前实例对应的Agent')
    return
  }

  loading.value = true
  try {
    const response = await getAgentConfigs(selectedInstance.agentId)
    const apiConfigs = response.data?.data || []
    
    // 从 roleConfig 获取的配置
    const roleConfig = agentConfigStore.agentConfig?.roleConfig || {}
    
    // 合并配置：优先显示 API 配置，然后添加 roleConfig 中的配置
    const mergedConfigs = new Map()
    
    // 先添加 API 配置
    apiConfigs.forEach(config => {
      mergedConfigs.set(config.key, config.value)
    })
    
    // 再添加 roleConfig 中不存在于 API 配置的项
    Object.entries(roleConfig).forEach(([key, value]) => {
      if (!mergedConfigs.has(key) && value !== undefined && value !== null) {
        mergedConfigs.set(key, String(value))
      }
    })
    
    // 转换为配置列表
    configList.value = Array.from(mergedConfigs.entries()).map(([key, value]) => ({
      key,
      value,
    }))
    
    configDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取配置失败')
    console.error('获取配置失败:', error)
  } finally {
    loading.value = false
  }
}

const addConfig = () => {
  configList.value.push({
    key: '',
    value: '',
  })
}

const removeConfig = async (index: number) => {
  const config = configList.value[index]
  try {
    await ElMessageBox.confirm(`确定要删除配置 "${config.key}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const selectedInstance = getInstance()?.find((item: Instance) => item.ip === selectedIp.value)
    if (!selectedInstance?.agentId) {
      ElMessage.error('未找到当前实例对应的Agent')
      return
    }

    loading.value = true
    try {
      await deleteAgentConfig(selectedInstance.agentId, config.key)
      configList.value.splice(index, 1)
      ElMessage.success('配置删除成功')
    } catch (error) {
      ElMessage.error('配置删除失败')
      console.error('配置删除失败:', error)
    } finally {
      loading.value = false
    }
  } catch {
    // 用户取消删除操作
  }
}

const handleSubmitConfig = async () => {
  // 验证配置是否完整
  if (configList.value.some((item) => !item.key || !item.value)) {
    ElMessage.warning('请填写完整的配置信息')
    return
  }

  const selectedInstance = getInstance()?.find((item: Instance) => item.ip === selectedIp.value)
  if (!selectedInstance?.agentId) {
    ElMessage.error('未找到当前实例对应的Agent')
    return
  }

  // 转换配置格式
  const config = configList.value.reduce((acc, curr) => {
    acc[curr.key] = curr.value
    return acc
  }, {} as Record<string, string>)

  loading.value = true
  try {
    await setBatchAgentConfig(selectedInstance.agentId, config)
    ElMessage.success('配置更新成功')
    configDialogVisible.value = false
    
    // 配置更新成功后执行 /refresh 命令
    await sendRefreshCommand()
  } catch (error) {
    ElMessage.error('配置更新失败')
    console.error('配置更新失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAgentChange = (agentKey: string) => {
  console.log('Agent changed to:', agentKey)
  agentConfigStore.setSelectedAgent(agentKey)
  props.onSwitchAgent?.(agentKey)
}

const handleLlmChange = (llmValue: string) => {
  console.log('LLM changed to:', llmValue)
  props.onSwitchLlm?.(llmValue)
}

// 发送 /refresh 命令
const sendRefreshCommand = async () => {
  try {
    if (props.onExecuteSystemCommand) {
      await props.onExecuteSystemCommand('/refresh', false)
    } else {
      console.warn('onExecuteSystemCommand prop is not provided')
    }
  } catch (error) {
    console.error('发送 /refresh 命令失败:', error)
  }
}

// 发送 /reload 命令
const sendReloadCommand = async () => {
  try {
    if (props.onExecuteSystemCommand) {
      await props.onExecuteSystemCommand('/reload', false)
    } else {
      console.warn('onExecuteSystemCommand prop is not provided')
    }
  } catch (error) {
    console.error('发送 /reload 命令失败:', error)
  }
}


</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 0 16px;
  box-sizing: border-box;
}

.instance-select {
  width: 100%;
  background: rgba(20, 20, 50, 0.55);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  flex-wrap: wrap;
  box-shadow: 0 10px 30px rgba(15, 15, 35, 0.25);
}

.instance-select .selector-group {
  display: flex;
  flex: 1 1 auto;
  flex-wrap: wrap;
  align-items: center;
  column-gap: 16px;
  row-gap: 12px;
  min-width: 0;
}

.instance-select .right-btns {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
  flex: 0 0 auto;
  min-width: fit-content;
}

.instance-select .right-btns .el-icon {
  cursor: pointer;
}

.instance-select .right-btns .el-icon:hover {
  transition: all 0.3s ease-in-out;
  transform: scale(1.2);
}

.instance-select .selector-group .el-select {
  flex: 1 1 200px;
  min-width: 180px;
  max-width: 240px;
  margin: 0;
  border: none !important;
  background-color: transparent;
}

.instance-select .el-select {
  --el-select-border-color-hover: rgba(255, 255, 255, 0.3);
  --el-select-input-focus-border-color: rgba(64, 158, 255, 0.8);
}

.instance-select .el-select__wrapper {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  box-shadow: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 36px;
}

.instance-select .el-select__wrapper:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.instance-select .el-select__wrapper.is-focused {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(64, 158, 255, 0.8);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  transform: translateY(-1px);
}

.instance-select .el-input__inner {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  height: 34px;
  line-height: 34px;
}

.instance-select .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.6);
  font-weight: 400;
}

.instance-select .el-select__selected-item {
  color: #fff !important;
  font-weight: 500;
}

.instance-select .el-select__selection .el-select__selected-item {
  color: #fff !important;
}

.instance-select .el-input__wrapper .el-input__inner {
  color: #fff !important;
}

.instance-select .el-select .el-input .el-input__inner {
  color: #fff !important;
}

.instance-select .el-select__caret {
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.instance-select .el-select__wrapper:hover .el-select__caret {
  color: rgba(255, 255, 255, 0.9);
}
.instance-select-popper {
  border: none !important;
  background-color: transparent;
}
.instance-select-popper .el-popper__arrow:before {
  background-color: rgba(22, 27, 34, 1) !important;
  border-color: rgba(22, 27, 34, 1) !important;
}

.config-list {
  margin-bottom: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.config-item {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.config-item .el-input {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 1200px) {
  .container {
    padding: 0 12px;
  }

  .instance-select {
    gap: 12px;
  }

  .instance-select .selector-group {
    column-gap: 12px;
  }

  .instance-select .selector-group .el-select {
    flex: 1 1 180px;
    min-width: 160px;
    max-width: 220px;
  }
}

@media (max-width: 992px) {
  .instance-select {
    background: rgba(20, 20, 50, 0.6);
    border-radius: 10px;
  }

  .instance-select .selector-group {
    flex: 1 1 100%;
  }

  .instance-select .selector-group .el-select {
    flex: 1 1 45%;
    min-width: 200px;
    max-width: none;
  }

  .instance-select .right-btns {
    flex: 1 1 100%;
    justify-content: flex-start;
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }

  .instance-select {
    gap: 10px;
    padding: 12px;
  }

  .instance-select .selector-group {
    gap: 10px;
  }

  .instance-select .selector-group .el-select {
    flex: 1 1 100%;
    width: 100%;
    min-width: 0;
  }

  .instance-select .right-btns {
    gap: 10px;
  }

  .instance-select .right-btns .el-icon {
    padding: 6px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 6px;
    gap: 10px;
  }

  .instance-select {
    border-radius: 8px;
    gap: 8px;
    padding: 10px;
  }

  .instance-select .right-btns {
    gap: 8px;
  }

  .instance-select .right-btns .el-icon {
    width: 28px;
    height: 28px;
    display: grid;
    place-items: center;
  }

  .instance-select .el-select__wrapper {
    height: 34px;
  }
}
</style>
