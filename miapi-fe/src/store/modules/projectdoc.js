import { getGroupList } from '@/api/projectdoc'

const state = {
  docList: []
}

const mutations = {
  DOC_LIST: (state, list) => {
    state.docList = list || []
  }
}

const actions = {
  changeDocList ({ commit }, list) {
    commit('DOC_LIST', list)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
