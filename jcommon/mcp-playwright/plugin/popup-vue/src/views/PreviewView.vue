<template>
  <div class="preview-container">
    <div class="preview-header">
      <h2>页面预览</h2>
      <div class="preview-controls">
        <button @click="capturePreview" class="capture-btn">
          <i class="fas fa-camera"></i>
          捕获预览
        </button>
        <button @click="refreshPreview" class="refresh-btn">
          <i class="fas fa-sync-alt"></i>
          刷新
        </button>
      </div>
    </div>

    <div class="preview-content">
      <img v-if="previewUrl" :src="previewUrl" alt="页面预览" />
      <div v-else class="placeholder">
        <i class="fas fa-image placeholder-icon"></i>
        <p>点击"捕获预览"按钮开始</p>
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
  padding: 24px;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.preview-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-header h2 {
  font-size: 1.5rem;
  color: #1a1a1a;
  margin: 0;
  font-weight: 600;
}

.preview-controls {
  display: flex;
  gap: 12px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s ease;
}

button i {
  font-size: 1rem;
}

.capture-btn {
  background-color: #10B981;
  color: white;
}

.capture-btn:hover {
  background-color: #059669;
  transform: translateY(-1px);
}

.refresh-btn {
  background-color: #3B82F6;
  color: white;
}

.refresh-btn:hover {
  background-color: #2563EB;
  transform: translateY(-1px);
}

.preview-content {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f8fafc;
  border: 2px dashed #e2e8f0;
  transition: all 0.3s ease;
}

.preview-content:hover {
  border-color: #94a3b8;
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
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.placeholder-icon {
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.5;
}

.placeholder p {
  margin: 0;
  font-size: 1rem;
}
</style>
