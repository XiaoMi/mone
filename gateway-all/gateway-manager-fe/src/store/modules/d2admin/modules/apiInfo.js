/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

// 设置文件
import setting from '@/setting.js'

export default {
  namespaced: true,
  state: {
    prefixedPath: setting.api.info.prefixedPath,
    tenementType: '' // 租户选择信息
  },
  getters: {
    prefixedPath: state => state.prefixedPath
  },
  mutations: {
    // 更改租户选择信息
    setTenementType (state, data) {
      state.tenementType = data
    }
  },
  actions: {
    setPrefixedPath ({ state, dispatch }, prefixedPath) {
      return new Promise(async resolve => {
        state.prefixedPath = prefixedPath
        await dispatch('d2admin/db/set', {
          dbName: 'sys',
          path: 'api.info.prefix.path',
          value: prefixedPath,
          user: true
        }, { root: true })
        resolve()
      })
    },
    loadPrefixedPath ({ state, dispatch }) {
      return new Promise(async resolve => {
        state.info = await dispatch('d2admin/db/get', {
          dbName: 'sys',
          path: 'api.info.prefix.path',
          defaultValue: setting.api.info.prefixedPath,
          user: true
        }, { root: true })
        resolve()
      })
    }
  }
}
