import { getRecyclingStationApiList, getApiList, getGrpcApiDetail, getGroup, getDubboApiDetail, getHttpApi, getGatewayApiDetail, removeStar, editApiStatus } from '@/api/apilist'

import { PROTOCOL_TYPE, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { API_DETAIL_TAB, GROUP_TYPE } from '@/views/ApiList/constant'
import { sortGroupList } from '../utils'

const state = {
  groupList: [],
  groupID: -1,
  groupDesc: '',
  apiDetail: {},
  apiList: [],
  apiCount: 0,
  isEditDetail: false,
  apiDetailActiveTab: API_DETAIL_TAB.DETAIL,
  detailGroupList: [],
  customMockUrl: ""
}

const mutations = {
  CHANGE_CUSTOM_MOCK_URL: (state, url) => {
    state.customMockUrl = url || ""
  },
  GROUP_LIST: (state, list) => {
    state.groupList = list || []
  },
  GROUP_GROUP_DESC: (state, desc) => {
    state.groupDesc = desc || ''
  },
  DETAIL_GROUP_LIST: (state, list) => {
    state.detailGroupList = list || []
  },
  API_DETAIL: (state, obj) => {
    state.apiDetail = obj || {
      baseInfo: {},
      headerInfo: [],
      mockInfo: {},
      requestInfo: [],
      testHistory: [],
      resultInfo: []
    }
  },
  GROUP_ID: (state, id) => {
    state.groupID = id
  },
  CHANGE_API_DETAIL_ACTIVE_TAB: (state, type) => {
    state.apiDetailActiveTab = type
  },
  CHANGE_API_COUNT: (state, count) => {
    state.apiCount = count
  },
  CHANGE_API_DETAIL: (state, obj = {}) => {
    state.apiDetail = { ...obj }
  },
  EDIT_DETAIL: (state, bool) => {
    state.isEditDetail = bool
  },
  CHANGE_API_LIST: (state, list) => {
    state.apiList = list || []
  }
}

const actions = {
  changeGroupId ({ commit }, id) {
    commit('GROUP_ID', id)
  },
  changeCustomMockUrl ({ commit }, url) {
    commit('CHANGE_CUSTOM_MOCK_URL', url)
  },
  changeGroupList ({ commit }, list) {
    commit('GROUP_LIST', list)
  },
  changeGroupDesc ({ commit }, desc) {
    commit('GROUP_GROUP_DESC', desc)
  },
  changeApiList ({ commit }, list) {
    commit('CHANGE_API_LIST', list)
  },
  changeEditDetail ({ commit }, bool) {
    commit('EDIT_DETAIL', bool)
  },
  changeApiDetail ({ commit }, obj) {
    commit('CHANGE_API_DETAIL', obj)
  },
  changeApiDetailActiveTab ({ commit }, type) {
    commit('CHANGE_API_DETAIL_ACTIVE_TAB', type)
  },
  groupList ({ commit, state, rootGetters }, projectID) {
    return new Promise((resolve) => {
      getGroup({ projectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let arr = sortGroupList(data.data.groupList || [])
          if (arr && arr.length && (state.groupID <= 0) && (rootGetters.groupType === GROUP_TYPE.API)) {
            commit('GROUP_ID', arr[0].groupID)
            commit('GROUP_GROUP_DESC', arr[0].groupDesc)
          }
          commit('GROUP_LIST', arr)
          resolve()
        }
      }).catch(e => {})
    })
  },
  apiList ({ commit, state }, { projectID, pageNo, pageSize }) {
    let param = {
      orderBy: 3,
      projectID,
      asc: 1,
      pageNo,
      pageSize
    }
    if (state.groupID < 0) {
      if (state.apiList.length) {
        commit("CHANGE_API_COUNT", 0)
        commit('CHANGE_API_LIST', [])
      }
    } else if (state.groupID === 0) { // 回收站
      getRecyclingStationApiList(param).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          if (data.data) {
            commit("CHANGE_API_COUNT", data.data.apiCount || 0)
            commit('CHANGE_API_LIST', data.data.apiList || [])
          }
        }
      }).catch(e => {})
    } else if (state.groupID) {
      param.groupID = state.groupID
      getApiList(param).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          if (data.data) {
            commit("CHANGE_API_COUNT", data.data.apiCount || 0)
            commit('CHANGE_API_LIST', data.data.apiList || [])
          }
        }
      }).catch(e => {})
    }
  },
  apiDetail ({ commit, state }, { apiID, apiProtocol, projectID, indexProjectID }) {
    getGroup({ projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        let arr = sortGroupList(data.data.groupList || [])
        commit('DETAIL_GROUP_LIST', arr)
      }
    }).catch(e => {})
    if (apiProtocol === PROTOCOL_TYPE.Gateway) {
      getGatewayApiDetail({ projectID: indexProjectID || projectID, apiID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('API_DETAIL', data.data)
        }
      }).catch(e => {})
    } else if (apiProtocol === PROTOCOL_TYPE.Dubbo) {
      getDubboApiDetail({ projectID: indexProjectID || projectID, apiID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('API_DETAIL', data.data)
        }
      }).catch(e => {})
    } else if (apiProtocol === PROTOCOL_TYPE.HTTP) {
      getHttpApi({ projectID: indexProjectID || projectID, apiID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('API_DETAIL', data.data)
        }
      }).catch(e => {})
    } else if (apiProtocol === PROTOCOL_TYPE.Grpc) {
      getGrpcApiDetail({ projectID: indexProjectID || projectID, apiID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('API_DETAIL', {
            ...data.data,
            requestInfo: data.data.requestInfo ? [data.data.requestInfo] : [],
            resultInfo: data.data.resultInfo ? [data.data.resultInfo] : []
          })
        }
      }).catch(e => {})
    }
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
