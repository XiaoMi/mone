import type { AxiosProgressEvent, GenericAbortSignal } from 'axios'
import { post, get, deleteApi } from '@/utils/request'
import { useSettingStore } from '@/stores/settings'

import type { ChatContextMsg } from '@/stores/chatContext'

export function fetchInput<T = any>(data, signal) {
  return post<T>({
    url: '/v1/document/data/analyze',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    signal
  })
}
export function docHistory<T = any>() {
  return get<T>({
    url: '/v1/document/list',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getDocDetail<T = any>(documentId) {
  return get<T>({
    url: `/v1/document/data?documentId=${documentId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getDocInputList<T = any>(documentId) {
  return get<T>({
    url: `/v1/document/get?documentId=${documentId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
export function delChat<T = any>(chatInfoId) {
  return deleteApi(<T>{
    url: `/v1/document/chat/delete?chatInfoId=${chatInfoId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function favorite<T = any>(documentId) {
  return get(<T>{
    url: `/v1/document/favorite?documentId=${documentId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function unfavorite<T = any>(documentId) {
  return get(<T>{
    url: `/v1/document/unfavorite?documentId=${documentId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function myfavorite<T = any>() {
  return get<T>({
    url: '/v1/document/favorite/list',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delDocApi<T = any>(documentId) {
  return deleteApi(<T>{
    url: `/v1/document/delete?documentId=${documentId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getChatInfoApi<T = any>(chatInfoId) {
  return post(<T>{
    url: `/v1/document/chat/info?chatInfoId=${chatInfoId}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateChatInfoApi<T = any>(data) {
  return post(<T>{
    url: '/v1/document/chat/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateMeta<T = any>(data) {
  return post(<T>{
    url: '/v1/document/content',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function appendRowApi<T = any>(data) {
  return post(<T>{
    url: '/v1/document/insert/row',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function appendColApi<T = any>(data) {
  return post(<T>{
    url: '/v1/document/column/insert',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updataCellApi<T = any>(data) {
  return post(<T>{
    url: '/v1/document/cell/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getColInfoApi<T = any>(id) {
  // 获取列信息
  return post(<T>{
    url: `/v1/document/columns/info?documentId=${id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delColApi<T = any>(data) {
  // 删除某一列
  return post(<T>{
    url: '/v1/document/column/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delRowApi<T = any>(data) {
  // 删除某一行
  return post(<T>{
    url: '/v1/document/row/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function addRowApi<T = any>(data) {
  // 新增一行
  return post(<T>{
    url: '/v1/document/row/insert',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function changeColNameApi<T = any>(data) {
  // 更新名
  return post(<T>{
    url: '/v1/document/column/updateName',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function createHttpApi<T = any>(data) {
  return post(<T>{
    url: '/v1/feature/router/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getApiList<T = any>(data) {
  return get(<T>{
    url: '/v1/feature/router/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function delHttpApi<T = any>(id) {
  return deleteApi(<T>{
    url: `/v1/feature/router/delete?id=${id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function editHttpApi<T = any>(data) {
  return post(<T>{
    url: '/v1/feature/router/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getHttpApi<T = any>(data) {
  return get(<T>{
    url: '/v1/feature/router/getById',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function testHttpApi<T = any>(data) {
  return post(<T>{
    url: '/v1/feature/router/query',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
