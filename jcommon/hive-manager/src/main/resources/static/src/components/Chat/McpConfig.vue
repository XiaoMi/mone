<template>
  <div class="mcp-container">
    <template v-if="Object.keys(serverList)?.length">
        <el-collapse v-model="activeCollapse" @change="handleCollapseChange">
            <el-collapse-item v-for="(key, index) in Object.keys(serverList)" :name="index" :key="key">
                <template #title>
                    <div class="title-container">
                        <el-icon class="arrow-icon" :class="{ 'is-active': activeCollapse.includes(index) }"><ArrowDown /></el-icon>
                        <span>{{key}}</span>
                    </div>
                </template>
                <template #icon>
                    <div class="dot" :class="{'success': serverList[key].status}"></div>
                </template>
                <ul class="info-list">
                  <li v-if="serverList[key].version">
                    <span class="info-title">version:</span>
                    <el-tag size="small" effect="plain">{{serverList[key].version}}</el-tag>
                  </li>
                </ul>
                <el-tabs v-model="activeTabs" v-if="serverList[key].tools && Object.keys(serverList[key].tools)?.length">
                  <el-tab-pane label="Tools" name="tools">
                    <div class="tool-section" v-for="(name) in Object.keys(serverList[key].tools)" :key="name">
                      <div class="tool-item">
                          <div class="tool-header">{{name}}</div>
                          <div class="tool-desc">{{serverList[key].tools[name].description || "无"}}</div>
                          <div class="param-item" v-if="serverList[key].tools[name].inputSchema">
                            <div class="param-label">PARAMETERS</div>
                            <div class="param-content" v-for="(item, ind) in handleParams(serverList[key].tools[name].inputSchema)" :key="ind">
                                <div class="query-param">
                                    <dl>
                                        <dt>{{item.name}}:</dt>
                                        <dd>{{item.description}}</dd>
                                    </dl>
                                </div>
                            </div>
                          </div>
                      </div>
                    </div>
                    <el-button type="primary" size="small" round plain @click="executeSql(key)" class="action-btn">
                      Restart server
                    </el-button>
                  </el-tab-pane>
                </el-tabs>
                <template v-else>
                  <el-empty description="暂无Tools">
                        <template #image>
                            <el-icon :size="48"><Warning /></el-icon>
                        </template>
                    </el-empty>
                  <el-button type="primary" size="small" round plain @click="executeSql(key)" class="action-btn">
                    Retry Connection
                  </el-button>
                </template>
            </el-collapse-item>
        </el-collapse>
        <el-button type="primary" @click="openFile" class="action-btn edit-btn">
            <el-icon><Edit /></el-icon>&emsp;
            Edit MCP Settings
        </el-button>
    </template>
    <el-empty v-else description="暂无数据">
        <template #image>
            <el-icon :size="48"><Warning /></el-icon>
        </template>
        <el-button round plain class="empty-btn" @click="openFile">Edit MCP Settings</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ArrowDown, Edit, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getMcp,
  getMcpStatus,
  getTools,
  getMcpVersion,
  mcpRetryConnection,
  openMcp,
  type McpServer,
  type McpTool
} from '@/api/mcp'

// 添加全局类型声明
declare global {
  interface Window {
    refreshMcp?: (isRefresh: string) => void;
  }
}

const activeCollapse = ref<number[]>([])
const activeTabs = ref('tools')
const serverList = ref<Record<string, McpServer>>({})
const winCaches = ref<Record<string, Function>>({})

const executeSql = async (name: string) => {
  try {
    await mcpRetryConnection(name);
    const data = await getTools(name);
    serverList.value[name].tools = data[name] || {};
    const res = await getMcpStatus(name);
    if (typeof res === 'object' && Object.keys(res)?.length) {
      serverList.value[name].status = res[name] == "1";
    } else if (typeof res === 'string') {
      serverList.value[name].status = res == "1";
    }
    const version = await getMcpVersion(name);
    serverList.value[name].version = version;
    ElMessage.success('操作成功')
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '未知错误'
    ElMessage.error('操作失败：' + errorMessage)
  }
}

const gitList = async () => {
    const response = await getMcp();
    const data = response.data.data.servers;
    const keys = Object.keys(data);
    const list: Record<string, McpServer> = {};
    if (keys.length) {
        keys.forEach(key => {
            list[key] = {
                ...data[key],
                status: false
            };
        })
    }
    serverList.value = list;
    await getStatus();
}

const getStatus = async () => {
    const res = await getMcpStatus();
    if (typeof res === 'object' && Object.keys(res)?.length) {
        Object.keys(res).forEach(key => {
          if (serverList.value[key]) {
            serverList.value[key].status = res[key] == "1"
          }
        })
    }
}

const handleParams = (str: string) => {
  try {
    const obj = JSON.parse(str);
    if (obj?.properties) {
      return Object.keys(obj.properties).map(v => ({
        name: obj.required?.includes(v) ? v + '*' : v,
        description: obj.properties[v].description || "无"
      }))
    }
  }catch {
    return [];
  }
}

const openFile = () => {
    openMcp()
}

const refreshMcp = (isRefresh: string) => {
  if (isRefresh == "1") {
    gitList()
  }
}

const handleCollapseChange = async (val: number[]) => {
  // 获取最新展开的项
  const lastOpened = val[val.length - 1];
  if (lastOpened !== undefined) {
    // 获取对应的服务器名称
    const serverName = Object.keys(serverList.value)[lastOpened];
    const data = await getTools(serverName);
    const res = await getMcpStatus(serverName);
    if (typeof res === 'object' && Object.keys(res)?.length) {
        Object.keys(res).forEach(key => {
          if (serverList.value[key]) {
            serverList.value[key].status = res[key] == "1"
          }
        })
    } else if (typeof res === 'string' && serverList.value[serverName]) {
      serverList.value[serverName].status = res == "1";
    }
    const version = await getMcpVersion(serverName);
    console.log("version", version)
    if (serverList.value[serverName]) {
      serverList.value[serverName].version = version;
      serverList.value[serverName].tools = data[serverName] || {};
    }
  }
}

onMounted(() => {
    gitList()
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
  background: var(--el-bg-color);

  // 滚动条样式优化
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: var(--el-fill-color-lighter);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--el-color-info-light-5);
    border-radius: 3px;

    &:hover {
      background: var(--el-color-info-light-3);
    }
  }

  .empty-btn {
    background-color: var(--el-color-info);
    border-color: var(--el-color-info);
    color: var(--el-color-white);

    &:hover {
      background-color: var(--el-color-info-light-3);
      border-color: var(--el-color-info-light-3);
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
    }

    .info-title {
      margin-right: 8px;
      font-weight: 500;
      color: var(--el-text-color-regular);
    }
  }

  .tool-section {
    margin-bottom: 16px;

    .tool-item {
      margin-bottom: 20px;
      padding: 16px;
      background: var(--el-fill-color-lighter);
      border-radius: 8px;
      border: 1px solid var(--el-border-color-light);
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        border-color: var(--el-color-primary-light-5);
      }

      .tool-header {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-color-primary);
        margin-bottom: 10px;
        display: flex;
        align-items: center;

        &::before {
          content: '🔧';
          margin-right: 8px;
          font-size: 14px;
        }
      }

      .tool-desc {
        color: var(--el-text-color-regular);
        font-size: 14px;
        margin-bottom: 12px;
        line-height: 1.5;
      }

      .param-item {
        border: 1px solid var(--el-border-color);
        padding: 12px;
        margin-bottom: 12px;
        border-radius: 6px;
        background: var(--el-bg-color);

        .param-label {
          font-weight: 600;
          margin-bottom: 10px;
          color: var(--el-color-warning);
          font-size: 12px;
          text-transform: uppercase;
          letter-spacing: 0.5px;
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
              padding: 8px 0;
              border-bottom: 1px solid var(--el-border-color-lighter);

              &:last-child {
                border-bottom: none;
              }

              dt {
                color: var(--el-color-white);
                background-color: var(--el-color-danger);
                padding: 2px 8px;
                margin-right: 12px;
                border-radius: 4px;
                font-size: 12px;
                font-weight: 500;
                min-width: fit-content;
                flex-shrink: 0;
              }

              dd {
                color: var(--el-text-color-regular);
                margin: 0;
                line-height: 1.4;
                flex: 1;
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

    &.edit-btn {
      margin-top: 24px;
      background-color: var(--el-color-primary);
      border-color: var(--el-color-primary);

      &:hover {
        background-color: var(--el-color-primary-light-3);
        border-color: var(--el-color-primary-light-3);
      }
    }
  }

  .dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: var(--el-color-danger);
    transition: all 0.3s ease;

    &.success {
      background-color: var(--el-color-success);
    }
  }

  .title-container {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;

    .arrow-icon {
      font-size: 14px;
      transition: transform 0.3s ease;
      transform: rotate(-90deg);
      color: var(--el-text-color-regular);

      &.is-active {
        transform: rotate(0deg);
        color: var(--el-color-primary);
      }
    }

    span {
      font-weight: 500;
      color: var(--el-text-color-primary);
      flex: 1;
    }
  }

  // Element Plus 组件样式覆盖
  :deep(.el-collapse-item__header) {
    background-color: var(--el-fill-color-light);
    border-radius: 6px;
    margin-bottom: 8px;
    padding: 12px 16px;
    border: 1px solid var(--el-border-color-light);
    transition: all 0.3s ease;

    &:hover {
      background-color: var(--el-fill-color);
      border-color: var(--el-color-primary-light-5);
    }
  }

  :deep(.el-collapse-item__content) {
    padding: 16px;
    background-color: var(--el-bg-color);
    border-radius: 6px;
    border: 1px solid var(--el-border-color-lighter);
    margin-bottom: 16px;
  }

  :deep(.el-collapse-item__wrap) {
    border: none;
  }

  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    background-color: var(--el-border-color-light);
  }

  :deep(.el-empty) {
    padding: 40px 20px;
  }

  :deep(.el-empty__description) {
    color: var(--el-text-color-placeholder);
  }
}
</style>

<style lang="scss">
.mcp-confirm {
    .el-message-box__btns {
        .el-button {
            &:hover {
                background-color: #333;
                border-color: #333;
                color: white;
            }
        }
        .el-button--primary {
            background-color: var(--el-text-color-seconday);
            border-color: var(--el-text-color-placeholder);
        }
    }
}
</style>
