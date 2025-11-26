<template>
  <div class="image-upload">
    <el-upload
      class="upload-container"
      ref="upload"
      :accept="fileTypes[props.type as keyof typeof fileTypes]"
      :limit="props.limit"
      :auto-upload="false"
      v-model:file-list="fileList"
      :on-preview="handlePreview"
      :on-change="beforeAvatarUpload"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
      :list-type="props.type === 'file' ? 'text' : 'picture'"
    >
      <div class="image-upload-icon">
        <el-icon size="18" color="#FFF"><DocumentAdd v-if="props.type === 'file'" /><UploadFilled v-else /></el-icon>
      </div>
    </el-upload>
  </div>
</template>
<script setup lang="ts">
import { ref, watch } from "vue";
import { ElMessage } from "element-plus";
import type { UploadUserFile, UploadProps, UploadInstance } from "element-plus";

const fileTypes = {
  image: '.png,.jpg,.jpeg,.gif,.webp',
  file: '.txt,.pdf,.docx,.xlsx,.pptx,.doc,.xls,.ppt,.js,.ts,.css,.java,.sh,.bash,.xml,.md'
}

type ModelValue = {
  mediaType: string,
  input: string,
  url: string
}

const props = defineProps<{
  limit: number,
  modelValue: ModelValue[],
  type: string
}>()

const emits = defineEmits(['update:modelValue']);

const fileList = ref<UploadUserFile[]>([])
const upload = ref<UploadInstance>()

watch(() => { return props.modelValue }, (val, preVal) => {
  if (val !== preVal && val.length == 0) {
    upload.value?.clearFiles()
    fileList.value = []
  }
})

const handleExceed = () => {

}

const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles)
  upload.value?.clearFiles()
  emits("update:modelValue", []);
}

const handlePreview: UploadProps['onPreview'] = (file) => {
  console.log(file)
}

const beforeAvatarUpload: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  if (uploadFile.raw) {
    const file = uploadFile.raw
    const reader = new FileReader()
    const { name, size } = file
    //   文件类型限制
    const fileTypeFlag = fileTypes[props.type as keyof typeof fileTypes].includes(
      name.substring(name.lastIndexOf('.')).toLowerCase()
    )
    if (!fileTypeFlag) {
      ElMessage.error(`文件类型只能是${fileTypes[props.type as keyof typeof fileTypes]}`)
      return
    }
    //   文件大小进行校验
    if (size > 1024 * 1024 * 100) {
      ElMessage.error('文件大小不能超过100Mb')
      return
    }
    reader?.readAsDataURL(file)
    reader.onload = function (event) {
      // 获取Base64编码的字符串
      const base64String = (event.target?.result ?? '') as string

      const arr = base64String?.split('base64,')
      const url = URL.createObjectURL(file)
      fileList.value = [
        {
          name: '',
          url
        }
      ]

      emits("update:modelValue", [{
        mediaType: file.type,
        input: arr[1],
        url,
        name: file.name
      }]);
    }
  }
}
</script>

<style lang="scss">
.image-upload {
  .upload-container {
    display: flex;
    justify-content: center;
    align-items: center;

    .el-upload-list {
      position: absolute;
      bottom: 56px;
      right: 8px;
    }
  }
}
</style>

<style lang="scss" scoped>
.image-upload {
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-upload-icon {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
