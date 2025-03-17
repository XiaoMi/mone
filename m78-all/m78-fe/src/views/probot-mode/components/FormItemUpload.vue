<template>
  <FormItem :title="title" :prop="prop" :require="require">
    <template #topRight v-if="props.limit == 1 && props.type == 'image/*'">
      <el-button type="primary" plain @click="imageMode = !imageMode" class="mode-change" link>{{
        !imageMode ? '切换图片模式' : '切换地址模式'
      }}</el-button>
    </template>
    <div v-if="props.limit == 1" class="limit-container">
      <BaseIconUpload
        @upload="
          (base64) => {
            onUploadBotAvatar(base64)
          }
        "
        :base64="fileListValue[fileListValue.length - 1]?.url"
        :defaultEmpty="true"
        height="106px"
        :type="props.type"
        v-if="imageMode"
        @delImg="handleRemove"
      />
      <el-input v-model="inputUrl" placeholder="请输入图片地址" class="input-container" v-else />
    </div>
    <el-upload
      v-else
      ref="uploadRef"
      :auto-upload="false"
      :show-file-list="!!fileListValue.length"
      :limit="limit"
      drag
      multiple
      v-model:file-list="fileListValue"
      :on-remove="handleRemove"
      list-type="picture"
      class="form-item-upload-container"
      :on-exceed="handleAvatarExceed"
      :on-change="handleAvatarChange"
      :accept="props.type"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">拖拽或者点击上传</div>
      <template #tip>
        <div class="el-upload__tip">
          现在支持上传数量为{{ props.limit }}，支持上传{{ props.type }}格式，大小不超过{{
            props.size + props.sizeUnit
          }}
        </div>
      </template>
    </el-upload>
  </FormItem>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type {
  UploadFile,
  UploadFiles,
  UploadInstance,
  UploadProps,
  UploadRawFile
} from 'element-plus'
import FormItem from './FormItem.vue'
import { uploadLLMImg } from '@/api/workflow'
import { ElMessage } from 'element-plus'
import BaseIconUpload from '@/components/BaseIconUpload.vue'

const props = defineProps({
  title: {
    type: String,
    default: '上传图片'
  },
  modelValue: {
    type: Array,
    default: []
  },
  size: {
    type: Number,
    default: 1
  },
  sizeUnit: {
    type: String,
    default: 'M'
  },
  limit: {
    type: Number,
    default: 2
  },
  type: {
    type: String,
    default: 'image/*'
  },
  prop: {
    type: String,
    default: ''
  },
  require: {}
})
const emits = defineEmits(['update:modelValue', 'change'])

const fileListValue = computed({
  get() {
    return props.modelValue.map((item) => {
      return {
        name: '',
        url: item
      }
    })
  },
  set(v) {
    console.log('v', v)
  }
})
const inputUrl = computed({
  get() {
    return props.modelValue[props.modelValue.length - 1]
  },
  set(v) {
    emits('update:modelValue', [v])
  }
})
const uploadRef = ref<UploadInstance>()
const imageMode = ref(true)

// 文件列表移除文件时的钩子
const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  console.log('文件移除', uploadFile, uploadFiles)
  // emits('update:modelValue', fileListValue.value)
  emits('update:modelValue', [''])
}
const handleAvatarExceed = (files: File[]) => {
  console.log('files超出限制', files)
  ElMessage.error('数量超过限制，请先删除再进行删除!')
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile: UploadRawFile) => {
  if (
    !(
      rawFile.type === props.type ||
      (props.type.includes('*') && props.type.split('/')[0] == rawFile.type.split('/')[0])
    )
  ) {
    ElMessage.error('格式不正确，格式应该为' + props.type + '!')
    fileListValue.value.splice(0, fileListValue.value.length - 1)
    return false
  } else if (rawFile.size > 1024 * props.size * (props.sizeUnit === 'kb' ? 1 : 1024)) {
    ElMessage.error('大小不超过 ' + props.size + props.sizeUnit + '!')
    fileListValue.value.splice(0, fileListValue.value.length - 1)
    return false
  }
  return true
}
// 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用
const handleAvatarChange = (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
  if (uploadFile.raw && beforeAvatarUpload(uploadFile.raw)) {
    const reader = new FileReader()
    reader.readAsDataURL(uploadFile.raw)
    reader.onload = () => {
      onUploadBotAvatar(reader?.result)
    }
    reader.onerror = (e) => {
      console.error(e)
      // emits('error', reader.result)
    }
  }
}

const onUploadBotAvatar = async (base64: string) => {
  try {
    if (base64) {
      const response = await uploadLLMImg({
        base64: base64,
        isInner: false
      })
      if (response.code === 0) {
        if (props.limit === 1) {
          emits('update:modelValue', [response.data])
          emits('change', [response.data])
        } else {
          emits('update:modelValue', [
            ...fileListValue.value.map((item: any) => item.url),
            response.data
          ])
          emits('change', [...fileListValue.value.map((item: any) => item.url), response.data])
        }
      } else {
        ElMessage.error(response.message || '出错了')
      }
    }
  } catch (e) {
    ElMessage.error('出错了' + e)
  }
}
</script>

<style lang="scss">
.form-item-upload-container {
  width: 100%;
  .oz-upload-dragger {
    padding: 20px 0px;
  }
  .oz-upload-list.oz-upload-list--picture {
    display: flex;
    width: 100%;
    overflow: auto;
    margin-top: 0px;
    padding-top: 10px;
  }
  .oz-upload-list__item {
    margin-right: 4px;
    margin-top: 0px;
    width: auto;
    overflow: visible !important;
    &:last-child {
      margin-right: 0px;
    }
  }
  .oz-upload-list__item-status-label,
  .oz-upload-list__item-info {
    display: none !important;
  }
  .oz-icon.oz-icon--close {
    z-index: 999;
    background: #eee;
    border-radius: 50%;
    right: -5px;
    top: -5px;
  }
  .el-upload__tip {
    font-size: 12px;
    color: #666;
    line-height: 18px;
    padding: 4px 2px 0px;
  }
}
.mode-change {
  margin-bottom: 6px;
}
.limit-container,
.input-container {
  width: 100% !important;
}
</style>
