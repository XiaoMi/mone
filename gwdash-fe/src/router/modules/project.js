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

export default {
  path: '/projectAudit/',
  name: 'projectAudit',
  meta: { title: '项目审核', icon: 'newspaper-o'},
  component: layoutHeaderAside,
  children: [
    {
      path: 'auditList',
      name: 'auditList',
      meta: { title: '审核列表', icon: 'upload' },
      component: () => import('@/pages/projectAudit/auditList')
    },
    {
      path: 'operationLog',
      name: 'operationLog',
      meta: { title: '操作日志', icon: 'upload' },
      component: () => import('@/pages/projectAudit/operationLog')
    },
  ]
}