
import { RADIO_TYPE, DEFAULT_HEADER } from '@/views/TestApi/constant'
import { ENVIRONMENT, PROTOCOL_TYPE, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { getApiEnvListByProjectId } from '@/api/apitest'

const state = {
  hostEnvList: [],
  apiTestProtocol: PROTOCOL_TYPE.HTTP,
  apiTestBodyType: RADIO_TYPE.FORM,
  requestType: '',
  url: '',
  httpEnv: undefined,
  headers: [],
  body: [],
  jsonBody: {},
  query: [],
  // dubbo
  version: '',
  ip: '',
  paramType: '', // 参数类型
  parameter: [], // 参数列表
  interfaceName: '',
  methodName: '',
  serviceNameGroup: ENVIRONMENT.staging,
  selectCaseId: null,
  group: undefined,
  addr: '',
  isRequestEnd: 0,
  timeout: 5000,
  retries: 1,
  response: '{}',
  requestEnvHeader: [],
  isGenParam: false,
  attachments: [],
  dubboTag: undefined,
  hasAttachment: false,
  dubboNamespace: 'default',
  dubboEnv: undefined,
  isClickCase: false,
  X5Param: {
    useX5Filter: false,
    appID: undefined,
    appkey: undefined,
    x5Method: undefined
  },
  grpcParams: {
    packageName: "",
    interfaceName: "",
    methodName: "",
    parameter: "",
    timeout: 1000,
    addrs: ""
  },
  defaultHeader: Object.keys(DEFAULT_HEADER).map(key => {
    return {
      paramKey: key,
      paramValue: DEFAULT_HEADER[key],
      default: true
    }
  })
}

const mutations = {
  CHANGE_TARGET: (state, obj) => {
    Object.keys(obj).forEach(key => {
      state[key] = obj[key]
    })
  },
  CHANGE_GRPC_PARAM: (state, obj) => {
    Object.keys(obj).forEach(key => {
      state.grpcParams[key] = obj[key]
    })
  },
  RESET_DEFAULT_HEADER: (state) => {
    state.defaultHeader = Object.keys(DEFAULT_HEADER).map(key => {
      return {
        paramKey: key,
        paramValue: DEFAULT_HEADER[key],
        default: true
      }
    })
  },
  RESET_X5_PARAM: (state, obj) => {
    Object.keys(obj).forEach(key => {
      state.X5Param[key] = obj[key]
    })
  },
  RESET_API_TEST: (state) => {
    state.httpEnv = undefined
    state.selectCaseId = null
    state.apiTestProtocol = PROTOCOL_TYPE.HTTP
    state.apiTestBodyType = RADIO_TYPE.FORM
    state.requestType = ''
    state.url = ''
    state.headers = []
    state.body = []
    state.jsonBody = {}
    state.query = []
    state.version = ''
    state.ip = ''
    state.paramType = '' // 参数类型
    state.parameter = [] // 参数列表
    state.interfaceName = ''
    state.methodName = ''
    state.serviceNameGroup = ENVIRONMENT.staging
    state.group = undefined
    state.addr = ''
    state.retries = 1
    state.response = '{}'
    state.timeout = 5000
    state.isGenParam = false
    state.isRequestEnd = new Date().getTime()
    state.requestEnvHeader = []
    state.dubboNamespace = 'default'
    state.attachments = []
    state.hasAttachment = false
    state.dubboTag = undefined
    state.dubboEnv = undefined
    state.defaultHeader = Object.keys(DEFAULT_HEADER).map(key => {
      return {
        paramKey: key,
        paramValue: DEFAULT_HEADER[key],
        default: true
      }
    })
    state.grpcParams = {
      packageName: "",
      interfaceName: "",
      methodName: "",
      parameter: "",
      timeout: 1000,
      addrs: ""
    }
    state.X5Param = {
      useX5Filter: false,
      appID: undefined,
      appkey: undefined
    }
  }
}

const actions = {
  changeApiTestTarget ({ commit }, obj) {
    commit('CHANGE_TARGET', obj)
  },
  changeGrpcParam ({ commit }, obj) {
    commit('CHANGE_GRPC_PARAM', obj)
  },
  changeX5Param ({ commit }, obj) {
    commit('RESET_X5_PARAM', obj)
  },
  resetDefaultHeader ({ commit }) {
    commit('RESET_DEFAULT_HEADER')
  },
  getHostEnvList ({ commit }, projectID) {
    getApiEnvListByProjectId({ projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        commit('CHANGE_TARGET', { hostEnvList: data.data || [] })
      }
    }).catch(e => {})
  },
  resetApiTest ({ commit }) {
    commit('RESET_API_TEST')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
