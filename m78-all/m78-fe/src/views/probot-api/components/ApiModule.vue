<template>
  <div class="api-container">
    <div class="api-head pb20">
      <h1 class="pb10">{{ data.type }}应用API</h1>
    </div>
    <div class="pb20">
      <div class="module">
        <h2 class="pb10">Base UR</h2>
        <CodeModule
          :data="{
            title: 'code',
            main: data.baseurl
          }"
        ></CodeModule>
      </div>
      <div class="module">
        <h2 class="pb10">Authentication</h2>
        <p class="pb10">
          Service API 使用 API-Key 进行鉴权。 强烈建议开发者把 API-Key
          放在后端存储，而非分享或者放在客户端存储，以免 API-Key 泄露，导致财产损失。 所有 API
          请求都应在 Authorization HTTP Header 中包含您的 API-Key，如下所示：
        </p>
        <CodeModule
          :data="{
            title: 'code',
            main: ' Authorization: {API_KEY}'
          }"
        ></CodeModule>
      </div>
    </div>
    <div class="api-content">
      <div class="pb20">
        <div class="title">执行 {{ data.type }}</div>
      </div>
      <!-- Request -->
      <div class="pb20">
        <div class="pb20">
          <div class="fsize18 pb10">Request Body</div>
          <ParamModule :data="data.request.params"></ParamModule>
        </div>
        <CodeModule
          :data="{
            title: 'Request',
            methods: data.methods,
            main: data.request.code
          }"
        ></CodeModule>
      </div>
      <!-- response -->
      <div class="pb20">
        <div class="fsize18 pb10">Response</div>
        <ParamModule :data="data.response.params"></ParamModule>
      </div>
      <CodeModule
        :data="{
          title: 'Response',
          main: data.response.code
        }"
      ></CodeModule>
    </div>
  </div>
</template>

<script setup lang="ts">
import CodeModule from './CodeModule.vue'
import ParamModule from './ParamModule.vue'

const props = defineProps({
  data: {
    type: Object,
    default() {
      return {}
    }
  }
})
</script>

<style scoped lang="scss">
.api-container {
  font-size: 14px;
  line-height: 25px;
  color: #666;
  .api-head {
    h1 {
      font-size: 24px;
      line-height: 30px;
      color: #18181b;
    }
  }
}
.module {
  margin-bottom: 30px;
  h2 {
    font-size: 16px;
    line-height: 30px;
    color: #333;
  }
}
.api-content {
  border-top: 1px solid rgba($color: #000000, $alpha: 0.2);
  padding-top: 30px;
  .head {
    padding-bottom: 10px;
    .methods {
      display: inline-block;
      padding: 0px 4px;
      border-radius: 4px;
      border: 1px solid rgb(56, 189, 248);
      color: rgb(56, 189, 248);
      background-color: rgb(56, 189, 248, 0.3);
      font-size: 12px;
    }
    .url {
      padding-left: 10px;
    }
  }
  .title {
    font-size: 20px;
    line-height: 30px;
    color: #18181b;
  }
}
</style>
