<template>
  <img src="@/assets/loading.gif" alt="loading" v-if="status == 0" class="img-loading" />
  <template v-if="status == 2">
    <p v-if="item?.rstMessage" class="err-tip">错误原因：{{ item.rstMessage }}</p>
    <img
      v-else
      src="@/assets/img-broken.png"
      alt="broken"
      class="failed-loading"
      :style="{ width: failedWidth }"
    />
  </template>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

const props = defineProps({
  item: {},
  status: {
    type: Number
  },
  failedWidth: {
    type: String,
    default: '40px'
  }
})
</script>

<style scoped>
.img-loading {
  width: 20px;
}
.err-tip {
  padding-top: 10px;
  font-size: 12px;
  color: #f89898;
  word-break: break-word;
  padding: 4px 10px;
  overflow: hidden;
  height: 100%;
  display: -webkit-box;
  -webkit-line-clamp: 8;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
