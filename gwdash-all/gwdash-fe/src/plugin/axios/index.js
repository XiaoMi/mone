/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import store from '@/store'
import axios from 'axios'
import { Message } from 'element-ui'
import util from '@/libs/util'

const casUrl = process.env.VUE_APP_CAS_URL

// 创建一个错误
function errorCreat (msg) {
  const err = new Error(msg)
  errorLog(err)
  throw err
}

// 记录和显示错误
function errorLog (err) {
  // 添加到日志
  store.dispatch('d2admin/log/add', {
    type: 'error',
    err,
    info: '数据请求异常'
  })
  // 打印到控制台
  if (process.env.NODE_ENV === 'development') {
    util.log.danger('>>>>>> Error >>>>>>')
    console.log(err)
  }
  // 显示提示
  if (err.message === 'timeout of 5000ms exceeded') {
    err.message = '请求超时'
  }
  Message({
    message: err.message,
    type: 'error',
    duration: 5 * 1000
  })
}

// 创建一个 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_API,
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在请求发送之前做一些处理
    if (!(/^https:\/\/|http:\/\//.test(config.url))) {
      const token = util.cookies.get('token')
      if (token && token !== 'undefined') {
        // 让每个请求携带token-- ['X-Token']为自定义key 请根据实际情况自行修改
        config.headers['X-Token'] = token
      }
    }
    if (/^\/open\//.test(config.url)) {
      config.baseURL = '/'
    }
    return config
  },
  error => {
    // 发送失败
    console.log(error)
    Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // dataAxios 是 axios 返回数据中的 data
    const dataAxios = response.data
    // 这个状态码是和后端约定的
    const { code } = dataAxios
    // 根据 code 进行判断
    if (code === undefined) {
      // 如果没有 code 代表这不是项目后端开发的接口 比如可能是 D2Admin 请求最新版本
      return dataAxios
    } else {
      // 有 code 代表这是一个后端接口 可以进行进一步的判断
      switch (code) {
        case 0:
          // [ 示例 ] code === 0 代表没有错误
          return dataAxios.data
        case 'xxx':
          // [ 示例 ] 其它和后台约定的 code
          errorCreat(`${response.config.url}: [ code: xxx ] ${dataAxios.msg}`)
          break
        default:
          // 不是正确的 code
          errorCreat(`${response.config.url}: ${dataAxios.message}`)
          break
      }
    }
  },
  error => {
    if (error && error.response) {
      switch (error.response.status) {
        case 400:
          error.message = '请求错误'
          break
        case 401:
          window.location.href = error.response.message || `${casUrl}/login?service=${encodeURIComponent(window.location.href)}`
          error.message = '未授权，请登录'
          break
        case 403:
          error.message = '权限不够，如需调整请联系架构组同学'
          break
        case 404:
          error.message = `请求地址出错: ${error.response.config.url}`
          break
        case 408:
          error.message = '请求超时'
          break
        case 500:
          error.message = '服务器内部错误'
          break
        case 501:
          error.message = '服务未实现'
          break
        case 502:
          error.message = '网关错误'
          break
        case 503:
          error.message = '服务不可用'
          break
        case 504:
          error.message = '网关超时'
          break
        case 505:
          error.message = 'HTTP版本不受支持'
          break
        default:
          break
      }
    }
    errorLog(error)
    return Promise.reject(error)
  }
)

export default service
