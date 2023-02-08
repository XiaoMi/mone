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

export default {
  namespaced: true,
  state: {
    // 尺寸
    value: '' // medium small mini
  },
  actions: {
    /**
     * @description 设置尺寸
     * @param {Object} state vuex state
     * @param {String} size 尺寸
     */
    set ({ state, dispatch }, size) {
      return new Promise(async resolve => {
        // store 赋值
        state.value = size
        // 持久化
        await dispatch('d2admin/db/set', {
          dbName: 'sys',
          path: 'size.value',
          value: state.value,
          user: true
        }, { root: true })
        // end
        resolve()
      })
    },
    /**
     * @description 从持久化数据读取尺寸设置
     * @param {Object} state vuex state
     */
    load ({ state, dispatch }) {
      return new Promise(async resolve => {
        // store 赋值
        state.value = await dispatch('d2admin/db/get', {
          dbName: 'sys',
          path: 'size.value',
          defaultValue: 'default',
          user: true
        }, { root: true })
        // end
        resolve()
      })
    }
  }
}
