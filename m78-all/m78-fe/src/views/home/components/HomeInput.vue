<template>
  <div class="home-input">
    <el-input
      class="input-item input-element"
      v-model="input"
      placeholder=""
      @keyup.enter="handleEnter"
    ></el-input>
    <div class="recorder">
      <Recorder style="cursor: pointer" @submit="handleSound"></Recorder>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useChatStore } from '@/stores/chat'
import Recorder from '@/components/common/recorder/index.vue'
import { fetchAudioToText } from '@/api/translate'
import { t } from '@/locales'

const chatStore = useChatStore()
const { updateHomeInput } = chatStore
const router = useRouter()

const input = ref<string>('')

function handleEnter(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    event.preventDefault()
    updateHomeInput(input.value)
    router.push({
      name: 'chat'
    })
  }
}

async function handleSound(url: string, base64: string, blob: Blob) {
  if (blob.size == 0) {
    ElMessage.error(t('homeInput.soundFail'))
    return
  }
  const data = new FormData()
  // data.append('audioFile', blob)
  data.append('audioFile', blob, 'recording.wav')
  data.append('fromLanguage', '简体中文')
  data.append('toLanguage', 'en')
  const res = (await fetchAudioToText(data)) as unknown as {
    code: number
    message: string
    data: {
      fromText: string
      toText: string
    }
  }
  console.log(res)
  if (res.code !== 0 || res.data == null) {
    ElMessage.error(res.message)
  } else {
    updateHomeInput(res.data.fromText)
    router.push({
      name: 'chat'
    })
  }
}
</script>

<style lang="scss" scoped>
.home-input {
  position: relative;
}

.recorder {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 40px;

  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
