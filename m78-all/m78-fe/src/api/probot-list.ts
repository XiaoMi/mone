/*
 * @Description:
 * @Date: 2024-08-12 15:24:05
 * @LastEditTime: 2024-08-12 16:51:51
 */
import { post } from '@/utils/request'

export function recommendCarouselList<T = any>(data: {
  type: number
  pageNum: number
  pageSize: number
  displayStatus: number
}) {
  return post<T>({
    url: '/v1/RecommendCarousel/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
