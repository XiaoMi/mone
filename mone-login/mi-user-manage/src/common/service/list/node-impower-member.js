import { Service } from '@/common/service/index';

export function getNodeImpowerMemberList(params) { //
  return Service({
    url: '/backend/node/user/role/list',
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
