<template>
  <div v-if="props.loading" class="loading-box div600">
    <img :src="loadingImg" alt="" class="loading-img" />
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  }
})
import loadingImg from '@/assets/loading.gif'
import { defineProps } from 'vue'
</script>

<style scoped lang="scss">
.loading-box {
  border: dashed 1px #dcdfe6;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}
.div600 {
  width: 600px;
  height: 600px;
}
.loading-img {
  width: 50px;
}
</style>
