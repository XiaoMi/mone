import { post, get } from '@/utils/request'

export function isCollect<T = any>(data: { type: string; collectId: string }) {
  return post<T>({
    url: '/v1/userCollect/isCollect',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function applyCollect<T = any>(data: { type: string; collectId: string }) {
  return post<T>({
    url: '/v1/userCollect/applyCollect',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function deleteCollect<T = any>(data: { type: string; collectId: string }) {
  return post<T>({
    url: '/v1/userCollect/deleteCollect',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 增加热度
export function addHotApi<T = any>(data: { botId: string }) {
  return get<T>({
    url: '/v1/hot/incr',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
