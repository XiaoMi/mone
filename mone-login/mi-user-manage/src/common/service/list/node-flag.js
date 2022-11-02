import { Service } from '@/common/service/index';

export function getFlagList(params) { //
  return Service({
    url: '/backend/node/flag/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function addFlag(params) {
  return Service({
    url: '/backend/node/flag/add',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function editFlag(params) {
  return Service({
    url: '/backend/node/flag/edit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getFlagDetail(params) {
  return Service({
    url: '/backend/node/flag/get',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function deleteFlag(params) {
  return Service({
    url: '/backend/node/flag/delete',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
