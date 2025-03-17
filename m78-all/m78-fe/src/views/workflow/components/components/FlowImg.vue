<template>
  <img v-if="`${imgUrl}`.includes('http')" :src="imgUrl" class="img-header" :style="commonStyle" />
  <BaseIcon v-else :index="imgUrl || '1'" class="img-header" :style="commonStyle"></BaseIcon>
</template>

<script setup lang="ts">
import { defineProps, computed } from 'vue'
import BaseIcon from '@/components/BaseIcon.vue'

const props = defineProps({
  imgUrl: {},
  imgWidth: {}
})
const commonStyle = computed(() => {
  return { width: `${props.imgWidth}px`, height: `${props.imgWidth}px` }
})
</script>

<style scoped lang="scss">
.img-header {
  border-radius: 8px;
  margin-right: 12px;
  :deep(.iconfont) {
    font-size: 20px;
  }
}
</style>
