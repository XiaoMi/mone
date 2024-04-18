<!--
 * @Description: 
 * @Date: 2024-01-16 20:48:20
 * @LastEditTime: 2024-01-29 15:28:57
-->
<template>
  <div class="translate-doc">
    <TranslateFile :category="props.category" @change="change" ref="fileRef">
      <div class="doc-file" v-if="fileData.type">
        <div><i :class="['iconfont', fileTypeIcon[fileData.type]]" /></div>
        <p>{{ fileData.name }}</p>
      </div>
      <div v-else class="doc-file">{{ t('translate.docLoading') }}</div>
    </TranslateFile>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import TranslateFile from './TranslateFile.vue'
import { fetchDocStream } from '@/api/translate'
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
const fileTypeIcon = ref({
  pdf: 'icon-baiwangyun-PDFwenjian',
  doc: 'icon-baiwangyun-wendang',
  txt: 'icon-baiwangyun-wenbenwendang'
})
const fileRef = ref()
const fileData = ref({})
const iscurrent = ref(false) //双重判断
const controller = new AbortController()
const signal = controller.signal

watch(
  () => props.categoryActive,
  () => {
    nextTick(() => {
      iscurrent.value = false
      fileData.value = {}
      fileRef.value.closeFile()
    })
  },
  {
    immediate: true,
    deep: true
  }
)
onUnmounted(() => {
  controller?.abort()
})

const change = ({ file }) => {
  iscurrent.value = true
  mitt.emit('translateData', {
    isTranslateLoadingDoc: t('translate.docLoading')
  })
  const formData = new FormData()
  formData.append('file', file)
  let str = ''
  console.log('file', file)
  fetchDocStream(formData, signal, ({ event }) => {
    console.log('event', event)
    if (iscurrent.value) {
      const xhr = event.target
      const { responseText } = xhr
      console.log('处理文档数据', responseText)
      try {
        const res = JSON.parse(responseText)
        str = res.data.content
        fileData.value = res.data
      } catch (error) {}
    }
  })
    .catch(() => {
      if (iscurrent.value) {
        ElMessage.error(t('translate.docError'))
      }
    })
    .finally(() => {
      if (iscurrent.value) {
        mitt.emit('translateData', {
          origin: str,
          isTranslateLoadingDoc: ''
        })
      }
      controller?.abort()
    })
}
</script>

<style scoped lang="scss">
.doc-file {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  flex: 1;
  .iconfont {
    font-size: 120px;
  }
  p {
    padding-top: 20px;
    font-size: 14px;
    flex: 1;
  }
}
</style>
