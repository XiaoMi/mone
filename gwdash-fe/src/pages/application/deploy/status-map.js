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
    name: '开始',
    type: 'info'
  },
  1: {
    name: '拉取代码',
    type: 'info'
  },
  2: {
    name: '构建',
    type: 'info'
  },
  3: {
    name: '获取',
    type: 'info'
  },
  4: {
    name: '构建完成',
    type: 'info'
  }
}
export const deployStep={
  0: {
    name: '开始',
    type: 'info'
  },
  1: {
    name: '下载',
    type: 'info'
  },
  2: {
    name: '存储',
    type: 'info'
  },
  3: {
    name: '启动服务',
    type: 'info'
  },
  4: {
    name: '上线完成',
    type: 'info'
  }
}

export const status = {
  0: {
    name: '构建中',
    type: 'finish'
  },
  1: {
    name: '构建成功',
    type: 'success'
  },
  2: {
    name: '构建失败',
    type: 'error'
  }
}


export default {
  taskStep,
  deployStep,
  status
}