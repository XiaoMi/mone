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
import SockJS from 'sockjs-client'

const text = ref('')
const res = ref('')
let sock: SockJS | null = null

onMounted(() => {
  sock = new SockJS(
    `${window.location.origin}${import.meta.env.VITE_GLOB_API_NEW_URL}ws/sockjs/bot/execute`
  )

  sock.onopen = function () {
    console.log('SockJS is open now.')
  }

  sock.onmessage = function (event) {
    const message = event.data
    res.value = message
    console.log('Received message: ' + message)
  }

  sock.onclose = function () {
    console.log('SockJS is closed now.')
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
