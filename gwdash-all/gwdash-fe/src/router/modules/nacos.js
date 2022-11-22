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

import layoutHeaderAside from '@/layout/header-aside'

export default {
  path: '/nacos/',
  name: 'nacos',
  meta: { title: '配置中心', icon: 'newspaper-o' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'instances',
      name: 'instances',
      meta: { title: 'nacos 实例', icon: 'upload' },
      component: () => import('@/pages/nacos/instances')
    },
    {
      path: 'configs',
      name: 'configs',
      meta: { title: 'nacos 配置', icon: 'upload' },
      component: () => import('@/pages/nacos/configs')
    }
  ]
}
