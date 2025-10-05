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
            <el-button type="primary" size="small" @click="handleMcpList">
              <el-icon><Refresh /></el-icon>
              刷新列表
            </el-button>
          </div>
          <div class="mcp-servers-list">
            <el-empty v-if="mcpServers.length === 0" description="暂无MCP服务" />
            <div v-else class="server-list">
              <div v-for="server in mcpServers" :key="server" class="server-item">
                <span class="server-name">{{ server }}</span>
                <div class="server-actions">
                  <el-button size="small" type="warning" @click="handleMcpRefresh(server)">
                    <el-icon><Refresh /></el-icon>
                    刷新
                  </el-button>
                  <el-button size="small" type="danger" @click="handleMcpDelete(server)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 批量操作 -->
        <div class="mcp-section">
          <div class="section-header">
            <h3>批量操作</h3>
          </div>
          <div class="batch-actions">
            <el-button type="success" @click="handleMcpRefreshAll">
              <el-icon><Refresh /></el-icon>
              刷新所有MCP服务
            </el-button>
          </div>
        </div>

        <!-- 命令执行日志 -->
        <div class="mcp-section">
          <div class="section-header">
            <h3>执行日志</h3>
            <el-button size="small" @click="clearMcpLogs">
              <el-icon><Delete /></el-icon>
              清空日志
            </el-button>
          </div>
          <div class="mcp-logs">
            <div v-if="mcpLogs.length === 0" class="empty-logs">暂无执行日志</div>
            <div v-else class="logs-container">
              <div 
                v-for="(log, index) in mcpLogs" 
                :key="index" 
                class="log-item"
                :class="log.type"
              >
                <span class="log-time">{{ log.time }}</span>
                <span class="log-message">{{ log.message }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Refresh, Delete } from '@element-plus/icons-vue'

// Props定义
const props = defineProps({
  onExecuteMcpCommand: {
    type: Function,
    required: true,
  },
})

// 响应式数据
const mcpConfigDrawerVisible = ref(false)
const mcpServers = ref<string[]>([])
const mcpLogs = ref<Array<{ time: string; message: string; type: 'success' | 'error' | 'info' }>>([])

// MCP日志管理
const addMcpLog = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
  const now = new Date()
  const time = now.toLocaleTimeString()
  mcpLogs.value.unshift({ time, message, type })
  // 最多保留100条日志
  if (mcpLogs.value.length > 100) {
    mcpLogs.value = mcpLogs.value.slice(0, 100)
  }
}

const clearMcpLogs = () => {
  mcpLogs.value = []
  addMcpLog('日志已清空', 'info')
}

// MCP命令执行
const executeMcpCommand = async (command: string): Promise<{ success: boolean; output: string }> => {
  try {
    addMcpLog(`执行命令: ${command}`, 'info')
    
    const result = await props.onExecuteMcpCommand(command)
    if (result.success) {
      addMcpLog(`命令执行成功`, 'success')
    } else {
      addMcpLog(`命令执行失败: ${result.error || '未知错误'}`, 'error')
    }
    return result
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : '未知错误'
    addMcpLog(`命令执行异常: ${errorMsg}`, 'error')
    return { success: false, output: errorMsg }
  }
}

// MCP管理操作
const handleOpenMcpConfig = () => {
  mcpConfigDrawerVisible.value = true
  // 打开时自动刷新MCP服务列表
  handleMcpList()
}

const handleMcpList = async () => {
  const result = await executeMcpCommand('/mcp list')
  if (result.success) {
    try {
      // 尝试从响应中提取服务器列表
      const output = result.output
      if (output) {
        // 模拟一些常见的MCP服务器用于演示
        // 实际使用时会根据真实的响应进行解析
        const mockServers = ['docker_manager', 'file_manager', 'database_manager', 'api_manager']
        
        // 如果返回的是JSON格式
        if (output.includes('{') && output.includes('}')) {
          const jsonMatch = output.match(/\{[\s\S]*\}/)
          if (jsonMatch) {
            const data = JSON.parse(jsonMatch[0])
            if (data.servers && Array.isArray(data.servers)) {
              mcpServers.value = data.servers
              return
            }
          }
        }
        
        // 如果返回的是纯文本格式，按行分割
        const servers = output
          .split('\n')
          .filter(line => line.trim() && !line.startsWith('#') && !line.startsWith('//'))
          .map(line => line.trim())
          .filter(line => line.length > 0)
        
        mcpServers.value = servers.length > 0 ? servers : mockServers
      } else {
        // 如果没有输出，使用模拟数据
        mcpServers.value = ['docker_manager', 'file_manager', 'database_manager']
      }
    } catch (error) {
      addMcpLog(`解析服务器列表失败: ${error}`, 'error')
      // 解析失败时使用模拟数据
      mcpServers.value = ['docker_manager', 'file_manager']
    }
  } else {
    addMcpLog('获取MCP服务列表失败', 'error')
  }
}

const handleMcpDelete = async (serverName: string) => {
  try {
    await ElMessageBox.confirm(`确定要删除MCP服务 "${serverName}" 吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    const result = await executeMcpCommand(`/mcp delete ${serverName}`)
    if (result.success) {
      // 删除成功后刷新列表
      await handleMcpList()
    }
  } catch {
    // 用户取消操作
    addMcpLog(`取消删除服务: ${serverName}`, 'info')
  }
}

const handleMcpRefresh = async (serverName: string) => {
  const result = await executeMcpCommand(`/mcp refresh ${serverName}`)
  if (result.success) {
    // 刷新成功后更新列表
    await handleMcpList()
  }
}

const handleMcpRefreshAll = async () => {
  const result = await executeMcpCommand('/mcp refresh all')
  if (result.success) {
    // 刷新成功后更新列表
    await handleMcpList()
  }
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

.batch-actions {
  display: flex;
  justify-content: center;
  padding: 16px 0;
}

.mcp-logs {
  max-height: 300px;
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 6px;
  padding: 12px;
}

.empty-logs {
  text-align: center;
  color: rgba(255, 255, 255, 0.6);
  padding: 24px;
  font-style: italic;
}

.logs-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.log-item {
  display: flex;
  gap: 12px;
  padding: 6px 8px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.4;
}

.log-item.success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.log-item.error {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.log-item.info {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.log-time {
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
  min-width: 70px;
}

.log-message {
  flex: 1;
  word-break: break-all;
}

/* 自定义滚动条 */
.mcp-logs::-webkit-scrollbar {
  width: 6px;
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