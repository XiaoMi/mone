<!--
 * @Description:
 * @Date: 2024-03-06 14:29:51
 * @LastEditTime: 2024-08-13 15:01:00
    
-->
<template>
  <el-upload
    ref="uploadRef"
    :auto-upload="false"
    :show-file-list="false"
    :limit="1"
    :on-exceed="handleAvatarExceed"
    :on-change="handleAvatarChange"
    :class="
      'upload-container ' +
      (defaultEmpty ? ' empty-container' : '' + (props.disabled ? ' disabled' : ''))
    "
    :drag="defaultEmpty"
    :accept="props.type"
    :disabled="props.disabled"
  >
    <div v-if="defaultEmpty" class="upload-empty-img" :style="{ height: height }">
      <el-image
        v-if="base64.length > 3"
        style="width: 100%; height: 100%"
        :src="base64"
        fit="contain"
        title="拖拽或者点击上传"
        class="upload-img"
      />
      <div v-else class="empty-tips">
        <p>
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        </p>
        <div class="el-upload__text">拖拽或者点击上传</div>
      </div>
      <el-button
        circle
        v-if="base64.length > 3"
        class="del-img"
        size="small"
        @click.stop="emits('delImg')"
      >
        <el-icon><Close /></el-icon>
      </el-button>
    </div>
    <template v-else>
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
    </template>
  </el-upload>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, genFileId } from 'element-plus'
import { Upload, Close } from '@element-plus/icons-vue'
import type {
  UploadFile,
  UploadFiles,
  UploadInstance,
  UploadProps,
  UploadRawFile
} from 'element-plus'
import BaseIcon from './BaseIcon.vue'

const emits = defineEmits(['upload', 'error', 'delImg'])

const props = defineProps({
  index: {
    type: String,
    default: String(Math.floor(Math.random() * 10))
  },
  base64: {
    type: String,
    default: () => ''
  },
  defaultEmpty: {
    type: Boolean,
    default: false
  },
  height: {
    type: String,
    default: '300px'
  },
  type: {
    type: String,
    default: 'image/*'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const uploadRef = ref<UploadInstance>()

// const imageUrl = ref('')
// const handleAvatarSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
//   imageUrl.value = URL.createObjectURL(uploadFile.raw!)
// }

const handleAvatarChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
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
  if (
    !(
      rawFile.type === props.type ||
      (props.type.includes('*') && props.type.split('/')[0] == rawFile.type.split('/')[0])
    )
  ) {
    ElMessage.error('格式不正确，格式应该为' + props.type + '!')
    return false
  } else if (!props.defaultEmpty && rawFile.size > 2 * 1024 * 200) {
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
  border-radius: 10px;
  position: relative;
  transition: all 0.3;
  border: 1px solid #f1f1f1;
  &.disabled {
    :deep(.oz-upload) {
      cursor: no-drop !important;
    }
  }
  .upload-icon {
    background-color: rgba(75, 74, 88, 0.4);
    width: 100%;
    height: 100%;
    position: absolute;
    border-radius: 10px;
    opacity: 0;
    z-index: 1;
    font-size: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
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
.empty-container {
  width: 100%;
  height: 100%;
  border: none;
  :deep(.oz-upload) {
    width: 100%;
  }
  :deep(.oz-upload-dragger) {
    padding: 0px;
  }
}
.upload-empty-img {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  .del-img {
    position: absolute;
    right: 5px;
    top: 5px;
    padding: 3px;
    width: 20px;
    height: 20px;
    visibility: hidden;
  }
  &:hover {
    .del-img {
      visibility: visible;
    }
  }
}

.empty-tips {
  text-align: center;
}
</style>
