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
  <div class="d2-layout-header-aside-group" :style="styleLayoutMainGroup" :class="{grayMode: grayActive}">
    <!-- 半透明遮罩 -->
    <div class="d2-layout-header-aside-mask"></div>
    <!-- 主体 -->
    <div class="d2-layout-header-aside-content" flex="dir:top">
      <!-- 顶栏 -->
      <div class="d2-theme-header" flex flex-box="0">
        <div class="d2-theme-header-content">
          <div class="logo-group" flex-box="0">
             <!-- <img :src="`${$baseUrl}image/logo/logo.png`"> -->
             <div class="logo" @click="toHomePage"></div>
             <div class="name">小米研发协同平台</div>
          </div>
          <d2-menu-header flex-box="1"/>
          <div class="d2-header-right" flex-box="0">
            <!-- 如果你只想在开发环境显示这个按钮请添加 v-if="$env === 'development'" -->
            <d2-header-search @click="handleSearchClick"/>
            <d2-header-user/>
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
            <!-- 搜索 -->
            <transition name="fade-scale">
              <div v-show="searchActive" class="d2-theme-container-main-layer" flex="dir:top">
                <d2-panel-search ref="panelSearch" @close="searchPanelClose"/>
              </div>
            </transition>
            <!-- 内容 -->
            <transition name="fade-scale">
              <div v-show="!searchActive" class="d2-theme-container-main-layer" flex="dir:top">
                <!-- 页面 -->
                <div class="d2-theme-container-main-body" flex-box="1">
                  <transition :name="transitionActive ? 'fade-transverse' : ''">
                    <keep-alive :include="keepAlive">
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
import { mapState, mapGetters, mapActions } from 'vuex'
import mixinSearch from './mixins/search'
export default {
  name: 'd2-layout-header-aside',
  mixins: [
    mixinSearch
  ],
  components: {
    'd2-menu-side': () => import('./components/menu-side'),
    'd2-menu-header': () => import('./components/menu-header'),
    'd2-header-search': () => import('./components/header-search'),
    'd2-header-user': () => import('./components/header-user')
  },
  data () {
    return {}
  },
  computed: {
    ...mapState('d2admin', {
      keepAlive: state => state.page.keepAlive,
      grayActive: state => state.gray.active,
      transitionActive: state => state.transition.active,
      asideCollapse: state => state.menu.asideCollapse
    }),
    ...mapGetters('d2admin', {
      themeActiveSetting: 'theme/activeSetting'
    }),
    asideHeight () {
      return this.$route.path === '/index' ? '768px' : '100%'
    },
    /**
     * @description 最外层容器的背景图片样式
     */
    styleLayoutMainGroup () {
      return {
        ...this.themeActiveSetting.backgroundImage ? {
          backgroundImage: `url('${this.$baseUrl}${this.themeActiveSetting.backgroundImage}')`
        } : {}
      }
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
}

.d2-layout-header-aside-group .d2-layout-header-aside-content .d2-theme-container .aside.d2-theme-container-aside  .d2-layout-header-aside-menu-side > .el-menu {
  min-height: 766px;
  background-color: #fff;
}
</style>

<style lang="scss" scoped>
.logo-group{
  display: flex;
  .logo {
    width: 125px;
    height: 58px;
    cursor: pointer;
    background-image: url(xx_replace_xx);
    background-size: 100% 100%;

    // background-image: url(xx_replace_xx);
    //  background-repeat: no-repeat;
    //  background-position: -10px -10px
  }
  .name {
    margin-top: 24px;
    height: 17px;
    line-height: 17px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #FFFFFF;
  }
}
</style>
