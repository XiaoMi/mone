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
  <el-submenu :index="menu.path || uniqueId">
    <template slot="title">
      <i :class="'icon spite-icon spite-icon-'+ menu.bgImage"></i>
      <span slot="title" class="title">{{menu.title}}</span>
    </template>
    <el-menu-item 
      v-for="(child,childIndex) in menu.children" 
      :key="childIndex" 
      :index="child.path">
      <div slot="title">{{ child.title }}</div>
    </el-menu-item>
  </el-submenu>
</template>

<script>
import { uniqueId } from 'lodash'
// 组件
import d2LayoutMainMenuItem from '../menu-item'

export default {
  name: 'd2-layout-header-aside-menu-sub',
  components: {
    'd2-layout-header-aside-menu-item': d2LayoutMainMenuItem
  },
  props: {
    menu: {
      type: Object,
      required: false,
      default: () => {}
    }
  },
  data () {
    return {
      uniqueId: uniqueId('d2-menu-empty-')
    }
  }
}
</script>
<style lang="scss" scoped>
.icon {
  display: inline-block;
  width: 20px;
  height: 20px;
}
.title {
  font-family: PingFangSC-Medium;
  font-size: 16px;
  margin-left: 6px;
}
</style>

<style lang="scss">
.d2-layout-header-aside-group .d2-layout-header-aside-content .d2-theme-container .d2-theme-container-main .d2-theme-container-main-layer {
  width: 1051px;
  margin-left: 10px;
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-submenu .el-submenu__title,
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu .el-menu-item {
  height: 40px;
  display: flex;
  align-items: center;
  margin-top: 9px;
  font-family: PingFangSC-Medium;
  font-size: 16px;
  color: #333333;
  line-height: 12px; 
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu.el-menu--inline .el-menu-item {
  height: 24px;
  margin-top: 10px;
  padding-left: 48px !important
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu.el-menu--inline .el-menu-item div {
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #666666;
    line-height: 12px;
    background: #F6F6F6;
    padding: 6px 8px;
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu.el-menu--inline .el-menu-item:hover {
  background-color: #FFFFFF;
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu.el-menu--inline .el-menu-item div:hover {
  color: #457BFC;
  background-color: #f0f4ff;
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu.el-menu--inline .el-menu-item.is-active div {
  color: #457BFC;
  background-color: #f0f4ff;
}

.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu .el-menu-item.is-active {
  color: #457BFC;
}
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-menu .el-menu-item:hover, 
.theme-d2 .d2-theme-container .d2-theme-container-aside .el-submenu .el-submenu__title:hover {
  background-color: #f0f4ff;
}
</style>