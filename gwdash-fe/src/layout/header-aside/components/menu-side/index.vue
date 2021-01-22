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
  <div class="d2-layout-header-aside-menu-side">
    <div 
      v-if="aside.length === 0 && !asideCollapse"
      style='width:100%; height:766px; background:#fff; padding-top:10px'>
      <div 
        class="d2-layout-header-aside-menu-empty" 
        flex="dir:top main:center cross:center">
        <d2-icon name="folder-open-o"/>
        <span>没有侧栏菜单</span>
        <span style='margin-top:6px'>请联系管理员添加 ~ </span>
      </div>
    </div>
    <el-menu
      v-else
      :collapse="asideCollapse"
      :unique-opened="true"
      :default-active="active"
      ref="menu"
      @select="handleMenuSelect">
      <template v-for="(item, index) in aside">
        <menu-link v-if='item.link' :menu='item' :key='index'/>
        <menu-item v-else-if='!item.children' :menu="item" :key='index'/>
        <menu-sub v-else :menu="item" :key="index"/>
      </template>
    </el-menu>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import menuMixin from '../mixin/menu'
import menuItem from '../components/menu-item'
import menuSub from '../components/menu-sub'
import menuLink from '../components/menu-link'
import BScroll from 'better-scroll'
export default {
  name: 'd2-layout-header-aside-menu-side',
  mixins: [
    menuMixin
  ],
  components: {
    menuItem,
    menuSub,
    menuLink
  },
  data () {
    return {
      active: '',
      asideHeight: 300,
      BS: null
    }
  },
  computed: {
    ...mapState('d2admin/menu', [
      'aside',
      'asideCollapse'
    ])
  },
  watch: {
    // 折叠和展开菜单的时候销毁 better scroll
    asideCollapse (val) {
      this.scrollDestroy()
      setTimeout(() => {
        this.scrollInit()
      }, 500)
    },
    // 监听路由 控制侧边栏激活状态
    '$route.matched': {
      handler (val) {
        this.active = val[val.length - 1].path
        this.$nextTick(() => {
          if (this.aside.length > 0) {
            this.$refs.menu.activeIndex = this.active
          }
        })
      },
      immediate: true
    }
  },
  mounted () {
    this.scrollInit()
  },
  beforeDestroy () {
    this.scrollDestroy()
  },
  methods: {
    scrollInit () {
      this.BS = new BScroll(this.$el, {
        mouseWheel: true
        // 如果你愿意可以打开显示滚动条
        // scrollbar: {
        //   fade: true,
        //   interactive: false
        // }
      })
    },
    scrollDestroy () {
      // https://github.com/d2-projects/d2-admin/issues/75
      try {
        this.BS.destroy()
      } catch (e) {
        delete this.BS
        this.BS = null
      }
    }
  }
}
</script>
