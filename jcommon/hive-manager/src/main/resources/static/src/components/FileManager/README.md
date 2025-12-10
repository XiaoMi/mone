# FileManager 组件使用文档

## 概述

FileManager 是一个功能完整的文件管理器组件，支持本地文件系统和远程API两种模式。

## 组件结构

```
components/FileManager/
├── types.ts                          # 类型定义
├── index.ts                          # 组件入口
├── FileManagerCore.vue               # 核心组件
└── adapters/                         # 适配器
    ├── LocalFileSystemAdapter.ts    # 本地文件系统适配器
    └── RemoteFileSystemAdapter.ts   # 远程API适配器
```

## 快速开始

### 1. 使用本地文件系统模式

```vue
<template>
  <FileManagerCore :adapter="adapter" :mode="local" />
</template>

<script setup>
import { FileManagerCore, LocalFileSystemAdapter } from '@/components/FileManager'

const adapter = new LocalFileSystemAdapter()
</script>
```

### 2. 使用远程API模式

```vue
<template>
  <FileManagerCore 
    :adapter="adapter" 
    :mode="remote"
    :initial-path="/home/user/documents"
  />
</template>

<script setup>
import { FileManagerCore, RemoteFileSystemAdapter } from '@/components/FileManager'

const adapter = new RemoteFileSystemAdapter()
</script>
```

## API

### Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| adapter | 文件系统适配器实例 | IFileSystemAdapter | - |
| mode | 工作模式 | 'local' \| 'remote' | 'local' |
| initialPath | 初始路径（远程模式） | string | '' |

### Events

| 事件名 | 说明 | 参数 |
|--------|------|------|
| fileSelected | 文件被选中 | (file: FileInfo) |
| fileOpened | 文件被打开 | (file: FileInfo) |
| fileSaved | 文件被保存 | (file: FileInfo) |
| fileDeleted | 文件被删除 | (file: FileInfo) |
| directoryChanged | 目录切换 | (path: string) |

### Expose Methods

| 方法名 | 说明 | 参数 |
|--------|------|------|
| loadDirectory | 重新加载当前目录 | - |
| refresh | 刷新文件列表 | - |
| closeEditor | 关闭编辑器 | - |

## 自定义适配器

实现 `IFileSystemAdapter` 接口来创建自定义适配器：

```typescript
import type { IFileSystemAdapter, FileInfo } from '@/components/FileManager'

export class CustomAdapter implements IFileSystemAdapter {
  async selectDirectory(): Promise<{ path: string; handle?: any }> {
    // 实现目录选择逻辑
  }
  
  async listDirectory(path: string, handle?: any): Promise<FileInfo[]> {
    // 实现目录列表获取逻辑
  }
  
  async readFile(file: FileInfo): Promise<string> {
    // 实现文件读取逻辑
  }
  
  async writeFile(file: FileInfo, content: string): Promise<void> {
    // 实现文件写入逻辑
  }
  
  async deleteFile(file: FileInfo): Promise<void> {
    // 实现文件删除逻辑
  }
  
  async createDirectory(parentPath: string, name: string, parentHandle?: any): Promise<void> {
    // 实现目录创建逻辑
  }
  
  async createFile(parentPath: string, name: string, parentHandle?: any): Promise<void> {
    // 实现文件创建逻辑
  }
}
```

## 完整示例

```vue
<template>
  <div>
    <el-button @click="switchMode">
      切换到 {{ mode === 'local' ? '远程' : '本地' }} 模式
    </el-button>
    
    <FileManagerCore 
      ref="fileManagerRef"
      :adapter="currentAdapter" 
      :mode="mode"
      @file-opened="handleFileOpened"
      @file-saved="handleFileSaved"
      @directory-changed="handleDirectoryChanged"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  FileManagerCore, 
  LocalFileSystemAdapter,
  RemoteFileSystemAdapter 
} from '@/components/FileManager'

const mode = ref('local')
const fileManagerRef = ref()

const localAdapter = new LocalFileSystemAdapter()
const remoteAdapter = new RemoteFileSystemAdapter()

const currentAdapter = computed(() => {
  return mode.value === 'local' ? localAdapter : remoteAdapter
})

function switchMode() {
  mode.value = mode.value === 'local' ? 'remote' : 'local'
  // 切换模式后关闭编辑器
  fileManagerRef.value?.closeEditor()
}

function handleFileOpened(file) {
  console.log('文件已打开:', file.name)
}

function handleFileSaved(file) {
  console.log('文件已保存:', file.name)
}

function handleDirectoryChanged(path) {
  console.log('目录已切换:', path)
}
</script>
```

## 特性

- ✅ 支持本地文件系统（File System Access API）
- ✅ 支持远程文件系统（HTTP API）
- ✅ CodeMirror 编辑器，支持多种语言语法高亮
- ✅ 文件类型检测，只打开文本文件
- ✅ 文件大小限制（5MB）
- ✅ 文件修改状态追踪
- ✅ 面包屑导航
- ✅ 创建、编辑、删除文件和目录
- ✅ 完全可定制的适配器模式

## 注意事项

1. **本地模式**需要浏览器支持 File System Access API（Chrome 86+、Edge 86+）
2. **远程模式**需要后端提供相应的API接口
3. 文件大小限制为 5MB，可在适配器中修改
4. 只支持文本文件编辑，二进制文件会提示不支持
