import { post, get } from '@/utils/request'
import request from '@/utils/request/axios'
//@ts-ignore
import qs from 'qs'

export function getChatList<T = any>() {
  return get<T>({
    url: '/v1/chat/messagetopic/list',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function addChat<T = any>(data: { title: string; description: string }) {
  return post<T>({
    url: '/v1/chat/chattopic/add',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateChat<T = any>(data: {
  id: number
  title: string
  knowledgeConfig: Chat.KnowledgeConfig
}) {
  return post<T>({
    url: '/v1/chat/chattopic/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delChat(data: { topicId: number }) {
  return request({
    url: '/v1/chat/chattopic/delete',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    method: 'POST',
    data: qs.stringify(data),
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getChatMessageList<T = any>(data: { topicId: number }) {
  return get<T>({
    url: '/v1/chat/chatmessage/list',
    data: data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function addChatMessage<T = any>(data: {
  topicId: number
  message: string
  messageRole: Chat.Role
  meta: { $$FeChat: string }
}) {
  return post<T>({
    url: '/v1/chat/chatmessage/add',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delChatMessage(data: { messageId: any }) {
  return request({
    url: '/v1/chat/chatmessage/delete',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    method: 'POST',
    data: qs.stringify(data),
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function clearChatMessage<T = any>(data: { topicId: number }) {
  return get({
    url: '/v1/chat/message/clear',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

//我的知识库列表
export function getKnowledgeMyList<T = any>(data: {}) {
  return get({
    url: '/v1/knowledge/myList',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 某个知识库下的文件列表
export function getKnowledgeFileMyList<T = any>(data: {}) {
  return get({
    url: '/v1/knowledge/file/myList',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 详情
export function getMessagetopicDetail<T = any>(data: { topicId: string }) {
  return get({
    url: '/v1/chat/messagetopic/detail',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
/**
 * @description 知识解析
 */
export const embedding = (
  data: [
    {
      fileName: string
      knowledgeBaseId: string
    }
  ]
): Promise<IResponse<any>> =>
  post({
    url: '/knowledge/base/files/embedding',
    data,
    baseURL: import.meta.env.VITE_GLOB_Z_API_URL
  })
