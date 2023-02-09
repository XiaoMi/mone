import { Service } from '@/common/service/index';

export function checkAcount(params) { //
  return Service({
    url: '/login/register/check',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function registMember(params) { //
  return Service({
    url: '/login/register',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function checkAcountLogin(params) { //
  return Service({
    url: '/login/account/check',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function getLoginInfo(params) { //
  return Service({
    url: '/login/login',
    method: 'get',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function loginCall(params) { // 登录
  return Service({
    url: '/login/session',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function resetPwd(params) { // 登录
  return Service({
    url: '/login/pwd/reset',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function findPwd(params) { // 找回密码
  return Service({
    url: '/login/find',
    method: 'post',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function logoutReq(params) { // 退出
  return Service({
    url: '/login/logout',
    method: 'get',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
  });
}

export function loginCodereq(params) { // 第三方重定向
  return Service({
    url: '/login/code',
    method: 'get',
    params,
    baseURL: process.env.VUE_APP_POMISSION_DOMAIN,
    timeout: 20 * 1000,
  });
}
