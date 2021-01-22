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
  <li 
    class='el-menu-item'
    style="padding-left:20px; pointer-events:auto">
    <i :class="`icon spite-icon spite-icon-${menu.bgImage}`"></i>
    <a :href="menu.href" target='_blank'>{{menu.title}}</a>
  </li>
</template>

<script>
export default {
  props: {
    menu: {
      type: Object,
      required: false,
      default: () => {}
    }
  },
  data () {
    return {}
  }
}
</script>

<style lang="scss" scoped>
.icon {
  display: inline-block;
  width: 20px;
  height: 20px;
}
a {
  font-family: PingFangSC-Medium;
  font-size: 16px;
  margin-left: 6px;
  line-height: 12px; 
  color: #333
}
a:hover {
  color: #457BFC
}
</style>