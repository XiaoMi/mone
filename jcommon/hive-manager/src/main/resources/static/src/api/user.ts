import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

interface LoginRequest {
  username: string
  password: string
}

interface User {
  id: number
  username: string
  // 添加其他用户字段
}

interface LoginResponse {
  token: string
  user: User
}

export const login = (data: LoginRequest) => {
  return Service<IResponse<LoginResponse>>({
    url: '/v1/users/login',
    method: 'post',
    data
  })
}

export const register = (data: LoginRequest) => {
  return Service<IResponse<LoginResponse>>({
    url: '/v1/users/register',
    method: 'post',
    data
  })
}



export const getUserList = () => {
  return Service<IResponse<LoginResponse>>({
    url: '/v1/users/list',
    method: 'get',
  })
}
