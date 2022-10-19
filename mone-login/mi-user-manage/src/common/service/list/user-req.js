import { Service } from '@/common/service/index';

export function getUserList(params) { //
  return Service({
    url: '/backend/user/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function changeUserStatus(params) { //
  return Service({
    url: '/backend/user/status',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
