<!--
 * @Description: 
 * @Date: 2024-08-14 17:01:56
 * @LastEditTime: 2024-10-15 21:36:59
-->
<template>
  <ApiModule :data="data"></ApiModule>
</template>

<script setup lang="ts">
import ApiModule from './components/ApiModule.vue'

const data = {
  type: 'Workflow',
  baseurl: import.meta.env.VITE_APP_DOMAIN+'/open-apis/ai-plugin-new/feature/router/flow/querySync',
  methods: 'POST',
  url: '/feature/router/flow/querySync',
  request: {
    params: {
      flowId: '工作流id',
      input: {
        _KEYDESC: 'keyDec',
        location: '开始节点入参，json格式'
      },
      operateCmd: 'testFlow',
      userName: '用户名'
    },
    code: `curl --location '${import.meta.env.VITE_APP_DOMAIN}/open-apis/ai-plugin-new/feature/router/flow/querySync'
--header 'Content-Type: application/json' 
--header 'Authorization: {API_KEY}' 
--data '{
    "flowId": "flowId",
    "input": {
        "开始节点入参，json格式"
    },
    "operateCmd": "testFlow",
    "userName": "{userName}"
}'`
  },
  response: {
    params: {
      code: 0,
      message: 'ok',
      data: {
        _KEYDESC: 'keyDec',
        flowRecordId: '39552'
      },
      traceId: null,
      attachments: null
    },
    code: `{
    "code": 0,
    "message": "ok",
    "data": "{\"flowRecordId\":\"39552\"}",
    "traceId": null,
    "attachments": null
}`
  }
}
</script>
