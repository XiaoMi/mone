<template>
  <div class="translate-file">
    <div v-if="fileContent" class="file-container">
      <div class="btn-container">
        <el-button class="close-icon" @click="closeFile"
          ><el-icon><CloseBold /></el-icon
        ></el-button>
      </div>
      <slot></slot>
    </div>
    <el-upload
      ref="upload"
      class="drag-upload"
      action="/"
      :auto-upload="false"
      :with-credentials="true"
      :show-file-list="false"
      :limit="1"
      :drag="true"
      :accept="props.category.accept"
      :on-change="changeFile"
      v-else
    >
      <el-empty>
        <template #description>
          <p
            v-html="
              t('common.drag', { docType: props.category.value, btnTitle: t('common.upload') })
            "
          ></p>
          <div class="clipboard-btn" v-if="props.category.key === 3">
            <el-button type="primary" @click.stop="clipboard">
              <el-icon><DocumentCopy /> </el-icon>
              <span class="clipboard-text">{{ t('translate.clipboard') }}</span>
            </el-button>
          </div>
        </template>
      </el-empty>
    </el-upload>
  </div>
</template>

<script setup lang="ts">
import { ref, defineExpose } from 'vue'
import type { UploadInstance } from 'element-plus'
import { t } from '@/locales'
import { ElMessage } from 'element-plus'

const props = defineProps({
  category: {
    type: Object,
    required: true
  }
})
const emit = defineEmits(['change'])

const upload = ref<UploadInstance>()
const fileContent = ref('')

const changeFile = (file) => {
  fileContent.value = URL.createObjectURL(file.raw)
  emit('change', { file: file.raw })
}
const closeFile = () => {
  fileContent.value = ''
}
defineExpose({
  closeFile
})

const clipboard = async () => {
  const fileList: File[] = []
  const urlList: string[] = []
  let blob
  const clipboardItems = await navigator.clipboard.read()
  console.log('clipboardItems', clipboardItems)
  for (const clipboardItem of clipboardItems) {
    for (const type of clipboardItem.types) {
      // 筛选图片类型的文件
      if (type.indexOf('image') > -1) {
        blob = await clipboardItem.getType(type)
        // 将Blob转换成File
        const file = new File([blob], `image-${Date.now()}`, { type: type })
        fileList.push(file)
        // 将Blob转换成url，方便前端展示预览图
        fileContent.value = URL.createObjectURL(blob)
        console.log('fileContent.value', fileContent.value)
        urlList.push(file)
      }
    }
  }
  if (fileList.length) {
    emit('change', { file: blob })
  } else {
    ElMessage.error(t('translate.clipboardNoData'))
  }
}
</script>

<style lang="scss" scoped>
.translate-file {
  height: 240px;
  margin-bottom: 40px;
}
.file-container {
  height: 100%;
  border: 1px solid var(--oz-menu-border-color);
  padding: 6px 10px;
  display: flex;
  flex-direction: column;
  .btn-container {
    display: flex;
    justify-content: flex-end;
    padding-bottom: 10px;
  }
  .close-icon {
    border: none;
    border-radius: 50%;
    font-size: 16px;
  }
}
.clipboard-btn {
  padding-top: 10px;
  .clipboard-text {
    padding-left: 2px;
  }
}
</style>
<style lang="scss">
.translate-file .drag-upload {
  .oz-upload-dragger {
    padding: 0px !important;
  }
  .oz-empty {
    padding: 0px 0px 20px !important;
  }
}
</style>
