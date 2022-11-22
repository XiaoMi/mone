import { constantRoutes, routes } from '@/router'

const state = {
  routes: [],
  addRoutes: [],
  language: ''
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = routes
  },
  SET_LANGUAGE: (state, language) => {
    state.language = language
  }
}

const actions = {
  generateRoutes ({ commit }) {
    return new Promise(resolve => {
      commit('SET_ROUTES', routes)
      resolve(routes)
    })
  },
  changeLanguage ({ commit }, language) {
    commit('SET_LANGUAGE', language)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
