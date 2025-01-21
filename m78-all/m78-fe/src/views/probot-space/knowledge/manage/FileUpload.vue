<!--
 * @Description: 
 * @Date: 2024-04-19 11:12:31
 * @LastEditTime: 2024-09-11 19:23:21
-->
<template>
  <div class="flex">
    <el-upload
      ref="upload"
      class="upload-file"
      :action="'/api/ai-plugin-new/v1/knowledge/uploadKnowledgeFileV2'"
      :show-file-list="false"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      :with-credentials="true"
      :before-upload="beforeAvatarUpload"
    >
      <el-button type="primary">文件上传</el-button>
    </el-upload>
    <el-button type="primary" plain style="margin-left: 10px" @click="letterClick"
      >飞书上传</el-button
    >
    <el-alert
      title=".pdf、.txt、.miapi、.athena、.md 支持选择的文件类型，文件大小限制40MB"
      type="warning"
      style="margin-left: 10px; padding: 6px 10px"
    />
    <LetterUpload v-model="letterUploadVisible" @letterSubmit="letterSubmit"></LetterUpload>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'
import LetterUpload from './LetterUpload.vue'

const letterUploadVisible = ref(false)

const emit = defineEmits('update', 'letterSubmit')

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const typeArr = rawFile.name.split('.')
  const typeByName = typeArr[typeArr.length - 1]
  if (
    rawFile.type === 'application/pdf' ||
    rawFile.type === 'text/plain' ||
    typeByName === 'md' ||
    typeByName === 'miapi' ||
    typeByName === 'athena'
  ) {
    if (rawFile.size / 1024 / 1024 > 40) {
      ElMessage.error('文件大小限制40MB!')
      return false
    } else {
      return true
    }
  } else {
    ElMessage.error('上传文件格式不正确!')
    return false
  }
}

const uploadError = (error: Error, uploadFile: any, uploadFiles: any) => {
  console.log(222, error, uploadFile, uploadFiles)
}
const uploadSuccess = (response: any, uploadFile: any, uploadFiles: any) => {
  if (response.code === 0) {
    ElMessage({
      message: response.message,
      type: 'success',
      duration: 3000
    })
    emit('update', uploadFile, response.data)
  } else {
    ElMessage({
      message: response.message,
      type: 'error',
      duration: 3000
    })
  }
}

const letterClick = () => {
  letterUploadVisible.value = true
}
const letterSubmit = (val) => {
  letterUploadVisible.value = false
  emit('letterSubmit', val)
}
</script>

<style scoped></style>
