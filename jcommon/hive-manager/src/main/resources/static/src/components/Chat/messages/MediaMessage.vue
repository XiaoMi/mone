<template>
  <div class="sc-message--text">
    <audio
      v-if="message.type === 'audio'"
      :src="src"
      controls
      autoplay
    ></audio>
    <div
      v-else-if="message.type === 'image'"
    >
      <el-image
        style="height: 80px;"
        fit="scale-down"
        :src="src"
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
