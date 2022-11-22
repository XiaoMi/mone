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
  path: '/',
  name: 'gateway',
  meta: { title: '智能网关', icon: 'tachometer' },
  redirect: { name: 'apiInfo' },
  component: layoutHeaderAside,
  children: [
    {
      path: 'index',
      name: 'index',
      redirect: { name: 'apiInfo' }
    },
    {
      path: 'apicluster/list',
      name: 'apicluster',
      meta: { title: 'API聚合列表', icon: 'users' },
      component: () => import('@/pages/gateway/api-cluster')
    },
    {
      path: 'apigroup/apply',
      name: 'apiGroupApply',
      meta: { title: '分组申请', icon: 'gitlab' },
      component: () => import('@/pages/gateway/api-group-apply')
    },
    {
      path: 'apidomain/list',
      name: 'apidomain',
      meta: { title: 'API域名管理', icon: 'users' },
      component: () => import('@/pages/gateway/api-domain')
    },
    {
      path: 'apigroup/list',
      name: 'apiGroup',
      meta: { title: 'API分组列表', icon: 'users' },
      component: () => import('@/pages/gateway/api-group')
    },
    {
      path: 'apiinfo/list',
      name: 'apiInfo',
      meta: { title: 'API信息列表', icon: 'list-alt' },
      component: () => import('@/pages/gateway/api-info')
    },
    {
      path: 'plugins/list',
      name: 'plugins',
      meta: { title: 'plugins', icon: 'plug' },
      component: () => import('@/pages/gateway/plugins')
    },
    {
      path: 'cfilter/upload/list',
      name: 'cFilterUpload',
      meta: { title: 'filter上传', icon: 'upload' },
      component: () => import('@/pages/gateway/common-filters/upload-list')
    },
    {
      path: 'plugin/audit/list',
      name: 'PluginAuditList',
      meta: { title: '我的审核', icon: 'legal' },
      component: () => import('@/pages/gateway/plugins-audit')
    },
    {
      path: 'cfilter/audit/list',
      name: 'cFilterAudit',
      meta: { title: 'filter审核', icon: 'legal' },
      component: () => import('@/pages/gateway/common-filters/audit-list')
    },
    {
      path: 'ds/list',
      name: 'ds',
      meta: { title: '数据源', icon: 'database' },
      component: () => import('@/pages/gateway/data-source')
    },
    {
      path: 'agent/list',
      name: 'agent',
      meta: { title: '节点信息', icon: 'sitemap' },
      component: () => import('@/pages/gateway/agent')
    }
  ]
}
