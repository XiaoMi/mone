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
  title: "配置中心",
  icon: "newspaper-o",
  bgImage: 'nacos',
  children: (pre => [
    {
      path: `${pre}instances`,
      title: "实例",
      icon: "rocket"
    },
    {
      path: `${pre}configs`,
      title: '配置',
      icon: 'file'
    }
  ])('/nacos/')
}
