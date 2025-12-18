<template>
  <div class="file-manager">
    <div class="header">
      <div class="header-title">
        <h2>文件管理器</h2>
        <el-tag 
          v-if="mode === 'websocket'" 
          :type="isWebSocketConnected ? 'success' : 'danger'"
          size="small"
          class="connection-status"
        >
          {{ isWebSocketConnected ? '已连接' : '未连接' }}
        </el-tag>
      </div>
      <div class="path-input">
        <el-button 
          v-if="mode === 'local' || mode === 'websocket'" 
          type="primary" 
          @click="handleSelectDirectory" 
          :loading="loading"
        >
          <el-icon><FolderOpened /></el-icon>
          选择目录
        </el-button>
        <el-input
          v-else-if="mode === 'remote'"
          v-model="inputPath"
          placeholder="请输入目录路径"
          @keyup.enter="handleLoadDirectory"
        >
          <template #prepend>
            <el-icon><FolderOpened /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleLoadDirectory" :loading="loading">加载</el-button>
          </template>
        </el-input>
        <span v-if="currentPath" class="current-path">
          当前目录: {{ currentPath }}
        </span>
      </div>
    </div>

    <div class="content">
      <!-- 左侧文件列表 -->
      <div class="file-list">
        <div class="toolbar">
          <el-button size="small" @click="handleRefresh" :disabled="loading">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
          <!-- <el-button size="small" @click="showCreateDialog = true" :disabled="!currentPath">
            <el-icon><Plus /></el-icon> 新建
          </el-button> -->
          <el-button 
            v-if="mode === 'websocket'" 
            size="small" 
            @click="handleSyncToLocal" 
            :disabled="!currentPath || syncing"
            :loading="syncing"
          >
            <el-icon><Download /></el-icon> 同步到本地
          </el-button>
        </div>

        <div class="breadcrumb" v-if="pathParts.length > 0">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item
              v-for="(part, index) in pathParts"
              :key="index"
              @click="navigateToPath(index)"
              class="breadcrumb-item"
            >
              {{ part || '根目录' }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="files" v-loading="loading">
          <div
            v-for="file in files"
            :key="file.path"
            class="file-item"
            :class="{ active: selectedFile?.path === file.path, directory: file.isDirectory }"
            @click="handleFileClick(file)"
            @dblclick="handleFileDblClick(file)"
          >
            <el-icon class="file-icon">
              <Folder v-if="file.isDirectory" />
              <Document v-else />
            </el-icon>
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size" v-if="!file.isDirectory">
              {{ formatSize(file.size) }}
            </span>
            <el-dropdown @command="(cmd: string) => handleFileAction(cmd, file)" trigger="click">
              <el-icon class="file-actions" @click.stop>
                <MoreFilled />
              </el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="file.isDirectory" command="open">
                    <el-icon><FolderOpened /></el-icon> 打开
                  </el-dropdown-item>
                  <el-dropdown-item v-else command="edit">
                    <el-icon><Edit /></el-icon> 编辑
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <el-icon><Delete /></el-icon> 删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <el-empty v-if="!loading && files.length === 0" description="当前目录为空" />
        </div>
      </div>

      <!-- 右侧编辑器 -->
      <div class="editor-panel" v-if="editingFile">
        <div class="editor-header">
          <div class="file-info">
            <el-icon><Document /></el-icon>
            <span class="editing-file-name">{{ editingFile.name }}</span>
            <el-tag v-if="isModified" size="small" type="warning">已修改</el-tag>
          </div>
          <div class="editor-actions">
            <el-button size="small" @click="saveFile" :loading="saving" type="primary">
              <el-icon><Check /></el-icon> 保存
            </el-button>
            <el-button size="small" @click="closeEditor">
              <el-icon><Close /></el-icon> 关闭
            </el-button>
          </div>
        </div>
        <div class="editor-content">
          <Codemirror
            v-model:value="fileContent"
            :options="cmOptions"
            style="height: 100%;"
          />
        </div>
      </div>

      <div class="empty-editor" v-else>
        <el-empty description="请选择文件进行编辑">
          <template #image>
            <el-icon :size="80" class="empty-icon"><DocumentCopy /></el-icon>
          </template>
        </el-empty>
      </div>
    </div>

    <!-- 新建文件/文件夹对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="createType === 'file' ? '新建文件' : '新建文件夹'"
      width="500px"
    >
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="createType">
            <el-radio label="file">文件</el-radio>
            <el-radio label="directory">文件夹</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="createForm.name" placeholder="请输入名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createFileOrDir" :loading="creating">
          创建
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  FolderOpened,
  Folder,
  Document,
  DocumentCopy,
  Refresh,
  Plus,
  Edit,
  Delete,
  MoreFilled,
  Check,
  Close,
  Download
} from '@element-plus/icons-vue'
import Codemirror from 'codemirror-editor-vue3'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/material.css'
import 'codemirror/theme/elegant.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/mode/css/css.js'
import 'codemirror/mode/xml/xml.js'
import 'codemirror/mode/markdown/markdown.js'
import 'codemirror/mode/python/python.js'
import 'codemirror/mode/shell/shell.js'
import 'codemirror/mode/sql/sql.js'
import 'codemirror/mode/yaml/yaml.js'
import 'codemirror/mode/go/go.js'
import 'codemirror/mode/clike/clike.js'

import type { FileInfo, IFileSystemAdapter, DirectoryStackItem, WSMessageType } from './types'
import { WSMessageType as MsgType } from './types'
import { useTheme } from '@/styles/theme/useTheme'
import { WebSocketFileSystemAdapter } from './adapters/WebSocketFileSystemAdapter'

// Props
interface Props {
  adapter: IFileSystemAdapter
  mode?: 'local' | 'remote' | 'websocket'
  initialPath?: string
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'local',
  initialPath: ''
})

// Emits
const emit = defineEmits<{
  fileSelected: [file: FileInfo]
  fileOpened: [file: FileInfo]
  fileSaved: [file: FileInfo]
  fileDeleted: [file: FileInfo]
  directoryChanged: [path: string]
}>()

// 获取主题
const { currentTheme } = useTheme()

// 状态管理
const inputPath = ref(props.initialPath)
const currentPath = ref('')
const currentDirHandle = ref<any>(null)
const directoryStack = ref<DirectoryStackItem[]>([])
const files = ref<FileInfo[]>([])
const selectedFile = ref<FileInfo | null>(null)
const editingFile = ref<FileInfo | null>(null)
const fileContent = ref('')
const originalContent = ref('')
const isModified = ref(false)
const loading = ref(false)
const saving = ref(false)
const creating = ref(false)
const syncing = ref(false)

// 新建对话框
const showCreateDialog = ref(false)
const createType = ref<'file' | 'directory'>('file')
const createForm = ref({
  name: ''
})

// CodeMirror 配置
const cmOptions = ref({
  mode: 'text/plain',
  theme: 'material',
  lineNumbers: true,
  line: true,
  lineWrapping: false,
  indentUnit: 2,
  tabSize: 2,
  readOnly: false
})

// 监听主题变化，动态切换编辑器主题
watch(
  () => currentTheme.value.name,
  (themeName) => {
    if (themeName.toLowerCase().includes('dark') || themeName.toLowerCase().includes('cyberpunk')) {
      cmOptions.value.theme = 'material'
    } else {
      cmOptions.value.theme = 'elegant'
    }
  },
  { immediate: true }
)

// WebSocket相关
const isWebSocketMode = computed(() => props.adapter instanceof WebSocketFileSystemAdapter)
const isWebSocketConnected = computed(() => {
  if (!isWebSocketMode.value) return false
  const wsAdapter = props.adapter as WebSocketFileSystemAdapter
  return wsAdapter.isWebSocketConnected()
})

// 组件挂载时初始化
onMounted(() => {
  // 为adapter设置操作完成回调（无论是WebSocket还是本地模式）
  if (props.adapter.setOperationCompleteCallback) {
    props.adapter.setOperationCompleteCallback(() => {
      console.log('[FileManager] Auto-refreshing after operation')
      loadDirectory()
    })
  }
  
  // 如果是WebSocket模式且有初始路径，加载目录
  if (isWebSocketMode.value && props.initialPath) {
    currentPath.value = props.initialPath
    loadDirectory()
  }
})

// 组件卸载时清理
onBeforeUnmount(() => {
  // 清理工作
})

// 文本文件扩展名
const textFileExtensions = [
  'txt', 'js', 'ts', 'jsx', 'tsx', 'vue', 'json', 'xml', 'html', 'css', 'scss', 'sass', 'less',
  'md', 'markdown', 'py', 'java', 'c', 'cpp', 'h', 'hpp', 'go', 'rs', 'sh', 'bash', 'zsh',
  'yml', 'yaml', 'toml', 'ini', 'conf', 'config', 'log', 'sql', 'php', 'rb', 'swift', 'kt',
  'gradle', 'properties', 'env', 'gitignore', 'dockerfile', 'makefile', 'csv', 'tsv'
]

// 路径分段
const pathParts = computed(() => {
  if (!currentPath.value) return []
  return currentPath.value.split('/').filter(p => p)
})

// 检查是否为文本文件
function isTextFile(filename: string): boolean {
  const ext = filename.split('.').pop()?.toLowerCase()
  if (!ext) return false
  return textFileExtensions.includes(ext)
}

// 根据文件扩展名获取 CodeMirror 模式
function getEditorMode(filename: string): string {
  const ext = filename.split('.').pop()?.toLowerCase()
  
  const modeMap: Record<string, string> = {
    'js': 'text/javascript',
    'jsx': 'text/jsx',
    'ts': 'text/typescript',
    'tsx': 'text/typescript-jsx',
    'json': 'application/json',
    'html': 'text/html',
    'xml': 'text/xml',
    'css': 'text/css',
    'scss': 'text/x-scss',
    'less': 'text/x-less',
    'md': 'text/x-markdown',
    'markdown': 'text/x-markdown',
    'py': 'text/x-python',
    'java': 'text/x-java',
    'c': 'text/x-csrc',
    'cpp': 'text/x-c++src',
    'h': 'text/x-csrc',
    'hpp': 'text/x-c++src',
    'go': 'text/x-go',
    'sh': 'text/x-sh',
    'bash': 'text/x-sh',
    'zsh': 'text/x-sh',
    'yml': 'text/x-yaml',
    'yaml': 'text/x-yaml',
    'sql': 'text/x-sql',
    'vue': 'text/html'
  }
  
  return modeMap[ext || ''] || 'text/plain'
}

// 监听文件内容变化
let isInitializing = false
watch(
  () => fileContent.value,
  (newVal) => {
    // 如果是初始化加载，不标记为已修改
    if (!isInitializing && editingFile.value) {
      if (newVal !== originalContent.value) {
        isModified.value = true
      } else {
        isModified.value = false
      }
    }
  }
)

// 格式化文件大小
function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 选择目录
async function handleSelectDirectory() {
  try {
    const result = await props.adapter.selectDirectory()
    currentPath.value = result.path
    currentDirHandle.value = result.handle
    directoryStack.value = [{ name: result.path, handle: result.handle, path: result.path }]
    
    await loadDirectory()
    emit('directoryChanged', currentPath.value)
  } catch (error: any) {
    if (error.name !== 'AbortError') {
      ElMessage.error(error.message)
    }
  }
}

// 加载目录（远程模式）
async function handleLoadDirectory() {
  if (!inputPath.value) {
    ElMessage.warning('请输入目录路径')
    return
  }
  
  currentPath.value = inputPath.value
  directoryStack.value = inputPath.value.split('/').filter(p => p).map((name, index, arr) => ({
    name,
    path: '/' + arr.slice(0, index + 1).join('/')
  }))
  
  await loadDirectory()
  emit('directoryChanged', currentPath.value)
}

// 刷新
async function handleRefresh() {
  await loadDirectory()
}

// 加载目录内容
async function loadDirectory() {
  if (!currentPath.value) {
    return
  }

  loading.value = true
  try {
    files.value = await props.adapter.listDirectory(currentPath.value, currentDirHandle.value)
    selectedFile.value = null
  } catch (error: any) {
    ElMessage.error(error.message || '加载目录失败')
  } finally {
    loading.value = false
  }
}

// 导航到路径
async function navigateToPath(index: number) {
  if (index < directoryStack.value.length) {
    directoryStack.value = directoryStack.value.slice(0, index + 1)
    const targetDir = directoryStack.value[index]
    currentDirHandle.value = targetDir.handle
    currentPath.value = directoryStack.value.map(d => d.name).join('/')
    await loadDirectory()
    emit('directoryChanged', currentPath.value)
  }
}

// 处理文件点击
function handleFileClick(file: FileInfo) {
  selectedFile.value = file
  emit('fileSelected', file)
}

// 处理文件双击
async function handleFileDblClick(file: FileInfo) {
  if (file.isDirectory) {
    currentDirHandle.value = file.handle
    const newPath = file.name
    directoryStack.value.push({ 
      name: file.name, 
      handle: file.handle,
      path: newPath
    })
    currentPath.value = directoryStack.value.map(d => d.name).join('/')
    await loadDirectory()
    emit('directoryChanged', currentPath.value)
  } else {
    file.path = currentPath.value.split("/").slice(1).join("/") + '/' + file.path
    openFile(file)
  }
}

// 处理文件操作
async function handleFileAction(command: string, file: FileInfo) {
  switch (command) {
    case 'open':
      if (file.isDirectory) {
        await handleFileDblClick(file)
      }
      break
    case 'edit':
      file.path = currentPath.value.split("/").slice(1).join("/") + '/' + file.path
      openFile(file)
      break
    case 'delete':
      confirmDelete(file)
      break
  }
}

// 打开文件进行编辑
async function openFile(file: FileInfo) {
  // 检查是否为文本文件
  if (!isTextFile(file.name)) {
    ElMessage.warning('不支持打开非文本文件')
    return
  }

  if (isModified.value) {
    try {
      await ElMessageBox.confirm(
        '当前文件已修改，是否保存？',
        '提示',
        {
          confirmButtonText: '保存',
          cancelButtonText: '不保存',
          type: 'warning',
          distinguishCancelAndClose: true
        }
      )
      await saveFile()
    } catch (action) {
      if (action === 'cancel') {
        // 用户选择不保存，继续打开新文件
      } else {
        // 用户取消操作
        return
      }
    }
  }

  loading.value = true
  try {
    const content = await props.adapter.readFile(file)
    
    // 设置初始化标志，防止触发 watch
    isInitializing = true
    
    editingFile.value = file
    fileContent.value = content
    originalContent.value = content
    isModified.value = false
    
    // 设置编辑器模式
    cmOptions.value.mode = getEditorMode(file.name)
    
    // 等待下一个 tick 后解除初始化标志
    await nextTick()
    setTimeout(() => {
      isInitializing = false
    }, 100)
    
    emit('fileOpened', file)
  } catch (error: any) {
    ElMessage.error(error.message || '读取文件失败')
  } finally {
    loading.value = false
  }
}

// 保存文件
async function saveFile() {
  if (!editingFile.value) return

  saving.value = true
  try {
    await props.adapter.writeFile(editingFile.value, fileContent.value)
    
    ElMessage.success('保存成功')
    originalContent.value = fileContent.value
    isModified.value = false
    
    // 刷新操作会由adapter的回调自动触发
    emit('fileSaved', editingFile.value)
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 关闭编辑器
async function closeEditor() {
  if (isModified.value) {
    try {
      await ElMessageBox.confirm(
        '当前文件已修改，是否保存？',
        '提示',
        {
          confirmButtonText: '保存',
          cancelButtonText: '不保存',
          type: 'warning',
          distinguishCancelAndClose: true
        }
      )
      await saveFile()
    } catch (action) {
      if (action === 'cancel') {
        // 用户选择不保存，继续关闭
      } else {
        // 用户取消操作
        return
      }
    }
  }

  editingFile.value = null
  fileContent.value = ''
  originalContent.value = ''
  isModified.value = false
  isInitializing = false
}

// 确认删除
async function confirmDelete(file: FileInfo) {
  try {
    await ElMessageBox.confirm(
      `确定要删除 ${file.isDirectory ? '文件夹' : '文件'} "${file.name}" 吗？`,
      '警告',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await performDelete(file)
  } catch {
    // 用户取消
  }
}

// 执行删除
async function performDelete(file: FileInfo) {
  try {
    if (props.mode === 'local' && currentDirHandle.value) {
      await currentDirHandle.value.removeEntry(file.name, { recursive: file.isDirectory })
      // 本地模式删除后手动刷新
      await loadDirectory()
    } else {
      // 使用adapter删除（会自动触发刷新回调）
      await props.adapter.deleteFile(file)
    }
    
    ElMessage.success('删除成功')
    if (editingFile.value?.path === file.path) {
      closeEditor()
    }
    emit('fileDeleted', file)
  } catch (error: any) {
    ElMessage.error(error.message || '删除失败')
  }
}

// 创建文件或文件夹
async function createFileOrDir() {
  if (!createForm.value.name) {
    ElMessage.warning('请输入名称')
    return
  }

  if (!currentPath.value) {
    ElMessage.warning('请先选择目录')
    return
  }

  creating.value = true
  try {
    if (createType.value === 'directory') {
      await props.adapter.createDirectory(currentPath.value, createForm.value.name, currentDirHandle.value)
      ElMessage.success('创建文件夹成功')
    } else {
      await props.adapter.createFile(currentPath.value, createForm.value.name, currentDirHandle.value)
      ElMessage.success('创建文件成功')
    }
    
    showCreateDialog.value = false
    createForm.value.name = ''
    // 刷新操作会由adapter的回调自动触发
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    creating.value = false
  }
}

// 同步到本地
async function handleSyncToLocal() {
  if (!isWebSocketMode.value) {
    ElMessage.warning('仅WebSocket模式支持同步到本地')
    return
  }

  if (!currentPath.value) {
    ElMessage.warning('请先选择目录')
    return
  }

  try {
    // 请求用户选择本地保存目录
    const localDirHandle = await (window as any).showDirectoryPicker({
      mode: 'readwrite'
    })

    syncing.value = true
    
    // 获取远程文件列表
    const wsAdapter = props.adapter as WebSocketFileSystemAdapter
    const rootHandle = wsAdapter.getRootDirHandle()
    
    if (!rootHandle) {
      ElMessage.error('未选择远程目录')
      return
    }

    let syncedCount = 0
    let errorCount = 0

    // 递归同步文件
    const syncDirectory = async (
      sourceHandle: FileSystemDirectoryHandle,
      targetHandle: FileSystemDirectoryHandle,
      relativePath: string = ''
    ) => {
      try {
        // @ts-ignore
        for await (const entry of sourceHandle.values()) {
          try {
            const entryPath = relativePath ? `${relativePath}/${entry.name}` : entry.name
            
            if (entry.kind === 'directory') {
              // 创建目录
              const newDirHandle = await targetHandle.getDirectoryHandle(entry.name, { create: true })
              await syncDirectory(entry as FileSystemDirectoryHandle, newDirHandle, entryPath)
            } else {
              // 复制文件
              const sourceFile = await (entry as FileSystemFileHandle).getFile()
              const targetFileHandle = await targetHandle.getFileHandle(entry.name, { create: true })
              const writable = await targetFileHandle.createWritable()
              await writable.write(await sourceFile.arrayBuffer())
              await writable.close()
              syncedCount++
              console.log(`已同步: ${entryPath}`)
            }
          } catch (error: any) {
            console.error(`同步失败: ${entry.name}`, error)
            errorCount++
          }
        }
      } catch (error: any) {
        console.error('同步目录失败:', error)
        throw error
      }
    }

    await syncDirectory(rootHandle, localDirHandle)
    
    ElMessage.success(`同步完成！成功: ${syncedCount} 个文件${errorCount > 0 ? `，失败: ${errorCount}` : ''}`)
  } catch (error: any) {
    if (error.name === 'AbortError') {
      // 用户取消
      return
    }
    console.error('同步失败:', error)
    ElMessage.error(error.message || '同步失败')
  } finally {
    syncing.value = false
  }
}

// 暴露方法给父组件
defineExpose({
  loadDirectory,
  refresh: handleRefresh,
  closeEditor
})
</script>

<style lang="scss" scoped>
.file-manager {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color-page);

  // 确保所有图标使用主题颜色
  :deep(.el-icon) {
    color: inherit;
  }

  // 按钮主题适配
  :deep(.el-button) {
    transition: all 0.3s;

    &:not(.el-button--primary) {
      background-color: var(--el-fill-color-light);
      border-color: var(--el-border-color);
      color: var(--el-text-color-regular);

      &:hover:not(:disabled) {
        background-color: var(--el-fill-color);
        border-color: var(--el-color-primary-light-7);
        color: var(--el-color-primary);
      }

      &:disabled {
        background-color: var(--el-fill-color-light);
        border-color: var(--el-border-color-lighter);
        color: var(--el-text-color-placeholder);
      }
    }
  }

  .header {
    padding: 20px;
    background: var(--el-bg-color);
    border-bottom: 1px solid var(--el-border-color);

    .header-title {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      h2 {
        margin: 0;
        font-size: 24px;
        color: var(--el-text-color-primary);
      }

      .connection-status {
        font-size: 12px;
      }
    }

    h2 {
      margin: 0 0 16px 0;
      font-size: 24px;
      color: var(--el-text-color-primary);
    }

    .path-input {
      display: flex;
      align-items: center;
      gap: 16px;

      .current-path {
        color: var(--el-text-color-regular);
        font-size: 14px;
      }
    }
  }

  .content {
    flex: 1;
    display: flex;
    overflow: hidden;

    .file-list {
      width: 400px;
      background: var(--el-bg-color);
      border-right: 1px solid var(--el-border-color);
      display: flex;
      flex-direction: column;

      .toolbar {
        padding: 12px;
        border-bottom: 1px solid var(--el-border-color);
        display: flex;
        gap: 8px;
      }

      .breadcrumb {
        padding: 12px;
        border-bottom: 1px solid var(--el-border-color);
        background: var(--el-fill-color-light);

        .breadcrumb-item {
          cursor: pointer;
          color: var(--el-text-color-regular);
          
          &:hover {
            color: var(--el-color-primary);
          }
        }
      }

      .files {
        flex: 1;
        overflow-y: auto;
        padding: 8px;

        .file-item {
          display: flex;
          align-items: center;
          padding: 10px 12px;
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.2s;
          position: relative;

          &:hover {
            background: var(--el-fill-color);
          }

          &.active {
            background: var(--el-color-primary-light-9);
            border-left: 3px solid var(--el-color-primary);
            padding-left: 9px;
            box-shadow: 0 0 8px rgba(64, 158, 255, 0.2);

            .file-name {
              color: var(--el-color-primary);
              font-weight: 500;
            }

            .file-icon {
              color: var(--el-color-primary);
            }
          }

          &.directory {
            .file-name {
              font-weight: 500;
              color: var(--el-color-primary);
            }

            .file-icon {
              color: var(--el-color-primary);
            }

            &.active {
              .file-name {
                color: var(--el-color-primary);
              }
            }
          }

          .file-icon {
            font-size: 20px;
            margin-right: 10px;
            color: var(--el-text-color-secondary);
            transition: color 0.2s;
          }

          .file-name {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: var(--el-text-color-regular);
          }

          .file-size {
            font-size: 12px;
            color: var(--el-text-color-secondary);
            margin-right: 8px;
          }

          .file-actions {
            opacity: 0;
            transition: opacity 0.2s;
            cursor: pointer;
            color: var(--el-text-color-secondary);

            &:hover {
              color: var(--el-color-primary);
            }
          }

          &:hover .file-actions {
            opacity: 1;
          }
        }
      }
    }

    .editor-panel {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: var(--el-bg-color);

      .editor-header {
        padding: 12px 20px;
        border-bottom: 1px solid var(--el-border-color);
        display: flex;
        justify-content: space-between;
        align-items: center;

        .file-info {
          display: flex;
          align-items: center;
          gap: 10px;

          .editing-file-name {
            font-weight: 500;
            color: var(--el-text-color-primary);
          }
        }

        .editor-actions {
          display: flex;
          gap: 8px;
        }
      }

      .editor-content {
        flex: 1;
        overflow: hidden;
        position: relative;
        background: var(--el-bg-color);

        :deep(.CodeMirror) {
          height: 100% !important;
          font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', monospace;
          font-size: 14px;
          background: var(--el-bg-color);
          color: var(--el-text-color-primary);
        }

        :deep(.CodeMirror-scroll) {
          height: 100%;
        }

        :deep(.CodeMirror-gutters) {
          background: var(--el-bg-color);
          border-right: 1px solid var(--el-border-color);
        }

        :deep(.CodeMirror-linenumber) {
          color: var(--el-text-color-secondary);
        }

        :deep(.CodeMirror-cursor) {
          border-left-color: var(--el-text-color-primary);
        }
      }
    }

    .empty-editor {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--el-bg-color);

      .empty-icon {
        color: var(--el-text-color-placeholder);
      }

      :deep(.el-empty) {
        padding: 40px 0;
      }

      :deep(.el-empty__description) {
        color: var(--el-text-color-secondary);
        margin-top: 16px;
      }

      :deep(.el-empty__image) {
        svg {
          fill: var(--el-fill-color-dark);
        }
      }
    }
  }

  // 全局el-empty主题适配
  :deep(.el-empty) {
    .el-empty__description {
      color: var(--el-text-color-secondary);
    }

    .el-empty__image {
      svg {
        fill: var(--el-fill-color-dark);
      }
    }
  }

  // 下拉菜单主题适配
  :deep(.el-dropdown-menu) {
    background-color: var(--el-bg-color-overlay);
    border: 1px solid var(--el-border-color-light);

    .el-dropdown-menu__item {
      color: var(--el-text-color-regular);

      &:hover {
        background-color: var(--el-fill-color-light);
        color: var(--el-color-primary);
      }

      .el-icon {
        color: var(--el-text-color-secondary);
      }
    }
  }
}

// Dark主题特殊优化
html.dark,
html[data-theme="dark"],
.dark {
  .file-manager {
    .file-list {
      .file-item {
        &.active {
          background: rgba(64, 158, 255, 0.15);
          border-left-color: var(--el-color-primary);
          box-shadow: 0 0 12px rgba(64, 158, 255, 0.3);

          .file-name {
            color: var(--el-color-primary-light-3);
          }

          .file-icon {
            color: var(--el-color-primary-light-3);
          }

          .file-size {
            color: var(--el-text-color-secondary);
          }
        }

        &:hover:not(.active) {
          background: rgba(255, 255, 255, 0.05);
        }
      }
    }

    .breadcrumb {
      background: rgba(255, 255, 255, 0.03);
    }
  }
}
</style>
