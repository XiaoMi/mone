import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'

export const createToken = () => {
  return Service<IResponse<string>>({
    url: '/v1/users/token',
    method: 'post',
  })
}

export const getInfo = () => {
  return Service<IResponse<any>>({
    url: '/v1/users/info',
    method: 'get',
  })
}

export const bindInner = (data: {
  internalAccount: string
}) => {
  return Service<IResponse<any>>({
    url: '/v1/users/internal-account',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
}