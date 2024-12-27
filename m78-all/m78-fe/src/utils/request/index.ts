import type { AxiosProgressEvent, AxiosResponse, GenericAbortSignal } from 'axios'
import request from './axios'

export interface HttpOption {
  url: string
  data?: any
  method?: string
  headers?: any
  baseURL?: string
  onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void
  signal?: GenericAbortSignal
  beforeRequest?: () => void
  afterRequest?: () => void
}

export interface Response<T = any> {
  data: T
  message: string | null
  status: string | number
  code: string | number
}

function http<T = any>({
  url,
  data,
  method,
  headers,
  baseURL,
  onDownloadProgress,
  signal,
  beforeRequest,
  afterRequest
}: HttpOption) {
  const successHandler = (res: AxiosResponse<Response<T>>) => {
    if (res.data) return res.data

    return Promise.reject(res.data)
  }

  const failHandler = (error: Response<Error>) => {
    afterRequest?.()
    throw new Error(error?.message || 'Error')
  }

  beforeRequest?.()

  method = method || 'GET'

  const params = Object.assign(typeof data === 'function' ? data() : data ?? {}, {})
  if (method === 'GET') {
    return request
      .get(url, { params, headers, signal, onDownloadProgress, baseURL })
      .then(successHandler, failHandler)
  } else if (method === 'POST') {
    return request
      .post(url, params, { headers, signal, onDownloadProgress, baseURL })
      .then(successHandler, failHandler)
  } else if (method === 'DELETE') {
    return request
      .delete(url, { headers, signal, onDownloadProgress, baseURL })
      .then(successHandler, failHandler)
  }
}

export function get<T = any>({
  url,
  data,
  method = 'GET',
  baseURL,
  headers,
  onDownloadProgress,
  signal,
  beforeRequest,
  afterRequest
}: HttpOption): Promise<Response<T>> {
  return http<T>({
    url,
    method,
    data,
    baseURL,
    headers,
    onDownloadProgress,
    signal,
    beforeRequest,
    afterRequest
  })
}
export function deleteApi<T = any>({
  url,
  data,
  method = 'DELETE',
  baseURL,
  signal,
  beforeRequest,
  afterRequest
}: HttpOption): Promise<Response<T>> {
  return http<T>({
    url,
    method,
    data,
    baseURL,
    signal,
    beforeRequest,
    afterRequest
  })
}

export function post<T = any>({
  url,
  data,
  method = 'POST',
  headers,
  baseURL,
  onDownloadProgress,
  signal,
  beforeRequest,
  afterRequest
}: HttpOption): Promise<Response<T>> {
  return http<T>({
    url,
    method,
    data,
    headers,
    baseURL,
    onDownloadProgress,
    signal,
    beforeRequest,
    afterRequest
  })
}

export default post
