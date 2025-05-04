/*
 * @Description:
 * @Date: 2024-03-11 15:20:16
 * @LastEditTime: 2024-03-20 16:11:16
 */

import { post, get } from '@/utils/request'

// 分类
export function getCategoryTypeList<T = any>() {
  return get<T>({
    url: '/v1/category/typeList',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 分类列表
export function getCategoryList<T = any>(data: { type: number | string }) {
  return get<T>({
    url: '/v1/category/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 分类创建
export function createCategory<T = any>(data: { categoryName: string }) {
  return post<T>({
    url: '/v1/category/create',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 分类删除
export function deleteCategory<T = any>(data: { categoryId: string }) {
  return post<T>({
    url: '/v1/category/delete',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
