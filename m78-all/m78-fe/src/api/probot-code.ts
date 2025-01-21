/*
 * @Description:
 * @Date: 2024-04-15 15:33:47
 * @LastEditTime: 2024-04-16 16:43:32
 */
import { post } from '@/utils/request'

export function codeList<T = any>(data: {}) {
  return post<T>({
    url: '/v1/code/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function saveOrUpdate<T = any>(data) {
  return post<T>({
    url: '/v1/code/saveOrUpdate',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function deleteCode<T = any>(data: { id: number }) {
  return post<T>({
    url: `/v1/code/deleteById`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getCodeById<T = any>(data: { id: number }) {
  return post<T>({
    url: '/v1/code/getById',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}
