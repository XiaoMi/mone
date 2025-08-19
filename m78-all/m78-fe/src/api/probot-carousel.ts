/*
 * @Description:
 * @Date: 2024-09-13 16:05:18
 * @LastEditTime: 2024-09-13 19:23:26
 */
import { post, get } from '@/utils/request'

export function getListByAdmin<T = any>(data: {}) {
  return post<T>({
    url: '/v1/RecommendCarousel/getListByAdmin',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    data
  })
}

export function add<T = any>(data: any) {
  return post<T>({
    url: '/v1/RecommendCarousel/add',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function update<T = any>(data: any) {
  return post<T>({
    url: '/v1/RecommendCarousel/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateDisplayStatus<T = any>(data: any) {
  return post<T>({
    url: '/v1/RecommendCarousel/updateDisplayStatus',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
