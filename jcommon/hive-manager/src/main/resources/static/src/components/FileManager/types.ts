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
  
  /**
   * 设置操作完成回调（用于自动刷新UI）
   */
  setOperationCompleteCallback?(callback: () => void): void
}

/**
 * 目录栈项
 */
export interface DirectoryStackItem {
  name: string
  handle?: any
  path?: string
}

/**
 * WebSocket消息类型
 */
export enum WSMessageType {
  // 请求类型
  LIST_FILES = 'list_files',
  READ_FILE = 'read_file',
  WRITE_FILE = 'write_file',
  DELETE_FILE = 'delete_file',
  CREATE_DIRECTORY = 'create_directory',
  CREATE_FILE = 'create_file',
  HEART_BEAT = 'heartbeat',
  
  // 响应类型
  DIRECTORY_LIST = 'directory_list',
  FILE_CONTENT = 'file_content',
  OPERATION_SUCCESS = 'operation_success',
  OPERATION_ERROR = 'operation_error',
  
  // 通知类型
  FILE_CHANGED = 'file_changed',
  FILE_CREATED = 'file_created',
  FILE_DELETED = 'file_deleted'
}

/**
 * WebSocket消息接口
 */
export interface WSMessage {
  type: 'notification'
  action: WSMessageType
  data?: any
  error?: string
  timestamp?: number
}

/**
 * WebSocket请求接口
 */
export interface WSRequest {
  type: string  // 'call' | 'heartbeat' 或其他类型
  action?: string  // type为'call'时必填
  reqId?: string
  data?: {
    path?: string
    name?: string
    content?: string
    isDirectory?: boolean
    parentPath?: string
    [key: string]: any
  }
  timestamp?: number
}

/**
 * WebSocket响应接口
 */
export interface WSResponse {
  type: 'call_response' | 'call_error'
  action: string
  resId: string
  success: boolean
  data?: any
  error?: string
  timestamp?: number
}
