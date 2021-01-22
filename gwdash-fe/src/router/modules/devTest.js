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

import layoutHeaderAside from '@/layout/header-aside'
export default {
  path: '/devTest/',
  name: 'devTest',
  meta: { title: 'dubbo测试', icon: 'newspaper-o'},
  component: layoutHeaderAside,
  children: [
    {
      path: 'index',
      name: 'devTestIndex',
      meta: { title: 'dubboTest', icon: 'upload' },
      component: () => import('@/pages/devTest/index')
    },
    {
      path: 'http',
      name: 'httpTest',
      meta: { title: 'httpTest', icon: 'upload' },
      component: () => import('@/pages/devTest/http')
    },
    {
      path: 'testcase',
      name: 'testCase',
      meta: { title: 'testCase', icon: 'upload' },
      component: () => import('@/pages/quality/test-case/index')
    }
  ]
}