/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import service from '@/plugin/axios'

export default {
  namespaced: true,
  state: {
    userInfo: {},
    serverEnv: '',
    avatarImg: '',
    isSuperRole: false,
    isAdmin: false,
    isOnline: false,
    isSuperuser: false
  },
  mutations: {
    setUserInfo (state, param) {
      const { user: userInfo, serverEnv } = param
      state.userInfo = userInfo
      state.serverEnv = serverEnv
      state.avatarImg = ''
      state.isSuperRole = (((userInfo && userInfo.roles) || []).findIndex(it => it.name === 'SuperRole') !== -1)
      state.isSuperuser = (userInfo.roles || []).find(item => item.name === 'project-superuser')
      state.isAdmin = (userInfo && userInfo.role === 1)
      state.isOnline = (serverEnv === 'c3' || serverEnv === 'c4' || serverEnv === 'intranet')
    }
  },
  actions: {
    getUserInfo ({ commit }) {
      return service({
        url: '/server/info'
      }).then(res => {
        commit('setUserInfo', res)
      })
    }
  }
}
