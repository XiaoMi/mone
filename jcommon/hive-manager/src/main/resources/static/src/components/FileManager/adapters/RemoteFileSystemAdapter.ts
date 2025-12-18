import type { IFileSystemAdapter, FileInfo } from '../types'
import { listFiles, readFile, writeFile, deleteFile, createDirectory } from '@/api/filesystem'

/**
 * 远程文件系统适配器（通过HTTP API）
 */
export class RemoteFileSystemAdapter implements IFileSystemAdapter {
  
  async selectDirectory(): Promise<{ path: string; handle?: any }> {
    // 对于远程文件系统，可以弹出对话框让用户输入路径
    // 或者显示一个目录树供选择
    throw new Error('远程文件系统需要通过其他方式指定目录路径')
  }
  
  async listDirectory(path: string): Promise<FileInfo[]> {
    const res = await listFiles(path)
    if (res.code === 0) {
      return res.data || []
    } else {
      throw new Error(res.message || '加载目录失败')
    }
  }
  
  async readFile(file: FileInfo): Promise<string> {
    const res = await readFile(file.path)
    if (res.code === 0 && res.data) {
      return res.data.content
    } else {
      throw new Error(res.message || '读取文件失败')
    }
  }
  
  async writeFile(file: FileInfo, content: string): Promise<void> {
    const res = await writeFile({
      path: file.path,
      content,
      createParents: false
    })
    if (res.code !== 0) {
      throw new Error(res.message || '保存失败')
    }
  }
  
  async deleteFile(file: FileInfo): Promise<void> {
    const res = await deleteFile(file.path)
    if (res.code !== 0) {
      throw new Error(res.message || '删除失败')
    }
  }
  
  async createDirectory(parentPath: string, name: string): Promise<void> {
    const newPath = parentPath + '/' + name
    const res = await createDirectory(newPath)
    if (res.code !== 0) {
      throw new Error(res.message || '创建文件夹失败')
    }
  }
  
  async createFile(parentPath: string, name: string): Promise<void> {
    const newPath = parentPath + '/' + name
    const res = await writeFile({
      path: newPath,
      content: '',
      createParents: true
    })
    if (res.code !== 0) {
      throw new Error(res.message || '创建文件失败')
    }
  }
}
