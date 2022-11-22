import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { getMyProjects, getAllProjectGroups, getProjectListByProjectGroupId } from '@/api/main'
import i18n from '../../lang'

const state = {
  projectList: [],
  focusProjectList: [],
  projectGroups: [],
  showFixedGroup: false,
  selectedProjectGroup: {
    groupDesc: i18n.t('myProject'),
    groupId: -1,
    groupName: i18n.t('myProject')
  },
  currentKey: undefined
}

const mutations = {
  CHANGE_LIST: (state, list) => {
    state.projectList = list || []
  },
  CHANGE_FOCUS_LIST: (state, list) => {
    state.focusProjectList = list || []
  },
  CHANGE_GROUP: (state, list) => {
    state.projectGroups = list || []
  },
  CHANGE_CURRENT_KEY: (state, currentKey) => {
    state.currentKey = currentKey
  },
  CHANGE_PROJECT_GROUP_INDEX: (state, obj) => {
    state.selectedProjectGroup = obj
  },
  CHANGE_FIXED_GROUP: (state, bool) => {
    state.showFixedGroup = !!bool
  }
}

const actions = {
  changeList ({ commit }, list) {
    commit('CHANGE_LIST', list)
  },
  changeFixedGroup ({ commit }, bool) {
    commit('CHANGE_FIXED_GROUP', bool)
  },
  changeProjectGroupIndex ({ commit }, obj) {
    commit('CHANGE_PROJECT_GROUP_INDEX', obj)
  },
  getProjectGroup ({ commit, state }) {
    return new Promise((resolve) => {
      getAllProjectGroups().then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('CHANGE_GROUP', data.data || [])
          resolve(data.data || [])
          return data.data || []
        }
      }).then((data) => {
        // if (!state.projectList.length && data.length) {
        // 	getProjectListByProjectGroupId({projectGroupID: data[0].groupId}).then(data=>{
        // 		if (data.message === AJAX_SUCCESS_MESSAGE) {
        // 			commit('CHANGE_LIST', data.data)
        // 		}
        // 	}).catch(e=>{})
        // }
      }).catch(e => {})
    })
  },
  getList ({ commit, state }, index) {
    let curProjectGroup = {
      groupDesc: i18n.t('myProject'),
      groupId: -1,
      groupName: i18n.t('myProject')
    }
    if (!index && (index !== 0)) {
      index = state.currentKey
    }
    commit('CHANGE_CURRENT_KEY', index)
    if (index === '999') {
      window.localStorage.setItem('groupId', '999')
      getMyProjects().then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('CHANGE_LIST', data.data.myAdmin || [])
          let projectList = (data.data.myFocus || []).map(v => {
            v.isFocus = true
            return v
          })
          commit('CHANGE_FOCUS_LIST', projectList)
        }
      }).catch(e => {})
    } else {
      curProjectGroup = state.projectGroups[Number(index)]
      window.localStorage.setItem('groupId', curProjectGroup.groupId)
      getProjectListByProjectGroupId({ projectGroupID: curProjectGroup.groupId }).then(data => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          commit('CHANGE_LIST', data.data)
        }
      }).catch(e => {})
    }
    commit('CHANGE_PROJECT_GROUP_INDEX', curProjectGroup)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
