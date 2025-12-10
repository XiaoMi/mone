/**
 * 文件信息接口
 */
export interface FileInfo {
  name: string
  path: string
  isDirectory: boolean
  size: number
  lastModified: number
  handle?: FileSystemFileHandle | FileSystemDirectoryHandle
}

/**
 * 文件系统适配器接口
 */
export interface IFileSystemAdapter {
  /**
   * 选择/打开目录
   */
  selectDirectory(): Promise<{ path: string; handle?: any }>
  
  /**
   * 列出目录内容
   */
  listDirectory(path: string, handle?: any): Promise<FileInfo[]>
  
  /**
   * 读取文件内容
   */
  readFile(file: FileInfo): Promise<string>
  
  /**
   * 写入文件内容
   */
  writeFile(file: FileInfo, content: string): Promise<void>
  
  /**
   * 删除文件或目录
   */
  deleteFile(file: FileInfo): Promise<void>
  
  /**
   * 创建目录
   */
  createDirectory(parentPath: string, name: string, parentHandle?: any): Promise<void>
  
  /**
   * 创建文件
   */
  createFile(parentPath: string, name: string, parentHandle?: any): Promise<void>
}

/**
 * 目录栈项
 */
export interface DirectoryStackItem {
  name: string
  handle?: any
  path?: string
}
