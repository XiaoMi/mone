import { get, post } from '@/utils/request'

export function fetchAgents<T = any>() {
  return get<T>({
    url: '/v1/agent/list',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function communicate<T = any>(
  agentId: string,
  data: {
    message: string
    topicId: string
  }
) {
  return post<T>({
    url: '/v1/agent/communicate?agentId=' + agentId,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
