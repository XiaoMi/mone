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

const isAdmin = !!(userInfo && userInfo.role === 1)

export default {
  path: '/account/',
  name: 'account',
  meta: { title: '账号管理', icon: 'id-card-o' },
  redirect: { name: 'account-settings' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'settings',
      name: 'account-settings',
      meta: { title: '个人账号详情', icon: 'user-o' },
      component: () => import('@/pages/account/settings')
    },
    {
      path: 'gitlab',
      name: 'account-gitlab',
      meta: { title: 'gitlab access token', icon: 'gitlab' },
      component: () => import('@/pages/account/gitlab')
    }
  ]
}
