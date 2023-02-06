import { getMyInfoDetail } from '@/common/service/list/common-req';

export default {
  namespaced: true,
  state: {
    userInfo: {
      account: '',
      topMgr: false,
    },
  },
  mutations: {
    updateUserInfo(state, data) {
      state.userInfo = {
        ...state.userInfo,
        ...data,
      };
    },
  },
  actions: {
    GETUSERINFO: (context) => {
      getMyInfoDetail({}).then(({ data }) => {
        context.commit('updateUserInfo', data);
      });
    },
  },
};
