import { Service } from '@/common/service/index';

export function getOrderList(params) { // 工单列表
  return Service({
    url: '/backend/apply/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getApplyDetail(params) { // 工单详情
  return Service({
    url: '/backend/apply/get',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function changeApplyStatus(params) { // 修改我申请的工单状态
  return Service({
    url: '/backend/apply/status',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function changeApprovalStatus(params) { // 修改待我审核工单状态
  return Service({
    url: '/backend/approval/status',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getApprovalList(params) { // 审核记录
  return Service({
    url: '/backend/approval/list',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
