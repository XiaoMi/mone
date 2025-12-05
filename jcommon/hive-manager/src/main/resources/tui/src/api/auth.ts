import { http } from './request'
import type { ApiResponse, User } from '../types'

export const login = async (username: string, password: string) => {
  return http.post<ApiResponse<{ user: User; token: string }>>('/api/v1/users/login', {
    username,
    password
  })
}

export const getUserInfo = async () => {
  return http.get<ApiResponse<User>>('/api/v1/users/info')
}

export const ping = async () => {
  return http.get('/ping')
}
