<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <div class="d2-layout-header-aside-group">
    <!-- 半透明遮罩 -->
    <div class="d2-layout-header-aside-mask"></div>
    <!-- 主体 -->
    <div class="d2-layout-header-aside-content" flex="dir:top">
      <!-- 顶栏 -->
      <div class="d2-theme-header" flex flex-box="0">
        <div class="d2-theme-header-content" flex>
          <MioneP isPro name="智能网关" class="gateway" />
          <!-- <MioneMessage /> -->
          <d2-menu-header flex-box="1"/>
          <div class="d2-header-right" flex-box="0">
            <d2-header-switch />
            <d2-header-user />
          </div>
        </div>
      </div>
      <!-- 下面 主体 -->
      <div flex-box="1" flex style="justify-content: center">
        <div class="d2-theme-container" flex>
          <!-- 主体 侧边栏 -->
          <div flex-box="0" ref="aside" :class="classObject" style="width: 220px">
              <d2-menu-side/>
          </div>
          <!-- 主体 -->
          <div class="d2-theme-container-main" flex-box="1" flex>
            <!-- 内容 -->
            <transition name="fade-scale">
              <div class="d2-theme-container-main-layer" flex="dir:top">
                <!-- 页面 -->
                <div class="d2-theme-container-main-body" flex-box="1">
                  <transition :name="transitionActive ? 'fade-transverse' : ''">
                    <keep-alive>
                      <router-view/>
                    </keep-alive>
                  </transition>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import MioneP from 'mione-p'
import 'mione-p/dist/mione-p.css'

export default {
  name: 'd2-layout-header-aside',
  components: {
    'd2-menu-side': () => import('./components/menu-side'),
    'd2-menu-header': () => import('./components/menu-header'),
    'd2-header-switch': () => import('./components/header-switch'),
    'd2-header-user': () => import('./components/header-user'),
    'MioneP': MioneP
  },
  data () {
    return {
      navUrl: process.env.VUE_APP_NAV_URL
    }
  },
  computed: {
    ...mapState('d2admin', {
      transitionActive: state => state.transition.active
    }),
    asideHeight () {
      return this.$route.path === '/index' ? '768px' : '100%'
    },
    classObject () {
      return {
        'd2-theme-container-aside': true,
        'aside': this.$route.path === '/index'
      }
    }
  },
  mounted () {},
  methods: {
    toHomePage () {
      this.$router.push('/index')
    }
  }
}
</script>

<style lang="scss">
// 注册主题
@import '~@/assets/style/theme/register.scss';
.notification{
  color: #409EFF;
}
.theme-d2 .d2-layout-header-aside-group {
  background-color: #EFF0F4;
}

.d2-layout-header-aside-group .d2-layout-header-aside-content .d2-theme-container .aside.d2-theme-container-aside .d2-layout-header-aside-menu-side {
  background-color: #EFF0F4;
  height: 766px;
  max-height: 100%;
}

.d2-layout-header-aside-group .d2-layout-header-aside-content .d2-theme-container .aside.d2-theme-container-aside  .d2-layout-header-aside-menu-side > .el-menu {
  min-height: 100%;
  background-color: #fff;
}
.d2-theme-header .gateway .mione-pro-logo .title {
  font-size: 18px;
}
</style>
