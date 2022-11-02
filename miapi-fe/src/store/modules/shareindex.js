const state = {
  pageInfoList: [],
  initPageInfoList: {},
  shareApiDetail: {},
  showIndexDoc: {
    content: undefined,
    title: ''
  }
}

const mutations = {
  CHANGE_PAGE_INFO_LIST: (state, list) => {
    state.pageInfoList = list || []
  },
  CHANGE_INDEX_DOC: (state, obj) => {
    state.showIndexDoc = obj
  },
  CHANGE_SHARE_DETAIL: (state, obj) => {
    state.shareApiDetail = obj || {}
  },
  CHANGE_INIT_PAGE_INFO_LIST: (state, obj) => {
    state.initPageInfoList = obj || {}
  }
}

const actions = {
  changePageInfoList ({ commit }, list) {
    commit('CHANGE_PAGE_INFO_LIST', list)
  },
  changeInitPageInfoList ({ commit }, list) {
    commit('CHANGE_INIT_PAGE_INFO_LIST', list)
  },
  changeIndexDoc ({ commit }, obj) {
    commit('CHANGE_INDEX_DOC', obj)
  },
  changeShareDetail ({ commit }, obj) {
    commit('CHANGE_SHARE_DETAIL', obj)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
