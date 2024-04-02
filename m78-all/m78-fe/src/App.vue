<script setup lang="ts">
import { computed } from 'vue'
import { useAppStore, useUserStore, useSettingStore } from './stores'
import { getElementPlusI18n } from './locales'

const { language } = useAppStore()
const userStore = useUserStore()
const { initUserInfo } = userStore
const { fetchModels } = useSettingStore()

const locale = computed(() => {
  return getElementPlusI18n(language)
})

initUserInfo()
fetchModels()
</script>

<template>
  <el-config-provider :locale="locale" namespace="oz">
    <RouterView />
  </el-config-provider>
</template>

<style>
@import '//at.alicdn.com/t/c/font_4406517_9eqmh0lvil.css';
#app {
  height: 100%;
}
</style>
