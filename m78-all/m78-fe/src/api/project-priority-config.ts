import { post } from '@/utils/request'

export function projectPriorityConfig<T = any>(data: any) {
  return post<T>({
    url: '/ai-senior-mgr/projectPriorityConfig',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_STUDIO_URL
  })
}

export function getProjectPriorityConfigByAccount<T = any>(data: any) {
  return post<T>({
    url: '/ai-senior-mgr/getProjectPriorityConfigByAccount',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_STUDIO_URL
  })
}
