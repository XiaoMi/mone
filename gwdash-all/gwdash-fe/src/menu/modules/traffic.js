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

const traffic = ((pre) => [
  {
    path: `${pre}record/config`,
    title: "流量录制配置"
  },
  {
    path: `${pre}record/result`,
    title: "流量录制结果"
  }
  // {
  //   path: `${pre}playback/config`,
  //   title: "流量回放配置"
  // },
  // {
  //   path: `${pre}playback/result`,
  //   title: "流量回放结果"
  // }
])('/traffic/')

const chainTest = {
  path: '/quality/chaintest',
  title: '全链路压测'
}

export default {
  title: "流量管理",
  icon: "newspaper-o",
  bgImage: 'application',
  children: [ ...traffic, chainTest ]
}
