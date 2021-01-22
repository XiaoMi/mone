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

// check,
// clone,
// build,
// findJar,
// upload,
export const taskStep = {
  0: {
    name: 'check',
    type: 'info'
  },
  1: {
    name: 'clone',
    type: 'info'
  },
  2: {
    name: 'build',
    type: 'info'
  },
  3: {
    name: '获取jar',
    type: 'info'
  },
  4: {
    name: '构建完成',
    type: 'info'
  }
}

export const status = {
  0: {
    name: '构建中',
    type: 'info'
  },
  1: {
    name: '构建成功',
    type: 'success'
  },
  2: {
    name: '构建失败',
    type: 'danger'
  }
}


export default {
  taskStep,
  status
}