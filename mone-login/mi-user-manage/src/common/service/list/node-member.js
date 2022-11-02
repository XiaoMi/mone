import { Service } from '@/common/service/index';

export function getMemberList(params) { //
  return Service({
    url: '/backend/node/user/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function editMember(params) {
  return Service({
    url: '/backend/node/user/edit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function addMember(params) {
  return Service({
    url: '/backend/node/user/add',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function deleteMember(params) {
  return Service({
    url: '/backend/node/user/delete',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
