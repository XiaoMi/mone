/* eslint-disable camelcase */
/* eslint-disable no-param-reassign */
import axios from 'axios';
import { Message } from 'element-ui';

function msgFn(msg) {
  Message({
    message: msg,
    type: 'error',
    duration: 3 * 1000,
  });
}

// 使用create方法创建axios实例
// eslint-disable-next-line import/prefer-default-export
export const Service = axios.create({
  timeout: 10000, // 请求超时时间
  method: 'post',
});

Service.interceptors.request.use(
  (config) => {
    if (config.method === 'post') {
      config.data = config.data || config.params;
      config.params = {};
    }
    return config;
  },
  (err) => Promise.reject(err),
);

// 添加响应拦截器
Service.interceptors.response.use((res) => {
  const { data } = res;
  if (data.code !== 0) {
    if (data.message) msgFn(data.message);
    return Promise.reject();
  }
  return data;
});
// , (error) => {
// if (error.response?.status === 401) {
//   const { tpc_token } = error.response.headers;
//   if (tpc_token === '1') {
//     window.location.href =
// `${window.location.origin}/user-manage/login?redirectUrl=${window.location.href}`;
//   }
// } else if (error.message && !error.message.startsWith('timeout')) {
//   msgFn(error.message);
// }
//   return Promise.reject(error);
// });
