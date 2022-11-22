import { Service, IResponse } from '@/common/req'

/**
 * @description 获取协作人员列表
 * @param {number} projectID
 */
export const getPartnerList = (data): Promise<IResponse> => Service({
  url: '/Partner/getPartnerList',
  method: 'post',
  data
})

/**
 * @description 获取用户信息
 * @param {number} projectID
 */
export const getUserInfo = (): Promise<IResponse> => Service({
  url: '/Partner/getUserInfo',
  method: 'get'
})

/**
 * @description 批量邀请、移除用户
 * @param {number[]} userIds
 * @param {number} projectID
 * @param {number} roleType
 */
export const invitePartner = (data): Promise<IResponse> => Service({
  url: '/Partner/invitePartner',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * @description 修改人员角色接口
 * @param {number} userID
 * @param {number} projectID
 * @param {string} roleTypes
 */
export const editPartnerRole = (data): Promise<IResponse> => Service({
  url: '/Partner/editPartnerRole',
  method: 'post',
  data
})

/**
 * @description 删除人员接口
 * @param {number} userID
 * @param {number} projectID
 */
export const removePartner = (data): Promise<IResponse> => Service({
  url: '/Partner/removePartner',
  method: 'post',
  data
})

/**
 * @description 获取全部用户
 */
export const getAllPartnerList = (data): Promise<IResponse> => Service({
  url: '/Partner/getAllPartnerList',
  method: 'get'
})

/**
 * @description 获取项目组成员列表
 * @param {number} groupID
 */
export const getGroupPartnerList = (data): Promise<IResponse> => Service({
  url: '/Partner/getGroupPartnerList',
  method: 'post',
  data
})

/**
 * @description 移除项目组成员
 * @param {number} groupID
 * @param {number} userID
 */
export const removeGroupPartner = (data): Promise<IResponse> => Service({
  url: '/Partner/removeGroupPartner',
  method: 'post',
  data
})

/**
 * @description 修改项目组人员角色
 * @param {number} groupID
 * @param {number} userID
 * @param {string} roleTypes
 */
export const editGroupPartnerRole = (data): Promise<IResponse> => Service({
  url: '/Partner/editGroupPartnerRole',
  method: 'post',
  data
})

/**
 * @description 添加项目组成员
 * @param {number} inviterUserID
 * @param {Array} userIds
 * @param {number} groupID
 * @param {number} roleType
 */
export const inviteGroupPartner = (data): Promise<IResponse> => Service({
  url: '/Partner/inviteGroupPartner',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})
