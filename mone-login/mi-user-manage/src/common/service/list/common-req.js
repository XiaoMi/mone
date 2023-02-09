import { Service } from '@/common/service/index';

export function getAllUser(params) { //
  return Service({
    url: '/backend/user/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getDic(params) {
  return Service({
    url: '/login/enum/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getMyInfoDetail(params) {
  return Service({
    url: '/backend/user/my',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
