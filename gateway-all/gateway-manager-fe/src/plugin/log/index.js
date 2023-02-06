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

import store from '@/store'
import util from '@/libs/util'

export default {
  install (Vue, options) {
    // 快速打印 log
    Vue.prototype.$log = util.log
    // 快速记录日志
    Vue.prototype.$logAdd = function (info, show = true) {
      // store 赋值
      store.dispatch('d2admin/log/add', {
        type: 'log',
        info
      })
      // 显示在控制台
      if (show && process.env.NODE_ENV === 'development') {
        util.log.default(info)
      }
    }
  }
}
