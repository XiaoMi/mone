/*
 * @Description:
 * @Date: 2024-03-18 17:47:08
 * @LastEditTime: 2024-08-15 15:07:02
 */
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
