import { Service, IResponse } from '@/common/req'

/**
 * @description 获取索引组列表
 * @param {number} projectID
 */
export const getIndexList = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/getIndexList',
  method: 'post',
  data
})

/**
 * @description 添加索引组列表
 * @param {number} projectID
 * @param {string} indexName
 * @param {string} indexDoc
 */
export const addIndex = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/addIndex',
  method: 'post',
  data
})

/**
 * @description 编辑索引组列表
 * @param {string} indexName
 * @param {number} projectID
 * @param {number} indexID
 * @param {string} indexDoc
 */
export const editIndex = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/editIndex',
  method: 'post',
  data
})

/**
 * @description 删除索引组列表
 * @param {number} indexID
 */
export const deleteIndex = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/deleteIndex',
  method: 'post',
  data
})

/**
 * @description 将接口拉入某个索引组
 * @param {number} indexID
 * @param {string} apiIDs
 * @param {number} projectID
 */
export const addApiToIndex = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/addApiToIndex',
  method: 'post',
  data
})

/**
 * @description 根据索引组拉取接口列表
 * @param {number} indexID
 * @param {number} projectID
 */
export const getApiListByIndex = (data): Promise<IResponse> => Service({
  url: '/Api/getApiListByIndex',
  method: 'post',
  data
})

/**
 * @description 拉去所有索引分组下的接口
 * @param {number} projectID
 */
export const getAllIndexGroupApiViewList = (data): Promise<IResponse> => Service({
  url: '/Api/getAllIndexGroupApiViewList',
  method: 'post',
  data
})

/**
 * @description 从集合移除接口
 * @param {number} projectID
 * @param {number} apiID
 * @param {number} indexID
 */
export const removeApiFromIndex = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/removeApiFromIndex',
  method: 'post',
  data
})

/**
 * @description 获取集合页详细数据接口
 * @param {numbers} indexIDs
 * @param {numbers} projectID
 */
export const getIndexPageInfo = (data): Promise<IResponse> => Service({
  url: '/ApiIndex/getIndexPageInfo',
  method: 'post',
  data
})

/**
 * @description 获取外网集合页详细数据接口
 * @param {numbers} indexIDs
 */
export const getOuterIndexPageInfo = (data): Promise<IResponse> => Service({
  url: 'xxx',
  method: 'post',
  withCredentials: false,
  data
})
