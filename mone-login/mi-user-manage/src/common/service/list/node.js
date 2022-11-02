import { Service } from '@/common/service/index';

export function getNodeFast() { //
  return Service({
    url: '/backend/node/fast',
    method: 'post',
    params: {},
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getNodeDetail(params) { //
  return Service({
    url: '/backend/node/get',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getNodeList(params) { //
  return Service({
    url: '/backend/node/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function deleteNode(params) { //
  return Service({
    url: '/backend/node/delete',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getOrgList(params) { // 节点部门
  return Service({
    url: '/backend/org/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
    timeout: 20 * 1000,
  });
}

export function editNode(params) {
  return Service({
    url: '/backend/node/edit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function addNode(params) {
  return Service({
    url: '/backend/node/add',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function changeNodeStatus(params) {
  return Service({
    url: '/backend/node/status',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function moveNodeReq(params) {
  return Service({
    url: '/backend/node/move',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function editNodeOrg(params) {
  return Service({
    url: '/backend/node/org/edit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getIamInfoApi(params) {
  return Service({
    url: '/backend/iam/get',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function editIamApi(params) {
  return Service({
    url: '/backend/node/iam/edit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
