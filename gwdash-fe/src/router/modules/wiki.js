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
import store from '@/store/index'

const isAdmin = !!(userInfo && userInfo.role == 1)
const isOnline = !!(serverEnv == 'c3' || serverEnv == 'c4' || serverEnv == 'intranet')

export default {
  path: '/wiki/',
  name: 'wiki',
  meta: { title: 'wiki', icon: 'wikipedia-w', hidden: isOnline},
  redirect: { name: 'feedback' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'doc/list',
      name: 'docList',
      meta: { title: '文档列表', icon: 'file-word-o', hidden: isOnline },
      component: () => import('@/pages/wiki/documents')
    },
    {
      path: 'doc/detail/:type/:projectId',
      name: 'docDocDetail',
      meta: { title: '文档详情', hidden: true },
      component: () => import('@/pages/wiki/doc-detail')
    },
    {
      path: 'issue/feedback',
      name: 'feedback',
      meta: { title: '问题反馈', icon: 'feed' },
      component: () => import('@/pages/wiki/feedback')
    },
    {
      path: 'issue/feedbacks',
      name: 'feedbackList',
      meta: { title: '反馈列表', icon: 'list' },
      component: () => import('@/pages/wiki/feedbacks')
    },
    {
      path: 'issue/:projectId/:issueId',
      name: 'docFbDetail',
      meta: { title: 'issue详情', hidden: true },
      component: () => import('@/pages/wiki/fb-detail')
    }
  ]
}