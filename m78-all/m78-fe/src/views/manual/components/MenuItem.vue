<!--
 * @Description: 
 * @Date: 2024-01-24 09:58:11
 * @LastEditTime: 2024-01-26 19:01:03
-->
<template>
  <template v-for="(item, key) in props.data" :key="key">
    <el-sub-menu :index="'/manual/' + item.index" v-if="item?.children?.length">
      <template #title v-if="item?.children">
        <span
          :class="path === '/manual/' + item?.index ? 'active' : ''"
          @click.stop="subMenuClick('/manual/' + item?.index)"
          >{{ item.title }}</span
        >
      </template>
      <menuItem :data="item?.children" :path="path"></menuItem>
    </el-sub-menu>
    <el-menu-item :index="'/manual/' + item.index" v-else> {{ item.title }}</el-menu-item>
  </template>
</template>

<script lang="ts">
export default {
  name: 'menuItem' //给组件命名
}
</script>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  path: {}
})

const router = useRouter()

const subMenuClick = (path: string) => {
  router.push({
    path: path
  })
}
</script>

<style scoped lang="scss">
a {
  display: inline-block;
  width: 100%;
}
.active {
  color: var(--oz-color-primary);
}
</style>
