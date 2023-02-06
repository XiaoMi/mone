import { Service } from '@/common/service/index';

export function addApply(params) {
  return Service({
    url: '/backend/apply/submit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function resubmitApply(params) {
  return Service({
    url: '/backend/apply/resubmit',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}
