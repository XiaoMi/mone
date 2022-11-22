import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { getProjectListByProjectGroupId } from '@/api/main'
import { getGroupApiViewList, getGroup } from '@/api/apilist'
import { sortGroupList } from '../utils'

const state = {
  importGroupList: [],
  imoprtInitSubMenu: {},
  curSubMenu: {},
  selectApiIds: []
}

const mutations = {
  CHANGE_GROUP_LIST: (state, list) => {
    state.importGroupList = list || []
  },
  CHANGE_INIT_SUBMENU: (state, obj) => {
    state.initSubMenu = obj || {}
  },
  CHANGE_CUR_SUBMENU: (state, obj) => {
    state.curSubMenu = obj || {}
  },
  CHANGE_SELECT_API_IDS: (state, list) => {
    state.selectApiIds = list || []
  }
}

const actions = {
  changeGroupList ({ commit }, list) {
    commit('CHANGE_GROUP_LIST', list)
  },
  changeInitSubMenu ({ commit }, obj) {
    commit('CHANGE_INIT_SUBMENU', obj)
  },
  changeCurSubMenu ({ commit }, obj) {
    commit('CHANGE_CUR_SUBMENU', obj)
  },
  changeSelectApiIds ({ commit }, list) {
    commit('CHANGE_SELECT_API_IDS', list)
  },
  getGroupAndSubMenu ({ commit }, projectID) {
    getGroup({ projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        let arr = sortGroupList(data.data.groupList || [])
        commit('CHANGE_GROUP_LIST', arr)
      }
    }).catch(e => {})

    getGroupApiViewList({ projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        commit('CHANGE_INIT_SUBMENU', data.data)
        commit('CHANGE_CUR_SUBMENU', data.data)
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
