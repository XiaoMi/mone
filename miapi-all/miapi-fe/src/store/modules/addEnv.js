
const state = {
  envData: {
    envName: '',
    httpDomain: '',
    id: '',
    headers: [],
    isAdd: false
  }
}

const mutations = {
  CHANGE_ENV_DATA: (state, obj) => {
    Object.keys(obj).forEach(key => {
      state.envData[key] = obj[key]
    })
  },
  RESET_ENV_DATA: (state, obj) => {
    state.envData = {
      envName: '',
      httpDomain: '',
      id: '',
      headers: [],
      isAdd: false
    }
  }
}

const actions = {
  changeAddEnvData ({ commit }, obj) {
    commit('CHANGE_ENV_DATA', obj)
  },
  resetAddEnvData ({ commit }, obj) {
    commit('RESET_ENV_DATA')
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
