<template>
  <div class="mcp-container">
    <!-- 搜索框 -->
    <div class="search-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索 Agent 名称或描述..."
        @input="handleSearch"
        clearable
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <div class="search-stats" v-if="searchQuery">
        找到 {{filteredServerList.length}} 个结果
      </div>
    </div>

    <!-- MCP 配置概要 -->
    <div class="mcp-summary" v-if="currentAgentId">
      <div class="summary-title">
        <el-icon><Position /></el-icon>
        当前 MCP 配置
      </div>
      <div class="summary-content">
        <span v-if="mcpAgentsList.length === 0" class="no-config">未配置任何 Agent</span>
        <el-tag
          v-else
          v-for="agentName in mcpAgentsList"
          :key="agentName"
          size="small"
          type="success"
          effect="plain"
          class="agent-tag"
        >
          {{agentName}}
        </el-tag>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 服务器列表模式 -->
    <template v-else-if="filteredServerList?.length">
        <el-collapse v-model="activeCollapse" @change="handleCollapseChange">
                          <el-collapse-item v-for="(item, index) in filteredServerList" :name="index" :key="item.agent.id">
                <template #title>
                    <div class="title-container">
                        <el-icon class="arrow-icon" :class="{ 'is-active': activeCollapse.includes(index) }"><ArrowDown /></el-icon>
                        <span>{{item.agent.name}}</span>
                    </div>
                </template>
                <template #icon>
                    <div class="dot" :class="{'success': item.instances && item.instances.length > 0}"></div>
                </template>
                <ul class="info-list">
                  <li v-if="item.agent.version">
                    <span class="info-title">版本:</span>
                    <el-tag size="small" effect="plain">{{item.agent.version}}</el-tag>
                  </li>
                  <li v-if="item.agent.group">
                    <span class="info-title">分组:</span>
                    <el-tag size="small" effect="plain" type="info">{{item.agent.group}}</el-tag>
                  </li>
                  <li v-if="item.agent.description">
                    <span class="info-title">描述:</span>
                    <span class="info-value">{{item.agent.description}}</span>
                  </li>
                  <li v-if="item.agent.createdAt">
                    <span class="info-title">创建时间:</span>
                    <span class="info-value">{{new Date(item.agent.createdAt).toLocaleString()}}</span>
                  </li>
                  <li>
                    <span class="info-title">状态:</span>
                    <el-tag size="small" effect="plain" :type="item.instances && item.instances.length > 0 ? 'success' : 'danger'">
                      {{item.instances && item.instances.length > 0 ? '在线' : '离线'}}
                    </el-tag>
                  </li>
                  <li v-if="item.instances?.length">
                    <span class="info-title">实例数:</span>
                    <el-tag size="small" effect="plain" type="success">{{item.instances.length}}</el-tag>
                  </li>
                </ul>

                <!-- MCP 使用状态按钮 -->
                <div class="mcp-actions" v-if="currentAgentId">
                  <el-button
                    size="small"
                    :type="isAgentInMcp(item.agent.name) ? 'success' : 'info'"
                    :plain="!isAgentInMcp(item.agent.name)"
                    @click="toggleAgentInMcp(item.agent.name)"
                    class="mcp-toggle-btn"
                  >
                    <el-icon><Position /></el-icon>
                    {{ isAgentInMcp(item.agent.name) ? '已使用 MCP' : '未使用 MCP' }}
                  </el-button>
                </div>

                <el-tabs v-model="activeTabs" v-if="item.agent.goal || item.agent.profile || item.agent.constraints">
                  <el-tab-pane label="详细信息" name="details">
                    <div class="tool-section">
                      <div class="tool-item" v-if="item.agent.goal">
                          <div class="tool-header">目标</div>
                          <div class="tool-desc">{{item.agent.goal}}</div>
                      </div>
                      <div class="tool-item" v-if="item.agent.profile">
                          <div class="tool-header">简介</div>
                          <div class="tool-desc">{{item.agent.profile}}</div>
                      </div>
                      <div class="tool-item" v-if="item.agent.constraints">
                          <div class="tool-header">约束条件</div>
                          <div class="tool-desc">{{item.agent.constraints}}</div>
                      </div>
                      <div class="tool-item" v-if="item.agent.toolMap">
                          <div class="tool-header">工具映射</div>
                          <JsonViewer
                            :content="item.agent.toolMap"
                            placeholder="暂无工具映射配置"
                            height="120px"
                          />
                      </div>
                      <div class="tool-item" v-if="item.agent.mcpToolMap">
                          <div class="tool-header">MCP工具映射</div>
                          <JsonViewer
                            :content="item.agent.mcpToolMap"
                            placeholder="暂无MCP工具映射配置"
                            height="120px"
                          />
                      </div>
                    </div>
                  </el-tab-pane>
                </el-tabs>
                <template v-else>
                  <el-empty description="暂无详细信息">
                        <template #image>
                            <el-icon :size="48"><Warning /></el-icon>
                        </template>
                    </el-empty>
                </template>
            </el-collapse-item>
        </el-collapse>
    </template>
    <el-empty v-else :description="searchQuery ? '未找到匹配的 Agent' : '暂无数据'">
        <template #image>
            <el-icon :size="48"><Warning /></el-icon>
        </template>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ArrowDown, Warning, Search, Position } from '@element-plus/icons-vue'

import { useTheme } from '@/styles/theme/useTheme'
import { getAgentList, getAgentConfig, setAgentConfig, type Agent } from '@/api/agent'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import JsonViewer from './JsonViewer.vue'

// 添加全局类型声明
declare global {
  interface Window {
    refreshMcp?: (isRefresh: string) => void;
  }
}

// 获取主题
const { currentTheme } = useTheme()

// 获取用户 store
const { getInstance } = useUserStore()

// 定义实例类型
interface InstanceType {
  id: string | number;
  ip: string;
  port: string | number;
  agentId?: string | number;
}

// 获取当前选中的实例
const getCurrentInstance = (): InstanceType | undefined => {
  const instances = getInstance()
  return instances && instances.length > 0 ? instances[0] : undefined
}

// 获取 MCP 配置的 Agent 列表
const mcpAgentsList = computed(() => {
  if (!mcpConfig.value) return []
  return mcpConfig.value.split(',').map(name => name.trim()).filter(name => name)
})



const activeCollapse = ref<number[]>([])
const activeTabs = ref('details')
const serverList = ref<{agent: Agent, instances: Array<object>, isFavorite: boolean}[]>([])
const winCaches = ref<Record<string, (isRefresh: string) => void>>({})
const searchQuery = ref('')
const isLoading = ref(false)
const mcpConfig = ref('') // 存储当前实例的 MCP 配置
const currentAgentId = ref<number | null>(null) // 存储当前实例的 agent ID

// 过滤后的列表
const filteredServerList = computed(() => {
  if (!searchQuery.value.trim()) {
    return serverList.value
  }

  const query = searchQuery.value.toLowerCase().trim()
  return serverList.value.filter(item => {
    const agent = item.agent
    return (
      agent.name.toLowerCase().includes(query) ||
      agent.description.toLowerCase().includes(query) ||
      agent.group?.toLowerCase().includes(query) ||
      agent.profile?.toLowerCase().includes(query) ||
      agent.goal?.toLowerCase().includes(query)
    )
  })
})

const gitList = async (name: string = '') => {
    isLoading.value = true
    try {
        const response = await getAgentList(name);
        serverList.value = response.data.data || [];
    } finally {
        isLoading.value = false
    }
}

// 搜索处理 - 使用防抖
let searchTimeout: NodeJS.Timeout | null = null
const handleSearch = () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }

  searchTimeout = setTimeout(async () => {
    // 如果有搜索内容，调用API搜索；否则获取全部
    await gitList(searchQuery.value.trim())
  }, 300)
}





// 获取当前实例的 MCP 配置
const getMcpConfig = async () => {
  const currentInstance = getCurrentInstance()
  if (!currentInstance?.agentId) {
    return
  }

  try {
    const response = await getAgentConfig(Number(currentInstance.agentId), 'mcp')
    if (response.data.data?.value) {
      mcpConfig.value = response.data.data.value
    } else {
      mcpConfig.value = ''
    }
    currentAgentId.value = Number(currentInstance.agentId)
  } catch {
    // 如果没有配置，设置为空字符串
    mcpConfig.value = ''
    currentAgentId.value = Number(currentInstance.agentId)
  }
}

// 检查 agent 是否在 MCP 配置中被使用
const isAgentInMcp = (agentName: string) => {
  if (!currentAgentId.value || !mcpConfig.value) {
    return false
  }
  const mcpAgents = mcpConfig.value.split(',').map(name => name.trim())
  return mcpAgents.includes(agentName)
}

// 切换 agent 在 MCP 中的使用状态
const toggleAgentInMcp = async (agentName: string) => {
  const currentInstance = getCurrentInstance()
  if (!currentInstance?.agentId) {
    ElMessage.error('未找到当前实例对应的Agent')
    return
  }

  try {
    const currentConfig = mcpConfig.value || ''
    const mcpAgents = currentConfig.split(',').map(name => name.trim()).filter(name => name)

    let newAgents: string[]
    if (mcpAgents.includes(agentName)) {
      // 移除 agent
      newAgents = mcpAgents.filter(name => name !== agentName)
      ElMessage.success(`已将 ${agentName} 从 MCP 配置中移除`)
    } else {
      // 添加 agent
      newAgents = [...mcpAgents, agentName]
      ElMessage.success(`已将 ${agentName} 添加到 MCP 配置中`)
    }

    const newConfig = newAgents.join(',')
    await setAgentConfig(Number(currentInstance.agentId), 'mcp', newConfig)
    mcpConfig.value = newConfig
  } catch (error) {
    console.error('更新 MCP 配置失败:', error)
    ElMessage.error('更新 MCP 配置失败')
  }
}

const refreshMcp = (isRefresh: string) => {
  if (isRefresh == "1") {
    gitList()
  }
}

const handleCollapseChange = async (val: number[]) => {
  // Agent列表模式下暂时不需要特殊处理
  console.log('折叠状态改变:', val);
}

onMounted(async () => {
    await gitList()
    // 获取当前实例的 MCP 配置
    await getMcpConfig()
    window.refreshMcp = refreshMcp
    winCaches.value.refreshMcp = refreshMcp
})

onUnmounted(() => {
    window.refreshMcp = winCaches.value.refreshMcp
})

</script>

<style lang="scss" scoped>
.mcp-container {
  padding: 16px;
  max-height: 70vh;
  overflow-y: auto;
  background: v-bind('currentTheme.colors.chatWindowBackground');
  color: v-bind('currentTheme.colors.textPrimary');
  backdrop-filter: blur(10px);
  border-radius: 8px;

  .search-container {
    margin-bottom: 16px;

    .search-input {
      :deep(.el-input__wrapper) {
        background: v-bind('currentTheme.colors.fillColor');
        border: 1px solid v-bind('currentTheme.colors.borderColorLight');
        border-radius: 12px;
        transition: all 0.3s ease;

        &:hover {
          border-color: v-bind('currentTheme.colors.chatLinkColor');
        }

        &.is-focus {
          border-color: v-bind('currentTheme.colors.primary');
          box-shadow: 0 0 8px v-bind('currentTheme.colors.chatBorderGlow');
        }
      }

      :deep(.el-input__inner) {
        color: v-bind('currentTheme.colors.textPrimary');
        font-size: 14px;

        &::placeholder {
          color: v-bind('currentTheme.colors.textSecondary');
        }
      }

      :deep(.el-input__prefix) {
        color: v-bind('currentTheme.colors.textSecondary');
      }
    }

    .search-stats {
      margin-top: 8px;
      font-size: 12px;
      color: v-bind('currentTheme.colors.textSecondary');
      text-align: right;
    }
  }

  .mcp-summary {
    margin-bottom: 16px;
    padding: 12px 16px;
    background: v-bind('currentTheme.colors.fillColorLight');
    border: 1px solid v-bind('currentTheme.colors.borderColorLight');
    border-radius: 8px;

    .summary-title {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      font-size: 13px;
      font-weight: 600;
      color: v-bind('currentTheme.colors.primary');

      .el-icon {
        margin-right: 6px;
      }
    }

    .summary-content {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      align-items: center;

      .no-config {
        font-size: 12px;
        color: v-bind('currentTheme.colors.textSecondary');
        font-style: italic;
      }

      .agent-tag {
        font-size: 11px;
        border-radius: 4px;
      }
    }
  }

  .loading-container {
    padding: 20px;

    :deep(.el-skeleton__item) {
      background: v-bind('currentTheme.colors.fillColorLight');
    }
  }

  // 滚动条样式优化，使用主题色
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: v-bind('currentTheme.colors.fillColorLighter');
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: v-bind('currentTheme.colors.chatLinkColor');
    border-radius: 3px;
    opacity: 0.6;

    &:hover {
      opacity: 0.8;
    }
  }



  .info-list {
    padding: 0;
    margin: 12px 0;
    list-style: none;

    li {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      padding: 4px 0;
    }

    .info-title {
      margin-right: 8px;
      font-weight: 500;
      color: v-bind('currentTheme.colors.textSecondary');
      font-size: 13px;
    }

    .info-value {
      color: v-bind('currentTheme.colors.textRegular');
      font-size: 13px;
      line-height: 1.4;
    }
  }

  .mcp-actions {
    margin: 12px 0;

    .mcp-toggle-btn {
      width: 100%;
      border-radius: 8px;
      transition: all 0.3s ease;
      font-weight: 500;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px v-bind('currentTheme.colors.chatBorderGlow');
      }

      :deep(.el-icon) {
        margin-right: 6px;
      }
    }
  }

  .tool-section {
    margin-bottom: 16px;

    .tool-item {
      margin-bottom: 20px;
      padding: 18px;
      background: v-bind('currentTheme.colors.fillColor');
      border-radius: 12px;
      border: 1px solid v-bind('currentTheme.colors.borderColorLight');
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      // 添加微妙的渐变效果
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 2px;
        background: linear-gradient(90deg, v-bind('currentTheme.colors.primary'), v-bind('currentTheme.colors.success'), v-bind('currentTheme.colors.warning'));
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px v-bind('currentTheme.colors.chatBorderGlow');
        border-color: v-bind('currentTheme.colors.chatLinkColor');

        &::before {
          opacity: 1;
        }
      }

      .tool-header {
        font-size: 16px;
        font-weight: 600;
        color: v-bind('currentTheme.colors.primary');
        margin-bottom: 12px;
        display: flex;
        align-items: center;

        &::before {
          content: '⚡';
          margin-right: 10px;
          font-size: 16px;
          color: v-bind('currentTheme.colors.chatLinkColor');
        }
      }

      .tool-desc {
        color: v-bind('currentTheme.colors.textRegular');
        font-size: 14px;
        margin-bottom: 15px;
        line-height: 1.6;
        background: v-bind('currentTheme.colors.fillColorLighter');
        padding: 8px 12px;
        border-radius: 6px;
        border-left: 3px solid v-bind('currentTheme.colors.info');
      }



      .param-item {
        border: 1px solid v-bind('currentTheme.colors.borderColor');
        padding: 14px;
        margin-bottom: 12px;
        border-radius: 8px;
        background: v-bind('currentTheme.colors.background');
        backdrop-filter: blur(5px);

        .param-label {
          font-weight: 600;
          margin-bottom: 12px;
          color: v-bind('currentTheme.colors.warning');
          font-size: 12px;
          text-transform: uppercase;
          letter-spacing: 1px;
          display: flex;
          align-items: center;

          &::before {
            content: '📝';
            margin-right: 6px;
            font-size: 14px;
          }
        }

        .param-content {
          margin-bottom: 8px;

          &:last-child {
            margin-bottom: 0;
          }

          .query-param {
            dl {
              display: flex;
              align-items: flex-start;
              margin: 0;
              padding: 10px 0;
              border-bottom: 1px solid v-bind('currentTheme.colors.borderColorLighter');

              &:last-child {
                border-bottom: none;
              }

              dt {
                color: v-bind('currentTheme.colors.background');
                background: linear-gradient(135deg, v-bind('currentTheme.colors.danger'), v-bind('currentTheme.colors.warning'));
                padding: 4px 10px;
                margin-right: 12px;
                border-radius: 6px;
                font-size: 11px;
                font-weight: 600;
                min-width: fit-content;
                flex-shrink: 0;
                box-shadow: 0 2px 4px v-bind('currentTheme.colors.borderColor');
              }

              dd {
                color: v-bind('currentTheme.colors.textRegular');
                margin: 0;
                line-height: 1.5;
                flex: 1;
                font-size: 13px;
              }
            }
          }
        }
      }
    }
  }

  .action-btn {
    width: 100%;
    margin-top: 12px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
    background-color: transparent;


  }

  .dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: v-bind('currentTheme.colors.danger');
    transition: all 0.3s ease;
    box-shadow: 0 0 8px currentColor;

    &.success {
      background-color: v-bind('currentTheme.colors.success');
    }
  }

  .title-container {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;

    .arrow-icon {
      font-size: 14px;
      transition: all 0.3s ease;
      transform: rotate(-90deg);
      color: v-bind('currentTheme.colors.textSecondary');

      &.is-active {
        transform: rotate(0deg);
        color: v-bind('currentTheme.colors.chatLinkColor');
      }
    }

    span {
      font-weight: 600;
      color: v-bind('currentTheme.colors.textPrimary');
      flex: 1;
      font-size: 15px;
    }
  }

  // Element Plus 组件样式覆盖，使用主题色
  :deep(.el-collapse-item__header) {
    background: v-bind('currentTheme.colors.fillColorLight');
    border-radius: 10px;
    margin-bottom: 8px;
    padding: 16px 20px;
    border: 1px solid v-bind('currentTheme.colors.borderColorLight');
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 4px;
      height: 100%;
      background: v-bind('currentTheme.colors.chatLinkColor');
      transform: scaleY(0);
      transition: transform 0.3s ease;
    }

    &:hover {
      background: v-bind('currentTheme.colors.fillColor');
      border-color: v-bind('currentTheme.colors.chatLinkColor');
      transform: translateX(2px);

      &::before {
        transform: scaleY(1);
      }
    }
  }

  :deep(.el-collapse-item__content) {
    padding: 20px;
    background: v-bind('currentTheme.colors.background');
    border-radius: 8px;
    border: 1px solid v-bind('currentTheme.colors.borderColorLighter');
    margin-bottom: 16px;
    backdrop-filter: blur(5px);
  }

  :deep(.el-collapse-item__wrap) {
    border: none;
    background-color: transparent;
  }

  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }

  :deep(.el-tabs__item) {
    color: v-bind('currentTheme.colors.textRegular');
    font-weight: 500;

    &.is-active {
      color: v-bind('currentTheme.colors.primary');
    }

    &:hover {
      color: v-bind('currentTheme.colors.chatLinkColor');
    }
  }

  :deep(.el-tabs__nav-wrap::after) {
    background-color: v-bind('currentTheme.colors.borderColorLight');
  }

  :deep(.el-tabs__active-bar) {
    background-color: v-bind('currentTheme.colors.primary');
  }

  :deep(.el-empty) {
    padding: 40px 20px;

    .el-empty__description {
      color: v-bind('currentTheme.colors.textSecondary');
    }

    .el-empty__image svg {
      fill: v-bind('currentTheme.colors.fillColor');
    }
  }

  :deep(.el-tag) {
    background: v-bind('currentTheme.colors.fillColorLight');
    border-color: v-bind('currentTheme.colors.borderColorLight');
    color: v-bind('currentTheme.colors.textRegular');

    &.el-tag--plain {
      background: v-bind('currentTheme.colors.background');
      color: v-bind('currentTheme.colors.primary');
      border-color: v-bind('currentTheme.colors.primary');
    }
  }

  :deep(.el-button) {
    border-color: v-bind('currentTheme.colors.borderColorLight');
    color: v-bind('currentTheme.colors.textPrimary');
    background: v-bind('currentTheme.colors.fillColor');
    transition: all 0.3s ease;

    &:hover {
      border-color: v-bind('currentTheme.colors.chatLinkColor');
      color: v-bind('currentTheme.colors.chatLinkColor');
      background: v-bind('currentTheme.colors.fillColorLight');
    }

    &.el-button--primary {
      background: v-bind('currentTheme.colors.primary');
      border-color: v-bind('currentTheme.colors.primary');
      color: v-bind('currentTheme.colors.background');

      &:hover {
        background: v-bind('currentTheme.colors.chatLinkColor');
        border-color: v-bind('currentTheme.colors.chatLinkColor');
      }
    }

    &.el-button--small {
      font-size: 12px;
      padding: 6px 12px;
    }

    &[plain] {
      background: transparent;
      color: v-bind('currentTheme.colors.primary');
      border-color: v-bind('currentTheme.colors.primary');

      &:hover {
        background: v-bind('currentTheme.colors.primary');
        color: v-bind('currentTheme.colors.background');
      }
    }
  }
}
</style>

<style lang="scss">
// 全局样式覆盖，确保消息框主题一致
.mcp-confirm {
  .el-message-box {
    background: v-bind('currentTheme.colors.chatWindowBackground');
    border: 1px solid v-bind('currentTheme.colors.borderColorLight');
    backdrop-filter: blur(10px);

    .el-message-box__title {
      color: v-bind('currentTheme.colors.textPrimary');
    }

    .el-message-box__content {
      color: v-bind('currentTheme.colors.textRegular');
    }

    .el-message-box__btns {
      .el-button {
        border-color: v-bind('currentTheme.colors.borderColorLight');
        color: v-bind('currentTheme.colors.textPrimary');
        background: v-bind('currentTheme.colors.fillColor');

        &:hover {
          background: v-bind('currentTheme.colors.fillColorLight');
          border-color: v-bind('currentTheme.colors.chatLinkColor');
          color: v-bind('currentTheme.colors.chatLinkColor');
        }
      }

      .el-button--primary {
        background: v-bind('currentTheme.colors.primary');
        border-color: v-bind('currentTheme.colors.primary');
        color: v-bind('currentTheme.colors.background');

        &:hover {
          background: v-bind('currentTheme.colors.chatLinkColor');
          border-color: v-bind('currentTheme.colors.chatLinkColor');
        }
      }
    }
  }
}
</style>
