import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { resetRouter } from '@/router'

const state = {
  roles: false,
}

const mutations = {
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      commit('SET_ROLES', true)
      resolve()
    })
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
