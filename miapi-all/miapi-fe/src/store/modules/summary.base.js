import { getProjectDetail } from '@/api/projectdetails'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'

const state = {
  projectDetail: {}
}

const mutations = {
  CHANGE_PROJECT_DETAIL: (state, obj) => {
    state.projectDetail = obj || {}
  }
}

const actions = {
  getDetail ({ commit }, projectID) {
    return new Promise((resolve) => {
      getProjectDetail({ projectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let res = data.data || {}
          commit('CHANGE_PROJECT_DETAIL', res)
          resolve(res)
        } else {
          let res = {
            apiCount: 0,
            busGroupID: 0,
            desc: "",
            isPublic: 1,
            logCount: 0,
            memberCount: 0,
            projectId: projectID,
            projectName: "",
            projectUpdateTime: ""
          }
          commit('CHANGE_PROJECT_DETAIL', res)
          resolve(res)
        }
      }).catch(e => {
        let res = {
          apiCount: 0,
          busGroupID: 0,
          desc: "",
          isPublic: 1,
          logCount: 0,
          memberCount: 0,
          projectId: projectID,
          projectName: "",
          projectUpdateTime: ""
        }
        commit('CHANGE_PROJECT_DETAIL', res)
        resolve(res)
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
