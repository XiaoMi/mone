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

import screenfull from 'screenfull'

export default {
  namespaced: true,
  state: {
    // 全屏激活
    active: false
  },
  actions: {
    /**
     * @description 初始化监听
     */
    listen ({ commit }) {
      return new Promise(resolve => {
        if (screenfull.enabled) {
          screenfull.on('change', () => {
            if (!screenfull.isFullscreen) {
              commit('set', false)
            }
          })
        }
        // end
        resolve()
      })
    },
    /**
     * @description 切换全屏
     */
    toggle ({ commit }) {
      return new Promise(resolve => {
        if (screenfull.isFullscreen) {
          screenfull.exit()
          commit('set', false)
        } else {
          screenfull.request()
          commit('set', true)
        }
        // end
        resolve()
      })
    }
  },
  mutations: {
    /**
     * @description 设置 store 里的全屏状态
     * @param {Object} state vuex state
     * @param {Boolean} active active
     */
    set (state, active) {
      state.active = active
    }
  }
}
