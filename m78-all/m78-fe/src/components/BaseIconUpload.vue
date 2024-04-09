<template>
  <el-upload
    ref="uploadRef"
    :auto-upload="false"
    :show-file-list="false"
    :limit="1"
    :on-exceed="handleAvatarExceed"
    :on-change="handleAvatarChange"
    class="upload-container"
  >
    <div class="upload-icon">
      <el-icon><Upload /></el-icon>
    </div>
    <div class="upload-bg">
      <el-image
        v-if="base64.length > 3"
        style="width: 100%; height: 100%"
        :src="base64"
        fit="cover"
      />
      <BaseIcon v-else size="large" :index="props.index"></BaseIcon>
    </div>
  </el-upload>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, genFileId } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import type {
  UploadFile,
  UploadFiles,
  UploadInstance,
  UploadProps,
  UploadRawFile
} from 'element-plus'
import BaseIcon from './BaseIcon.vue'

const emits = defineEmits(['upload', 'error'])

const props = defineProps({
  index: {
    type: String,
    default: String(Math.floor(Math.random() * 10))
  },
  base64: {
    type: String,
    default: () => ''
  }
})

const uploadRef = ref<UploadInstance>()

const handleAvatarChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  console.log(uploadFile, uploadFiles)
  if (uploadFile.raw && beforeAvatarUpload(uploadFile.raw)) {
    // imageUrl.value = URL.createObjectURL(uploadFile.raw!)
    const reader = new FileReader()
    reader.readAsDataURL(uploadFile.raw)
    reader.onload = () => {
      // console.log(reader.result)
      emits('upload', reader.result)
    }
    reader.onerror = (e) => {
      console.error(e)
      emits('error', reader.result)
    }
  }
}

// 覆盖前一个文件
const handleAvatarExceed = (files: File[]) => {
  uploadRef.value!.clearFiles()
  const file = files[0] as UploadRawFile
  file.uid = genFileId()
  uploadRef.value!.handleStart(file)
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile: UploadRawFile) => {
  if (rawFile.type !== 'image/png') {
    ElMessage.error('Avatar picture must be png format!')
    return false
  } else if (rawFile.size > 2 * 1024 * 200) {
    ElMessage.error('Avatar picture size can not exceed 200KB!')
    return false
  }
  return true
}
</script>

<style scoped lang="scss">
.upload-container {
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  font-size: 20px;
  border-radius: 10px;
  position: relative;
  transition: all 0.3;
  .upload-icon {
    background-color: rgba(75, 74, 88, 0.4);
    width: 100%;
    height: 100%;
    position: absolute;
    border-radius: 10px;
    opacity: 0;
    z-index: 1;
    .oz-icon {
      font-size: 30px;
      color: #fff;
    }
  }
  .upload-bg {
    width: 100px;
    height: 100px;
    position: relative;
  }
  &:hover {
    .upload-icon {
      opacity: 1;
    }
  }
}
</style>
