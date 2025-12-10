<template>
  <div class="file-manager">
    <div class="header">
      <h2>文件管理器</h2>
      <div class="path-input">
        <el-button 
          v-if="mode === 'local'" 
          type="primary" 
          @click="handleSelectDirectory" 
          :loading="loading"
        >
          <el-icon><FolderOpened /></el-icon>
          选择目录
        </el-button>
        <el-input
          v-else
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
          <el-button size="small" @click="showCreateDialog = true" :disabled="!currentPath">
            <el-icon><Plus /></el-icon> 新建
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
          <el-icon :size="80" color="#ccc"><DocumentCopy /></el-icon>
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
import { ref, computed, watch, nextTick } from 'vue'
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
  Close
} from '@element-plus/icons-vue'
import Codemirror from 'codemirror-editor-vue3'
import 'codemirror/lib/codemirror.css'
import 'codemirror/theme/material.css'
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

import type { FileInfo, IFileSystemAdapter, DirectoryStackItem } from './types'

// Props
interface Props {
  adapter: IFileSystemAdapter
  mode?: 'local' | 'remote'
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
    currentPath.value = props.mode === 'local' 
      ? directoryStack.value.map(d => d.name).join('/')
      : targetDir.path || ''
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
    if (props.mode === 'local') {
      currentDirHandle.value = file.handle
    }
    const newPath = props.mode === 'local' 
      ? file.name 
      : currentPath.value + '/' + file.name
    directoryStack.value.push({ 
      name: file.name, 
      handle: file.handle,
      path: newPath
    })
    currentPath.value = props.mode === 'local'
      ? directoryStack.value.map(d => d.name).join('/')
      : newPath
    await loadDirectory()
    emit('directoryChanged', currentPath.value)
  } else {
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
    
    // 刷新文件列表
    await loadDirectory()
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
    } else {
      await props.adapter.deleteFile(file)
    }
    
    ElMessage.success('删除成功')
    if (editingFile.value?.path === file.path) {
      closeEditor()
    }
    await loadDirectory()
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
    await loadDirectory()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    creating.value = false
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
  background: #f5f7fa;

  .header {
    padding: 20px;
    background: white;
    border-bottom: 1px solid #e4e7ed;

    h2 {
      margin: 0 0 16px 0;
      font-size: 24px;
      color: #303133;
    }

    .path-input {
      display: flex;
      align-items: center;
      gap: 16px;

      .current-path {
        color: #606266;
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
      background: white;
      border-right: 1px solid #e4e7ed;
      display: flex;
      flex-direction: column;

      .toolbar {
        padding: 12px;
        border-bottom: 1px solid #e4e7ed;
        display: flex;
        gap: 8px;
      }

      .breadcrumb {
        padding: 12px;
        border-bottom: 1px solid #e4e7ed;
        background: #fafafa;

        .breadcrumb-item {
          cursor: pointer;
          &:hover {
            color: #409eff;
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
            background: #f5f7fa;
          }

          &.active {
            background: #ecf5ff;
            border-left: 3px solid #409eff;
          }

          &.directory {
            .file-name {
              font-weight: 500;
              color: #409eff;
            }
          }

          .file-icon {
            font-size: 20px;
            margin-right: 10px;
            color: #909399;
          }

          .file-name {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            color: #606266;
          }

          .file-size {
            font-size: 12px;
            color: #909399;
            margin-right: 8px;
          }

          .file-actions {
            opacity: 0;
            transition: opacity 0.2s;
            cursor: pointer;

            &:hover {
              color: #409eff;
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
      background: white;

      .editor-header {
        padding: 12px 20px;
        border-bottom: 1px solid #e4e7ed;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .file-info {
          display: flex;
          align-items: center;
          gap: 10px;

          .editing-file-name {
            font-weight: 500;
            color: #303133;
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

        :deep(.CodeMirror) {
          height: 100% !important;
          font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', monospace;
          font-size: 14px;
        }

        :deep(.CodeMirror-scroll) {
          height: 100%;
        }
      }
    }

    .empty-editor {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      background: white;
    }
  }
}
</style>
