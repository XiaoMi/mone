import { post, get } from '@/utils/request'

// 图片理解
export function ImageUnderstanding<T = any>(data: {
  model: string
  cmd: string
  imageUrls: Array<string>
  input: string
}) {
  return post<T>({
    url: '/v1/multiModal/imageUnderstanding',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 文本生图
export function TextToImage<T = any>(data: {
  model: string
  cmd: string
  refImageUrl: string
  input: string
  num: number
  style: string
  size: string
}) {
  return post<T>({
    url: '/v1/multiModal/textToImage',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 涂鸦生成
export function SketchToImage<T = any>(data: {
  model: string
  cmd: string
  baseImageUrl: string
  input: string
  num: number
  size: string
  sketchExtraction: boolean
  sketchColor: Array<any>
}) {
  return post<T>({
    url: '/v1/multiModal/sketchToImage',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 图文融合
export function TextAndImage<T = any>(data: {
  model: string
  cmd: string
  input: string
  maskImageUrl: string
  num: number
  negativeInput: string
}) {
  return post<T>({
    url: '/v1/multiModal/textAndImage',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 艺术字
export function ArtWord<T = any>(data: {
  cmd: string
  model: string
  input: string
  textContent: string
  fontName: string
  outputImageRatio: string
  textureStyle: string
  num: number
}) {
  return post<T>({
    url: '/v1/multiModal/artWord',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 获取历史记录
export function getHistory<T = any>(data: { pageNum: number; pageSize: number }) {
  return post<T>({
    url: '/v1/multiModal/historyList',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 背景图生成
export function backGen<T = any>(data: {
  model: string
  userName: string
  cmd: string
  input: string
  num: number
  baseImageUrl: string
  title: string
  subTitle: string
  temperature: number
}) {
  return post<T>({
    url: '/v1/multiModal/backgroundGen',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 获取taskInfo
export function getTaskInfo<T = any>(data: { taskId: number }) {
  return get<T>({
    url: '/v1/multiModal/taskInfo',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 获取历史详情
export function getHisDetails<T = any>(data: any) {
  return post<T>({
    url: '/v1/multiModal/historyDetailsByIds',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 重绘
export function repaintApi<T = any>(data: any) {
  return post<T>({
    url: '/v1/multiModal/styleRepaint',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 重绘
export function delHis<T = any>(data: any) {
  return post<T>({
    url: '/v1/multiModal/deleteByIds',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 办公图表
export function workChartGen<T = any>(data: any) {
  return post<T>({
    url: '/v1/multiModal/workChartGen',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getModalList<T = any>(data: any) {
  return get<T>({
    url: '/v1/model/list/filter',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
