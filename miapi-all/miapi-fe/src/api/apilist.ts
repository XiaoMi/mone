import { Service, IResponse } from '@/common/req'

/**
 * @description 获取组列表
 * @param {number} projectID
 */
export const getGroup = (data): Promise<IResponse> => Service({
  url: '/ApiGroup/getGroupList',
  method: 'post',
  data
})

/**
 * @description 删除组
 * @param {number} projectID
 * @param {number} groupID
 *
 */
export const deleteGroup = (data): Promise<IResponse> => Service({
  url: '/ApiGroup/deleteGroup',
  method: 'post',
  data
})

/**
 * @description 添加api分组
 * @param {string} groupName
 * @param {number} projectID
 *
 */
export const addGroup = (data): Promise<IResponse> => Service({
  url: '/ApiGroup/addGroup',
  method: 'post',
  data
})

/**
 * @description 修改接口分组信息
 * @param {string} groupName
 * @param {number} groupID
 * @param {number} projectID
 *
 */
export const editGroup = (data): Promise<IResponse> => Service({
  url: '/ApiGroup/editGroup',
  method: 'post',
  data
})

/**
 * @description 获取回收站列表
 * @param {number} orderBy
 * @param {number} projectID
 * @param {number} asc
 */
export const getRecyclingStationApiList = (data): Promise<IResponse> => Service({
  url: '/Api/getRecyclingStationApiList',
  method: 'post',
  data
})

/**
 * @description 获取分组接口列表
 * @param {number} orderBy
 * @param {number} projectID
 * @param {number} asc
 * @param {number} groupID
 * @param {number} pageNo
 * @param {number} pageSize
 */
export const getApiList = (data): Promise<IResponse> => Service({
  url: '/Api/getApiList',
  method: 'post',
  data
})

/**
 * @description 获取所有api接口
 * @param {number} orderBy  （0：apiName、1：apiUpdateTime  2:starred  3:apiID）
 * @param {number} projectID
 * @param {number} asc  (0:正序  1:倒序)
 * @param {number} groupID
 * @param {number} pageNo
 * @param {number} pageSize
 */
export const getAllApiList = (data): Promise<IResponse> => Service({
  url: '/Api/getAllApiList',
  method: 'post',
  data
})

/**
 * @description 查询获取接口详情
 * @param {number} projectID
 * @param {string} tips
 */
export const searchApi = (data): Promise<IResponse> => Service({
  url: '/Api/searchApi',
  method: 'post',
  data
})

/**
 * @description 删除接口到回收站
 */
export const removeApi = (data): Promise<IResponse> => Service({
  url: '/Api/removeApi',
  method: 'post',
  data
})

/**
 * @description 删除接口
 */
export const deleteApi = (data): Promise<IResponse> => Service({
  url: '/Api/deleteApi',
  method: 'post',
  data
})

/**
 * @description 清空回收站
 * @param {number} projectID
 */
export const cleanRecyclingStation = (data): Promise<IResponse> => Service({
  url: '/Api/cleanRecyclingStation',
  method: 'post',
  data
})

/**
 * @description 添加http类型api
 * @param {number} projectID
 */
export const addHttpApi = (data): Promise<IResponse> => Service({
  url: '/Api/addHttpApi',
  method: 'post',
  data
})

/**
 * @description 编辑http类型api
 */
export const editHttpApi = (data): Promise<IResponse> => Service({
  url: '/Api/editHttpApi',
  method: 'post',
  data
})

/**
 * @description 添加dubbo类型api
 */
export const addDubboApi = (data): Promise<IResponse> => Service({
  url: '/Api/addDubboApi',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * @description 添加dubbo类型api
 * @param {number} projectID
 * @param {number} apiID
 */
export const getHttpApi = (data): Promise<IResponse> => Service({
  url: '/Api/getHttpApi',
  method: 'post',
  data
})

/**
 * @description 加载http Controller
 * @param {string} serviceName
 */
export const loadHttpApiServices = (data): Promise<IResponse> => Service({
  url: '/Api/loadHttpApiInfos',
  method: 'post',
  data
})

/**
 * @description 批量添加http接口
 * @param {string} moduleClassName
 * @param {Array} apiNames
 * @param {number} projectID
 * @param {number} groupID
 * @param {string} ip
 * @param {number} port
 * @param {boolean} forceUpdate
 */
export const batchAddHttpApi = (data): Promise<IResponse> => Service({
  url: '/Api/batchAddHttpApi',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * @description 根据服务名搜索可加载服务
 * @param {string} serviceName
 * @param {string} env
 */
export const loadDubboApiServices = (data): Promise<IResponse> => Service({
  url: '/Api/loadDubboApiServices',
  method: 'post',
  data
})

/**
 * @description 加载dubbo api相关选项信息
 * @param {string} serviceName
 * @param {string} env
 * @param {string} ip
 */
export const loadDubboApiInfos = (data): Promise<IResponse> => Service({
  url: '/Api/loadDubboApiInfos',
  method: 'post',
  data
})

/**
 * @param {string} ip
 * @param {number} port
 * @param {string} moduleClassName
 * @param {string} apiName
 */
export const getDubboApiDetailRemote = (data): Promise<IResponse> => Service({
  url: '/Api/getDubboApiDetailRemote',
  method: 'post',
  data
})

/**
 * @description 获取dubbo类型api详情
 * @param {number} projectID
 * @param {number} apiID
 */
export const getDubboApiDetail = (data): Promise<IResponse> => Service({
  url: '/Api/getDubboApiDetail',
  method: 'post',
  data
})

/**
 * @description 更新Dubbo类型API
 * @param {object} apiBo
 * @param {number} apiID
 */
export const updateDubboApi = (data): Promise<IResponse> => Service({
  url: '/Api/updateDubboApi',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * @description 获取http mock数据
 * @param {number} projectID
 * @param {number} apiID
 */
export const getHttpApiMockData = (data): Promise<IResponse> => Service({
  url: '/Api/getHttpApiMockData',
  method: 'post',
  data
})

/**
 * @description 获取mock数据
 * @param {number} projectID
 * @param {number} apiID
 */
export const dubboApiMock = (data): Promise<IResponse> => Service({
  url: '/Mock/dubboApiMock',
  method: 'post',
  data
})

/**
 * @description 添加mock数据
 * @param {number} projectID
 * @param {number} apiID
 * @param {string} dubboApiMockData
 */
export const addDubboApiMockData = (data): Promise<IResponse> => Service({
  url: '/Api/addDubboApiMockData',
  method: 'post',
  data
})

/**
 * @description http接口详情历史
 * @param {number} projectID
 * @param {number} apiID
 */
export const getApiHistoryList = (data): Promise<IResponse> => Service({
  url: '/Api/getApiHistoryList',
  method: 'post',
  data
})

/**
 * @description 添加接口星标
 * @param {number} projectID
 * @param {number} apiID
 */
export const addStar = (data): Promise<IResponse> => Service({
  url: '/Api/addStar',
  method: 'post',
  data
})

/**
 * @description 移除接口星标
 * @param {number} projectID
 * @param {number} apiID
 */
export const removeStar = (data): Promise<IResponse> => Service({
  url: '/Api/removeStar',
  method: 'post',
  data
})

/**
 * @description 获取mock
 * @param {string} mockRule
 */
export const previewHttpMockData = (data): Promise<IResponse> => Service({
  url: '/Mock/previewHttpMockData',
  method: 'post',
  data
})

/**
 * @description 加载网关类型api
 * @param {string} url
 */
export const loadGatewayApiInfo = (data): Promise<IResponse> => Service({
  url: '/Api/loadGatewayApiInfo',
  method: 'post',
  data
})

/**
 * @description 保存单个gateway类型网关接口
 */
export const addGatewayApi = (data): Promise<IResponse> => Service({
  url: '/Api/addGatewayApi',
  method: 'post',
  data
})

/**
 * @description 批量加载网关接口
 * @param {number} projectID
 * @param {number} groupID
 * @param {string} env
 * @param {string} urlList
 */
export const batchAddGatewayApi = (data): Promise<IResponse> => Service({
  url: '/Api/batchAddGatewayApi',
  method: 'post',
  data
})

/**
 * @description 批量保存gateway类型网关接口
 */
export const batchAddDubboApi = (data): Promise<IResponse> => Service({
  url: '/Api/batchAddDubboApi',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * @description 更新gateway类型网关接口
 */
export const updateGatewayApi = (data): Promise<IResponse> => Service({
  url: '/Api/updateGatewayApi',
  method: 'post',
  data
})

/**
 * @description 获取gateway类型网关接口详情
 * @param {number} apiID
 * @param {number} projectID
 */
export const getGatewayApiDetail = (data): Promise<IResponse> => Service({
  url: '/Api/getGatewayApiDetail',
  method: 'post',
  data
})

/**
 * @description 更新接口状态 POST
 * @param {number} apiID
 * @param {number} projectID
 * @param {number} status
 */
export const editApiStatus = (data): Promise<IResponse> => Service({
  url: '/Api/editApiStatus',
  method: 'post',
  data
})

/**
 * @description api分组下拉下级列表
 * @param {number} groupID
 * @param {number} projectID
 */
export const getGroupApiViewList = (data): Promise<IResponse> => Service({
  url: '/Api/getGroupApiViewList',
  method: 'post',
  data
})

/**
 * @description 手动同步Dubbo接口
 * @param {string} serviceName
 * @param {string} methodName
 * @param {string} group
 * @param {string} version
 * @param {string} env
 * @param {string} updateMsg
 */
export const manualUpdateDubboApi = (data): Promise<IResponse> => Service({
  url: '/Api/manualUpdateDubboApi',
  method: 'post',
  data
})

/**
 * @description 手动同步Http接口数据
 * @param {number} apiID
 * @param {number} projectID
 * @param {string} updateMsg
 */
export const manualUpdateHttpApi = (data): Promise<IResponse> => Service({
  url: '/Api/manualUpdateHttpApi',
  method: 'post',
  data
})

/**
 * @description 手动同步Gateway接口
 * @param {number} apiID
 * @param {number} projectID
 * @param {string} updateMsg
 * @param {string} env
 */
export const manualUpdateGatewayApi = (data): Promise<IResponse> => Service({
  url: '/Api/manualUpdateGatewayApi',
  method: 'post',
  data
})

/**
* @description 获取 Grpc 接口详情
* @param {number} projectID
* @param {number} apiID
*/
export const getGrpcApiDetail = (data): Promise<IResponse> => Service({
  url: '/Api/getGrpcApiDetail',
  method: 'post',
  data
})

/**
* @description 更新Grpc接口
* @param {number} projectId
* @param {string} apiPath
* @param {string} apiDesc
* @param {string} apiRemark
* @param {object} requestParam
* @param {object} responseParam
* @param {string} updateMsg
* @param {string} apiErrorCodes
*/
export const updateGrpcApi = (data): Promise<IResponse> => Service({
  url: '/Api/updateGrpcApi',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
* @description 批量加载Grpc接口
* @param {string} env
* @param {string} symbol
* @param {string} ip
* @param {number} port
* @param {number} projectID
* @param {boolean} forceUpdate
* @param {Array} serviceMethods
*/
export const batchAddGrpcApi = (data): Promise<IResponse> => Service({
  url: '/Api/batchAddGrpcApi',
  method: 'post',
  data,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
* @description 加载搜索grpc服务方法及接口集
* @param {string} appName
*/
export const loadGrpcApiInfos = (data): Promise<IResponse> => Service({
  url: '/Api/loadGrpcApiInfos',
  method: 'post',
  data
})

/**
* @description 搜索Grpc服务
* @param {string} serviceName
*/
export const loadGrpcService = (data): Promise<IResponse> => Service({
  url: '/Api/loadGrpcService',
  method: 'post',
  data
})

/**
* @description 获取Grpc服务地址
* @param {string} appName
*/
export const loadGrpcServerAddr = (data): Promise<IResponse> => Service({
  url: '/Api/loadGrpcServerAddr',
  method: 'post',
  data
})

/**
* @description 编辑自定义期望
* @param {number} apiID
* @param {number} expType
* @param {number} type
* @param {string} content
*/
export const editApiDiyExp = (data): Promise<IResponse> => Service({
  url: '/Api/editApiDiyExp',
  method: 'post',
  data
})


export const batchHttp = (data): Promise<IResponse> => Service({
  url: '/Api/getBatchHttpApi',
  method: 'post',
  data
})
export const batchGateway = (data): Promise<IResponse> => Service({
  url: '/Api/getBatchGatewayApiDetail',
  method: 'post',
  data
})
export const batchDubbo = (data): Promise<IResponse> => Service({
  url: '/Api/getBatchDubboApiDetail',
  method: 'post',
  data
})
export const batchGrpc = (data): Promise<IResponse> => Service({
  url: '/Api/getBatchGrpcApiDetail',
  method: 'post',
  data
})