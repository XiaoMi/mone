/*
 * @Description:
 * @Date: 2024-12-04 15:41:37
 * @LastEditTime: 2024-12-05 11:23:13
 */
import { post } from '@/utils/request'

export function getEmployeeStatusByAccount<T = any>(data: [string]) {
  return post<T>({
    url: '/getEmployeeStatusByAccount',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_STUDIO_URL
  })
}

export function configureEmployeeStatus<T = any>(data: any) {
  return post<T>({
    url: '/configureEmployeeStatus',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_STUDIO_URL
  })
}
