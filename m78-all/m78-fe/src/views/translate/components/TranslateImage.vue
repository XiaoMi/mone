<!--
 * @Description: 
 * @Date: 2024-01-16 20:48:20
 * @LastEditTime: 2024-01-29 15:20:13
-->
<template>
  <div class="translate-image">
    <TranslateFile :category="props.category" @change="change" ref="fileRef">
      <div class="image-file">
        <img :src="fileSrc" />
      </div>
    </TranslateFile>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import TranslateFile from './TranslateFile.vue'
import { fetchImageStream } from '@/api/translate'
import CryptoJS from 'crypto-js'
import mitt from '@/utils/bus'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

const props = defineProps({
  category: {
    type: Object,
    required: true
  },
  categoryActive: {
    type: Number,
    required: true
  }
})
const fileSrc = ref('')
const fileRef = ref()
const iscurrent = ref(true)

watch(
  () => props.categoryActive,
  () => {
    iscurrent.value = false
    nextTick(() => {
      fileSrc.value = ''
      fileRef.value.closeFile()
    })
  },
  {
    immediate: true,
    deep: true
  }
)

const change = ({ file }) => {
  iscurrent.value = true
  mitt.emit('translateData', {
    isTranslateLoadingImg: t('translate.imgLoading')
  })
  fileSrc.value = URL.createObjectURL(file)
  const formData = new FormData()
  formData.append('imageFile', file)
  formData.append('operateType', 1) //1是提取文本
  const controller = new AbortController()
  const signal = controller.signal
  let text = ''
  fetchImageStream(formData, signal, ({ event }) => {
    if (iscurrent.value) {
      const xhr = event.target
      const { responseText } = xhr
      console.log('处理图片数据', responseText)
      const chunks = responseText.split('\n')
      const start = chunks.indexOf('event:start')
      if (start > -1) {
        chunks.splice(start, 2)
        mitt.emit('translateData', {
          isTranslateLoadingImg: t('translate.imgRenderLoading')
        })
      }
      const end = chunks.indexOf('event:end')
      if (end > -1) {
        chunks.splice(end, 2)
      }
      for (let chunk of chunks) {
        if (chunk.includes('data:')) {
          chunk = chunk.substring(5)
          if (chunk) {
            try {
              text += CryptoJS.enc.Base64.parse(chunk).toString(CryptoJS.enc.Utf8)
              mitt.emit('translateData', {
                origin: text,
                isTranslateLoadingImg: '',
                type: 'process'
              })
            } catch (error) {
              console.error(error)
            }
          }
        } else {
          continue
        }
      }
    } else {
      controller.abort()
    }
  })
    .catch(() => {
      if (iscurrent.value) {
        ElMessage.error(t('translate.imgError'))
      }
    })
    .finally(() => {
      if (iscurrent.value) {
        controller.abort()
        mitt.emit('translateData', {
          origin: text,
          isTranslateLoadingImg: ''
        })
        if (!text) {
          ElMessage.error(t('translate.imgNoData'))
        }
      }
    })
}
</script>

<style scoped lang="scss">
.image-file {
  flex: 1;
  overflow: auto;
  text-align: center;
  img {
    max-width: 100%;
    max-height: 100%;
  }
}
</style>
