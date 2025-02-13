<!--
 * @Description: 
 * @Date: 2024-08-14 17:01:56
 * @LastEditTime: 2024-08-22 17:55:30
-->
<template>
  <ApiModule :data="data"></ApiModule>
</template>

<script setup lang="ts">
import ApiModule from './components/ApiModule.vue'

const data = {
  type: 'Bot',
  baseurl: import.meta.env.VITE_APP_DOMAIN+'/open-apis/ai-plugin-new/feature/router/probot/query ',
  methods: 'POST',
  url: '/feature/router/probot/query',
  request: {
    params: {
      userName: 'userName',
      botId: ' botId ',
      input: '输入的内容 '
    },
    code: `curl --request POST \
  --url ${import.meta.env.VITE_APP_DOMAIN}/open-apis/ai-plugin-new/feature/router/probot/query 
  --header 'Accept: application/json, text/plain, */*' 
  --header 'Accept-Language: zh-CN,zh;q=0.9,en;q=0.8' 
  --header 'Cache-Control: no-cache' 
  --header 'Connection: keep-alive' 
  --header 'Content-Type: application/json' 
  --header 'Authorization: {API_KEY}' 
  --data '{
    "userName": {userName},
    "botId": {botId},
    "input": {输入的内容}
}'`
  },
  response: {
    params: {
      code: 0,
      message: 'ok',
      data: {
        _KEYDESC: 'keyDec',
        result: {
          _KEYDESC: 'keyDec',
          type: 'llm',
          content: '您好，我是AI chat，有什么可以帮助您的吗？'
        }
      },
      traceId: null,
      attachments: null
    },
    code: `{
    "code": 0,
    "message": "ok",
    "data": "{\"result\":{\"type\":\"llm\",\"content\":\"您好，我是AI chat，有什么可以帮助您的吗？\"}}",
    "traceId": null,
    "attachments": null
}`
  }
}
</script>

<style scoped lang="scss"></style>
