<template>
  <div class="sc-message--text">
    <audio
      v-if="message.type === 'audio'"
      :src="src"
      controls
      class="custom-audio-player"
    ></audio>
    <div
      v-else-if="message.type === 'image'"
    >
      <el-image
        style="height: 80px;"
        fit="scale-down"
        :src="src"
        :preview-src-list="[src]"
      >
        <template #error>
          <div class="image-slot">
            <el-icon><icon-picture /></el-icon>
          </div>
        </template>
      </el-image>
      <div>{{ message.data.content }}</div>
    </div>
    <template v-else-if="message.type === 'images'">
      <div v-for="(item, index) in items" :key="index">
        <el-image
          style="height: 80px;"
          fit="scale-down"
          v-if="handleImageSrc(item)"
          :src="handleImageSrc(item)"
          :preview-src-list="[handleImageSrc(item)]"
        >
          <template #error>
            <div class="image-slot">
              <el-icon><icon-picture /></el-icon>
            </div>
          </template>
        </el-image>
        <div v-if="handleImageText(item)">{{ handleImageText(item) }}</div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps({
  message: {
    type: Object,
    required: true
  },
});

const src = computed(() => {
  const url = props.message.data.text;
  if (url) {
    return url;
  }
  return "";
});
const items = computed(() => {
  let list = props.message.data.text;
  try {
    list = JSON.parse(list);
  }catch(e) {
    list = [];
  }
  return list;
});

const handleImageSrc = (item: any) => {
  if (item.type === "image"){
    return `data:${item.mimeType};base64,${item.data}`;
  }
  return "";
}

const handleImageText = (item: any) => {
  if (item.type === "text"){
    return item.text;
  }
  return "";
}
</script>

<style scoped>
.custom-audio-player {
  width: 300px;
  height: 40px;
  border-radius: 20px;
  outline: none;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1),
              inset 0 1px 1px rgba(255, 255, 255, 0.2);
  padding: 4px;
  transition: all 0.3s ease;
}

.custom-audio-player:hover {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.custom-audio-player::-webkit-media-controls-panel {
  background-color: transparent !important;
  border-radius: 20px;
}

.custom-audio-player::-webkit-media-controls-play-button {
  background-color: transparent;
  border-radius: 50%;
  margin: 0 8px;
  transition: all 0.2s ease;
  filter: drop-shadow(0 0 4px rgba(2, 227, 239, 0.5));
}

.custom-audio-player::-webkit-media-controls-play-button:hover {
  background-color: #40f7ff;
  filter: drop-shadow(0 0 8px rgba(0, 242, 254, 0.7));
}

.custom-audio-player::-webkit-media-controls-timeline {
  border-radius: 4px;
  height: 3px;
  background-color: transparent;
}

.custom-audio-player::-webkit-media-controls-current-time-display,
.custom-audio-player::-webkit-media-controls-time-remaining-display {
  color: #ffffff;
  font-size: 12px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.custom-audio-player::-webkit-media-controls-volume-slider {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.custom-audio-player::-webkit-media-controls-mute-button {
  filter: brightness(0) invert(1);
  opacity: 1;
}

/* 自定义进度条样式 */
.custom-audio-player::-webkit-slider-thumb {
  background: #00f2fe;
}

.custom-audio-player::-webkit-slider-runnable-track {
  background: rgba(255, 255, 255, 0.3);
}

/* 播放按钮内部三角形颜色 */
.custom-audio-player::-webkit-media-controls-play-button {
  color: #ffffff;
}

/* 时间显示样式 */
.custom-audio-player::-webkit-media-controls-time-remaining-display,
.custom-audio-player::-webkit-media-controls-current-time-display {
  color: #000;
  font-size: 12px;
  font-family: 'Monaco', monospace;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}
</style>
