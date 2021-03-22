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

export const options = [{
  label: '待处理',
  value: 1
}, {
  label: '处理中',
  value: 2
}, {
  label: '关闭',
  value: 3
}, {
  label: '重新打开',
  value: 4
}]

export const statusMap = {
  1: {
    type: 'info',
    text: '待处理'
  },
  2: {
    type: 'warning',
    text: '处理中'
  },
  3: {
    type: 'success',
    text: '关闭'
  },
  4: {
    type: 'danger',
    text: '重新打开'
  }
}

export default {
  options

}
