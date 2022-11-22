import { getIndexList, getApiListByIndex } from '@/api/apiindex'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { GROUP_TYPE } from '@/views/ApiList/constant'

const state = {
  indexGroupList: [],
  indexGroupID: '',
  indexApiList: [],
  showShareDialog: false
}

const mutations = {
  CHANGE_GROUP_LIST: (state, list) => {
    state.indexGroupList = list || []
  },
  CHANGE_SHOW_SHARE_DIALOG: (state, bool) => {
    state.showShareDialog = bool
  },
  CHANGE_GROUP_INDEX_ID: (state, id) => {
    state.indexGroupID = id || undefined
  },
  CHANGE_INDEX_API_LIST: (state, list) => {
    state.indexApiList = list || []
  }
}

const actions = {
  changeIndexApiList ({ commit }, list) {
    commit('CHANGE_INDEX_API_LIST', list)
  },
  changeIndexGroupList ({ commit }, list) {
    commit('CHANGE_GROUP_LIST', list)
  },
  changeGroupIndexId ({ commit }, id) {
    commit('CHANGE_GROUP_INDEX_ID', id)
  },
  changeShareDialogBool ({ commit }, bool) {
    commit('CHANGE_SHOW_SHARE_DIALOG', bool)
  },
  getApiList ({ commit, state }, { indexGroupID, projectID }) {
    let id = indexGroupID || state.indexGroupID
    getApiListByIndex({ indexID: id, projectID }).then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        commit("CHANGE_INDEX_API_LIST", data.data)
      }
    }).catch(e => {})
  },
  getIndexGroupList ({ commit, state, dispatch, rootGetters }, projectID) {
    return new Promise((resolve) => {
      getIndexList({ projectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let list = data.data.map(item => {
            return {
              groupID: item.indexId,
              groupName: item.indexName,
              groupDesc: item.description,
              indexDoc: item.indexDoc,
              isChild: 0,
              childGroupList: []
            }
          })
          if (!state.indexGroupID && list.length && (rootGetters.groupType === GROUP_TYPE.INDEX)) {
            commit('CHANGE_GROUP_INDEX_ID', list[0].groupID)
            dispatch('apilist/changeGroupDesc', list[0].groupDesc, { root: true })
          } else if (!list.length) {
            commit('CHANGE_GROUP_INDEX_ID')
            commit('CHANGE_INDEX_API_LIST', [])
          }
          commit('CHANGE_GROUP_LIST', list)
        }
      }).catch(e => {}).finally(() => {
        resolve()
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
