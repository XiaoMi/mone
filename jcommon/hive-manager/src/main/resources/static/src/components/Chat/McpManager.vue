<template>
  <div class="mcp-manager">
    <!-- MCP配置按钮 -->
    <el-tooltip
      class="mcp-manager-tooltip"
      effect="dark"
      content="MCP配置"
      placement="top"
    >
      <el-icon size="14px" color="var(--el-color-info)" @click="handleOpenMcpConfig">
        <svg
          viewBox="0 0 1024 1024"
          xmlns="http://www.w3.org/2000/svg"
          width="14"
          height="14"
        >
          <path
            d="M512 85.333333c117.76 0 213.333333 95.573333 213.333333 213.333334v85.333333h85.333334c47.146667 0 85.333333 38.186667 85.333333 85.333333v469.333334c0 47.146667-38.186667 85.333333-85.333333 85.333333H213.333333c-47.146667 0-85.333333-38.186667-85.333333-85.333333V469.333333c0-47.146667 38.186667-85.333333 85.333333-85.333333h85.333334v-85.333333c0-117.76 95.573333-213.333333 213.333333-213.333334z m298.666667 298.666667H213.333333v469.333333h597.333334V384z m-298.666667 85.333333c23.573333 0 42.666667 19.093333 42.666667 42.666667v128c0 23.573333-19.093333 42.666667-42.666667 42.666667s-42.666667-19.093333-42.666667-42.666667v-128c0-23.573333 19.093333-42.666667 42.666667-42.666667z m0-384c-70.826667 0-128 57.173333-128 128v85.333333h256v-85.333333c0-70.826667-57.173333-128-128-128z"
            fill="currentColor"
          />
        </svg>
      </el-icon>
    </el-tooltip>

    <!-- MCP配置抽屉 -->
    <el-drawer
      v-model="mcpConfigDrawerVisible"
      title="MCP服务管理"
      size="600px"
      direction="rtl"
    >
      <div class="mcp-config-content">
        <!-- MCP服务列表 -->
        <div class="mcp-section">
          <div class="section-header">
            <h3>MCP服务列表</h3>
            <el-button type="primary" size="small" @click="handleMcpList" :loading="isFetchingServers">
              <el-icon><Refresh /></el-icon>
              刷新列表
            </el-button>
          </div>
          <div class="mcp-servers-list">
            <div class="add-server">
              <el-input
                v-model="newServerName"
                size="small"
                placeholder="请输入 MCP 服务名称"
                clearable
                @keyup.enter="handleMcpAdd"
              />
              <el-button
                type="primary"
                size="small"
                :disabled="!canSubmitNewServer || isAddingServer"
                :loading="isAddingServer"
                @click="handleMcpAdd"
              >
                添加服务
              </el-button>
            </div>
            <el-empty v-if="!mcpServers.length && !isFetchingServers" description="暂无MCP服务" />
            <div v-else class="server-list" v-loading="isFetchingServers">
              <div v-for="server in mcpServers" :key="server.name" class="server-item">
                <div class="server-meta">
                  <span class="server-name">{{ server.name }}</span>
                  <div class="server-stats" v-if="server.toolsCount !== undefined || server.status">
                    <span v-if="server.toolsCount !== undefined" class="server-tools">
                      工具数：{{ server.toolsCount }}
                    </span>
                    <el-tag
                      v-if="server.status"
                      size="small"
                      effect="dark"
                      :type="getStatusTagType(server.status)"
                    >
                      {{ formatStatusLabel(server.status) }}
                    </el-tag>
                  </div>
                </div>
                <div class="server-actions">
                  <el-button
                    size="small"
                    type="success"
                    @click="handleMcpRefresh(server.name)"
                    :loading="isServerLoading(server.name, 'refresh')"
                  >
                    <el-icon><Refresh /></el-icon>
                    添加/刷新
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleMcpDelete(server.name)"
                    :loading="isServerLoading(server.name, 'delete')"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Delete } from '@element-plus/icons-vue'

// Props定义
const props = defineProps({
  onExecuteMcpCommand: {
    type: Function,
    required: true,
  },
})

interface McpCommandResult {
  success: boolean
  output?: string
  error?: string
}

interface McpServer {
  name: string
  toolsCount?: number
  status?: string
}

interface ParsedMcpListResult {
  servers: McpServer[]
  message?: string
  totalCount?: number
}

// 响应式数据
const mcpConfigDrawerVisible = ref(false)
const mcpServers = ref<McpServer[]>([])
const newServerName = ref('')
const isAddingServer = ref(false)
const isFetchingServers = ref(false)
const serverLoadingMap = ref<Record<string, { refresh: boolean; delete: boolean }>>({})

const canSubmitNewServer = computed(() => newServerName.value.trim().length > 0)

// MCP日志管理（暂时输出至控制台，后续可接入UI）
const addMcpLog = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
  const now = new Date().toLocaleTimeString()
  console.log(`[%cMCP%c ${type}] ${now} ${message}`, 'color:#409EFF;', 'color:inherit;')
}

// MCP命令执行
const executeMcpCommand = async (command: string): Promise<McpCommandResult> => {
  try {
    addMcpLog(`执行命令: ${command}`, 'info')
    
    const result: McpCommandResult = await props.onExecuteMcpCommand(command)
    const output = typeof result?.output === 'string' ? result.output : ''
    const error = typeof result?.error === 'string' ? result.error : undefined

    if (result?.success) {
      addMcpLog(`命令执行成功`, 'success')
    } else {
      addMcpLog(`命令执行失败: ${error || '未知错误'}`, 'error')
    }
    return {
      success: !!result?.success,
      output,
      error,
    }
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    addMcpLog(`命令执行异常: ${errorMsg}`, 'error')
    return { success: false, output: '', error: errorMsg }
  }
}

const tryParseJson = (raw: string): Record<string, unknown> | Array<unknown> | null => {
  if (!raw) return null
  let payload = raw.trim()
  if (!payload || payload.startsWith('<')) return null

  if (!payload.startsWith('{') && !payload.startsWith('[')) {
    const needsClosingBrace = !payload.endsWith('}')
    payload = `{${payload}${needsClosingBrace ? '}' : ''}`
  }
  try {
    return JSON.parse(payload)
  } catch (parseError) {
    console.warn('初次解析JSON失败:', parseError)
    const jsonMatch = payload.match(/\{[\s\S]*\}/)
    if (jsonMatch) {
      try {
        return JSON.parse(jsonMatch[0])
      } catch (innerError) {
        console.warn('解析JSON失败:', innerError)
      }
    }
  }
  return null
}

const normalizeServers = (servers: unknown[]): McpServer[] => {
  return servers
    .map((server, index) => {
      if (typeof server === 'string') {
        return { name: server }
      }
      if (server && typeof server === 'object') {
        const serverObj = server as Record<string, unknown>
        const name = typeof serverObj.name === 'string' && serverObj.name.trim() ? serverObj.name.trim() : `MCP_${index + 1}`
        const toolsCount = typeof serverObj.toolsCount === 'number' ? serverObj.toolsCount : undefined
        const status = typeof serverObj.status === 'string' ? serverObj.status : undefined
        return { name, toolsCount, status }
      }
      return null
    })
    .filter((item): item is McpServer => !!item && !!item.name)
}

const parseMcpListResponse = (rawOutput: string): ParsedMcpListResult => {
  const fallback: ParsedMcpListResult = { servers: [] }
  if (!rawOutput?.trim()) {
    return fallback
  }

  const candidatePayloads: string[] = []
  const dataLines = rawOutput
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(line => !!line)

  dataLines.forEach(line => {
    if (line.startsWith('data:')) {
      candidatePayloads.push(line.slice(5).trim())
    }
  })

  if (!candidatePayloads.length) {
    candidatePayloads.push(rawOutput.trim())
  }

  for (const payload of candidatePayloads) {
    const parsed = tryParseJson(payload)
    if (parsed && typeof parsed === 'object' && !Array.isArray(parsed)) {
      const record = parsed as Record<string, unknown>
      if (Array.isArray(record.servers)) {
        return {
          servers: normalizeServers(record.servers),
          message: typeof record.message === 'string' ? record.message : undefined,
          totalCount: typeof record.totalCount === 'number' ? record.totalCount : undefined,
        }
      }
    }
    if (Array.isArray(parsed)) {
      return {
        servers: normalizeServers(parsed),
      }
    }
  }

  // Fallback: 提取所有 name 字段
  const nameMatches = Array.from(rawOutput.matchAll(/"name"\s*:\s*"([^"\\]+)"/g)).map(match => match[1])
  if (nameMatches.length) {
    return {
      servers: nameMatches.map(name => ({ name })),
    }
  }

  // 最后尝试按照行拆分
  const plainNames = rawOutput
    .replace(/data:/g, '')
    .split(/[\s,]+/)
    .map(item => item.replace(/["\[\]{}]/g, '').trim())
    .filter(item => item && !/^(success|message|totalCount|timestamp|true|false)$/i.test(item) && item.includes('_'))

  if (plainNames.length) {
    return {
      servers: plainNames.map(name => ({ name })),
    }
  }

  return fallback
}

const setServerLoading = (name: string, action: 'refresh' | 'delete', value: boolean) => {
  const prev = serverLoadingMap.value[name] || { refresh: false, delete: false }
  serverLoadingMap.value = {
    ...serverLoadingMap.value,
    [name]: {
      ...prev,
      [action]: value,
    },
  }
}

const clearObsoleteServerLoading = (servers: McpServer[]) => {
  const availableNames = new Set(servers.map(server => server.name))
  const nextMap: Record<string, { refresh: boolean; delete: boolean }> = {}
  Object.entries(serverLoadingMap.value).forEach(([name, state]) => {
    if (availableNames.has(name)) {
      nextMap[name] = state
    }
  })
  serverLoadingMap.value = nextMap
}

const isServerLoading = (name: string, action: 'refresh' | 'delete') => {
  return serverLoadingMap.value[name]?.[action] === true
}

// MCP管理操作
const handleOpenMcpConfig = () => {
  mcpConfigDrawerVisible.value = true
  // 打开时自动刷新MCP服务列表
  handleMcpList()
}

const handleMcpList = async () => {
  if (isFetchingServers.value) return
  isFetchingServers.value = true
  const result = await executeMcpCommand('/mcp list')

  try {
    if (result.success) {
      const parsed = parseMcpListResponse(result.output || '')
      mcpServers.value = parsed.servers
      clearObsoleteServerLoading(parsed.servers)

      if (parsed.message) {
        addMcpLog(parsed.message, 'info')
      }

      if (!parsed.servers.length) {
        addMcpLog('未获取到任何MCP服务', 'info')
      } else {
        const count = parsed.totalCount ?? parsed.servers.length
        addMcpLog(`已获取 ${count} 个MCP服务`, 'success')
      }
    }
  } catch (error) {
    addMcpLog(`解析服务器列表失败: ${error instanceof Error ? error.message : error}`, 'error')
  } finally {
    isFetchingServers.value = false
  }
}

const handleMcpDelete = async (serverName: string) => {
  try {
    await ElMessageBox.confirm(`确定要删除MCP服务 "${serverName}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    setServerLoading(serverName, 'delete', true)
    const result = await executeMcpCommand(`/mcp delete ${serverName}`)
    if (result.success) {
      // 删除成功后刷新列表
      ElMessage.success(`已删除 MCP 服务 ${serverName}`)
      addMcpLog(`服务 ${serverName} 已删除`, 'success')
      await handleMcpList()
    }
  } catch {
    // 用户取消操作
    addMcpLog(`取消删除服务: ${serverName}`, 'info')
  } finally {
    setServerLoading(serverName, 'delete', false)
  }
}

const handleMcpRefresh = async (serverName: string) => {
  setServerLoading(serverName, 'refresh', true)
  const result = await executeMcpCommand(`/mcp refresh ${serverName}`)
  if (result.success) {
    // 刷新成功后更新列表
    ElMessage.success(`已添加或刷新 MCP 服务 ${serverName}`)
    addMcpLog(`服务 ${serverName} 添加/刷新成功`, 'success')
    await handleMcpList()
  }
  setServerLoading(serverName, 'refresh', false)
}

const handleMcpAdd = async () => {
  if (!canSubmitNewServer.value || isAddingServer.value) {
    return
  }

  const serverName = newServerName.value.trim()
  isAddingServer.value = true

  const result = await executeMcpCommand(`/mcp refresh ${serverName}`)

  if (result.success) {
    ElMessage.success(`已添加 MCP 服务 ${serverName}`)
    addMcpLog(`添加服务成功: ${serverName}`, 'success')
    newServerName.value = ''
    await handleMcpList()
  } else {
    ElMessage.error(`添加 MCP 服务失败：${result.error || '未知错误'}`)
  }

  isAddingServer.value = false
}

const statusLabelMap: Record<string, string> = {
  connected: '已连接',
  connecting: '连接中',
  disconnected: '已断开',
  error: '异常',
  running: '运行中',
  idle: '空闲',
}

const getStatusTagType = (status?: string): 'success' | 'info' | 'warning' | 'danger' => {
  if (!status) return 'info'
  const normalized = status.toLowerCase()
  if (['connected', 'running', 'active', 'ready'].includes(normalized)) return 'success'
  if (['connecting', 'pending', 'initializing', 'refreshing'].includes(normalized)) return 'warning'
  if (['disconnected', 'error', 'failed', 'offline'].includes(normalized)) return 'danger'
  return 'info'
}

const formatStatusLabel = (status?: string) => {
  if (!status) return '未知状态'
  const normalized = status.toLowerCase()
  return statusLabelMap[normalized] || status
}

// 暴露给父组件的方法
defineExpose({
  openMcpConfig: handleOpenMcpConfig,
  refreshMcpList: handleMcpList,
})
</script>

<style scoped>
.mcp-manager {
  display: inline-block;
}

.mcp-manager-tooltip .el-icon {
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.mcp-manager-tooltip .el-icon:hover {
  transform: scale(1.2);
}

/* MCP配置抽屉样式 */
.mcp-config-content {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.mcp-section {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 8px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-header h3 {
  margin: 0;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.mcp-servers-list {
  min-height: 200px;
}

.server-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.server-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.server-item:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.server-name {
  color: #fff;
  font-weight: 500;
  font-size: 14px;
}

.server-actions {
  display: flex;
  gap: 8px;
}

.server-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.server-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.server-tools {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.add-server {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.add-server :deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.35);
  border-color: rgba(255, 255, 255, 0.15);
  color: #fff;
}



.mcp-logs::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.mcp-logs::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.mcp-logs::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>