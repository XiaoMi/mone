import axios, { type AxiosResponse, type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import handleCode from './handleCode'
import qs from 'qs'

// 取消重复请求
let pending = {}

export interface IResponse<T = any> {
  data?: T;
  message?: string;
  code?: number;
}

export const Service = axios.create({
  timeout: 15000, // 请求超时时间
  method: 'post',
  withCredentials: true,
  baseURL: import.meta.env.VITE_APP_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    // 'Content-Type': 'application/x-www-form-urlencoded'
  }
})

// 删除重复请求
const removePending = (pending, config) => {
  let key = `${config.url}&${config.method}`
  if (pending[key]) {
    pending[key].call(config) // 取消请求
    delete pending[key]
  }
}

// 添加请求拦截器
Service.interceptors.request.use((config) => {
  // removePending(pending, config);
  // config.cancelToken = new axios.CancelToken((c) => { // 给每个请求加上特定取消请求方法
  //   pending[`${config.url}&${config.method}`] = c;
  // });
  // if (!config.hideLoading) {
    // showLoading()
  // }
  if ((config.headers['Content-Type'] as string).indexOf('application/x-www-form-urlencoded') >= 0) {
    config.data = qs.stringify(config.data)
  }
  const storedToken = localStorage.getItem('token')
  if (storedToken) {
    config.headers['Authorization'] = `Bearer ${storedToken}`
  }
  return config
},
(error: AxiosError): Promise<AxiosError> => Promise.reject(error)
)

// 添加响应拦截器
Service.interceptors.response.use((response:AxiosResponse):AxiosResponse<IResponse> | Promise<any>  => {
  try {
    // hideLoading()
  } catch (error) {}
  if (handleCode(response.data)) {
    return response
  }
  return Promise.reject()
}, (error: AxiosError<{
  message: string;
}>): Promise<AxiosError> => {
  try {
    // hideLoading()
  } catch (error) {}
  ElMessage({
    message: `${error.response?.data?.message || error.message || ''}`,
    type: 'error',
    duration: 3 * 1000
  })
  return Promise.reject(error)
})
