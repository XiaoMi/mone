import { post, get } from '@/utils/request'
/*
 * @Description:列表接口
 */
// 类型枚举
export function getCardTypes<T = any>() {
  return get({
    url: '/v1/card/getCardTypes',
    headers: {
      'content-type': 'application/json'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 状态枚举
export function getCardStatus<T = any>() {
  return get({
    url: '/v1/card/getCardStatus',
    headers: {
      'content-type': 'application/json'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
//卡片列表
export function listCards<T = any>(data: any) {
  return post({
    url: '/v1/card/listCards',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 新增卡片
export function addCardBasic<T = any>(data: any) {
  return post({
    url: '/v1/card/addCardBasic',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 编辑卡片
export function updateCardBasic<T = any>(data: any) {
  return post({
    url: '/v1/card/updateCardBasic',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
/*
 * @Description:卡片管理接口
 */
// 详情
export function getCardDetail<T = any>(data: { cardId: string }) {
  return get({
    url: '/v1/card/getCardDetail',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 发布
export function publishCard<T = any>(data: { cardId: string }) {
  return post({
    url: '/v1/card/publishCard',
    headers: {
      'content-type': 'application/x-www-form-urlencoded'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function addCardDetail<T = any>(data: any) {
  return post({
    url: '/v1/card/addCardDetail',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function updateCardDetail<T = any>(data: any) {
  return post({
    url: '/v1/card/updateCardDetail',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 左侧组件元素枚举
export function getElementTypes<T = any>(data: { type: string }) {
  return get({
    url: '/v1/card/getElementTypes',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 变量类型的枚举
export function getCardVariableClassTypes<T = any>() {
  return get({
    url: '/v1/card/getCardVariableClassTypes',
    headers: {
      'content-type': 'application/x-www-form-urlencoded'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 新增变量
export function addCardVariable<T = any>(data: any) {
  return post({
    url: '/v1/card/addCardVariable',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 编辑变量
export function updateCardVariable<T = any>(data: any) {
  return post({
    url: '/v1/card/updateCardVariable',
    headers: {
      'content-type': 'application/json'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 删除变量
export function deleteCardVariableById<T = any>(data: any) {
  return post({
    url: '/v1/card/deleteCardVariableById',
    headers: {
      'content-type': 'application/x-www-form-urlencoded'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
//变量列表
export function getCardVariablesByCardId<T = any>(data: { cardId: string }) {
  return get({
    url: '/v1/card/getCardVariablesByCardId',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 背景枚举
export function getBackgroundTypes<T = any>() {
  return get({
    url: '/v1/card/getBackgroundTypes',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 位置枚举
export function getPositions<T = any>(data: { type: string }) {
  return get({
    url: '/v1/card/getPositions',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 隐显设置枚举
export function getVisibilityOptions<T = any>() {
  return get({
    url: '/v1/card/getVisibilityTypes',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 根据变量类型获取操作符
export function getOperators<T = any>(data: { type: string }) {
  return get({
    url: '/v1/card/getOperators',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 右值类型枚举
export function getVisibilityValueTypes<T = any>() {
  return get({
    url: '/v1/card/getVisibilityValueTypes',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
//点击事件
export function getClickEventTypes<T = any>() {
  return get({
    url: '/v1/card/getClickEventTypes',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
