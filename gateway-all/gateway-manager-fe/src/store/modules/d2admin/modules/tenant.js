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
import service from '@/plugin/axios'
import { Message } from 'element-ui'

export default {
  namespaced: true,
  state: {
    tenantId: setting.tenant.id,
    tenantOptions: []
  },
  getters: {
    tenantId: state => state.tenantId,
    tenantOptions: state => state.tenantOptions
  },
  actions: {
    setTenantId ({ state, dispatch }, tenantId) {
      return new Promise(async resolve => {
        state.tenantId = tenantId
        await dispatch('d2admin/db/set', {
          dbName: 'sys',
          path: 'gw.tenant.id',
          value: tenantId,
          user: true
        }, { root: true })
        resolve()
      })
    },
    loadTenantId ({ state, dispatch }) {
      return new Promise(async (resolve, reject) => {
        // 优先从服务端获取当前用户网关租户id
        try {
          state.tenantId = await service({
            url: '/tenement/get',
            method: 'post',
            data: {}
          })
        } catch (e) {
          // 失败获取本地存储的网关组合id
          state.tenantId = await dispatch('d2admin/db/get', {
            dbName: 'sys',
            path: 'gw.tenant.id',
            defaultValue: setting.tenant.id,
            user: true
          }, { root: true })
          if (state.tenantId === '') {
            Message({
              type: 'error',
              message: '用户网关获取失败~'
            })
            reject(new Error('用户网关不存在'))
          }
        }
        resolve()
      })
    },
    loadTenantOptions ({ state }) {
      return new Promise(async (resolve, reject) => {
        try {
          state.tenantOptions = await service({
            url: '/tenement',
            method: 'post',
            data: {}
          })
        } catch (e) {
          Message({
            type: 'error',
            message: '可选网关信息获取失败'
          })
          state.tenantOptions = []
          reject(new Error('可选网关信息获取失败'))
        }
        resolve()
      })
    }
  }
}
