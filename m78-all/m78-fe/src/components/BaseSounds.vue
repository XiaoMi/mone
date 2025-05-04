<!--
 * @Description: 
 * @Date: 2024-03-14 20:46:07
 * @LastEditTime: 2024-07-17 11:28:46
-->
<template>
  <div>
    <el-tooltip :content="t('translate.read')" v-if="!isReading">
      <el-button
        :class="'sounds-icon ' + (props.size === 'small' ? 'small' : '')"
        @click.stop="startRead"
        :disabled="props.disabled"
        text
      >
        <i class="iconfont icon-yuyinlangdu"></i>
      </el-button>
    </el-tooltip>
    <el-tooltip :content="t('translate.stopRead')" v-else>
      <el-button
        :class="'sounds-icon ' + (props.size === 'small' ? 'small' : '')"
        @click.stop="stopRead"
        text
      >
        <i class="iconfont icon-tingzhi"></i>
      </el-button>
    </el-tooltip>
    <audio ref="resultAudio" :src="resultAudioSrc" autoplay />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { fetchTextToAudio } from '@/api/translate'
import { t } from '@/locales'
import { ElMessage } from 'element-plus'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  },
  content: {
    type: String,
    default: ''
  },
  language: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: ''
  }
})

const resultAudio = ref()
const resultAudioSrc = ref()
const isReading = ref(false)
const readFileData = ref<{
  [propName: string]: any
}>({})

// 朗读
const startRead = () => {
  if (props.content) {
    try {
      isReading.value = true
      const objKey = props.content + props.language
      const getReadFileData = localStorage.getItem('readFileData')
      const fileData = getReadFileData ? JSON.parse(getReadFileData) : {}
      const resultFile = fileData[objKey]
      let isExist = getReadFileData ? resultFile : false
      if (isExist) {
        resultAudioSrc.value = resultFile
        if (resultAudio.value) {
          ;(resultAudio.value as HTMLAudioElement).play()
        }
      } else {
        fetchTextToAudio({
          text: props.content,
          dialect: props.language
        })
          .then((res) => {
            resultAudioSrc.value = URL.createObjectURL(res)
            if (resultAudio.value) {
              // 做数据缓存
              readFileData[objKey] = resultAudioSrc.value
              localStorage.setItem('readFileData', JSON.stringify(readFileData))
              ;(resultAudio.value as HTMLAudioElement).play()
            }
          })
          .catch((error) => {
            console.log('error', error)
          })
      }
    } catch (error) {
      console.log('error', error)
    }
  } else {
    ElMessage.error(t('translate.readNoData'))
  }
}
const stopRead = () => {
  isReading.value = false
  if (resultAudio.value) {
    ;(resultAudio.value as HTMLAudioElement).pause()
  }
}

onMounted(() => {
  resultAudio.value?.addEventListener(
    'ended',
    function () {
      stopRead()
    },
    false
  )
})

defineExpose({
  stopRead
})
</script>

<style scoped lang="scss">
.sounds-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  .iconfont {
    font-size: 16px;
  }
  &.small {
    width: 29px;
    height: 29px;
  }
}
</style>
