<!--
 * @Description: 
 * @Date: 2024-01-24 09:58:11
 * @LastEditTime: 2024-01-26 18:44:19
-->
<template>
  <div v-for="(item, index) in props.data" :key="item.index + index">
    <template
      v-if="
        path === '/manual' ||
        path.includes('/manual/' + item?.index) ||
        ('/manual/' + item?.index).includes(path)
      "
    >
      <h1 :class="['title-' + item.deep]">{{ item.title }}</h1>
      <component :is="item.componentName"></component>
    </template>
    <div v-if="item?.children?.length">
      <ContentItem :data="item?.children" :path="path"></ContentItem>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'ContentItem' //给组件命名
}
</script>

<script setup lang="ts">
const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  path: {}
})
</script>

<style scoped lang="scss">
.title-1 {
  font-size: 22px;
  padding: 10px 0;
}
.title-2 {
  font-size: 20px;
  padding: 8px 0;
}
.title-3 {
  font-size: 18px;
  padding: 6px 0;
}
</style>
