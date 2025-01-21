/*
 * @Description:
 * @Date: 2024-08-15 20:45:02
 * @LastEditTime: 2024-08-16 15:48:50
 */
import { post, get } from '@/utils/request'

export function createByOneSentence<T = any>(data: any) {
  return post<T>({
    url: '/v1/bot/createByOneSentence',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/json',
      workspaceId: data.workspaceId
    }
  })
}

export function chatMode<T = any>(data: any) {
  return post<T>({
    url: '/v1/model/list/v1/chat',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/json',
      workspaceId: data.workspaceId
    }
  })
}
