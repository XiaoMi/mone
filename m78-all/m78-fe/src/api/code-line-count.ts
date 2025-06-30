// import { get } from '@/utils/request'
import request from '@/utils/request/axios'
// @ts-ignore
import qs from 'qs'

export function getUserCodeLines(data: { currentPage: number; pageSize: number }) {
  return request({
    url: '/v1/code/statistics/userCodeLines',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    method: 'POST',
    data: qs.stringify(data),
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
