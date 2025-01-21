import { post, get } from '@/utils/request'

export function listAllPerdayInfos<T = any>(data: any) {
  return post<T>({
    url: '/v1//invokeHistory/listAllPerdayInfos',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function listPerdayInfosByAdmin<T = any>(data: any) {
  return post<T>({
    url: '/v1/invokeHistory/listPerdayInfosByAdmin',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function getBotSimpleInfo<T = any>(data: any) {
  return get<T>({
    url: '/v1/bot/getBotSimpleInfo',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function flowDetail<T = any>(data: any) {
  return get<T>({
    url: '/v1/flow/detail',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function orgGetById<T = any>({ id }) {
  return post<T>({
    url: `/v1/botplugin/org/getById?id=${id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function listHistoryDetails<T = any>(data: any) {
  return post<T>({
    url: '/v1/invokeHistory/listHistoryDetails',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
