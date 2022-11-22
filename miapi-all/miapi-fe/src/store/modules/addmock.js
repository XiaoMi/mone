import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { RADIO_TYPE } from '@/views/ApiList/constant'

const state = {
  mockData: {
    mockRule: '',
    mockJsonData: '',
    mockRequestParamType: RADIO_TYPE.FORM,
    mockRequestRaw: '',
    paramsJson: '',
    mockExpName: '',
    mockDataType: RADIO_TYPE.FORM,
    requestTime: 0,
    isDefault: false,
    mockScript: "",
    enableMockScript: false
  }
}

const mutations = {
  CHANGE_MOCK_DATA: (state, obj) => {
    Object.keys(obj).forEach(key => {
      state.mockData[key] = obj[key]
    })
  },
  RESET_MOCK_DATA: (state) => {
    state.mockData = {
      mockRule: '',
      mockJsonData: '',
      paramsJson: '',
      mockExpName: '',
      mockRequestParamType: RADIO_TYPE.FORM,
      mockRequestRaw: '',
      mockDataType: RADIO_TYPE.FORM,
      requestTime: 0,
      isDefault: false,
      mockScript: "",
      enableMockScript: false
    }
  }
}

const actions = {
  changeAddMockData ({ commit }, obj) {
    commit('CHANGE_MOCK_DATA', obj)
  },
  resetMockData ({ commit }) {
    commit('RESET_MOCK_DATA')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
