import { Service, IResponse } from '@/common/req'

/**
 * @description 获取api的接口历史列表
 * @param {number} apiID
 */
export const getHistoryRecordList = (data): Promise<IResponse> => Service({
  url: '/ApiHistory/getHistoryRecordList',
  method: 'post',
  data
})

/**
 * @description api接口版本比对
 * @param {number} apiID
 * @param {number} recordID
 */
export const compareWithOldVersion = (data): Promise<IResponse> => Service({
  url: '/ApiHistory/compareWithOldVersion',
  method: 'post',
  data
})

/**
 * @description 获取接口历史详情
 * @param {number} recordID
 */
export const getHistoryRecordById = (data): Promise<IResponse> => Service({
  url: '/ApiHistory/getHistoryRecordById',
  method: 'post',
  data
})


/**
 * @description 回滚接口版本
 * @param {number} apiID
 * @param {number} targetHisID
 */
 export const getRollbackToHis = (data): Promise<IResponse> => Service({
  url: '/ApiHistory/rollbackToHis',
  method: 'post',
  data
})