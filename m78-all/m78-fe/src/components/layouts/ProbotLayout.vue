<template>
  <div :class="'probot-layout ' + (state.active ? '' : 'h100')">
    <div class="probot-bg"></div>
    <RouterView class="probot-page" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
const state = reactive({
  active: true
})
const route = useRoute()
const paths: string[] = ['/probot', '/probot-index']

watch(
  () => route.path,
  () => {
    state.active = paths.includes(route.path)
  }
)

onUnmounted(() => {})

onMounted(() => {
  state.active = paths.includes(route.path)
})
</script>

<style scoped lang="scss">
.probot-layout {
  margin-top: -60px !important;
  padding-top: 60px;
  background-size: 200% 200%;
  background-position: 0px 0;
  background-image: url('../../assets/probot-bg-image.png');
  &.h100 {
    height: 100%;
  }
}
.probot-bg {
  background: linear-gradient(270deg, #cbe6fd, #f0f6fb);
  background-size: 200% 200%;
  animation: search_styles_Gradient__79kRw 3s ease infinite;
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}
@keyframes search_styles_Gradient__79kRw {
  0% {
    background-position: 0 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  to {
    background-position: 0 50%;
  }
}
</style>
