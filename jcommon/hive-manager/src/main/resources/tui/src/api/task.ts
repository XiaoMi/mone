import { http } from './request'
import type { ApiResponse, Task } from '../types'

export const getTaskList = async (params?: { serverAgentId?: number }) => {
  return http.get<ApiResponse<Task[]>>('/api/v1/tasks', { params })
}

export const getTaskById = async (taskUuid: string) => {
  return http.get<ApiResponse<Task>>(`/api/v1/tasks/${taskUuid}`)
}

export const createTask = async (data: Partial<Task>) => {
  return http.post<ApiResponse>('/api/v1/tasks', data)
}

export const updateTask = async (data: Partial<Task>) => {
  return http.put<ApiResponse>(`/api/v1/tasks/${data.taskUuid}/update`, {
    description: data.description,
    serverAgentId: data.serverAgentId
  })
}

export const executeTask = async (data: { id: string; metadata: any }) => {
  return http.post<ApiResponse>('/api/v1/tasks/execute', data)
}
