<template>
  <div class="preview-container">
    <div class="preview-controls">
      <button @click="capturePreview" class="capture-btn">捕获预览</button>
      <button @click="refreshPreview" class="refresh-btn">刷新</button>
    </div>

    <div class="preview-content">
      <img v-if="previewUrl" :src="previewUrl" alt="页面预览" />
      <div v-else class="placeholder">
        点击"捕获预览"按钮开始
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const previewUrl = ref('')

const capturePreview = async () => {
  try {
    const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
    if (!tab.id) return

    const dataUrl = await chrome.tabs.captureVisibleTab()
    previewUrl.value = dataUrl
  } catch (error) {
    console.error('预览捕获失败:', error)
  }
}

const refreshPreview = () => {
  capturePreview()
}
</script>

<style scoped>
.preview-container {
  padding: 16px;
  width: 100%;
}

.preview-controls {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.capture-btn {
  background-color: #4CAF50;
  color: white;
}

.refresh-btn {
  background-color: #2196F3;
  color: white;
}

.preview-content {
  width: 100%;
  height: 300px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.preview-content img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  background-color: #f5f5f5;
}
</style>
