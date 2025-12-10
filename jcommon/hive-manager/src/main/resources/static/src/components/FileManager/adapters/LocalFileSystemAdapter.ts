import { ElMessage } from 'element-plus'
import type { IFileSystemAdapter, FileInfo, DirectoryStackItem } from '../types'

/**
 * 本地文件系统适配器（使用 File System Access API）
 */
export class LocalFileSystemAdapter implements IFileSystemAdapter {
  
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
    
    for await (const entry of handle.values()) {
      try {
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
}
