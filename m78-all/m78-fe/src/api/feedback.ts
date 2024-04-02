import { post } from '@/utils/request'

export function feedback<T = any>(data) {
  return post<T>({
    url: '/v1/contact/submit',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
