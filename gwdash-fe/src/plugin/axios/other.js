/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

/**
 * 解决此域名资源请求：http://st.shopapi.io.mi.com
 */

import axios from 'axios'
import store from '@/store'
import { Message } from 'element-ui'

const service = axios.create({
  baseURL: '/mtop',
  timeout: 1000
})

// 请求拦截器
service.interceptors.request.use(config => {
  return config
},error => {
  console.log(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
  const {code,data} = res.data
  switch(code) {
    case 0: 
      return data;
    case 'xxx':
      showErrorLog(`${res.config.url}:[code:xxx]${xx}`)
      break
    default:
      showErrorLog(`${res.config.url}:${xx}`)
      break
  }
},error => {
  if (error && error.response) {
    switch (error.response.status) {
        case 400:
            error.message = '请求错误'
            break
        case 401:
            window.location.href=error.response.message || `${casUrl}/login?service=${encodeURIComponent(window.location.href)}`
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
    showErrorLog(error)
  }
})


function showErrorLog(msg) {
 const error = new Error(msg);
  // 添加到日志
  store.dispatch('d2admin/log/add', {
    type: 'error',
    error,
    info: '数据请求异常'
  })
  // 打印到控制台
  if (process.env.NODE_ENV === 'development') {
    util.log.danger('>>>>>> Error >>>>>>')
    console.log(error)
  }
  // 显示提示
  if(error.message==='timeout of 5000ms exceeded'){
    error.message="请求超时";
  }
  Message({
    message: error.message,
    type: 'error',
    duration: 5 * 1000
  })
  throw error
}

export default service