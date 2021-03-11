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

import { httpGet } from '@/api/sys/http'
import semver from 'semver'
import util from '@/libs/util.js'
import setting from '@/setting.js'

export default {
  namespaced: true,
  state: {
    // D2Admin 版本
    version: setting.releases.version,
    // 最新版本的信息
    latest: {},
    // 有新版本
    update: false
  },
  actions: {
    /**
     * @description 检查版本更新
     * @param {Object} param context
     */
    checkUpdate ({ state, commit }) {
      httpGet(setting.releases.api)
        .then(res => {
          let versionGet = res.tag_name
          const update = semver.lt(state.version, versionGet)
          if (update) {
            util.log.capsule('D2Admin', `New version ${res.name}`)
            console.log(`版本号: ${res.tag_name} | 详情 ${res.html_url}`)
            commit('updateSet', true)
          }
          commit('latestSet', res)
        })
        .catch(err => {
          console.log('checkUpdate error', err)
        })
    }
  },
  mutations: {
    /**
     * @description 显示版本信息
     * @param {Object} state vuex state
     */
    versionShow (state) {
    },
    /**
     * @description 设置是否有新的 D2Admin 版本
     * @param {Object} state vuex state
     * @param {Boolean} update can update
     */
    updateSet (state, update) {
      // store 赋值
      state.update = update
    },
    /**
     * @description 设置最新版本的信息
     * @param {Object} state vuex state
     * @param {Object}} latest releases value
     */
    latestSet (state, latest) {
      // store 赋值
      state.latest = latest
    }
  }
}
