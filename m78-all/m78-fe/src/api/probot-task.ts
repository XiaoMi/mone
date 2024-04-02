import { post } from '@/utils/request'

// 创建任务
export function createTask(data) {
  return post<T>({
    url: `/v1/task/createTask`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 查询任务
export function queryTask(data) {
  return post<T>({
    url: `/v1/task/queryTask`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function deleteTask(data) {
  return post<T>({
    url: `/v1/task/deleteTask`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateTask(data) {
  return post<T>({
    url: `/v1/task/updateTask`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function executeTask(data) {
  return post<T>({
    url: `/v1/task/executeTask`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function changeStatus(data) {
  return post<T>({
    url: '/v1/task/disableTask',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
