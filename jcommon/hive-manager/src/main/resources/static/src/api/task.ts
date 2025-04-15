import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

export interface Task {
  id: number
  title: string
  description: string
  createdAt: string
  clientAgentId: number
  serverAgentId: number
  status: string
}


// 获取任务列表
export const getTaskList = (serverAgentId: number) => {
    return Service<IResponse<Task[]>>({
      url: `/v1/tasks?serverAgentId=${serverAgentId}`,
      method: 'get',
    })
  }

// 根据id获取任务
export const getTaskById = (id: number) => {
  return Service<IResponse<Task>>({
    url: `/v1/tasks/${id}`,
    method: 'get',
  })
}

// 创建任务的请求参数接口
export interface CreateTaskRequest {
  taskUuid: string
  clientAgentId: number | null
  serverAgentId: number | null
  skillId: number | null
  title: string
  description: string
  status: string
}

// 创建任务
export const createTask = (params: CreateTaskRequest) => {
  return Service<IResponse<Task>>({
    url: '/v1/tasks',
    method: 'post',
    data: params
  })
}
