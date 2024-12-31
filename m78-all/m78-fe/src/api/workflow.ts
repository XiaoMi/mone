import { get, post, deleteApi } from '@/utils/request'

// edit
export function saveFlow<T = any>(data) {
  return post<T>({
    url: '/v1/flow/update/setting',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function editBase<T = any>(data) {
  return post<T>({
    url: '/v1/flow/update/base',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 创建
export function createFlow<T = any>(data) {
  return post<T>({
    url: '/v1/flow/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function updateFlow<T = any>(data) {
  return post<T>({
    url: '/v1/flow/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function deleteFlow<T = any>(flowBaseId) {
  return deleteApi(<T>{
    url: `/v1/flow/delete?flowBaseId=${flowBaseId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 获取详情
export function getFlowDetail<T = any>(id) {
  return get<T>({
    url: `/v1/flow/detail?id=${id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// flow list
export function getFlowList<T = any>(data) {
  return post<T>({
    url: '/v1/flow/list ',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function testFlow<T = any>(data: { flowId: string; inputs: Record<string, any> }) {
  return post<T>({
    url: '/v1/flow/test',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getPluginList<T = any>() {
  return post<T>({
    url: '/v1/botplugin/list',
    data: {
      pageNum: 1,
      pageSize: 100
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getFlowStatus<T = any>(data?: any, onDownloadProgress?: any) {
  return post<T>({
    url: '/v1/flow/flowStatus/stream',
    data,
    onDownloadProgress: onDownloadProgress,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 生成代码
export function getCodeGen<T = any>(data?: any, onDownloadProgress?: any) {
  return post<T>({
    url: '/v1/code/generate',
    data,
    onDownloadProgress: onDownloadProgress,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
