import type { IFileSystemAdapter, FileInfo, WSMessage, WSRequest, WSResponse, WSMessageType } from '../types'
import { WSMessageType as MsgType } from '../types'

/**
 * WebSocket控制的本地文件系统适配器
 * 本地浏览器打开目录，远程服务器通过WebSocket发送指令控制本地文件操作
 */
export class WebSocketFileSystemAdapter implements IFileSystemAdapter {
  private ws: WebSocket | null = null
  private isConnected = false
  private rootDirHandle: FileSystemDirectoryHandle | null = null
  private currentPath = ''
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectDelay = 3000
  private ignorePatterns: string[] = []
  private onOperationComplete?: () => void

  constructor(
    private wsUrl: string,
    private onConnected?: () => void,
    private onDisconnected?: () => void,
    private onError?: (error: Event) => void,
    ignorePatterns?: string[]
  ) {
    // 默认忽略常见的目录和文件
    this.ignorePatterns = ignorePatterns || [
      'node_modules',
      '.git',
      '.github',
      '.DS_Store',
      'dist',
      'build',
      'target',
      '.idea',
      '.vscode',
      '*.log',
      'coverage',
      '.next',
      '.nuxt',
      'out',
      'tmp',
      'temp'
    ]
  }

  /**
   * 连接到WebSocket服务器
   */
  async connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.ws = new WebSocket(this.wsUrl)

        this.ws.onopen = () => {
          console.log('[WebSocket] Connected to', this.wsUrl)
          this.isConnected = true
          this.reconnectAttempts = 0
          this.onConnected?.()
          resolve()
        }

        this.ws.onclose = () => {
          console.log('[WebSocket] Disconnected')
          this.isConnected = false
          this.onDisconnected?.()
          this.handleReconnect()
        }

        this.ws.onerror = (error) => {
          console.error('[WebSocket] Error:', error)
          this.onError?.(error)
          reject(error)
        }

        this.ws.onmessage = (event) => {
          this.handleMessage(event.data)
        }
      } catch (error) {
        reject(error)
      }
    })
  }

  /**
   * 断开连接
   */
  disconnect(): void {
    if (this.ws) {
      this.ws.close()
      this.ws = null
      this.isConnected = false
    }
  }

  /**
   * 重连逻辑
   */
  private handleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('[WebSocket] Max reconnect attempts reached')
      return
    }

    this.reconnectAttempts++
    console.log(`[WebSocket] Reconnecting... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)

    setTimeout(() => {
      this.connect().catch((error) => {
        console.error('[WebSocket] Reconnect failed:', error)
      })
    }, this.reconnectDelay)
  }

  /**
   * 处理接收到的WebSocket消息（服务器发来的指令）
   */
  private async handleMessage(data: string): Promise<void> {
    try {
      const message: WSRequest = JSON.parse(data)
      console.log('[WebSocket] Received command:', message)

      let response: WSResponse = {
        type: message.type,
        requestId: message.requestId,
        success: false,
      }

      let needsRefresh = false

      try {
        // 根据消息类型执行相应的本地文件操作
        switch (message.type) {
          case MsgType.LIST_DIRECTORY:
            response.data = await this.executeListDirectory(message.data.path || '')
            response.success = true
            break

          case MsgType.READ_FILE:
            response.data = await this.executeReadFile(message.data.path || '')
            response.success = true
            break

          case MsgType.WRITE_FILE:
            await this.executeWriteFile(message.data.path || '', message.data.content || '')
            response.success = true
            needsRefresh = true
            break

          case MsgType.DELETE_FILE:
            await this.executeDeleteFile(message.data.path || '', message.data.isDirectory || false)
            response.success = true
            needsRefresh = true
            break

          case MsgType.CREATE_DIRECTORY:
            await this.executeCreateDirectory(message.data.parentPath || '', message.data.name || '')
            response.success = true
            needsRefresh = true
            break

          case MsgType.CREATE_FILE:
            await this.executeCreateFile(message.data.parentPath || '', message.data.name || '')
            response.success = true
            needsRefresh = true
            break

          default:
            throw new Error(`Unknown message type: ${message.type}`)
        }
      } catch (error: any) {
        response.success = false
        response.error = error.message || 'Operation failed'
      }

      // 发送响应回服务器
      this.sendResponse(response)

      // 如果操作成功且需要刷新，通知UI更新
      if (response.success && needsRefresh && this.onOperationComplete) {
        console.log('[WebSocket] Triggering UI refresh after operation')
        this.onOperationComplete()
      }
    } catch (error) {
      console.error('[WebSocket] Failed to parse message:', error)
    }
  }

  /**
   * 发送响应到服务器
   */
  private sendResponse(response: WSResponse): void {
    if (this.ws && this.isConnected) {
      this.ws.send(JSON.stringify(response))
    }
  }

  /**
   * 发送通知到服务器
   */
  private sendNotification(type: WSMessageType, data: any): void {
    if (this.ws && this.isConnected) {
      const notification: WSMessage = { type, data }
      this.ws.send(JSON.stringify(notification))
    }
  }

  /**
   * 执行列出目录操作 - 递归获取完整目录结构
   */
  private async executeListDirectory(path: string): Promise<{ files: FileInfo[] }> {
    if (!this.rootDirHandle) {
      throw new Error('No directory selected')
    }

    const targetHandle = await this.getHandleByPath(path)
    if (!targetHandle || targetHandle.kind !== 'directory') {
      throw new Error('Invalid directory path')
    }

    const files: FileInfo[] = []
    await this.recursiveListDirectory(targetHandle as FileSystemDirectoryHandle, path, files)

    // 排序：目录优先，然后按名称
    files.sort((a, b) => {
      if (a.isDirectory && !b.isDirectory) return -1
      if (!a.isDirectory && b.isDirectory) return 1
      return a.path.localeCompare(b.path)
    })

    return { files }
  }

  /**
   * 递归列出目录中的所有文件和子目录
   */
  private async recursiveListDirectory(
    dirHandle: FileSystemDirectoryHandle,
    currentPath: string,
    files: FileInfo[]
  ): Promise<void> {
    // @ts-ignore - FileSystemDirectoryHandle values() method
    for await (const entry of dirHandle.values()) {
      try {
        // 检查是否应该忽略此文件/目录
        if (this.shouldIgnore(entry.name)) {
          console.log(`[Ignore] Skipping: ${entry.name}`)
          continue
        }

        const entryPath = currentPath ? `${currentPath}/${entry.name}` : entry.name
        let size = 0
        let lastModified = 0
        
        if (entry.kind === 'file') {
          const fileHandle = entry as FileSystemFileHandle
          const file_obj = await fileHandle.getFile()
          size = file_obj.size
          lastModified = file_obj.lastModified

          files.push({
            name: entry.name,
            path: entryPath,
            isDirectory: false,
            size,
            lastModified,
            handle: entry,
          })
        } else if (entry.kind === 'directory') {
          // 添加目录本身
          files.push({
            name: entry.name,
            path: entryPath,
            isDirectory: true,
            size: 0,
            lastModified: 0,
            handle: entry,
          })

          // 递归处理子目录
          await this.recursiveListDirectory(entry as FileSystemDirectoryHandle, entryPath, files)
        }
      } catch (e) {
        console.error('Error reading entry:', entry.name, e)
      }
    }
  }

  /**
   * 检查文件/目录名是否应该被忽略
   */
  private shouldIgnore(name: string): boolean {
    return this.ignorePatterns.some(pattern => {
      // 支持通配符模式
      if (pattern.includes('*')) {
        const regex = new RegExp('^' + pattern.replace(/\*/g, '.*') + '$')
        return regex.test(name)
      }
      // 精确匹配
      return name === pattern
    })
  }

  /**
   * 设置忽略模式
   */
  setIgnorePatterns(patterns: string[]): void {
    this.ignorePatterns = patterns
  }

  /**
   * 添加忽略模式
   */
  addIgnorePattern(pattern: string): void {
    if (!this.ignorePatterns.includes(pattern)) {
      this.ignorePatterns.push(pattern)
    }
  }

  /**
   * 移除忽略模式
   */
  removeIgnorePattern(pattern: string): void {
    this.ignorePatterns = this.ignorePatterns.filter(p => p !== pattern)
  }

  /**
   * 获取当前忽略模式
   */
  getIgnorePatterns(): string[] {
    return [...this.ignorePatterns]
  }

  /**
   * 执行读取文件操作
   */
  private async executeReadFile(path: string): Promise<{ content: string }> {
    if (!this.rootDirHandle) {
      throw new Error('No directory selected')
    }

    const fileHandle = await this.getHandleByPath(path)
    if (!fileHandle || fileHandle.kind !== 'file') {
      throw new Error('Invalid file path')
    }

    const file = await (fileHandle as FileSystemFileHandle).getFile()
    const content = await file.text()

    return { content }
  }

  /**
   * 执行写入文件操作
   */
  private async executeWriteFile(path: string, content: string): Promise<void> {
    if (!this.rootDirHandle) {
      throw new Error('No directory selected')
    }

    const fileHandle = await this.getHandleByPath(path)
    if (!fileHandle || fileHandle.kind !== 'file') {
      throw new Error('Invalid file path')
    }

    const writable = await (fileHandle as FileSystemFileHandle).createWritable()
    await writable.write(content)
    await writable.close()

    // 发送文件变更通知
    this.sendNotification(MsgType.FILE_CHANGED, { path, content })
  }

  /**
   * 执行删除文件操作
   */
  private async executeDeleteFile(path: string, isDirectory: boolean): Promise<void> {
    if (!this.rootDirHandle) {
      throw new Error('No directory selected')
    }

    const pathParts = path.split('/').filter(p => p)
    const fileName = pathParts.pop()
    if (!fileName) {
      throw new Error('Invalid path')
    }

    const parentPath = pathParts.join('/')
    const parentHandle = await this.getHandleByPath(parentPath)
    if (!parentHandle || parentHandle.kind !== 'directory') {
      throw new Error('Invalid parent directory')
    }

    await (parentHandle as FileSystemDirectoryHandle).removeEntry(fileName, { recursive: isDirectory })

    // 发送文件删除通知
    this.sendNotification(MsgType.FILE_DELETED, { path, name: fileName })
  }

  /**
   * 执行创建目录操作
   */
  private async executeCreateDirectory(parentPath: string, name: string): Promise<void> {
    if (!this.rootDirHandle) {
      throw new Error('No directory selected')
    }

    const parentHandle = await this.getHandleByPath(parentPath)
    if (!parentHandle || parentHandle.kind !== 'directory') {
      throw new Error('Invalid parent directory')
    }

    await (parentHandle as FileSystemDirectoryHandle).getDirectoryHandle(name, { create: true })

    const newPath = parentPath ? `${parentPath}/${name}` : name
    // 发送文件创建通知
    this.sendNotification(MsgType.FILE_CREATED, { path: newPath, name, isDirectory: true })
  }

  /**
   * 执行创建文件操作
   */
  private async executeCreateFile(parentPath: string, name: string): Promise<void> {
    if (!this.rootDirHandle) {
      throw new Error('No directory selected')
    }

    const parentHandle = await this.getHandleByPath(parentPath)
    if (!parentHandle || parentHandle.kind !== 'directory') {
      throw new Error('Invalid parent directory')
    }

    await (parentHandle as FileSystemDirectoryHandle).getFileHandle(name, { create: true })

    const newPath = parentPath ? `${parentPath}/${name}` : name
    // 发送文件创建通知
    this.sendNotification(MsgType.FILE_CREATED, { path: newPath, name, isDirectory: false })
  }

  /**
   * 根据路径获取文件/目录句柄
   */
  private async getHandleByPath(path: string): Promise<FileSystemHandle | null> {
    if (!this.rootDirHandle) return null
    if (!path) return this.rootDirHandle

    const parts = path.split('/').filter(p => p)
    let currentHandle: FileSystemHandle = this.rootDirHandle

    for (const part of parts) {
      if (currentHandle.kind === 'directory') {
        try {
          currentHandle = await (currentHandle as FileSystemDirectoryHandle).getFileHandle(part)
        } catch {
          try {
            currentHandle = await (currentHandle as FileSystemDirectoryHandle).getDirectoryHandle(part)
          } catch {
            return null
          }
        }
      } else {
        return null
      }
    }

    return currentHandle
  }

  // 实现 IFileSystemAdapter 接口（用于本地UI交互）
  async selectDirectory(): Promise<{ path: string; handle?: any }> {
    try {
      // @ts-ignore - showDirectoryPicker is a File System Access API
      const dirHandle = await window.showDirectoryPicker({
        mode: 'readwrite'
      })
      this.rootDirHandle = dirHandle
      // 使用空字符串作为根路径，以便正确处理相对路径
      this.currentPath = ''
      
      // 通知服务器目录已选择
      this.sendNotification(MsgType.DIRECTORY_LIST, {
        path: this.currentPath,
        rootName: dirHandle.name,
      })

      return { path: dirHandle.name, handle: dirHandle }
    } catch (error: any) {
      if (error.name === 'AbortError') {
        throw error
      }
      throw new Error('选择目录失败: ' + error.message)
    }
  }

  async listDirectory(path: string, handle?: any): Promise<FileInfo[]> {
    // 如果提供了handle，直接使用它（与LocalFileSystemAdapter一致）
    if (handle) {
      const files: FileInfo[] = []
      // @ts-ignore - FileSystemDirectoryHandle values() method
      for await (const entry of handle.values()) {
        try {
          // 检查是否应该忽略此文件/目录
          if (this.shouldIgnore(entry.name)) {
            console.log(`[Ignore] Skipping: ${entry.name}`)
            continue
          }

          let size = 0
          let lastModified = 0
          
          if (entry.kind === 'file') {
            const fileHandle = entry as FileSystemFileHandle
            const file_obj = await fileHandle.getFile()
            size = file_obj.size
            lastModified = file_obj.lastModified
          }

          files.push({
            name: entry.name,
            path: entry.name,
            isDirectory: entry.kind === 'directory',
            size,
            lastModified,
            handle: entry,
          })
        } catch (e) {
          console.error('Error reading entry:', entry.name, e)
        }
      }

      // 排序：目录优先，然后按名称
      files.sort((a, b) => {
        if (a.isDirectory && !b.isDirectory) return -1
        if (!a.isDirectory && b.isDirectory) return 1
        return a.name.localeCompare(b.name)
      })

      return files
    }
    
    // 兼容旧的基于path的调用方式
    const result = await this.executeListDirectory(path)
    return result.files
  }

  async readFile(file: FileInfo): Promise<string> {
    const result = await this.executeReadFile(file.path)
    return result.content
  }

  async writeFile(file: FileInfo, content: string): Promise<void> {
    await this.executeWriteFile(file.path, content)
  }

  async deleteFile(file: FileInfo): Promise<void> {
    await this.executeDeleteFile(file.path, file.isDirectory)
  }

  async createDirectory(parentPath: string, name: string, parentHandle?: any): Promise<void> {
    await this.executeCreateDirectory(parentPath, name)
  }

  async createFile(parentPath: string, name: string, parentHandle?: any): Promise<void> {
    await this.executeCreateFile(parentPath, name)
  }

  /**
   * 获取连接状态
   */
  isWebSocketConnected(): boolean {
    return this.isConnected
  }

  /**
   * 设置操作完成回调
   */
  setOperationCompleteCallback(callback: () => void): void {
    this.onOperationComplete = callback
  }

  /**
   * 获取WebSocket实例
   */
  getWebSocket(): WebSocket | null {
    return this.ws
  }

  /**
   * 获取根目录句柄
   */
  getRootDirHandle(): FileSystemDirectoryHandle | null {
    return this.rootDirHandle
  }
}
