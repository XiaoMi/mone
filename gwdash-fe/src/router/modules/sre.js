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

export default {
  path: '/sre/',
  name: 'sre',
  meta: { title: '运维中心', icon: 'scribd'},
  component: layoutHeaderAside,
  children: [
    {
      path: 'machine/list',
      name: 'machineList',
      meta: { title: '机器列表', icon: 'server'},
      component: () => import('@/pages/sre/machine-list')
    },
    {
      path: 'machine/apply',
      name: 'machineApply',
      meta: { title: '申请机器记录', icon: 'server'},
      component: () => import('@/pages/sre/applyMachine')
    },
    {
      path: 'agent/list',
      name: 'agentList',
      meta: { title: '远程调试', icon: 'sitemap', hidden: !isAdmin },
      component: () => import('@/pages/sre/agent-list')
    },
    {
      path: 'agent/ssh',
      name: 'agentSSH',
      meta: { title: '远程控制台(Beta)', icon: 'sitemap', hidden: !isAdmin },
      component: () => import('@/pages/sre/deploy-ssh')
    },
    {
      path: 'nginx/list',
      name: 'nginxList',
      meta: { title: 'nginx治理', icon: 'server' },
      component: () => import('@/pages/sre/nginx')
    },
    {
      path: 'error/list',
      name: 'errorList',
      meta: { title: '错误列表', icon: 'cogs' },
      component: () => import('@/pages/sre/error-list')
    },
    {
      path: 'resource/list',
      name: 'resourceList',
      meta: { title: '资源列表', icon: 'cogs' },
      component: () => import('@/pages/sre/resource-list')
    },
    {
      path: 'deploy/onsiteInspection',
      name: 'onsiteInspection',
      meta: { title: '每日巡检', icon: 'cogs' },
      component: () => import('@/pages/sre/onsiteInspection')
    },
    {
      path: 'quota/record',
      name: 'quotaRecord',
      meta: { title: 'quota记录', icon: 'cogs' },
      component: () => import('@/pages/sre/quotaRecord')
    },
    {
      path: 'health/check',
      name: 'healthcheck',
      meta: { title: '健康监测', icon: 'cogs' },
      component: () => import('@/pages/sre/healthCheck')
    }
  ]
}