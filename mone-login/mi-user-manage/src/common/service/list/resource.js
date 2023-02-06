import { Service } from '@/common/service/index';

export function getResourceList(params) { //
  return Service({
    url: '/backend/resource/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function deleteResource(params) { //
  return Service({
    url: '/backend/resource/delete',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function addResource(params) {
  return Service({
    url: '/backend/resource/add',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function editResource(params) {
  return Service({
    url: '/backend/resource/edit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getResourceDetail(params) {
  return Service({
    url: '/backend/resource/get',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function changeResourceStatus(params) {
  return Service({
    url: '/backend/resource/status',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getResourcePool(params) { // 资源池列表
  return Service({
    url: '/backend/resource/pool',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function deleteResourceRel(params) { // 解绑关联资源
  return Service({
    url: '/backend/resource/delRelation',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
