import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

/**
 * 文件信息接口
 */
export interface FileInfo {
  name: string
  path: string
  isDirectory: boolean
  size: number
  lastModified: number
  children?: FileInfo[]
}

/**
 * 文件内容接口
 */
export interface FileContent {
  path: string
  content: string
  encoding: string
  size: number
}

/**
 * 文件写入请求接口
 */
export interface FileWriteRequest {
  path: string
  content: string
  createParents?: boolean
}

/**
 * 获取目录下的文件列表
 */
export function listFiles(path: string): Promise<IResponse<FileInfo[]>> {
  return Service({
    url: '/api/v1/filesystem/list',
    method: 'get',
    params: { path }
  })
}

/**
 * 读取文件内容
 */
export function readFile(path: string): Promise<IResponse<FileContent>> {
  return Service({
    url: '/api/v1/filesystem/read',
    method: 'get',
    params: { path }
  })
}

/**
 * 写入文件内容
 */
export function writeFile(data: FileWriteRequest): Promise<IResponse<string>> {
  return Service({
    url: '/api/v1/filesystem/write',
    method: 'post',
    data
  })
}

/**
 * 删除文件或目录
 */
export function deleteFile(path: string): Promise<IResponse<string>> {
  return Service({
    url: '/api/v1/filesystem/delete',
    method: 'delete',
    params: { path }
  })
}

/**
 * 创建目录
 */
export function createDirectory(path: string): Promise<IResponse<string>> {
  return Service({
    url: '/api/v1/filesystem/mkdir',
    method: 'post',
    params: { path }
  })
}
