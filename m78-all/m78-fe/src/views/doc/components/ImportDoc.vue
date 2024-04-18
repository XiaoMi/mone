<template>
  <el-upload
    ref="upload"
    class="upload-demo"
    :class="type == 'drag' ? 'drag-upload' : ''"
    action="/api/v1/upload"
    :before-upload="beforeUpload"
    :on-success="handleFile"
    :on-error="onError"
    :limit="1"
    :show-file-list="false"
    :auto-upload="true"
    :drag="type == 'drag'"
    accept=".csv,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
    :disabled="props.disabled"
  >
    <MyBtn
      bgColor="#e9b192"
      iconClass="icon-xinjianwendang"
      :text="t('excle.upload')"
      v-if="type == 'btn'"
      :disabled="props.disabled"
    />
    <el-empty v-if="type == 'drag'">
      <template #description>
        <p
          style="letter-spacing: 1px"
          v-html="t('common.drag', { docType: t('excle.docs'), btnTitle: t('excle.upload') })"
        ></p>
      </template>
    </el-empty>
  </el-upload>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage, type UploadProps } from 'element-plus'
import type { MessageParamsWithType, UploadInstance } from 'element-plus'
import MyBtn from '@/components/MyBtn.vue'
import { t } from '@/locales'

const showProgress = ref(false)
const percent = ref(0)
const emit = defineEmits(['uploadSuccess', 'beforeUpload', 'uploadFailed'])
const props = defineProps({
  type: {},
  disabled: {
    type: Boolean,
    default: false
  }
})
const upload = ref<UploadInstance>()

const handleFile = ({ code, message, data }) => {
  upload.value?.clearFiles()
  if (code != 0) {
    emit('uploadFailed')
    ElMessage.error(message)
    return
  }
  emit('uploadSuccess', data)
}

const beforeUpload: UploadProps['beforeUpload'] = () => {
  emit('beforeUpload')
  return true
}
const onError = (e) => {
  upload.value?.clearFiles()
  ElMessage.error(e.message || '出错了！')
  emit('uploadFailed')
}
</script>

<style lang="scss" scoped>
.import-btn {
  width: 100%;
  margin-bottom: 10px;
}
.left-wrap {
  background: #e7e7e7;
}
.upload-demo {
  width: 100%;
  :deep(.oz-upload) {
    width: 100%;
    .btn {
      width: 100%;
    }
  }
}
.my-header {
  display: flex;
  align-items: center;
}

.title-text {
  margin-left: 3px;
  font-size: 17px;
}
.icon-baiwangyun-EXCELwenjian {
  font-size: 16px;
  color: #409eff;
}
.file-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.file-name {
  margin-left: 4px;
}
.percent-box {
  display: flex;
  align-items: center;
  justify-content: center;
}
.percent-text {
  margin-right: 3px;
}

.drag-upload {
  height: 100%;
  :deep(.oz-upload-dragger),
  :deep(.oz-upload) {
    height: 100%;
  }
  :deep(.oz-upload-dragger) {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
