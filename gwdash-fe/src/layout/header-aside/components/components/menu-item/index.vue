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
  <el-menu-item :index="menu.path || uniqueId">
    <i :class="'icon spite-icon spite-icon-'+ menu.bgImage"></i>
    <span slot="title" class="title">{{menu.title || '未命名菜单'}}</span>
  </el-menu-item>
</template>

<script>
import { uniqueId } from 'lodash'
export default {
  name: 'd2-layout-header-aside-menu-item',
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
  line-height: 12px; 
}
</style>
<style lang="scss">
i.spite-icon{
  background-repeat:no-repeat;
  background-size:184px 20px;
  background-image:url(https://img.youpin.mi-img.com/middlewareGroup/26d982fa8669db87ee4195946438631e.png?w=553&h=60);
}
i.spite-icon-home{
  background-position:0px center;
}
i.spite-icon-gateway{
  background-position:-27px center;
}
i.spite-icon-application{
  background-position:-54px center;
}
i.spite-icon-sre{
  background-position:-81px center;
}
i.spite-icon-mischedule{
  background-position:-81px center;
}
i.spite-icon-nacos{
  background-position:-108px center;
}
i.spite-icon-wiki{
  background-position:-136px center;
}
i.spite-icon-cat{
  background-position:-164px center;
}
</style>