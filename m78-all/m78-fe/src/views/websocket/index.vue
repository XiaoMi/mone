<template>
  <div class="h-full p-[20px]">
    <div>
      <el-form inline>
        <el-form-item>
          <el-input v-model="text"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="sendMessage">发送</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>{{ res }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

const text = ref('')
const res = ref('')
let sock: WebSocket | null = null

onMounted(() => {
  if (window.location.origin.startsWith('http://')) {
    sock = new WebSocket(`ws://${window.location.host}/api/ai-plugin-new/ws/bot`)
  } else {
    sock = new WebSocket(`wss://${window.location.host}/api/ai-plugin-new/ws/bot`)
  }

  sock.onopen = function (event) {
    console.log('WebSocket is open now.', event)
  }

  sock.onmessage = function (event) {
    const message = event.data
    res.value = message
    console.log('Received message: ' + message)
  }

  sock.onclose = function (event) {
    console.log('WebSocket is closed now.', event)
    sock?.close()
  }

  sock.onerror = function (event) {
    console.error(event)
    sock = null
  }
})

function sendMessage() {
  sock?.send(text.value)
}

onBeforeUnmount(() => {
  sock?.close()
})
</script>
