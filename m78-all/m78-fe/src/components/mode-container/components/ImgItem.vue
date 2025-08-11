<template>
  <div class="single-img-wrap">
    <img alt="" class="center-img" :src="props.imgUrl" />
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

const props = defineProps({
  imgUrl: {}
})
</script>

<style scoped>
.center-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.single-img-wrap {
  width: 100%;
  height: 100%;
  border: dashed 1px #dcdfe6;
}
</style>
