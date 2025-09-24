<template>
  <div class="sc-message--ops-item">
    <el-tooltip content="朗读" v-if="!isReading">
      <el-button
        :class="'sounds-icon ' + (props.size === 'small' ? 'small' : '')"
        @click.stop="startRead"
        :disabled="props.disabled"
        text
      >
        <font-awesome-icon :icon="['fas', 'volume-high']" />
      </el-button>
    </el-tooltip>
    <el-tooltip content="停止朗读" v-else>
      <el-button
        :class="'sounds-icon ' + (props.size === 'small' ? 'small' : '')"
        @click.stop="stopRead"
        text
      >
        <font-awesome-icon :icon="['fas', 'stop']" />
      </el-button>
    </el-tooltip>
    <audio ref="resultAudio" :src="resultAudioSrc" autoplay />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import util from '@/libs/util'

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
const startRead = async () => {
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
        await util.textToAudio(props.content)
      }
    } catch (error) {
      console.log('error', error)
    }
  } else {
    ElMessage.error('没有可朗读的内容')
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
  display: flex;
  align-items: center;
  justify-content: center;

  &.small {
    width: 29px;
    height: 29px;
  }
}

.sc-message--ops-item {
  padding: 5px 0;
  display: flex;
  align-items: center;
  font-size: 12px;
  cursor: pointer;
}
</style>
