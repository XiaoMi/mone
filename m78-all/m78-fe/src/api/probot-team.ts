import { post } from '@/utils/request'

export function createTeam<T = any>(data: {}) {
  return post<T>({
    url: '/v1/workspace/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function updateTeam<T = any>(data: {
  workspaceId?: number
  workspaceName?: string
  remark?: string
  avatarUrl?: string
}) {
  return post<T>({
    url: '/v1/workspace/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      workspaceId: data?.workspaceId
    }
  })
}

export function transferTeam<T = any>(data: { workspaceId?: number; username?: string }) {
  return post<T>({
    url: '/v1/workspace/transfer',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      workspaceId: data?.workspaceId
    }
  })
}
export function deleteTeam<T = any>(data: { workspaceId: number }) {
  return post<T>({
    url: '/v1/workspace/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      workspaceId: data?.workspaceId
    }
  })
}
