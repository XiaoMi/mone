import { post, get } from '@/utils/request'

export function getDbList<T = any>(data) {
  return get<T>({
    url: '/v1/ai_table/getTableByWorkspaceId',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delDb<T = any>(data) {
  return post<T>({
    url: '/v1/ai_table/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function createDb<T = any>(data) {
  return post<T>({
    url: '/v1/ai_table/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getDbDetail<T = any>(data) {
  return get<T>({
    url: '/v1/ai_table/getTableByName',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
