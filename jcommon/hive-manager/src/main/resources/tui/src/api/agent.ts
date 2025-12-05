import { http } from './request'
import type { ApiResponse, Agent, AgentListItem } from '../types'

export const getAgentList = async (searchQuery = '', isFavorite = false) => {
  return http.get<ApiResponse<AgentListItem[]>>('/api/v1/agents/list', {
    params: { searchQuery, isFavorite }
  })
}

export const getAgentDetail = async (id: number) => {
  return http.get<ApiResponse<{ agent: Agent; instances: any[] }>>(`/api/v1/agents/${id}`)
}

export const createAgent = async (data: Partial<Agent>) => {
  return http.post<ApiResponse>('/api/v1/agents/create', data)
}

export const updateAgent = async (id: number, data: Partial<Agent>) => {
  return http.put<ApiResponse>(`/api/v1/agents/${id}`, data)
}

export const deleteAgent = async (id: number) => {
  return http.delete<ApiResponse>(`/api/v1/agents/${id}`)
}

export const addFavorite = async (data: { userId: number; type: number; targetId: number }) => {
  return http.post<ApiResponse>('/api/favorite/add', data)
}

export const deleteFavorite = async (data: { userId: number; type: number; targetId: number }) => {
  return http.post<ApiResponse>('/api/favorite/remove', data)
}

export const clearHistory = async (data: any) => {
  return http.post<ApiResponse>('/api/v1/agents/clearHistory', data)
}

export const offlineAgent = async (data: any) => {
  return http.post<ApiResponse>('/api/v1/agents/offline', data)
}
