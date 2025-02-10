<template>
  <el-dropdown @command="handleCommand">
    <span class="lang-switch">
      {{ currentLang === 'zh-CN' ? '中文' : 'English' }}
      <el-icon class="el-icon--right"><arrow-down /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN">中文</el-dropdown-item>
        <el-dropdown-item command="en-US">English</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { setLocale } from '@/locales/index'
import { useI18n } from 'vue-i18n'
import { ArrowDown } from '@element-plus/icons-vue'

const { locale } = useI18n()
const currentLang = ref(locale.value)

const handleCommand = (command: string) => {
  locale.value = command
  currentLang.value = command
  setLocale(command as 'zh-CN' | 'en-US')
}
</script>

<style scoped>
.lang-switch {
  cursor: pointer;
  display: flex;
  align-items: center;
}
</style>
