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
  path: '/quality/',
  name: 'quality',
  meta: { title: '测试管理', icon: 'newspaper-o' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'testcase',
      name: 'testcase',
      meta: { title: 'testcase', icon: 'thermometer-quarter' },
      component: () => import('@/pages/quality/test-case')
    },
    {
      path: 'test/release/management',
      name: 'testmember',
      meta: { title: 'testmember', icon: 'thermometer-quarter' },
      component: () => import('@/pages/quality/test-member')
    },
    {
      path: 'chaintest',
      name: 'chaintest',
      meta: { title: 'chaintest', icon: 'thermometer-quarter' },
      component: () => import('@/pages/quality/chain-test')
    }
  ]
}
