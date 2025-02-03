import { post, get } from '@/utils/request'

export function apikeyCreate<T = any>(data: { typeId: string; type: string }) {
  return post<T>({
    url: '/v1/apikey/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getByTypeIdAndType<T = any>(data: { typeId: string; type: string }) {
  return get<T>({
    url: '/v1/apikey/getByTypeIdAndType',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function apikeyDelete<T = any>(data: { id: string; typeId: string; type: string }) {
  return post<T>({
    url: '/v1/apikey/delete?id=' + data.id,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
