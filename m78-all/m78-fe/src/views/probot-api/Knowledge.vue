<!--
 * @Description: 
 * @Date: 2024-08-14 17:01:56
 * @LastEditTime: 2024-08-22 17:56:16
-->
<template>
  <ApiModule :data="data"></ApiModule>
</template>

<script setup lang="ts">
import ApiModule from './components/ApiModule.vue'

const data = {
  type: 'Knowledge',
  baseurl:import.meta.env.VITE_APP_DOMAIN+'/open-apis/ai-plugin-new/searchKnowledge',
  methods: 'POST',
  url: '/searchKnowledge',
  request: {
    params: {
      userName: '用户名',
      knowledgeBaseId: '知识库id',
      queryText: '检索内容',
      limit: '1'
    },
    code: `curl --location '${import.meta.env.VITE_APP_DOMAIN}/open-apis/ai-plugin-new/searchKnowledge' 
--header 'Content-Type: application/json' 
--header 'Authorization: {API_KEY}' 
--data '{
    "userName": {userName},
    "knowledgeBaseId": {知识库id},
    "queryText": {input},
    "limit": 1
}'`
  },
  response: {
    params: {
      code: 0,
      message: 'ok',
      data: [
        {
          _KEYDESC: 'keyDec',
          tenantId: 777,
          knowledgeBaseId: 2453,
          docId: 203221,
          blockId: 17236039939300002,
          distance: 0.7551607315920075,
          vector: null,
          content:
            '自动驾驶领域：AI Agent在自动驾驶汽车中扮演着核心角色，它们能够处理来自车辆传感器的数据，做出驾驶决策，确保行车安全'
        }
      ],
      traceId: null,
      attachments: null
    },
    code: `{
    "code": 0,
    "message": "ok",
    "data": [
        {
            "tenantId": 777,
            "knowledgeBaseId": 2453,
            "docId": 203221,
            "blockId": 17236039939300002,
            "distance": 0.7551607315920075,
            "vector": null,
            "content": "自动驾驶领域：AI Agent在自动驾驶汽车中扮演着核心角色，它们能够处理来自车辆传感器的数据，做出驾驶决策，确保行车安全"
        }
    ],
    "traceId": null,
    "attachments": null
}`
  }
}
</script>
