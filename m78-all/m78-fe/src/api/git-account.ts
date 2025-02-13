/*
 * @Description:
 * @Date: 2024-11-14 14:33:18
 * @LastEditTime: 2024-12-05 11:22:38
 */
import { post } from '@/utils/request'

export function addGitlabMapping<T = any>(data: { user: string; gitAccountList: string[] }) {
  return post<T>({
    url: '/asm/v0/userMapping/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export interface SearchGitlabResponse {
  code: number
  message: string
  data: {
    list: {
      id: number
      user: string
      gitlabAccount: string[]
      githubAccount: string[]
    }[]
    pageNo: number
    pageSize: number
    pageTotal: number
  }
}

export function searchGitlab<SearchGitlabResponse>(data: {
  pageNo: number
  pageSize: number
  userAccount?: string // 可选参数，用于模糊搜索
}) {
  return post<SearchGitlabResponse>({
    url: '/asm/v0/userMapping/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 添加这些新的导出
export const deleteGitlabMapping = (data: { user: string }) => {
  return post<any>({
    url: '/asm/v0/userMapping/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export const updateGitlabMapping = (data: { user: string; gitAccountList: string[] }) => {
  return post<any>({
    url: '/asm/v0/userMapping/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
