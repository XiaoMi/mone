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
// polyfill
import 'babel-polyfill'
// Vue
import Vue from 'vue'
import App from './App'
// store
import store from '@/store'
// 模拟数据
// import '@/mock'
// 多国语
// import i18n from './i18n'
// 核心插件
import d2Admin from '@/plugin/d2admin'

import VueCompositionAPI from '@vue/composition-api'

// import MioneP from 'mione-p'
// import 'mione-p/dist/mione-p.css'

// [ 可选插件组件 ]D2-Crud
// import D2Crud from '@d2-projects/d2-crud'

import 'v-contextmenu/dist/index.css'
import contentmenu from 'v-contextmenu'

// 菜单和路由设置
import router from './router'
// import { menuAside } from '@/menu'
// import { frameInRoutes } from '@/router/routes'

// codemirror
import VueCodemirror from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'

// Element 组件库
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import jsonDag from '@/components/json-dag/index'

import { mapActions } from 'vuex'

// echarts插件
// import echarts from 'echarts'
// Vue.prototype.$echarts = echarts;

// 核心插件
Vue.use(d2Admin)

// 可选插件组件
// https://github.com/d2-projects/d2-crud
// Vue.use(D2Crud)

Vue.use(contentmenu)

// Element 组件库
Vue.use(ElementUI)

// codemirror
Vue.use(VueCodemirror)

Vue.use(jsonDag)

Vue.use(VueCompositionAPI)

// Vue.use(MioneP)

// Vue-tools
Vue.config.devtools = true

new Vue({
  router,
  store,
  render: (h) => h(App),
  created () {
    Promise.all([
      // 获取用户信息
      this.$store.dispatch('d2admin/dealUserInfo/getUserInfo'),
      // 侧边栏菜单
      this.$store.dispatch('d2admin/menu/loadAsideSet'),
      // 获取网关租户相关信息
      this.$store.dispatch('d2admin/tenant/loadTenantId'),
      this.$store.dispatch('d2admin/tenant/loadTenantOptions')
    ]).then((e) => {
      console.log(e)
      this.setIsLoading(false)
    }, (e) => {
      console.error(e)
    })
  },
  mounted () {
    // 用户登录后从数据库加载一系列的设置
    this.$store.dispatch('d2admin/account/load')
    // 获取并记录用户 UA
    this.$store.commit('d2admin/ua/get')
  },
  methods: {
    ...mapActions('d2admin/page', ['setIsLoading'])
  }
}).$mount('#app')
