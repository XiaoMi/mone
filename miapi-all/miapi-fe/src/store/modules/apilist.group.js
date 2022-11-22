import Cookies from 'js-cookie'
import { GROUP_TYPE } from '@/views/ApiList/constant'
import { searchApi, getGroupApiViewList } from '@/api/apilist'
import { getAllIndexGroupApiViewList } from '@/api/apiindex'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'

const state = {
  groupType: GROUP_TYPE.API,
  groupComp: {
    searchSort: false,
    searchWord: '',
    curPath: '',
    uniqueOpened: true,
    defaultOpeneds: [],
    subMenu: {},
    initSubMenu: {},
    indexSubMenu: {},
    indexInitSubMenu: {}
  }
}

const mutations = {
  GROUP_COMP: (state, obj) => {
    Object.keys(obj).forEach(v => {
      state.groupComp[v] = obj[v]
    })
  },
  GROUP_TYPE: (state, type) => {
    state.groupType = type
  },
  RESET_GROUP_COMP: (state) => {
    state.groupComp = {
      searchSort: false,
      searchWord: '',
      curPath: '',
      uniqueOpened: true,
      defaultOpeneds: [],
      subMenu: {},
      initSubMenu: {}
    }
  }
}

const actions = {
  changeGroupComp ({ commit }, obj) {
    commit('GROUP_COMP', obj)
  },
  changeGroupType ({ commit }, type) {
    commit('GROUP_TYPE', type)
  },
  resetGroupComp ({ commit }) {
    commit('RESET_GROUP_COMP')
  },
  getGroupViewList ({ commit }, projectID) {
    getGroupApiViewList({ projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        commit('GROUP_COMP', { init: true, subMenu: data.data || {}, initSubMenu: data.data || {} })
      }
    }).catch(e => {})
  },
  getAllIndexGroupViewList ({ commit }, projectID) {
    getAllIndexGroupApiViewList({ projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        commit('GROUP_COMP', { init: true, indexSubMenu: data.data || {}, indexInitSubMenu: data.data || {} })
      }
    }).catch(e => {})
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
