import { getGroup, getAllApiList, getApi } from '@/api/apilist'

const state = {
	selfUserInfo: {},
}
  
const mutations = {
	CHANGE_SELF_USER: (state, obj) => {
		state.selfUserInfo = obj
	},
}

const actions = {
	changeSelfUser({ commit }, obj) {
		commit('CHANGE_SELF_USER', obj)
	},
}

export default {
	namespaced: true,
	state,
	mutations,
	actions
}
  