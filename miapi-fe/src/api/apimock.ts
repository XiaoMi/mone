import { Service, IResponse } from '@/common/req'

/**
 * @description 根据api id获取Mock期望列表 POST
 * @param {number} apiID
 * @param {number} projectID
 */
export const getMockExpectList = (data): Promise<IResponse> => Service({
  url: '/Mock/getMockExpectList',
  method: 'post',
  data
})

/**
 * @description 接口新增和修改期望
 * @param {number} mockExpID
 * @param {string} mockExpName
 * @param {string} paramsJson
 * @param {string} mockData
 * @param {number} mockDataType // 0表单类型预览存入 1 用户自定义json串
 * @param {number} projectID
 * @param {number} apiID
 * @param {number} apiType // 1 http 2 dubbo 3 gateway
 * @param {string} mockScript
 * @param {boolean} enableMockScript
 */
export const editApiMockExpect = (data): Promise<IResponse> => Service({
  url: '/Api/editApiMockExpect',
  method: 'post',
  data
})

/**
 * @description 预览mock数据 POST
 * @param {string} mockRule
 */
export const previewMockData = (data): Promise<IResponse> => Service({
  url: '/Mock/previewMockData',
  method: 'post',
  data
})

/**
 * @description 根据mock期望id删除期望 POST
 * @param {number} mockExpectID
 * @param {number} projectID
 */
export const deleteMockExpect = (data): Promise<IResponse> => Service({
  url: '/Mock/deleteMockExpect',
  method: 'post',
  data
})

/**
 * @description 激活与禁用该期望  POST
 * @param {number} mockExpectID
 * @param {number} enable  // 1启用,0弃用
 * @param {number} projectID
 */
export const enableMockExpect = (data): Promise<IResponse> => Service({
  url: '/Mock/enableMockExpect',
  method: 'post',
  data
})

/**
 * @description 根据mock期望id获取Mock期望详情 POST
 * @param {number} mockExpectID
 */
export const getMockExpectDetail = (data): Promise<IResponse> => Service({
  url: '/Mock/getMockExpectDetail',
  method: 'post',
  data
})

/**
 * @description 自定义mock POST
 * @param {string} originUrl
 * @param {string} newUrl
 */
export const selfConfMockUrl = (data): Promise<IResponse> => Service({
  url: '/Mock/selfConfMockUrl',
  method: 'post',
  data
})
