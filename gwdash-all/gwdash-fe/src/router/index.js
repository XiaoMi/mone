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

import Vue from 'vue'
import VueRouter from 'vue-router'
import routes from './routes'
import store from '@/store/index'
import util from '@/libs/util.js'

// 进度条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

Vue.use(VueRouter)

const router = new VueRouter({
  base: '/gwdash',
  mode: 'history',
  routes
})

const list = [
  '/gateway/apigroup/list',
  '/gateway/apiinfo/list',
  '/gateway/plugins/list',
  '/gateway/cfilter/upload/list',
  '/gateway/ds/list',
  '/gateway/agent/list',
  '/gateway/mirror/list',
  '/application/list',
  '/application/show',
  '/sre/machine/list',
  '/sre/agent/list',
  '/sre/agent/ssh',
  '/sre/nginx/list',
  '/sre/error/list',
  '/sre/agent/ssh',
  '/sre/resource/list',
  '/nacos/instances',
  '/nacos/configs',
  '/cat',
  '/wiki/doc/list',
  '/wiki/issue/feedback',
  '/wiki/issue/feedbacks',
  '/projectAudit/operationLog',
  '/projectAudit/auditList'
]

router.beforeEach((to, from, next) => {
  // 进度条
  NProgress.start()
  // 关闭搜索面板
  store.commit('d2admin/search/set', false)
  // 验证当前路由所有的匹配中是否需要有登录验证的
  // if (to.matched.some(r => r.meta.requiresAuth)) {
  //   // 这里暂时将cookie里是否存有token作为验证是否登录的条件
  //   // 请根据自身业务需要修改
  //   const token = util.cookies.get('token')
  //   if (token && token !== 'undefined') {
  //     next()
  //   } else {
  //     // 将当前预计打开的页面完整地址临时存储 登录后继续跳转
  //     // 这个 cookie(redirect) 会在登录后自动删除
  //     util.cookies.set('redirect', to.fullPath)
  //     // 没有登录的时候跳转到登录界面
  //     next({
  //       name: 'login'
  //     })
  //   }
  // } else {
  //   // 不需要身份校验 直接通过
  //   next()
  // }

  // 首页常见功能卡片
  if (list.indexOf(to.fullPath) !== -1) {
    store.commit('d2admin/feature/recordTimes', to.fullPath)
  }
  next()
})

router.afterEach(to => {
  // 进度条
  NProgress.done()
  // 需要的信息
  const app = router.app
  const { name, params, query } = to
  // 多页控制 打开新的页面
  app.$store.dispatch('d2admin/page/open', { name, params, query })
  // 更改标题
  util.title(to.meta.title)
})

export default router
