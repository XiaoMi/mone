import { ElMessage } from 'element-plus'
import type { IFileSystemAdapter, FileInfo, DirectoryStackItem } from '../types'

/**
 * 本地文件系统适配器（使用 File System Access API）
 */
export class LocalFileSystemAdapter implements IFileSystemAdapter {
  private ignorePatterns: string[] = []

  constructor(ignorePatterns?: string[]) {
    // 默认忽略常见的目录和文件
    this.ignorePatterns = ignorePatterns || [
      'node_modules',
      '.git',
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
  
  async selectDirectory(): Promise<{ path: string; handle: FileSystemDirectoryHandle }> {
    try {
      const dirHandle = await (window as any).showDirectoryPicker({
        mode: 'readwrite'
      })
      
      return {
        path: dirHandle.name,
        handle: dirHandle
      }
    } catch (error: any) {
      if (error.name !== 'AbortError') {
        throw new Error('选择目录失败: ' + error.message)
      }
      throw error
    }
  }
  
  async listDirectory(path: string, handle?: FileSystemDirectoryHandle): Promise<FileInfo[]> {
    if (!handle) {
      throw new Error('需要提供目录句柄')
    }
    
    const fileList: FileInfo[] = []
    
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
          const file = await entry.getFile()
          size = file.size
          lastModified = file.lastModified
        }
        
        fileList.push({
          name: entry.name,
          path: entry.name,
          isDirectory: entry.kind === 'directory',
          size,
          lastModified,
          handle: entry
        })
      } catch (e) {
        console.error('Error reading entry:', entry.name, e)
      }
    }
    
    // 排序：目录优先，然后按名称
    fileList.sort((a, b) => {
      if (a.isDirectory && !b.isDirectory) return -1
      if (!a.isDirectory && b.isDirectory) return 1
      return a.name.localeCompare(b.name)
    })
    
    return fileList
  }
  
  async readFile(file: FileInfo): Promise<string> {
    const fileHandle = file.handle as FileSystemFileHandle
    const fileObj = await fileHandle.getFile()
    
    // 检查文件大小（限制为 5MB）
    if (fileObj.size > 5 * 1024 * 1024) {
      throw new Error('文件太大（超过5MB），无法打开')
    }
    
    return await fileObj.text()
  }
  
  async writeFile(file: FileInfo, content: string): Promise<void> {
    const fileHandle = file.handle as FileSystemFileHandle
    const writable = await fileHandle.createWritable()
    await writable.write(content)
    await writable.close()
  }
  
  async deleteFile(file: FileInfo): Promise<void> {
    // 需要父目录句柄来删除
    throw new Error('本地文件系统适配器需要通过父目录句柄删除文件')
  }
  
  async createDirectory(parentPath: string, name: string, parentHandle?: FileSystemDirectoryHandle): Promise<void> {
    if (!parentHandle) {
      throw new Error('需要提供父目录句柄')
    }
    
    await parentHandle.getDirectoryHandle(name, { create: true })
  }
  
  async createFile(parentPath: string, name: string, parentHandle?: FileSystemDirectoryHandle): Promise<void> {
    if (!parentHandle) {
      throw new Error('需要提供父目录句柄')
    }
    
    const fileHandle = await parentHandle.getFileHandle(name, { create: true })
    const writable = await fileHandle.createWritable()
    await writable.write('')
    await writable.close()
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
}
