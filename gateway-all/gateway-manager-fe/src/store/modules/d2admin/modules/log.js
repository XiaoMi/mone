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

import dayjs from 'dayjs'
import { get, toString } from 'lodash'
import util from '@/libs/util.js'

export default {
  namespaced: true,
  state: {
    // 错误日志
    list: []
  },
  getters: {
    /**
     * @description 返回现存 log (all) 的条数
     * @param {*} state vuex state
     */
    length (state) {
      return state.list.length
    },
    /**
     * @description 返回现存 log (error) 的条数
     * @param {*} state vuex state
     */
    lengthError (state) {
      return state.list.filter(l => l.type === 'error').length
    }
  },
  actions: {
    /**
     * @description 添加一个日志
     * @param {Object} param type {String} 类型
     * @param {Object} param err {Error} 错误对象
     * @param {Object} param vm {Object} vue 实例
     * @param {Object} param info {String} 信息
     */
    add ({ state, rootState }, { type, err, vm, info }) {
      // store 赋值
      state.list.push(Object.assign({
        // 记录类型
        type: 'log', // or error
        // 信息
        info: '',
        // 错误对象
        err: '',
        // vue 实例
        vm: '',
        // 当前用户信息
        user: rootState.d2admin.user.info,
        // 当前用户的 uuid
        uuid: util.cookies.get('uuid'),
        // 当前的 token
        token: util.cookies.get('token'),
        // 当前地址
        url: get(window, 'location.href', ''),
        // 当前时间
        time: dayjs().format('YYYY-M-D HH:mm:ss')
      }, {
        type,
        err,
        vm,
        info: toString(info)
      }))
    }
  },
  mutations: {
    /**
     * @description 清空日志
     * @param {Object} state vuex state
     */
    clean (state) {
      // store 赋值
      state.list = []
    }
  }
}
