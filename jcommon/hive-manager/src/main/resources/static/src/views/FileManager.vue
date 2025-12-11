<template>
  <FileManagerCore :adapter="adapter" :mode="mode" />
</template>

<script lang="ts" setup>
import { FileManagerCore, LocalFileSystemAdapter } from '@/components/FileManager'

// 使用本地文件系统适配器
const mode = 'local'
const adapter = new LocalFileSystemAdapter()
</script>

<style scoped>
/* 样式已经在 FileManagerCore 中定义 */
</style>
