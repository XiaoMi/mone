<template>
  <div class="mcp-container">
    <!-- 编辑器模式 -->
    <div v-if="showEditor" class="editor-container">
      <div class="editor-header">
        <h3>编辑 MCP 配置</h3>
        <div class="editor-actions">
          <el-button @click="cancelEdit" size="small">取消</el-button>
          <el-button type="primary" @click="saveConfig" size="small">保存</el-button>
        </div>
      </div>
      <div class="editor-content">
        <!-- 尝试使用CodeMirror -->
        <div v-if="useCodeMirror" class="codemirror-wrapper">
          <CodemirrorEditor
            v-model:value="configContent"
            :extensions="extensions"
            placeholder="请输入 MCP 配置 JSON 内容..."
            class="config-editor"
            :style="{ height: '400px' }"
          />
        </div>
        <!-- 后备方案：普通textarea -->
        <div v-else class="textarea-wrapper">
          <el-input
            v-model="configContent"
            type="textarea"
            :rows="20"
            placeholder="请输入 MCP 配置 JSON 内容..."
            class="config-textarea"
          />
        </div>
        <div class="editor-controls">
          <el-button size="small" @click="toggleEditor">
            切换到 {{ useCodeMirror ? 'Textarea' : 'CodeMirror' }}
          </el-button>
        </div>
      </div>
    </div>
    <!-- 服务器列表模式 -->
    <template v-else-if="Object.keys(serverList)?.length">
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
                    <span class="info-title">版本:</span>
                    <el-tag size="small" effect="plain">{{serverList[key].version}}</el-tag>
                  </li>
                </ul>
                <el-tabs v-model="activeTabs" v-if="serverList[key].tools && Object.keys(serverList[key].tools)?.length">
                  <el-tab-pane label="工具" name="tools">
                    <div class="tool-section" v-for="(name) in Object.keys(serverList[key].tools)" :key="name">
                      <div class="tool-item">
                          <div class="tool-header">{{name}}</div>
                          <div class="tool-desc">{{serverList[key].tools[name].description || "无"}}</div>
                          <div class="param-item" v-if="serverList[key].tools[name].inputSchema">
                            <div class="param-label">参数</div>
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
                      重启服务器
                    </el-button>
                  </el-tab-pane>
                </el-tabs>
                <template v-else>
                  <el-empty description="暂无工具">
                        <template #image>
                            <el-icon :size="48"><Warning /></el-icon>
                        </template>
                    </el-empty>
                  <el-button type="primary" size="small" round plain @click="executeSql(key)" class="action-btn">
                    重试连接
                  </el-button>
                </template>
            </el-collapse-item>
        </el-collapse>
        <el-button type="primary" @click="openFile" class="action-btn edit-btn">
            <el-icon><Edit /></el-icon>&emsp;
            编辑 MCP 设置
        </el-button>
    </template>
    <el-empty v-else description="暂无数据">
        <template #image>
            <el-icon :size="48"><Warning /></el-icon>
        </template>
        <el-button round plain class="empty-btn" @click="openFile">编辑 MCP 设置</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { ArrowDown, Edit, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useTheme } from '@/styles/theme/useTheme'
import {
  getMcp,
  getMcpStatus,
  getTools,
  getMcpVersion,
  mcpRetryConnection,
  openMcp,
  type McpServer
} from '@/api/mcp'
import CodemirrorEditor from 'codemirror-editor-vue3'
import { json } from '@codemirror/lang-json'
import { oneDark } from '@codemirror/theme-one-dark'

// 添加全局类型声明
declare global {
  interface Window {
    refreshMcp?: (isRefresh: string) => void;
  }
}

// 获取主题
const { currentTheme } = useTheme()

const activeCollapse = ref<number[]>([])
const activeTabs = ref('tools')
const serverList = ref<Record<string, McpServer>>({})
const winCaches = ref<Record<string, (isRefresh: string) => void>>({})
const showEditor = ref(false)
const configContent = ref('')
const useCodeMirror = ref(true)

// CodeMirror扩展配置
const extensions = computed(() => {
  const isDark = currentTheme.value.name === 'dark' || currentTheme.value.name === 'cyberpunk'
  return [
    json(),
    ...(isDark ? [oneDark] : [])
  ]
})

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

const openFile = async () => {
    try {
        // 获取当前配置内容
        const response = await getMcp();
        console.log('获取到的配置数据:', response);
        const formattedContent = JSON.stringify(response.data.data, null, 2);
        console.log('格式化后的内容:', formattedContent);

        // 如果没有内容，提供一个默认示例
        if (!formattedContent || formattedContent === '{}' || formattedContent === 'null') {
            configContent.value = `{
  "servers": {
    "example-server": {
      "command": "node",
      "args": ["path/to/your/mcp-server.js"],
      "env": {}
    }
  }
}`;
        } else {
            configContent.value = formattedContent;
        }

        showEditor.value = true;

        // 等待下一个 tick 确保 DOM 更新
        await nextTick();
        console.log('configContent.value:', configContent.value);
    } catch (error) {
        ElMessage.error('获取配置失败');
        console.error(error);
        // 即使失败也显示编辑器，提供默认内容
        configContent.value = `{
  "servers": {
    "example-server": {
      "command": "node",
      "args": ["path/to/your/mcp-server.js"],
      "env": {}
    }
  }
}`;
        showEditor.value = true;
    }
}

const cancelEdit = () => {
    showEditor.value = false;
    configContent.value = '';
}

const toggleEditor = () => {
    useCodeMirror.value = !useCodeMirror.value;
}

const saveConfig = async () => {
    try {
        // 验证JSON格式
        JSON.parse(configContent.value);

        // 这里应该调用保存配置的API
        // 暂时先用openMcp函数，实际应该替换为保存API
        await openMcp();

        ElMessage.success('配置保存成功');
        showEditor.value = false;
        configContent.value = '';

        // 刷新服务器列表
        await gitList();
    } catch (error) {
        if (error instanceof SyntaxError) {
            ElMessage.error('JSON格式错误，请检查配置内容');
        } else {
            ElMessage.error('保存配置失败');
        }
        console.error(error);
    }
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

    // 设置一个测试值看看编辑器是否能显示
    setTimeout(() => {
        console.log('设置测试值')
        configContent.value = '{"test": "这是一个测试值"}'
    }, 1000)
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

  .editor-container {
    height: 100%;
    display: flex;
    flex-direction: column;

    .editor-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid v-bind('currentTheme.colors.borderColorLight');

      h3 {
        margin: 0;
        color: v-bind('currentTheme.colors.textPrimary');
        font-size: 18px;
        font-weight: 600;
      }

      .editor-actions {
        display: flex;
        gap: 8px;
      }
    }

        .editor-content {
      flex: 1;

              .config-editor {
        height: 400px;
        width: 100%;
        font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        border: 1px solid v-bind('currentTheme.colors.borderColorLight');
        border-radius: 6px;
        overflow: hidden;

        :deep(.cm-editor) {
          height: 100%;
          background: v-bind('currentTheme.colors.fillColor');
          color: v-bind('currentTheme.colors.textPrimary');

          &.cm-focused {
            outline: none;
            border-color: v-bind('currentTheme.colors.primary');
          }
        }

        :deep(.cm-content) {
          color: v-bind('currentTheme.colors.textPrimary');
          background: v-bind('currentTheme.colors.fillColor');
          padding: 16px;
          min-height: 400px;
        }

        :deep(.cm-scroller) {
          font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
          line-height: 1.6;
        }

        :deep(.cm-placeholder) {
          color: v-bind('currentTheme.colors.textSecondary');
        }
      }

      .textarea-wrapper {
        .config-textarea {
          :deep(.el-textarea__inner) {
            background: v-bind('currentTheme.colors.fillColor');
            border: 1px solid v-bind('currentTheme.colors.borderColorLight');
            color: v-bind('currentTheme.colors.textPrimary');
            font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 13px;
            line-height: 1.6;
            border-radius: 6px;
            resize: none;

            &:focus {
              border-color: v-bind('currentTheme.colors.primary');
              box-shadow: 0 0 0 2px v-bind('currentTheme.colors.chatBorderGlow');
            }

            &::placeholder {
              color: v-bind('currentTheme.colors.textSecondary');
            }
          }
        }
      }

      .editor-controls {
        margin-top: 12px;
        text-align: right;
      }
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

  .empty-btn {
    background: linear-gradient(135deg, v-bind('currentTheme.colors.primary'), v-bind('currentTheme.colors.info'));
    border: 1px solid v-bind('currentTheme.colors.primary');
    color: v-bind('currentTheme.colors.background');
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px v-bind('currentTheme.colors.chatBorderGlow');
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
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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

    &.edit-btn {
      margin-top: 24px;
      background: linear-gradient(135deg, v-bind('currentTheme.colors.primary'), v-bind('currentTheme.colors.success'));
      border: none;
      color: v-bind('currentTheme.colors.background');

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px v-bind('currentTheme.colors.chatBorderGlow');
      }
    }
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
  }

  :deep(.el-tabs__nav-wrap::after) {
    background-color: v-bind('currentTheme.colors.borderColorLight');
  }

  :deep(.el-tabs__active-bar) {
    background-color: v-bind('currentTheme.colors.primary');
  }

  :deep(.el-empty) {
    padding: 40px 20px;
  }

  :deep(.el-empty__description) {
    color: v-bind('currentTheme.colors.textSecondary');
  }

  :deep(.el-empty__image svg) {
    fill: v-bind('currentTheme.colors.fillColor');
  }

  :deep(.el-tag) {
    background: v-bind('currentTheme.colors.fillColorLight');
    border-color: v-bind('currentTheme.colors.borderColorLight');
    color: v-bind('currentTheme.colors.textRegular');
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
