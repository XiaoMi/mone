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

import gateway from './modules/gateway'
import application from './modules/application'
import sre from './modules/sre'
import nacos from './modules/nacos'
import cat from './modules/cat'
import wiki from './modules/wiki'
import account from './modules/account'
import course from './modules/course'
import devTest from "./modules/devTest"
import layoutHeaderAside from '@/layout/header-aside'
import project from './modules/project'
import config from './modules/config'
import quality from './modules/quality'
import traffic from './modules/traffic'

// 在主框架内显示
const frameIn = [
  {
    path: '/',
    redirect: { name: 'index' },
    component: layoutHeaderAside,
    children: [
      {
        path: 'index',
        name: 'index',
        meta: { title: '首页', icon: 'home' },
        component: () => import('@/pages/index')
      }
    ]
  },
  gateway,
  application,
  sre,
  nacos,
  cat,
  wiki,
  account,
  course,
  devTest,
  project,
  config,
  quality,
  traffic
]

// 主框架之外显示
const frameOut = [
  // 页面重定向使用 必须保留
  {
    path: '/redirect/:path*',
    component: {
      beforeCreate () {
        this.$router.replace(JSON.parse(this.$route.params.path))
      },
      render: h => h()
    }
  },
  {
    path: '/deploy/allConsole',
    name: 'allConsole',
    meta: { title: '所有日志',},
    component: () => import('@/pages/application/deploy/components/allConsole.vue')
  },
]
// 错误页面
const errorPage = [
  // 403
  {
    path: '/403',
    name: '403',
    component: () => import('@/pages/error-page-403')
  },
  // 404
  {
    path: '*',
    name: '404',
    component: () => import('@/pages/error-page-404')
  }
]

// 导出需要显示菜单的
export const frameInRoutes = frameIn

// 重新组织后导出
export default [
  ...frameIn,
  ...frameOut,
  ...errorPage
]