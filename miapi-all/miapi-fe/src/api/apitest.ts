import { Service, IResponse } from '@/common/req'

/**
 * @description 获取项目下环境列表
 * @param {number} projectID
 */
export const getApiEnvListByProjectId = (data): Promise<IResponse> => Service({
  url: '/Project/getApiEnvListByProjectId',
  method: 'post',
  data
})

/**
 * @description 获取环境详情
 * @param {number} envID
 */
export const getApiEnvById = (data): Promise<IResponse> => Service({
  url: '/Project/getApiEnvById',
  method: 'post',
  data
})

/**
 * @description 删除API环境
 * @param {number} envID
 * @param {number} projectID
 */
export const deleteApiEnv = (data): Promise<IResponse> => Service({
  url: '/Project/deleteApiEnv',
  method: 'post',
  data
})

/**
 * @description 编辑API环境
 * @param {number} id
 * @param {string} envName
 * @param {string} httpDomain
 * @param {string} envDesc
 * @param {number} projectID
 */
export const editApiEnv = (data): Promise<IResponse> => Service({
  url: '/Project/editApiEnv',
  method: 'post',
  data
})

/**
 * @description 添加API环境
 * @param {string} envName
 * @param {string} httpDomain
 * @param {string} envDesc
 * @param {number} projectID
 */
export const addApiEnv = (data): Promise<IResponse> => Service({
  url: '/Project/addApiEnv',
  method: 'post',
  data
})

/**
 * @description 测试http接口
 * @param {string} method
 * @param {string} url
 * @param {number} timeout
 * @param {string} headers
 * @param {string} body
 */
export const httpTest = (data): Promise<IResponse> => Service({
  url: '/ApiTest/httpTest',
  method: 'post',
  data,
  timeout: 20000
})

/**
 * @description 测试dubbo接口
 * @param {string} interfaceName
 * @param {string} methodName
 * @param {string} group
 * @param {string} version
 * @param {boolean} production
 * @param {string} ip
 * @param {string} paramType
 * @param {string} parameter
 * @param {number} timeout
 * @param {string} addr
 */
export const dubboTest = (data): Promise<IResponse> => Service({
  url: '/ApiTest/dubboTest',
  method: 'post',
  data,
  timeout: 20000
})

/**
 * @description 获取接口方法集
 * @param {string} serviceName
 */
export const getServiceMethod = (data): Promise<IResponse> => Service({
  url: '/ApiTest/getServiceMethod',
  method: 'post',
  data
})

/**
* @description 调试grpc接口
* @param {string} packageName
* @param {string} interfaceName
* @param {string} methodName
* @param {string} parameter
* @param {number} timeout
* @param {string} addrs
*/
export const grpcTest = (data): Promise<IResponse> => Service({
  url: '/ApiTest/grpcTest',
  method: 'post',
  data,
  timeout: 20000
})

/**
* @description 保存Dubbo接口测试用例
* @param {string} caseName
* @param {int} apiId
* @param {int} requestTimeout
* @param {string} dubboInterface
* @param {string} dubboMethodName
* @param {string} dubboGroup
* @param {string} dubboVersion
* @param {int} retry
* @param {string} env
* @param {string} dubboAddr
* @param {string} dubboParamType
* @param {string} dubboParamBody
* @param {boolean} useGenericParam
* @param {boolean} useAttachment
* @param {string} attachment
* @param {int} caseGroupId
*/
export const saveDubboTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/saveDubboTestCase',
  method: 'post',
  data
})

/**
* @description 更新Dubbo接口测试用例
* @param {string} caseName
* @param {int} requestTimeout
* @param {string} dubboInterface
* @param {string} dubboMethodName
* @param {string} dubboGroup
* @param {string} dubboVersion
* @param {int} retry
* @param {string} env
* @param {string} dubboAddr
* @param {string} dubboParamType
* @param {string} dubboParamBody
* @param {boolean} useGenericParam
* @param {boolean} useAttachment
* @param {string} attachment
* @param {int} id
*/
export const updateDubboTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/updateDubboTestCase',
  method: 'post',
  data
})

/**
* @description 更新Http测试用例
* @param {string} caseName
* @param {string} httpMethod
* @param {string} url
* @param {int} envId
* @param {int} requestTimeout
* @param {string} httpHeaders
* @param {string} httpRequestBody
* @param {boolean} useX5Filter
* @param {string} x5AppKey
* @param {int} x5AppId
* @param {int} httpReqBodyType
* @param {int} id
*/
export const updateHttpTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/updateHttpTestCase',
  method: 'post',
  data
})

/**
* @description 保存http测试用例
* @param {string} caseName
* @param {int} apiId
* @param {string} httpMethod
* @param {string} url
* @param {int} envId
* @param {int} requestTimeout
* @param {string} httpHeaders
* @param {string} httpRequestBody
* @param {boolean} useX5Filter
* @param {string} x5AppKey
* @param {int} x5AppId
* @param {int} caseGroupId
* @param {int} httpReqBodyType
*/
export const saveHttpTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/saveHttpTestCase',
  method: 'post',
  data
})

/**
* @description 保存Gateway测试用例
* @param {string} caseName
* @param {int} apiId
* @param {string} httpMethod
* @param {string} url
* @param {int} requestTimeout
* @param {string} httpHeaders
* @param {string} httpRequestBody
* @param {boolean} useX5Filter
* @param {string} x5AppKey
* @param {int} x5AppId
* @param {int} caseGroupId
* @param {int} httpReqBodyType
* @param {string} gatewayDomain
*/
export const saveGatewayTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/saveGatewayTestCase',
  method: 'post',
  data
})

/**
* @description 更新Gateway测试用例
* @param {string} caseName
* @param {string} httpMethod
* @param {string} url
* @param {int} requestTimeout
* @param {string} httpHeaders
* @param {string} httpRequestBody
* @param {boolean} useX5Filter
* @param {string} x5AppKey
* @param {int} x5AppId
* @param {int} caseGroupId
* @param {int} httpReqBodyType
* @param {string} gatewayDomain
* @param {int} id
*/
export const updateGatewayTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/updateGatewayTestCase',
  method: 'post',
  data
})

/**
* @description 保存grpc接口测试用例
* @param {string} caseName
* @param {int} requestTimeout
* @param {string} appName
* @param {string} packageName
* @param {string} interfaceName
* @param {string} methodName
* @param {string} grpcAddr
* @param {string} grpcParamBody
* @param {int} id
*/
export const updateGrpcTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/updateGrpcTestCase',
  method: 'post',
  data
})

/**
* @description 保存grpc接口测试用例
* @param {string} caseName
* @param {int} apiId
* @param {int} requestTimeout
* @param {string} appName
* @param {string} packageName
* @param {string} interfaceName
* @param {string} methodName
* @param {string} grpcAddr
* @param {string} grpcParamBody
* @param {int} caseGroupId
*/
export const saveGrpcTestCase = (data): Promise<IResponse> => Service({
  url: '/ApiTest/saveGrpcTestCase',
  method: 'post',
  data
})

/**
* @description 获取具体api下的用例分组及列表
* @param {int} apiId
* @param {int} projectId
*/
export const getCasesByApi = (data): Promise<IResponse> => Service({
  url: '/ApiTest/getCasesByApi',
  method: 'post',
  data
})

/**
* @description 获取项目全局下的接口用例分组及case列表
* @param {int} projectId
*/
export const getCasesByProject = (data): Promise<IResponse> => Service({
  url: '/ApiTest/getCasesByProject',
  method: 'post',
  data
})

/**
* @description 添加用例文件夹
* @param {string} name
* @param {int} projectId
* @param {int} apiId
* @param {boolean} globalCase
*/
export const saveTestCaseDir = (data): Promise<IResponse> => Service({
  url: '/ApiTest/saveTestCaseDir',
  method: 'post',
  data
})

/**
* @description 根据id删除用例组
* @param {int} groupId
*/
export const deleteCaseGroup = (data): Promise<IResponse> => Service({
  url: '/ApiTest/deleteCaseGroup',
  method: 'post',
  data
})

/**
* @description 根据id删除用例
* @param {int} caseId
*/
export const deleteCaseById = (data): Promise<IResponse> => Service({
  url: '/ApiTest/deleteCaseById',
  method: 'post',
  data
})

/**
* @description 根据id获取用例详情
* @param {int} caseId
*/
export const getCaseDetailById = (data): Promise<IResponse> => Service({
  url: '/ApiTest/getCaseDetailById',
  method: 'post',
  data
})

/**
* @description 修改用例名
* @param {int} caseId
* @param {string} caseName
*/
export const updateCaseName = (data): Promise<IResponse> => Service({
  url: '/ApiTest/updateCaseName',
  method: 'post',
  data
})

/**
* @description 修改文件夹名
* @param {int} dirId
* @param {string} dirName
*/
export const updateCaseDirName = (data): Promise<IResponse> => Service({
  url: '/ApiTest/updateCaseDirName',
  method: 'post',
  data
})

/**
* @description 申请线上Dubbo接口调用权限
* @param {int} projectId
* @param {string} serviceName
* @param {string} group
* @param {string} version
*/
export const applyOnlineDubboTest = (data): Promise<IResponse> => Service({
  url: '/ApiTest/applyOnlineDubboTest',
  method: 'post',
  data
})
