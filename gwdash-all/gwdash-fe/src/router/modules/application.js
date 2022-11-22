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
import store from '@/store/index'

export default {
  path: '/application/',
  name: 'application',
  meta: { title: '应用管理', icon: 'product-hunt' },
  redirect: { name: '' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'list',
      name: 'projectList',
      meta: {
        title: '项目列表',
        icon: 'folder-open',
        cache: true // 开启页面缓存
      },
      component: () => import('@/pages/application/list')
    },
    {
      path: 'show',
      name: 'projectList',
      meta: {
        title: '看板',
        icon: 'folder-open',
        cache: true // 开启页面缓存
      },
      component: () => import('@/pages/application/displayBoard')
    },
    {
      path: 'list-old',
      name: 'projectListOld',
      meta: {
        title: '项目列表',
        icon: 'folder-open',
        cache: true // 开启页面缓存
      },
      component: () => import('@/pages/application/list/index-old.vue')
    },
    {
      path: 'audit/list',
      name: 'projectAuditList',
      meta: { title: '我的审核', icon: 'legal' },
      component: () => import('@/pages/application/audit-list')
    },
    {
      path: 'build/list',
      name: 'projectBuildList',
      meta: { title: '项目构建列表', hidden: true },
      component: () => import('@/pages/application/build-list')
    },
    {
      path: 'deploy/list',
      name: 'projectDeployList',
      meta: { title: '项目部署记录', hidden: true },
      component: () => import('@/pages/application/deploy-list')
    },
    {
      path: 'env_setting/:projectId/:envId/:deployType',
      name: 'projectEnvSetting',
      meta: { title: '环境', hidden: true },
      component: () => import('@/pages/application/env-setting')
    },
    {
      path: 'rollback',
      name: 'projectRollback',
      meta: { title: '回滚项目', hidden: true, rollback: true },
      component: () => import('@/pages/application/deploy')
    },
    {
      path: 'deploy',
      name: 'projectDeploy',
      meta: { title: '项目构建', hidden: true, rollback: false },
      component: () => import('@/pages/application/deploy')
    },
    {
      path: 'log/view/:projectId/:envId',
      name: 'logView',
      meta: {
        title: '日志查看',
        icon: 'folder-open'
      },
      component: () => import('@/pages/application/log-view')
    }
  ]
}
