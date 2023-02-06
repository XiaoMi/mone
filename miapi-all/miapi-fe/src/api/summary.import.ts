import { Service, IResponse } from '@/common/req'

/**
 * @description 导入swagger的json文件 POST
 * @param {object} swaggerData
 * @param {number} projectID
 */
export const importSwaggerApi = (data): Promise<IResponse> => Service({
  url: '/ApiImport/importSwaggerApi',
  method: 'post',
  data,
  timeout: 30000
})
