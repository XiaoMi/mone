<script lang="ts" setup>
import { computed } from 'vue';
import { useAppStore } from '@/stores'
import { setLocale } from '@/locales';
import type { Language } from '@/stores/app/helper'

const appStore = useAppStore()

const language = computed(() => {
  return appStore.language
})

const handleCommand = (command: Language) => {
  appStore.setLanguage(command)
  setLocale(command)
}
</script>

<template>
  <div class="i18n">
    <el-dropdown @command="handleCommand">
      <el-icon size="24" class="language">
        <svg>
          <path
            fill="currentColor"
            d="m18.5 10l4.4 11h-2.155l-1.201-3h-4.09l-1.199 3h-2.154L16.5 10h2zM10 2v2h6v2h-1.968a18.222 18.222 0 0 1-3.62 6.301a14.864 14.864 0 0 0 2.336 1.707l-.751 1.878A17.015 17.015 0 0 1 9 13.725a16.676 16.676 0 0 1-6.201 3.548l-.536-1.929a14.7 14.7 0 0 0 5.327-3.042A18.078 18.078 0 0 1 4.767 8h2.24A16.032 16.032 0 0 0 9 10.877a16.165 16.165 0 0 0 2.91-4.876L2 6V4h6V2h2zm7.5 10.885L16.253 16h2.492L17.5 12.885z"
          ></path>
        </svg>
      </el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-if="language !== 'en-US'" command="en-US">English</el-dropdown-item>
          <el-dropdown-item v-if="language !== 'zh-CN'" command="zh-CN">中文</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<style lang="scss" scoped>
.i18n {
  margin-right: 10px;
}
</style>
