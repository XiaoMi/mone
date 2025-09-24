/*
 * @Description:
 * @Date: 2024-08-28 15:13:44
 * @LastEditTime: 2024-08-30 16:52:37
 */
import { post } from '@/utils/request'

export function listPerdayInfoByBotId<T = any>(data: { relateId: string; daysAgo: string }) {
  return post<T>({
    url: '/v1/invokeHistory/listPerdayInfoByBotId',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function listHistoryDetails<T = any>(data: any) {
  return post<T>({
    url: '/v1/invokeHistory/listHistoryDetails',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
