<template>
  <div class="preview-container">
    <div class="preview-header">
      <h2>{{ t('preview.title') }}</h2>
      <div class="preview-controls">
        <el-button type="primary" @click="capturePreview">
          <el-icon><Camera /></el-icon>
          {{ t('preview.capture') }}
        </el-button>
        <el-button @click="refreshPreview">
          <el-icon><Refresh /></el-icon>
          {{ t('preview.refresh') }}
        </el-button>
      </div>
    </div>

    <div class="preview-content">
      <el-image v-if="previewUrl" :src="previewUrl" :preview-src-list="[previewUrl]" fit="contain">
        <template #placeholder>
          <div class="image-placeholder">
            <el-icon><Loading /></el-icon>
          </div>
        </template>
        <template #error>
          <div class="image-error">
            <el-icon><PictureFilled /></el-icon>
            <span>{{ t('preview.loadError') }}</span>
          </div>
        </template>
      </el-image>
      <div v-else class="placeholder">
        <el-icon><Picture /></el-icon>
        <p>{{ t('preview.clickToStart') }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { t } from '@/locales/index'
import { ElMessage } from 'element-plus'
import { Camera, Refresh, Picture, PictureFilled, Loading } from '@element-plus/icons-vue'

const previewUrl = ref('')

declare const chrome: any

const capturePreview = async () => {
  try {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
    if (!tab.id) return

    const dataUrl = await chrome.tabs.captureVisibleTab()
    previewUrl.value = dataUrl
    ElMessage.success(t('preview.captureSuccess'))
  } catch (error) {
    console.error('预览捕获失败:', error)
    ElMessage.error(t('preview.captureError'))
  }
}

const refreshPreview = () => {
  capturePreview()
}
</script>

<style scoped>
.preview-container {
  padding: 1.5rem;
  width: 100%;
  height: 100%;
  background-color: var(--el-bg-color);
  transition: all 0.3s ease;
}

.preview-header {
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-header h2 {
  font-size: 1.5rem;
  color: var(--el-text-color-primary);
  margin: 0;
  font-weight: 600;
}

.preview-controls {
  display: flex;
  gap: 0.75rem;
}

.preview-controls :deep(.el-button) {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.preview-content {
  width: 100%;
  height: calc(100vh - 200px);
  border-radius: 8px;
  overflow: hidden;
  background-color: var(--el-bg-color-page);
  border: 2px dashed var(--el-border-color-light);
  transition: all 0.3s ease;
}

.preview-content:hover {
  border-color: var(--el-border-color);
}

.preview-content :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.placeholder,
.image-placeholder,
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
  gap: 1rem;
}

.placeholder .el-icon,
.image-error .el-icon {
  font-size: 3rem;
  opacity: 0.7;
}

.image-placeholder .el-icon {
  font-size: 2rem;
  animation: rotating 2s linear infinite;
}

.placeholder p,
.image-error span {
  margin: 0;
  font-size: 1rem;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 暗色主题适配 */
html.dark .preview-content {
  border-color: var(--el-border-color-darker);
}

html.dark .preview-content:hover {
  border-color: var(--el-border-color);
}
</style>
