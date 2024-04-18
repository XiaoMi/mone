<template>
  <el-dropdown @command="handleCommand">
    <span class="el-dropdown-link" @click="switchShow">
      <el-icon><More /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="refresh">{{ t('common.refresh') }}</el-dropdown-item>
        <el-dropdown-item command="delete">{{ t('common.delete') }}</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>
<script setup>
import { t } from '@/locales'
import { ref } from 'vue'
const emits = defineEmits(['commandFn'])
const handleCommand = (command) => {
  emits('commandFn', command)
}
</script>
<style lang="scss" scoped>
.el-dropdown-link {
  margin-left: 10px;
}
</style>
