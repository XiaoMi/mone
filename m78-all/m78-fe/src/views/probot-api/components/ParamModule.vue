<!--
 * @Description: 
 * @Date: 2024-08-14 21:33:27
 * @LastEditTime: 2024-08-14 22:08:47
-->
<template>
  <div v-for="(item, key) in data" :key="key" :class="[key === '_KEYDESC' ? 'inline' : '']">
    <template v-if="key === '_KEYDESC'">{{ item }}</template>
    <div v-else class="param-item">
      <span class="key">{{ key }}</span>
      <span class="type">({{ getType(item) }})</span>
      <ParamModule :data="item" v-if="getType(item) === 'object'"></ParamModule>
      <ParamModule :data="item[0]" v-else-if="getType(item) === 'array'"></ParamModule>
      <span v-else>{{ item }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import ParamModule from './ParamModule.vue'
const props = defineProps({
  data: {
    type: Object,
    default() {
      return {}
    }
  }
})

var class2type = {}, //用于记录[object class]样式
  objs = 'Boolean Number String Function Array Date RegExp Null Undefined Object'.split(' ')
for (var i = 0, l = objs.length; i < l; i++) {
  class2type['[object ' + objs[i] + ']'] = objs[i].toLowerCase()
}
function getType(obj) {
  return class2type[Object.prototype.toString.call(obj)] || ''
}
</script>

<style scoped lang="scss">
.inline {
  display: inline;
}
.param-item {
  padding-top: 10px;
  padding-left: 20px;
  &::before {
    content: '';
    display: inline-block;
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background-color: #666;
    margin-right: 10px;
  }
  span.key {
    display: inline-block;
    padding: 0px 6px;
    border-radius: 6px;
    border: 1px solid #ddd;
    background-color: #f7f7f7;
    font-size: 12px;
    margin-right: 8px;
  }
  span.type {
    display: inline-block;
    font-size: 12px;
    margin-right: 8px;
  }
}
</style>
