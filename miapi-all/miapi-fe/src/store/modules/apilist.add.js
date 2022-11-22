import { addGatewayApi, addDubboApi, addHttpApi, batchAddDubboApi } from '@/api/apilist'
import { PROTOCOL_TYPE, AJAX_SUCCESS_MESSAGE, API_REQUEST_PARAM_TYPE, ENVIRONMENT } from '@/views/constant'
import { ElMessage } from 'element-plus'
import { getQuery } from '@/utils'
import { handleFilter, handleCheckValue } from '../utils'
import router from '@/router'
import { PATH } from '@/router/constant'
import handleCommit from '@/common/commitInfo'
import i18n from '../../lang'

const state = {
  editValid: {},
  step: 1,
  addApiProtocol: '1',
  isDubboNewAdd: false,
  isHttpNewAdd: false,
  ipPort: '',
  serviceName: '',
  rawIsError: false,
  batchAddHttpParam: {
    httpModuleClassName: '',
    apiEnv: ENVIRONMENT.staging, // staging  online
    apiNames: [],
    // projectID: '',
    groupID: undefined,
    ip: '',
    port: '',
    forceUpdate: 0
  },
  httpParam: {
    apiName: '',
    apiURI: '',
    apiEnv: ENVIRONMENT.staging, // staging  online
    apiDesc: '',
    apiRemark: '',
    apiProtocol: '1', // 接口类型 http:1 https:2 dubbo:3  gateway:4
    apiRequestType: '0',
    apiStatus: '0', // 接口状态 (1:激活 0：无效)
    groupID: undefined,
    projectID: -1,
    apiNoteType: 0, // 接口详细说明文本类型
    apiNoteRaw: '', // 详细说明富文本
    apiNote: '', // 详细说明Markdown
    apiRequestParamType: API_REQUEST_PARAM_TYPE.FORM_DATA, // 接口请求类型 0：form-data  1 form-json 2 raw
    apiResponseParamType: API_REQUEST_PARAM_TYPE.JSON,
    apiResponseRaw: '',
    apiRequestRaw: '', // 接口源数据
    apiHeader: [],
    apiRequestParam: [],
    apiResultParam: [],
    apiErrorCodes: []
  },
  dubboParam: {
    apiDocName: "",
    apiGroup: "",
    apiRemark: '',
    apiDesc: '',
    namespace: 'default',
    apiStatus: undefined,
    apiModelClass: "",
    apiEnv: ENVIRONMENT.staging, // staging  online
    apiName: "",
    apiRespDec: "",
    apiVersion: "",
    async: false,
    description: "",
    apiNoteType: 0,
    apiNoteRaw: '',
    apiNote: '',
    params: [],
    projectId: -1,
    name: '',
    groupId: undefined,
    paramsLayerList: [],
    responseLayer: [],
    apiErrorCodes: []
  },
  gatewayParam: {
    name: '',
    description: '',
    apiDesc: '',
    apiRemark: '',
    url: '',
    path: '',
    projectId: -1,
    groupId: undefined,
    apiHeader: [],
    apiRequestParam: [],
    apiStatus: undefined,
    apiResultParam: [],
    apiNoteType: 0,
    apiNoteRaw: '',
    apiNote: '',
    apiRequestRaw: '',
    apiRequestParamType: API_REQUEST_PARAM_TYPE.FORM_DATA, // 接口请求类型 0：form-data  1 form-json 2 raw
    apiResponseParamType: API_REQUEST_PARAM_TYPE.JSON,
    apiResponseRaw: '',
    httpMethod: '',
    serviceName: '',
    routeType: 0,
    methodName: '',
    serviceGroup: '',
    serviceVersion: '',
    timeout: 0,
    env: 'staging',
    qpsLimit: 0,
    application: '',
    updater: '',
    paramTemplate: {},
    allowMock: true,
    contentType: '',
    status: 0, // 1 : 0,
    invokeLimit: 50,
    apiErrorCodes: []
  },
  grpcParam: {
    env: "staging",
    appName: "",
    symbol: "",
    ip: "",
    port: 0,
    projectID: 0,
    forceUpdate: true,
    serviceMethods: []
  },
  updateGrpcParam: {
    projectId: 0,
    apiPath: "",
    apiDesc: "",
    appName: "",
    apiRemark: "",
    requestParam: [],
    responseParam: [],
    updateMsg: "",
    apiErrorCodes: "{}"
  }
}

const mutations = {
  CHANGE_BATCH_HTTP_PARAM: (state, obj) => {
    Object.keys(obj).forEach((key) => {
      state.batchAddHttpParam[key] = obj[key]
    })
  },
  CHANGE_HTTP_PARAM: (state, obj) => {
    Object.keys(obj).forEach((key) => {
      state.httpParam[key] = obj[key]
    })
  },
  CHANGE_IP_PORT: (state, ipPort) => {
    state.ipPort = ipPort
  },
  CHANGE_SERVICE_NAME: (state, serviceName) => {
    state.serviceName = serviceName
  },
  CHANGE_DUBBO_PARAM: (state, obj) => {
    Object.keys(obj).forEach((key) => {
      state.dubboParam[key] = obj[key]
    })
  },
  CHANGE_GATEWAY_PARAM: (state, obj) => {
    Object.keys(obj).forEach((key) => {
      state.gatewayParam[key] = obj[key]
    })
  },
  CHANGE_GRPC_PARAM: (state, obj) => {
    Object.keys(obj).forEach((key) => {
      state.grpcParam[key] = obj[key]
    })
  },
  CHANGE_GRPC_UPDATE_PARAM: (state, obj) => {
    Object.keys(obj).forEach((key) => {
      state.updateGrpcParam[key] = obj[key]
    })
  },
  CHANGE_CAN_SAVE: (state, bool) => {
    state.editValid = bool
  },
  CHANGE_RAW_BOOL: (state, bool) => {
    state.rawIsError = bool
  },
  CHANGE_DUBBO_NEW_ADD: (state, bool) => {
    state.isDubboNewAdd = bool
  },
  CHANGE_HTTP_NEW_ADD: (state, bool) => {
    state.isHttpNewAdd = bool
  },
  CHANGE_ADD_APIPROTOCOL: (state, val) => {
    state.addApiProtocol = val
  },
  CHANGE_STEP: (state, step) => {
    state.step = step
  },
  RESET_BATCH_HTTP_PARAM: (state) => {
    state.batchAddHttpParam = {
      httpModuleClassName: '',
      apiNames: [],
      apiEnv: ENVIRONMENT.staging, // staging  online
      // projectID: '',
      groupID: undefined,
      ip: '',
      port: '',
      forceUpdate: 0
    }
  },
  RESET_HTTP_PARAM: (state) => {
    state.editHttpId = ''
    state.rawIsError = false
    state.httpParam = {
      apiName: '',
      apiURI: '',
      apiDesc: '',
      apiEnv: ENVIRONMENT.staging,
      apiRemark: '',
      apiProtocol: '1', // 接口类型 http:1 https:2 dubbo:3  gateway:4
      apiRequestType: '0',
      apiStatus: '0', // 接口状态 (1:激活 0：无效)
      groupID: undefined,
      projectID: -1,
      apiNoteType: 0, // 接口详细说明文本类型
      apiNoteRaw: '', // 详细说明富文本
      apiNote: '', // 详细说明Markdown
      apiRequestParamType: API_REQUEST_PARAM_TYPE.FORM_DATA, // 接口请求类型 0：form-data  1 form-json 2 raw
      apiResponseParamType: API_REQUEST_PARAM_TYPE.JSON,
      apiResponseRaw: '',
      apiRequestRaw: '', // 接口源数据
      apiHeader: [],
      apiRequestParam: [],
      apiResultParam: [],
      apiErrorCodes: []
    }
  },
  RESET_DUBBO_PARAM: (state, isAll = true) => {
    if (isAll) {
      state.ipPort = ''
      state.serviceName = undefined
    }
    state.rawIsError = false
    state.dubboParam = {
      apiDocName: "",
      apiGroup: "",
      apiRemark: '',
      namespace: 'default',
      apiDesc: '',
      apiModelClass: "",
      apiStatus: undefined,
      apiName: "",
      apiRespDec: "",
      apiEnv: ENVIRONMENT.staging, // staging  online
      apiVersion: "",
      async: false,
      description: "",
      apiNoteType: 0,
      apiNoteRaw: '',
      apiNote: '',
      name: '',
      params: [],
      projectId: -1,
      groupId: undefined,
      paramsLayerList: [],
      responseLayer: [],
      apiErrorCodes: []
    }
  },
  RESET_GATEWAY_PARAM: (state) => {
    state.rawIsError = false
    state.gatewayParam = {
      name: '',
      description: '',
      apiDesc: '',
      apiRemark: '',
      url: '',
      path: '',
      projectId: -1,
      groupId: undefined,
      apiHeader: [],
      apiStatus: undefined,
      apiRequestParam: [],
      apiResultParam: [],
      apiNoteType: 0,
      apiRequestRaw: '',
      apiRequestParamType: API_REQUEST_PARAM_TYPE.FORM_DATA, // 接口请求类型 0：form-data  1 form-json 2 raw
      apiResponseParamType: API_REQUEST_PARAM_TYPE.JSON,
      apiResponseRaw: '',
      apiNoteRaw: '',
      apiNote: '',
      httpMethod: '',
      serviceName: '',
      routeType: 0,
      methodName: '',
      serviceGroup: '',
      serviceVersion: '',
      timeout: 0,
      env: 'staging',
      qpsLimit: 0,
      application: '',
      updater: '',
      paramTemplate: {},
      allowMock: true,
      contentType: '',
      status: 0, // 1 : 0,
      invokeLimit: 50,
      apiErrorCodes: []
    }
  },
  RESET_GRPC_PARAM: (state) => {
    state.grpcParam = {
      env: "staging",
      symbol: "",
      ip: "",
      port: 0,
      projectID: 0,
      forceUpdate: true,
      serviceMethods: []
    }
  },
  RESET_GRPC_UPDATE_PARAM: (state) => {
    state.updateGrpcParam = {
      projectId: 0,
      apiPath: "",
      apiDesc: "",
      apiRemark: "",
      requestParam: {},
      responseParam: {},
      updateMsg: "",
      apiErrorCodes: "{}"
    }
  }
}

const actions = {
  changeBatchHttpParam ({ commit }, obj) {
    commit('CHANGE_BATCH_HTTP_PARAM', obj)
  },
  changeHttpParam ({ commit }, obj) {
    commit('CHANGE_HTTP_PARAM', obj)
  },
  changeDubboParam ({ commit }, obj) {
    commit('CHANGE_DUBBO_PARAM', obj)
  },
  changeGatewayParam ({ commit }, obj) {
    commit('CHANGE_GATEWAY_PARAM', obj)
  },
  changeGrpcParam ({ commit }, obj) {
    commit('CHANGE_GRPC_PARAM', obj)
  },
  changeGrpcUpdateParam ({ commit }, obj) {
    commit('CHANGE_GRPC_UPDATE_PARAM', obj)
  },
  changeAddApiProtocol ({ commit }, val) {
    commit('CHANGE_ADD_APIPROTOCOL', val)
  },
  changeStep ({ commit }, step) {
    commit('CHANGE_STEP', step)
  },
  changeCanSave ({ commit }, bool) {
    commit('CHANGE_CAN_SAVE', bool)
  },
  changeDubboNewAdd ({ commit }, bool) {
    commit('CHANGE_DUBBO_NEW_ADD', bool)
  },
  changeHttpNewAdd ({ commit }, bool) {
    commit('CHANGE_HTTP_NEW_ADD', bool)
  },
  changeIpPort ({ commit }, ipPort) {
    commit('CHANGE_IP_PORT', ipPort)
  },
  changeDubboServiceName ({ commit }, name) {
    commit('CHANGE_SERVICE_NAME', name)
  },
  changeIsRawBool ({ commit }, bool) {
    commit('CHANGE_RAW_BOOL', bool)
  },
  resetBatchHttpParam ({ commit }) {
    commit('RESET_BATCH_HTTP_PARAM')
  },
  resetHttpParam ({ commit }) {
    commit('RESET_HTTP_PARAM')
  },
  resetDubboParam ({ commit }, isAll) {
    commit('RESET_DUBBO_PARAM', isAll)
  },
  resetGatewayParam ({ commit }) {
    commit('RESET_GATEWAY_PARAM')
  },
  resetGrpcParam ({ commit }) {
    commit('RESET_GRPC_PARAM')
  },
  resetGrpcUpdateParam ({ commit }) {
    commit('RESET_GRPC_UPDATE_PARAM')
  },
  handleSubmit ({ state, commit, dispatch }) {
    let projectID = getQuery('projectID')
    let params = {}
    if (state.rawIsError) {
      ElMessage.error(i18n.t('errorMessage.jsonFormatWrong'))
      return
    }
    switch (state.addApiProtocol) {
      case PROTOCOL_TYPE.HTTP:
        params = {
          ...state.httpParam,
          apiRequestRaw: typeof state.httpParam.apiRequestRaw === 'string' ? state.httpParam.apiRequestRaw : JSON.stringify(state.httpParam.apiRequestRaw),
          apiHeader: state.httpParam.apiHeader.filter(item => !!item.headerName || !!item.headerValue),
          apiRequestParam: handleFilter(state.httpParam.apiRequestParam),
          apiResultParam: handleFilter(state.httpParam.apiResultParam),
          projectID
        }
        if (handleCheckValue(params, state.addApiProtocol)) {
          return
        }
        params = {
          ...params,
          apiHeader: JSON.stringify(params.apiHeader),
          apiRequestParam: JSON.stringify(params.apiRequestParam),
          apiResultParam: JSON.stringify(params.apiResultParam),
          apiResponseParamType: params.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON,
          apiErrorCodes: JSON.stringify(params.apiErrorCodes)
        }
        handleCommit((updateMsg) => {
          params.updateMsg = updateMsg
          addHttpApi(params).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              ElMessage.success(i18n.t('addedSuccessfully'))
              commit('CHANGE_STEP', 1)
              commit('RESET_HTTP_PARAM')
              dispatch('apilist.group/getGroupViewList', projectID, { root: true })
              router.push({ path: PATH.API, query: { projectID } })
            }
          }).catch(e => {})
        })
        break
      case PROTOCOL_TYPE.Dubbo:
        let paramsLayerList = handleFilter(state.dubboParam.paramsLayerList, 'dubbo')
        let responseLayer = handleFilter(state.dubboParam.responseLayer, 'dubbo')

        if (handleCheckValue({ apiResultParam: responseLayer, apiRequestParam: paramsLayerList }, state.addApiProtocol)) {
          return
        }

        handleCommit((updateMsg) => {
          addDubboApi({
            apiVersion: '',
            apiGroup: '',
            ...state.dubboParam,
            paramsLayerList,
            apiName: Array.isArray(state.dubboParam.apiName) ? state.dubboParam.apiName[0] : state.dubboParam.apiName,
            responseLayer: responseLayer[0] || {},
            apiErrorCodes: JSON.stringify(state.dubboParam.apiErrorCodes),
            projectId: projectID,
            updateMsg
          }).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              ElMessage.success(i18n.t('addedSuccessfully'))
              commit('CHANGE_STEP', 1)
              commit('RESET_DUBBO_PARAM')
              dispatch('apilist.group/getGroupViewList', projectID, { root: true })
              router.push({ path: PATH.API, query: { projectID } })
            }
          }).catch(e => {})
        })
        break
      case PROTOCOL_TYPE.Gateway:
        let apiHeader = state.gatewayParam.apiHeader.filter(item => !!item.headerName || !!item.headerValue)
        let apiResultParam = handleFilter(state.gatewayParam.apiResultParam)
        let apiRequestParam = handleFilter(state.gatewayParam.apiRequestParam)
        if (handleCheckValue({ apiHeader, apiResultParam, apiRequestParam }, state.addApiProtocol)) {
          return
        }
        params = {
          ...state.gatewayParam,
          apiRequestRaw: typeof state.gatewayParam.apiRequestRaw === 'string' ? state.gatewayParam.apiRequestRaw : JSON.stringify(state.gatewayParam.apiRequestRaw),
          apiHeader: JSON.stringify(apiHeader),
          apiResultParam: apiResultParam.length ? JSON.stringify(apiResultParam) : JSON.stringify({}),
          apiRequestParam: JSON.stringify(apiRequestParam),
          status: state.gatewayParam.status ? 1 : 0,
          projectId: projectID,
          apiErrorCodes: JSON.stringify(state.gatewayParam.apiErrorCodes),
          allowMock: true,
          apiResponseParamType: state.gatewayParam.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON
        }
        delete params.apiUpdateTime
        delete params.mockData
        delete params.mockDataDesc
        handleCommit((updateMsg) => {
          params.updateMsg = updateMsg
          addGatewayApi(params).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              ElMessage.success(i18n.t('addedSuccessfully'))
              commit('CHANGE_STEP', 1)
              commit('RESET_GATEWAY_PARAM')
              dispatch('apilist.group/getGroupViewList', projectID, { root: true })
              router.push({ path: PATH.API, query: { projectID } })
            }
          }).catch(e => {})
        })
        break
      default:
        break
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
