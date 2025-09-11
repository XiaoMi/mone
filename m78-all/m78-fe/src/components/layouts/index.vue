<!--
 * @Description:
 * @Date: 2024-01-10 14:24:16
 * @LastEditTime: 2024-10-22 14:33:05
-->
<template>
  <div class="page-wrap">
    <div
      class="header-wrap"
      :class="{ isScroll: state.isScroll, isScrollProbot: state.active?.includes('probot') }"
    >
      <BaseHeader />
    </div>
    <div class="page-container">
      <RouterView class="page-content" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
const state = reactive({
  isScroll: false,
  active: '/'
})
const route = useRoute()
const paths: string[] = ['/', '/code', '/about']

const bindScroll = () => {
  if (!paths.includes(route.path)) {
    state.isScroll = true
  } else {
    state.isScroll = !!document.querySelector('.page-wrap')?.scrollTop
  }
}

watch(
  () => route.path,
  () => {
    bindScroll()
    state.active = route.path
  }
)

onUnmounted(() => {
  document.querySelector('.page-wrap')?.removeEventListener('scroll', bindScroll, false)
})

onMounted(() => {
  state.active = route.path
  bindScroll()
  document.querySelector('.page-wrap')?.addEventListener('scroll', bindScroll, false)
  // document.addEventListener('scroll', bindScroll, false)
})
</script>

<style scoped lang="scss">
.page-wrap {
  min-width: 1200px;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  .header-wrap {
    position: fixed;
    left: 0;
    top: 0;
    right: 0;
    z-index: 999;
    // background-color: var(--oz-menu-bg-color);
    background-color: transparent;
    color: #fff !important;
    transition: all 0.3s;
    :deep(i) {
      &.ep-sunny,
      &.language {
        color: #fff !important;
      }
    }
    &.isScroll {
      background-color: #fff;
      color: #333 !important;
      :deep(i) {
        &.ep-sunny,
        &.language {
          color: #333 !important;
        }
      }
      &.isScrollProbot {
        background-color: rgb(219 239 255);
      }
    }
    &.isScrollProbot {
      color: #333 !important;
      :deep(i) {
        &.ep-sunny,
        &.language {
          color: #333 !important;
        }
      }
    }
  }
}
.page-container {
  height: 100%;
  padding-top: 60px !important;
  display: flex;
  flex-direction: column;
}
.page-content {
  flex: 1;
}
</style>
